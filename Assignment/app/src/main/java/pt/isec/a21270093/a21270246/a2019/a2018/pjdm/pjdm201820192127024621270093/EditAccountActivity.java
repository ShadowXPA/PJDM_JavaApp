package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class EditAccountActivity extends Activity {
    MyApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        app = (MyApp)getApplication();

        ((EditText)findViewById(R.id.AEAUsername)).setText(app.AuthenticatedUser.getUsername());
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
                EditText etUserName = findViewById(R.id.AEAUsername);
                EditText etOldPass = findViewById(R.id.AEAOldPassword);
                EditText etPass = findViewById(R.id.AEAPassword);
                String Username = etUserName.getText().toString().trim();
                String OldPassword = etOldPass.getText().toString();
                String Password = etPass.getText().toString();
                boolean strChecks = true;
                if (Username.length() < 3 || Username.length() > 22) {
                    findViewById(R.id.LLAEAUsername).setBackgroundColor(Color.RED);
                    Toast.makeText(EditAccountActivity.this, R.string.username_length_error, Toast.LENGTH_LONG).show();
                    strChecks = false;
                } else {
                    findViewById(R.id.LLAEAUsername).setBackgroundColor(getResources().getColor(R.color.lightgray));
                }
                if (Password.length() < 4 || Password.length() > 22) {
                    findViewById(R.id.LLAEAPassword).setBackgroundColor(Color.RED);
                    Toast.makeText(EditAccountActivity.this, R.string.password_length_error, Toast.LENGTH_LONG).show();
                    strChecks = false;
                } else {
                    findViewById(R.id.LLAEAPassword).setBackgroundColor(getResources().getColor(R.color.lightgray));
                }
                if (!app.AuthenticatedUser.AuthenticateUser(OldPassword)) {
                    findViewById(R.id.LLAEAOldPassword).setBackgroundColor(Color.RED);
                    Toast.makeText(EditAccountActivity.this, R.string.not_authenticated, Toast.LENGTH_LONG).show();
                    strChecks = false;
                } else {
                    findViewById(R.id.LLAEAOldPassword).setBackgroundColor(getResources().getColor(R.color.lightgray));
                }

                if (strChecks && app.ListUser.EditUser(Username, OldPassword, Password, app.AuthenticatedUser)) {
                    finish();
                } else {
                    if (strChecks)
                        Toast.makeText(EditAccountActivity.this, R.string.edit_acc_error, Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
