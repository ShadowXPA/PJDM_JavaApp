package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CreateTextNoteActivity extends Activity {
    private MyApp app;
    private boolean editingNote;
    private int index;
    private Note tempNote;
    private boolean noteOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_text_note);
        app = (MyApp)getApplication();
        editingNote = getIntent().getBooleanExtra("editingNote", false);
        index = getIntent().getIntExtra("index", 0);
        boolean sharedNote = getIntent().getBooleanExtra("sharedNote", false);

        if (editingNote) {
            noteOwner = getIntent().getBooleanExtra("noteOwner", false);

            if (!noteOwner) {
                findViewById(R.id.ACTNShared).setEnabled(false);
                findViewById(R.id.ACTNTitle).setEnabled(false);
                findViewById(R.id.ACTNDescription).setEnabled(false);
            }

            ((CheckBox)findViewById(R.id.ACTNShared)).setChecked(sharedNote);
            if (sharedNote) {
                tempNote = app.ListNotes.GetSharedNoteListItemInverted(index).get(app.ListNotes.SharedNoteKeyVal);
                if (tempNote != null) {
                    //app.SharedUsers = new ArrayList<>(tempNote.getSharedUsers());
                    List<Integer> aux = new ArrayList<>();
                    int size = app.ListUser.GetUserListButSelfInverted().size();
                    for (Integer i : tempNote.getSharedUsers()) {
                        for (int j = 0; j < size; j++) {
                            if (i.intValue() == app.ListUser.GetUserListButSelfInverted().get((size - 1) - j).GetUserID()) {
                                aux.add(i);
                            }
                        }
                    }
                    app.SharedUsers = new ArrayList<>(aux);
                }
            } else {
                tempNote = app.ListNotes.GetNoteListItemInverted(index).get(app.ListNotes.NoteKeyVal);
            }

            ((EditText)findViewById(R.id.ACTNTitle)).setText(tempNote.getTitle());
            ((EditText)findViewById(R.id.ACTNDescription)).setText(((TextNote)tempNote).getText());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            MenuInflater mi = getMenuInflater();
            mi.inflate(R.menu.create_edit_note_menu, menu);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.CENMShared: {
                CheckBox cbShared = findViewById(R.id.ACTNShared);
                boolean Shared = cbShared.isChecked();

                if (Shared && (!editingNote || noteOwner)) {
                    Intent intent = new Intent(CreateTextNoteActivity.this, SelectSharedUsersActivity.class);
                    startActivity(intent);
                } else {
                    if (editingNote && !noteOwner)
                        Toast.makeText(CreateTextNoteActivity.this, R.string.user_not_owner_error, Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(CreateTextNoteActivity.this, R.string.check_shared_error, Toast.LENGTH_LONG).show();
                }
                return true;
            }
            case R.id.CENMSave: {
                CheckBox cbShared = findViewById(R.id.ACTNShared);
                EditText etTitle = findViewById(R.id.ACTNTitle);
                EditText etDescription = findViewById(R.id.ACTNDescription);
                LinearLayout llTitleField = findViewById(R.id.ACTNTitleField);
                LinearLayout llDescriptionField = findViewById(R.id.ACTNDescriptionField);

                boolean Shared = cbShared.isChecked();
                String Title = etTitle.getText().toString().trim();
                String Description = etDescription.getText().toString().trim();
                boolean check = true;

                if (Title.isEmpty()) {
                    llTitleField.setBackgroundColor(Color.RED);
                    Toast.makeText(CreateTextNoteActivity.this, R.string.title_is_empty_error, Toast.LENGTH_LONG).show();
                    check = false;
                } else {
                    llTitleField.setBackgroundColor(getResources().getColor(R.color.lightgray));
                }

                if (Description.isEmpty()) {
                    llDescriptionField.setBackgroundColor(Color.RED);
                    Toast.makeText(CreateTextNoteActivity.this, R.string.description_is_empty_error, Toast.LENGTH_LONG).show();
                    check = false;
                } else {
                    llDescriptionField.setBackgroundColor(getResources().getColor(R.color.lightgray));
                }

                List<Integer> temp = new ArrayList<>(app.SharedUsers);

                if (Shared) {
                    if (temp.size() == 0) {
                        check = false;
                    }
                } else {
                    temp = null;
                }

                if (!editingNote) {
                    // Add new Text note
                    if (check && app.ListNotes.AddTextNote(Title, Description, Shared, temp)) {
                        finish();
                    } else {
                        Toast.makeText(CreateTextNoteActivity.this, R.string.new_tn_error, Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (noteOwner) {
                        // Edit existing Text note
                        if (check && app.ListNotes.EditTextNote(index, Title, Description, Shared, temp, tempNote)) {
                            finish();
                        } else {
                            Toast.makeText(CreateTextNoteActivity.this, R.string.edit_tn_error, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(CreateTextNoteActivity.this, R.string.user_not_owner_error, Toast.LENGTH_LONG).show();
                    }
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
