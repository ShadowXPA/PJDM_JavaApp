package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import android.app.Application;

import java.util.Comparator;
import java.util.Map;

public class MyComparatorNoteType implements Comparator<Map<String, Note>> {
    private MyApp app;

    public MyComparatorNoteType(Application app) {
        this.app = (MyApp)app;
    }

    @Override
    public int compare(Map<String, Note> o1, Map<String, Note> o2) {
        Note n1 = o1.get(app.ListNotes.NoteKeyVal);
        Note n2 = o2.get(app.ListNotes.NoteKeyVal);

        if (n1 == null || n2 == null)
            return -1;

        return n1.compareType().compareTo(n2.compareType());
    }
}
