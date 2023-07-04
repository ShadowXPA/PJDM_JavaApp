package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    private int ID;
    // Minimum length 3, Maximum length 22
    private String Username;
    // Minimum length 4, Maximum length 22
    private String Password;
    private Date CreationDate;
    private boolean Admin;

    public boolean AuthenticateUser(String Password) {
        return this.Password.equals("" + Password.hashCode());
    }

    public Integer GetUserID() {
        return this.ID;
    }

    public boolean isAdmin() {
        return Admin;
    }

    public User(int ID, String username, String password, Date creationDate, boolean admin) {
        this.ID = ID;
        setUsername(username);
        setPassword(password);
        CreationDate = creationDate;
        Admin = admin;
    }

    public String getFormatedDate() {
        return android.text.format.DateFormat.format("MMM dd, yyyy hh:mm:ss a", CreationDate).toString();
    }

    public String getUserTitle() {
        return this.Username + " ID: " + this.ID;
    }

    public String getUserDescription() {
        String str = getFormatedDate();

        if (this.Admin) {
            str += " - Administrator";
        }

        return str;
    }

    public String getUsername() {
        return Username;
    }

    public boolean setUsername(String username) {
        if (!username.isEmpty()) {
            this.Username = username;
            return true;
        } else {
            return false;
        }
    }

    public boolean setPassword(String password) {
        if (!password.isEmpty()) {
            this.Password = "" + password.hashCode();
            return true;
        } else {
            return false;
        }
    }

    public Date getDate() {
        return this.CreationDate;
    }
}
