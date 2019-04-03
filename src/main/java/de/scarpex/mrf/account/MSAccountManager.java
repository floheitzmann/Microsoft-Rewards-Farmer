package de.scarpex.mrf.account;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class manages the available Microsoft accounts.
 */
public class MSAccountManager {
    private final static String CONFIG = "accounts.json";
    private final JSONParser parser = new JSONParser();
    private final List<MSAccount> accounts;

    public MSAccountManager(File folder) {
        this.accounts = new ArrayList<>();
        loadAccounts(folder);
    }

    // Maybe modify the creation of the file and send some information?
    // but idk right now

    private void loadAccounts(File folder) {
        File file = new File(folder, CONFIG);
        if (!file.exists()) {
            try (InputStream input = getClass().getResourceAsStream("/" + CONFIG)) {
                try (OutputStream output = new FileOutputStream(file)) {
                    IOUtils.copy(input, output);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Object object = this.parser.parse(new FileReader(file));
            JSONArray array = (JSONArray) ((JSONObject) object).get("accounts");

            array.forEach(value -> {
                String login = (String) ((JSONObject) value).get("login");
                String password = (String) ((JSONObject) value).get("password");

                this.accounts.add(new MSAccount(login, password));
            });
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a list with Microsoft accounts.
     *
     * @return a list.
     */
    public List<MSAccount> getAccounts() {
        return this.accounts;
    }
}
