package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DrawNote extends Note {
    // Note Class to save note data...

    private List<Point> PointList;
    // 0-3 = BgColor - 4-7 = LineColor
    private int[] ColorList;
    // True = Dashed - False = Solid
    private boolean LineStyle;

    public List<Point> getPointList() {
        return new ArrayList<>(PointList);
    }

    public void setPointList(List<Point> pointList) {
        if (pointList != null)
            PointList = new ArrayList<>(pointList);
        else
            PointList = new ArrayList<>();
    }

    public int[] getColorList() {
        return ColorList;
    }

    public void setColorList(int[] colorList) {
        if (colorList != null && colorList.length == 8)
            ColorList = new int[] { colorList[0], colorList[1], colorList[2], colorList[3], colorList[4], colorList[5], colorList[6], colorList[7] };
        else
            ColorList = new int[] { 255, 255, 255, 255, 0, 0, 0, 255 };
    }

    public boolean isDashed() {
        return LineStyle;
    }

    public void setLineStyle(boolean lineStyle) {
        this.LineStyle = lineStyle;
    }

    @Override
    public String compareType() {
        return NoteType.DRAW_NOTE;
    }

    public DrawNote(int ID, String title, User author, Date creationDate, boolean shared, List<Integer> users, List<Point> pointList, int[] colorList, boolean lineStyle) {
        super(ID, title, author, creationDate, shared, users);
        if (pointList != null)
            PointList = new ArrayList<>(pointList);
        else
            PointList = new ArrayList<>();
        if (colorList != null && colorList.length == 8)
            ColorList = new int[] { colorList[0], colorList[1], colorList[2], colorList[3], colorList[4], colorList[5], colorList[6], colorList[7] };
        else
            // Default values
            ColorList = new int[] { 255, 255, 255, 255, 0, 0, 0, 255 };
        LineStyle = lineStyle;
    }
}
