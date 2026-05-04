package codex.mmbotaniabridge;

import github.kasuminova.mmce.common.itemtype.ChancedIngredientStack;
import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementEnergy;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementFluid;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementIngredientArray;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementItem;
import hellfirepvp.modularmachinery.common.crafting.requirement.type.RequirementType;
import hellfirepvp.modularmachinery.common.lib.RegistriesMM;
import hellfirepvp.modularmachinery.common.lib.RequirementTypesMM;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import kport.modularmagic.common.crafting.requirement.RequirementMana;
import kport.modularmagic.common.crafting.requirement.types.ModularMagicRequirements;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class BotaniaAdapterHelper {
    private BotaniaAdapterHelper() {
    }

    static int modifyDuration(Collection<RecipeModifier> modifiers, int duration) {
        return Math.max(1, Math.round(RecipeModifier.applyModifiers(
                modifiers, RequirementTypesMM.REQUIREMENT_DURATION, null, (float) duration, false)));
    }

    static boolean addItemInput(MachineRecipe machineRecipe, Collection<RecipeModifier> modifiers, Object input, int amount) {
        int modifiedAmount = Math.round(RecipeModifier.applyModifiers(
                modifiers, RequirementTypesMM.REQUIREMENT_INGREDIENT_ARRAY, IOType.INPUT, (float) amount, false));
        if (modifiedAmount <= 0) {
            return true;
        }
        if (input instanceof String) {
            machineRecipe.addRequirement(new RequirementItem(IOType.INPUT, (String) input, modifiedAmount));
            return true;
        }
        if (input instanceof ItemStack) {
            ItemStack stack = copyStack((ItemStack) input);
            if (isStackEmpty(stack)) {
                return false;
            }
            if (getStackMetadata(stack) == 32767) {
                setStackDamage(stack, 32767);
            }
            setStackCount(stack, Math.max(1, modifiedAmount));
            machineRecipe.addRequirement(new RequirementItem(IOType.INPUT, stack));
            return true;
        }
        return false;
    }

    static boolean addItemOutput(MachineRecipe machineRecipe, Collection<RecipeModifier> modifiers, ItemStack output) {
        if (output == null || isStackEmpty(output)) {
            return false;
        }
        ItemStack stack = copyStack(output);
        int amount = getStackCount(stack);
        int modifiedAmount = Math.round(RecipeModifier.applyModifiers(
                modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.OUTPUT, (float) amount, false));
        if (modifiedAmount <= 0) {
            return true;
        }
        setStackCount(stack, modifiedAmount);
        machineRecipe.addRequirement(new RequirementItem(IOType.OUTPUT, stack));
        return true;
    }

    static boolean addIngredientChoicesInput(MachineRecipe machineRecipe, Collection<RecipeModifier> modifiers, List<ItemStack> inputs, int amount) {
        int modifiedAmount = Math.round(RecipeModifier.applyModifiers(
                modifiers, RequirementTypesMM.REQUIREMENT_INGREDIENT_ARRAY, IOType.INPUT, (float) amount, false));
        if (modifiedAmount <= 0) {
            return true;
        }
        List<ChancedIngredientStack> choices = new ArrayList<>();
        for (ItemStack input : inputs) {
            if (input == null || isStackEmpty(input)) {
                continue;
            }
            ItemStack stack = copyStack(input);
            setStackCount(stack, Math.max(1, modifiedAmount));
            choices.add(new ChancedIngredientStack(stack));
        }
        if (choices.isEmpty()) {
            return false;
        }
        if (choices.size() == 1) {
            machineRecipe.addRequirement(new RequirementItem(IOType.INPUT, choices.get(0).itemStack));
        } else {
            machineRecipe.addRequirement(new RequirementIngredientArray(choices, IOType.INPUT));
        }
        return true;
    }

    static void addChancedItemOutput(MachineRecipe machineRecipe, Collection<RecipeModifier> modifiers, ItemStack output, float chance) {
        if (output == null || isStackEmpty(output)) {
            return;
        }
        ItemStack stack = copyStack(output);
        int modifiedAmount = Math.round(RecipeModifier.applyModifiers(
                modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.OUTPUT, (float) getStackCount(stack), false));
        float modifiedChance = (float) RecipeModifier.applyModifiers(
                modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.OUTPUT, chance, true);
        if (modifiedAmount <= 0 || modifiedChance <= 0f) {
            return;
        }
        setStackCount(stack, modifiedAmount);
        RequirementItem requirementItem = new RequirementItem(IOType.OUTPUT, stack);
        requirementItem.setChance(modifiedChance);
        machineRecipe.addRequirement(requirementItem);
    }

    static boolean addItemInputs(MachineRecipe machineRecipe, Collection<RecipeModifier> modifiers, Collection<ItemStack> inputs, int amount) {
        boolean addedAny = false;
        for (ItemStack input : inputs) {
            if (input == null || isStackEmpty(input)) {
                continue;
            }
            if (!addItemInput(machineRecipe, modifiers, input, amount)) {
                return false;
            }
            addedAny = true;
        }
        return addedAny;
    }

    static boolean addFluid(MachineRecipe machineRecipe, Collection<RecipeModifier> modifiers, IOType ioType, FluidStack fluidStack) {
        if (fluidStack == null || fluidStack.getFluid() == null || fluidStack.amount <= 0) {
            return false;
        }
        FluidStack stack = fluidStack.copy();
        int modifiedAmount = Math.round(RecipeModifier.applyModifiers(
                modifiers, RequirementTypesMM.REQUIREMENT_FLUID, ioType, (float) stack.amount, false));
        if (modifiedAmount <= 0) {
            return true;
        }
        stack.amount = modifiedAmount;
        machineRecipe.addRequirement(new RequirementFluid(ioType, stack));
        return true;
    }

    static void addChancedFluidOutput(MachineRecipe machineRecipe, Collection<RecipeModifier> modifiers, FluidStack fluidStack, float chance) {
        if (fluidStack == null || fluidStack.getFluid() == null || fluidStack.amount <= 0) {
            return;
        }
        FluidStack stack = fluidStack.copy();
        int modifiedAmount = Math.round(RecipeModifier.applyModifiers(
                modifiers, RequirementTypesMM.REQUIREMENT_FLUID, IOType.OUTPUT, (float) stack.amount, false));
        float modifiedChance = (float) RecipeModifier.applyModifiers(
                modifiers, RequirementTypesMM.REQUIREMENT_FLUID, IOType.OUTPUT, chance, true);
        if (modifiedAmount <= 0 || modifiedChance <= 0f) {
            return;
        }
        stack.amount = modifiedAmount;
        RequirementFluid requirementFluid = new RequirementFluid(IOType.OUTPUT, stack);
        requirementFluid.setChance(modifiedChance);
        machineRecipe.addRequirement(requirementFluid);
    }

    static boolean addItemOutputIfPresent(MachineRecipe machineRecipe, Collection<RecipeModifier> modifiers, ItemStack output) {
        return output == null || isStackEmpty(output) || addItemOutput(machineRecipe, modifiers, output);
    }

    static boolean addItemInputIfPresent(MachineRecipe machineRecipe, Collection<RecipeModifier> modifiers, ItemStack input) {
        return input == null || isStackEmpty(input) || addItemInput(machineRecipe, modifiers, input, getStackCount(input));
    }

    static boolean addFluidIfPresent(MachineRecipe machineRecipe, Collection<RecipeModifier> modifiers, IOType ioType, FluidStack fluidStack) {
        return fluidStack == null || fluidStack.getFluid() == null || fluidStack.amount <= 0 || addFluid(machineRecipe, modifiers, ioType, fluidStack);
    }

    static ItemStack copyStack(ItemStack stack) {
        try {
            return (ItemStack) stack.getClass().getMethod("copy").invoke(stack);
        } catch (Exception ignored) {
            try {
                return (ItemStack) stack.getClass().getMethod("func_77946_l").invoke(stack);
            } catch (Exception exception) {
                throw new RuntimeException("Unable to copy ItemStack", exception);
            }
        }
    }

    static boolean isStackEmpty(ItemStack stack) {
        try {
            return (Boolean) stack.getClass().getMethod("isEmpty").invoke(stack);
        } catch (Exception ignored) {
            try {
                return (Boolean) stack.getClass().getMethod("func_190926_b").invoke(stack);
            } catch (Exception exception) {
                throw new RuntimeException("Unable to check ItemStack emptiness", exception);
            }
        }
    }

    static int getStackMetadata(ItemStack stack) {
        try {
            return (Integer) stack.getClass().getMethod("getMetadata").invoke(stack);
        } catch (Exception ignored) {
            try {
                return (Integer) stack.getClass().getMethod("func_77952_i").invoke(stack);
            } catch (Exception exception) {
                throw new RuntimeException("Unable to get ItemStack metadata", exception);
            }
        }
    }

    static int getStackCount(ItemStack stack) {
        try {
            return (Integer) stack.getClass().getMethod("getCount").invoke(stack);
        } catch (Exception ignored) {
            try {
                return (Integer) stack.getClass().getMethod("func_190916_E").invoke(stack);
            } catch (Exception exception) {
                throw new RuntimeException("Unable to get ItemStack count", exception);
            }
        }
    }

    static void setStackDamage(ItemStack stack, int damage) {
        try {
            stack.getClass().getMethod("setItemDamage", Integer.TYPE).invoke(stack, damage);
        } catch (Exception ignored) {
            try {
                stack.getClass().getMethod("func_77964_b", Integer.TYPE).invoke(stack, damage);
            } catch (Exception exception) {
                throw new RuntimeException("Unable to set ItemStack damage", exception);
            }
        }
    }

    static void setStackCount(ItemStack stack, int amount) {
        try {
            stack.getClass().getMethod("setCount", Integer.TYPE).invoke(stack, amount);
        } catch (Exception ignored) {
            try {
                stack.getClass().getMethod("func_190920_e", Integer.TYPE).invoke(stack, amount);
            } catch (Exception exception) {
                throw new RuntimeException("Unable to set ItemStack count", exception);
            }
        }
    }

    static void addMana(MachineRecipe machineRecipe, Collection<RecipeModifier> modifiers, int mana, boolean perTick) {
        RequirementType<?, ?> requirementMana = RegistriesMM.REQUIREMENT_TYPE_REGISTRY.getValue(ModularMagicRequirements.KEY_REQUIREMENT_MANA);
        int modifiedMana = Math.round(RecipeModifier.applyModifiers(
                modifiers, requirementMana, IOType.INPUT, (float) mana, false));
        if (modifiedMana > 0) {
            machineRecipe.addRequirement(new RequirementMana(IOType.INPUT, modifiedMana, perTick));
        }
    }

    static void addEnergy(MachineRecipe machineRecipe, Collection<RecipeModifier> modifiers, long energy) {
        int modifiedEnergy = Math.round(RecipeModifier.applyModifiers(
                modifiers, RequirementTypesMM.REQUIREMENT_ENERGY, IOType.INPUT, (float) energy, false));
        if (modifiedEnergy > 0) {
            machineRecipe.addRequirement(new RequirementEnergy(IOType.INPUT, modifiedEnergy));
        }
    }

    static void addTooltip(MachineRecipe machineRecipe, List<String> tooltipList, String tooltip) {
        machineRecipe.addTooltip(tooltip);
        if (tooltipList != null && !tooltipList.contains(tooltip)) {
            tooltipList.add(tooltip);
        }
    }
}
