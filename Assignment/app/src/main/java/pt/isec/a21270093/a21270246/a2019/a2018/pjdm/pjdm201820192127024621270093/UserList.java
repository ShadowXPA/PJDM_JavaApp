package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserList {
    private List<Map<String, User>> UserList;
    public final String UserKeyVal = "User";
    private final Object LockThread = new Object();
    private MyApp app;

    public List<Map<String, User>> GetUserList() {
        return new ArrayList<>(UserList);
    }

    public void LoadFromFileAsync(final ListView LV, final LayoutInflater li) {
        Thread td = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (LockThread) {
                    try {
                        LoadFromFile();
                    } catch (IOException | ClassNotFoundException ignored) { }

                    LV.post(new Runnable() {
                        @Override
                        public void run() {
                            LV.setAdapter(new MyAdapterUser(app, li));
                        }
                    });
                    /*activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            app.MAU.notifyDataSetChanged();
                        }
                    });*/
                }
            }
        });

        td.setName("BGUserLoad");
        td.start();
    }

    public void SortAndSave() {
        Thread td = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (LockThread) {
                    SortList(app);
                    try {
                        SaveToFile();
                    } catch (IOException ignored) { }
                    /*activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            app.MAU.notifyDataSetChanged();
                        }
                    });*/
                }
            }
        });

        td.setName("BGUserSave");
        td.start();
    }

    private void SortList(Application app) {
        Collections.sort(UserList, new MyComparatorUser(app));
    }

    public Map<String, User> GetUserListItemInverted(int index) {
        int size = UserList.size();
        if (size > 0)
            return new HashMap<>(UserList.get((size - 1) - index));
        else
            return null;
    }

    public List<User> GetUserListButSelfInverted() {
        List<User> aux = new ArrayList<>();

        for (Map<String, User> user : GetUserList()) {
            if (user.get(UserKeyVal).GetUserID() != app.AuthenticatedUser.GetUserID() && !aux.contains(user.get(UserKeyVal))) {
                aux.add(user.get(UserKeyVal));
            }
        }

        return aux;
    }

    public User GetUserInverted(int index) {
        int size = GetUserListButSelfInverted().size();
        return size > 0 ? GetUserListButSelfInverted().get((size - 1) - index) : null;
    }

    public boolean AddUser(String Username, String Password, boolean Admin) {
        if (!Username.isEmpty() && !Password.isEmpty()) {
            int USER_ID = app.SPUserPref.getInt("userID", 0);
            Map<String, User> aux = new HashMap<>();
            // First User is Admin
            if (UserList.size() == 0)
                Admin = true;
            aux.put(UserKeyVal, new User(USER_ID, Username, Password, new Date(), Admin));
            UserList.add(aux);
            USER_ID++;
            app.SPUserPref.edit().putInt("userID", USER_ID).apply();
            SortAndSave();
            return true;
        }
        return false;
    }

    public boolean EditUser(String Username, String OldPassword, String Password, int index) {
        return EditUser(Username, OldPassword, Password, GetUserListItemInverted(index).get(UserKeyVal));
    }

    public boolean EditUser(String Username, String OldPassword, String Password, User user) {
        if (!Username.isEmpty() && !Password.isEmpty()) {
            if (user.AuthenticateUser(OldPassword)) {
                String OldUsername = user.getUsername();

                if (!user.setUsername(Username))
                    return false;

                if (!user.setPassword(Password)) {
                    user.setUsername(OldUsername);
                    return false;
                }

                SortAndSave();
                return true;
            }
        }
        return false;
    }

    public boolean RemoveUser(int index, String Password) {
        try {
            if (GetUserListItemInverted(index).get(UserKeyVal).AuthenticateUser(Password)) {
                int _USERID = GetUserListItemInverted(index).get(UserKeyVal).GetUserID();
                int _ADMINID = UserList.get(0).get(UserKeyVal).GetUserID();
                if (_USERID != _ADMINID) {
                    File fl = new File(app.getFilesDir() + "/notes_" + _USERID + ".bin");
                    if (fl.exists()) {
                        fl.delete();
                    }
                    // Deletes the user's image folder
                    File imgDir = new File(app.getFilesDir() + "/srcImgs/" + _USERID);
                    if (imgDir.exists()) {
                        // Delete files inside before deleting the dir... Doesn't work otherwise
                        for (File f : imgDir.listFiles()) {
                            if (f.isDirectory()) {
                                for (File j : f.listFiles()) {
                                    j.delete();
                                }
                            }
                            f.delete();
                        }
                        imgDir.delete();
                    }
                    // Removing User's Shared notes
                    /*if (app.ListNotes != null) {
                        app.ListNotes.RemoveSharedNotes(_USERID);
                    }*/

                    UserList.remove(GetUserListItemInverted(index));
                    SortAndSave();
                    return true;
                } else {
                    // Remove user if last user is admin
                    if (UserList.size() == 1) {
                        // Delete User's Note File...
                        File fl = new File(app.getFilesDir() + "/notes_" + _ADMINID + ".bin");
                        if (fl.exists()) {
                            fl.delete();
                        }
                        // Delete Shared Notes File is no user is available...
                        File f2 = new File(app.getFilesDir() + "/sharedNotes.bin");
                        if (f2.exists()) {
                            f2.delete();
                        }
                        // Delete last user's imgs
                        File f3 = new File(app.getFilesDir() + "/srcImgs/" + _ADMINID);
                        if (f3.exists()) {
                            for (File f : f3.listFiles()) {
                                if (f.isDirectory()) {
                                    for (File j : f.listFiles()) {
                                        j.delete();
                                    }
                                }
                                f.delete();
                            }
                            f3.delete();
                        }
                        // Delete srcImgs folder
                        File f4 = new File(app.getFilesDir() + "/srcImgs");
                        if (f4.exists())
                            f4.delete();

                        UserList.remove(GetUserListItemInverted(index));
                        SortAndSave();
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private void SaveToFile() throws IOException {
        FileOutputStream fOS = app.openFileOutput("users.bin", Context.MODE_PRIVATE);
        ObjectOutputStream oOS = new ObjectOutputStream(fOS);

        oOS.writeObject(UserList);

        oOS.close();
    }

    private void LoadFromFile() throws IOException, ClassNotFoundException {
        FileInputStream fIS = app.openFileInput("users.bin");
        ObjectInputStream oIS = new ObjectInputStream(fIS);

        UserList = new ArrayList<>();
        UserList = (List<Map<String,User>>) oIS.readObject();

        oIS.close();
    }

    public UserList(Application app) {
        UserList = new ArrayList<>();
        this.app = (MyApp)app;
    }
}
