# Botania / ExtraBotany

## Adapter IDs

- `botania:petal_apothecary`：花药台，读取 `BotaniaAPI.petalRecipes`。
- `botania:rune_altar`：符文祭坛，读取 `BotaniaAPI.runeAltarRecipes`。
- `botania:mana_infusion`：魔力注入/魔力池，读取 `BotaniaAPI.manaInfusionRecipes`。
- `extrabotany:pedestal`：额外植物学基座，读取 ExtraBotany 运行时配方表。

## 示例

```zenscript
import mods.modularmachinery.RecipeAdapterBuilder;
import mods.modularmachinery.RecipeModifierBuilder;

RecipeAdapterBuilder.create("mm_rune_altar", "botania:rune_altar")
    .addModifier(RecipeModifierBuilder.create("modularmagic:mana", "input", 2.0, 1, false).build())
    .build();

RecipeAdapterBuilder.create("mm_petal", "botania:petal_apothecary").build();
```

## 说明

这些表会反映 CraftTweaker/ModTweaker 在最终运行时对 Botania 配方的增删改。