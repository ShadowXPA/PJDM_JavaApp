package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CreateWeatherNoteSetUpActivity extends Activity {
    private MyApp app;
    private LinearLayout ACWNSUCityField;
    private EditText ACWNSUCity;
    private Button ACWNSUCheck;
    private Boolean superCheck;
    private TextView errorLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_weather_note_set_up);
        app = (MyApp)getApplication();
        ACWNSUCityField = findViewById(R.id.ACWNSUCityField);
        ACWNSUCity = findViewById(R.id.ACWNSUCity);
        ACWNSUCheck = findViewById(R.id.ACWNSUCheck);
        errorLog = findViewById(R.id.errorLog);

        superCheck = false;

        ACWNSUCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = true;
                String city = ACWNSUCity.getText().toString();

                if (city.isEmpty()) {
                    ACWNSUCityField.setBackgroundColor(Color.RED);
                    Toast.makeText(CreateWeatherNoteSetUpActivity.this, R.string.city_is_empty_error, Toast.LENGTH_LONG).show();
                    check = false;
                } else {
                    ACWNSUCityField.setBackgroundColor(getResources().getColor(R.color.lightgray));
                }

                if (check) {
                    WeatherMapDataAsyncTask wmdat = new WeatherMapDataAsyncTask(app, ACWNSUCityField, errorLog);
                    wmdat.execute("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&lang=en&appid=621cebe289093c23d3b128456ee73df3", getResources().getString(R.string.accessing_page_error));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        app.weatherData = null;
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
            case R.id.CEUSave: {
                String city = ACWNSUCity.getText().toString().trim();

                if (city.isEmpty()) {
                    ACWNSUCityField.setBackgroundColor(Color.RED);
                    Toast.makeText(CreateWeatherNoteSetUpActivity.this, R.string.city_is_empty_error, Toast.LENGTH_LONG).show();
                } else {
                    ACWNSUCityField.setBackgroundColor(getResources().getColor(R.color.lightgray));
                }

                if (app.weatherData != null && app.weatherData.length == 11) {
                    Intent intent = new Intent(CreateWeatherNoteSetUpActivity.this, CreateWeatherNoteActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(CreateWeatherNoteSetUpActivity.this, R.string.check_city_error, Toast.LENGTH_LONG).show();
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
