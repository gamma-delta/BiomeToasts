package at.petrak.biometoasts.data;

import at.petrak.biometoasts.BiomeToastsMod;
import at.petrak.biometoasts.client.BiomeThumbnail;
import at.petrak.biometoasts.client.icon.ThumbnailIcon;
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
import java.util.Map;

import static at.petrak.biometoasts.BiomeToastsMod.modLoc;

public class BiomeThumbnailManager extends SimplePreparableReloadListener<Map<ResourceLocation, BiomeThumbnail>> {
    private static final ResourceLocation FILE_LOCATION = modLoc("thumbnails.json");

    private final Map<ResourceLocation, BiomeThumbnail> thumbnails = new HashMap<>();

    public BiomeThumbnailManager() {
    }

    public BiomeThumbnail getThumbnail(Biome biome) {
        var id = biome.getRegistryName();

        if (this.thumbnails.containsKey(id)) {
            return this.thumbnails.get(id);
        } else {
            BiomeToastsMod.LOGGER.warn("Tried to get a thumbnail for {} but one wasn't supplied", id);
            return new BiomeThumbnail(id, new ThumbnailIcon.Blank());
        }
    }

    @Override
    protected Map<ResourceLocation, BiomeThumbnail> prepare(ResourceManager recman,
        ProfilerFiller pProfiler) {
        var out  = new HashMap<ResourceLocation, BiomeThumbnail>();
        try {
            for (var resource : recman.getResources(FILE_LOCATION))
            {
                try (
                    var stream = resource.getInputStream();
                    var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)
                    ) {
                    try {
                        var json = GsonHelper.parse(reader);

                        var thumbnails = GsonHelper.getAsJsonObject(json, "thumbnails");
                        var thumbResLocs = thumbnails.keySet();
                        for (var reslocStr : thumbResLocs) {
                            var key = new ResourceLocation(reslocStr);
                            var thumb = deserializeThumbnail(thumbnails.get(reslocStr), key, reslocStr);
                            out.put(key, thumb);
                        }
                    } catch (RuntimeException exn) {
                        BiomeToastsMod.LOGGER.warn("Invalid biometoasts:thumbnails.json in resourcepack: '{}'", resource.getSourceName(), exn);
                    }
                }
            }
        } catch (IOException e) {
        }

        return out;
    }

    private static BiomeThumbnail deserializeThumbnail(JsonElement json, ResourceLocation key, String realKey) {
        ThumbnailIcon icon;
        if (GsonHelper.isStringValue(json)) {
            var s = json.getAsString();
            icon = ThumbnailIcon.read(s);
        } else {
            var jobj = GsonHelper.convertToJsonObject(json, realKey);
            var s = GsonHelper.getAsString(jobj, "icon");
            icon = ThumbnailIcon.read(s);
        }
        return new BiomeThumbnail(key, icon);
    }

    @Override
    protected void apply(Map<ResourceLocation, BiomeThumbnail> map, ResourceManager recman,
        ProfilerFiller pProfiler) {
        this.thumbnails.clear();
        this.thumbnails.putAll(map);
    }
}
