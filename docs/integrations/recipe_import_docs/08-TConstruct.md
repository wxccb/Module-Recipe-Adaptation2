# Tinkers' Construct / 匠魂

## Adapter IDs

- `tconstruct:melting`：冶炼炉熔炼
- `tconstruct:alloying`：合金
- `tconstruct:table_casting`：浇铸台
- `tconstruct:basin_casting`：浇铸盆
- `tconstruct:drying`：晾干架

## 示例

```zenscript
import mods.modularmachinery.RecipeAdapterBuilder;
import mods.modularmachinery.RecipeModifierBuilder;

RecipeAdapterBuilder.create("mm_smeltery", "tconstruct:melting")
    .addModifier(RecipeModifierBuilder.create("modularmachinery:fluid", "output", 2.0, 1, false).build())
    .build();

RecipeAdapterBuilder.create("mm_casting_basin", "tconstruct:basin_casting").build();
```

## 说明

读取 `TinkerRegistry` 的最终熔炼、合金、浇铸和晾干配方表。浇铸配方会尽量导入 cast 输入、流体输入和物品输出。