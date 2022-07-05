package com.github.mjaroslav.globalnavalbattle.server.network;

import com.github.mjaroslav.globalnavalbattle.server.logic.ServerPlayer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.ReferenceCountUtil;
import com.github.mjaroslav.globalnavalbattle.common.logic.Player;
import com.github.mjaroslav.globalnavalbattle.common.network.LeaveReason;
import com.github.mjaroslav.globalnavalbattle.common.network.LoginStatus;
import com.github.mjaroslav.globalnavalbattle.common.network.Network;
import com.github.mjaroslav.globalnavalbattle.common.network.PacketManager;
import com.github.mjaroslav.globalnavalbattle.common.utils.JsonUtils;
import com.github.mjaroslav.globalnavalbattle.common.utils.Logger;
import com.github.mjaroslav.globalnavalbattle.common.utils.Userdata;
import com.github.mjaroslav.globalnavalbattle.common.utils.UserdataBuilder;
import com.github.mjaroslav.globalnavalbattle.server.Server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.github.mjaroslav.globalnavalbattle.common.Packets.*;

public class NetworkManager implements Network {
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workGroup = new NioEventLoopGroup();
    private final Set<Channel> awaitingChannels = new HashSet<>();
    private final Map<String, ServerPlayer> players = new HashMap<>();
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
    public ServerPlayer getPlayer(String username) {
        return players.get(username);
    }

    @Override
    public List<Player> getPlayers() {
        return new ArrayList<>(players.values());
    }

    private ServerPlayer getPlayerByConnection(Channel channel) {
        for (ServerPlayer player : players.values())
            if (player.connection().remoteAddress().equals(channel.remoteAddress()))
                return player;
        return null;
    }

    @Override
    public void sendPacket(Object packet, String... players) {
        if (players == null || players.length == 0) {
            this.players.values().forEach(e -> sendPacket(packet, e.connection()));
        } else {
            for (String username : players)
                if (this.players.containsKey(username))
                    sendPacket(packet, getPlayer(username).connection());
        }
    }

    private ChannelFuture sendPacket(Object packet, Channel connection) {
        String data;
        if (packet instanceof String)
            data = (String) packet;
        else
            data = JsonUtils.toString(packet, false);
        Logger.debug(String.format("%s -> %s %s", connection.localAddress(), connection.remoteAddress(), data));
        if (connection.isOpen())
            return connection.writeAndFlush(data);
        return null;
    }

    @Override
    public void receivePacket(Object packet, Object... attachments) {
        JsonObject data = JsonUtils.toJsonElement((String) packet).getAsJsonObject();
        ChannelHandlerContext ctx = (ChannelHandlerContext) attachments[0];
        Logger.debug(String.format("%s <- %s %s", ctx.channel().localAddress(), ctx.channel().remoteAddress(), packet));
        packetManager.receive(data, ctx, this);
        if (!data.has(MAIN_TYPE)) {
            Logger.error("Empty packet received!");
            return;
        }
        String type = data.get(MAIN_TYPE).getAsString();
        ServerPlayer player = getPlayerByConnection(ctx.channel());
        Server.getServer().onPacketFromPlayer(player, this, type, data.has(MAIN_PACKET) ? data.get(MAIN_PACKET) : null);
    }

    private final JsonOutHandler JSON_OUT_HANDLER = new JsonOutHandler();

    private final JsonInHandler JSON_IN_HANDLER = new JsonInHandler();

    private final ExceptionHandler EXCEPTION_HANDLER =new ExceptionHandler();

    @Override
    public void openConnection(String host, int port) throws Exception {
        Logger.info("Starting the server...");
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        awaitingChannels.remove(ctx.channel());
                        super.exceptionCaught(ctx, cause);
                    }

