# Immersive Engineering / 沉浸工程

## Adapter IDs

- `immersiveengineering:crusher`：粉碎机
- `immersiveengineering:arc_furnace`：电弧炉
- `immersiveengineering:alloy_kiln`：合金窑
- `immersiveengineering:bottling_machine`：灌装机
- `immersiveengineering:squeezer`：压榨机
- `immersiveengineering:fermenter`：发酵机
- `immersiveengineering:metal_press`：金属冲压机
- `immersiveengineering:mixer`：搅拌机
- `immersiveengineering:refinery`：精炼厂
- `immersiveengineering:blast_furnace`：高炉
- `immersiveengineering:coke_oven`：焦炉

## 示例

```zenscript
import mods.modularmachinery.RecipeAdapterBuilder;
import mods.modularmachinery.RecipeModifierBuilder;

RecipeAdapterBuilder.create("mm_ie_crusher", "immersiveengineering:crusher")
    .addModifier(RecipeModifierBuilder.create("modularmachinery:item", "output", 1.5, 1, true).build())
    .build();

RecipeAdapterBuilder.create("mm_ie_refinery", "immersiveengineering:refinery")
    .addModifier(RecipeModifierBuilder.create("modularmachinery:fluid", "input", 2.0, 1, false).build())
    .addModifier(RecipeModifierBuilder.create("modularmachinery:fluid", "output", 2.0, 1, false).build())
    .build();
```

## 说明

读取 IE API 的 `recipeList` / 多方块配方对象。粉碎机副产物按概率输出；焦炉会导出杂酚油流体输出。