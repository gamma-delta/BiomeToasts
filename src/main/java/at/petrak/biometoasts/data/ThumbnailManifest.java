package at.petrak.biometoasts.data;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record ThumbnailManifest(Map<ResourceLocation, Thumbnail.Biome> biomeIcons,
                                Map<ResourceLocation, Thumbnail.Structure> structures,
                                boolean showBiomeIcons,
                                boolean showStructureIcons) {
}
