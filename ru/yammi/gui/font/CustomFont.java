package ru.yammi.gui.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

public class CustomFont {
    private float imgSize = 512.0F;
    protected CustomFont.CharData[] charData = new CustomFont.CharData[256];
    protected Font font;
    protected boolean antiAlias;
    protected boolean fractionalMetrics;
    protected int fontHeight = -1;
    protected int charOffset = 0;
    protected DynamicTexture tex;

    public CustomFont(Font font, boolean antiAlias, boolean fractionalMetrics) {
        this.font = font;
        this.antiAlias = antiAlias;
        this.fractionalMetrics = fractionalMetrics;
        this.tex = this.setupTexture(font, antiAlias, fractionalMetrics, this.charData);
    }

    protected DynamicTexture setupTexture(Font font, boolean antiAlias, boolean fractionalMetrics, CustomFont.CharData[] chars) {
        try {
            return new DynamicTexture(this.generateFontImage(font, antiAlias, fractionalMetrics, chars));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    protected BufferedImage generateFontImage(Font font, boolean antiAlias, boolean fractionalMetrics, CustomFont.CharData[] chars) {
        int imgSize = (int)this.imgSize;
        BufferedImage bufferedImage = null;
        bufferedImage = new BufferedImage(imgSize, imgSize, 2);
        Graphics2D g = (Graphics2D)bufferedImage.getGraphics();
        g.setFont(font);
        g.setColor(new Color(255, 255, 255, 0));
        g.fillRect(0, 0, imgSize, imgSize);
        g.setColor(Color.WHITE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAlias ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        FontMetrics fontMetrics = g.getFontMetrics();
        int charHeight = 0;
        int positionX = 0;
        int positionY = 1;

        for(int i = 0; i < chars.length; ++i) {
            char ch = (char)i;
            CustomFont.CharData charData = new CustomFont.CharData();
            Rectangle2D dimensions = fontMetrics.getStringBounds(String.valueOf(ch), g);
            charData.width = dimensions.getBounds().width + 8;
            charData.height = dimensions.getBounds().height;
            if(positionX + charData.width >= imgSize) {
                positionX = 0;
                positionY += charHeight;
                charHeight = 0;
            }

            if(charData.height > charHeight) {
                charHeight = charData.height;
            }

            charData.storedX = positionX;
            charData.storedY = positionY;
            if(charData.height > this.fontHeight) {
                this.fontHeight = charData.height;
            }

            chars[i] = charData;
            g.drawString(String.valueOf(ch), positionX + 2, positionY + fontMetrics.getAscent());
            positionX += charData.width;
        }

        return bufferedImage;
    }

    public String trimStringToWidth(String text, int width)
    {
        return this.trimStringToWidth(text, width, false);
    }

    public String trimStringToWidth(String text, int width, boolean reverse)
    {
        StringBuilder stringbuilder = new StringBuilder();
        int i = 0;
        int j = reverse ? text.length() - 1 : 0;
        int k = reverse ? -1 : 1;
        boolean flag = false;
        boolean flag1 = false;

        for (int l = j; l >= 0 && l < text.length() && i < width; l += k)
        {
            char c0 = text.charAt(l);
            int i1 = this.getCharWidth(c0);

            if (flag)
            {
                flag = false;

                if (c0 != 108 && c0 != 76)
                {
                    if (c0 == 114 || c0 == 82)
                    {
                        flag1 = false;
                    }
                }
                else
                {
                    flag1 = true;
                }
            }
            else if (i1 < 0)
            {
                flag = true;
            }
            else
            {
                i += i1;

                if (flag1)
                {
                    ++i;
                }
            }

            if (i > width)
            {
                break;
            }

            if (reverse)
            {
                stringbuilder.insert(0, (char)c0);
            }
            else
            {
                stringbuilder.append(c0);
            }
        }

        return stringbuilder.toString();
    }

    private int getCharWidth(char c) {
        return this.charData[c].width;
    }

    public void drawChar(CustomFont.CharData[] chars, char c, float x, float y) throws ArrayIndexOutOfBoundsException {
        try {
            this.drawQuad(x, y, (float)chars[c].width, (float)chars[c].height, (float)chars[c].storedX, (float)chars[c].storedY, (float)chars[c].width, (float)chars[c].height);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    protected void drawQuad(float x, float y, float width, float height, float srcX, float srcY, float srcWidth, float srcHeight) {
        float renderSRCX = srcX / this.imgSize;
        float renderSRCY = srcY / this.imgSize;
        float renderSRCWidth = srcWidth / this.imgSize;
        float renderSRCHeight = srcHeight / this.imgSize;
        GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
        GL11.glVertex2d((double)(x + width), (double)y);
        GL11.glTexCoord2f(renderSRCX, renderSRCY);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
        GL11.glVertex2d((double)x, (double)(y + height));
        GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
        GL11.glVertex2d((double)x, (double)(y + height));
        GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY + renderSRCHeight);
        GL11.glVertex2d((double)(x + width), (double)(y + height));
        GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
        GL11.glVertex2d((double)(x + width), (double)y);
    }

    public int getStringHeight(String text) {
        return this.getHeight();
    }

    public int getHeight() {
        return (this.fontHeight - 8) / 2;
    }

    public int getStringWidth(String text) {
        int width = 0;
        char[] arr$ = text.toCharArray();
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            char c = arr$[i$];
            if(c < this.charData.length && c >= 0) {
                width += this.charData[c].width - 8 + this.charOffset;
            }
        }

        return width / 2;
    }

    public boolean isAntiAlias() {
        return this.antiAlias;
    }

    public void setAntiAlias(boolean antiAlias) {
        if(this.antiAlias != antiAlias) {
            this.antiAlias = antiAlias;
            this.tex = this.setupTexture(this.font, antiAlias, this.fractionalMetrics, this.charData);
        }

    }

    public boolean isFractionalMetrics() {
        return this.fractionalMetrics;
    }

    public void setFractionalMetrics(boolean fractionalMetrics) {
        if(this.fractionalMetrics != fractionalMetrics) {
            this.fractionalMetrics = fractionalMetrics;
            this.tex = this.setupTexture(this.font, this.antiAlias, fractionalMetrics, this.charData);
        }

    }

    public Font getFont() {
        return this.font;
    }

    public void setFont(Font font) {
        this.font = font;
        this.tex = this.setupTexture(font, this.antiAlias, this.fractionalMetrics, this.charData);
    }

    protected class CharData {
        public int width;
        public int height;
        public int storedX;
        public int storedY;
    }
}
