package codex.mmbotaniabridge;

import crafttweaker.util.IEventHandler;
import github.kasuminova.mmce.common.event.recipe.RecipeEvent;
import github.kasuminova.mmce.common.itemtype.ChancedIngredientStack;
import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.adapter.RecipeAdapter;
import hellfirepvp.modularmachinery.common.crafting.helper.ComponentRequirement;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementIngredientArray;
import hellfirepvp.modularmachinery.common.lib.RegistriesMM;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import kport.modularmagic.common.crafting.requirement.RequirementAspect;
import kport.modularmagic.common.crafting.requirement.types.ModularMagicRequirements;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IThaumcraftRecipe;
import thaumcraft.api.crafting.InfusionRecipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

final class ThaumcraftInfusionAdapter extends RecipeAdapter {
    ThaumcraftInfusionAdapter() {
        super(new ResourceLocation("thaumcraft", "infusion"));
    }

    @Override
    public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                      List<RecipeModifier> modifiers,
                                                      List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                      Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                      List<String> tooltipList) {
        List<MachineRecipe> recipes = new ArrayList<>();
        for (Map.Entry<ResourceLocation, IThaumcraftRecipe> entry : ThaumcraftApi.getCraftingRecipes().entrySet()) {
            IThaumcraftRecipe thaumcraftRecipe = entry.getValue();
            if (!(thaumcraftRecipe instanceof InfusionRecipe)) {
                continue;
            }
            InfusionRecipe recipe = (InfusionRecipe) thaumcraftRecipe;
            ItemStack output = resolveOutput(recipe.getRecipeOutput());
            if (output == null || BotaniaAdapterHelper.isStackEmpty(output)) {
                continue;
            }
            MachineRecipe machineRecipe = createRecipeShell(owningMachine,
                    new ResourceLocation("modularmachinery", "thaumcraft_infusion_" + sanitize(entry.getKey()) + "_" + incId++),
                    BotaniaAdapterHelper.modifyDuration(modifiers, Math.max(120, 80 + recipe.instability * 40)), 0, false);
            if (!BotaniaAdapterHelper.addItemOutput(machineRecipe, modifiers, output)) {
                continue;
            }
            if (!addIngredientInput(machineRecipe, modifiers, recipe.getRecipeInput())) {
                continue;
            }
            if (!addComponentInputs(machineRecipe, modifiers, recipe.getComponents())) {
                continue;
            }
            addAspects(machineRecipe, modifiers, recipe.getAspects());
            BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, Math.max(40, 40L + recipe.instability * 20L));
            BotaniaAdapterHelper.addTooltip(machineRecipe, tooltipList, "Imported Thaumcraft Infusion recipe");
            BotaniaAdapterHelper.addTooltip(machineRecipe, tooltipList, "Instability: " + recipe.instability);
            String research = recipe.getResearch();
            if (research != null && !research.isEmpty()) {
                BotaniaAdapterHelper.addTooltip(machineRecipe, tooltipList, "Research: " + research);
            }
            RecipeAdapter.addAdditionalRequirements(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
            recipes.add(machineRecipe);
        }
        return recipes;
    }

    private static boolean addComponentInputs(MachineRecipe machineRecipe, List<RecipeModifier> modifiers, NonNullList<Ingredient> components) {
        for (Ingredient component : components) {
            if (!addIngredientInput(machineRecipe, modifiers, component)) {
                return false;
            }
        }
        return true;
    }

    private static boolean addIngredientInput(MachineRecipe machineRecipe, List<RecipeModifier> modifiers, Ingredient ingredient) {
        if (ingredient == null) {
            return false;
        }
        ItemStack[] matchingStacks = ingredient.getMatchingStacks();
        List<ChancedIngredientStack> choices = new ArrayList<>();
        int modifiedAmount = Math.max(1, Math.round(RecipeModifier.applyModifiers(
                modifiers, hellfirepvp.modularmachinery.common.lib.RequirementTypesMM.REQUIREMENT_INGREDIENT_ARRAY, IOType.INPUT, 1f, false)));
        for (ItemStack matchingStack : matchingStacks) {
            if (matchingStack == null || BotaniaAdapterHelper.isStackEmpty(matchingStack)) {
                continue;
            }
            ItemStack stack = BotaniaAdapterHelper.copyStack(matchingStack);
            BotaniaAdapterHelper.setStackCount(stack, modifiedAmount);
            choices.add(new ChancedIngredientStack(stack));
        }
        if (choices.isEmpty()) {
            return false;
        }
        machineRecipe.addRequirement(new RequirementIngredientArray(choices, IOType.INPUT));
        return true;
    }

    private static void addAspects(MachineRecipe machineRecipe, List<RecipeModifier> modifiers, AspectList aspects) {
        if (aspects == null || aspects.size() <= 0) {
            return;
        }
        for (Aspect aspect : aspects.getAspects()) {
            int amount = aspects.getAmount(aspect);
            Object requirementType = RegistriesMM.REQUIREMENT_TYPE_REGISTRY.getValue(ModularMagicRequirements.KEY_REQUIREMENT_ASPECT);
            int modifiedAmount = Math.round(RecipeModifier.applyModifiers(
                    modifiers, (hellfirepvp.modularmachinery.common.crafting.requirement.type.RequirementType<?, ?>) requirementType,
                    IOType.INPUT, (float) amount, false));
            if (modifiedAmount > 0) {
                machineRecipe.addRequirement(new RequirementAspect(IOType.INPUT, modifiedAmount, aspect));
            }
        }
    }

    private static ItemStack resolveOutput(Object output) {
        if (output instanceof ItemStack) {
            return BotaniaAdapterHelper.copyStack((ItemStack) output);
        }
        return null;
    }

    private static String sanitize(ResourceLocation location) {
        return (location.func_110624_b() + "_" + location.func_110623_a()).replace(':', '_').replace('/', '_');
    }
}
