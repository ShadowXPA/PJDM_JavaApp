package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CreateDrawNoteSetUpActivity extends Activity {
    private MyApp app;
    private boolean editingNote;
    private int index;
    private boolean noteOwner;
    private SeekBar seekRed;
    private SeekBar seekGreen;
    private SeekBar seekBlue;
    private SeekBar seekAlpha;

    private SeekBar seekRed2;
    private SeekBar seekGreen2;
    private SeekBar seekBlue2;
    private SeekBar seekAlpha2;

    private RadioGroup RG;

    private CheckBox cbShared;
    private EditText etTitle;
    private boolean sharedNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_draw_note_set_up);
        app = (MyApp) getApplication();
        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                (findViewById(R.id.ACDNSUColorPreview)).setBackgroundColor(Color.argb(((SeekBar)findViewById(R.id.ACDNSUAlphaBar)).getProgress(),
                        ((SeekBar)findViewById(R.id.ACDNSURedBar)).getProgress(),
                        ((SeekBar)findViewById(R.id.ACDNSUGreenBar)).getProgress(),
                        ((SeekBar)findViewById(R.id.ACDNSUBlueBar)).getProgress()));
                (findViewById(R.id.ACDNSUColorPreview2)).setBackgroundColor(Color.argb(((SeekBar)findViewById(R.id.ACDNSUAlphaBar2)).getProgress(),
                        ((SeekBar)findViewById(R.id.ACDNSURedBar2)).getProgress(),
                        ((SeekBar)findViewById(R.id.ACDNSUGreenBar2)).getProgress(),
                        ((SeekBar)findViewById(R.id.ACDNSUBlueBar2)).getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        };

        RG = findViewById(R.id.ACDNSURG);

        // Background Color SeekBars
        seekRed = findViewById(R.id.ACDNSURedBar);
        seekRed.setProgress(255);
        seekGreen = findViewById(R.id.ACDNSUGreenBar);
        seekGreen.setProgress(255);
        seekBlue = findViewById(R.id.ACDNSUBlueBar);
        seekBlue.setProgress(255);
        seekAlpha = findViewById(R.id.ACDNSUAlphaBar);
        seekAlpha.setProgress(255);

        seekRed.setOnSeekBarChangeListener(listener);
        seekGreen.setOnSeekBarChangeListener(listener);
        seekBlue.setOnSeekBarChangeListener(listener);
        seekAlpha.setOnSeekBarChangeListener(listener);

        findViewById(R.id.ACDNSUColorPreview).setBackgroundColor(Color.argb(255, 255, 255, 255));

        // Pencil Color SeekBars
        seekRed2 = findViewById(R.id.ACDNSURedBar2);
        seekRed2.setProgress(0);
        seekGreen2 = findViewById(R.id.ACDNSUGreenBar2);
        seekGreen2.setProgress(0);
        seekBlue2 = findViewById(R.id.ACDNSUBlueBar2);
        seekBlue2.setProgress(0);
        seekAlpha2 = findViewById(R.id.ACDNSUAlphaBar2);
        seekAlpha2.setProgress(255);

        seekRed2.setOnSeekBarChangeListener(listener);
        seekGreen2.setOnSeekBarChangeListener(listener);
        seekBlue2.setOnSeekBarChangeListener(listener);
        seekAlpha2.setOnSeekBarChangeListener(listener);

        findViewById(R.id.ACDNSUColorPreview2).setBackgroundColor(Color.argb(255, 0, 0, 0));

        cbShared = findViewById(R.id.ACDNSUShared);
        etTitle = findViewById(R.id.ACDNSUTitle);

        editingNote = getIntent().getBooleanExtra("editingNote", false);
        index = getIntent().getIntExtra("index", 0);
        sharedNote = getIntent().getBooleanExtra("sharedNote", false);
        noteOwner = true;

        if (editingNote) {
            noteOwner = getIntent().getBooleanExtra("noteOwner", false);

            if (!noteOwner) {
                cbShared.setEnabled(false);
                etTitle.setEnabled(false);
                seekRed.setEnabled(false);
                seekGreen.setEnabled(false);
                seekBlue.setEnabled(false);
                seekAlpha.setEnabled(false);
                seekRed2.setEnabled(false);
                seekGreen2.setEnabled(false);
                seekBlue2.setEnabled(false);
                seekAlpha2.setEnabled(false);
                findViewById(R.id.ACDNSUSolid).setEnabled(false);
                findViewById(R.id.ACDNSUDashed).setEnabled(false);
            }

            cbShared.setChecked(sharedNote);
            Note tempNote;
            if (sharedNote) {
                tempNote = app.ListNotes.GetSharedNoteListItemInverted(index).get(app.ListNotes.SharedNoteKeyVal);
                if (tempNote != null) {
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

            etTitle.setText(tempNote.getTitle());
            if (((DrawNote) tempNote).isDashed()) {
                ((RadioButton)findViewById(R.id.ACDNSUSolid)).setChecked(false);
                ((RadioButton)findViewById(R.id.ACDNSUDashed)).setChecked(true);
            } else {
                ((RadioButton)findViewById(R.id.ACDNSUDashed)).setChecked(false);
                ((RadioButton)findViewById(R.id.ACDNSUSolid)).setChecked(true);
            }

            int[] c = ((DrawNote) tempNote).getColorList();
            seekAlpha.setProgress(c[0]);
            seekRed.setProgress(c[1]);
            seekGreen.setProgress(c[2]);
            seekBlue.setProgress(c[3]);
            seekAlpha2.setProgress(c[4]);
            seekRed2.setProgress(c[5]);
            seekGreen2.setProgress(c[6]);
            seekBlue2.setProgress(c[7]);

            findViewById(R.id.ACDNSUColorPreview).setBackgroundColor(Color.argb(c[0],c[1],c[2],c[3]));
            findViewById(R.id.ACDNSUColorPreview2).setBackgroundColor(Color.argb(c[4],c[5],c[6],c[7]));
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
                boolean Shared = cbShared.isChecked();

                if (Shared && (!editingNote || noteOwner)) {
                    Intent intent = new Intent(CreateDrawNoteSetUpActivity.this, SelectSharedUsersActivity.class);
                    startActivity(intent);
                } else {
                    if (editingNote && !noteOwner)
                        Toast.makeText(CreateDrawNoteSetUpActivity.this, R.string.user_not_owner_error, Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(CreateDrawNoteSetUpActivity.this, R.string.check_shared_error, Toast.LENGTH_LONG).show();
                }
                return true;
            }
            case R.id.CENMSave: {
                boolean lineStyle;
                switch (RG.getCheckedRadioButtonId()) {
                    case R.id.ACDNSUSolid: {
                        lineStyle = false;
                        break;
                    }
                    case R.id.ACDNSUDashed: {
                        lineStyle = true;
                        break;
                    }
                    default:
                        lineStyle = false;
                        break;
                }

                LinearLayout linearLayout = findViewById(R.id.ACDNSUTitleField);

                boolean Shared = cbShared.isChecked();
                String Title = etTitle.getText().toString().trim();
                boolean check = true;

                if (Title.isEmpty()) {
                    linearLayout.setBackgroundColor(Color.RED);
                    Toast.makeText(CreateDrawNoteSetUpActivity.this, R.string.title_is_empty_error, Toast.LENGTH_LONG).show();
                    check = false;
                } else {
                    linearLayout.setBackgroundColor(getResources().getColor(R.color.lightgray));
                }

                List<Integer> temp = new ArrayList<>(app.SharedUsers);

                if (Shared && noteOwner) {
                    if (temp.size() == 0) {
                        check = false;
                    }
                }

                if (check) {
                    Intent intent = new Intent(CreateDrawNoteSetUpActivity.this, CreateDrawNoteActivity.class);
                    intent.putExtra("DNTitle", Title);
                    intent.putExtra("sharedNote", Shared);
                    intent.putExtra("sharedNoteOld", sharedNote);
                    intent.putExtra("index", index);
                    intent.putExtra("lineStyle", lineStyle);
                    intent.putExtra("editingNote", editingNote);
                    intent.putExtra("noteOwner", noteOwner);
                    // COLORS
                    intent.putExtra("BgColAlpha", seekAlpha.getProgress());
                    intent.putExtra("BgColRed", seekRed.getProgress());
                    intent.putExtra("BgColGreen", seekGreen.getProgress());
                    intent.putExtra("BgColBlue", seekBlue.getProgress());

                    intent.putExtra("PenColAlpha", seekAlpha2.getProgress());
                    intent.putExtra("PenColRed", seekRed2.getProgress());
                    intent.putExtra("PenColGreen", seekGreen2.getProgress());
                    intent.putExtra("PenColBlue", seekBlue2.getProgress());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(CreateDrawNoteSetUpActivity.this, R.string.new_dn_error, Toast.LENGTH_LONG).show();
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
