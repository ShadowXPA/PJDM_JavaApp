package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class DrawArea extends View implements GestureDetector.OnGestureListener {
    // Draw Area, Area to draw lines...

    private GestureDetector GD;
    private List<Point> pointList;
    private Paint paint;
    private boolean noteOwner;
    private String BGImg;

    public List<Point> getPointList() {
        return new ArrayList<>(pointList);
    }

    public String getBGImg() {
        return BGImg;
    }

    public void resetPointList() {
        this.pointList = new ArrayList<>();
    }

    public DrawArea(Context context, int[] ARGB, List<Point> pointList, boolean lineStyle, boolean noteOwner, String BGImg) {
        super(context);
        GD = new GestureDetector(context, this);
        if (pointList == null) {
            this.pointList = new ArrayList<>();
        } else {
            this.pointList = new ArrayList<>(pointList);
        }

        paint = new Paint(Paint.DITHER_FLAG);
        paint.setColor(Color.argb(ARGB[0], ARGB[1], ARGB[2], ARGB[3]));
        paint.setStrokeWidth(3.0f);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        ///*
        if (lineStyle) {
            paint.setPathEffect(new DashPathEffect(new float[] {10, 14}, 0));
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        //*/

        this.noteOwner = noteOwner;

        if (BGImg != null)
            this.BGImg = BGImg;
    }

    public DrawArea(Context context) {
        this(context, new int[] { 255, 0, 0, 0 }, null, true, true, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < pointList.size() - 1; i++) {
            if (pointList.get(i) != null && pointList.get(i + 1) != null)
                canvas.drawLine(pointList.get(i).getX(), pointList.get(i).getY(), pointList.get(i + 1).getX(), pointList.get(i + 1).getY(), paint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (BGImg != null) {
            PNUtil.setPic(this, BGImg);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!GD.onTouchEvent(event))
            return super.onTouchEvent(event);

        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (noteOwner)
            pointList.add(null);
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) { }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (noteOwner)
            pointList.add(new Point(e2.getX(), e2.getY()));
        this.invalidate();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) { }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
