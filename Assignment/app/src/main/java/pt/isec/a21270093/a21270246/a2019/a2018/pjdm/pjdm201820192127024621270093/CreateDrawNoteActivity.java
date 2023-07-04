package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CreateDrawNoteActivity extends Activity {
    private MyApp app;
    private String Title;
    private boolean Shared;
    private boolean sharedOld;
    private int index;
    private boolean lineStyle;
    private boolean editingNote;
    private boolean noteOwner;
    private int BGAlpha;
    private int BGRed;
    private int BGGreen;
    private int BGBlue;
    private int PENAlpha;
    private int PENRed;
    private int PENGreen;
    private int PENBlue;
    private FrameLayout FL;
    private DrawArea DA;
    private boolean erased;
    private Note tempNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_draw_note);
        app = (MyApp) getApplication();

        Title = getIntent().getStringExtra("DNTitle");
        Shared = getIntent().getBooleanExtra("sharedNote", false);
        sharedOld = getIntent().getBooleanExtra("sharedNoteOld", false);
        index = getIntent().getIntExtra("index", 0);
        lineStyle = getIntent().getBooleanExtra("lineStyle", false);
        editingNote = getIntent().getBooleanExtra("editingNote", false);
        noteOwner = getIntent().getBooleanExtra("noteOwner", false);
        BGAlpha = getIntent().getIntExtra("BgColAlpha", 255);
        BGRed = getIntent().getIntExtra("BgColRed", 255);
        BGGreen = getIntent().getIntExtra("BgColGreen", 255);
        BGBlue = getIntent().getIntExtra("BgColBlue", 255);
        PENAlpha = getIntent().getIntExtra("PenColAlpha", 255);
        PENRed = getIntent().getIntExtra("PenColRed", 0);
        PENGreen = getIntent().getIntExtra("PenColGreen", 0);
        PENBlue = getIntent().getIntExtra("PenColBlue", 0);
        erased = false;

        if (editingNote) {
            // If the note is shared
            if (Shared) {
                // See if it was shared but is now private and vice-versa
                if (sharedOld) {
                    tempNote = app.ListNotes.GetSharedNoteListItemInverted(index).get(app.ListNotes.SharedNoteKeyVal);
                } else {
                    tempNote = app.ListNotes.GetNoteListItemInverted(index).get(app.ListNotes.NoteKeyVal);
                }
            } else {
                if (!sharedOld) {
                    tempNote = app.ListNotes.GetNoteListItemInverted(index).get(app.ListNotes.NoteKeyVal);
                } else {
                    tempNote = app.ListNotes.GetSharedNoteListItemInverted(index).get(app.ListNotes.SharedNoteKeyVal);
                }
            }
        }

        FL = findViewById(R.id.ACDNFrame);
        newDrawArea();
    }

    public void newDrawArea() {
        if (!editingNote) {
            DA = new DrawArea(this, new int[]{PENAlpha, PENRed, PENGreen, PENBlue}, null, lineStyle, noteOwner, null);
        } else {
            if (!erased) {
                List<Point> points;
                if (Shared) {
                    points = ((DrawNote) tempNote).getPointList();
                } else {
                    points = ((DrawNote) tempNote).getPointList();
                }
                DA = new DrawArea(this, new int[]{PENAlpha, PENRed, PENGreen, PENBlue}, points, lineStyle, noteOwner, null);
            } else {
                DA = new DrawArea(this, new int[]{PENAlpha, PENRed, PENGreen, PENBlue}, null, lineStyle, noteOwner, null);
                erased = false;
            }
        }

        DA.setBackgroundColor(Color.argb(BGAlpha, BGRed, BGGreen, BGBlue));

        FL.removeAllViews();
        FL.addView(DA);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            MenuInflater mi = getMenuInflater();
            mi.inflate(R.menu.drawing_menu, menu);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.DMClear: {
                if (noteOwner) {
                    erased = true;
                    newDrawArea();
                } else {
                    Toast.makeText(CreateDrawNoteActivity.this, R.string.user_not_owner_error, Toast.LENGTH_LONG).show();
                }
                return true;
            }
            case R.id.DMSave: {
                boolean check = true;
                List<Integer> temp = new ArrayList<>(app.SharedUsers);

                if (Shared && noteOwner) {
                    if (temp.size() == 0) {
                        check = false;
                    }
                } else {
                    temp = null;
                }

                if (check) {
                    if (!editingNote) {
                        if (app.ListNotes.AddDrawNote(Title, Shared, temp, DA.getPointList(), new int[]{BGAlpha, BGRed, BGGreen, BGBlue, PENAlpha, PENRed, PENGreen, PENBlue}, lineStyle)) {
                            Toast.makeText(CreateDrawNoteActivity.this, R.string.created, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(CreateDrawNoteActivity.this, R.string.notCreated, Toast.LENGTH_LONG).show();
                        }
                        finish();
                    } else {
                        if (noteOwner) {
                            if (app.ListNotes.EditDrawNote(index, Title, Shared, temp, DA.getPointList(), new int[]{BGAlpha, BGRed, BGGreen, BGBlue, PENAlpha, PENRed, PENGreen, PENBlue}, lineStyle, tempNote)) {
                                Toast.makeText(CreateDrawNoteActivity.this, R.string.edited, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(CreateDrawNoteActivity.this, R.string.notEdited, Toast.LENGTH_LONG).show();
                            }
                            finish();
                        } else {
                            Toast.makeText(CreateDrawNoteActivity.this, R.string.user_not_owner_error, Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(CreateDrawNoteActivity.this, "This should not show up....", Toast.LENGTH_LONG).show();
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
