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
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

final class ThermalExpansionRecipeAdapters {
    private ThermalExpansionRecipeAdapters() {
    }

    static Collection<RecipeAdapter> createAdapters() {
        List<RecipeAdapter> adapters = new ArrayList<>();
        adapters.add(new PulverizerAdapter());
        adapters.add(new SawmillAdapter());
        adapters.add(new InductionSmelterAdapter());
        adapters.add(new PhytogenicInsolatorAdapter());
        adapters.add(new MagmaCrucibleAdapter());
        adapters.add(new FractionatingStillAdapter());
        adapters.add(new EnergeticInfuserAdapter());
        adapters.add(new FluidTransposerAdapter("fluid_transposer_fill", true));
        adapters.add(new FluidTransposerAdapter("fluid_transposer_extract", false));
        adapters.add(new EnchanterAdapter());
        adapters.add(new CentrifugalSeparatorAdapter("centrifugal_separator", false));
        adapters.add(new CentrifugalSeparatorAdapter("centrifugal_separator_mobs", true));
        adapters.add(new AlchemicalImbuerAdapter());
        adapters.add(new IgneousExtruderAdapter("igneous_extruder_igneous", false));
        adapters.add(new IgneousExtruderAdapter("igneous_extruder_sedimentary", true));
        adapters.add(new CompactorAdapter("compactor_all", "ALL"));
        adapters.add(new CompactorAdapter("compactor_plate", "PLATE"));
        adapters.add(new CompactorAdapter("compactor_coin", "COIN"));
        adapters.add(new CompactorAdapter("compactor_gear", "GEAR"));
        adapters.add(new RedstoneFurnaceAdapter("redstone_furnace", false));
        adapters.add(new RedstoneFurnaceAdapter("redstone_furnace_pyrolysis", true));
        adapters.add(new GlacialPrecipitatorAdapter());
        return adapters;
    }

    private abstract static class BaseAdapter extends RecipeAdapter {
        private final String name;

        BaseAdapter(String name) {
            super(new ResourceLocation("thermalexpansion", name));
            this.name = name;
        }

        MachineRecipe shell(ResourceLocation owningMachine, List<RecipeModifier> modifiers, int energy) {
            return createRecipeShell(owningMachine,
                    new ResourceLocation("modularmachinery", "te_" + name + "_" + incId++),
                    BotaniaAdapterHelper.modifyDuration(modifiers, Math.max(20, Math.min(1200, energy / 20))), 0, false);
        }

        void finish(MachineRecipe machineRecipe,
                    List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                    Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                    List<String> tooltipList) {
            BotaniaAdapterHelper.addTooltip(machineRecipe, tooltipList, "Imported Thermal Expansion " + name + " recipe");
            RecipeAdapter.addAdditionalRequirements(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
        }
    }

    private abstract static class RecipeListAdapter extends BaseAdapter {
        RecipeListAdapter(String name) {
            super(name);
        }

        abstract Object[] recipes() throws Exception;

        abstract boolean addRecipe(MachineRecipe machineRecipe, List<RecipeModifier> modifiers, Object recipe) throws Exception;

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            try {
                for (Object recipe : recipes()) {
                    int energy = integer(call(recipe, "getEnergy"));
                    MachineRecipe machineRecipe = shell(owningMachine, modifiers, energy);
                    if (!addRecipe(machineRecipe, modifiers, recipe)) {
                        continue;
                    }
                    BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, energy);
                    finish(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                    recipes.add(machineRecipe);
                }
            } catch (Exception exception) {
                throw new RuntimeException("Unable to import Thermal Expansion recipes for " + getRegistryName(), exception);
            }
            return recipes;
        }
    }

    static final class PulverizerAdapter extends RecipeListAdapter {
        PulverizerAdapter() { super("pulverizer"); }
        Object[] recipes() throws Exception { return staticArray("cofh.thermalexpansion.util.managers.machine.PulverizerManager", "getRecipeList"); }
        boolean addRecipe(MachineRecipe mr, List<RecipeModifier> mods, Object r) throws Exception {
            return addItemInput(mr, mods, stack(r, "getInput")) && BotaniaAdapterHelper.addItemOutput(mr, mods, stack(r, "getPrimaryOutput"))
                    && addChance(mr, mods, stack(r, "getSecondaryOutput"), integer(call(r, "getSecondaryOutputChance")));
        }
    }

