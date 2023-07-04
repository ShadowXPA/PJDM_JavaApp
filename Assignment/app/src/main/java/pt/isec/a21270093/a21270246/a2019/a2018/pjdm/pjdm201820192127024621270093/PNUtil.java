package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public final class PNUtil {
    private static MyApp app;
    private PNUtil() {}

    public PNUtil(Application app) {
        PNUtil.app = (MyApp) app;
    }

    public static MyApp getApp() {
        // To use inside WeatherNote only...
        return app;
    }

    public static void setPic(View mImageView, String mCurrentPhotoPath)
    {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
    }

    public static void copy(File src, File dst) throws IOException {
        // Copy files from src to dst
        if (!src.getAbsolutePath().equals(dst.getAbsolutePath())) {
            InputStream in = new FileInputStream(src);
            try {
                OutputStream out = new FileOutputStream(dst);
                try {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                } finally {
                    out.close();
                }
            } finally {
                in.close();
            }
        }
    }

    public static String weatherData(String urlAdress, String errorStr) {
        // "http://api.openweathermap.org/data/2.5/weather?q=" + CITYNAME + "&units=metric&lang=en&appid=621cebe289093c23d3b128456ee73df3"

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlAdress);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(10000);
            connection.setDoInput(true);
            connection.connect();
            int cod = connection.getResponseCode();
            if (cod == HttpURLConnection.HTTP_OK) {
                InputStream IS = connection.getInputStream();
                BufferedReader BR = new BufferedReader(new InputStreamReader(IS));
                String data;
                while ((data = BR.readLine()) != null)
                    sb.append(data).append("\n");
            } else {
                sb.append(errorStr);
            }
        } catch (Exception ignored) { }

        return sb.toString();
    }

    public static String convertMeterPerSecToMilePerHour(String MeterPerSecond) {
        double MPS;
        try {
            MPS = Double.parseDouble(MeterPerSecond);
        } catch (Exception e) {
            return getApp().getResources().getString(R.string.error);
        }

        return "" + (MPS * 2.2369);
    }

    public static String convertCelsiusToFahrenheit(String Celsius) {
        double Celcius2;
        try {
            Celcius2 = Double.parseDouble(Celsius);
        } catch (Exception e) {
            return getApp().getResources().getString(R.string.error);
        }

        return "" + (Celcius2 * 1.8 + 32);
    }

    public static String convertDegreeToDegreeStr(String Degree) {
        // Convert a degree number into a string
        // DegreeStr North = 0 or 360, East = 90, South = 180, West = 270, 0 < Northeast < 90, 90 < Southeast < 180, 180 < Southwest < 270, 270 < Northwest < 360
        int Degree2;
        try {
            if (Degree.contains("º"))
                Degree2 = Integer.parseInt(Degree.substring(0, Degree.indexOf("º")));
            else
                Degree2 = Integer.parseInt(Degree);
        } catch (Exception e) {
            Degree2 = 361;
        }
        String temp;

        switch (Degree2) {
            case 0:
            case 360:
                temp = app.getResources().getString(R.string.north);
                break;
            case 90:
                temp = app.getResources().getString(R.string.east);
                break;
            case 180:
                temp = app.getResources().getString(R.string.south);
                break;
            case 270:
                temp = app.getResources().getString(R.string.west);
                break;
            default:
                if (0 < Degree2 && Degree2 < 90) {
                    temp = app.getResources().getString(R.string.northeast);
                } else if (Degree2 < 180) {
                    temp = app.getResources().getString(R.string.southeast);
                } else if (Degree2 < 270) {
                    temp = app.getResources().getString(R.string.southwest);
                } else if (Degree2 < 360) {
                    temp = app.getResources().getString(R.string.northwest);
                } else {
                    temp = app.getResources().getString(R.string.error);
                }
        }

        return temp;
    }
}
