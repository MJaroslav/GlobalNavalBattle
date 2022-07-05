package com.github.mjaroslav.globalnavalbattle.client.resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResourcePath {
    public static final String ONLY_JAR_MARKER = "@";
    public static final String INTERNAL_RESOURCES = "resources";
    public static final String EXTERNAL_RESOURCES = "externalResources";

    private String name;
    private boolean useCustomResources;
    private ResourceType type;

    public ResourcePath(String name) {
        this(ResourceType.OTHER, name, !name.startsWith(ONLY_JAR_MARKER));
    }

    public ResourcePath(String name, boolean useCustomResources) {
        this(ResourceType.OTHER, name, useCustomResources);
    }

    public ResourcePath(ResourceType type, String name) {
        this(type, name, !name.startsWith(ONLY_JAR_MARKER));
    }

    public ResourcePath(ResourceType type, String name, boolean useCustomResources) {
        this.type = type;
        this.name = name.replace(ONLY_JAR_MARKER, "");
        this.useCustomResources = useCustomResources;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void useCustomResources(boolean useCustomResources) {
        this.useCustomResources = useCustomResources;
    }

    public ResourceType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean canLoadFromExternalFolder() {
        return useCustomResources;
    }

    public Path getExternalPath() {
        Path path = Paths.get(EXTERNAL_RESOURCES);
        if (getType() != ResourceType.OTHER)
            path = path.resolve(getType().PATH);
        return path.resolve(getName());
    }

    public Path getInternalPath() {
        Path path = Paths.get(INTERNAL_RESOURCES);
        if (getType() != ResourceType.OTHER)
            path = path.resolve(getType().PATH);
        return path.resolve(getName());
    }

    public boolean hasExternal() {
        return Files.exists(getExternalPath());
    }

    public Path getPath() {
        return canLoadFromExternalFolder() && hasExternal() ? getExternalPath() : getInternalPath();
    }

    public InputStream getExternalInputStream() throws IOException {
        return Files.newInputStream(getExternalPath());
    }

    public InputStream getInternalInputStream() {
        return ResourcePath.class.getResourceAsStream("/" + getInternalPath().toString());
    }

    public InputStream getInputStream() throws IOException {
        return canLoadFromExternalFolder() && hasExternal() ? getExternalInputStream() : getInternalInputStream();
    }

    public Path getExternalPath(String subname) {
        return getExternalPath().resolve(subname);
    }

    public Path getInternalPath(String subname) {
        return getInternalPath().resolve(subname);
    }

    public boolean hasExternal(String subname) {
        return Files.exists(getExternalPath(subname));
    }

    public Path getPath(String subname) {
        return canLoadFromExternalFolder() && hasExternal(subname) ? getExternalPath(subname) : getInternalPath(subname);
    }

    public InputStream getExternalInputStream(String subname) throws IOException {
        return Files.newInputStream(getExternalPath(subname));
    }

    public InputStream getInternalInputStream(String subname) {
        return ResourcePath.class.getResourceAsStream("/" + getInternalPath(subname).toString());
    }

    public InputStream getInputStream(String subname) throws IOException {
        return canLoadFromExternalFolder() && hasExternal(subname) ?
                getExternalInputStream(subname) : getInternalInputStream(subname);
    }

    public ResourcePath copy() {
        return new ResourcePath(type, name, useCustomResources);
    }

    public ResourcePath copy(String subname) {
        return new ResourcePath(type, name + "/" + subname, useCustomResources);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ResourcePath) {
            ResourcePath path = (ResourcePath) obj;
            return name.equals(path.name) && type == path.type && useCustomResources == path.useCustomResources;
        } else return super.equals(obj);
    }

    @Override
    public String toString() {
        return String.format("%s[%s, %s]", super.toString(), getName(), getType());
    }
}