    static final class SawmillAdapter extends RecipeListAdapter {
        SawmillAdapter() { super("sawmill"); }
        Object[] recipes() throws Exception { return staticArray("cofh.thermalexpansion.util.managers.machine.SawmillManager", "getRecipeList"); }
        boolean addRecipe(MachineRecipe mr, List<RecipeModifier> mods, Object r) throws Exception {
            return addItemInput(mr, mods, stack(r, "getInput")) && BotaniaAdapterHelper.addItemOutput(mr, mods, stack(r, "getPrimaryOutput"))
                    && addChance(mr, mods, stack(r, "getSecondaryOutput"), integer(call(r, "getSecondaryOutputChance")));
        }
    }

    static final class InductionSmelterAdapter extends RecipeListAdapter {
        InductionSmelterAdapter() { super("induction_smelter"); }
        Object[] recipes() throws Exception { return staticArray("cofh.thermalexpansion.util.managers.machine.SmelterManager", "getRecipeList"); }
        boolean addRecipe(MachineRecipe mr, List<RecipeModifier> mods, Object r) throws Exception {
            return addItemInput(mr, mods, stack(r, "getPrimaryInput")) && addItemIfPresent(mr, mods, stack(r, "getSecondaryInput"))
                    && BotaniaAdapterHelper.addItemOutput(mr, mods, stack(r, "getPrimaryOutput"))
                    && addChance(mr, mods, stack(r, "getSecondaryOutput"), integer(call(r, "getSecondaryOutputChance")));
        }
    }

    static final class PhytogenicInsolatorAdapter extends RecipeListAdapter {
        PhytogenicInsolatorAdapter() { super("phytogenic_insolator"); }
        Object[] recipes() throws Exception { return staticArray("cofh.thermalexpansion.util.managers.machine.InsolatorManager", "getRecipeList"); }
        boolean addRecipe(MachineRecipe mr, List<RecipeModifier> mods, Object r) throws Exception {
            if (!addItemInput(mr, mods, stack(r, "getPrimaryInput")) || !BotaniaAdapterHelper.addItemOutput(mr, mods, stack(r, "getPrimaryOutput"))) return false;
            if (bool(call(r, "hasFertilizer")) && !addItemInput(mr, mods, stack(r, "getSecondaryInput"))) return false;
            addChance(mr, mods, stack(r, "getSecondaryOutput"), integer(call(r, "getSecondaryOutputChance")));
            int water = integer(call(r, "getWater"));
            if (water > 0) BotaniaAdapterHelper.addFluid(mr, mods, IOType.INPUT, new FluidStack(FluidRegistry.WATER, water));
            return true;
        }
    }

    static final class MagmaCrucibleAdapter extends RecipeListAdapter {
        MagmaCrucibleAdapter() { super("magma_crucible"); }
        Object[] recipes() throws Exception { return staticArray("cofh.thermalexpansion.util.managers.machine.CrucibleManager", "getRecipeList"); }
        boolean addRecipe(MachineRecipe mr, List<RecipeModifier> mods, Object r) throws Exception {
            return addItemInput(mr, mods, stack(r, "getInput")) && BotaniaAdapterHelper.addFluid(mr, mods, IOType.OUTPUT, fluid(r, "getOutput"));
        }
    }

    static final class FractionatingStillAdapter extends RecipeListAdapter {
        FractionatingStillAdapter() { super("fractionating_still"); }
        Object[] recipes() throws Exception { return staticArray("cofh.thermalexpansion.util.managers.machine.RefineryManager", "getRecipeList"); }
        boolean addRecipe(MachineRecipe mr, List<RecipeModifier> mods, Object r) throws Exception {
            return BotaniaAdapterHelper.addFluid(mr, mods, IOType.INPUT, fluid(r, "getInput"))
                    && BotaniaAdapterHelper.addFluid(mr, mods, IOType.OUTPUT, fluid(r, "getOutputFluid"))
                    && addChance(mr, mods, stack(r, "getOutputItem"), integer(call(r, "getChance")));
        }
    }

    static final class EnergeticInfuserAdapter extends RecipeListAdapter {
        EnergeticInfuserAdapter() { super("energetic_infuser"); }
        Object[] recipes() throws Exception { return staticArray("cofh.thermalexpansion.util.managers.machine.ChargerManager", "getRecipeList"); }
        boolean addRecipe(MachineRecipe mr, List<RecipeModifier> mods, Object r) throws Exception {
            return addItemInput(mr, mods, stack(r, "getInput")) && BotaniaAdapterHelper.addItemOutput(mr, mods, stack(r, "getOutput"));
        }
    }

