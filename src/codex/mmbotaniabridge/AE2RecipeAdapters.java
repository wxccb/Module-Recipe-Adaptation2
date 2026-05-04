package codex.mmbotaniabridge;

import crafttweaker.util.IEventHandler;
import github.kasuminova.mmce.common.event.recipe.RecipeEvent;
import github.kasuminova.mmce.common.itemtype.ChancedIngredientStack;
import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.adapter.RecipeAdapter;
import hellfirepvp.modularmachinery.common.crafting.helper.ComponentRequirement;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementCatalyst;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementIngredientArray;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementItem;
import hellfirepvp.modularmachinery.common.lib.RequirementTypesMM;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import appeng.api.AEApi;
import appeng.api.features.IGrinderRecipe;
import appeng.api.features.IInscriberRecipe;
import appeng.api.features.InscriberProcessType;

final class AE2RecipeAdapters {
    private AE2RecipeAdapters() {
    }

    static final class GrinderAdapter extends RecipeAdapter {
        GrinderAdapter() {
            super(new ResourceLocation("appliedenergistics2", "grinder"));
        }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            Collection<IGrinderRecipe> ae2Recipes = AEApi.instance().registries().grinder().getRecipes();
            for (IGrinderRecipe recipe : ae2Recipes) {
                MachineRecipe machineRecipe = createRecipeShell(owningMachine,
                        new ResourceLocation("modularmachinery", "ae2_grinder_" + incId++),
                        BotaniaAdapterHelper.modifyDuration(modifiers, Math.max(40, recipe.getRequiredTurns() * 40)),
                        0, false);
                if (!BotaniaAdapterHelper.addItemInput(machineRecipe, modifiers, recipe.getInput(), 1)) {
                    continue;
                }
                if (!BotaniaAdapterHelper.addItemOutput(machineRecipe, modifiers, recipe.getOutput())) {
                    continue;
                }
                addOptionalOutput(machineRecipe, modifiers, recipe.getOptionalOutput().orElse(null), recipe.getOptionalChance());
                addOptionalOutput(machineRecipe, modifiers, recipe.getSecondOptionalOutput().orElse(null), recipe.getSecondOptionalChance());
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, Math.max(20, recipe.getRequiredTurns() * 40L));
                BotaniaAdapterHelper.addTooltip(machineRecipe, tooltipList, "Imported AE2 Grinder recipe");
                RecipeAdapter.addAdditionalRequirements(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    static final class InscriberAdapter extends RecipeAdapter {
        InscriberAdapter() {
            super(new ResourceLocation("appliedenergistics2", "inscriber"));
        }

        @Override
        public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                          List<RecipeModifier> modifiers,
                                                          List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                          Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                          List<String> tooltipList) {
            List<MachineRecipe> recipes = new ArrayList<>();
            Collection<IInscriberRecipe> ae2Recipes = AEApi.instance().registries().inscriber().getRecipes();
            for (IInscriberRecipe recipe : ae2Recipes) {
                MachineRecipe machineRecipe = createRecipeShell(owningMachine,
                        new ResourceLocation("modularmachinery", "ae2_inscriber_" + incId++),
                        BotaniaAdapterHelper.modifyDuration(modifiers, recipe.getProcessType() == InscriberProcessType.PRESS ? 80 : 120),
                        0, false);
                if (!BotaniaAdapterHelper.addItemOutput(machineRecipe, modifiers, recipe.getOutput())) {
                    continue;
                }
                if (!addInscriberInput(machineRecipe, modifiers, recipe.getInputs())) {
                    continue;
                }
                addOptionalInscriberChoices(machineRecipe, recipe.getTopInputs(), recipe.getTopOptional().orElse(null));
                addOptionalInscriberChoices(machineRecipe, recipe.getBottomInputs(), recipe.getBottomOptional().orElse(null));
                BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, 20);
                BotaniaAdapterHelper.addTooltip(machineRecipe, tooltipList, "Imported AE2 Inscriber recipe");
                BotaniaAdapterHelper.addTooltip(machineRecipe, tooltipList, "Type: " + recipe.getProcessType().name());
                RecipeAdapter.addAdditionalRequirements(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
                recipes.add(machineRecipe);
            }
            return recipes;
        }
    }

    private static boolean addInscriberInput(MachineRecipe machineRecipe, List<RecipeModifier> modifiers, Collection<ItemStack> inputs) {
        List<ItemStack> inputCandidates = new ArrayList<>();
        for (ItemStack stack : inputs) {
            if (stack != null) {
                inputCandidates.add(stack);
            }
        }
        if (inputCandidates.isEmpty()) {
            return false;
        }
        if (inputCandidates.size() == 1) {
            return BotaniaAdapterHelper.addItemInput(machineRecipe, modifiers, inputCandidates.get(0), 1);
        }
        List<ChancedIngredientStack> requiredStacks = new ArrayList<>();
        for (ItemStack stack : inputCandidates) {
            requiredStacks.add(new ChancedIngredientStack(stack));
        }
        machineRecipe.addRequirement(new RequirementIngredientArray(requiredStacks, IOType.INPUT));
        return true;
    }

    private static void addOptionalInscriberChoices(MachineRecipe machineRecipe, Collection<ItemStack> stackList, ItemStack optionalStack) {
        List<ChancedIngredientStack> choices = new ArrayList<>();
        if (stackList != null) {
            for (ItemStack stack : stackList) {
                if (stack != null) {
                    choices.add(new ChancedIngredientStack(stack));
                }
            }
        }
        if (choices.isEmpty() && optionalStack != null) {
            choices.add(new ChancedIngredientStack(optionalStack));
        }
        if (!choices.isEmpty()) {
            machineRecipe.addRequirement(new RequirementCatalyst(choices));
        }
    }

    private static void addOptionalOutput(MachineRecipe machineRecipe, List<RecipeModifier> modifiers, ItemStack output, float chance) {
        if (output == null) {
            return;
        }
        ItemStack stack = copyStack(output);
        int modifiedAmount = Math.round(RecipeModifier.applyModifiers(
                modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.OUTPUT, (float) stack.getCount(), false));
        float modifiedChance = (float) RecipeModifier.applyModifiers(
                modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.OUTPUT, chance, true);
        if (modifiedAmount <= 0 || modifiedChance <= 0f) {
            return;
        }
        stack.setCount(modifiedAmount);
        RequirementItem requirementItem = new RequirementItem(IOType.OUTPUT, stack);
        requirementItem.setChance(modifiedChance);
        machineRecipe.addRequirement(requirementItem);
    }

    private static ItemStack copyStack(ItemStack stack) {
        return stack.copy();
    }
}
