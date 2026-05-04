# Module - Recipe Adaptation2

> 面向 Minecraft 1.12.2 整合包的 Modular Machinery / MMCE 配方适配桥接模组。

Module - Recipe Adaptation2 可以把其它模组的机器、魔法、多输入多输出配方导入到 Modular Machinery 机器脚本中。它读取的是当前游戏运行时的最终配方状态，因此 CraftTweaker 或其它魔改脚本新增、删除、替换后的配方会按修改后的结果导入。

## 功能亮点

- **运行时导入**：导入当前整合包里实际存在的最终配方，而不是只读取模组默认配方。
- **MM/MMCE 写法友好**：适配 Modular Machinery 与 MMCE/扩展写法的机器脚本工作流。
- **概率产出支持**：源机器存在概率物品/流体产出时，会转为模块化机械的概率需求。
- **倍率调整**：导入脚本可以选择机器，并对输入、输出、概率产出做倍率调整。
- **统一模组本体**：Botania、Thaumcraft、AE2、IC2、沉浸工程、热力膨胀、林业、匠魂、Ender IO、ExtraBotany 适配集中在一个 jar 中。

## 支持联动

| 模组 | 已适配配方类型 |
| --- | --- |
| Botania | 花药台、符文祭坛、魔力灌注 |
| ExtraBotany | 基座 |
| Thaumcraft | 注魔、要素输入/输出辅助 |
| Applied Energistics 2 | 石英磨具、压印器 |
| IndustrialCraft 2 | 打粉机、压缩机、提取机、离心机及其它 IC2 机器配方管理器 |
| Immersive Engineering | 粉碎机、电弧炉、合金窑、装瓶机、榨油机、发酵机、金属冲压机、搅拌机、精炼厂、高炉、焦炉 |
| Thermal Expansion | 粉碎机、锯木机、感应炉、植物培育机、熔岩坩埚、分馏塔、充能器、流体转置机、附魔机、离心分离机、炼金灌注器、火成生成器、压缩机、红石炉、冰川沉淀机 |
| Forestry | 木工机、离心机、榨汁机、蒸馏机、发酵机、湿润机、热电子加工机 |
| Tinkers' Construct | 熔融、合金、浇铸台、浇铸盆、晾干架 |
| Ender IO | SAG 磨粉机、合金炉、切片拼接机、Vat |

## 仓库结构

```text
.
├── src/                         统一桥接模组 Java 源码
├── stubs/                       本地编译用 stub 类
├── build/                       已打包 jar 备份
├── docs/                        中文使用说明
│   ├── 配方导入说明.md
│   └── integrations/            各联动模组说明
├── tools/                       本地编译参数与历史参考文件
└── mcmod.info                   改名后的模组展示元信息
```

## 使用方式

1. 将 `build/Module-Recipe-Adaptation2-1.3.0.jar` 放入整合包 `mods` 文件夹。
2. 保留 `Modular Machinery`；其它联动模组按需安装，未安装的联动不会注册。
3. 在 Modular Machinery/MMCE 脚本中选择需要导入的机器适配器 ID。
4. 如需要，可在导入脚本中设置输入翻倍、产出翻倍、概率翻倍等倍率。
5. 查看 `docs/配方导入说明.md` 与 `docs/integrations/recipe_import_docs/` 获取详细示例和各模组适配列表。

## 给整合包作者的说明

- 桥接读取的是 **当前运行时配方状态**。
- 魔改删除的配方不会再被导入。
- 魔改新增或替换后的配方会按修改后的版本导入。
- 源机器提供概率信息时，会尽量保留概率产出。
- 所有联动均通过 `Loader.isModLoaded` 判断，一个 jar 可以用于不同模组组合的整合包。

## 构建说明

本备份仓库保留了生成当前 jar 时使用的源码和本地编译 stub。原始工作环境为 Minecraft 1.12.2 / Forge，并依赖整合包内相关模组 API。

已备份的可用 jar：

```text
build/Module-Recipe-Adaptation2-1.3.0.jar
```

后续如果要改成标准 Gradle 工程，可以从 `src/`、`stubs/` 和 `tools/javac_args_current.txt` 继续整理。

## 文档索引

| 文档 | 内容 |
| --- | --- |
| `docs/配方导入说明.md` | 总体使用方式 |
| `docs/integrations/recipe_import_docs/00-总览.md` | 适配器总览 |
| `docs/integrations/recipe_import_docs/01-Botania-ExtraBotany.md` | Botania / ExtraBotany |
| `docs/integrations/recipe_import_docs/02-AE2.md` | Applied Energistics 2 |
| `docs/integrations/recipe_import_docs/03-IC2.md` | IndustrialCraft 2 |
| `docs/integrations/recipe_import_docs/04-Thaumcraft.md` | Thaumcraft |
| `docs/integrations/recipe_import_docs/05-ImmersiveEngineering.md` | Immersive Engineering |
| `docs/integrations/recipe_import_docs/06-ThermalExpansion.md` | Thermal Expansion |
| `docs/integrations/recipe_import_docs/07-Forestry.md` | Forestry |
| `docs/integrations/recipe_import_docs/08-TConstruct.md` | Tinkers' Construct |
| `docs/integrations/recipe_import_docs/09-EnderIO.md` | Ender IO |

## 当前状态

这是一个整合包专用桥接模组的源码与本体备份仓库。当前重点是保存源码、本体 jar 和使用说明，方便恢复、迁移或继续整理为标准开发工程。