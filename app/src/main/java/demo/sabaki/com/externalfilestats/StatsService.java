package demo.sabaki.com.externalfilestats;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.Binder;
import android.os.Handler;
import android.app.NotificationManager;
import android.app.Notification;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import static android.os.Environment.MEDIA_MOUNTED;
import static android.os.Environment.MEDIA_MOUNTED_READ_ONLY;
import static demo.sabaki.com.externalfilestats.R.id.text;

public class StatsService extends Service {
    private NotificationManager mNM;
    volatile boolean stopRequested = false;
    // Unique Identification Number for the Notification.
    // UIN to start Notification, and to cancel it.
    private int NOTIFICATION = R.string.local_service_started; // we hope that is unique;)

    /**
     * Class for clients to use to access this service.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC, i.e. AIDL.
     */
    public class LocalBinder extends Binder {
        StatsService getService() {   // we extend Binder, but use none of its methods. Nor is there an
            return StatsService.this; // for this.
        }
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
    }

    @Override
    // called if someone calls onStartService, i.e. is started Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("StatsService", "Received start id " + startId + ": " + intent);
        Handler serviceHandler = new Handler();
        new Thread() {
            public void run() {
                File primaryExternalStorage;
                String mountState;
                boolean isMounted = false;
                mountState = Environment.getExternalStorageState(); //TODO: fill this out.
                if (mountState.equals(MEDIA_MOUNTED) || mountState.equals(MEDIA_MOUNTED_READ_ONLY))
                    isMounted = true;
                if (isMounted) {
                    primaryExternalStorage = Environment.getExternalStorageDirectory(); // primary only for now.
                }
            }
        }.start();
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        //R store startId to use w/ stopSelfLog
        //R use Service.stopSelf() or Context.stopService(intent) when job/task done.

        return START_STICKY;
    }

    @Override
    // called IF stopSelf(), stopService AND no connections w/ Context.BIND_AUTO_CREATE flag.
    public void onDestroy() {
        Log.i("StatsService", "destroyed");
        stopRequested = true;
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);
        // Tell the user we stopped.
        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_LONG).show();
    }

    @Override
    // called by system as a result of LocalServiceClient calling Context.bindService()
    public IBinder onBind(Intent intent) {
        return mBinder; // set exclusively by LocalBinder call below.
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();
    // a bit confusing that he put this here, between two  callback definitions: it is executed
    // when LocalService instantiated.

    /**
     * Show a notification while this service is running.
     * I may axe this, but it might be handy for debug.
     */
    private void showNotification() {
        NotificationCompat.Builder nb = new NotificationCompat.Builder(this);
        // In this sample, we'll use the same text for the ticker and the expanded notification
        String text = (String) getText(R.string.local_service_started);

        // Set the icon, scrolling text and timestamp
        nb.setSmallIcon(R.drawable.notif_icon);
        nb.setContentText("Scan Progress");
        nb.setContentTitle(text);
        nb.setProgress(100, 50, false); //TODO: provide real values
        Notification notification = nb.build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }
}

