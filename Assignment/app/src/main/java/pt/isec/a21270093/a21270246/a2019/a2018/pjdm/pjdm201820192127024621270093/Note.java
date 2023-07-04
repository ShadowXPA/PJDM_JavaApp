package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class Note implements Serializable {
    private int ID;
    private String Title;
    private int AuthorID;
    private String AuthorUN;
    private Date CreationDate;
    private boolean Shared;
    private List<Integer> SharedUsers;

    public Note(int ID, String title, User author, Date creationDate, boolean shared, List<Integer> users) {
        this.ID = ID;
        Title = title;
        AuthorID = author.GetUserID();
        AuthorUN = author.getUserTitle();
        CreationDate = creationDate;
        if (shared && users != null && users.size() != 0) {
            Shared = true;
            SharedUsers = new ArrayList<>(users);
        } else {
            Shared = false;
            SharedUsers = new ArrayList<>();
        }
    }

    public String getAuthorUN() {
        return AuthorUN;
    }

    public List<Integer> getSharedUsers() {
        if (SharedUsers != null)
            return SharedUsers;
        else
            return new ArrayList<>();
    }

    public abstract String compareType();

    public int getID() {
        return ID;
    }

    public String getTitle() {
        return Title;
    }

    public int getAuthor() {
        return AuthorID;
    }

    public boolean isAuthor(int UserID) {
        return AuthorID == UserID;
    }

    public boolean isShared() {
        return Shared;
    }

    public void setShared(boolean shared, List<Integer> users) {
        this.Shared = shared;
        if (shared)
            SharedUsers = new ArrayList<>(users);
        else
            SharedUsers = new ArrayList<>();
    }

    public void addSharedAuthor(int userID) {
        if (Shared)
            if (!SharedUsers.contains(userID))
                SharedUsers.add(userID);
    }

    public void removeSharedAuthor(int userID) {
        if (Shared)
            SharedUsers.remove(userID);
    }

    public boolean isUserInSharedList(int user) {
        for (int u : SharedUsers) {
            if (u == user) {
                return true;
            }
        }
        return false;
    }

    public Date getDate() {
        return this.CreationDate;
    }

    public void resetDate() {
        this.CreationDate = new Date();
    }

    public String getFormatedDate() {
        return android.text.format.DateFormat.format("MMM dd, yyyy hh:mm:ss a", CreationDate).toString();
    }

    public String getNoteDescription() {
        return getFormatedDate() + " - " + AuthorUN;
    }

    public boolean setTitle(String title) {
        if (!title.isEmpty()) {
            this.Title = title;
            return true;
        } else {
            return false;
        }
    }
}
