package codex.mmbotaniabridge;

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
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.DryingRecipe;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.AlloyRecipe;
import slimeknights.tconstruct.library.smeltery.ICastingRecipe;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

final class TConstructRecipeAdapters {
    private TConstructRecipeAdapters() {
    }

    static Collection<RecipeAdapter> createAdapters() {
        List<RecipeAdapter> adapters = new ArrayList<>();
        adapters.add(new MeltingAdapter());
        adapters.add(new AlloyAdapter());
        adapters.add(new TableCastingAdapter());
        adapters.add(new BasinCastingAdapter());
        adapters.add(new DryingAdapter());
        return adapters;
    }

    private abstract static class BaseAdapter extends RecipeAdapter {
        private final String name;

        BaseAdapter(String name) {
            super(new ResourceLocation("tconstruct", name));
            this.name = name;
        }

        MachineRecipe shell(ResourceLocation owningMachine, List<RecipeModifier> modifiers, int duration) {
            return createRecipeShell(owningMachine,
                    new ResourceLocation("modularmachinery", "tconstruct_" + name + "_" + incId++),
                    BotaniaAdapterHelper.modifyDuration(modifiers, Math.max(20, duration)), 0, false);
        }

        void finish(MachineRecipe machineRecipe,
                    List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                    Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                    List<String> tooltipList) {
            BotaniaAdapterHelper.addTooltip(machineRecipe, tooltipList, "Imported TConstruct " + name + " recipe");
            RecipeAdapter.addAdditionalRequirements(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
        }
    }

    static final class MeltingAdapter extends BaseAdapter {
        MeltingAdapter() { super("melting"); }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            for (MeltingRecipe recipe : TinkerRegistry.getAllMeltingRecipies()) {
                MachineRecipe machineRecipe = shell(owningMachine, modifiers, Math.max(40, recipe.getUsableTemperature() / 4));
                if (!addRecipeMatch(machineRecipe, modifiers, recipe.input)
                        || !BotaniaAdapterHelper.addFluid(machineRecipe, modifiers, IOType.OUTPUT, recipe.getResult())) {
                    continue;
                }
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, Math.max(20, recipe.getUsableTemperature()));
                finish(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    static final class AlloyAdapter extends BaseAdapter {
        AlloyAdapter() { super("alloying"); }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            for (AlloyRecipe recipe : TinkerRegistry.getAlloys()) {
                MachineRecipe machineRecipe = shell(owningMachine, modifiers, 120);
                boolean valid = true;
                for (FluidStack fluidStack : recipe.getFluids()) {
                    if (!BotaniaAdapterHelper.addFluid(machineRecipe, modifiers, IOType.INPUT, fluidStack)) {
                        valid = false;
                        break;
                    }
                }
                if (!valid || !BotaniaAdapterHelper.addFluid(machineRecipe, modifiers, IOType.OUTPUT, recipe.getResult())) {
                    continue;
                }
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, 80);
                finish(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    static final class TableCastingAdapter extends CastingAdapter {
        TableCastingAdapter() { super("table_casting", true); }
    }

    static final class BasinCastingAdapter extends CastingAdapter {
        BasinCastingAdapter() { super("basin_casting", false); }
    }

    private abstract static class CastingAdapter extends BaseAdapter {
        private final boolean table;

        CastingAdapter(String name, boolean table) {
            super(name);
            this.table = table;
        }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            List<ICastingRecipe> castingRecipes = table ? TinkerRegistry.getAllTableCastingRecipes() : TinkerRegistry.getAllBasinCastingRecipes();
            for (ICastingRecipe recipe : castingRecipes) {
                FluidStack fluidStack = null;
                ItemStack output = null;
                try {
                    fluidStack = recipe.getFluid(null, null);
                    output = recipe.getResult(null, fluidStack == null ? null : fluidStack.getFluid());
                } catch (RuntimeException ignored) {
                }
                if ((fluidStack == null || fluidStack.getFluid() == null || fluidStack.amount <= 0)
                        && recipe instanceof slimeknights.tconstruct.library.smeltery.CastingRecipe) {
                    fluidStack = ((slimeknights.tconstruct.library.smeltery.CastingRecipe) recipe).getFluid();
                    output = ((slimeknights.tconstruct.library.smeltery.CastingRecipe) recipe).getResult();
                }
                MachineRecipe machineRecipe = shell(owningMachine, modifiers, recipe.getTime());
                if (!BotaniaAdapterHelper.addFluid(machineRecipe, modifiers, IOType.INPUT, fluidStack)
                        || !BotaniaAdapterHelper.addItemOutput(machineRecipe, modifiers, output)) {
                    continue;
                }
                if (recipe instanceof slimeknights.tconstruct.library.smeltery.CastingRecipe) {
                    RecipeMatch cast = ((slimeknights.tconstruct.library.smeltery.CastingRecipe) recipe).cast;
                    if (cast != null && !addRecipeMatch(machineRecipe, modifiers, cast)) {
                        continue;
                    }
                }
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, Math.max(20, recipe.getTime()));
                finish(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    static final class DryingAdapter extends BaseAdapter {
        DryingAdapter() { super("drying"); }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            for (DryingRecipe recipe : TinkerRegistry.getAllDryingRecipes()) {
                MachineRecipe machineRecipe = shell(owningMachine, modifiers, recipe.getTime());
                if (!addRecipeMatch(machineRecipe, modifiers, recipe.input)
                        || !BotaniaAdapterHelper.addItemOutput(machineRecipe, modifiers, recipe.getResult())) {
                    continue;
                }
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, Math.max(20, recipe.getTime() / 10L));
                finish(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    private static boolean addRecipeMatch(MachineRecipe machineRecipe, Collection<RecipeModifier> modifiers, RecipeMatch recipeMatch) {
        if (recipeMatch == null) {
            return true;
        }
        List<ItemStack> inputs = recipeMatch.getInputs();
        if (inputs == null || inputs.isEmpty()) {
            return false;
        }
        return BotaniaAdapterHelper.addIngredientChoicesInput(machineRecipe, modifiers, inputs, Math.max(1, recipeMatch.amountNeeded));
    }
}
