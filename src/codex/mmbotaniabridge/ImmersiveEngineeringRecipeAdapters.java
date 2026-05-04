package codex.mmbotaniabridge;

import blusunrize.immersiveengineering.api.ComparableItemStack;
import blusunrize.immersiveengineering.api.crafting.AlloyRecipe;
import blusunrize.immersiveengineering.api.crafting.ArcFurnaceRecipe;
import blusunrize.immersiveengineering.api.crafting.BlastFurnaceRecipe;
import blusunrize.immersiveengineering.api.crafting.BottlingMachineRecipe;
import blusunrize.immersiveengineering.api.crafting.CokeOvenRecipe;
import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import blusunrize.immersiveengineering.api.crafting.FermenterRecipe;
import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;
import blusunrize.immersiveengineering.api.crafting.MixerRecipe;
import blusunrize.immersiveengineering.api.crafting.RefineryRecipe;
import blusunrize.immersiveengineering.api.crafting.SqueezerRecipe;
import crafttweaker.util.IEventHandler;
import github.kasuminova.mmce.common.event.recipe.RecipeEvent;
import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.adapter.RecipeAdapter;
import hellfirepvp.modularmachinery.common.crafting.helper.ComponentRequirement;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

final class ImmersiveEngineeringRecipeAdapters {
    private ImmersiveEngineeringRecipeAdapters() {
    }

    static Collection<RecipeAdapter> createAdapters() {
        List<RecipeAdapter> adapters = new ArrayList<>();
        adapters.add(new CrusherAdapter());
        adapters.add(new ArcFurnaceAdapter());
        adapters.add(new AlloyKilnAdapter());
        adapters.add(new BottlingMachineAdapter());
        adapters.add(new SqueezerAdapter());
        adapters.add(new FermenterAdapter());
        adapters.add(new MetalPressAdapter());
        adapters.add(new MixerAdapter());
        adapters.add(new RefineryAdapter());
        adapters.add(new BlastFurnaceAdapter());
        adapters.add(new CokeOvenAdapter());
        return adapters;
    }

    private abstract static class BaseAdapter extends RecipeAdapter {
        private final String name;

        BaseAdapter(String name) {
            super(new ResourceLocation("immersiveengineering", name));
            this.name = name;
        }

        MachineRecipe shell(ResourceLocation owningMachine, List<RecipeModifier> modifiers, int duration) {
            return createRecipeShell(owningMachine,
                    new ResourceLocation("modularmachinery", "ie_" + name + "_" + incId++),
                    BotaniaAdapterHelper.modifyDuration(modifiers, Math.max(1, duration)), 0, false);
        }

        void finish(MachineRecipe machineRecipe,
                    List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                    Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                    List<String> tooltipList) {
            BotaniaAdapterHelper.addTooltip(machineRecipe, tooltipList, "Imported Immersive Engineering " + name + " recipe");
            RecipeAdapter.addAdditionalRequirements(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
        }
    }