    static final class FluidTransposerAdapter extends RecipeListAdapter {
        private final boolean fill;
        FluidTransposerAdapter(String name, boolean fill) { super(name); this.fill = fill; }
        Object[] recipes() throws Exception { return staticArray("cofh.thermalexpansion.util.managers.machine.TransposerManager", fill ? "getFillRecipeList" : "getExtractRecipeList"); }
        boolean addRecipe(MachineRecipe mr, List<RecipeModifier> mods, Object r) throws Exception {
            if (!addItemInput(mr, mods, stack(r, "getInput"))) return false;
            if (fill && !BotaniaAdapterHelper.addFluid(mr, mods, IOType.INPUT, fluid(r, "getFluid"))) return false;
            if (!fill && !BotaniaAdapterHelper.addFluid(mr, mods, IOType.OUTPUT, fluid(r, "getFluid"))) return false;
            int chance = integer(call(r, "getChance"));
            return addChance(mr, mods, stack(r, "getOutput"), chance <= 0 ? 100 : chance);
        }
    }

    static final class EnchanterAdapter extends RecipeListAdapter {
        EnchanterAdapter() { super("enchanter"); }
        Object[] recipes() throws Exception { return staticArray("cofh.thermalexpansion.util.managers.machine.EnchanterManager", "getRecipeList"); }
        boolean addRecipe(MachineRecipe mr, List<RecipeModifier> mods, Object r) throws Exception {
            BotaniaAdapterHelper.addTooltip(mr, new ArrayList<String>(), "Enchantment: " + call(r, "getEnchantName"));
            return addItemInput(mr, mods, stack(r, "getPrimaryInput")) && addItemInput(mr, mods, stack(r, "getSecondaryInput"))
                    && BotaniaAdapterHelper.addItemOutput(mr, mods, stack(r, "getOutput"));
        }
    }

    static final class CentrifugalSeparatorAdapter extends RecipeListAdapter {
        private final boolean mobs;
        CentrifugalSeparatorAdapter(String name, boolean mobs) { super(name); this.mobs = mobs; }
        Object[] recipes() throws Exception { return staticArray("cofh.thermalexpansion.util.managers.machine.CentrifugeManager", mobs ? "getRecipeListMobs" : "getRecipeList"); }
        boolean addRecipe(MachineRecipe mr, List<RecipeModifier> mods, Object r) throws Exception {
            if (!addItemInput(mr, mods, stack(r, "getInput"))) return false;
            List<?> outputs = (List<?>) call(r, "getOutput");
            List<?> chances = (List<?>) call(r, "getChance");
            for (int i = 0; i < outputs.size(); i++) {
                int chance = chances != null && i < chances.size() ? integer(chances.get(i)) : 100;
                addChance(mr, mods, (ItemStack) outputs.get(i), chance);
            }
            BotaniaAdapterHelper.addFluidIfPresent(mr, mods, IOType.OUTPUT, fluid(r, "getFluid"));
            return true;
        }
    }

    static final class AlchemicalImbuerAdapter extends RecipeListAdapter {
        AlchemicalImbuerAdapter() { super("alchemical_imbuer"); }
        Object[] recipes() throws Exception { return staticArray("cofh.thermalexpansion.util.managers.machine.BrewerManager", "getRecipeList"); }
        boolean addRecipe(MachineRecipe mr, List<RecipeModifier> mods, Object r) throws Exception {
            return addItemInput(mr, mods, stack(r, "getInput")) && BotaniaAdapterHelper.addFluid(mr, mods, IOType.INPUT, fluid(r, "getInputFluid"))
                    && BotaniaAdapterHelper.addFluid(mr, mods, IOType.OUTPUT, fluid(r, "getOutputFluid"));
        }
    }

    static final class IgneousExtruderAdapter extends RecipeListAdapter {
        private final boolean sedimentary;
        IgneousExtruderAdapter(String name, boolean sedimentary) { super(name); this.sedimentary = sedimentary; }
        Object[] recipes() throws Exception { return staticArray("cofh.thermalexpansion.util.managers.machine.ExtruderManager", "getRecipeList", new Class<?>[]{Boolean.TYPE}, sedimentary); }
        boolean addRecipe(MachineRecipe mr, List<RecipeModifier> mods, Object r) throws Exception {
            return BotaniaAdapterHelper.addFluid(mr, mods, IOType.INPUT, fluid(r, "getInputHot"))
                    && BotaniaAdapterHelper.addFluid(mr, mods, IOType.INPUT, fluid(r, "getInputCold"))
                    && BotaniaAdapterHelper.addItemOutput(mr, mods, stack(r, "getOutput"));
        }
    }

