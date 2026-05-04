package codex.mmbotaniabridge;

import hellfirepvp.modularmachinery.common.CommonProxy;
import hellfirepvp.modularmachinery.common.crafting.adapter.RecipeAdapter;
import hellfirepvp.modularmachinery.common.lib.RegistriesMM;
import kport.modularmagic.common.crafting.requirement.types.ModularMagicRequirements;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Collection;

@Mod(
        modid = MMBotaniaBridgeMod.MODID,
        name = MMBotaniaBridgeMod.NAME,
        version = MMBotaniaBridgeMod.VERSION,
        acceptedMinecraftVersions = "[1.12.2]",
        dependencies = "required-after:modularmachinery;after:botania;after:extrabotany;after:appliedenergistics2;after:ae2stuff;after:thaumcraft;after:ic2;after:immersiveengineering;after:thermalexpansion;after:forestry;after:tconstruct;after:enderio;after:gugu-utils"
)
public class MMBotaniaBridgeMod {
    public static final String MODID = "codexmmrecipebridge";
    public static final String NAME = "Module - Recipe Adaptation2";
    public static final String VERSION = "1.3.0";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (Loader.isModLoaded("botania")) {
            CommonProxy.registryPrimer.register(new PetalApothecaryAdapter());
            CommonProxy.registryPrimer.register(new RuneAltarAdapter());
            CommonProxy.registryPrimer.register(new ManaInfusionAdapter());
        }
        if (Loader.isModLoaded("thaumcraft") && hasAspectRequirement()) {
            CommonProxy.registryPrimer.register(new ThaumcraftInfusionAdapter());
        }
        if (Loader.isModLoaded("appliedenergistics2")) {
            CommonProxy.registryPrimer.register(new AE2RecipeAdapters.GrinderAdapter());
            CommonProxy.registryPrimer.register(new AE2RecipeAdapters.InscriberAdapter());
        }
        if (Loader.isModLoaded("ic2")) {
            registerAll(IC2RecipeAdapters.createAdapters());
        }
        if (Loader.isModLoaded("extrabotany")) {
            CommonProxy.registryPrimer.register(new ExtraBotanyPedestalAdapter());
        }
        if (Loader.isModLoaded("immersiveengineering")) {
            registerAll(ImmersiveEngineeringRecipeAdapters.createAdapters());
        }
        if (Loader.isModLoaded("thermalexpansion")) {
            registerAll(ThermalExpansionRecipeAdapters.createAdapters());
        }
        if (Loader.isModLoaded("forestry")) {
            registerAll(ForestryRecipeAdapters.createAdapters());
        }
        if (Loader.isModLoaded("tconstruct")) {
            registerAll(TConstructRecipeAdapters.createAdapters());
        }
        if (Loader.isModLoaded("enderio")) {
            registerAll(EnderIORecipeAdapters.createAdapters());
        }
    }

    private static boolean hasAspectRequirement() {
        return RegistriesMM.REQUIREMENT_TYPE_REGISTRY.getValue(ModularMagicRequirements.KEY_REQUIREMENT_ASPECT) != null;
    }

    private static void registerAll(Collection<RecipeAdapter> adapters) {
        for (RecipeAdapter adapter : adapters) {
            CommonProxy.registryPrimer.register(adapter);
        }
    }
}
