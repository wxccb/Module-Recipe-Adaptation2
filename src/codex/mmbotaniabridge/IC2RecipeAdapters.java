package codex.mmbotaniabridge;

import crafttweaker.util.IEventHandler;
import github.kasuminova.mmce.common.event.recipe.RecipeEvent;
import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.adapter.RecipeAdapter;
import hellfirepvp.modularmachinery.common.crafting.helper.ComponentRequirement;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementFluid;
import hellfirepvp.modularmachinery.common.lib.RequirementTypesMM;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import ic2.api.item.IC2Items;
import ic2.api.recipe.IBasicMachineRecipeManager;
import ic2.api.recipe.ICannerBottleRecipeManager;
import ic2.api.recipe.ICannerEnrichRecipeManager;
import ic2.api.recipe.IElectrolyzerRecipeManager;
import ic2.api.recipe.IFermenterRecipeManager;
import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.MachineRecipeResult;
import ic2.api.recipe.Recipes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

final class IC2RecipeAdapters {
    private IC2RecipeAdapters() {
    }

    static Collection<RecipeAdapter> createAdapters() {
        List<RecipeAdapter> adapters = new ArrayList<>();
        adapters.add(new BasicAdapter("macerator", Recipes.macerator, 120, 2));
        adapters.add(new BasicAdapter("extractor", Recipes.extractor, 120, 2));
        adapters.add(new BasicAdapter("compressor", Recipes.compressor, 120, 2));
        adapters.add(new BasicAdapter("centrifuge", Recipes.centrifuge, 500, 16));
        adapters.add(new BasicAdapter("blockcutter", Recipes.blockcutter, 200, 8));
        adapters.add(new BasicAdapter("blastfurnace", Recipes.blastfurnace, 400, 32));
        adapters.add(new BasicAdapter("recycler", Recipes.recycler, 45, 1));
        adapters.add(new BasicAdapter("metalformer_extruding", Recipes.metalformerExtruding, 120, 4));
        adapters.add(new BasicAdapter("metalformer_cutting", Recipes.metalformerCutting, 120, 4));
        adapters.add(new BasicAdapter("metalformer_rolling", Recipes.metalformerRolling, 120, 4));
        adapters.add(new BasicAdapter("ore_washing", Recipes.oreWashing, 200, 16, new FluidStack(FluidRegistry.WATER, 1000)));
        adapters.add(new FurnaceAdapter());
        adapters.add(new CannerBottleAdapter());
        adapters.add(new CannerEnrichAdapter());
        adapters.add(new ElectrolyzerAdapter());
        adapters.add(new FermenterAdapter());
        adapters.add(new MatterAmplifierAdapter());
        adapters.add(new ScrapboxDropsAdapter());
        return adapters;
    }

    static final class BasicAdapter extends RecipeAdapter {
        private final String name;
        private final IBasicMachineRecipeManager manager;
        private final int duration;
        private final long energy;
        private final FluidStack extraFluidInput;

        BasicAdapter(String name, IBasicMachineRecipeManager manager, int duration, long energy) {
            this(name, manager, duration, energy, null);
        }

