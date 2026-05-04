package codex.mmbotaniabridge;

import crafttweaker.util.IEventHandler;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.ICentrifugeRecipe;
import forestry.api.recipes.ICraftingProvider;
import forestry.api.recipes.IDescriptiveRecipe;
import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.IFermenterRecipe;
import forestry.api.recipes.IForestryRecipe;
import forestry.api.recipes.IMoistenerRecipe;
import forestry.api.recipes.ISqueezerRecipe;
import forestry.api.recipes.IStillRecipe;
import forestry.api.recipes.RecipeManagers;
import github.kasuminova.mmce.common.event.recipe.RecipeEvent;
import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.adapter.RecipeAdapter;
import hellfirepvp.modularmachinery.common.crafting.helper.ComponentRequirement;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

final class ForestryRecipeAdapters {
    private ForestryRecipeAdapters() {
    }

    static Collection<RecipeAdapter> createAdapters() {
        List<RecipeAdapter> adapters = new ArrayList<>();
        adapters.add(new CarpenterAdapter());
        adapters.add(new CentrifugeAdapter());
        adapters.add(new SqueezerAdapter());
        adapters.add(new StillAdapter());
        adapters.add(new FermenterAdapter());
        adapters.add(new MoistenerAdapter());
        adapters.add(new FabricatorAdapter());
        return adapters;
    }

    private abstract static class BaseAdapter<T extends IForestryRecipe> extends RecipeAdapter {
        private final String name;

        BaseAdapter(String name) {
            super(new ResourceLocation("forestry", name));
            this.name = name;
        }

        abstract ICraftingProvider<T> manager();

        abstract int duration(T recipe);

