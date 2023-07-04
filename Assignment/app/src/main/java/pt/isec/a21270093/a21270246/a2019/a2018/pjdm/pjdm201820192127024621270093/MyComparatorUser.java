package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import android.app.Application;

import java.util.Comparator;
import java.util.Map;

public class MyComparatorUser implements Comparator<Map<String, User>> {
    private MyApp app;

    public MyComparatorUser(Application app) {
        this.app = (MyApp)app;
    }

    @Override
    public int compare(Map<String, User> o1, Map<String, User> o2) {
        User u1 = o1.get(app.ListUser.UserKeyVal);
        User u2 = o2.get(app.ListUser.UserKeyVal);

        if (u1.getDate() == null || u2.getDate() == null)
            return -1;

        return u1.getDate().compareTo(u2.getDate());
    }
}
