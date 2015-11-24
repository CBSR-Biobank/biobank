package edu.ualberta.med.biobank.tools.cli.command;

import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {

    private final Map<String, Command> commands = new HashMap<String, Command>();

    public static CommandRegistry getInstance() {
        return CommandRegistryHolder.INSTANCE;
    }

    public void addCommand(Command command) {
        commands.put(command.getName(), command);
    }

    public void invokeCommand(String commandName, String[] args) {
        checkCommands();
        if (commands.containsKey(commandName)) {
            if (!commands.get(commandName).runCommand(args)) {
                System.exit(1);
            }
        } else {
            System.out.println("Error: no command registered: " + commandName);
            System.exit(1);
        }
    }

    public void showCommands() {
        System.out.println("Possible commands:");
        for (Command command : commands.values()) {
            System.out.println(command.getName());
        }
    }

    public void showCommandsAndHelp() {
        System.out.println("Possible commands:");
        for (Command command : commands.values()) {
            System.out.println(command.getName() + "\t- " + command.getHelp());
        }
    }

    public void showCommandHelp(String commandName) {
        checkCommands();
        if (commands.containsKey(commandName)) {
            Command command = commands.get(commandName);
            System.out.println(command.getUsage() + "\n\n" + command.getHelp());
        } else {
            System.out.println("Error: no command registered: " + commandName);
            System.exit(1);
        }
    }

    private void checkCommands() {
        if (commands.isEmpty()) {
            System.out.println("Error: no commands registered");
            System.exit(-1);
        }

    }

    private static class CommandRegistryHolder {
        private static final CommandRegistry INSTANCE = new CommandRegistry();
    }
}
