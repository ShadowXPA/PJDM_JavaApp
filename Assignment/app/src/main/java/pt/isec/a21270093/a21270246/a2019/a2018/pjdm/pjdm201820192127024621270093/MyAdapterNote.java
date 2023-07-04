package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class MyAdapterNote extends BaseAdapter {
    private MyApp app;
    private LayoutInflater li;

    public MyAdapterNote(Application app, LayoutInflater li) {
        this.app = (MyApp)app;
        this.li = li;
    }

    @Override
    public int getCount() {
        return app.ListNotes.GetNoteList().size();
    }

    @Override
    public Object getItem(int position) {
        return app.ListNotes.GetNoteListItemInverted(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            Map<String, Note> item = (HashMap<String, Note>) getItem(position);
            if (item != null) {

                if (convertView == null) {
                    convertView = li.inflate(R.layout.listview_layout, parent, false);
                }

                String NoteTitle;
                String NoteDescription;

                try {
                    NoteTitle = item.get(app.ListNotes.NoteKeyVal).getTitle();
                    NoteDescription = item.get(app.ListNotes.NoteKeyVal).getNoteDescription();
                } catch (Exception e) {
                    NoteTitle = app.getResources().getString(R.string.notetitle_error);
                    NoteDescription = app.getResources().getString(R.string.notedescription_error);
                }

                TextView tv1 = convertView.findViewById(R.id.LVText1);
                TextView tv2 = convertView.findViewById(R.id.LVText2);
                ImageView iv = convertView.findViewById(R.id.LVImage);
                Note nt = item.get(app.ListNotes.NoteKeyVal);

                tv1.setText(NoteTitle);
                tv2.setText(NoteDescription);
                if (nt instanceof TextNote && !(nt instanceof WeatherNote)) {
                    iv.setImageResource(android.R.drawable.ic_menu_edit);
                } else if (nt instanceof DrawNote && !(nt instanceof ImageNote)) {
                    iv.setImageResource(android.R.drawable.ic_menu_gallery);
                } else if (nt instanceof ImageNote) {
                    iv.setImageResource(android.R.drawable.ic_menu_camera);
                } else if (nt instanceof WeatherNote) {
                    iv.setImageResource(android.R.drawable.ic_menu_compass);
                } else if (nt instanceof GeolocationNote) {
                    iv.setImageResource(android.R.drawable.ic_menu_mylocation);
                } else {
                    iv.setImageResource(android.R.drawable.ic_dialog_alert);
                }

                return convertView;
            }
        } catch (Exception ignored) { }
        return null;
    }
}
