package codex.mmthaumbridge;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

@ZenRegister
@ZenClass("mods.codexmmthaumbridge.ThaumcraftAspectBridge")
public class ThaumcraftAspectBridge {
    @ZenMethod("getAspects")
    public static AspectEntry[] getAspects(IItemStack stack) {
        AspectList aspects = AspectRegistrationService.resolveAspects(toMcStack(stack));
        Aspect[] aspectArray = aspects.getAspectsSortedByName();
        AspectEntry[] entries = new AspectEntry[aspectArray.length];
        for (int i = 0; i < aspectArray.length; i++) {
            Aspect aspect = aspectArray[i];
            entries[i] = new AspectEntry(aspect.getTag(), aspect.getName(), aspects.getAmount(aspect));
        }
        return entries;
    }

    @ZenMethod("getAspectTags")
    public static String[] getAspectTags(IItemStack stack) {
        AspectEntry[] entries = getAspects(stack);
        String[] tags = new String[entries.length];
        for (int i = 0; i < entries.length; i++) {
            tags[i] = entries[i].getTag();
        }
        return tags;
    }

    @ZenMethod("getAspectAmount")
    public static int getAspectAmount(IItemStack stack, String tag) {
        Aspect aspect = Aspect.getAspect(tag);
        return aspect == null ? 0 : AspectRegistrationService.resolveAspects(toMcStack(stack)).getAmount(aspect);
    }

    @ZenMethod("hasAspect")
    public static boolean hasAspect(IItemStack stack, String tag) {
        return getAspectAmount(stack, tag) > 0;
    }

    @ZenMethod("ensureAspects")
    public static boolean ensureAspects(IItemStack stack) {
        return AspectRegistrationService.ensureAspects(toMcStack(stack));
    }

    @ZenMethod("registerMissingAspectsNow")
    public static int registerMissingAspectsNow() {
        return AspectRegistrationService.registerMissingAspects("zenscript");
    }

    @ZenMethod("getAspectSummary")
    public static String getAspectSummary(IItemStack stack) {
        AspectEntry[] entries = getAspects(stack);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < entries.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(entries[i].getTag()).append(':').append(entries[i].getAmount());
        }
        return builder.toString();
    }

    private static ItemStack toMcStack(IItemStack stack) {
        if (stack == null) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack = CraftTweakerMC.getItemStack(stack);
        return itemStack == null ? ItemStack.EMPTY : itemStack;
    }
}
