package demo.sabaki.com.externalfilestats;

import android.content.ComponentName;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    private Button StartScan;
    private Button StopScan;
    private Intent mScanServiceIntent;
    private ComponentName mScanServiceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StartScan = (Button) findViewById(R.id.start_scan_button);
        StopScan = (Button) findViewById(R.id.stop_scan_button);

        StartScan.setOnClickListener(this);
        StopScan.setOnClickListener(this);
    }
    //TODO: this approach looks slightly unfamiliar: compare to others such as SylloGizmo.
    @Override
    public void onClick(View v) {
        if (v.equals(StartScan)) {
            mScanServiceIntent = new Intent(this, StatsService.class);
            mScanServiceName = startService(mScanServiceIntent); // not sure if I will ever use this, bu...
        } else if (v.equals(StopScan)) {
            stopService(mScanServiceIntent);
        } else
        Toast.makeText(this, R.string.no_can_do, Toast.LENGTH_LONG).show(); //TODO: find better R.id val.
    }

    @Override
    protected void onDestroy() {
        stopService (mScanServiceIntent.setComponent(mScanServiceName));
        super.onDestroy();
    }
}
