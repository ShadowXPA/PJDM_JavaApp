package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import android.app.Application;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class WeatherMapDataAsyncTask extends AsyncTask<String, Void, String> {
    private MyApp app;
    private LinearLayout ll;
    private TextView tv;

    public WeatherMapDataAsyncTask(Application app, LinearLayout ll, TextView tv) {
        this.app = (MyApp) app;
        this.ll = ll;
        this.tv = tv;
    }

    @Override
    protected String doInBackground(String... strings) {
        // strings[0] = URL, strings[1] = error message from the string table

        String temp = strings[1];
        try {
            temp = PNUtil.weatherData(strings[0], strings[1]);
        } catch (Exception ignored) { }

        return temp;
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            if (s.isEmpty()) {
                ll.setBackgroundColor(Color.RED);
                tv.setText(app.getResources().getString(R.string.internet_access_error));
            } else if (s.equals(app.getResources().getString(R.string.accessing_page_error))) {
                ll.setBackgroundColor(Color.RED);
                tv.setText(app.getResources().getString(R.string.accessing_page_error));
            } else {
                JSONObject jsonObject = new JSONObject(s);

                int code = jsonObject.getInt("cod");

                if (code == 200) {
                    // Put data on the weatherData array
                    // 0 - Weather
                    // 1 - Description
                    // 2 - Temperature
                    // 3 - Pressure
                    // 4 - Humidity
                    // 5 - MinTemperature
                    // 6 - MaxTemperature
                    // 7 - WindSpeed
                    // 8 - Degree
                    // 9 - City
                    // 10 - Country
                    app.weatherData = new String[11];

                    JSONArray weather;
                    JSONObject weatherIn = null;

                    try {
                        weather = jsonObject.getJSONArray("weather");
                        weatherIn = weather.getJSONObject(0);
                        app.weatherData[0] = weatherIn.getString("main");
                    } catch (Exception e) {
                        app.weatherData[0] = app.getResources().getString(R.string.not_available);
                    }

                    try {
                        app.weatherData[1] = weatherIn.getString("description");
                    } catch (Exception e) {
                        app.weatherData[1] = app.getResources().getString(R.string.not_available);
                    }


                    JSONObject main = null;
                    try {
                        main = jsonObject.getJSONObject("main");
                        double Temperature = main.getDouble("temp");
                        app.weatherData[2] = "" + Temperature;
                    } catch (Exception e) {
                        app.weatherData[2] = app.getResources().getString(R.string.not_available);
                    }

                    try {
                        int Pressure = main.getInt("pressure");
                        app.weatherData[3] = "" + Pressure;
                    } catch (Exception e) {
                        app.weatherData[3] = app.getResources().getString(R.string.not_available);
                    }

                    try {
                        int Humidity = main.getInt("humidity");
                        app.weatherData[4] = "" + Humidity;
                    } catch (Exception e) {
                        app.weatherData[4] = app.getResources().getString(R.string.not_available);
                    }

                    try {
                        double MinTemperature = main.getDouble("temp_min");
                        app.weatherData[5] = "" + MinTemperature;
                    } catch (Exception e) {
                        app.weatherData[5] = app.getResources().getString(R.string.not_available);
                    }

                    try {
                        double MaxTemperature = main.getDouble("temp_max");
                        app.weatherData[6] = "" + MaxTemperature;
                    } catch (Exception e) {
                        app.weatherData[6] = app.getResources().getString(R.string.not_available);
                    }

                    JSONObject wind = null;
                    try {
                        wind = jsonObject.getJSONObject("wind");
                        double Speed = wind.getDouble("speed");
                        app.weatherData[7] = "" + Speed;
                    } catch (Exception e) {
                        app.weatherData[7] = app.getResources().getString(R.string.not_available);
                    }

                    try {
                        int Degree = wind.getInt("deg");
                        app.weatherData[8] = "" + Degree;
                    } catch (Exception e) {
                        app.weatherData[8] = app.getResources().getString(R.string.not_available);
                    }

                    try {
                        app.weatherData[9] = jsonObject.getString("name");
                    } catch (Exception e) {
                        app.weatherData[9] = app.getResources().getString(R.string.not_available);
                    }

                    try {
                        JSONObject sys = jsonObject.getJSONObject("sys");
                        String Country = sys.getString("country");
                        app.weatherData[10] = Country;
                    } catch (Exception e) {
                        app.weatherData[10] = app.getResources().getString(R.string.not_available);
                    }

                    ll.setBackgroundColor(Color.GREEN);
                } else {
                    ll.setBackgroundColor(Color.RED);
                }

                tv.setText("");
            }

        } catch (JSONException ignored) {}
    }
}
