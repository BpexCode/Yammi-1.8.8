package ru.yammi.config;

import com.google.gson.*;
import ru.yammi.Yammi;
import ru.yammi.gui.YammiScreen;
import ru.yammi.gui.elements.TabElement;
import ru.yammi.module.Category;
import ru.yammi.module.Module;
import ru.yammi.module.misc.ClickGUI;
import ru.yammi.module.option.Option;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Config {

    private JsonObject jsonObject;
    private String path = System.getProperty("user.home") + File.separator + "boba.json";
    private static Config instance;

    private JsonArray modules = new JsonArray();
    private JsonArray tabs = new JsonArray();
    private JsonArray xray = new JsonArray();
    private JsonArray friends = new JsonArray();

    public Config(){
        instance = this;
    }

    public static void load(){
        if(instance == null)
            new Config();
        instance.loadAll();
    }

    public void loadAll() {
        try {
            this.loadFile();
            this.loadModules();
            this.loadGUI();
            this.loadFriends();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFriends(){
        JsonArray friendsArray = jsonObject.getAsJsonArray("friends");
        if(friendsArray != null) {
            for (int i = 0; i < friendsArray.size(); i++) {
                JsonObject jsonObject = friendsArray.get(i).getAsJsonObject();
                String nick = jsonObject.get("nick").getAsString();
                Yammi.getInstance().getFriendList().add(nick);
            }
        }
    }

    private void loadGUI(){
        YammiScreen yammiScreen = Yammi.getInstance().getModule(ClickGUI.class).getClickGUIScreen(true);

        JsonArray tabsArray = jsonObject.getAsJsonArray("tabs");
        if(tabsArray != null){
            for(int i = 0; i < tabsArray.size(); i++){
                JsonObject jsonObject = tabsArray.get(i).getAsJsonObject();

                Category category = Category.values()[i];
                TabElement tabElement = yammiScreen.getElementByCategory(category);
                int x = jsonObject.get("x").getAsInt();
                int y = jsonObject.get("y").getAsInt();
                tabElement.setX(x);
                tabElement.setY(y);
            }
        }
    }


    private void loadModules(){
        JsonArray moduleArray = jsonObject.getAsJsonArray("modules");
        for (int i = 0; i < moduleArray.size(); ++i) {
            JsonObject moduleObject = moduleArray.get(i).getAsJsonObject();
            String name = moduleObject.get("name").getAsString();
            Module m = Yammi.getInstance().getModule(name);
            if (m != null) {
                m.setKeybind(moduleObject.get("bind").getAsInt());
                m.setState(moduleObject.get("state").getAsBoolean());

                JsonArray optionsArray = moduleObject.get("options").getAsJsonArray();
                for(int x = 0; x < optionsArray.size(); x++) {
                    JsonElement jsonElement = optionsArray.get(x);
                    JsonObject optionObject = jsonElement.getAsJsonObject();
                    String tag = optionObject.get("tag").getAsString();

                    Option option = m.getOption(tag);
                    option.setSliderX(optionObject.get("slider").getAsInt());

                    JsonElement element = optionObject.get("int_value");

                    if (element != null) {
                        option.setIntValue(element.getAsInt());
                    } else if ((element = optionObject.get("double_value")) != null) {
                        option.setDoubleValue(element.getAsDouble());
                    } else if ((element = optionObject.get("float_value")) != null) {
                        option.setFloatValue(element.getAsFloat());
                    } else if ((element = optionObject.get("boolean_value")) != null) {
                        option.setBooleanValue(element.getAsBoolean());
                    } else if ((element = optionObject.get("string_value")) != null) {
                        option.setStringValue(element.getAsString());
                    } else if ((element = optionObject.get("color_value")) != null) {
                        option.setColorValue( element.getAsInt());
                    } else if ((element = optionObject.get("mode_value")) != null) {
                        option.setMode(new Option.Mode(element.getAsString()));
                    }
                }
            }
        }
    }

    private void loadFile() throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
            store();
        }
        FileReader fileReader = new FileReader(file);
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(fileReader);
        jsonObject = jsonElement.getAsJsonObject();

        fileReader.close();
    }

    public static void store(){
        if(instance == null)
            new Config();
        instance.saveAll();
    }

    private void saveAll() {
        try {
            this.saveModules();
            this.saveGUI();

            this.saveFriends();
            jsonObject = new JsonObject();
            jsonObject.add("modules", modules);
            jsonObject.add("tabs", tabs);
            jsonObject.add("friends", friends);
            this.saveFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveFile() throws Exception {
        final File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        final FileWriter fileWriter = new FileWriter(file);
        final GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        builder.create().toJson(jsonObject, fileWriter);
        fileWriter.close();
    }

    private void saveFriends(){
        friends = new JsonArray();
        List<String> friendList = new ArrayList<>();
        for(String s : friendList){
            JsonObject object = new JsonObject();
            object.addProperty("nick", s);
            friends.add(object);
        }
    }

    private void saveGUI(){
        tabs = new JsonArray();
        ClickGUI clickGUI = Yammi.getInstance().getModule(ClickGUI.class);
        YammiScreen yammiScreen = clickGUI.getClickGUIScreen(true);
        List<TabElement> tabElements = yammiScreen.getElements();

        for(TabElement tabElement : tabElements){
            JsonObject object = new JsonObject();
            object.addProperty("x", tabElement.getX());
            object.addProperty("y", tabElement.getY());
            tabs.add(object);
        }
    }


    private void saveModules(){
        modules = new JsonArray();
        for(Module module : Yammi.getInstance().getModules()){
            final JsonObject obj = new JsonObject();
            obj.addProperty("name", module.getName());
            obj.addProperty("bind", module.getKeybind());
            obj.addProperty("state", module.getState());

            List<Option> options = module.getOptions();
            JsonArray optionsArray = new JsonArray();
            for(Option option : options) {
                JsonObject optionObject = new JsonObject();

                optionObject.addProperty("tag", option.getName());
                Option.OptionType type = option.getOptionType();

                if(type == Option.OptionType.INT_VALUE) {
                    optionObject.addProperty("int_value", option.getIntValue());
                }
                if(type == Option.OptionType.DOUBLE_VALUE) {
                    optionObject.addProperty("double_value", option.getDoubleValue());
                }
                if(type == Option.OptionType.FLOAT_VALUE) {
                    optionObject.addProperty("float_value", option.getFloatValue());
                }
                if(type == Option.OptionType.BOOLEAN_VALUE) {
                    optionObject.addProperty("boolean_value", option.isBooleanValue());
                }
                if(type == Option.OptionType.STRING) {
                    optionObject.addProperty("string_value", option.getStringValue());
                }
                if(type == Option.OptionType.COLOR) {
                    optionObject.addProperty("color_value", option.getColorValue());
                }
                if(type == Option.OptionType.MODE) {
                    optionObject.addProperty("mode_value", option.getMode().getName());
                }

                optionObject.addProperty("slider", option.getSliderX());
                optionsArray.add(optionObject);
            }

            obj.add("options", optionsArray);
            modules.add(obj);
        }
        //Yammi.getInstance().getModules().stream().forEach(module -> {
        //});
    }

    private void setSliderX(Module m, String name, int x) {
        if (m.getOptions().size() > 0) {
            for (Option option : m.getOptions()) {
                if (option.getName().equals(name)) {
                    option.setSliderX(x);
                }
            }
        }
    }

}
