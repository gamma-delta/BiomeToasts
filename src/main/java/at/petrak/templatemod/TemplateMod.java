package at.petrak.templatemod;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(TemplateMod.MOD_ID)
public class TemplateMod {
    public static final String MOD_ID = "templatemod";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public TemplateMod() {
        // For things that happen in initialization
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        // For everything else
        var evBus = MinecraftForge.EVENT_BUS;
    }

    public static ResourceLocation modLoc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
