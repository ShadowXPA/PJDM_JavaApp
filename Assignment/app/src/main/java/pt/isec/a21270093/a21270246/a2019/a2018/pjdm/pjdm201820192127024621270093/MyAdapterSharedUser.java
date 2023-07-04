package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.HashMap;

public class MyAdapterSharedUser extends BaseAdapter {
    private MyApp app;
    private LayoutInflater li;

    public MyAdapterSharedUser(Application app, LayoutInflater li) {
        this.app = (MyApp)app;
        this.li = li;
    }

    @Override
    public int getCount() {
        return app.ListUser.GetUserListButSelfInverted().size() > 0 ? app.ListUser.GetUserListButSelfInverted().size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return app.ListUser.GetUserInverted(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            User item = (User)getItem(position);
            if (item != null && item.GetUserID() != app.AuthenticatedUser.GetUserID()) {

                if (convertView == null) {
                    convertView = li.inflate(R.layout.share_user_layout, parent, false);
                }

                String UserTitle = item.getUserTitle();

                TextView tv1 = convertView.findViewById(R.id.SULUsername);

                tv1.setText(UserTitle);

                boolean checked = app.SharedUsers.contains(item.GetUserID());

                ((CheckBox)convertView.findViewById(R.id.SULShared)).setChecked(checked);

                return convertView;
            }
        } catch (Exception ignored) { }
        return null;
    }
}
