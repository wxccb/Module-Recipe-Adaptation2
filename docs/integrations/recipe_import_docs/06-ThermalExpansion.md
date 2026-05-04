# Thermal Expansion / 热力膨胀

## Adapter IDs

- `thermalexpansion:pulverizer`：磨粉机
- `thermalexpansion:sawmill`：锯木机
- `thermalexpansion:induction_smelter`：感应炉
- `thermalexpansion:phytogenic_insolator`：植物培育机
- `thermalexpansion:magma_crucible`：岩浆坩埚
- `thermalexpansion:fractionating_still`：分馏釜
- `thermalexpansion:energetic_infuser`：能量灌注机
- `thermalexpansion:fluid_transposer_fill`：流体转置机-填充
- `thermalexpansion:fluid_transposer_extract`：流体转置机-抽取
- `thermalexpansion:enchanter`：注魔机
- `thermalexpansion:centrifugal_separator`：离心分离机
- `thermalexpansion:centrifugal_separator_mobs`：离心分离机-生物掉落表
- `thermalexpansion:alchemical_imbuer`：炼药/灌注机
- `thermalexpansion:igneous_extruder_igneous`：造石机-火成岩
- `thermalexpansion:igneous_extruder_sedimentary`：造石机-沉积岩
- `thermalexpansion:compactor_all`：压实机-通用
- `thermalexpansion:compactor_plate`：压实机-板
- `thermalexpansion:compactor_coin`：压实机-币
- `thermalexpansion:compactor_gear`：压实机-齿轮
- `thermalexpansion:redstone_furnace`：红石炉
- `thermalexpansion:redstone_furnace_pyrolysis`：红石炉-热解
- `thermalexpansion:glacial_precipitator`：急冻机/冰川沉淀机

## 示例

```zenscript
import mods.modularmachinery.RecipeAdapterBuilder;
import mods.modularmachinery.RecipeModifierBuilder;

RecipeAdapterBuilder.create("mm_pulverizer", "thermalexpansion:pulverizer")
    .addModifier(RecipeModifierBuilder.create("modularmachinery:item", "output", 2.0, 1, true).build())
    .build();

RecipeAdapterBuilder.create("mm_compactor", "thermalexpansion:compactor_plate").build();
```

## 说明

读取 CoFH/TE manager 的 `getRecipeList()` 最终表。副产物 chance 为 0-100 百分比，会转换为 MM chanced output。