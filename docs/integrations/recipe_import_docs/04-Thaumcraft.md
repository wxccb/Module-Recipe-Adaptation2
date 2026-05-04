# Thaumcraft 6 / 神秘时代

## Adapter IDs

- `thaumcraft:infusion`：注魔配方，读取 `ThaumcraftApi.getCraftingRecipes()` 中最终的 `InfusionRecipe`。

## 示例

```zenscript
import mods.modularmachinery.RecipeAdapterBuilder;
import mods.modularmachinery.RecipeModifierBuilder;

RecipeAdapterBuilder.create("mm_infusion", "thaumcraft:infusion")
    .addModifier(RecipeModifierBuilder.create("modularmagic:aspect", "input", 2.0, 1, false).build())
    .addModifier(RecipeModifierBuilder.create("modularmachinery:item", "input", 2.0, 1, false).build())
    .build();
```

## 脚本辅助

统一包内保留旧桥接脚本入口包名与 ZenClass：

```zenscript
import mods.codexmmthaumbridge.ThaumcraftAspectBridge;

val tags = ThaumcraftAspectBridge.getAspectTags(<minecraft:iron_ingot>);
val summary = ThaumcraftAspectBridge.getAspectSummary(<minecraft:iron_ingot>);
```

`mods.modularmachinery.RecipePrimer` 也保留扩展方法：

- `addItemAspectInputs(item)` / `addItemAspectInputs(item, multiplier)`
- `addItemAspectOutputs(item)` / `addItemAspectOutputs(item, multiplier)`

## 说明

注魔中心物品、周围组件、输出和源质都会导入。源质 requirement 使用当前包内的 ModularMagic/MMCE aspect requirement；如果没有加载对应 requirement，adapter 不会注册。
