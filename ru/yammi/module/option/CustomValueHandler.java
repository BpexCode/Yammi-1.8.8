package ru.yammi.module.option;

public abstract class CustomValueHandler {

    private Option option;

    public CustomValueHandler(Option option){
        this.option = option;
    }

    public CustomValueHandler(){}

    public void setOption(Option option) {
        this.option = option;
    }

    public Option getOption(){
        return option;
    }

    public abstract Object getCustomValue();

    public abstract void setCustomValue(Object object);

    public abstract void doRender(int x, int y);

}
