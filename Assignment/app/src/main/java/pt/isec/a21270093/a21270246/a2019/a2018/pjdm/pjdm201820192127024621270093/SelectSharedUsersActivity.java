package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class SelectSharedUsersActivity extends Activity {
    private MyApp app;
    private ListView ASSULVUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_shared_users);
        app = (MyApp)getApplication();

        ASSULVUsers = findViewById(R.id.ASSULVUsers);
        ASSULVUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox cb = view.findViewById(R.id.SULShared);
                boolean checked = cb.isChecked();

                if (checked) {
                    cb.setChecked(false);
                    app.SharedUsers.remove(app.ListUser.GetUserInverted(position).GetUserID());
                } else {
                    cb.setChecked(true);
                    if (!app.SharedUsers.contains(app.ListUser.GetUserInverted(position).GetUserID()))
                        app.SharedUsers.add(app.ListUser.GetUserInverted(position).GetUserID());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ASSULVUsers.setAdapter(new MyAdapterSharedUser(app, getLayoutInflater()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            MenuInflater mi = getMenuInflater();
            mi.inflate(R.menu.create_edit_user_menu, menu);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.CEUSave:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
