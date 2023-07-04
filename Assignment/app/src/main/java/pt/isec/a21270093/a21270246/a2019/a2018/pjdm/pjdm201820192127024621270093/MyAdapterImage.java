package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

public class MyAdapterImage extends BaseAdapter {
    // Used for the GridView

    private LayoutInflater li;
    private List<String> imgs;

    public MyAdapterImage(List<String> imgs, LayoutInflater li) {
        this.li = li;
        this.imgs = imgs;
    }

    @Override
    public int getCount() {
        return imgs.size();
    }

    @Override
    public Object getItem(int position) {
        return imgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            String item = (String)getItem(position);
            if (item != null) {
                if (convertView == null) {
                    convertView = li.inflate(R.layout.photos, parent, false);
                }

                try {
                    ImageView iv = convertView.findViewById(R.id.ImgPhoto);

                    // Get the dimensions of the View
                    int targetW = 100;
                    int targetH = 100;

                    // Get the dimensions of the bitmap
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(item, bmOptions);
                    int photoW = bmOptions.outWidth;
                    int photoH = bmOptions.outHeight;

                    // Determine how much to scale down the image
                    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

                    // Decode the image file into a Bitmap sized to fill the View
                    bmOptions.inJustDecodeBounds = false;
                    bmOptions.inSampleSize = scaleFactor;
                    bmOptions.inPurgeable = true;

                    Bitmap bitmap = BitmapFactory.decodeFile(item, bmOptions);
                    iv.setBackgroundDrawable(new BitmapDrawable(bitmap));
                } catch (Exception ignored) { }

                return convertView;
            }
        } catch (Exception ignored) { }
        return null;
    }
}
