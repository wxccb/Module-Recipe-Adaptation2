package codex.mmbotaniabridge;

import crafttweaker.util.IEventHandler;
import github.kasuminova.mmce.common.event.recipe.RecipeEvent;
import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.adapter.RecipeAdapter;
import hellfirepvp.modularmachinery.common.crafting.helper.ComponentRequirement;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipePetals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

final class PetalApothecaryAdapter extends RecipeAdapter {
    PetalApothecaryAdapter() {
        super(new ResourceLocation("botania", "petal_apothecary"));
    }

    @Override
    public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                      List<RecipeModifier> modifiers,
                                                      List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                      Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                      List<String> tooltipList) {
        List<MachineRecipe> recipes = new ArrayList<>();
        for (RecipePetals recipe : BotaniaAPI.petalRecipes) {
            MachineRecipe machineRecipe = createRecipeShell(owningMachine,
                    new ResourceLocation("modularmachinery", "botania_petal_apothecary_" + incId++),
                    BotaniaAdapterHelper.modifyDuration(modifiers, 100), 0, false);
            if (!BotaniaAdapterHelper.addItemOutput(machineRecipe, modifiers, recipe.getOutput())) {
                continue;
            }
            boolean valid = true;
            for (Object input : recipe.getInputs()) {
                if (!BotaniaAdapterHelper.addItemInput(machineRecipe, modifiers, input, 1)) {
                    valid = false;
                    break;
                }
            }
            if (!valid) {
                continue;
            }
            BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, 20);
            BotaniaAdapterHelper.addTooltip(machineRecipe, tooltipList, "§bImported Botania Petal Apothecary recipe");
            RecipeAdapter.addAdditionalRequirements(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
            recipes.add(machineRecipe);
        }
        return recipes;
    }
}