        abstract boolean addRecipe(MachineRecipe machineRecipe, List<RecipeModifier> modifiers, T recipe);

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            ICraftingProvider<T> manager = manager();
            if (manager == null) {
                return recipes;
            }
            for (T recipe : manager.recipes()) {
                MachineRecipe machineRecipe = createRecipeShell(owningMachine,
                        new ResourceLocation("modularmachinery", "forestry_" + name + "_" + incId++),
                        BotaniaAdapterHelper.modifyDuration(modifiers, Math.max(20, duration(recipe))), 0, false);
                if (!addRecipe(machineRecipe, modifiers, recipe)) {
                    continue;
                }
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, Math.max(20, duration(recipe) * 2L));
                BotaniaAdapterHelper.addTooltip(machineRecipe, tooltipList, "Imported Forestry " + name + " recipe");
                RecipeAdapter.addAdditionalRequirements(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    static final class CarpenterAdapter extends BaseAdapter<ICarpenterRecipe> {
        CarpenterAdapter() { super("carpenter"); }
        ICraftingProvider<ICarpenterRecipe> manager() { return RecipeManagers.carpenterManager; }
        int duration(ICarpenterRecipe recipe) { return recipe.getPackagingTime(); }
        boolean addRecipe(MachineRecipe mr, List<RecipeModifier> mods, ICarpenterRecipe recipe) {
            IDescriptiveRecipe grid = recipe.getCraftingGridRecipe();
            if (grid == null || !BotaniaAdapterHelper.addItemOutput(mr, mods, grid.getOutput())) return false;
            if (!BotaniaAdapterHelper.addItemInputIfPresent(mr, mods, recipe.getBox())) return false;
            BotaniaAdapterHelper.addFluidIfPresent(mr, mods, IOType.INPUT, recipe.getFluidResource());
            return addGrid(mr, mods, grid.getRawIngredients(), grid.getOreDicts());
        }
    }

    static final class CentrifugeAdapter extends BaseAdapter<ICentrifugeRecipe> {
        CentrifugeAdapter() { super("centrifuge"); }
        ICraftingProvider<ICentrifugeRecipe> manager() { return RecipeManagers.centrifugeManager; }
        int duration(ICentrifugeRecipe recipe) { return recipe.getProcessingTime(); }
        boolean addRecipe(MachineRecipe mr, List<RecipeModifier> mods, ICentrifugeRecipe recipe) {
            if (!BotaniaAdapterHelper.addItemInput(mr, mods, recipe.getInput(), BotaniaAdapterHelper.getStackCount(recipe.getInput()))) return false;
            for (Map.Entry<ItemStack, Float> entry : recipe.getAllProducts().entrySet()) {
                BotaniaAdapterHelper.addChancedItemOutput(mr, mods, entry.getKey(), entry.getValue());
            }
            return true;
        }
    }

    static final class SqueezerAdapter extends BaseAdapter<ISqueezerRecipe> {
        SqueezerAdapter() { super("squeezer"); }
        ICraftingProvider<ISqueezerRecipe> manager() { return RecipeManagers.squeezerManager; }
        int duration(ISqueezerRecipe recipe) { return recipe.getProcessingTime(); }
        boolean addRecipe(MachineRecipe mr, List<RecipeModifier> mods, ISqueezerRecipe recipe) {
            if (!BotaniaAdapterHelper.addItemInputs(mr, mods, recipe.getResources(), 1)) return false;
            BotaniaAdapterHelper.addFluidIfPresent(mr, mods, IOType.OUTPUT, recipe.getFluidOutput());
            BotaniaAdapterHelper.addChancedItemOutput(mr, mods, recipe.getRemnants(), recipe.getRemnantsChance());
            return true;
        }
    }

    static final class StillAdapter extends BaseAdapter<IStillRecipe> {
        StillAdapter() { super("still"); }
        ICraftingProvider<IStillRecipe> manager() { return RecipeManagers.stillManager; }
        int duration(IStillRecipe recipe) { return Math.max(20, recipe.getCyclesPerUnit() * 20); }
        boolean addRecipe(MachineRecipe mr, List<RecipeModifier> mods, IStillRecipe recipe) {
            return BotaniaAdapterHelper.addFluid(mr, mods, IOType.INPUT, recipe.getInput())
                    && BotaniaAdapterHelper.addFluid(mr, mods, IOType.OUTPUT, recipe.getOutput());
        }
    }

    static final class FermenterAdapter extends BaseAdapter<IFermenterRecipe> {
        FermenterAdapter() { super("fermenter"); }
        ICraftingProvider<IFermenterRecipe> manager() { return RecipeManagers.fermenterManager; }
        int duration(IFermenterRecipe recipe) { return 100; }
        boolean addRecipe(MachineRecipe mr, List<RecipeModifier> mods, IFermenterRecipe recipe) {
            if (recipe.getResourceOreName() != null && !recipe.getResourceOreName().isEmpty()) {
                if (!BotaniaAdapterHelper.addItemInput(mr, mods, recipe.getResourceOreName(), 1)) return false;
            } else if (!BotaniaAdapterHelper.addItemInputIfPresent(mr, mods, recipe.getResource())) return false;
            BotaniaAdapterHelper.addFluidIfPresent(mr, mods, IOType.INPUT, recipe.getFluidResource());
            FluidStack output = new FluidStack(recipe.getOutput(), Math.max(1, Math.round(recipe.getFermentationValue() * recipe.getModifier())));
            return BotaniaAdapterHelper.addFluid(mr, mods, IOType.OUTPUT, output);
        }
    }

    static final class MoistenerAdapter extends BaseAdapter<IMoistenerRecipe> {
        MoistenerAdapter() { super("moistener"); }
        ICraftingProvider<IMoistenerRecipe> manager() { return RecipeManagers.moistenerManager; }
        int duration(IMoistenerRecipe recipe) { return recipe.getTimePerItem(); }
        boolean addRecipe(MachineRecipe mr, List<RecipeModifier> mods, IMoistenerRecipe recipe) {
            return BotaniaAdapterHelper.addItemInput(mr, mods, recipe.getResource(), BotaniaAdapterHelper.getStackCount(recipe.getResource()))
                    && BotaniaAdapterHelper.addItemOutput(mr, mods, recipe.getProduct());
        }
    }

    static final class FabricatorAdapter extends BaseAdapter<IFabricatorRecipe> {
        FabricatorAdapter() { super("fabricator"); }
        ICraftingProvider<IFabricatorRecipe> manager() { return RecipeManagers.fabricatorManager; }
        int duration(IFabricatorRecipe recipe) { return 100; }
        boolean addRecipe(MachineRecipe mr, List<RecipeModifier> mods, IFabricatorRecipe recipe) {
            if (!BotaniaAdapterHelper.addItemOutput(mr, mods, recipe.getRecipeOutput())) return false;
            BotaniaAdapterHelper.addFluidIfPresent(mr, mods, IOType.INPUT, recipe.getLiquid());
            BotaniaAdapterHelper.addItemInputIfPresent(mr, mods, recipe.getPlan());
            return addGrid(mr, mods, recipe.getIngredients(), recipe.getOreDicts());
        }
    }

    private static boolean addGrid(MachineRecipe machineRecipe, List<RecipeModifier> modifiers,
                                   NonNullList<NonNullList<ItemStack>> ingredients, NonNullList<String> oreDicts) {
        for (int i = 0; i < ingredients.size(); i++) {
            String oreDict = oreDicts != null && i < oreDicts.size() ? oreDicts.get(i) : null;
            if (oreDict != null && !oreDict.isEmpty()) {
                if (!BotaniaAdapterHelper.addItemInput(machineRecipe, modifiers, oreDict, 1)) return false;
                continue;
            }
            NonNullList<ItemStack> choices = ingredients.get(i);
            if (choices != null && !choices.isEmpty() && !BotaniaAdapterHelper.addIngredientChoicesInput(machineRecipe, modifiers, choices, 1)) {
                return false;
            }
        }
        return true;
    }
}
