package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    TextView weatherData;

    public class DownloadTask extends AsyncTask<String,Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                String stringData = "";
                String description = "";
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");

                String loc = "Location: " + jsonObject.getString("name") + "\n";
                String temp = "Temperature: " + jsonObject.getJSONObject("main").getString("temp") + "°C" + "\n";
                JSONArray arr = new JSONArray(weatherInfo);
                for(int i = 0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    description = "Condition: " + jsonPart.getString("description") + "\n";
                }
                String wind = "Wind Speed: " + jsonObject.getJSONObject("wind").getString("speed") + " meter/sec";
                if(!loc.isEmpty() && !temp.isEmpty() && !description.isEmpty() && !wind.isEmpty()) {
                    stringData = loc + temp + description + wind;
                    weatherData.setText(stringData);
                }

            } catch (Exception e) {
                weatherData.setText("Error! Location not found.");
                Toast.makeText(getApplicationContext(), "Could not find weather info :(", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    public void getWeather(View view) {
        try {
            DownloadTask task = new DownloadTask();
            String location = editText.getText().toString();
            String encodedLocation = URLEncoder.encode(location,"UTF-8");
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);
            String result = task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + encodedLocation + "&units=metric&appid=53dcfc2c39a5911c35522b8ea110d83f").get();
            Log.i("result",result);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Could not find weather info :(", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.editText);
        weatherData = (TextView) findViewById(R.id.weatherData);
    }
}
