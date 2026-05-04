# Ender IO

## Adapter IDs

- `enderio:sag_mill`：SAG 磨粉机
- `enderio:alloy_smelter`：合金炉
- `enderio:slice_and_splice`：切片拼接机
- `enderio:vat`：发酵桶/酿造桶

## 示例

```zenscript
import mods.modularmachinery.RecipeAdapterBuilder;
import mods.modularmachinery.RecipeModifierBuilder;

RecipeAdapterBuilder.create("mm_sag_mill", "enderio:sag_mill")
    .addModifier(RecipeModifierBuilder.create("modularmachinery:item", "output", 2.0, 1, true).build())
    .build();

RecipeAdapterBuilder.create("mm_vat", "enderio:vat")
    .addModifier(RecipeModifierBuilder.create("modularmachinery:fluid", "output", 2.0, 1, false).build())
    .build();
```

## 说明

读取 Ender IO 运行时 recipe manager。SAG 磨粉机概率输出、发酵桶流体输入输出都会导入。