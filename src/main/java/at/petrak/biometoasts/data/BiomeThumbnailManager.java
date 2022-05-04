package at.petrak.biometoasts.data;

import at.petrak.biometoasts.BiomeToastsMod;
import at.petrak.biometoasts.client.ThumbnailIcon;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.biome.Biome;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static at.petrak.biometoasts.BiomeToastsMod.modLoc;

public class BiomeThumbnailManager extends SimplePreparableReloadListener<ThumbnailManifest> {
    private static final ResourceLocation FILE_LOCATION = modLoc("thumbnails.json");

    private ThumbnailManifest MANIFEST = null;

    public BiomeThumbnailManager() {
    }

    public Thumbnail.Biome getBiomeThumbnail(Biome biome) {
        var id = biome.getRegistryName();

        if (MANIFEST.biomeIcons().containsKey(id)) {
            return this.MANIFEST.biomeIcons().get(id);
        } else {
            BiomeToastsMod.LOGGER.warn("Tried to get a thumbnail for {} but one wasn't supplied", id);
            return new Thumbnail.Biome(id, new ThumbnailIcon.Blank(), null);
        }
    }

    @Override
    protected ThumbnailManifest prepare(ResourceManager recman, ProfilerFiller pProfiler) {
        var biomeIcons = new HashMap<ResourceLocation, Thumbnail.Biome>();
        var structureIcons = new HashMap<ResourceLocation, Thumbnail.Structure>();

        var showBiomeIcons = true;
        var showStructureIcons = true;

        try {
            for (var resource : recman.getResources(FILE_LOCATION)) {
                try (
                    var stream = resource.getInputStream();
                    var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)
                ) {
                    try {
                        var json = GsonHelper.parse(reader);

                        var biomes = GsonHelper.getAsJsonObject(json, "biomes", null);
                        if (biomes != null) {
                            var biomeResLocs = biomes.keySet();
                            for (var reslocStr : biomeResLocs) {
                                var key = new ResourceLocation(reslocStr);
                                var thumb = deserializeBiomeThumbnail(biomes.get(reslocStr), key, reslocStr);
                                biomeIcons.put(key, thumb);
                            }
                        }

                        var structures = GsonHelper.getAsJsonObject(json, "structures", null);
                        if (structures != null) {
                            var structuresResLocs = structures.keySet();
                            for (var reslocStr : structuresResLocs) {
                                var key = new ResourceLocation(reslocStr);
                                var thumb = deserializeStructureThumbnail(structures.get(reslocStr), key, reslocStr);
                                structureIcons.put(key, thumb);
                            }
                        }

                        // We just clobber the old version
                        if (json.has("showBiomeIcons")) {
                            showBiomeIcons = GsonHelper.getAsBoolean(json, "showBiomeIcons");
                        }
                        if (json.has("showStructureIcons")) {
                            showStructureIcons = GsonHelper.getAsBoolean(json, "showStructureIcons");
                        }

                    } catch (RuntimeException exn) {
                        BiomeToastsMod.LOGGER.warn("Invalid biometoasts:thumbnails.json in resourcepack: '{}'",
                            resource.getSourceName(), exn);
                    }
                }
            }
        } catch (IOException e) {
        }

        return new ThumbnailManifest(biomeIcons, structureIcons, showBiomeIcons, showStructureIcons);
    }

    private static Thumbnail.Biome deserializeBiomeThumbnail(JsonElement json, ResourceLocation key, String realKey) {
        ThumbnailIcon icon;
        String postTranslationKey = null;
        if (GsonHelper.isStringValue(json)) {
            var s = json.getAsString();
            icon = ThumbnailIcon.read(s);
        } else {
            var jobj = GsonHelper.convertToJsonObject(json, realKey);
            var s = GsonHelper.getAsString(jobj, "icon");
            postTranslationKey = GsonHelper.getAsString(jobj, "postMessage", null);
            icon = ThumbnailIcon.read(s);
        }
        return new Thumbnail.Biome(key, icon, postTranslationKey);
    }

    private static Thumbnail.Structure deserializeStructureThumbnail(JsonElement json, ResourceLocation key,
        String realKey) {
        var jobj = GsonHelper.convertToJsonObject(json, realKey);

        var s = GsonHelper.getAsString(jobj, "icon");
        ThumbnailIcon icon = ThumbnailIcon.read(s);
        var nameKey = GsonHelper.getAsString(jobj, "structure." + realKey);

        String postTranslationKey = GsonHelper.getAsString(jobj, "postMessage", null);

        return new Thumbnail.Structure(key, icon, nameKey, postTranslationKey);
    }

    @Override
    protected void apply(ThumbnailManifest map, ResourceManager recman, ProfilerFiller pProfiler) {
        MANIFEST = map;
    }
}
