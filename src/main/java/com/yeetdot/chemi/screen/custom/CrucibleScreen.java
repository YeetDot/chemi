package com.yeetdot.chemi.screen.custom;

import com.yeetdot.chemi.Chemi;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class CrucibleScreen extends HandledScreen<CrucibleScreenHandler> {
    public static final Identifier GUI_TEXTURE = Identifier.of(Chemi.MOD_ID, "textures/gui/crucible/crucible.png");
    public static final Identifier LIT_PROGRESS_TEXTURE = Identifier.of(Chemi.MOD_ID, "textures/gui/crucible/lit_progress.png");
    public static final Identifier COOK_PROGRESS_TEXTURE = Identifier.of(Chemi.MOD_ID, "textures/gui/crucible/cook_progress.png");

    public CrucibleScreen(CrucibleScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x, y, 0f, 0f, backgroundWidth, backgroundHeight, 256 , 256);
        if ((this.handler).isBurning()) {
            int l = MathHelper.ceil((this.handler).getFuelProgress() * 13.0f) + 1;
            context.drawTexture(RenderPipelines.GUI_TEXTURED, LIT_PROGRESS_TEXTURE,x + 56, y + 36 + 14 - l, 0, 14 - l, 14, l, 14, 14);
        }
        int l = MathHelper.ceil((this.handler).getCookProgress() * 24.0F);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, COOK_PROGRESS_TEXTURE, x + 79, y + 34,0, 0, l, 16, 24, 16);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
