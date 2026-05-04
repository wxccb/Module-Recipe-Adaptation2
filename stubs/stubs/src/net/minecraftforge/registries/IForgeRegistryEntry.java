package net.minecraftforge.registries;

import net.minecraft.util.ResourceLocation;

public interface IForgeRegistryEntry<V> {
    V setRegistryName(ResourceLocation name);
    ResourceLocation getRegistryName();
    Class<V> getRegistryType();

    class Impl<V> implements IForgeRegistryEntry<V> {
        private ResourceLocation registryName;

        @SuppressWarnings("unchecked")
        public V setRegistryName(ResourceLocation name) {
            this.registryName = name;
            return (V) this;
        }

        @SuppressWarnings("unchecked")
        public V setRegistryName(String name) {
            return setRegistryName(new ResourceLocation(name));
        }

        @SuppressWarnings("unchecked")
        public V setRegistryName(String namespace, String path) {
            return setRegistryName(new ResourceLocation(namespace, path));
        }

        public ResourceLocation getRegistryName() {
            return registryName;
        }

        @SuppressWarnings("unchecked")
        public Class<V> getRegistryType() {
            return (Class<V>) getClass();
        }
    }
}