package at.petrak.biometoasts.data;

import at.petrak.biometoasts.client.ThumbnailIcon;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public abstract sealed class Thumbnail {
    public final ResourceLocation id;
    public final ThumbnailIcon icon;
    public final @Nullable String postTranslationKey;

    public abstract String getTranslationKey();

    public Thumbnail(ResourceLocation id, ThumbnailIcon icon, @Nullable String postTranslationKey) {
        this.id = id;
        this.icon = icon;
        this.postTranslationKey = postTranslationKey;
    }

    public static final class Biome extends Thumbnail {
        public Biome(ResourceLocation id, ThumbnailIcon icon, @Nullable String postTranslationKey) {
            super(id, icon, postTranslationKey);
        }

        @Override
        public String getTranslationKey() {
            return "biome." + id.getNamespace() + "." + id.getPath();
        }
    }

    public static final class Structure extends Thumbnail {
        public final String nameKey;

        public Structure(ResourceLocation id, ThumbnailIcon icon, String nameKey, @Nullable String postTranslationKey) {
            super(id, icon, postTranslationKey);
            this.nameKey = nameKey;
        }

        @Override
        public String getTranslationKey() {
            return nameKey;
        }
    }
}
