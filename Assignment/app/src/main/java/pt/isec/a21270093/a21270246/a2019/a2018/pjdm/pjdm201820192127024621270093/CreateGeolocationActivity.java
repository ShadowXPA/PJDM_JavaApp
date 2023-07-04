package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CreateGeolocationActivity extends Activity implements LocationListener, OnMapReadyCallback {
    private MyApp app;
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private MapFragment mapFragment;
    private LatLng loc;

    private MarkerOptions mo;

    private List<String> imgs;
    private boolean editingNote;
    private int index;
    private Note tempNote;
    private boolean noteOwner;
    private boolean sharedNote;
    private String ImgUrl;
    private String FolderUrl;
    private boolean delImgs;

    private boolean LocationGot;

    private Button AddImg;
    private LinearLayout llTitle;
    private EditText etTitle;
    private GridView GridV;
    private CheckBox cbShared;
    private TextView lat;
    private TextView lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_geolocation);
        app = (MyApp) getApplication();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        lat = findViewById(R.id.Lati);
        lon = findViewById(R.id.Longi);
        AddImg = findViewById(R.id.ButAddImg);
        llTitle = findViewById(R.id.ACGNTitleField);
        etTitle = findViewById(R.id.ACGNTitle);
        GridV = findViewById(R.id.GridV);
        cbShared = findViewById(R.id.ACGNShared);

        imgs = new ArrayList<>();
        LocationGot = false;
        delImgs = false;

        AddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
                        Toast.makeText(CreateGeolocationActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    if (photoFile != null)
                    {
                        ImgUrl = photoFile.getAbsolutePath();

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, 20);
                    }
                }
            }
        });

        editingNote = getIntent().getBooleanExtra("editingNote", false);
        index = getIntent().getIntExtra("index", 0);
        sharedNote = getIntent().getBooleanExtra("sharedNote", false);
        noteOwner = true;

        GridV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final LinearLayout LLPhoto = (LinearLayout)getLayoutInflater().inflate(R.layout.photosdialog, (ViewGroup) findViewById(R.id.LLGN), false);

                ImageView iv = LLPhoto.findViewById(R.id.ImgPhoto2);
                String photoPath = imgs.get(position);

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(photoPath, bmOptions);

                bmOptions.inJustDecodeBounds = false;
                bmOptions.inPurgeable = true;

                Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
                iv.setBackgroundDrawable(new BitmapDrawable(bitmap));


                new AlertDialog.Builder(CreateGeolocationActivity.this)
                        .setCancelable(false)
                        .setView(LLPhoto)
                        .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        int noteID = app.SPUserPref.getInt("noteID", 0);
        FolderUrl = getFilesDir() + "/srcImgs/" + app.AuthenticatedUser.GetUserID() + "/note_" + noteID;

        if (editingNote) {
            noteOwner = getIntent().getBooleanExtra("noteOwner", false);
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

            if (!noteOwner) {
                cbShared.setEnabled(false);
                etTitle.setEnabled(false);
                AddImg.setEnabled(false);
            }

            cbShared.setChecked(sharedNote);
            etTitle.setText(tempNote.getTitle());
            imgs = new ArrayList<>(((GeolocationNote)tempNote).getImgUrl());
            FolderUrl = ((GeolocationNote) tempNote).getFolderUrl();
        }

        if (noteOwner) {
            // Only the note owner is allowed to delete
            GridV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    String title = getResources().getString(R.string.deleting) + " 'IMG_" + position + "'";

                    new AlertDialog.Builder(CreateGeolocationActivity.this)
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
                                    imgs.remove(position);
                                    GridV.setAdapter(new MyAdapterImage(imgs, getLayoutInflater()));
                                }
                            })
                            .show();

                    return true;
                }
            });
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 20)
        {
            imgs.add(ImgUrl);
            delImgs = false;
        }
    }

    private File createImageFile() throws IOException
    {
        int noteID;
        if (!editingNote)
            noteID = app.SPUserPref.getInt("noteID", 0);
        else {
            noteID = tempNote.getID();
        }
        String imageFileName = "img_" + noteID + "_" + imgs.size();
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

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
    protected void onDestroy() {
        super.onDestroy();
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            File ext = new File(storageDir + "/PNExt");
            if (ext.exists()) {
                for (File ex : ext.listFiles())
                    ex.delete();
                ext.delete();
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
        boolean Shared = cbShared.isChecked();
        switch (item.getItemId()) {
            case R.id.CENMShared: {
                if (Shared && (!editingNote || noteOwner)) {
                    Intent intent = new Intent(CreateGeolocationActivity.this, SelectSharedUsersActivity.class);
                    startActivity(intent);
                } else {
                    if (editingNote && !noteOwner)
                        Toast.makeText(CreateGeolocationActivity.this, R.string.user_not_owner_error, Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(CreateGeolocationActivity.this, R.string.check_shared_error, Toast.LENGTH_LONG).show();
                }
                return true;
            }
            case R.id.CENMSave: {
                String Title = etTitle.getText().toString().trim();
                boolean check = true;

                if (Title.isEmpty()) {
                    llTitle.setBackgroundColor(Color.RED);
                    Toast.makeText(CreateGeolocationActivity.this, R.string.title_is_empty_error, Toast.LENGTH_LONG).show();
                    check = false;
                } else {
                    llTitle.setBackgroundColor(getResources().getColor(R.color.lightgray));
                }

                List<Integer> temp = new ArrayList<>(app.SharedUsers);

                if (Shared) {
                    if (temp.size() == 0) {
                        check = false;
                    }
                } else {
                    temp = null;
                }

                if (LocationGot) {
                    if (!editingNote) {
                        if (check && app.ListNotes.AddGeolocationNote(Title, Shared, temp, FolderUrl, loc.latitude, loc.longitude, imgs)) {
                            delImgs = true;
                            finish();
                        } else {
                            Toast.makeText(CreateGeolocationActivity.this, R.string.new_gn_error, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        if (noteOwner) {
                            if (check && app.ListNotes.EditGeolocationNote(index, Title, Shared, temp, tempNote, imgs)) {
                                delImgs = true;
                                finish();
                            } else {
                                Toast.makeText(CreateGeolocationActivity.this, R.string.edit_gn_error, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(CreateGeolocationActivity.this, R.string.user_not_owner_error, Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(CreateGeolocationActivity.this, R.string.location_note_found, Toast.LENGTH_LONG).show();
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 2);
                return;
            }
        }

        GridV.setAdapter(new MyAdapterImage(imgs, getLayoutInflater()));

        if (!editingNote) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } else {
            if (!LocationGot) {
                double latitu = ((GeolocationNote) tempNote).getLat();
                double lngitu = ((GeolocationNote) tempNote).getLng();
                loc = new LatLng(latitu, lngitu);
                if (noteOwner)
                    mo = new MarkerOptions().position(loc).title(getResources().getString(R.string.your_location));
                else
                    mo = new MarkerOptions().position(loc).title(getResources().getString(R.string.others_location) + " " + tempNote.getAuthorUN());

                String Lati = getResources().getString(R.string.latitude_) + " " + ((GeolocationNote) tempNote).getLat();
                String Longi = getResources().getString(R.string.longitude_) + " " + ((GeolocationNote) tempNote).getLng();
                lat.setText(Lati);
                lon.setText(Longi);

                LocationGot = true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode)
        {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    AddImg.performClick();

                break;
            case 2:
                if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    onResume();

                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (googleMap != null && !LocationGot) {
            loc = new LatLng(location.getLatitude(), location.getLongitude());

            mo = new MarkerOptions().position(loc).title(getResources().getString(R.string.your_location));

            Marker marker = googleMap.addMarker(mo);
            marker.showInfoWindow();

            String Lati = getResources().getString(R.string.latitude_) + " " + String.format(Locale.ENGLISH, "%.6f", location.getLatitude());
            String Longi = getResources().getString(R.string.longitude_) + " " + String.format(Locale.ENGLISH, "%.6f", location.getLongitude());

            lat.setText(Lati);
            lon.setText(Longi);

            CameraPosition cp = new CameraPosition.Builder()
                    .target(loc)
                    .zoom(17)
                    .bearing(0)
                    .tilt(0)
                    .build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));

            locationManager.removeUpdates(this);

            LocationGot = true;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setAllGesturesEnabled(false);

        if (editingNote) {
            Marker marker = googleMap.addMarker(mo);

            marker.showInfoWindow();

            CameraPosition cp = new CameraPosition.Builder()
                    .target(loc)
                    .zoom(17)
                    .bearing(0)
                    .tilt(0)
                    .build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
        }
    }
}
