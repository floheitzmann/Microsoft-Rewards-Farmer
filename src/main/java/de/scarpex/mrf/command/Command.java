package de.scarpex.mrf.command;

import java.util.Arrays;
import java.util.List;

/**
 * This abstract class allows the creation of general. The general must
 * be registered in the {@link CommandManager} so they can be executed in
 * the command line.
 */
public abstract class Command {
    private String name, description;
    private List<String> aliases;

    /**
     * Creates a command and receives the first information about it.
     *
     * @param name     The name of the command.
     * @param description The description of the command.
     * @param aliases     All aliases of the command.
     */
    public Command(String name, String description, String... aliases) {
        this.name = name;
        this.description = description;
        this.aliases = Arrays.asList(aliases);
    }

    String getName() {
        return this.name;
    }

    String getDescription() {
        return this.description;
    }

    List<String> getAliases() {
        return this.aliases;
    }

    /**
     * Executes the given command.
     *
     * @param args Passed command arguments.
     */
    public abstract void execute(String[] args);
}