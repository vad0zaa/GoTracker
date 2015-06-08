package ee.sinchukov.gotracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity implements LocationListener {

    public static String TAG = "My App";
    private EditText editTextPeriod;
    private  TextView textViewLatitude;
    private TextView textViewLongitude;
    SimpleDateFormat dateFormat;
    LocationManager locationManager ;
    String provider;
    int period = 5;

    public static final String FILENAME = "location.txt";

    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();

    Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateFormat = new SimpleDateFormat("dd.MMM HH:mm:ss");

        editTextPeriod = (EditText)findViewById(R.id.editTextPeriod);
        editTextPeriod.setText("5");

        textViewLongitude = (TextView)findViewById(R.id.textViewLongitude);
        textViewLatitude = (TextView)findViewById(R.id.textViewLatitude);

        // Getting LocationManager object
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        // Creating an empty criteria object
        Criteria criteria = new Criteria();

        // Getting the name of the provider that meets the criteria
        provider = locationManager.getBestProvider(criteria, false);
    }



    public void startTracker(View view){



        if(provider!=null && !provider.equals("")){

            // get location every x seconds
            try{ period = Integer.parseInt(editTextPeriod.getText().toString());}
            catch(Exception e){}

            // Clean location data log file
            cleanFile(FILENAME);

            // Get the location from the given provider
            Location location = locationManager.getLastKnownLocation(provider);

            locationManager.requestLocationUpdates(provider, period*1000, 1, this);

            if(location!=null) {
                startTimer();
            }
            else
                Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(getBaseContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
        }

    }

    public void stopTracker(View view){
        stopTimer();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        String date = dateFormat.format(new Date());
        textViewLongitude.setText(date + " Longitude:" + location.getLongitude());
        textViewLatitude.setText(date + " Latitude:" + location.getLatitude());

        // next line is commented because we are writing to file with timer
        //appendToFile(date + "," + location.getLongitude() + "," + location.getLatitude(), FILENAME);
    }

    private void appendToFile(String data, String fileName) {
        try {

             OutputStreamWriter outputStreamWriter = new
             OutputStreamWriter(openFileOutput(fileName, Context.MODE_APPEND));
            BufferedWriter buffWriter = new BufferedWriter(outputStreamWriter);
            buffWriter.write(data);
            buffWriter.newLine();
            buffWriter.flush();
            buffWriter.close();
        }
        catch (IOException e) {
            Log.e(MainActivity.TAG, "File write failed: " + e.toString());
        }
    }

    public void showLocationLog(View view){
        Intent intent = new Intent(this,LocationLog.class);
        startActivity(intent);
    }

    private void cleanFile(String fileName) {
        try {
            OutputStreamWriter outputStreamWriter = new
                    OutputStreamWriter(openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write("");
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e(MainActivity.TAG, "File write failed: " + e.toString());
        }
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        String date = dateFormat.format(new Date());
                        Log.d(TAG,date + "," + lastLocation.getLongitude() + "," + lastLocation.getLatitude());
                        appendToFile(date + "," + lastLocation.getLongitude() + "," + lastLocation.getLatitude(),FILENAME);
                        Toast toast = Toast.makeText(getApplicationContext(),date+ " saving location...", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
        };
    }

    public void startTimer(){
        timer = new Timer();
        initializeTimerTask();
        //schedule the timer, after the first 1000ms the TimerTask will run every period*1000ms
        timer.schedule(timerTask, 1000, period*1000);
    }

    public void stopTimer() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
