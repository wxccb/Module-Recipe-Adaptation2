package codex.mmthaumbridge;

import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;

final class AspectRegistrationService {
    private AspectRegistrationService() {
    }

    static int registerMissingAspects(String source) {
        return 0;
    }

    static boolean ensureAspects(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        AspectList aspects = resolveAspects(stack);
        if (aspects == null || aspects.size() <= 0) {
            return false;
        }
        if (!ThaumcraftApi.exists(stack)) {
            ThaumcraftApi.registerObjectTag(stack, aspects);
        }
        return true;
    }

    static AspectList resolveAspects(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return new AspectList();
        }
        AspectList aspects = AspectHelper.getObjectAspects(stack);
        if (aspects == null || aspects.size() <= 0) {
            aspects = AspectHelper.generateTags(stack);
        }
        return aspects == null ? new AspectList() : aspects;
    }
}
