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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CreateWeatherNoteActivity extends Activity {
    private MyApp app;
    private boolean Shared;
    private int index;
    private boolean editingNote;
    private boolean noteOwner;
    private Note tempNote;
    private EditText etTitle;
    private EditText etDescription;
    private TextView Weather;
    private TextView Description;
    private TextView Temperature;
    private TextView Pressure;
    private TextView Humidity;
    private TextView Min;
    private TextView Max;
    private TextView Speed;
    private TextView Degree;
    private TextView DegStr;
    private TextView City;
    private TextView Country;
    private LinearLayout llTitleField;
    private LinearLayout llDescriptionField;
    private CheckBox cbShared;
    private boolean sharedNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_weather_note);
        app = (MyApp) getApplication();
        etTitle = findViewById(R.id.ACWNTitle);
        etDescription = findViewById(R.id.ACWNDescription);
        llTitleField = findViewById(R.id.ACWNTitleField);
        llDescriptionField = findViewById(R.id.ACWNDescriptionField);
        cbShared = findViewById(R.id.ACWNShared);
        Weather = findViewById(R.id.ACWNWeatherValue);
        Description = findViewById(R.id.ACWNWeatherDescValue);
        Temperature = findViewById(R.id.ACWNTemperatureValue);
        Pressure = findViewById(R.id.ACWNPressureValue);
        Humidity = findViewById(R.id.ACWNHumidityValue);
        Min = findViewById(R.id.ACWNMinTempValue);
        Max = findViewById(R.id.ACWNMaxTempValue);
        Speed = findViewById(R.id.ACWNSpeedValue);
        Degree = findViewById(R.id.ACWNDegreeValue);
        DegStr = findViewById(R.id.ACWNDegreeStrValue);
        City = findViewById(R.id.ACWNCityValue);
        Country = findViewById(R.id.ACWNCountryValue);

        index = getIntent().getIntExtra("index", 0);
        editingNote = getIntent().getBooleanExtra("editingNote", false);
        noteOwner = getIntent().getBooleanExtra("noteOwner", false);
        sharedNote = getIntent().getBooleanExtra("sharedNote", false);
        boolean metric = app.SPSettings.getBoolean("metric", true);

        if (!editingNote) {
            Weather.setText(app.weatherData[0]);
            Description.setText(app.weatherData[1]);
            String str;
            if (metric) {
                str = app.weatherData[2] + " ºC";
                Temperature.setText(str);
                str = app.weatherData[5] + " ºC";
                Min.setText(str);
                str = app.weatherData[6] + " ºC";
                Max.setText(str);
                str = app.weatherData[7] + " m/s";
                Speed.setText(str);
            } else {
                str = PNUtil.convertCelsiusToFahrenheit(app.weatherData[2]) + " ºF";
                Temperature.setText(str);
                str = PNUtil.convertCelsiusToFahrenheit(app.weatherData[5]) + " ºF";
                Min.setText(str);
                str = PNUtil.convertCelsiusToFahrenheit(app.weatherData[6]) + " ºF";
                Max.setText(str);
                str = PNUtil.convertMeterPerSecToMilePerHour(app.weatherData[7]) + " mph";
                Speed.setText(str);
            }
            str = app.weatherData[3] + " hPa";
            Pressure.setText(str);
            str = app.weatherData[4] + " %";
            Humidity.setText(str);
            if ((str = app.weatherData[8]).equals(getResources().getString(R.string.not_available)))
                Degree.setText(str);
            else {
                str = app.weatherData[8] + "º";
                Degree.setText(str);
            }
            DegStr.setText(PNUtil.convertDegreeToDegreeStr(app.weatherData[8]));
            City.setText(app.weatherData[9]);
            Country.setText(app.weatherData[10]);
        } else {
            if (!noteOwner) {
                cbShared.setEnabled(false);
                etTitle.setEnabled(false);
                etDescription.setEnabled(false);
            }

            cbShared.setChecked(sharedNote);
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

            etTitle.setText(tempNote.getTitle());
            etDescription.setText(((WeatherNote)tempNote).getText());

            Weather.setText(((WeatherNote) tempNote).getWeather());
            Description.setText(((WeatherNote) tempNote).getDescription());
            Temperature.setText(((WeatherNote) tempNote).getTemperature());
            Min.setText(((WeatherNote) tempNote).getMinTemperature());
            Max.setText(((WeatherNote) tempNote).getMaxTemperature());
            Pressure.setText(((WeatherNote) tempNote).getPressure());
            Humidity.setText(((WeatherNote) tempNote).getHumidity());
            Speed.setText(((WeatherNote) tempNote).getWindSpeed());
            Degree.setText(((WeatherNote) tempNote).getDegree());
            DegStr.setText(((WeatherNote) tempNote).getDegreeStr());
            City.setText(((WeatherNote) tempNote).getCity());
            Country.setText(((WeatherNote) tempNote).getCountry());
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
                Shared = cbShared.isChecked();

                if (Shared && (!editingNote || noteOwner)) {
                    Intent intent = new Intent(CreateWeatherNoteActivity.this, SelectSharedUsersActivity.class);
                    startActivity(intent);
                } else {
                    if (editingNote && !noteOwner)
                        Toast.makeText(CreateWeatherNoteActivity.this, R.string.user_not_owner_error, Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(CreateWeatherNoteActivity.this, R.string.check_shared_error, Toast.LENGTH_LONG).show();
                }
                return true;
            }
            case R.id.CENMSave: {
                Shared = cbShared.isChecked();
                String Title = etTitle.getText().toString().trim();
                String Description = etDescription.getText().toString().trim();
                boolean check = true;

                if (Title.isEmpty()) {
                    llTitleField.setBackgroundColor(Color.RED);
                    Toast.makeText(CreateWeatherNoteActivity.this, R.string.title_is_empty_error, Toast.LENGTH_LONG).show();
                    check = false;
                } else {
                    llTitleField.setBackgroundColor(getResources().getColor(R.color.lightgray));
                }

                if (Description.isEmpty()) {
                    llDescriptionField.setBackgroundColor(Color.RED);
                    Toast.makeText(CreateWeatherNoteActivity.this, R.string.description_is_empty_error, Toast.LENGTH_LONG).show();
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
                    if (check && app.ListNotes.AddWeatherNote(Title, Description, Shared, temp)) {
                        finish();
                    } else {
                        Toast.makeText(CreateWeatherNoteActivity.this, R.string.new_wn_error, Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (noteOwner) {
                        if (check && app.ListNotes.EditWeatherNote(index, Title, Description, Shared, temp, tempNote)) {
                            finish();
                        } else {
                            Toast.makeText(CreateWeatherNoteActivity.this, R.string.edit_wn_error, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(CreateWeatherNoteActivity.this, R.string.user_not_owner_error, Toast.LENGTH_LONG).show();
                    }
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
