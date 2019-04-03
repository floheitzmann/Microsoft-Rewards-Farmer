package de.scarpex.mrf.account;

/**
 * This class contains the credentials of a Microsoft account.
 */
public class MSAccount {
    private final String login, password;

    public MSAccount(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return this.login;
    }

    public String getPassword() {
        return this.password;
    }
}
