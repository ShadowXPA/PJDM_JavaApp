package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

public class CreateAccountActivity extends Activity {
    MyApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        app = (MyApp)getApplication();
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
                EditText et1 = findViewById(R.id.ACAUsername);
                EditText et2 = findViewById(R.id.ACAPassword);
                String Username = et1.getText().toString().trim();
                String Password = et2.getText().toString();
                boolean strChecks = true;
                if (!(Username.length() >= 3 && Username.length() <= 22)) {
                    findViewById(R.id.LLACAUsername).setBackgroundColor(Color.RED);
                    Toast.makeText(CreateAccountActivity.this, R.string.username_length_error, Toast.LENGTH_LONG).show();
                    strChecks = false;
                } else {
                    findViewById(R.id.LLACAUsername).setBackgroundColor(getResources().getColor(R.color.lightgray));
                }
                if (!(Password.length() >= 4 && Password.length() <= 22)) {
                    findViewById(R.id.LLACAPassword).setBackgroundColor(Color.RED);
                    Toast.makeText(CreateAccountActivity.this, R.string.password_length_error, Toast.LENGTH_LONG).show();
                    strChecks = false;
                } else {
                    findViewById(R.id.LLACAPassword).setBackgroundColor(getResources().getColor(R.color.lightgray));
                }

                if (strChecks && app.ListUser.AddUser(Username, Password, false)) {
                    finish();
                } else {
                    if (strChecks)
                        Toast.makeText(CreateAccountActivity.this, R.string.new_acc_error, Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
