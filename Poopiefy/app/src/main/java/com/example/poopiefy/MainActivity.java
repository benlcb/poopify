package com.example.poopiefy;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;

import uk.co.lemberg.motiondetectionlib.MotionDetector;

public class MainActivity extends AppCompatActivity {

    private final String CHANNEL_ID = "ch1";
    Context myContext;
    NotificationCompat.Builder builder;
    NotificationManagerCompat notificationManager;
    int detection_count = 0;

    private static final String TAG = MainActivity.class.getSimpleName();
    private MotionDetector motionDetector;
    private ScrollView scrollLogs;
    private TextView textLogs;

    private DateFormat dateFormat;
    private DateFormat timeFormat;



    private final MotionDetector.Listener gestureListener = new MotionDetector.Listener() {
        @Override
        public void onGestureRecognized(MotionDetector.GestureType gestureType) {
            //motion detected
            //showToast(gestureType.toString());
            //addLog("Gesture detected: " + gestureType);
            //Log.d(TAG, "Gesture detected: " + gestureType);

            if(detection_count++ % 2 == 0) {
                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(1, builder.build());
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        scrollLogs = findViewById(R.id.scrollLogs);
        textLogs = findViewById(R.id.textLogs);

        myContext = this.getApplicationContext();

        //enable notifications
        createNotificationChannel();

        builder = new NotificationCompat.Builder(myContext, "ch1")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Cleanliness is a virtue.")
                .setContentText("Please wash your hands, you dirty pig!")
                .setPriority(NotificationCompat.PRIORITY_MAX);

        notificationManager = NotificationManagerCompat.from(this);

        //Motion Detector
        motionDetector = new MotionDetector(this, gestureListener);

        try {
            motionDetector.start();
        }
        catch (Exception e) {
            e.printStackTrace();
            showToast("Failed to start motion detector. Error:" + e);
        }
    }

    private DateFormat getDateFormat() {
        if (dateFormat == null) {
            dateFormat = android.text.format.DateFormat.getDateFormat(this);
        }
        return dateFormat;
    }

    private DateFormat getTimeFormat() {
        if (timeFormat == null) {
            timeFormat = android.text.format.DateFormat.getTimeFormat(this);
        }
        return timeFormat;
    }

    private void addLog(String str) {
        Date date = new Date();
        String logStr = String.format("[%s %s] %s\n", getDateFormat().format(date), getTimeFormat().format(date), str);
        textLogs.append(logStr);
        scrollLogs.fullScroll(View.FOCUS_DOWN);
    }

    private void showToast(String str) {
        Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        motionDetector.stop();
        super.onDestroy();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ch1";
            String description = "Washing hands channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
