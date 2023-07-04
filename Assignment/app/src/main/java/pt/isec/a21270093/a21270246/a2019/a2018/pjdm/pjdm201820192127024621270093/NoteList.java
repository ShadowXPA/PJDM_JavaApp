package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.provider.ContactsContract;
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

public class NoteList {
    private List<Map<String, Note>> NoteList;
    private List<Map<String, Note>> SharedNoteList;
    private final Object LockThread = new Object();
    public final String NoteKeyVal = "Note";
    public final String SharedNoteKeyVal = "SharedNote";
    private MyApp app;

    public List<Map<String, Note>> GetNoteList() {
        return new ArrayList<>(NoteList);
    }

    public List<Map<String, Note>> GetFULLSharedNoteList() {
        return SharedNoteList;
    }

    public List<Map<String, Note>> GetSharedNoteList() {
        // Gets a list of shared notes that the user can view
        List<Map<String, Note>> temp = new ArrayList<>();

        for (Map<String, Note> aux : SharedNoteList) {
            if (aux.get(SharedNoteKeyVal).getAuthor() == (app.AuthenticatedUser.GetUserID()) || aux.get(SharedNoteKeyVal).isUserInSharedList(app.AuthenticatedUser.GetUserID())) {
                if (!temp.contains(aux))
                    temp.add(aux);
            }
        }

        return temp;
    }

    public void LoadFromFileAsync(final ListView LV, final LayoutInflater li) {
        // Background Load
        Thread td = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (LockThread) {
                    int orderBy = Integer.parseInt(app.SPSettings.getString("orderBy", "0"));
                    try {
                        LoadFromFile();
                    } catch (IOException | ClassNotFoundException ignored) { }
                    RemoveSharedNotes();
                    SortLists(app, orderBy);
                    LV.post(new Runnable() {
                        @Override
                        public void run() {
                            boolean sharedListMode = app.SPSettings.getBoolean("sharedListMode", false);
                            if (sharedListMode) {
                                LV.setAdapter(new MyAdapterSharedNote(app, li));
                                //app.MASN.notifyDataSetChanged();
                            } else {
                                LV.setAdapter(new MyAdapterNote(app, li));
                                //app.MAN.notifyDataSetChanged();
                            }
                        }
                    });
                    //notifyDataSet(activity);
                }
            }
        });

        td.setName("BGNoteLoad");
        td.start();
    }

    public void SortAndSave() {
        // Background Save
        Thread td = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (LockThread) {
                    int orderBy = Integer.parseInt(app.SPSettings.getString("orderBy", "0"));
                    SortLists(app, orderBy);
                    try {
                        SaveToFile();
                    } catch (IOException ignored) {
                    }
                    //notifyDataSet(activity);
                }
            }
        });

        td.setName("BGNoteSave");
        td.start();
    }

