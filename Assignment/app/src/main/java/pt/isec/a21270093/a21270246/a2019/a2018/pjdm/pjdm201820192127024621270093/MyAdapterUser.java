package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class MyAdapterUser extends BaseAdapter {
    private MyApp app;
    private LayoutInflater li;

    public MyAdapterUser(Application app, LayoutInflater li) {
        this.app = (MyApp)app;
        this.li = li;
    }

    @Override
    public int getCount() {
        return app.ListUser.GetUserList().size();
    }

    @Override
    public Object getItem(int position) {
        return app.ListUser.GetUserListItemInverted(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            Map<String, User> item = (HashMap<String, User>) getItem(position);
            if (item != null) {

                if (convertView == null) {
                    convertView = li.inflate(R.layout.userlistview_layout, parent, false);
                }

                String UserTitle;
                String UserDescription;

                try {
                    UserTitle = item.get(app.ListUser.UserKeyVal).getUserTitle();
                    UserDescription = item.get(app.ListUser.UserKeyVal).getUserDescription();
                } catch (Exception e) {
                    UserTitle = app.getResources().getString(R.string.username_error);
                    UserDescription = app.getResources().getString(R.string.userdescription_error);
                }

                TextView tv1 = convertView.findViewById(R.id.LVUserText1);
                TextView tv2 = convertView.findViewById(R.id.LVUserText2);

                tv1.setText(UserTitle);
                tv2.setText(UserDescription);

                return convertView;
            }
        } catch (Exception ignored) { }
        return null;
    }
}
