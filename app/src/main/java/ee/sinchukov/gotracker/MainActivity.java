package ee.sinchukov.gotracker;

import android.app.Activity;
import android.content.Context;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity implements LocationListener {

    public static String TAG = "my app";
    private  TextView textViewLatitude;
    private TextView textViewLongitude;

    LocationManager locationManager ;
    String provider;

    public static final String FILENAME = "location.txt";

    String logData = "no data";

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
    }

    @Override
    public void onLocationChanged(Location location) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        String date = df.format(new Date());

        textViewLongitude.setText(date + " Longitude:" + location.getLongitude());
        textViewLatitude.setText(date + " Latitude:" + location.getLatitude());

        writeToFile(date + " Longitude:" + location.getLongitude() + " Latitude:" + location.getLatitude(), FILENAME);

    }

    private void writeToFile(String data, String fileName) {
        try {
                    OutputStreamWriter outputStreamWriter = new
                    OutputStreamWriter(openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e(MainActivity.TAG, "File write failed: " + e.toString());
        }
    }

    public void readLog(View view){
        readFileFromInternalStorage(FILENAME);
    }

    public void readFileFromInternalStorage(String dataFile){
        String fileName = dataFile;
        String logData = "log data: ";
                try {
                    String line;
                    Log.d("my app", "read log started...");
                    BufferedReader reader;
                    InputStream stream = openFileInput(fileName);
                    reader = new BufferedReader(new InputStreamReader(stream));
                    line = reader.readLine();
                    while(line != null){
                        Log.d("my app","read line: "+ line);
                        logData = logData +line;
                        line = reader.readLine();
                    }
                 stream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        Log.d("my app","all lines: "+ logData);
        Toast.makeText(this, logData, Toast.LENGTH_LONG).show();
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
