package ru.yammi.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import ru.yammi.Yammi;
import ru.yammi.config.Config;
import ru.yammi.gui.font.FontGL11;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.utils.ColorUtils;
import ru.yammi.utils.PositionUtils;
import ru.yammi.utils.R2DUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class TabElement implements IElement {

    public static boolean anyTabClicked = false;

    private Minecraft mc = Minecraft.getMinecraft();
    private Category category;
    private int x = 0;
    private int y = 0;
    private int lastX = 0;
    private int lastY = 0;
    private boolean dragging = false;

    private List<IElement> elements = new ArrayList<>();
    private ResourceLocation currentImage;

    private boolean sorted = false;
    private int prevScale = 2;

    public TabElement(Category categoryIn, boolean fromConfig){
        this.category = categoryIn;
        this.x = categoryIn.ordinal() * PositionUtils.TAB_WIDTH;
        this.currentImage = new ResourceLocation("minecraft", categoryIn.name().toLowerCase(Locale.ROOT) + "Image");

        load(category);
    }

    private void load(Category categoryIn) {
        for(Module module : Yammi.getInstance().getModules()) {
            if(module.getCategory() == categoryIn) {
                elements.add(new ModuleElement(module));
            }
        }

        /*elements.sort(new Comparator<ModuleElement>() {
            @Override
            public int compare(ModuleElement o1, ModuleElement o2) {
                return Yammi.getInstance().getTabFont().getStringWidth(o2.getModule().getName()) - Yammi.getInstance().getTabFont().getStringWidth(o1.getModule().getName());
            }
        });*/
    }

    @Override
    public void keyTyped(int key) {
        for (int i = 0; i < elements.size(); i++) {
            IElement element = elements.get(i);
            element.keyTyped(key);
        }
    }

    @Override
    public void draw(int posX, int posY, float partialTicks) {
        if(!sorted) {
            sorted = true;
            elements.sort(new Comparator<IElement>() {
                @Override
                public int compare(IElement oo1, IElement oo2) {
                    ModuleElement o1 = (ModuleElement) oo1;
                    ModuleElement o2= (ModuleElement) oo2;
                    return Yammi.getInstance().getTabFont().getStringWidth(o2.getModule().getName()) - Yammi.getInstance().getTabFont().getStringWidth(o1.getModule().getName());
                }
            });
            sorted = true;
        }

        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        int width = PositionUtils.TAB_WIDTH - 30;
        int heigth = PositionUtils.TAB_HEIGHT - 10;

        //GL11.glPushMatrix();

        float scale = 1.0F;

        int factor = scaledResolution.getScaleFactor();
        switch (factor) {
            case 1: {
                scale += 1F;
                break;
            }
            case 3: {
                scale -= 0.3F;
                break;
            }
            case 5 : {
                scale -= 0.5F;
                break;
            }
        }
        //GL11.glScalef(scale, scale, 0F);

        this.renderPanel(width, heigth);
        String text = this.category.name();

        FontGL11 font = Yammi.getInstance().getTabFont();

        PositionUtils.RenderStringPosition stringPosition = PositionUtils.TAB_STRING_POSITION;
        int xOffset = 0;
        switch (stringPosition) {
            case LEFT: {
                xOffset = 30;
                break;
            }
            case CENTER: {
                xOffset = (width/ 2) - (font.getStringWidth(text) / 2);
                break;
            }
            case RIGHT: {
                xOffset = width - font.getStringWidth(text) - 30;
                break;
            }
        }

        Yammi.getInstance().getTabFont().drawString(text, x + xOffset, y + 2, -1);
        this.drawImage();

        R2DUtils.drawRect(x, y + heigth, x + width, y + heigth + PositionUtils.TAB_SEPARATOR_HEIGHT, ColorUtils.TAB_SEPARATOR_COLOR);

        for (int i = 0; i < elements.size(); i++) {
            IElement element = elements.get(i);
            element.draw(this.x, this.y + (i * 17), partialTicks);
        }


        if(this.mc != null && this.mc.currentScreen != null) {

        }

       // GL11.glPopMatrix();
    }

    private void renderPanel(int width, int height){
        R2DUtils.drawRect(x + PositionUtils.CIRCLE_RADIUS, y, x + width - PositionUtils.CIRCLE_RADIUS, y + PositionUtils.CIRCLE_RADIUS, ColorUtils.TAB_BACKGROUND_COLOR);
        R2DUtils.drawRect(x, y + PositionUtils.CIRCLE_RADIUS, x + width, y + height, ColorUtils.TAB_BACKGROUND_COLOR);

        R2DUtils.enableGL2D();
        R2DUtils.drawFullCircle(x + PositionUtils.CIRCLE_RADIUS, y + PositionUtils.CIRCLE_RADIUS, PositionUtils.CIRCLE_RADIUS, ColorUtils.TAB_BACKGROUND_COLOR, true);
        R2DUtils.drawFullCircle(x + width - PositionUtils.CIRCLE_RADIUS, y + PositionUtils.CIRCLE_RADIUS, PositionUtils.CIRCLE_RADIUS, ColorUtils.TAB_BACKGROUND_COLOR, true);
        R2DUtils.disableGL2D();
    }

    private void drawImage(){
        GL11.glPushMatrix();

        this.mc.getTextureManager().bindTexture(currentImage);
        this.mc.getTextureManager().getTexture(currentImage).setBlurMipmap(false, false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(x + 5, y + 2, 0.0f, 0.0f ,11, 11, 11, 11);
        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GL11.glPopMatrix();
    }

    @Override
    public void mouseReleased(int xD, int yD, int state) {
        if (this.isDragging()) {
            this.setDragging(false);
            //Config.store();

            anyTabClicked = false;
            Config.store();
        }
    }

    @Override
    public void mouseClicked(int xD, int yD, int mouse) {
        if(mouse == 0) {
            ScaledResolution scaledResolution = new ScaledResolution(this.mc);
            int screenWidth = scaledResolution.getScaledWidth();
            int screenHeight = scaledResolution.getScaledHeight();
            final int startX = this.x;
            final int startY = this.y;
            final int endX = this.x + PositionUtils.TAB_WIDTH - 30;
            final int endY = this.y + 15;

            int i = Mouse.getEventX() * screenWidth / this.mc.displayWidth;
            int j = screenHeight - Mouse.getEventY() * screenHeight / this.mc.displayHeight - 1;

            if (i >= startX && i <= endX && j >= startY && j <= endY) {
                this.setDragging(true);
                this.lastX = i;
                this.lastY = j;

                anyTabClicked = true;
                Yammi.getInstance().getScreen().setTopElement(this);
            }
        }
    }

    @Override
    public void mouseClickMove(int xD, int yD, int mouse, long time) {
        if (this.isDragging()) {
            this.x = this.x + xD - this.lastX;
            this.y = this.y + yD - this.lastY;
            this.lastX = xD;
            this.lastY = yD;
        }
    }

    public void setDragging(final boolean dragging) {
        this.dragging = dragging;
    }

    public boolean isDragging() {
        return this.dragging;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public Category getCategory(){
        return category;
    }

}
