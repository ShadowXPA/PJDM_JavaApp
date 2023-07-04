package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
    private MyApp app;
    ListView LVMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (MyApp)getApplication();
        app.pn = new PNUtil(app);
        app.SPUserPref = getSharedPreferences("userPref", MODE_PRIVATE);
        app.SPSettings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        LVMain = findViewById(R.id.LVMain);
        LVMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final LinearLayout LILLL = (LinearLayout)getLayoutInflater().inflate(R.layout.login_layout, (ViewGroup) findViewById(R.id.LLMain), false);
                String str = getResources().getString(R.string.logging_in) + " '" + ((TextView)view.findViewById(R.id.LVUserText1)).getText().toString() + "'";

                ((TextView)LILLL.findViewById(R.id.LILText)).setText(str);

                new AlertDialog.Builder(MainActivity.this)
                        .setCancelable(false)
                        .setView(LILLL)
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(R.string.logIn, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String Password = ((EditText)LILLL.findViewById(R.id.LILPasswordField)).getText().toString();
                                if (app.ListUser.GetUserListItemInverted(position).get(app.ListUser.UserKeyVal).AuthenticateUser(Password)) {
                                    app.AuthenticatedUser = app.ListUser.GetUserListItemInverted(position).get(app.ListUser.UserKeyVal);
                                    Intent intent = new Intent(MainActivity.this, UserNotesActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(MainActivity.this, R.string.login_error, Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNeutralButton(R.string.edit, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String Password = ((EditText)LILLL.findViewById(R.id.LILPasswordField)).getText().toString();
                                if (app.ListUser.GetUserListItemInverted(position).get(app.ListUser.UserKeyVal).AuthenticateUser(Password)) {
                                    app.AuthenticatedUser = app.ListUser.GetUserListItemInverted(position).get(app.ListUser.UserKeyVal);
                                    Intent intent = new Intent(MainActivity.this, EditAccountActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(MainActivity.this, R.string.login_error, Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .show();
            }
        });
        LVMain.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                String strDelete = getResources().getString(R.string.deleting) + " '" + ((TextView)view.findViewById(R.id.LVUserText1)).getText().toString() + "'";

                final LinearLayout Dulll = (LinearLayout) getLayoutInflater().inflate(R.layout.deleting_user_layout, (ViewGroup) findViewById(R.id.LLMain), false);
                ((TextView)Dulll.findViewById(R.id.DULDeletingMsg)).setText(strDelete);

                new AlertDialog.Builder(MainActivity.this)
                        .setCancelable(false)
                        .setView(Dulll)
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (app.ListUser.RemoveUser(position, ((EditText)Dulll.findViewById(R.id.DULPasswordField)).getText().toString())) {
                                    LVMain.setAdapter(new MyAdapterUser(app, getLayoutInflater()));
                                } else {
                                    Toast.makeText(MainActivity.this, R.string.userNotDeleted_error, Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .show();

                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        app.ListUser.LoadFromFileAsync(LVMain, getLayoutInflater());
        //LVMain.setAdapter(app.MAU);
    }

    @Override
    protected void onStop() {
        super.onStop();
        app.ListUser.SortAndSave();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            MenuInflater mi = getMenuInflater();
            mi.inflate(R.menu.main_menu, menu);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.MMAbout:
                final LinearLayout ALLL = (LinearLayout)getLayoutInflater().inflate(R.layout.about_layout, (ViewGroup) findViewById(R.id.LLMain), false);

                new AlertDialog.Builder(MainActivity.this)
                        .setCancelable(false)
                        .setView(ALLL)
                        .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

                return true;
            case R.id.MMNewAcc:
                Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
