package com.example.weather;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    // URL to get weathers JSON
    private static final String url = "https://api.openweathermap.org/data/2.5/find?lat=20.5937&lon=78.9629&cnt=10&appid=bd1b01db2aff17721085c66c03583a2e";
    private final String TAG = MainActivity.class.getSimpleName();
    ArrayList<HashMap<String, String>> WeatherList;
    private ProgressDialog pDialog;
    private ListView lv;
    private NotificationManagerCompat notificationManagr;
    public static final String CHANNEL_2_ID = "A2";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WeatherList = new ArrayList<>();

        lv = findViewById(R.id.list);

        new weatherdata().execute();
        notificationManagr = NotificationManagerCompat.from(this);

        createNotificatioChannels();




    }


    public void ch2(String string,String x) {

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.clouds);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.clouds)
                .setColor(Color.MAGENTA)
                .setContentText("Current Temparature:"+string)
                .setLargeIcon(icon)
                .build();
        notificationManagr.notify(2, notification);


    }
    private void createNotificatioChannels()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel channel2=new NotificationChannel(
                    CHANNEL_2_ID,
                    "A2",
                    NotificationManager.IMPORTANCE_LOW
            );


            NotificationManager manager =getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel2);

        }

    }



    public String displaytemp(String temp){

        float tempx = Float.parseFloat(String.valueOf(temp));
        int x= Math.round(tempx-273);
        return String.valueOf(x);
    }


    @SuppressWarnings("deprecation")
    @SuppressLint("StaticFieldLeak")
    private class weatherdata extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);


                    JSONArray array = jsonObj.getJSONArray("list");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject child = array.getJSONObject(i);
                        String name = child.getString("name");

                        JSONObject coord = child.getJSONObject("coord");
                        String lat = coord.getString("lat");
                        String lon = coord.getString("lon");

                        JSONObject main = child.getJSONObject("main");
                        String temp = main.getString("temp");
                        String temp_min = main.getString("temp_min");
                        String temp_max = main.getString("temp_max");
                        String hum = main.getString("humidity");

                        JSONObject wind = child.getJSONObject("wind");
                        String win = wind.getString("speed");

                        HashMap<String, String> weather = new HashMap<>();

                        JSONArray wea = child.getJSONArray("weather");
                        String des = null;
                        String icon=null;
                        {
                            for (int j = 0; j < wea.length(); j++) {
                                JSONObject d = wea.getJSONObject(j);
                                des = d.getString("description");
                                icon=d.getString("icon");
                            }

                        }

                        weather.put("icon", icon);
                        weather.put("des", des);
                        weather.put("name", name);
                        weather.put("temp", displaytemp(temp) + "°C");
                        weather.put("wind", win);
                        weather.put("lat", lat);
                        weather.put("lon", lon);

                        weather.put("temp_min", displaytemp(temp_min));
                        weather.put("temp_max", displaytemp(temp_max));
                        weather.put("hum", hum);


                        WeatherList.add(weather);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show());

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                        "Couldn't get json from server. Check LogCat for possible errors!", Toast.LENGTH_LONG).show());

            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            ListAdapter adapter = new SimpleAdapter(MainActivity.this,
                    WeatherList,
                    R.layout.list_item,
                    new String[]{"name", "des", "temp"},
                    new int[]{R.id.City_Name, R.id.Weather, R.id.Temp});

            lv.setAdapter(adapter);

            lv.setOnItemClickListener((arg0, arg1, position, arg3) -> {
                Toast.makeText(MainActivity.this, WeatherList.get(position).get("name")
                        + " " , Toast.LENGTH_SHORT).show();

                String t=WeatherList.get(position).get("temp");
                String x=WeatherList.get(position).get("icon");
                Log.e("TAG", "onPostExecute: "+t );

                Intent intent = new Intent(getApplicationContext(),MainActivity2.class);
                intent.putExtra("name",WeatherList.get(position).get("name"));
                intent.putExtra("hum",WeatherList.get(position).get("hum"));
                intent.putExtra("wind",WeatherList.get(position).get("wind"));
                intent.putExtra("temp",WeatherList.get(position).get("temp"));
                intent.putExtra("temp_min",WeatherList.get(position).get("temp_min"));
                intent.putExtra("temp_max",WeatherList.get(position).get("temp_max"));
                intent.putExtra("lon",WeatherList.get(position).get("lon"));
                intent.putExtra("lat",WeatherList.get(position).get("lat"));
                intent.putExtra("des",WeatherList.get(position).get("des"));
                intent.putExtra("icon", WeatherList.get(position).get("icon"));
                startActivity(intent);
                ch2(t,x);


            });


        }

    }


}