package codex.mmbotaniabridge;

import crazypants.enderio.base.recipe.IManyToOneRecipe;
import crazypants.enderio.base.recipe.IRecipe;
import crazypants.enderio.base.recipe.IRecipeInput;
import crazypants.enderio.base.recipe.RecipeOutput;
import crazypants.enderio.base.recipe.alloysmelter.AlloyRecipeManager;
import crazypants.enderio.base.recipe.sagmill.SagMillRecipeManager;
import crazypants.enderio.base.recipe.slicensplice.SliceAndSpliceRecipeManager;
import crazypants.enderio.base.recipe.vat.VatRecipeManager;
import crafttweaker.util.IEventHandler;
import github.kasuminova.mmce.common.event.recipe.RecipeEvent;
import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.adapter.RecipeAdapter;
import hellfirepvp.modularmachinery.common.crafting.helper.ComponentRequirement;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

final class EnderIORecipeAdapters {
    private EnderIORecipeAdapters() {
    }

    static Collection<RecipeAdapter> createAdapters() {
        List<RecipeAdapter> adapters = new ArrayList<>();
        adapters.add(new SagMillAdapter());
        adapters.add(new AlloySmelterAdapter());
        adapters.add(new SliceAndSpliceAdapter());
        adapters.add(new VatAdapter());
        return adapters;
    }

    private abstract static class BaseAdapter extends RecipeAdapter {
        private final String name;

        BaseAdapter(String name) {
            super(new ResourceLocation("enderio", name));
            this.name = name;
        }

        MachineRecipe shell(ResourceLocation owningMachine, List<RecipeModifier> modifiers, int energy) {
            return createRecipeShell(owningMachine,
                    new ResourceLocation("modularmachinery", "enderio_" + name + "_" + incId++),
                    BotaniaAdapterHelper.modifyDuration(modifiers, Math.max(20, Math.min(1200, energy / 20))), 0, false);
        }

        void finish(MachineRecipe machineRecipe,
                    List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                    Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                    List<String> tooltipList) {
            BotaniaAdapterHelper.addTooltip(machineRecipe, tooltipList, "Imported Ender IO " + name + " recipe");
            RecipeAdapter.addAdditionalRequirements(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
        }
    }

    static final class SagMillAdapter extends BaseAdapter {
        SagMillAdapter() { super("sag_mill"); }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            for (IRecipe recipe : SagMillRecipeManager.getInstance().getRecipes()) {
                MachineRecipe machineRecipe = shell(owningMachine, modifiers, recipe.getEnergyRequired());
                if (!addInputs(machineRecipe, modifiers, recipe.getInputs()) || !addOutputs(machineRecipe, modifiers, recipe.getOutputs())) {
                    continue;
                }
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, recipe.getEnergyRequired());
                finish(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    static final class AlloySmelterAdapter extends BaseAdapter {
        AlloySmelterAdapter() { super("alloy_smelter"); }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            return manyToOneRecipes(owningMachine, modifiers, additionalRecipeRequirements, recipeEventHandlers, tooltipList,
                    AlloyRecipeManager.getInstance().getRecipes(), this);
        }
    }

    static final class SliceAndSpliceAdapter extends BaseAdapter {
        SliceAndSpliceAdapter() { super("slice_and_splice"); }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            return manyToOneRecipes(owningMachine, modifiers, additionalRecipeRequirements, recipeEventHandlers, tooltipList,
                    SliceAndSpliceRecipeManager.getInstance().getRecipes(), this);
        }
    }

    static final class VatAdapter extends BaseAdapter {
        VatAdapter() { super("vat"); }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            for (IRecipe recipe : VatRecipeManager.getInstance().getRecipes()) {
                MachineRecipe machineRecipe = shell(owningMachine, modifiers, recipe.getEnergyRequired());
                if (!addInputs(machineRecipe, modifiers, recipe.getInputs()) || !addOutputs(machineRecipe, modifiers, recipe.getOutputs())) {
                    continue;
                }
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, recipe.getEnergyRequired());
                finish(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    private static Collection<MachineRecipe> manyToOneRecipes(ResourceLocation owningMachine,
                                                             List<RecipeModifier> modifiers,
                                                             List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                             Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                             List<String> tooltipList,
                                                             Collection<IManyToOneRecipe> recipeList,
                                                             BaseAdapter adapter) {
        List<MachineRecipe> recipes = new ArrayList<>();
        for (IManyToOneRecipe recipe : recipeList) {
            MachineRecipe machineRecipe = adapter.shell(owningMachine, modifiers, recipe.getEnergyRequired());
            if (!addInputs(machineRecipe, modifiers, recipe.getInputs()) || !addOutputs(machineRecipe, modifiers, recipe.getOutputs())) {
                continue;
            }
            BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, recipe.getEnergyRequired());
            adapter.finish(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
            recipes.add(machineRecipe);
        }
        return recipes;
    }

    private static boolean addInputs(MachineRecipe machineRecipe, List<RecipeModifier> modifiers, IRecipeInput[] inputs) {
        for (IRecipeInput input : inputs) {
            if (input == null || !input.isValid()) {
                continue;
            }
            if (input.isFluid()) {
                FluidStack fluidStack = input.getFluidInput();
                if (!BotaniaAdapterHelper.addFluid(machineRecipe, modifiers, IOType.INPUT, fluidStack)) {
                    return false;
                }
            } else {
                ItemStack[] alternatives = input.getEquivelentInputs();
                if (alternatives != null && alternatives.length > 0) {
                    List<ItemStack> choices = new ArrayList<>();
                    for (ItemStack alternative : alternatives) {
                        if (alternative != null && !BotaniaAdapterHelper.isStackEmpty(alternative)) {
                            choices.add(alternative);
                        }
                    }
                    if (!choices.isEmpty()) {
                        if (!BotaniaAdapterHelper.addIngredientChoicesInput(machineRecipe, modifiers, choices, Math.max(1, input.getStackSize()))) {
                            return false;
                        }
                        continue;
                    }
                }
                ItemStack stack = input.getInput();
                if (!BotaniaAdapterHelper.addItemInput(machineRecipe, modifiers, stack, Math.max(1, input.getStackSize()))) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean addOutputs(MachineRecipe machineRecipe, List<RecipeModifier> modifiers, RecipeOutput[] outputs) {
        for (RecipeOutput output : outputs) {
            if (output == null || !output.isValid()) {
                continue;
            }
            if (output.isFluid()) {
                float chance = output.getChance();
                if (chance >= 1.0f) {
                    if (!BotaniaAdapterHelper.addFluid(machineRecipe, modifiers, IOType.OUTPUT, output.getFluidOutput())) {
                        return false;
                    }
                } else {
                    BotaniaAdapterHelper.addChancedFluidOutput(machineRecipe, modifiers, output.getFluidOutput(), chance);
                }
            } else {
                float chance = output.getChance();
                if (chance >= 1.0f) {
                    if (!BotaniaAdapterHelper.addItemOutput(machineRecipe, modifiers, output.getOutput())) {
                        return false;
                    }
                } else {
                    BotaniaAdapterHelper.addChancedItemOutput(machineRecipe, modifiers, output.getOutput(), chance);
                }
            }
        }
        return true;
    }
}
