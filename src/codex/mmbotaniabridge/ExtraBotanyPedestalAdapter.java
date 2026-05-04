package codex.mmbotaniabridge;

import com.meteor.extrabotany.common.crafting.recipe.RecipePedestal;
import crafttweaker.util.IEventHandler;
import github.kasuminova.mmce.common.event.recipe.RecipeEvent;
import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.adapter.RecipeAdapter;
import hellfirepvp.modularmachinery.common.crafting.helper.ComponentRequirement;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

final class ExtraBotanyPedestalAdapter extends RecipeAdapter {
    ExtraBotanyPedestalAdapter() {
        super(new ResourceLocation("extrabotany", "pedestal"));
    }

    @Override
    public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachine,
                                                      List<RecipeModifier> modifiers,
                                                      List<ComponentRequirement<?, ?>> additionalRecipeRequirements,
                                                      Map<Class<?>, List<IEventHandler<RecipeEvent>>> recipeEventHandlers,
                                                      List<String> tooltipList) {
        List<MachineRecipe> recipes = new ArrayList<>();
        for (RecipePedestal recipe : RecipePedestal.getRecipeList()) {
            MachineRecipe machineRecipe = createRecipeShell(owningMachine,
                    new ResourceLocation("modularmachinery", "extrabotany_pedestal_" + incId++),
                    BotaniaAdapterHelper.modifyDuration(modifiers, 80), 0, false);
            if (!BotaniaAdapterHelper.addItemInput(machineRecipe, modifiers, recipe.getInput(), 1)) {
                continue;
            }
            if (!BotaniaAdapterHelper.addItemOutput(machineRecipe, modifiers, recipe.getOutput())) {
                continue;
            }
            BotaniaAdapterHelper.addEnergy(machineRecipe, modifiers, 20);
            BotaniaAdapterHelper.addTooltip(machineRecipe, tooltipList, "§bImported ExtraBotany Pedestal recipe");
            RecipeAdapter.addAdditionalRequirements(machineRecipe, additionalRecipeRequirements, recipeEventHandlers, tooltipList);
            recipes.add(machineRecipe);
        }
        return recipes;
    }
}

