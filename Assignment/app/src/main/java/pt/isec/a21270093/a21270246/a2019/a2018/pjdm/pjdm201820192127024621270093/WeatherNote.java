package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import java.util.Date;
import java.util.List;

public class WeatherNote extends TextNote {
    // Weather
        // Weather Condition group
        private String Weather;
        // Weather Description
        private String Description;

    // Main
        // Temperature in ºC or ºF (Depending on 'imperial' boolean)
        private String Temperature;
        // Pressure in hPa (HectoPascal) or 1 mbar (millibar)
        private String Pressure;
        // Humidity in %
        private String Humidity;
        // MinTemperature in ºC or ºF (Depending on 'imperial' boolean)
        private String MinTemperature;
        // MaxTemperature in ºC or ºF (Depending on 'imperial' boolean)
        private String MaxTemperature;

    // Wind
        // WindSpeed in Metric: meter/sec Imperial: miles/hour
        private String WindSpeed;
        // Degree
        private String Degree;

    // City info
        // City
        private String City;
        // Country
        private String Country;

    public String getWeather() {
        return Weather;
    }

    public String getDescription() {
        return Description;
    }

    public String getTemperature() {
        boolean metric = PNUtil.getApp().SPSettings.getBoolean("metric", true);

        if (metric)
            return Temperature + " ºC";
        else
            return PNUtil.convertCelsiusToFahrenheit(Temperature) + " ºF";
    }

    public String getPressure() {
        return Pressure + " hPa";
    }

    public String getHumidity() {
        return Humidity + " %";
    }

    public String getMinTemperature() {
        boolean metric = PNUtil.getApp().SPSettings.getBoolean("metric", true);

        if (metric)
            return MinTemperature + " ºC";
        else
            return PNUtil.convertCelsiusToFahrenheit(MinTemperature) + " ºF";
    }

    public String getMaxTemperature() {
        boolean metric = PNUtil.getApp().SPSettings.getBoolean("metric", true);

        if (metric)
            return MaxTemperature + " ºC";
        else
            return PNUtil.convertCelsiusToFahrenheit(MaxTemperature) + " ºF";
    }

    public String getWindSpeed() {
        boolean metric = PNUtil.getApp().SPSettings.getBoolean("metric", true);

        if (metric)
            return WindSpeed + " m/s";
        else
            return PNUtil.convertMeterPerSecToMilePerHour(WindSpeed) + " mph";
    }

    public String getDegree() {
        try {
            return "" + Integer.parseInt(Degree) + "º";
        } catch (Exception e) {
            return PNUtil.getApp().getResources().getString(R.string.not_available);
        }
    }

    public String getDegreeStr() {
        return PNUtil.convertDegreeToDegreeStr(Degree);
    }

    public String getCity() {
        return City;
    }

    public String getCountry() {
        return Country;
    }

    public WeatherNote(int ID, String title, User author, Date creationDate, boolean shared, List<Integer> users, String text, String weather, String description, String temperature, String pressure, String humidity, String minTemperature, String maxTemperature, String windSpeed, String degree, String city, String country) {
        super(ID, title, author, creationDate, shared, users, text);
        Weather = weather;
        Description = description;
        Temperature = temperature;
        Pressure = pressure;
        Humidity = humidity;
        MinTemperature = minTemperature;
        MaxTemperature = maxTemperature;
        WindSpeed = windSpeed;
        Degree = degree;
        City = city;
        Country = country;
    }

    @Override
    public String compareType() {
        return NoteType.WEATHER_NOTE;
    }
}
