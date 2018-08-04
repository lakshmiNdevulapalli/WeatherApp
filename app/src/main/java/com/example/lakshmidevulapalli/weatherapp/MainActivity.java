package com.example.lakshmidevulapalli.weatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
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

    TextView textView;
    TextInputEditText inputEditText;

    public void weather(View view){
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(inputEditText.getWindowToken(),0);

        /**
         * Cities with weired names are encoded
          */
        try {
            String encodedCityName = URLEncoder.encode(inputEditText.getText().toString(),"UTF-8");

            DownloadWeatherApi weatherApi = new DownloadWeatherApi();
            weatherApi.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName +"&APPID=ea574594b9d36ab688642d5fbeab847e");

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Could not find weather",Toast.LENGTH_LONG);
        }
    }

    public class DownloadWeatherApi extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),"Could not find weather",Toast.LENGTH_LONG);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            String message = "";

            try {
                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("weather");
                Log.i("Website content", weatherInfo);

                JSONArray array = new JSONArray(weatherInfo);
                for(int i = 0; i < array.length(); i++){
                    JSONObject object = array.getJSONObject(i);

                    String main = "";
                    String description = "";
                    main =  object.getString("main");
                    description = object.getString("description");

                    if(main != "" && description!= ""){
                        message = main + " : " + description + "\r\n";
                    }
                }

                if( message != ""){
                    textView.setText(message);
                }else{
                    Toast.makeText(getApplicationContext(),"Could not find weather",Toast.LENGTH_LONG);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputEditText = findViewById(R.id.input);
        textView = findViewById(R.id.showWeather);

    }
}
