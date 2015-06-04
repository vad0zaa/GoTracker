package ee.sinchukov.gotracker;

import android.app.ListActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class LocationLog extends ListActivity {

    ArrayList<MyLocation> myLocationsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readFileFromInternalStorage(MainActivity.FILENAME);

        String[] from =new String[] { MyLocation.DATE, MyLocation.LATITUDE, MyLocation.LONGITUDE };
        int[] to=new int[] {R.id.dateView, R.id.latitudeView, R.id.longitudeView };

        ListAdapter adapter = new SimpleAdapter(this, myLocationsList, R.layout.activity_location_log,from,to);
        setListAdapter(adapter);

    }

    void createMyLocationList(String date, String latitude, String longitude) {
        myLocationsList.add(new MyLocation(date, latitude, longitude));
    }

    public void readFileFromInternalStorage(String dataFile){
        String fileName = dataFile;
        String logData = "";
        try {
            String line;
            Log.d("my app", "read log started...");
            BufferedReader reader;
            InputStream stream = openFileInput(fileName);
            reader = new BufferedReader(new InputStreamReader(stream));
            line = reader.readLine();
            while(line != null){
                Log.d("my app","read line: "+ line);
                String[] values = line.split(",");
                Log.d("my app","read values: "+ values[0]+","+values[1]+","+values[2]);
                createMyLocationList(values[0],values[1],values[2]);
                line = reader.readLine();
            }
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }













    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location_log, menu);
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
