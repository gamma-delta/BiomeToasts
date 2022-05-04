package at.petrak.biometoasts.client;

import at.petrak.biometoasts.BiomeToastsMod;
import at.petrak.biometoasts.data.Thumbnail;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TranslatableComponent;

public record BiomeToast(Thumbnail thumbnail) implements Toast {
    public static final String KEY_TITLE = BiomeToastsMod.MOD_ID + ".toast.generic";
    public static final String KEY_SUBTITLE = BiomeToastsMod.MOD_ID + ".toast.subtitle";

    @Override
    public Visibility render(PoseStack ps, ToastComponent pToastComponent, long pTimeSinceLastVisible) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        pToastComponent.blit(ps, 0, 0, 0, 0, this.width(), this.height());

        ps.pushPose();
        ps.translate(8, 8, 1);
        thumbnail.icon.draw(ps, pToastComponent, pTimeSinceLastVisible);
        ps.popPose();

        var transKey = thumbnail.getTranslationKey();
        var subtitle = new TranslatableComponent(KEY_SUBTITLE, new TranslatableComponent(transKey));

        Font font = pToastComponent.getMinecraft().font;
        font.draw(ps, new TranslatableComponent(KEY_TITLE), 30.0F, 7.0F, 0xff_ffff00);
        font.draw(ps, subtitle, 30.0F, 18.0F, 0xff_ffffff);

        /*
        var mc = Minecraft.getInstance();
        var player = mc.player;
        var world = mc.level;
        var biomeID = world.getBiome(player.blockPosition()).value().getRegistryName();
        if (biomeID == null || !biomeID.equals(thumbnail.biomeName())) {
            return Visibility.HIDE;
        }
        */

        return pTimeSinceLastVisible >= 3000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }
}
