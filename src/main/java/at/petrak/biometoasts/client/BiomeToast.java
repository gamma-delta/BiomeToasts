package at.petrak.biometoasts.client;

import at.petrak.biometoasts.BiomeToastsMod;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public record BiomeToast(ResourceLocation biome) implements Toast {
    public static final String KEY_TITLE = BiomeToastsMod.MOD_ID + ".toast.generic";
    public static final String KEY_SUBTITLE = BiomeToastsMod.MOD_ID + ".toast.subtitle";

    @Override
    public Visibility render(PoseStack pPoseStack, ToastComponent pToastComponent, long pTimeSinceLastVisible) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        pToastComponent.blit(pPoseStack, 0, 0, 0, 0, this.width(), this.height());

        String biomeKey = "biome." + biome.getNamespace() + "." + biome.getPath();
        var subtitle = new TranslatableComponent(KEY_SUBTITLE, new TranslatableComponent(biomeKey));

        Font font = pToastComponent.getMinecraft().font;
        font.draw(pPoseStack, new TranslatableComponent(KEY_TITLE), 30.0F, 7.0F, 0xff_ffff00);
        font.draw(pPoseStack, subtitle, 30.0F, 18.0F, 0xff_ffffff);

        var mc = Minecraft.getInstance();
        var player = mc.player;
        var world = mc.level;

        var biomeID = world.getBiome(player.blockPosition()).value().getRegistryName();
        if (biomeID == null || !biomeID.equals(this.biome)) {
            return Visibility.HIDE;
        }
        return pTimeSinceLastVisible >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }
}
