package de.scarpex.mrf.command.general;

import de.scarpex.mrf.chromedriver.ChromeDriverUtils;
import de.scarpex.mrf.chromedriver.OSType;
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
            if(ChromeDriverUtils.getOS() == OSType.WINDOWS){
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
