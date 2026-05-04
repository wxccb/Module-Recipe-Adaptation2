package codex.mmbotaniabridge;

import crafttweaker.util.IEventHandler;
import github.kasuminova.mmce.common.event.recipe.RecipeEvent;
import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.adapter.RecipeAdapter;
import hellfirepvp.modularmachinery.common.crafting.helper.ComponentRequirement;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeManaInfusion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

final class ManaInfusionAdapter extends RecipeAdapter {
    ManaInfusionAdapter() {
        super(new ResourceLocation("botania", "mana_infusion"));
    }

    @Override
    public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                      List<RecipeModifier> modifiers,
                                                      List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                      Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                      List<String> tooltipList) {
        List<MachineRecipe> recipes = new ArrayList<>();
        for (RecipeManaInfusion recipe : BotaniaAPI.manaInfusionRecipes) {
            MachineRecipe machineRecipe = createRecipeShell(owningMachine,
                    new ResourceLocation("modularmachinery", "botania_mana_infusion_" + incId++),
                    BotaniaAdapterHelper.modifyDuration(modifiers, Math.max(40, recipe.getManaToConsume() / 50)), 0, false);
            if (!BotaniaAdapterHelper.addItemOutput(machineRecipe, modifiers, recipe.getOutput())) {
                continue;
            }
            if (!BotaniaAdapterHelper.addItemInput(machineRecipe, modifiers, recipe.getInput(), 1)) {
                continue;
            }
            BotaniaAdapterHelper.addMana(machineRecipe, modifiers, recipe.getManaToConsume(), false);
            BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, 20);
            String catalyst = recipe.isAlchemy()
                    ? "Alchemy Catalyst"
                    : recipe.isConjuration() ? "Conjuration Catalyst" : "Mana Pool";
            BotaniaAdapterHelper.addTooltip(machineRecipe, tooltipList, "§bImported Botania Mana Infusion recipe");
            BotaniaAdapterHelper.addTooltip(machineRecipe, tooltipList, "§7Catalyst: " + catalyst);
            RecipeAdapter.addAdditionalRequirements(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
            recipes.add(machineRecipe);
        }
        return recipes;
    }
}

