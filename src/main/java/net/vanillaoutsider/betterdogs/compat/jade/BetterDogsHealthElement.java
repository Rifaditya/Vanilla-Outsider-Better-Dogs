// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.compat.jade;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.Identifiers;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.config.IWailaConfig;
import snownee.jade.api.ui.Element;

public class BetterDogsHealthElement extends Element {
    public enum HeartType {
        NORMAL,
        FROZEN
    }

    private final HeartType heartType;
    private final float maxHealth;
    private final float health;
    private final float absorption;
    private int iconsPerLine = 10;
    private int lineCount = 1;
    private int iconCount = 1;

    public BetterDogsHealthElement(HeartType heartType, float maxHealth, float health, float absorption) {
        this.heartType = heartType;
        this.maxHealth = maxHealth;
        this.health = health;
        this.absorption = absorption;

        IPluginConfig config = IWailaConfig.get().getPlugin();
        int totalHearts = Mth.ceil(maxHealth) + Mth.ceil(absorption);

        // BETTER DOGS: Bypass the Jade text-fallback limitation!
        // We ALWAYS render hearts, even if it exceeds the configuration limit.
        int maxHeartsPerLine = Math.max(1, config.getInt(Identifiers.MC_ENTITY_HEALTH_ICONS_PER_LINE));
        totalHearts = Mth.ceil(totalHearts * 0.5F);
        this.iconCount = totalHearts;
        this.iconsPerLine = Math.min(maxHeartsPerLine, totalHearts);
        this.lineCount = Mth.ceil((float) totalHearts / maxHeartsPerLine);
    }

    private static final net.minecraft.resources.ResourceLocation ICONS_LOCATION = new net.minecraft.resources.ResourceLocation("minecraft", "textures/gui/icons.png");

    @Override
    public Vec2 getSize() {
        return new Vec2(8 * this.iconsPerLine + 1, 5 + 4 * this.lineCount);
    }

    @Override
    public void render(GuiGraphics graphics, float x, float y, float maxX, float maxY) {
        float h = this.health * 0.5F;
        int xOffset = (iconCount - 1) % iconsPerLine * 8;
        int yOffset = lineCount * 4 - 4;

        for (int i = iconCount; i > 0; --i) {
            int xPos = (int) x + xOffset;
            int yPos = (int) y + yOffset;

            // Container (empty heart at u=16, v=0)
            graphics.blit(ICONS_LOCATION, xPos, yPos, 16, 0, 9, 9);

            boolean renderAbsorb = i > Mth.ceil(maxHealth * 0.5F);
            float curHealth = h;
            int fullU = heartType == HeartType.FROZEN ? 178 : 52;
            int halfU = heartType == HeartType.FROZEN ? 187 : 61;

            if (renderAbsorb) {
                curHealth = (Mth.ceil(maxHealth) + absorption) * 0.5F;
                fullU = 160;
                halfU = 169;
            }

            if (i <= Mth.floor(curHealth)) {
                // Full heart
                graphics.blit(ICONS_LOCATION, xPos, yPos, fullU, 0, 9, 9);
            } else if (i < curHealth + 1) {
                // Half heart
                graphics.blit(ICONS_LOCATION, xPos, yPos, halfU, 0, 9, 9);
            }

            xOffset -= 8;
            if (xOffset < 0) {
                xOffset = iconsPerLine * 8 - 8;
                yOffset -= 4;
            }
        }
    }

    @Override
    public String getMessage() {
        return I18n.get("narration.jade.health", Mth.ceil(health));
    }
}
