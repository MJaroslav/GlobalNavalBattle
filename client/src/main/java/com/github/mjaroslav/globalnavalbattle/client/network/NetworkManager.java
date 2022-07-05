package com.github.mjaroslav.globalnavalbattle.client.network;

import com.github.mjaroslav.globalnavalbattle.client.ClientServer;
import com.github.mjaroslav.globalnavalbattle.client.GlobalNavalBattle;
import com.github.mjaroslav.globalnavalbattle.client.logic.ClientPlayer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.ReferenceCountUtil;
import com.github.mjaroslav.globalnavalbattle.client.render.gui.screen.ScreenError;
import com.github.mjaroslav.globalnavalbattle.client.render.gui.screen.ScreenWaitingRoom;
import com.github.mjaroslav.globalnavalbattle.common.logic.Player;
import com.github.mjaroslav.globalnavalbattle.common.network.LeaveReason;
import com.github.mjaroslav.globalnavalbattle.common.network.LoginStatus;
import com.github.mjaroslav.globalnavalbattle.common.network.Network;
import com.github.mjaroslav.globalnavalbattle.common.network.PacketManager;
import com.github.mjaroslav.globalnavalbattle.common.utils.JsonUtils;
import com.github.mjaroslav.globalnavalbattle.common.utils.Logger;
import com.github.mjaroslav.globalnavalbattle.common.utils.Userdata;
import com.github.mjaroslav.globalnavalbattle.common.utils.UserdataBuilder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.mjaroslav.globalnavalbattle.common.Packets.*;

public class NetworkManager implements Network {
    private final Map<String, ClientPlayer> players = new HashMap<>();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Channel channel;
    private final PacketManager packetManager = new PacketManager();
    private boolean ready;
    private boolean closed;

    public boolean isReady() {
        return ready;
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    public Player getPlayer(String username) {
        return players.get(username);
    }

    @Override
    public List<Player> getPlayers() {
        return new ArrayList<>(players.values());
    }

    @Override
    public void sendPacket(Object packet, String... players) {
        String data;
        if (packet instanceof String)
            data = (String) packet;
        else
            data = JsonUtils.toString(packet, false);
        Logger.debug(String.format("%s -> %s %s", channel.localAddress(), channel.remoteAddress(), data));
        if (channel.isOpen())
            channel.writeAndFlush(data);
    }

    @Override
    public void receivePacket(Object packet, Object... attachments) {
        JsonObject data = (JsonObject) JsonUtils.toJsonElement((String) packet);
        ChannelHandlerContext ctx = (ChannelHandlerContext) attachments[0];
        Logger.debug(String.format("%s <- %s %s", ctx.channel().localAddress(), ctx.channel().remoteAddress(), data));
        packetManager.receive(data, ctx, this);
    }

    private final ChannelOutboundHandlerAdapter JSON_OUT_HANDLER = new ChannelOutboundHandlerAdapter() {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
            String data;
            if (msg instanceof String)
                data = (String) msg;
            else if (msg instanceof JsonElement)
                data = JsonUtils.toString((JsonElement) msg, false);
            else
                data = JsonUtils.toString(msg, false);
            ctx.write(data, promise);
        }
    };

    private final ChannelInboundHandlerAdapter JSON_IN_HANDLER = new ChannelInboundHandlerAdapter() {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            try {
                receivePacket(msg, ctx);
            } catch (Exception e) {
                e.printStackTrace();
                ctx.fireExceptionCaught(e);
            } finally {
                ReferenceCountUtil.release(msg);
            }
        }
    };

    private final ChannelDuplexHandler EXCEPTION_HANDLER = new ChannelDuplexHandler() {
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            GlobalNavalBattle.getGlobalNavalBattle().setScreen(new ScreenError("Потеряно соединение с сервером", cause));
            ClientServer.closeServer();
        }
    };

    @Override
    public void openConnection(String host, int port) throws Exception {
        Logger.info("Logging...");
        Bootstrap bootstrap = new Bootstrap().group(workerGroup).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        GlobalNavalBattle.getGlobalNavalBattle().setScreen(new ScreenError("Невозможно соединиться с сервером", cause));
                        ClientServer.closeServer();
                    }

                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new StringDecoder(StandardCharsets.UTF_8),
                                new StringEncoder(StandardCharsets.UTF_8), JSON_IN_HANDLER, JSON_OUT_HANDLER, EXCEPTION_HANDLER);
                        ch.closeFuture().addListener(future -> {
                            closed = true;
                            ready = false;
                        });
                    }
                }).option(ChannelOption.SO_KEEPALIVE, true).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15000);
        ChannelFuture future = bootstrap.connect(host, port).sync();
        channel = future.channel();
        ready = true;
        channel.closeFuture().sync();
    }

    @Override
    public void closeConnection() {
        ready = false;
        closed = true;
        Logger.info("Disconnecting...");
        workerGroup.shutdownGracefully();
        Logger.info("Disconnected!");
    }

    @Override
    public void init() {
        packetManager.addListener(TYPE_STATUS, (packet, ctx, network) -> {
            LoginStatus status = LoginStatus.getById(packet.getAsInt());
            if (status == LoginStatus.OK) {
                Logger.info("Logged!");
                GlobalNavalBattle.getGlobalNavalBattle().setScreen(new ScreenWaitingRoom());
            } else {
                Logger.error("Login error:" + status.toString());
                GlobalNavalBattle.getGlobalNavalBattle().setScreen(new ScreenError("Отказано в соединении: " + status.toString()));
                ClientServer.closeServer();
            }
        });
        packetManager.addListener(TYPE_LOGIN, (packet, ctx, network) -> sendPacket(PacketFactory.login(
                new UserdataBuilder(GlobalNavalBattle.getGlobalNavalBattle().getOptions().getUsername()).build())));
        packetManager.addListener(TYPE_PLAYERS, (packet, args, net) -> {
            Userdata[] data = JsonUtils.fromJson(packet, Userdata[].class);
            for (Userdata user : data) {
                ClientPlayer player = new ClientPlayer(user.getUsername());
                players.put(player.username(), player);
            }
        });
        packetManager.addListener(TYPE_LEAVE, (packet, args, net) -> {
            LeaveReason reason = LeaveReason.getByID(packet.getAsJsonObject().get(PACKET_REASON).getAsInt());
            String username = packet.getAsJsonObject().get(PACKET_USERNAME).getAsString();
            Logger.info(username + " disconnected with reason " + reason.name());
            players.remove(username);
        });
        packetManager.addListener(TYPE_JOIN, (packet, args, net) -> {
            Userdata data = JsonUtils.fromJson(packet, Userdata.class);
            ClientPlayer player = new ClientPlayer(data.getUsername());
            Logger.info(player.username() + " joined");
            players.put(player.username(), player);
        });
    }
}
