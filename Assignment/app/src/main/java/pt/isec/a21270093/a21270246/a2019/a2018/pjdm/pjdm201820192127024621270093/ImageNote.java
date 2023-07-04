package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import java.util.Date;
import java.util.List;

public class ImageNote extends DrawNote {
    private String BGImg;

    public ImageNote(int ID, String title, User author, Date creationDate, boolean shared, List<Integer> users, List<Point> pointList, int[] colorList, boolean lineStyle, String BGImg) {
        super(ID, title, author, creationDate, shared, users, pointList, colorList, lineStyle);
        this.BGImg = BGImg;
    }

    public String getBGImg() {
        return BGImg;
    }

    public void setBGImg(String BGImg) {
        this.BGImg = BGImg;
    }

    @Override
    public String compareType() {
        return NoteType.IMAGE_NOTE;
    }
}