                    @Override
                    protected void initChannel(SocketChannel ch) {
                        Logger.info("New connection with " + ch.remoteAddress() + "! Waiting login response...");
                        awaitingChannels.add(ch);
                        ch.pipeline().addLast(new StringDecoder(StandardCharsets.UTF_8),
                                new StringEncoder(StandardCharsets.UTF_8), JSON_IN_HANDLER, JSON_OUT_HANDLER, EXCEPTION_HANDLER);
                        /* Fire when connection lost or closed */
                        ch.writeAndFlush(PacketFactory.login());
                        ch.closeFuture().addListener(future -> {
                            Logger.info(ch.remoteAddress() + " connection closed or lost.");
                            if (awaitingChannels.contains(ch))
                                awaitingChannels.remove(ch);
                            else {
                                ServerPlayer disconnectedPlayer = null;
                                for (ServerPlayer player : players.values())
                                    if (player.connection().equals(ch)) {
                                        disconnectedPlayer = player;
                                        break;
                                    }
                                if (disconnectedPlayer != null) {
                                    sendPacket(PacketFactory.leave(disconnectedPlayer.username(), LeaveReason.CONNECTION_LOST));
                                    players.remove(disconnectedPlayer.username());
                                }
                            }
                        });
                    }
                }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
        Channel channel = bootstrap.bind(port).sync().channel();
        Logger.info("Server ready!");
        ready = true;
        channel.closeFuture().sync();
    }

    @Override
    public void closeConnection() throws Exception {
        ready = false;
        closed = true;
        Logger.info("Stopping the server...");
        workGroup.shutdownGracefully().await();
        bossGroup.shutdownGracefully().await();
        Logger.info("Server stopped!");
    }

    @Override
    public void init() {
        packetManager.addListener(TYPE_LOGIN, (packet, ctx, net) -> {
            Userdata user = JsonUtils.fromJson(packet, Userdata.class);
            if (awaitingChannels.contains(ctx.channel())) {
                if (players.containsKey(user.getUsername())) {
                    ChannelFuture f = sendPacket(PacketFactory.status(LoginStatus.USERNAME_USED), ctx.channel());
                    if (f != null) f.addListener(ChannelFutureListener.CLOSE);
                    return;
                }
                if (!UserdataBuilder.usernameAllowed(user.getUsername())) {
                    ChannelFuture f = sendPacket(PacketFactory.status(LoginStatus.BAD_USERNAME), ctx.channel());
                    if (f != null) f.addListener(ChannelFutureListener.CLOSE);
                    return;
                }
                ServerPlayer newPlayer = new ServerPlayer(user.getUsername(), ctx.channel());
                Logger.info(newPlayer.username() + " join from " + ctx.channel().remoteAddress());
                sendPacket(PacketFactory.join(newPlayer));
                players.put(newPlayer.username(), newPlayer);
                awaitingChannels.remove(ctx.channel());
                sendPacket(PacketFactory.players(getPlayers().toArray(new Player[]{})), newPlayer.username());
                sendPacket(PacketFactory.status(LoginStatus.OK), newPlayer.username());
                Server.getServer().onPlayerJoined(newPlayer);
            } else {
                ChannelFuture f = sendPacket(PacketFactory.status(LoginStatus.NOT_LOGGED), ctx.channel());
                if (f != null) f.addListener(ChannelFutureListener.CLOSE);
            }
        });
        packetManager.addListener(TYPE_LEAVE, (packet, args, net) -> {
            LeaveReason reason = LeaveReason.getByID(packet.getAsJsonObject().get(PACKET_REASON).getAsInt());
            String username = packet.getAsJsonObject().get(PACKET_USERNAME).getAsString();
            Logger.info(username + " disconnected with reason " + reason.name());
            ServerPlayer player = players.get(username);
            awaitingChannels.add(player.connection());
            player.connection().close();
            players.remove(username);
            sendPacket(PacketFactory.leave(username, reason));
            Server.getServer().onPlayerLeaved(username);
        });
    }

    @ChannelHandler.Sharable
    private final class JsonOutHandler extends ChannelOutboundHandlerAdapter {
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
    }

    @ChannelHandler.Sharable
    private final class JsonInHandler extends ChannelInboundHandlerAdapter {
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
    }

    @ChannelHandler.Sharable
    private final class ExceptionHandler extends ChannelDuplexHandler {
            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                if (cause instanceof IOException) {
                    ServerPlayer player = getPlayerByConnection(ctx.channel());
                    if (player != null)
                        Logger.error("Connection with " + player.username() + " lost!");
                }
            }
    }
}
