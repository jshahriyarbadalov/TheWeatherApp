package com.deveducation.theweather;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.cityName);
        resultTextView = findViewById(R.id.resultTextView);

    }

    public void findWeather(View view) {

        try {

            String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");
            DownloadTask task = new DownloadTask();
            task.execute("http://openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=b6907d289e10d714a6e88b30761fae22");
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
        }

    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

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

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                String message = "";

                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("weather");

                Log.i("Weather content", weatherInfo);

                JSONArray array = new JSONArray(weatherInfo);

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonPart = array.getJSONObject(i);

                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                    if (!main.equals("") && !description.equals("")) {
                        message += main + ": " + description + "\r\n";
                    }
                }

                if (!message.equals("")) {
                    resultTextView.setText(message);
                } else {
                    Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
            }


        }
    }
}