        BasicAdapter(String name, IBasicMachineRecipeManager manager, int duration, long energy, FluidStack extraFluidInput) {
            super(new ResourceLocation("ic2", name));
            this.name = name;
            this.manager = manager;
            this.duration = duration;
            this.energy = energy;
            this.extraFluidInput = extraFluidInput;
        }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            if (manager == null || !manager.isIterable()) {
                return recipes;
            }
            for (ic2.api.recipe.MachineRecipe<IRecipeInput, Collection<ItemStack>> recipe : manager.getRecipes()) {
                MachineRecipe machineRecipe = createRecipeShell(owningMachine,
                        new ResourceLocation("modularmachinery", "ic2_" + name + "_" + incId++),
                        BotaniaAdapterHelper.modifyDuration(modifiers, duration), 0, false);
                if (!addRecipeInput(machineRecipe, modifiers, recipe.getInput())) {
                    continue;
                }
                boolean valid = true;
                for (ItemStack output : recipe.getOutput()) {
                    if (!BotaniaAdapterHelper.addItemOutput(machineRecipe, modifiers, output)) {
                        valid = false;
                        break;
                    }
                }
                if (!valid) {
                    continue;
                }
                if (extraFluidInput != null) {
                    addFluid(machineRecipe, modifiers, IOType.INPUT, extraFluidInput);
                }
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, energy);
                BotaniaAdapterHelper.addTooltip(machineRecipe, tooltipList, "Imported IC2 " + name + " recipe");
                RecipeAdapter.addAdditionalRequirements(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    static final class FurnaceAdapter extends RecipeAdapter {
        FurnaceAdapter() {
            super(new ResourceLocation("ic2", "electric_furnace"));
        }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            if (Recipes.furnace == null || !Recipes.furnace.isIterable()) {
                return recipes;
            }
            for (ic2.api.recipe.MachineRecipe<ItemStack, ItemStack> recipe : Recipes.furnace.getRecipes()) {
                MachineRecipe machineRecipe = createRecipeShell(owningMachine,
                        new ResourceLocation("modularmachinery", "ic2_electric_furnace_" + incId++),
                        BotaniaAdapterHelper.modifyDuration(modifiers, 100), 0, false);
                if (!BotaniaAdapterHelper.addItemInput(machineRecipe, modifiers, recipe.getInput(), BotaniaAdapterHelper.getStackCount(recipe.getInput()))) {
                    continue;
                }
                if (!BotaniaAdapterHelper.addItemOutput(machineRecipe, modifiers, recipe.getOutput())) {
                    continue;
                }
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, 4);
                BotaniaAdapterHelper.addTooltip(machineRecipe, tooltipList, "Imported IC2 electric furnace recipe");
                RecipeAdapter.addAdditionalRequirements(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    static final class CannerBottleAdapter extends RecipeAdapter {
        CannerBottleAdapter() {
            super(new ResourceLocation("ic2", "canner_bottle"));
        }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            if (Recipes.cannerBottle == null || !Recipes.cannerBottle.isIterable()) {
                return recipes;
            }
            for (ic2.api.recipe.MachineRecipe<ICannerBottleRecipeManager.Input, ItemStack> recipe : Recipes.cannerBottle.getRecipes()) {
                MachineRecipe machineRecipe = createRecipeShell(owningMachine,
                        new ResourceLocation("modularmachinery", "ic2_canner_bottle_" + incId++),
                        BotaniaAdapterHelper.modifyDuration(modifiers, 120), 0, false);
                if (!addRecipeInput(machineRecipe, modifiers, recipe.getInput().container)) {
                    continue;
                }
                if (!addRecipeInput(machineRecipe, modifiers, recipe.getInput().fill)) {
                    continue;
                }
                if (!BotaniaAdapterHelper.addItemOutput(machineRecipe, modifiers, recipe.getOutput())) {
                    continue;
                }
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, 4);
                BotaniaAdapterHelper.addTooltip(machineRecipe, tooltipList, "Imported IC2 canner bottle recipe");
                RecipeAdapter.addAdditionalRequirements(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    static final class CannerEnrichAdapter extends RecipeAdapter {
        CannerEnrichAdapter() {
            super(new ResourceLocation("ic2", "canner_enrich"));
        }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            if (Recipes.cannerEnrich == null || !Recipes.cannerEnrich.isIterable()) {
                return recipes;
            }
            for (ic2.api.recipe.MachineRecipe<ICannerEnrichRecipeManager.Input, FluidStack> recipe : Recipes.cannerEnrich.getRecipes()) {
                MachineRecipe machineRecipe = createRecipeShell(owningMachine,
                        new ResourceLocation("modularmachinery", "ic2_canner_enrich_" + incId++),
                        BotaniaAdapterHelper.modifyDuration(modifiers, 120), 0, false);
                addFluid(machineRecipe, modifiers, IOType.INPUT, recipe.getInput().fluid);
                if (!addRecipeInput(machineRecipe, modifiers, recipe.getInput().additive)) {
                    continue;
                }
                addFluid(machineRecipe, modifiers, IOType.OUTPUT, recipe.getOutput());
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, 4);
                BotaniaAdapterHelper.addTooltip(machineRecipe, tooltipList, "Imported IC2 canner enrich recipe");
                RecipeAdapter.addAdditionalRequirements(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    static final class ElectrolyzerAdapter extends RecipeAdapter {
        ElectrolyzerAdapter() {
            super(new ResourceLocation("ic2", "electrolyzer"));
        }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            if (Recipes.electrolyzer == null) {
                return recipes;
            }
            for (Map.Entry<String, IElectrolyzerRecipeManager.ElectrolyzerRecipe> entry : Recipes.electrolyzer.getRecipeMap().entrySet()) {
                IElectrolyzerRecipeManager.ElectrolyzerRecipe recipe = entry.getValue();
                Fluid inputFluid = FluidRegistry.getFluid(entry.getKey());
                if (inputFluid == null) {
                    continue;
                }
                FluidStack input = new FluidStack(inputFluid, recipe.inputAmount);
                MachineRecipe machineRecipe = createRecipeShell(owningMachine,
                        new ResourceLocation("modularmachinery", "ic2_electrolyzer_" + entry.getKey() + "_" + incId++),
                        BotaniaAdapterHelper.modifyDuration(modifiers, Math.max(1, recipe.ticksNeeded)), 0, false);
                addFluid(machineRecipe, modifiers, IOType.INPUT, input);
                for (IElectrolyzerRecipeManager.ElectrolyzerOutput output : recipe.outputs) {
                    addFluid(machineRecipe, modifiers, IOType.OUTPUT, output.getOutput());
                }
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, Math.max(1, recipe.EUaTick));
                BotaniaAdapterHelper.addTooltip(machineRecipe, tooltipList, "Imported IC2 electrolyzer recipe");
                RecipeAdapter.addAdditionalRequirements(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    static final class FermenterAdapter extends RecipeAdapter {
        FermenterAdapter() {
            super(new ResourceLocation("ic2", "fermenter"));
        }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            if (Recipes.fermenter == null) {
                return recipes;
            }
            for (Map.Entry<String, IFermenterRecipeManager.FermentationProperty> entry : Recipes.fermenter.getRecipeMap().entrySet()) {
                IFermenterRecipeManager.FermentationProperty recipe = entry.getValue();
                Fluid inputFluid = FluidRegistry.getFluid(entry.getKey());
                FluidStack output = recipe.getOutput();
                if (inputFluid == null || output == null) {
                    continue;
                }
                FluidStack input = new FluidStack(inputFluid, recipe.inputAmount);
                MachineRecipe machineRecipe = createRecipeShell(owningMachine,
                        new ResourceLocation("modularmachinery", "ic2_fermenter_" + entry.getKey() + "_" + incId++),
                        BotaniaAdapterHelper.modifyDuration(modifiers, Math.max(100, recipe.heat)), 0, false);
                addFluid(machineRecipe, modifiers, IOType.INPUT, input);
                addFluid(machineRecipe, modifiers, IOType.OUTPUT, output);
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, Math.max(1, recipe.heat / 50));
                BotaniaAdapterHelper.addTooltip(machineRecipe, tooltipList, "Imported IC2 fermenter recipe");
                RecipeAdapter.addAdditionalRequirements(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    static final class MatterAmplifierAdapter extends RecipeAdapter {
        MatterAmplifierAdapter() {
            super(new ResourceLocation("ic2", "matter_amplifier"));
        }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            IMachineRecipeManager<IRecipeInput, Integer, ItemStack> manager = Recipes.matterAmplifier;
            if (manager == null || !manager.isIterable()) {
                return recipes;
            }
            for (ic2.api.recipe.MachineRecipe<IRecipeInput, Integer> recipe : manager.getRecipes()) {
                MachineRecipe machineRecipe = createRecipeShell(owningMachine,
                        new ResourceLocation("modularmachinery", "ic2_matter_amplifier_" + incId++),
                        BotaniaAdapterHelper.modifyDuration(modifiers, 20), 0, false);
                if (!addRecipeInput(machineRecipe, modifiers, recipe.getInput())) {
                    continue;
                }
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, Math.max(1, recipe.getOutput()));
                BotaniaAdapterHelper.addTooltip(machineRecipe, tooltipList, "Imported IC2 matter amplifier recipe");
                BotaniaAdapterHelper.addTooltip(machineRecipe, tooltipList, "Amplification: " + recipe.getOutput());
                RecipeAdapter.addAdditionalRequirements(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    static final class ScrapboxDropsAdapter extends RecipeAdapter {
        ScrapboxDropsAdapter() {
            super(new ResourceLocation("ic2", "scrapbox_drops"));
        }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            if (Recipes.scrapboxDrops == null) {
                return recipes;
            }
            ItemStack scrapBox = IC2Items.getItem("crafting", "scrap_box");
            if (scrapBox == null || BotaniaAdapterHelper.isStackEmpty(scrapBox)) {
                return recipes;
            }
            for (Map.Entry<ItemStack, Float> drop : Recipes.scrapboxDrops.getDrops().entrySet()) {
                MachineRecipe machineRecipe = createRecipeShell(owningMachine,
                        new ResourceLocation("modularmachinery", "ic2_scrapbox_drop_" + incId++),
                        BotaniaAdapterHelper.modifyDuration(modifiers, 20), 0, false);
                if (!BotaniaAdapterHelper.addItemInput(machineRecipe, modifiers, scrapBox, 1)) {
                    continue;
                }
                BotaniaAdapterHelper.addChancedItemOutput(machineRecipe, modifiers, drop.getKey(), drop.getValue());
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, 1);
                BotaniaAdapterHelper.addTooltip(machineRecipe, tooltipList, "Imported IC2 scrapbox drop");
                RecipeAdapter.addAdditionalRequirements(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    private static boolean addRecipeInput(MachineRecipe machineRecipe, Collection<RecipeModifier> modifiers, IRecipeInput input) {
        if (input == null) {
            return false;
        }
        List<ItemStack> candidates = input.getInputs();
        if (candidates == null || candidates.isEmpty()) {
            return false;
        }
        return BotaniaAdapterHelper.addIngredientChoicesInput(machineRecipe, modifiers, candidates, Math.max(1, input.getAmount()));
    }

    private static void addFluid(MachineRecipe machineRecipe, Collection<RecipeModifier> modifiers, IOType ioType, FluidStack fluidStack) {
        if (fluidStack == null || fluidStack.getFluid() == null || fluidStack.amount <= 0) {
            return;
        }
        FluidStack stack = fluidStack.copy();
        int modifiedAmount = Math.round(RecipeModifier.applyModifiers(
                modifiers, RequirementTypesMM.REQUIREMENT_FLUID, ioType, (float) stack.amount, false));
        if (modifiedAmount <= 0) {
            return;
        }
        stack.amount = modifiedAmount;
        machineRecipe.addRequirement(new RequirementFluid(ioType, stack));
    }
}
