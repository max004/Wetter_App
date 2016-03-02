package com.example.max.apibsp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class WetterActivity extends Activity {

    private static final String DEBUG_TAG = "HttpExample";
    private String stringUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20title%2C%20item.title%2C%20item.yweather%3Acondition.temp%20from" +
                               "%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22Innsbruck%2C%20" +
                               "at%22)&format=xml&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

    private TextView temperature;
    private TextView conditionFor;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wetteractivity);

        conditionFor = (TextView) findViewById(R.id.conditions);
        title = (TextView) findViewById(R.id.title);
        temperature = (TextView) findViewById(R.id.temperature);

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Toast.makeText(getBaseContext(), "Netzwerkverbindung erfolgreich", Toast.LENGTH_SHORT).show();
            new DownloadWebpageTask().execute(stringUrl);
        } else {
            Toast.makeText(getBaseContext(), "Netzwerkverbindung fehlgeschlagen", Toast.LENGTH_SHORT).show();
        }

        // Gets the URL from the UI's text field.
    //    connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
      //  networkInfo = connMgr.getActiveNetworkInfo();
        //if (networkInfo != null && networkInfo.isConnected()) {

        //} else {
          //  Toast.makeText(getBaseContext(), "No Network Connection available", Toast.LENGTH_SHORT).show();
        //}

    }

    public void onClickListener(View view){

        Intent intent = new Intent(WetterActivity.this, MainActivity.class);
        startActivity(intent);

    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {


            String[] text = result.split(">");
            String[] subtextTitle;
            String[] subtextTemperature;
            String[] subtextCondition;
            int temperatureCelsius = 0;

            subtextCondition = text[9].split("<");
            subtextTitle = text[5].split("<");
            subtextTemperature = text[7].split("\"");

            temperatureCelsius = Integer.parseInt(subtextTemperature[3]);
            temperatureCelsius = (int) ((temperatureCelsius - 32) / 1.8);

            conditionFor.setText(subtextCondition[0]);
            title.setText(subtextTitle[0]);
            temperature.setText("Temperatur: "+temperatureCelsius+"°C");


        }


        // Given a URL, establishes an HttpUrlConnection and retrieves
// the web page content as a InputStream, which it returns as
// a string.
        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(DEBUG_TAG, "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, len);
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }


        // Reads an InputStream and converts it to a String.
        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);

        }

    }
}
