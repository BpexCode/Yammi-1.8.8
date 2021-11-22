package ru.yammi.command;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    private List<ICommand> commands = new ArrayList<>();

    public CommandManager(){
        commands.add(new FriendCommand());
    }

    public boolean execute(String message) {
        if(message.length() == 1)
            return false;

        String[] splits = message.split(" ");
        String cmdName = splits == null ? message : splits[0];

        for(ICommand command : commands) {
            if(command.getName().equals(cmdName)) {
                List<String> args = new ArrayList<>();

                if(message.contains(" ")) {
                    for(int i = 1; i < splits.length; i++) {
                        args.add(splits[i]);
                    }
                }

                command.execute(args);
                return true;
            }
        }
        return false;
    }

}
