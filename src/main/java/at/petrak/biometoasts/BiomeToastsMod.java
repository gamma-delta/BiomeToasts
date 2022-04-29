package at.petrak.biometoasts;

import at.petrak.biometoasts.client.BiomeToast;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(BiomeToastsMod.MOD_ID)
public class BiomeToastsMod {
    public static final String MOD_ID = "biometoasts";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public BiomeToastsMod() {
        // For things that happen in initialization
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        // For everything else
        var evBus = MinecraftForge.EVENT_BUS;

        evBus.register(BiomeToastsMod.class);
    }

    public static ResourceLocation modLoc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    // very classy
    @OnlyIn(Dist.CLIENT)
    private static ResourceLocation LAST_BIOME = null;

    @SubscribeEvent
    public static void biomeChange(TickEvent.ClientTickEvent evt) {
        var mc = Minecraft.getInstance();
        var player = mc.player;
        var world = mc.level;
        if (player == null || world == null) return;

        var biomeID = world.getBiome(player.blockPosition()).value().getRegistryName();
        if (LAST_BIOME == null || !LAST_BIOME.equals(biomeID)) {
            LAST_BIOME = biomeID;
            mc.getToasts().addToast(new BiomeToast(biomeID));
        }
    }

    @SubscribeEvent
    public static void login(ClientPlayerNetworkEvent.LoggedInEvent evt) {
        LAST_BIOME = null;
    }
}
