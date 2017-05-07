package edu.deanza.cis53_hw4_99951;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.net.*;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String[] data ={
                "Monday 6/23 - Sunny",
                "Tuesday 6/24 - Foggy",
                "Wednesday 6/25 - Cloudy",
                "Thursday 6/26 - Rainy",
                "Friday 6/27 - Foggy",
                "Saturday 6/28 - TRAPPED IN WEATHER STATION",
                "Sunday 6/29 - Sunny"
        };

        List<String> weekForecast = new ArrayList<String>(Arrays.asList(data));
        ArrayAdapter<String> forecastAdapter =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.list_item_forecast,
                        R.id.list_item_forecast_textview,
                        weekForecast);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(forecastAdapter);

        return rootView;
    }

    static class FetchWeatherTask extends AsyncTask<String, Void, String[]>{

        @Override
        protected String[] doInBackground(String... data) {
            getWeatherData(data[0], data[1]);
            return new String[0];
        }

        public void getWeatherData(String city, String dayCount){
            //
            // HTTP request sample code
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            // https://docs.oracle.com/javase/7/docs/api/java/net/HttpURLConnection.html
            //
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                String apiID = "c47e0b71e2f535d7335321540aba0f66"; // Use your API key that openweathermap Web sent you

                // BUILD URI object
                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";
                final String APPID = apiID;
                Uri builtUri = Uri.parse(FORECAST_BASE_URL)
                        .buildUpon()
                        .appendQueryParameter(QUERY_PARAM, city)
                        .appendQueryParameter(FORMAT_PARAM, "json")
                        .appendQueryParameter(UNITS_PARAM, "imperial")
                        .appendQueryParameter(DAYS_PARAM, dayCount)
                        .appendQueryParameter(APPID_PARAM, APPID)
                        .build();
                URL url = new URL(builtUri.toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(MainActivity.APP_TAG, "HTTP IO Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                forecastJsonStr = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                    Log.d(MainActivity.APP_TAG, forecastJsonStr);
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(MainActivity.APP_TAG, "HTTP Error in closing ", e);
                    }
                }
            }
        }
    }
}
