package net.minecraftforge.registries;

import net.minecraft.util.ResourceLocation;

public class ForgeRegistry<V extends IForgeRegistryEntry<V>> {
    public V getValue(ResourceLocation name) {
        return null;
    }
}