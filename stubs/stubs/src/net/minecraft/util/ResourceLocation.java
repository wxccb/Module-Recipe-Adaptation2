package net.minecraft.util;

public class ResourceLocation {
    private final String namespace;
    private final String path;

    public ResourceLocation(String location) {
        int separator = location.indexOf(':');
        if (separator >= 0) {
            this.namespace = location.substring(0, separator);
            this.path = location.substring(separator + 1);
        } else {
            this.namespace = "minecraft";
            this.path = location;
        }
    }

    public ResourceLocation(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
    }

    public String func_110624_b() {
        return namespace;
    }

    public String func_110623_a() {
        return path;
    }

    @Override
    public String toString() {
        return namespace + ":" + path;
    }
}