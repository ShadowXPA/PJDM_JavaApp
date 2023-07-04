package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import java.util.Date;
import java.util.List;

public class TextNote extends Note {
    private String Text;

    public String getText() {
        return Text;
    }

    public boolean setText(String text) {
        if (!text.isEmpty()) {
            this.Text = text;
            return true;
        } else {
            return false;
        }
    }

    public TextNote(int ID, String title, User author, Date creationDate, boolean shared, List<Integer> users, String text) {
        super(ID, title, author, creationDate, shared, users);
        Text = text;
    }

    @Override
    public String compareType() {
        return NoteType.TEXT_NOTE;
    }

}
