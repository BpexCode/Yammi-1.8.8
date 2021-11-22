package ru.yammi.module.option;

public class Option {

    private OptionType optionType;
    private String name;
    private int sliderX = 0;

    private int intValue;
    private int minIntValue;
    private int maxIntValue;

    private double doubleValue;
    private double minDoubleValue;
    private double maxDoubleValue;

    private float floatValue;
    private float minFloatValue;
    private float maxFloatValue;

    private boolean booleanValue;

    private String stringValue;

    private CustomValueHandler customValueHandler;
    private Object customValue;

    private Mode[] modes;
    private Mode mode;

    private int colorValue;

    public Option(String name, float minValue, float maxValue) {
        this(OptionType.FLOAT_VALUE, name);
        this.setMinFloatValue(minValue);
        this.setMaxFloatValue(maxValue);
    }

    public Option(String name, double minValue, double maxValue) {
        this(OptionType.DOUBLE_VALUE, name);
        this.setMinDoubleValue(minValue);
        this.setMaxDoubleValue(maxValue);
    }

    public Option(String name, int minValue, int maxValue) {
        this(OptionType.INT_VALUE, name);
        this.setMinIntValue(minValue);
        this.setMaxIntValue(maxValue);
    }

    public Option(String name, String stringValue) {
        this(OptionType.STRING, name);
        this.setStringValue(stringValue);
    }

    public Option(String name) {
        this(OptionType.BOOLEAN_VALUE, name);
    }

    public Option(String name, CustomValueHandler customValueHandler) {
        this(OptionType.CUSTOM, name);
        this.setCustomValueHandler(customValueHandler);
        customValueHandler.setOption(this);
    }

    public Option(String name, Mode[] modes) {
        this(OptionType.MODE, name);
        this.setModes(modes);
        this.setMode(modes[0]);
    }

    public Option(OptionType optionType, String name) {
        this.setOptionType(optionType);
        this.setName(name);
        if (this.optionType == OptionType.BOOLEAN_VALUE) {
            this.setBooleanValue(false);
        }
        if(this.optionType == OptionType.COLOR) {
            colorValue = -1;
        }
    }

    public boolean isModeSelect(Mode mode) {
        return this.mode.getName().equals(mode.getName());
    }

    public void selectMode(Mode mode) {
        this.mode = mode;
    }

    public Mode[] getModes() {
        return modes;
    }

    public void setModes(Mode[] modes) {
        this.modes = modes;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    public CustomValueHandler getCustomValueHandler() {
        return customValueHandler;
    }

    public void setCustomValueHandler(CustomValueHandler customValueHandler) {
        this.customValueHandler = customValueHandler;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Object getCustomValue() {
        return customValue;
    }

    public void setCustomValue(Object customValue) {
        this.customValue = customValue;
    }

    public int getColorValue(){
        return this.colorValue;
    }

    public void setColorValue(int color) {
        this.colorValue = color;
    }

    public int getSliderX() {
        return sliderX;
    }

    public void setSliderX(int sliderX) {
        this.sliderX = sliderX;
    }

    public int getMinIntValue() {
        return minIntValue;
    }

    public void setMinIntValue(int minIntValue) {
        this.minIntValue = minIntValue;
    }

    public int getMaxIntValue() {
        return maxIntValue;
    }

    public void setMaxIntValue(int maxIntValue) {
        this.maxIntValue = maxIntValue;
    }

    public double getMinDoubleValue() {
        return minDoubleValue;
    }

    public void setMinDoubleValue(double minDoubleValue) {
        this.minDoubleValue = minDoubleValue;
    }

    public double getMaxDoubleValue() {
        return maxDoubleValue;
    }

    public void setMaxDoubleValue(double maxDoubleValue) {
        this.maxDoubleValue = maxDoubleValue;
    }

    public float getMinFloatValue() {
        return minFloatValue;
    }

    public void setMinFloatValue(float minFloatValue) {
        this.minFloatValue = minFloatValue;
    }

    public float getMaxFloatValue() {
        return maxFloatValue;
    }

    public void setMaxFloatValue(float maxFloatValue) {
        this.maxFloatValue = maxFloatValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(float floatValue) {
        this.floatValue = floatValue;
    }

    public boolean isBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public OptionType getOptionType() {
        return optionType;
    }

    public void setOptionType(OptionType optionType) {
        this.optionType = optionType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public enum OptionType {
        MODE,
        CUSTOM,
        COLOR,
        STRING,
        INT_VALUE,
        FLOAT_VALUE,
        DOUBLE_VALUE,
        BOOLEAN_VALUE,
    }

    public static class Mode {

        private String name;

        public Mode(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
