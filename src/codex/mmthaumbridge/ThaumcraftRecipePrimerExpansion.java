package codex.mmthaumbridge;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import hellfirepvp.modularmachinery.common.integration.crafttweaker.RecipePrimer;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenExpansion("mods.modularmachinery.RecipePrimer")
public class ThaumcraftRecipePrimerExpansion {
    @ZenMethod("addItemAspectOutputs")
    public static RecipePrimer addItemAspectOutputs(RecipePrimer primer, IItemStack stack) {
        return addItemAspectOutputs(primer, stack, 1.0D);
    }

    @ZenMethod("addItemAspectOutputs")
    public static RecipePrimer addItemAspectOutputs(RecipePrimer primer, IItemStack stack, double multiplier) {
        for (AspectEntry entry : ThaumcraftAspectBridge.getAspects(stack)) {
            addAspectOutput(primer, entry.getTag(), scale(entry.getAmount(), multiplier));
        }
        return primer;
    }

    @ZenMethod("addItemAspectInputs")
    public static RecipePrimer addItemAspectInputs(RecipePrimer primer, IItemStack stack) {
        return addItemAspectInputs(primer, stack, 1.0D);
    }

    @ZenMethod("addItemAspectInputs")
    public static RecipePrimer addItemAspectInputs(RecipePrimer primer, IItemStack stack, double multiplier) {
        for (AspectEntry entry : ThaumcraftAspectBridge.getAspects(stack)) {
            addAspectInput(primer, entry.getTag(), scale(entry.getAmount(), multiplier));
        }
        return primer;
    }

    @ZenMethod("addAnyItemAspectOutput")
    public static RecipePrimer addAnyItemAspectOutput(RecipePrimer primer) {
        return addAnyItemAspectOutput(primer, 1, 1.0D);
    }

    @ZenMethod("addAnyItemAspectOutput")
    public static RecipePrimer addAnyItemAspectOutput(RecipePrimer primer, int amount) {
        return addAnyItemAspectOutput(primer, amount, 1.0D);
    }

    @ZenMethod("addAnyItemAspectOutput")
    public static RecipePrimer addAnyItemAspectOutput(RecipePrimer primer, int amount, double multiplier) {
        return primer.addRecipeTooltip("Codex MM Recipe Bridge: dynamic any-item aspect output from the old bridge is not used by recipe import adapters.");
    }

    @ZenMethod("addAnyItemIdentityOutput")
    public static RecipePrimer addAnyItemIdentityOutput(RecipePrimer primer) {
        return addAnyItemIdentityOutput(primer, 1, 1.0D);
    }

    @ZenMethod("addAnyItemIdentityOutput")
    public static RecipePrimer addAnyItemIdentityOutput(RecipePrimer primer, int amount) {
        return addAnyItemIdentityOutput(primer, amount, 1.0D);
    }

    @ZenMethod("addAnyItemIdentityOutput")
    public static RecipePrimer addAnyItemIdentityOutput(RecipePrimer primer, int amount, double multiplier) {
        return primer.addRecipeTooltip("Codex MM Recipe Bridge: dynamic any-item identity output from the old bridge is not used by recipe import adapters.");
    }

    private static void addAspectInput(RecipePrimer primer, String tag, int amount) {
        com.warmthdawn.mod.gugu_utils.crafttweaker.modularmachenary.RecipePrimerExt.addThaumcraftAspcetInput(primer, amount, tag);
    }

    private static void addAspectOutput(RecipePrimer primer, String tag, int amount) {
        com.warmthdawn.mod.gugu_utils.crafttweaker.modularmachenary.RecipePrimerExt.addThaumcraftAspcetOutput(primer, amount, tag);
    }

    private static int scale(int amount, double multiplier) {
        return Math.max(1, (int) Math.round(amount * multiplier));
    }
}