    static final class CrusherAdapter extends BaseAdapter {
        CrusherAdapter() {
            super("crusher");
        }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            for (CrusherRecipe recipe : CrusherRecipe.recipeList) {
                MachineRecipe machineRecipe = shell(owningMachine, modifiers, recipe.getTotalProcessTime());
                if (!addIngredient(machineRecipe, modifiers, recipe.input)) {
                    continue;
                }
                if (!BotaniaAdapterHelper.addItemOutput(machineRecipe, modifiers, recipe.output)) {
                    continue;
                }
                if (recipe.secondaryOutput != null && recipe.secondaryChance != null) {
                    for (int i = 0; i < recipe.secondaryOutput.length && i < recipe.secondaryChance.length; i++) {
                        BotaniaAdapterHelper.addChancedItemOutput(machineRecipe, modifiers, recipe.secondaryOutput[i], recipe.secondaryChance[i]);
                    }
                }
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, recipe.getTotalProcessEnergy());
                finish(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    static final class ArcFurnaceAdapter extends BaseAdapter {
        ArcFurnaceAdapter() {
            super("arc_furnace");
        }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            for (ArcFurnaceRecipe recipe : ArcFurnaceRecipe.recipeList) {
                MachineRecipe machineRecipe = shell(owningMachine, modifiers, recipe.getTotalProcessTime());
                if (!addIngredient(machineRecipe, modifiers, recipe.input)) {
                    continue;
                }
                boolean valid = true;
                if (recipe.additives != null) {
                    for (IngredientStack additive : recipe.additives) {
                        if (!addIngredient(machineRecipe, modifiers, additive)) {
                            valid = false;
                            break;
                        }
                    }
                }
                if (!valid || !BotaniaAdapterHelper.addItemOutput(machineRecipe, modifiers, recipe.output)) {
                    continue;
                }
                BotaniaAdapterHelper.addItemOutputIfPresent(machineRecipe, modifiers, recipe.slag);
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, recipe.getTotalProcessEnergy());
                finish(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    static final class AlloyKilnAdapter extends BaseAdapter {
        AlloyKilnAdapter() {
            super("alloy_kiln");
        }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            for (AlloyRecipe recipe : AlloyRecipe.recipeList) {
                MachineRecipe machineRecipe = shell(owningMachine, modifiers, recipe.time);
                if (!addIngredient(machineRecipe, modifiers, recipe.input0) || !addIngredient(machineRecipe, modifiers, recipe.input1)) {
                    continue;
                }
                if (!BotaniaAdapterHelper.addItemOutput(machineRecipe, modifiers, recipe.output)) {
                    continue;
                }
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, Math.max(40, recipe.time * 4L));
                finish(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    static final class BottlingMachineAdapter extends BaseAdapter {
        BottlingMachineAdapter() {
            super("bottling_machine");
        }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            for (BottlingMachineRecipe recipe : BottlingMachineRecipe.recipeList) {
                MachineRecipe machineRecipe = shell(owningMachine, modifiers, recipe.getTotalProcessTime());
                if (!addIngredient(machineRecipe, modifiers, recipe.input)
                        || !BotaniaAdapterHelper.addFluid(machineRecipe, modifiers, IOType.INPUT, recipe.fluidInput)
                        || !BotaniaAdapterHelper.addItemOutput(machineRecipe, modifiers, recipe.output)) {
                    continue;
                }
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, recipe.getTotalProcessEnergy());
                finish(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    static final class SqueezerAdapter extends BaseAdapter {
        SqueezerAdapter() {
            super("squeezer");
        }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            for (SqueezerRecipe recipe : SqueezerRecipe.recipeList) {
                MachineRecipe machineRecipe = shell(owningMachine, modifiers, recipe.getTotalProcessTime());
                if (!addIngredient(machineRecipe, modifiers, recipe.input)) {
                    continue;
                }
                BotaniaAdapterHelper.addFluidIfPresent(machineRecipe, modifiers, IOType.OUTPUT, recipe.fluidOutput);
                BotaniaAdapterHelper.addItemOutputIfPresent(machineRecipe, modifiers, recipe.itemOutput);
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, recipe.getTotalProcessEnergy());
                finish(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    static final class FermenterAdapter extends BaseAdapter {
        FermenterAdapter() {
            super("fermenter");
        }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            for (FermenterRecipe recipe : FermenterRecipe.recipeList) {
                MachineRecipe machineRecipe = shell(owningMachine, modifiers, recipe.getTotalProcessTime());
                if (!addIngredient(machineRecipe, modifiers, recipe.input)) {
                    continue;
                }
                BotaniaAdapterHelper.addFluidIfPresent(machineRecipe, modifiers, IOType.OUTPUT, recipe.fluidOutput);
                BotaniaAdapterHelper.addItemOutputIfPresent(machineRecipe, modifiers, recipe.itemOutput);
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, recipe.getTotalProcessEnergy());
                finish(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    static final class MetalPressAdapter extends BaseAdapter {
        MetalPressAdapter() {
            super("metal_press");
        }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            for (Object value : MetalPressRecipe.recipeList.values()) {
                if (!(value instanceof MetalPressRecipe)) {
                    continue;
                }
                MetalPressRecipe recipe = (MetalPressRecipe) value;
                MachineRecipe machineRecipe = shell(owningMachine, modifiers, recipe.getTotalProcessTime());
                if (!addIngredient(machineRecipe, modifiers, recipe.input) || !addComparableItem(machineRecipe, modifiers, recipe.mold)) {
                    continue;
                }
                if (!BotaniaAdapterHelper.addItemOutput(machineRecipe, modifiers, recipe.output)) {
                    continue;
                }
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, recipe.getTotalProcessEnergy());
                finish(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    static final class MixerAdapter extends BaseAdapter {
        MixerAdapter() {
            super("mixer");
        }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            for (MixerRecipe recipe : MixerRecipe.recipeList) {
                MachineRecipe machineRecipe = shell(owningMachine, modifiers, recipe.getTotalProcessTime());
                if (!BotaniaAdapterHelper.addFluid(machineRecipe, modifiers, IOType.INPUT, recipe.fluidInput)) {
                    continue;
                }
                boolean valid = true;
                if (recipe.itemInputs != null) {
                    for (IngredientStack input : recipe.itemInputs) {
                        if (!addIngredient(machineRecipe, modifiers, input)) {
                            valid = false;
                            break;
                        }
                    }
                }
                if (!valid || !BotaniaAdapterHelper.addFluid(machineRecipe, modifiers, IOType.OUTPUT, recipe.fluidOutput)) {
                    continue;
                }
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, recipe.getTotalProcessEnergy());
                finish(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    static final class RefineryAdapter extends BaseAdapter {
        RefineryAdapter() {
            super("refinery");
        }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            for (RefineryRecipe recipe : RefineryRecipe.recipeList) {
                MachineRecipe machineRecipe = shell(owningMachine, modifiers, recipe.getTotalProcessTime());
                if (!BotaniaAdapterHelper.addFluid(machineRecipe, modifiers, IOType.INPUT, recipe.input0)
                        || !BotaniaAdapterHelper.addFluidIfPresent(machineRecipe, modifiers, IOType.INPUT, recipe.input1)
                        || !BotaniaAdapterHelper.addFluid(machineRecipe, modifiers, IOType.OUTPUT, recipe.output)) {
                    continue;
                }
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, recipe.getTotalProcessEnergy());
                finish(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    static final class BlastFurnaceAdapter extends BaseAdapter {
        BlastFurnaceAdapter() {
            super("blast_furnace");
        }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            for (BlastFurnaceRecipe recipe : BlastFurnaceRecipe.recipeList) {
                MachineRecipe machineRecipe = shell(owningMachine, modifiers, recipe.time);
                if (!addIngredientObject(machineRecipe, modifiers, recipe.input, 1)
                        || !BotaniaAdapterHelper.addItemOutput(machineRecipe, modifiers, recipe.output)) {
                    continue;
                }
                BotaniaAdapterHelper.addItemOutputIfPresent(machineRecipe, modifiers, recipe.slag);
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, Math.max(40, recipe.time * 4L));
                finish(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    static final class CokeOvenAdapter extends BaseAdapter {
        CokeOvenAdapter() {
            super("coke_oven");
        }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            for (CokeOvenRecipe recipe : CokeOvenRecipe.recipeList) {
                MachineRecipe machineRecipe = shell(owningMachine, modifiers, recipe.time);
                if (!addIngredientObject(machineRecipe, modifiers, recipe.input, 1)
                        || !BotaniaAdapterHelper.addItemOutput(machineRecipe, modifiers, recipe.output)) {
                    continue;
                }
                if (recipe.creosoteOutput > 0 && FluidRegistry.getFluid("creosote") != null) {
                    BotaniaAdapterHelper.addFluid(machineRecipe, modifiers, IOType.OUTPUT,
                            new FluidStack(FluidRegistry.getFluid("creosote"), recipe.creosoteOutput));
                }
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, Math.max(20, recipe.time));
                finish(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    private static boolean addIngredient(MachineRecipe machineRecipe, Collection<RecipeModifier> modifiers, IngredientStack ingredient) {
        if (ingredient == null) {
            return false;
        }
        if (ingredient.fluid != null) {
            return BotaniaAdapterHelper.addFluid(machineRecipe, modifiers, IOType.INPUT, ingredient.fluid);
        }
        int amount = Math.max(1, ingredient.inputSize);
        if (ingredient.oreName != null && !ingredient.oreName.isEmpty()) {
            return BotaniaAdapterHelper.addItemInput(machineRecipe, modifiers, ingredient.oreName, amount);
        }
        List<ItemStack> stackList = ingredient.getSizedStackList();
        if (stackList != null && !stackList.isEmpty()) {
            return BotaniaAdapterHelper.addIngredientChoicesInput(machineRecipe, modifiers, stackList, amount);
        }
        if (ingredient.stack != null) {
            return BotaniaAdapterHelper.addItemInput(machineRecipe, modifiers, ingredient.stack, Math.max(amount, BotaniaAdapterHelper.getStackCount(ingredient.stack)));
        }
        return false;
    }

    private static boolean addIngredientObject(MachineRecipe machineRecipe, Collection<RecipeModifier> modifiers, Object input, int amount) {
        if (input instanceof IngredientStack) {
            return addIngredient(machineRecipe, modifiers, (IngredientStack) input);
        }
        if (input instanceof ItemStack || input instanceof String) {
            return BotaniaAdapterHelper.addItemInput(machineRecipe, modifiers, input, amount);
        }
        return false;
    }

    private static boolean addComparableItem(MachineRecipe machineRecipe, Collection<RecipeModifier> modifiers, ComparableItemStack comparableItemStack) {
        return comparableItemStack != null && comparableItemStack.stack != null
                && BotaniaAdapterHelper.addItemInput(machineRecipe, modifiers, comparableItemStack.stack,
                BotaniaAdapterHelper.getStackCount(comparableItemStack.stack));
    }
}
