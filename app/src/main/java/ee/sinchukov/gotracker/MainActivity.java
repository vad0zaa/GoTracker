package ee.sinchukov.gotracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends Activity implements LocationListener {

    public static String TAG = "my app";
    private  TextView textViewLatitude;
    private TextView textViewLongitude;

    LocationManager locationManager ;
    String provider;

    public static final String FILENAME = "location.txt";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewLongitude = (TextView)findViewById(R.id.textViewLongitude);
        textViewLatitude = (TextView)findViewById(R.id.textViewLatitude);

        // Getting LocationManager object
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        // Creating an empty criteria object
        Criteria criteria = new Criteria();

        // Getting the name of the provider that meets the criteria
        provider = locationManager.getBestProvider(criteria, false);

        // Clean location data log file
        cleanFile(FILENAME);

    }



    public void startTracker(View view){

        if(provider!=null && !provider.equals("")){

            // Get the location from the given provider
            Location location = locationManager.getLastKnownLocation(provider);

            locationManager.requestLocationUpdates(provider, 1000, 1, this);

            if(location!=null)
                onLocationChanged(location);
            else
                Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(getBaseContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
        }

    }

    public void stopTracker(View view){
        locationManager.removeUpdates(this);
        cleanFile(FILENAME);
    }

    @Override
    public void onLocationChanged(Location location) {

        SimpleDateFormat df = new SimpleDateFormat("dd.MMM HH:mm:ss");
        String date = df.format(new Date());

        textViewLongitude.setText(date + " Longitude:" + location.getLongitude());
        textViewLatitude.setText(date + " Latitude:" + location.getLatitude());

        appendToFile(date + "," + location.getLongitude() + "," + location.getLatitude(), FILENAME);
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
