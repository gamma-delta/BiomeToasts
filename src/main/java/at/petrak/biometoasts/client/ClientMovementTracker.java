package at.petrak.biometoasts.client;

import at.petrak.biometoasts.BiomeToastsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientMovementTracker {
    // very classy
    @OnlyIn(Dist.CLIENT)
    private static Biome LAST_BIOME = null;

    // this is synced from the server
    @OnlyIn(Dist.CLIENT)
    private static ResourceLocation LAST_STRUCTURE = null;

    @SubscribeEvent
    public static void biomeChange(TickEvent.ClientTickEvent evt) {
        var mc = Minecraft.getInstance();
        var player = mc.player;
        var world = mc.level;
        if (player == null || world == null) {
            return;
        }

        Biome biome = world.getBiome(player.blockPosition()).value();
        var biomeID = biome.getRegistryName();
        if (LAST_BIOME == null || !LAST_BIOME.getRegistryName().equals(biomeID)) {
            LAST_BIOME = biome;
            var thumbnail = BiomeToastsMod.THUMBNAIL_MANAGER.getBiomeThumbnail(biome);
            mc.getToasts().addToast(new BiomeToast(thumbnail));
        }
    }

    @SubscribeEvent
    public static void login(ClientPlayerNetworkEvent.LoggedInEvent evt) {
        LAST_BIOME = null;
    }
}