/*
    private void notifyDataSet(Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean sharedListMode = app.SPSettings.getBoolean("sharedListMode", false);
                if (sharedListMode) {
                    app.MASN.notifyDataSetChanged();
                } else {
                    app.MAN.notifyDataSetChanged();
                }
            }
        });
    }
*/

    public Map<String, Note> GetNoteListItemInverted(int index) {
        int size = NoteList.size();
        if (size > 0)
            return new HashMap<>(NoteList.get((size - 1) - index));
        else
            return null;
    }

    public Map<String, Note> GetSharedNoteListItemInverted(int index) {
        int size = GetSharedNoteList().size();
        if (size > 0)
            return new HashMap<>(GetSharedNoteList().get((size - 1) - index));
        else
            return null;
    }

    private void SortLists(Application app, int orderBy) {
        switch (orderBy) {
            case OrderBy.DATE: {
                SortListDate(app);
                break;
            }
            case OrderBy.TYPE_TITLE: {
                SortListTypeAndTitle(app);
                break;
            }
            default:
                break;
        }
    }

    private void SortListTypeAndTitle(Application app) {
        // Sort by Title first then by Type
        Collections.sort(this.NoteList, new MyComparatorNoteTitle(app));
        Collections.sort(this.NoteList, new MyComparatorNoteType(app));
        Collections.sort(this.SharedNoteList, new MyComparatorSharedNoteTitle(app));
        Collections.sort(this.SharedNoteList, new MyComparatorSharedNoteType(app));
    }

    private void SortListDate(Application app) {
        // Sort by Date
        Collections.sort(this.NoteList, new MyComparatorNote(app));
        Collections.sort(this.SharedNoteList, new MyComparatorSharedNote(app));
    }

    /* ADD AND EDIT NOTES */

    /* TEXT NOTE */

    public boolean AddTextNote(String Title, String Text, boolean Shared, List<Integer> Users) {
        // int ID, String title, User author, Date creationDate, boolean shared, List<User> users, String text
        if (!Title.isEmpty() && !Text.isEmpty()) {
            int NOTE_ID = app.SPUserPref.getInt("noteID", 0);
            Map<String, Note> aux = new HashMap<>();
            if (!Shared) {
                aux.put(NoteKeyVal, new TextNote(NOTE_ID, Title, app.AuthenticatedUser, new Date(), false, null, Text));
                NoteList.add(aux);
            } else {
                aux.put(SharedNoteKeyVal, new TextNote(NOTE_ID, Title, app.AuthenticatedUser, new Date(), true, Users, Text));
                SharedNoteList.add(aux);
            }
            NOTE_ID++;
            app.SPUserPref.edit().putInt("noteID", NOTE_ID).apply();

            SortAndSave();
            /*
            String orderBy = app.SPSettings.getString("orderBy", "0");
            SortLists(app, Integer.parseInt(orderBy));
            try {
                SaveToFile();
            } catch (IOException ignored) { }
            */
            return true;
        }
        return false;
    }

    public boolean EditTextNote(int index, String Title, String Text, boolean Shared, List<Integer> Users, Note note) {
        if (!Title.isEmpty() && !Text.isEmpty()) {
            note.setTitle(Title);
            ((TextNote)note).setText(Text);
            boolean oldShared = note.isShared();
            if (Shared) {
                note.setShared(true, Users);
            }
            else {
                note.setShared(false, null);
            }
            // User edited the note so the date will change
            note.resetDate();

            // if user changed note's visibility (shared with others or not) do this
            if (oldShared != Shared) {
                if (Shared) {
                    // remove from user's note list into shared list
                    HashMap<String, Note> aux = new HashMap<>();
                    aux.put(SharedNoteKeyVal, GetNoteListItemInverted(index).get(NoteKeyVal));
                    SharedNoteList.add(aux);
                    NoteList.remove(GetNoteListItemInverted(index));
                } else {
                    // remove from shared list into user's note list
                    HashMap<String, Note> aux = new HashMap<>();
                    aux.put(NoteKeyVal, GetSharedNoteListItemInverted(index).get(SharedNoteKeyVal));
                    NoteList.add(aux);
                    SharedNoteList.remove(GetSharedNoteListItemInverted(index));
                }
            }

            SortAndSave();
            return true;
        }
        return false;
    }

    /* END TEXT NOTE */

    /* DRAW NOTE */

    public boolean AddDrawNote(String Title, boolean Shared, List<Integer> Users, List<Point> pointList, int[] colorList, boolean lineStyle) {
        // int ID, String title, User author, Date creationDate, boolean shared, List<Integer> users, List<Point> pointList, int[] colorList, boolean lineStyle
        if (!Title.isEmpty()) {
            int NOTE_ID = app.SPUserPref.getInt("noteID", 0);
            Map<String, Note> aux = new HashMap<>();
            if (!Shared) {
                aux.put(NoteKeyVal, new DrawNote(NOTE_ID, Title, app.AuthenticatedUser, new Date(), false, null, pointList, colorList, lineStyle));
                NoteList.add(aux);
            } else {
                aux.put(SharedNoteKeyVal, new DrawNote(NOTE_ID, Title, app.AuthenticatedUser, new Date(), true, Users, pointList, colorList, lineStyle));
                SharedNoteList.add(aux);
            }
            NOTE_ID++;
            app.SPUserPref.edit().putInt("noteID", NOTE_ID).apply();

            SortAndSave();
            return true;
        }
        return false;
    }

    public boolean EditDrawNote(int index, String Title, boolean Shared, List<Integer> Users, List<Point> pointList, int[] colorList, boolean lineStyle, Note note) {
        if (!Title.isEmpty()) {
            note.setTitle(Title);
            ((DrawNote)note).setPointList(pointList);
            ((DrawNote)note).setColorList(colorList);
            ((DrawNote)note).setLineStyle(lineStyle);
            boolean oldShared = note.isShared();
            if (Shared) {
                note.setShared(true, Users);
            }
            else {
                note.setShared(false, null);
            }
            note.resetDate();

            if (oldShared != Shared) {
                if (Shared) {
                    HashMap<String, Note> aux = new HashMap<>();
                    aux.put(SharedNoteKeyVal, GetNoteListItemInverted(index).get(NoteKeyVal));
                    SharedNoteList.add(aux);
                    NoteList.remove(GetNoteListItemInverted(index));
                } else {
                    HashMap<String, Note> aux = new HashMap<>();
                    aux.put(NoteKeyVal, GetSharedNoteListItemInverted(index).get(SharedNoteKeyVal));
                    NoteList.add(aux);
                    SharedNoteList.remove(GetSharedNoteListItemInverted(index));
                }
            }

            SortAndSave();
            return true;
        }
        return false;
    }

    /* END DRAW NOTE */

    /* IMAGE NOTE */

    public boolean AddImageNote(String Title, boolean Shared, List<Integer> Users, List<Point> pointList, int[] colorList, boolean lineStyle, String ImgUrl) {
        // int ID, String title, User author, Date creationDate, boolean shared, List<Integer> users, List<Point> pointList, int[] colorList, boolean lineStyle, String BGImg
        if (!Title.isEmpty() && !ImgUrl.isEmpty()) {
            int NOTE_ID = app.SPUserPref.getInt("noteID", 0);
            Map<String, Note> aux = new HashMap<>();
            if (!Shared) {
                aux.put(NoteKeyVal, new ImageNote(NOTE_ID, Title, app.AuthenticatedUser, new Date(), false, null, pointList, colorList, lineStyle, ImgUrl));
                NoteList.add(aux);
            } else {
                aux.put(SharedNoteKeyVal, new ImageNote(NOTE_ID, Title, app.AuthenticatedUser, new Date(), true, Users, pointList, colorList, lineStyle, ImgUrl));
                SharedNoteList.add(aux);
            }
            NOTE_ID++;
            app.SPUserPref.edit().putInt("noteID", NOTE_ID).apply();

            SortAndSave();
            return true;
        }
        return false;
    }

    public boolean EditImageNote(int index, String Title, boolean Shared, List<Integer> Users, List<Point> pointList, int[] colorList, boolean lineStyle, String ImgUrl, Note note) {
        if (!Title.isEmpty() && !ImgUrl.isEmpty()) {
            note.setTitle(Title);
            ((DrawNote)note).setPointList(pointList);
            ((DrawNote)note).setColorList(colorList);
            ((DrawNote)note).setLineStyle(lineStyle);
            ((ImageNote)note).setBGImg(ImgUrl);
            boolean oldShared = note.isShared();
            if (Shared) {
                note.setShared(true, Users);
            }
            else {
                note.setShared(false, null);
            }
            note.resetDate();

            if (oldShared != Shared) {
                if (Shared) {
                    HashMap<String, Note> aux = new HashMap<>();
                    aux.put(SharedNoteKeyVal, GetNoteListItemInverted(index).get(NoteKeyVal));
                    SharedNoteList.add(aux);
                    NoteList.remove(GetNoteListItemInverted(index));
                } else {
                    HashMap<String, Note> aux = new HashMap<>();
                    aux.put(NoteKeyVal, GetSharedNoteListItemInverted(index).get(SharedNoteKeyVal));
                    NoteList.add(aux);
                    SharedNoteList.remove(GetSharedNoteListItemInverted(index));
                }
            }

            SortAndSave();
            return true;
        }
        return false;
    }

    /* END IMAGE NOTE */

    /* WEATHER NOTE */

    public boolean AddWeatherNote(String Title, String Text, boolean Shared, List<Integer> Users) {
        // int ID, String title, User author, Date creationDate, boolean shared, List<Integer> users, String text, String weather, String description, String temperature, String pressure, String humidity, String minTemperature, String maxTemperature, String windSpeed, String degree, String city, String country
        if (!Title.isEmpty() && !Text.isEmpty()) {
            int NOTE_ID = app.SPUserPref.getInt("noteID", 0);
            Map<String, Note> aux = new HashMap<>();
            if (!Shared) {
                aux.put(NoteKeyVal, new WeatherNote(NOTE_ID, Title, app.AuthenticatedUser, new Date(), false, null, Text, app.weatherData[0], app.weatherData[1], app.weatherData[2], app.weatherData[3], app.weatherData[4], app.weatherData[5], app.weatherData[6], app.weatherData[7], app.weatherData[8], app.weatherData[9], app.weatherData[10]));
                NoteList.add(aux);
            } else {
                aux.put(SharedNoteKeyVal, new WeatherNote(NOTE_ID, Title, app.AuthenticatedUser, new Date(), true, Users, Text, app.weatherData[0], app.weatherData[1], app.weatherData[2], app.weatherData[3], app.weatherData[4], app.weatherData[5], app.weatherData[6], app.weatherData[7], app.weatherData[8], app.weatherData[9], app.weatherData[10]));
                SharedNoteList.add(aux);
            }
            NOTE_ID++;
            app.SPUserPref.edit().putInt("noteID", NOTE_ID).apply();

            SortAndSave();
            return true;
        }
        return false;
    }

    public boolean EditWeatherNote(int index, String Title, String Text, boolean Shared, List<Integer> Users, Note note) {
        if (!Title.isEmpty() && !Text.isEmpty()) {
            note.setTitle(Title);
            ((WeatherNote)note).setText(Text);
            boolean oldShared = note.isShared();
            if (Shared) {
                note.setShared(true, Users);
            }
            else {
                note.setShared(false, null);
            }
            // User edited the note so the date will change
            note.resetDate();

            // if user changed note's visibility (shared with others or not) do this
            if (oldShared != Shared) {
                if (Shared) {
                    // remove from user's note list into shared list
                    HashMap<String, Note> aux = new HashMap<>();
                    aux.put(SharedNoteKeyVal, GetNoteListItemInverted(index).get(NoteKeyVal));
                    SharedNoteList.add(aux);
                    NoteList.remove(GetNoteListItemInverted(index));
                } else {
                    // remove from shared list into user's note list
                    HashMap<String, Note> aux = new HashMap<>();
                    aux.put(NoteKeyVal, GetSharedNoteListItemInverted(index).get(SharedNoteKeyVal));
                    NoteList.add(aux);
                    SharedNoteList.remove(GetSharedNoteListItemInverted(index));
                }
            }

            SortAndSave();
            return true;
        }
        return false;
    }

    /* END WEATHER NOTE */

    /* GEOLOCATION NOTE */

    public boolean AddGeolocationNote(String Title, boolean Shared, List<Integer> Users, String folderUrl, double lat, double lng, List<String> imgs) {
        // int ID, String title, User author, Date creationDate, boolean shared, List<Integer> users, String folderUrl, double lat, double lng
        if (!Title.isEmpty()) {
            int NOTE_ID = app.SPUserPref.getInt("noteID", 0);
            Map<String, Note> aux = new HashMap<>();
            if (!Shared) {
                aux.put(NoteKeyVal, new GeolocationNote(NOTE_ID, Title, app.AuthenticatedUser, new Date(), false, null, folderUrl, lat, lng));
                for (String str : imgs) {
                    try {
                        ((GeolocationNote) aux.get(NoteKeyVal)).AddImg(new File(str));
                    } catch (IOException ignored) {
                    }
                }
                NoteList.add(aux);
            } else {
                aux.put(SharedNoteKeyVal, new GeolocationNote(NOTE_ID, Title, app.AuthenticatedUser, new Date(), true, Users, folderUrl, lat, lng));
                for (String str : imgs) {
                    try {
                        ((GeolocationNote) aux.get(SharedNoteKeyVal)).AddImg(new File(str));
                    } catch (IOException ignored) {
                    }
                }
                SharedNoteList.add(aux);
            }
            NOTE_ID++;
            app.SPUserPref.edit().putInt("noteID", NOTE_ID).apply();

            SortAndSave();
            return true;
        }
        return false;
    }

    public boolean EditGeolocationNote(int index, String Title, boolean Shared, List<Integer> Users, Note note, List<String> imgs) {
        if (!Title.isEmpty()) {
            note.setTitle(Title);
            boolean oldShared = note.isShared();
            if (Shared) {
                note.setShared(true, Users);
            }
            else {
                note.setShared(false, null);
            }
            // User edited the note so the date will change
            note.resetDate();

            for (String str : ((GeolocationNote)note).getImgUrl()) {
                if (!imgs.contains(str)) {
                    // removing files that are no longer in the list
                    ((GeolocationNote)note).RemoveImg(str);
                }
            }

            for (String str : imgs) {
                if (!((GeolocationNote)note).getImgUrl().contains(str)) {
                    try {
                        ((GeolocationNote)note).AddImg(new File(str));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            // if user changed note's visibility (shared with others or not) do this
            if (oldShared != Shared) {
                if (Shared) {
                    // remove from user's note list into shared list
                    HashMap<String, Note> aux = new HashMap<>();
                    aux.put(SharedNoteKeyVal, GetNoteListItemInverted(index).get(NoteKeyVal));
                    SharedNoteList.add(aux);
                    NoteList.remove(GetNoteListItemInverted(index));
                } else {
                    // remove from shared list into user's note list
                    HashMap<String, Note> aux = new HashMap<>();
                    aux.put(NoteKeyVal, GetSharedNoteListItemInverted(index).get(SharedNoteKeyVal));
                    NoteList.add(aux);
                    SharedNoteList.remove(GetSharedNoteListItemInverted(index));
                }
            }

            SortAndSave();
            return true;
        }
        return false;
    }

    /* END GEOLOCATION NOTE */

    /* END ADD AND EDIT NOTES */

    public boolean RemoveNote(int index, User user) {
        try {
            if (!app.SPSettings.getBoolean("sharedListMode", false)) {
                Map<String, Note> temp = GetNoteListItemInverted(index);
                if (temp.get(NoteKeyVal) instanceof ImageNote) {
                    File fileToDel = new File(((ImageNote) temp.get(NoteKeyVal)).getBGImg());
                    if (fileToDel.exists())
                        fileToDel.delete();
                } else if (temp.get(NoteKeyVal) instanceof GeolocationNote) {
                    File fileToDel = new File(((GeolocationNote) temp.get(NoteKeyVal)).getFolderUrl());
                    if (fileToDel.exists()) {
                        for (File f : fileToDel.listFiles())
                            f.delete();
                        fileToDel.delete();
                    }
                }
                NoteList.remove(temp);
            }
            else {
                if (GetSharedNoteListItemInverted(index).get(SharedNoteKeyVal).getAuthor() == (user.GetUserID())) {
                    Map<String, Note> temp = GetSharedNoteListItemInverted(index);
                    // Delete img file from imagenote
                    if (temp.get(SharedNoteKeyVal) instanceof ImageNote) {
                        File fileToDel = new File(((ImageNote) temp.get(SharedNoteKeyVal)).getBGImg());
                        if (fileToDel.exists())
                            fileToDel.delete();
                    } else if (temp.get(SharedNoteKeyVal) instanceof GeolocationNote) {
                        File fileToDel = new File(((GeolocationNote) temp.get(SharedNoteKeyVal)).getFolderUrl());
                        if (fileToDel.exists()) {
                            for (File f : fileToDel.listFiles())
                                f.delete();
                            fileToDel.delete();
                        }
                    }
                    SharedNoteList.remove(temp);
                } else {
                    return false;
                }
            }

            SortAndSave();
            /*
            String orderBy = app.SPSettings.getString("orderBy", "0");
            SortLists(app, Integer.parseInt(orderBy));
            try {
                SaveToFile();
            } catch (IOException ignored) { }
            */
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void RemoveSharedNotes() {
        // Remove notes of the user that no longer exists
        for (int i = app.ListNotes.SharedNoteList.size() - 1; i >= 0; i--) {
            int _LISTUSERID = app.ListNotes.SharedNoteList.get(i).get(app.ListNotes.SharedNoteKeyVal).getAuthor();
            int count = 0;
            for (int j = 0; j < app.ListUser.GetUserList().size(); j++) {
                int _USERID = app.ListUser.GetUserList().get(j).get(app.ListUser.UserKeyVal).GetUserID();
                if (_LISTUSERID == _USERID) {
                    count++;
                    break;
                }
            }
            if (count == 0) {
                app.ListNotes.SharedNoteList.remove(i);
            }
        }
    }

    private void SaveToFile() throws IOException {
        try {
            FileOutputStream fOS = app.openFileOutput("notes_" + app.AuthenticatedUser.GetUserID() + ".bin", Context.MODE_PRIVATE);
            ObjectOutputStream oOS = new ObjectOutputStream(fOS);

            oOS.writeObject(NoteList);

            oOS.close();
        } catch (Exception ignore) { }

        FileOutputStream fOS2 = app.openFileOutput("sharedNotes.bin", Context.MODE_PRIVATE);
        ObjectOutputStream oOS2 = new ObjectOutputStream(fOS2);

        oOS2.writeObject(SharedNoteList);

        oOS2.close();
    }

    private void LoadFromFile() throws IOException, ClassNotFoundException {
        try {
            NoteList = new ArrayList<>();
            FileInputStream fIS = app.openFileInput("notes_" + app.AuthenticatedUser.GetUserID() + ".bin");
            ObjectInputStream oIS = new ObjectInputStream(fIS);

            NoteList = (List<Map<String, Note>>) oIS.readObject();

            oIS.close();
        } catch (ClassNotFoundException e) {
            throw new ClassCastException();
        } catch (Exception ignore) { }

        SharedNoteList = new ArrayList<>();
        FileInputStream fIS2 = app.openFileInput("sharedNotes.bin");
        ObjectInputStream oIS2 = new ObjectInputStream(fIS2);

        SharedNoteList = (List<Map<String, Note>>) oIS2.readObject();

        oIS2.close();
    }

    public NoteList(Application app) {
        NoteList = new ArrayList<>();
        SharedNoteList = new ArrayList<>();
        this.app = (MyApp)app;
    }
}
