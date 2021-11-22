package ru.yammi.gui.elements.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import ru.yammi.Yammi;
import ru.yammi.gui.elements.IElement;
import ru.yammi.gui.font.FontGL11;
//import ru.yammi.module.misc.KeybindManager;
import ru.yammi.utils.ColorUtils;
import ru.yammi.utils.MouseUtils;
import ru.yammi.utils.R2DUtils;
import ru.yammi.utils.TimerUtils;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextField {

    private String text = "Search...";

    private boolean selected = false;
    private boolean selectAll = false;

    private boolean firstClick = true;
    private Minecraft mc = Minecraft.getMinecraft();
    private MouseUtils mouseUtils = new MouseUtils(125L);

    private Pattern pattern = Pattern.compile("[a-zA-Z0-9]");

    public void draw(int x, int y, int width) {
        FontGL11 font = getFont();

        R2DUtils.drawRect(x, y + 15, x + width, y + 16, ColorUtils.KEYBIND_PANEL_SEPARATOR_COLOR);
        this.drawCursor(x, y);
        font.drawString(text, x, y, -1);

        if(isHovered(x, y, width)) {
            if(firstClick) {
                if (mouseUtils.isMouseButtonDown(0)) {
                    this.text = "";
                    this.selected = true;
                    this.firstClick = false;
                }
            }
        }

        updateKeys();
    }

    private boolean isHovered(int x, int y, int width) {
        int i = Mouse.getEventX() * this.mc.currentScreen.width / this.mc.displayWidth;
        int j = this.mc.currentScreen.height - Mouse.getEventY() * this.mc.currentScreen.height / this.mc.displayHeight - 1;
        int startSettingsBoxPosX = x;
        int startSettingsBoxPosY = y;
        int endSettingsBoxPosX = x + width;
        int endSettingsBoxPosY = y + 8;
        boolean hovered = i >= startSettingsBoxPosX && i <= endSettingsBoxPosX && j >= startSettingsBoxPosY && j <= endSettingsBoxPosY;
        return hovered;
    }

    public void drawCursor(int x, int y){
        /*if(cursorTicks >= 0 && cursorTicks < 120) {
            R2DUtils.drawRect(x + cursorPos, y, x + cursorPos + 1, y + 8, -1);
            cursorTicks++;
        }
        if(cursorTicks >= 120 && cursorTicks < 240) {
            cursorTicks++;
        }
        if(cursorTicks >= 240) {
            cursorTicks = 0;
        }*/
    }

    public void sortModules(){
        //Yammi.getInstance().getModule(KeybindManager.class).getKeybindScreen().getKeybindSelectPanel().sortModules(this.text);
    }

    public void onInit(){
        this.setText("Search...");
        this.setSelected(true);
        this.firstClick = true;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public FontGL11 getFont(){
        return Yammi.getInstance().getModuleFont();
    }

    private boolean checkCtrlA(int key) {
        return GuiScreen.isKeyComboCtrlA(key);
    }

    private boolean checkCtrlC(int key) {
        return GuiScreen.isKeyComboCtrlC(key);
    }

    private boolean checkCtrlV(int key) {
        return GuiScreen.isKeyComboCtrlV(key);
    }

    private void updateKeys(){
        if (Keyboard.isCreated())
        {
            while (Keyboard.next()){
                if(Keyboard.getEventKeyState()){
                    int pressed = Keyboard.getEventKey();
                    char c = Keyboard.getEventCharacter();
                    if(pressed != 0) {
                        this.onKeyTyped(pressed, c);
                        this.checkKeyCombo(pressed);
                    }
                }
            }
        }
    }

    private void checkKeyCombo(int key){
        if(checkCtrlA(key)) {
            this.selectAll = !selectAll;
        }
        if(checkCtrlC(key)) {
            try {
                StringSelection selection = new StringSelection(this.text);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(checkCtrlV(key)) {
            try {
                Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
                if(transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    String text = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                    if (this.selectAll) {
                        this.text = text;
                        this.selectAll = false;
                    } else {
                        for (char ch : text.toCharArray()) {
                            this.text += ch;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void delete(){
        if(this.text.length() > 0){
            if(this.selectAll) {
                this.text = "";
                this.selectAll = false;
            } else {
                this.text = this.text.substring(0, this.text.length() - 1);
            }
        }
    }

    public void onKeyTyped(int key, char c) {
        switch (key){
            case Keyboard.KEY_LCONTROL:
                return;
            case Keyboard.KEY_RETURN:
                this.text = "";
                sortModules();
                return;
            case Keyboard.KEY_BACK:
                this.delete();
                sortModules();
                return;
            case Keyboard.KEY_SPACE:
                this.text += (char)' ';
                sortModules();
                return;

        }
        if(this.text.equals("Search..."))
            this.text = "";
        String s = new String(new char[] {c});
        Matcher matcher = pattern.matcher(s);
        if(matcher.find()) {
            if(selectAll) {
                this.text = String.valueOf(c);
                this.selectAll = false;
                sortModules();
            } else {
                this.text += c;
                sortModules();
            }
        }
    }
}
