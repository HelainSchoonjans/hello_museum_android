package jonathanperrinet.fr.dialler_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private String currentTel = "+33 666 666 666";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = (Spinner) findViewById(R.id.spinner_tel);
        ArrayList<String> tels = new ArrayList<>();
        tels.add("+32 473 411 314");
        tels.add("+33 777 777 777");

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tels);

        spinner.setAdapter(adapter);
        /*spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d(TAG, adapter.getItem(position));
            }
        });*/
    }

    public void onClickCall(View view) {
        // Samsung 1
        String number = "+32 494 282 306";
        // Samsung 2
//        String number = "+32 494 283 665";
        // TODO HSC: change the number
        Uri call = Uri.parse("tel:" + number);
        Intent callIntent = new Intent(Intent.ACTION_CALL, call);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CALL_PHONE)) {
            } else {
                /*ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);*/
            }
        }
        startActivity(callIntent);
    }
}
