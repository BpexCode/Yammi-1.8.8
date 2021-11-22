package ru.yammi.command;

import ru.yammi.Yammi;
import ru.yammi.config.Config;
import ru.yammi.gui.notification.NotificationSystem;
import ru.yammi.module.misc.HUD;

import java.util.List;
import java.util.function.Predicate;

public class FriendCommand implements ICommand {

    @Override
    public void execute(List<String> args) {
        if(args.size() == 0) {
            String message = "Invalid arguments, usage: friend <add|remove|clear|view> <nick>";
            Yammi.getInstance().getModule(HUD.class).getNotificationSystem().addNotification(message, NotificationSystem.NotificationType.CANCEL);
            return;
        }
        if(args.size() == 2) {
            String subcommand = args.get(0);
            if (subcommand.equalsIgnoreCase("add")) {
                String nick = args.get(1);
                Yammi.getInstance().getFriendList().add(nick);
                Config.store();
                String message = "Player " + nick + " added into friend list";
                Yammi.getInstance().getModule(HUD.class).getNotificationSystem().addNotification(message, NotificationSystem.NotificationType.OK);
                return;
            }
            if (subcommand.equalsIgnoreCase("remove")) {
                String nick = args.get(1);
                //Yammi.getInstance().getFriendList().removeIf(s -> s.equalsIgnoreCase(nick));
                Yammi.getInstance().getFriendList().removeIf(new Predicate<String>() {
                    @Override
                    public boolean test(String s) {
                        return s.equalsIgnoreCase(nick);
                    }
                });
                Config.store();
                String s = "Player " + nick + " removed from friend list";
                Yammi.getInstance().getModule(HUD.class).getNotificationSystem().addNotification(s, NotificationSystem.NotificationType.OK);
                return;
            }
            String s = "Subcommand " + subcommand + " not found";
            Yammi.getInstance().getModule(HUD.class).getNotificationSystem().addNotification(s, NotificationSystem.NotificationType.OK);
            return;
        } else if(args.size() == 1) {
            String subcommand = args.get(0);
            if (subcommand.equalsIgnoreCase("clear")) {
                Yammi.getInstance().getFriendList().clear();
                Config.store();
                String s = "Friend list cleared";
                Yammi.getInstance().getModule(HUD.class).getNotificationSystem().addNotification(s, NotificationSystem.NotificationType.OK);
                return;
            }
            if(subcommand.equalsIgnoreCase("view")) {
                StringBuilder builder = new StringBuilder();
                builder.append("Friends:");
                for(String s : Yammi.getInstance().getFriendList()) {
                    builder.append(" " + s + ",");
                }
                String s = builder.toString();
                Yammi.getInstance().getModule(HUD.class).getNotificationSystem().addNotification(s, NotificationSystem.NotificationType.OK);
                return;
            }
            String s = "Subcommand " + subcommand + " not found";
            Yammi.getInstance().getModule(HUD.class).getNotificationSystem().addNotification(s, NotificationSystem.NotificationType.OK);
            return;
        }
    }

    @Override
    public String getName() {
        return "#friend";
    }
}
