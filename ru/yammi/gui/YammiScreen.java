package ru.yammi.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import ru.yammi.Yammi;
import ru.yammi.gui.animation.OpenScreenAnimation;
import ru.yammi.gui.elements.IElement;
import ru.yammi.gui.elements.TabElement;
import ru.yammi.gui.notification.NotificationSystem;
import ru.yammi.module.Category;
import ru.yammi.module.misc.ClickGUI;
import ru.yammi.utils.Reflection;
import ru.yammi.utils.ShaderUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class YammiScreen extends GuiScreen {

    private ShaderUtils shaderUtils = new ShaderUtils();
    private OpenScreenAnimation screenAnimation = new OpenScreenAnimation();
    private NotificationSystem notificationSystem = new NotificationSystem(4);

    private TabElement topElement;
    private List<TabElement> elements = new CopyOnWriteArrayList<>();

    private boolean isVimeworld = false;

    public YammiScreen(boolean fromConfig) {
        for(Category category : Category.values()) {
            TabElement tabElement = new TabElement(category, fromConfig);
            elements.add(tabElement);
        }
        try {
            Field field = Reflection.getField(Entity.class, "OBFVAL_0");
            isVimeworld = field != null;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void mouseClickMove(int xD, int yD, int mouse, long time) {
        for(IElement element : elements)
            element.mouseClickMove(xD, yD, mouse, time);
    }

    public void mouseClicked(int xD, int yD, int mouse) {
        for(IElement element : elements)
            element.mouseClicked(xD, yD, mouse);
    }

    public void mouseReleased(int xD, int yD, int state) {
        for(IElement element : elements)
            element.mouseReleased(xD, yD, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for(IElement element : elements)
            element.keyTyped(keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if(!isVimeworld)
            shaderUtils.onRender();
        notificationSystem.draw();

        screenAnimation.beginDraw();

        for(int i = 0; i < elements.size(); i++) {
            elements.get(i).draw(mouseX, mouseY, partialTicks);
        }

        screenAnimation.endDraw();
    }

    public void setTopElement(TabElement tabElement) {
        elements.sort(new Comparator<TabElement>() {
            @Override
            public int compare(TabElement o1, TabElement o2) {
                if(o2 == tabElement)
                    return -1;
                return 0;
            }
        });
    }

    @Override
    public void initGui() {
        notificationSystem.initGui();
        if(!isVimeworld)
            shaderUtils.enable();
        screenAnimation.onOpenScreen();
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        if(!isVimeworld)
             shaderUtils.disable();
        screenAnimation.onCloseScreen();
        Yammi.getInstance().getModule(ClickGUI.class).setState(false);
        super.onGuiClosed();
    }

    @Override
    public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor){
        super.drawGradientRect(left, top, right, bottom, startColor, endColor);
    }

    public TabElement getElementByCategory(Category category) {
        for(TabElement tabElement : getElements()){
            if(tabElement.getCategory() == category)
                return tabElement;
        }
        return null;
    }

    public List<TabElement> getElements() {
        return elements;
    }

    public NotificationSystem getNotificationSystem() {
        return notificationSystem;
    }
}
