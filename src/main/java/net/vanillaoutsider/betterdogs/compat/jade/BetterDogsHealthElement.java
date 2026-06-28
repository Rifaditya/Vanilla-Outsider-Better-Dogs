// SPDX-License-Identifier: GPL-3.0-or-later
package net.vanillaoutsider.betterdogs.compat.jade;

import java.util.Objects;
import org.jspecify.annotations.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import snownee.jade.JadeClient;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.config.IWailaConfig;
import snownee.jade.api.ui.Element;
import snownee.jade.api.ui.IDisplayHelper;
import snownee.jade.track.HealthTrackInfo;

public class BetterDogsHealthElement extends Element {
    private Hud.HeartType heartType;
    private final float maxHealth;
    private final float health;
    private final float absorption;
    private int iconsPerLine = 10;
    private int lineCount = 1;
    private int iconCount = 1;
    private @Nullable HealthTrackInfo track;

    public BetterDogsHealthElement(Hud.HeartType heartType, float maxHealth, float health, float absorption) {
        this.heartType = heartType;
        this.maxHealth = maxHealth;
        this.health = health;
        this.absorption = absorption;
        
        IPluginConfig config = IWailaConfig.get().plugin();
        int iconCount = Mth.ceil(maxHealth) + Mth.ceil(absorption);
        
        // BETTER DOGS: Bypass the Jade text-fallback limitation!
        // We ALWAYS render hearts, even if it exceeds the configuration limit.
        int maxHeartsPerLine = Math.max(1, config.getInt(JadeIds.MC_ENTITY_HEALTH_ICONS_PER_LINE));
        iconCount = Mth.ceil(iconCount * 0.5F);
        this.iconCount = iconCount;
        this.iconsPerLine = Math.min(maxHeartsPerLine, iconCount);
        this.lineCount = Mth.ceil((float) iconCount / maxHeartsPerLine);
        
        this.width = 8 * this.iconsPerLine + 1;
        this.height = 5 + 4 * this.lineCount;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        float health = this.health * 0.5F;
        float lastHealth = health;
        float lastAbsorption = absorption;
        boolean blink = false;
        
        if (track == null && getTag() != null) {
            track = JadeClient.tickHandler().progressTracker.getOrCreate(
                    getTag(),
                    HealthTrackInfo.class,
                    () -> new HealthTrackInfo(this.health, absorption));
        }
        
        if (track != null) {
            track.setHealth(this.health, absorption);
            track.update(Minecraft.getInstance().getDeltaTracker().getRealtimeDeltaTicks());
            lastHealth = track.getLastHealth() * 0.5F;
            lastAbsorption = track.getLastAbsorption();
            blink = track.isBlinking();
        }

        IDisplayHelper helper = IDisplayHelper.get();
        int xOffset = (iconCount - 1) % iconsPerLine * 8;
        int yOffset = lineCount * 4 - 4;
        Identifier containerSprite = Hud.HeartType.CONTAINER.getSprite(false, false, blink);
        
        for (int i = iconCount; i > 0; --i) {
            int xPos = getX() + xOffset;
            int yPos = getY() + yOffset;
            helper.blitSprite(graphics, RenderPipelines.GUI_TEXTURED, containerSprite, xPos, yPos, 9, 9);

            boolean renderAbsorb = i > Mth.ceil(maxHealth * 0.5F);
            Hud.HeartType curHeart = heartType;
            float curHealth = health;
            float curLastHealth = lastHealth;
            
            if (renderAbsorb) {
                curHeart = Hud.HeartType.ABSORBING;
                curHealth = (Mth.ceil(maxHealth) + absorption) * 0.5F;
                curLastHealth = (Mth.ceil(maxHealth) + lastAbsorption) * 0.5F;
            }
            if (i <= Mth.floor(curHealth)) { // Full heart
                helper.blitSprite(graphics, RenderPipelines.GUI_TEXTURED, curHeart.getSprite(false, false, false), xPos, yPos, 9, 9);
            }

            if (i > curHealth) {
                if (i <= Mth.floor(curLastHealth)) { // Full heart (last + blink)
                    helper.blitSprite(graphics, RenderPipelines.GUI_TEXTURED, curHeart.getSprite(false, false, true), xPos, yPos, 9, 9);
                } else if ((i > curLastHealth) && (i < curLastHealth + 1)) { // Half heart (blink)
                    helper.blitSprite(graphics, RenderPipelines.GUI_TEXTURED, curHeart.getSprite(false, true, true), xPos, yPos, 9, 9);
                }
                if (i < curHealth + 1) { // Half heart
                    helper.blitSprite(graphics, RenderPipelines.GUI_TEXTURED, curHeart.getSprite(false, true, false), xPos, yPos, 9, 9);
                }
            }

            xOffset -= 8;
            if (xOffset < 0) {
                xOffset = iconsPerLine * 8 - 8;
                yOffset -= 4;
            }
        }
    }

    @Override
    public Component getNarration() {
        return Component.translatable("narration.jade.health", Mth.ceil(health));
    }
}
