package at.petrak.biometoasts.client.icon;

import at.petrak.biometoasts.BiomeToastsMod;
import at.petrak.biometoasts.client.RenderHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public sealed interface ThumbnailIcon {
    void draw(PoseStack ps, ToastComponent toast, long timeSinceLastVisible);

    record Item(ItemStack stack) implements ThumbnailIcon {
        @Override
        public void draw(PoseStack ps, ToastComponent toast, long timeSinceLastVisible) {
            RenderHelper.renderItemStackInGui(ps, this.stack, 0, 0);
        }
    }

    record File(ResourceLocation path) implements ThumbnailIcon {
        @Override
        public void draw(PoseStack ps, ToastComponent toast, long timeSinceLastVisible) {
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            RenderSystem.setShaderTexture(0, this.path);
            GuiComponent.blit(ps, 0, 0, 0, 0, 16, 16, 16, 16);
        }
    }

    record Blank() implements ThumbnailIcon {
        @Override
        public void draw(PoseStack ps, ToastComponent toast, long timeSinceLastVisible) {

        }
    }

    static ThumbnailIcon read(String s) {
        if (s.endsWith(".png")) {
            return new File(new ResourceLocation(s));
        } else {
            try {
                var input = ItemArgument.item().parse(new StringReader(s));
                // the argument name `pAllowOversizedStacks` is just straight up not true, it's flipped
                var stack = input.createItemStack(1, true);
                return new Item(stack);
            } catch (CommandSyntaxException e) {
                BiomeToastsMod.LOGGER.warn("Invalid itemstack string: {}", e.getMessage());
                return new Blank();
            }
        }
    }
}
