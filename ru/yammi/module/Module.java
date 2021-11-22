package ru.yammi.module;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import ru.yammi.module.option.Option;

import java.util.ArrayList;
import java.util.List;

public class Module {

    private String name;
    private boolean state = false;
    private int keybind = Keyboard.KEY_NONE;

    private Category category;
    protected Minecraft mc = Minecraft.getMinecraft();

    private List<Option> options = new ArrayList<Option>();
    private String caption = "No caption";

    public Module(String name, Category category, String caption) {
        this(name, category);
        this.caption = caption;
    }

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getKeybind() {
        return keybind;
    }

    public void setKeybind(int keybind) {
        this.keybind = keybind;
    }

    public void onEnable(){}

    public void onDisable(){}

    public List<Option> getOptions() {
        return this.options;
    }

    protected Option.Mode[] getModes(String... modes){
        Option.Mode[] result = new Option.Mode[modes.length];
        for(int i = 0; i < modes.length; i++){
            result[i] = new Option.Mode(modes[i]);
        }
        return result;
    }

    public Option getOption(String name){
        for(Option option : this.getOptions()){
            if(option.getName().equals(name))
                return option;
        }
        return null;
    }

    public String getCaption() {
        return caption;
    }

    @Override
    public String toString(){
        return this.getName();
    }
}
