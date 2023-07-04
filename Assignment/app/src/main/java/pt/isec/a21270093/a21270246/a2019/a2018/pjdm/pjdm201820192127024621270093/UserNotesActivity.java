package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class UserNotesActivity extends Activity {
    private MyApp app;
    ListView LVNotesMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_notes);
        app = (MyApp)getApplication();

        LVNotesMain = findViewById(R.id.LVNotesMain);
        LVNotesMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                app.SharedUsers = new ArrayList<>();
                Note editingNote;
                boolean sharedListMode = app.SPSettings.getBoolean("sharedListMode", false);

                if (sharedListMode) {
                    editingNote = app.ListNotes.GetSharedNoteListItemInverted(position).get(app.ListNotes.SharedNoteKeyVal);
                } else {
                    editingNote = app.ListNotes.GetNoteListItemInverted(position).get(app.ListNotes.NoteKeyVal);
                }

                if (editingNote instanceof TextNote && !(editingNote instanceof WeatherNote)) {
                    Intent intent = new Intent(UserNotesActivity.this, CreateTextNoteActivity.class);
                    intent.putExtra("editingNote", true);
                    intent.putExtra("index", position);
                    intent.putExtra("sharedNote", sharedListMode);
                    intent.putExtra("noteOwner", editingNote.isAuthor(app.AuthenticatedUser.GetUserID()));
                    startActivity(intent);
                } else if (editingNote instanceof DrawNote && !(editingNote instanceof ImageNote)) {
                    Intent intent = new Intent(UserNotesActivity.this, CreateDrawNoteSetUpActivity.class);
                    intent.putExtra("editingNote", true);
                    intent.putExtra("index", position);
                    intent.putExtra("sharedNote", sharedListMode);
                    intent.putExtra("noteOwner", editingNote.isAuthor(app.AuthenticatedUser.GetUserID()));
                    startActivity(intent);
                } else if (editingNote instanceof ImageNote) {
                    Intent intent = new Intent(UserNotesActivity.this, CreateImageNoteSetUpActivity.class);
                    intent.putExtra("editingNote", true);
                    intent.putExtra("index", position);
                    intent.putExtra("sharedNote", sharedListMode);
                    intent.putExtra("noteOwner", editingNote.isAuthor(app.AuthenticatedUser.GetUserID()));
                    startActivity(intent);
                } else if (editingNote instanceof WeatherNote) {
                    Intent intent = new Intent(UserNotesActivity.this, CreateWeatherNoteActivity.class);
                    intent.putExtra("editingNote", true);
                    intent.putExtra("index", position);
                    intent.putExtra("sharedNote", sharedListMode);
                    intent.putExtra("noteOwner", editingNote.isAuthor(app.AuthenticatedUser.GetUserID()));
                    startActivity(intent);
                } else if (editingNote instanceof GeolocationNote) {
                    Intent intent = new Intent(UserNotesActivity.this, CreateGeolocationActivity.class);
                    intent.putExtra("editingNote", true);
                    intent.putExtra("index", position);
                    intent.putExtra("sharedNote", sharedListMode);
                    intent.putExtra("noteOwner", editingNote.isAuthor(app.AuthenticatedUser.GetUserID()));
                    startActivity(intent);
                } else {
                    Toast.makeText(UserNotesActivity.this, R.string.note_error, Toast.LENGTH_LONG).show();
                }
            }
        });
        LVNotesMain.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                String title = getResources().getString(R.string.deleting) + " '" + ((TextView)view.findViewById(R.id.LVText1)).getText().toString() + "'";

                new AlertDialog.Builder(UserNotesActivity.this)
                        .setCancelable(false)
                        .setTitle(title)
                        .setMessage(R.string.deleteMsgNote)
                        .setIcon(android.R.drawable.ic_delete)
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!app.ListNotes.RemoveNote(position, app.AuthenticatedUser)) {
                                    Toast.makeText(UserNotesActivity.this, R.string.noteNotDeleted_error, Toast.LENGTH_SHORT).show();
                                } else {
                                    boolean sharedListMode = app.SPSettings.getBoolean("sharedListMode", false);
                                    if (sharedListMode) {
                                        LVNotesMain.setAdapter(new MyAdapterSharedNote(app, getLayoutInflater()));
                                    } else {
                                        LVNotesMain.setAdapter(new MyAdapterNote(app, getLayoutInflater()));
                                    }
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
        app.ListNotes.LoadFromFileAsync(LVNotesMain, getLayoutInflater());
        /*
        boolean sharedListMode = app.SPSettings.getBoolean("sharedListMode", false);
        if (sharedListMode) {
            LVNotesMain.setAdapter(app.MASN);
        } else {
            LVNotesMain.setAdapter(app.MAN);
        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        app.ListNotes.SortAndSave();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            MenuInflater mi = getMenuInflater();
            mi.inflate(R.menu.usernotes_menu, menu);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        app.SharedUsers = new ArrayList<>();
        switch (item.getItemId()) {
            case R.id.UNMSettings: {
                Intent intent = new Intent(UserNotesActivity.this, PreferenceScreenActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.UNMTextNote: {
                Intent intent = new Intent(UserNotesActivity.this, CreateTextNoteActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.UNMDrawNote: {
                Intent intent = new Intent(UserNotesActivity.this, CreateDrawNoteSetUpActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.UNMImageNote: {
                Intent intent = new Intent(UserNotesActivity.this, CreateImageNoteSetUpActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.UNMWeatherNote: {
                ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni != null && ni.isConnected()) {
                    Intent intent = new Intent(UserNotesActivity.this, CreateWeatherNoteSetUpActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(UserNotesActivity.this, getResources().getString(R.string.internet_access_error), Toast.LENGTH_LONG).show();
                }
                return true;
            }
            case R.id.UNMGeolocationNote: {
                ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo ni = cm.getActiveNetworkInfo();
                LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                if (ni != null && ni.isConnected()) {
                    if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Intent intent = new Intent(UserNotesActivity.this, CreateGeolocationActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(UserNotesActivity.this, getResources().getString(R.string.location_access_error), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(UserNotesActivity.this, getResources().getString(R.string.internet_access_error), Toast.LENGTH_LONG).show();
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
