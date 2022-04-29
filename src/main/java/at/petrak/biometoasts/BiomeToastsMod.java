package at.petrak.biometoasts;

import at.petrak.biometoasts.client.BiomeToast;
import at.petrak.biometoasts.client.MovementTracker;
import at.petrak.biometoasts.datagen.BiomeThumbnailManager;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(BiomeToastsMod.MOD_ID)
public class BiomeToastsMod {
    public static final String MOD_ID = "biometoasts";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final BiomeThumbnailManager THUMBNAIL_MANAGER = new BiomeThumbnailManager();

    public BiomeToastsMod() {
        // For things that happen in initialization
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        // For everything else
        var evBus = MinecraftForge.EVENT_BUS;

        evBus.register(BiomeToastsMod.class);
        evBus.register(MovementTracker.class);
    }

    public static ResourceLocation modLoc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    @SubscribeEvent
    public static void initResourceListeners(AddReloadListenerEvent evt) {
        evt.addListener(THUMBNAIL_MANAGER);
    }
}