    static final class CompactorAdapter extends RecipeListAdapter {
        private final String modeName;
        CompactorAdapter(String name, String modeName) { super(name); this.modeName = modeName; }
        Object[] recipes() throws Exception {
            Class<?> modeClass = Class.forName("cofh.thermalexpansion.util.managers.machine.CompactorManager$Mode");
            Object mode = Enum.valueOf((Class<Enum>) modeClass, modeName);
            return staticArray("cofh.thermalexpansion.util.managers.machine.CompactorManager", "getRecipeList", new Class<?>[]{modeClass}, mode);
        }
        boolean addRecipe(MachineRecipe mr, List<RecipeModifier> mods, Object r) throws Exception {
            return addItemInput(mr, mods, stack(r, "getInput")) && BotaniaAdapterHelper.addItemOutput(mr, mods, stack(r, "getOutput"));
        }
    }

    static final class RedstoneFurnaceAdapter extends RecipeListAdapter {
        private final boolean pyrolysis;
        RedstoneFurnaceAdapter(String name, boolean pyrolysis) { super(name); this.pyrolysis = pyrolysis; }
        Object[] recipes() throws Exception { return staticArray("cofh.thermalexpansion.util.managers.machine.FurnaceManager", "getRecipeList", new Class<?>[]{Boolean.TYPE}, pyrolysis); }
        boolean addRecipe(MachineRecipe mr, List<RecipeModifier> mods, Object r) throws Exception {
            if (!addItemInput(mr, mods, stack(r, "getInput")) || !BotaniaAdapterHelper.addItemOutput(mr, mods, stack(r, "getOutput"))) return false;
            int creosote = integer(call(r, "getCreosote"));
            if (creosote > 0 && FluidRegistry.getFluid("creosote") != null) BotaniaAdapterHelper.addFluid(mr, mods, IOType.OUTPUT, new FluidStack(FluidRegistry.getFluid("creosote"), creosote));
            return true;
        }
    }

    static final class GlacialPrecipitatorAdapter extends RecipeListAdapter {
        GlacialPrecipitatorAdapter() { super("glacial_precipitator"); }
        Object[] recipes() throws Exception { return staticArray("cofh.thermalexpansion.util.managers.machine.PrecipitatorManager", "getRecipeList"); }
        boolean addRecipe(MachineRecipe mr, List<RecipeModifier> mods, Object r) throws Exception {
            return BotaniaAdapterHelper.addFluid(mr, mods, IOType.INPUT, fluid(r, "getInput")) && BotaniaAdapterHelper.addItemOutput(mr, mods, stack(r, "getOutput"));
        }
    }

    private static boolean addItemInput(MachineRecipe machineRecipe, List<RecipeModifier> modifiers, ItemStack stack) {
        return stack != null && !BotaniaAdapterHelper.isStackEmpty(stack)
                && BotaniaAdapterHelper.addItemInput(machineRecipe, modifiers, stack, BotaniaAdapterHelper.getStackCount(stack));
    }

    private static boolean addItemIfPresent(MachineRecipe machineRecipe, List<RecipeModifier> modifiers, ItemStack stack) {
        return stack == null || BotaniaAdapterHelper.isStackEmpty(stack) || addItemInput(machineRecipe, modifiers, stack);
    }

    private static boolean addChance(MachineRecipe machineRecipe, List<RecipeModifier> modifiers, ItemStack stack, int chancePercent) {
        if (stack == null || BotaniaAdapterHelper.isStackEmpty(stack)) return true;
        if (chancePercent <= 0 || chancePercent >= 100) return BotaniaAdapterHelper.addItemOutput(machineRecipe, modifiers, stack);
        BotaniaAdapterHelper.addChancedItemOutput(machineRecipe, modifiers, stack, Math.max(0, chancePercent) / 100.0f);
        return true;
    }

    private static Object[] staticArray(String className, String methodName) throws Exception {
        return staticArray(className, methodName, new Class<?>[0]);
    }

    private static Object[] staticArray(String className, String methodName, Class<?>[] types, Object... args) throws Exception {
        Object value = Class.forName(className).getMethod(methodName, types).invoke(null, args);
        if (value == null) return new Object[0];
        if (value instanceof Collection) return ((Collection<?>) value).toArray();
        return (Object[]) value;
    }

    private static Object call(Object target, String name) throws Exception {
        Method method = target.getClass().getMethod(name);
        return method.invoke(target);
    }

    private static ItemStack stack(Object target, String name) throws Exception {
        return (ItemStack) call(target, name);
    }

    private static FluidStack fluid(Object target, String name) throws Exception {
        return (FluidStack) call(target, name);
    }

    private static int integer(Object value) {
        return value instanceof Number ? ((Number) value).intValue() : 0;
    }

    private static boolean bool(Object value) {
        return value instanceof Boolean && (Boolean) value;
    }
}
