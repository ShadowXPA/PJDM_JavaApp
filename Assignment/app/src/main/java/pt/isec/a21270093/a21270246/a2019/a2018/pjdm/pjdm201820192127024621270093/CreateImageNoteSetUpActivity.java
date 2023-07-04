package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CreateImageNoteSetUpActivity extends Activity {
    private MyApp app;
    private boolean editingNote;
    private int index;
    private boolean noteOwner;
    private Button ButSelImg;
    private SeekBar seekRed2;
    private SeekBar seekGreen2;
    private SeekBar seekBlue2;
    private SeekBar seekAlpha2;
    private String ImgUrl;
    private boolean delImgs;
    private boolean copyImgs;
    private boolean newPic;
    private Note tempNote;
    private boolean sharedNote;

    private RadioGroup RG;
    private RadioGroup RGImg;

    private CheckBox cbShared;
    private EditText etTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_image_note_set_up);
        app = (MyApp) getApplication();
        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                (findViewById(R.id.ACINSUColorPreview2)).setBackgroundColor(Color.argb(((SeekBar)findViewById(R.id.ACINSUAlphaBar2)).getProgress(),
                        ((SeekBar)findViewById(R.id.ACINSURedBar2)).getProgress(),
                        ((SeekBar)findViewById(R.id.ACINSUGreenBar2)).getProgress(),
                        ((SeekBar)findViewById(R.id.ACINSUBlueBar2)).getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        };

        delImgs = true;

        View.OnClickListener listener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (RGImg.getCheckedRadioButtonId()) {
                    case R.id.ACINSUNewImg:
                        newPic = true;
                        break;
                    case R.id.ACINSUSelImg:
                        newPic = false;
                        break;
                    default:
                        newPic = false;
                        break;
                }

                SelectImg(newPic);
            }
        };

        RG = findViewById(R.id.ACINSURG);
        RGImg = findViewById(R.id.ACINSURG2);
        ButSelImg = findViewById(R.id.ButSelImg);
        ButSelImg.setOnClickListener(listener1);

        // Pencil Color SeekBars
        seekRed2 = findViewById(R.id.ACINSURedBar2);
        seekRed2.setProgress(0);
        seekGreen2 = findViewById(R.id.ACINSUGreenBar2);
        seekGreen2.setProgress(0);
        seekBlue2 = findViewById(R.id.ACINSUBlueBar2);
        seekBlue2.setProgress(0);
        seekAlpha2 = findViewById(R.id.ACINSUAlphaBar2);
        seekAlpha2.setProgress(255);

        seekRed2.setOnSeekBarChangeListener(listener);
        seekGreen2.setOnSeekBarChangeListener(listener);
        seekBlue2.setOnSeekBarChangeListener(listener);
        seekAlpha2.setOnSeekBarChangeListener(listener);

        findViewById(R.id.ACINSUColorPreview2).setBackgroundColor(Color.argb(255, 0, 0, 0));

        cbShared = findViewById(R.id.ACINSUShared);
        etTitle = findViewById(R.id.ACINSUTitle);

        editingNote = getIntent().getBooleanExtra("editingNote", false);
        index = getIntent().getIntExtra("index", 0);
        sharedNote = getIntent().getBooleanExtra("sharedNote", false);
        noteOwner = true;

        if (editingNote) {
            noteOwner = getIntent().getBooleanExtra("noteOwner", false);

            if (!noteOwner) {
                cbShared.setEnabled(false);
                etTitle.setEnabled(false);
                seekRed2.setEnabled(false);
                seekGreen2.setEnabled(false);
                seekBlue2.setEnabled(false);
                seekAlpha2.setEnabled(false);
                findViewById(R.id.ACINSUNewImg).setEnabled(false);
                findViewById(R.id.ACINSUSelImg).setEnabled(false);
                findViewById(R.id.ACINSUSolid).setEnabled(false);
                findViewById(R.id.ACINSUDashed).setEnabled(false);
                ButSelImg.setEnabled(false);
            }

            cbShared.setChecked(sharedNote);

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
            if (((ImageNote) tempNote).isDashed()) {
                ((RadioButton)findViewById(R.id.ACINSUSolid)).setChecked(false);
                ((RadioButton)findViewById(R.id.ACINSUDashed)).setChecked(true);
            } else {
                ((RadioButton)findViewById(R.id.ACINSUDashed)).setChecked(false);
                ((RadioButton)findViewById(R.id.ACINSUSolid)).setChecked(true);
            }
            ImgUrl = ((ImageNote) tempNote).getBGImg();
            copyImgs = false;

            int[] c = ((ImageNote) tempNote).getColorList();
            seekAlpha2.setProgress(c[4]);
            seekRed2.setProgress(c[5]);
            seekGreen2.setProgress(c[6]);
            seekBlue2.setProgress(c[7]);

            findViewById(R.id.ACINSUColorPreview2).setBackgroundColor(Color.argb(c[4],c[5],c[6],c[7]));
        }

        //
        if(Build.VERSION.SDK_INT >= 24)
        {
            try
            {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }
            catch(Exception e) { }
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode)
        {
            case 1:
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    ButSelImg.performClick();

                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.CENMShared: {
                boolean Shared = cbShared.isChecked();

                if (Shared && (!editingNote || noteOwner)) {
                    Intent intent = new Intent(CreateImageNoteSetUpActivity.this, SelectSharedUsersActivity.class);
                    startActivity(intent);
                } else {
                    if (editingNote && !noteOwner)
                        Toast.makeText(CreateImageNoteSetUpActivity.this, R.string.user_not_owner_error, Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(CreateImageNoteSetUpActivity.this, R.string.check_shared_error, Toast.LENGTH_LONG).show();
                }
                return true;
            }
            case R.id.CENMSave: {
                boolean lineStyle;
                switch (RG.getCheckedRadioButtonId()) {
                    case R.id.ACINSUSolid: {
                        lineStyle = false;
                        break;
                    }
                    case R.id.ACINSUDashed: {
                        lineStyle = true;
                        break;
                    }
                    default:
                        lineStyle = false;
                        break;
                }

                LinearLayout linearLayout = findViewById(R.id.ACINSUTitleField);

                boolean Shared = cbShared.isChecked();
                String Title = etTitle.getText().toString().trim();
                boolean check = true;

                if (Title.isEmpty()) {
                    linearLayout.setBackgroundColor(Color.RED);
                    Toast.makeText(CreateImageNoteSetUpActivity.this, R.string.title_is_empty_error, Toast.LENGTH_LONG).show();
                    check = false;
                } else {
                    linearLayout.setBackgroundColor(getResources().getColor(R.color.lightgray));
                }

                if (ImgUrl == null || ImgUrl.isEmpty()) {
                    Toast.makeText(CreateImageNoteSetUpActivity.this, R.string.select_image_error, Toast.LENGTH_LONG).show();
                    check = false;
                }

                List<Integer> temp = new ArrayList<>(app.SharedUsers);

                if (Shared && noteOwner) {
                    if (temp.size() == 0) {
                        check = false;
                    }
                }

                if (check) {
                    delImgs = false;
                    Intent intent = new Intent(CreateImageNoteSetUpActivity.this, CreateImageNoteActivity.class);
                    intent.putExtra("DNTitle", Title);
                    intent.putExtra("sharedNote", Shared);
                    intent.putExtra("sharedNoteOld", sharedNote);
                    intent.putExtra("index", index);
                    intent.putExtra("lineStyle", lineStyle);
                    intent.putExtra("editingNote", editingNote);
                    intent.putExtra("noteOwner", noteOwner);
                    // COLORS
                    intent.putExtra("PenColAlpha", seekAlpha2.getProgress());
                    intent.putExtra("PenColRed", seekRed2.getProgress());
                    intent.putExtra("PenColGreen", seekGreen2.getProgress());
                    intent.putExtra("PenColBlue", seekBlue2.getProgress());
                    intent.putExtra("copyImgs", copyImgs);
                    intent.putExtra("cameraPic", newPic);

                    intent.putExtra("imgUrl", ImgUrl);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(CreateImageNoteSetUpActivity.this, R.string.new_in_error, Toast.LENGTH_LONG).show();
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*
        if (delImgs) {
            File dir = new File(getFilesDir() + "/srcImgs/");
            if (dir.exists()) {
                File storageDir = new File(dir + "/" + app.AuthenticatedUser.GetUserID());
                if (storageDir.exists()) {
                    if (ImgUrl != null) {
                        File file = new File(ImgUrl);
                        if (file.exists())
                            file.delete();
                    }
                }
            }
        }
*/


            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            File ext = new File(storageDir + "/PNExt");
            if (ext.exists()) {
                for (File ex : ext.listFiles())
                    ex.delete();
                ext.delete();
            }

    }

    private void SelectImg(boolean newPic) {
        if (!newPic) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    return;
                }
            }

            Intent selImage = new Intent(Intent.ACTION_PICK);
            selImage.setType("image/*");
            startActivityForResult(selImage, 10);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    return;
                }
            }

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null)
            {
                File photoFile = null;

                try
                {
                    photoFile = createImageFile();
                }
                catch (IOException ex)
                {
                    Toast.makeText(CreateImageNoteSetUpActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

                if (photoFile != null)
                {
                    ImgUrl = photoFile.getAbsolutePath();

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, 20);
                }
            }
        }
        copyImgs = true;
    }

    private File createImageFile() throws IOException
    {
        int noteID;
        if (!editingNote)
            noteID = app.SPUserPref.getInt("noteID", 0);
        else {
            noteID = tempNote.getID();
        }
        String imageFileName = "note_" + noteID;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        /*File dir = new File(getFilesDir() + "/srcImgs/");
        if (!dir.exists())
            dir.mkdir();
        File storageDir = new File(dir + "/" + app.AuthenticatedUser.GetUserID());
        if (!storageDir.exists())
            storageDir.mkdir();*/

        File ext = new File(storageDir + "/PNExt");

        if (!ext.exists())
            ext.mkdir();

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                ext      /* directory */
        );

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 10 &&	data !=	null &&	data.getData() != null)
        {
            Uri _uri = data.getData();

            if (_uri != null)
            {
                Cursor cursor =	getContentResolver().query(_uri,
                        new String[] { MediaStore.Images.ImageColumns.DATA	},
                        null, null,	null);

                cursor.moveToFirst();

                ImgUrl = cursor.getString(0);

                cursor.close();
            }
        }

        if (requestCode == 20 && data != null && data.getData() != null) {
            delImgs = false;
        }
    }
}
