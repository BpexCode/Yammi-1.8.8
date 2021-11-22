package ru.yammi.command;

import java.util.List;

public interface ICommand {

    public void execute(List<String> args);
    public String getName();

}
