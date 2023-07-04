package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import android.app.Application;

import java.util.Comparator;
import java.util.Map;

public class MyComparatorSharedNoteTitle implements Comparator<Map<String, Note>> {
    private MyApp app;

    public MyComparatorSharedNoteTitle(Application app) {
        this.app = (MyApp)app;
    }

    @Override
    public int compare(Map<String, Note> o1, Map<String, Note> o2) {
        Note n1 = o1.get(app.ListNotes.SharedNoteKeyVal);
        Note n2 = o2.get(app.ListNotes.SharedNoteKeyVal);

        if(n1.getTitle() == null || n2.getTitle() == null)
            return 1;

        return -n1.getTitle().compareTo(n2.getTitle());
    }
}
