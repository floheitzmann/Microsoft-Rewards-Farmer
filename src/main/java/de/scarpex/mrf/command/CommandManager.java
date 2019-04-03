package de.scarpex.mrf.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * This class governs the management of the general and executes them.
 */
public class CommandManager {
    private static final Logger log = LogManager.getLogger(CommandManager.class);

    private final String DEFAULT_COMMAND_LINE = "%s | [%s] | %s";
    private List<Command> commands = new ArrayList<>();

    /**
     * This method reads the entered line and checks if it is a command.
     * If it is a command, the command will be executed.
     *
     * @param line The command line input.
     */
    public void readCommandLine(String line) {
        String[] args = line.split(" ");

        // Yes, the basic help command is hardcoded. :^)
        if (args[0].equalsIgnoreCase("help")) {
            this.commands.forEach(command -> {
                String aliases = "";

                for (int i = 0; i < command.getAliases().size(); i++) {
                    aliases += command.getAliases().get(i)
                            + ((i + 1) == command.getAliases().size() ? "" : ",");
                }

                System.out.println(String.format(DEFAULT_COMMAND_LINE,
                        command.getName(), aliases, command.getDescription()));
            });
        } else {
            this.commands.forEach(command -> {
                if (command.getName().equalsIgnoreCase(args[0])
                        || command.getAliases().contains(args[0])) {
                    command.execute(args);
                    log.info(String.format("command executed -> %s", command.getName()));
                }
            });
        }
    }

    public void registerCommand(Command command) {
        if (isCommand(command.getName())) {
            log.warn(String.format("The %s command is already registered.", command.getName()));
            return;
        }

        this.commands.add(command);
        log.info(String.format("Registered command - %s", command.getName()));
    }

    private boolean isCommand(String name) {
        boolean bool = false;

        for (Command command : this.commands) {
            if (command.getName().equalsIgnoreCase(name)) {
                bool = true;
                break;
            }
        }

        return bool;
    }
}
