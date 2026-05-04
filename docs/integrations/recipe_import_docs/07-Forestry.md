# Forestry / 林业

## Adapter IDs

- `forestry:carpenter`：木工机
- `forestry:centrifuge`：离心机
- `forestry:squeezer`：压榨机
- `forestry:still`：蒸馏器
- `forestry:fermenter`：发酵机
- `forestry:moistener`：湿润机
- `forestry:fabricator`：电子管/热电子加工台

## 示例

```zenscript
import mods.modularmachinery.RecipeAdapterBuilder;
import mods.modularmachinery.RecipeModifierBuilder;

RecipeAdapterBuilder.create("mm_forestry_centrifuge", "forestry:centrifuge")
    .addModifier(RecipeModifierBuilder.create("modularmachinery:item", "output", 2.0, 1, true).build())
    .build();
```

## 说明

读取 `forestry.api.recipes.RecipeManagers` 的各 manager 最终表。离心机产物、压榨机残渣按概率输出。