package de.scarpex.mrf.command.general;

import de.scarpex.mrf.command.Command;

import java.io.IOException;

/**
 * To clean the console.
 */
public class ClearCommand extends Command {
    public ClearCommand() {
        super("clear", "Clears the console.", "cc");
    }

    @Override
    public void execute(String[] args) {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
