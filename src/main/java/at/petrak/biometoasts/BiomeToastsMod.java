package at.petrak.biometoasts;

import at.petrak.biometoasts.client.ClientMovementTracker;
import at.petrak.biometoasts.data.BiomeThumbnailManager;
import at.petrak.biometoasts.server.ServerMovementTracker;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.resource.PathResourcePack;
import org.slf4j.Logger;

import java.io.IOException;

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

        modBus.register(BiomeToastsMod.class);

        evBus.register(ClientMovementTracker.class);
        evBus.register(ServerMovementTracker.class);
    }

    public static ResourceLocation modLoc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    @SubscribeEvent
    public static void initResourceListeners(RegisterClientReloadListenersEvent evt) {
        evt.registerReloadListener(THUMBNAIL_MANAGER);
    }

    // https://github.com/MinecraftForge/MinecraftForge/blob/1.18.x/src/test/java/net/minecraftforge/debug/AddPackFinderEventTest.java
    @SubscribeEvent
    public static void addPackFinders(AddPackFindersEvent evt) {
        try {
            if (evt.getPackType() == PackType.CLIENT_RESOURCES) {
                IModFile modFile = ModList.get().getModFileById(MOD_ID).getFile();
                var resourcePath = modFile.findResource("alticons");
                var pack = new PathResourcePack(modFile.getFileName() + ":" + resourcePath, resourcePath);
                var metadataSection = pack.getMetadataSection(PackMetadataSection.SERIALIZER);
                if (metadataSection != null) {
                    evt.addRepositorySource((packConsumer, packConstructor) ->
                        packConsumer.accept(packConstructor.create(
                            "builtin/" + MOD_ID, new TranslatableComponent("biometoasts.alticons"), false,
                            () -> pack, metadataSection, Pack.Position.TOP, PackSource.BUILT_IN, false)));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
