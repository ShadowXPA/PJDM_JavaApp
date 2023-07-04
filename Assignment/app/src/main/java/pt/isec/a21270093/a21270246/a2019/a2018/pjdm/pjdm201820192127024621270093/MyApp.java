package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import android.app.Application;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyApp extends Application {
    // Note List
    public NoteList ListNotes = new NoteList(this);
    // User List
    public UserList ListUser = new UserList(this);
    // Saves next User ID and next Note ID
    public SharedPreferences SPUserPref;
    // PreferenceScreen SharedPreferences, Saves Shared List Mode, Order By, and Metric System
    public SharedPreferences SPSettings;
    // Authenticated User...
    public User AuthenticatedUser;
    // Temporary Shared Users
    public List<Integer> SharedUsers;
    public PNUtil pn;
    // Temporary Weather Data
    public String[] weatherData;

}
