package com.example.n11demo03;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The Opening activity
 * <p>
 * A basic demo application to demonstrate:
 * 1. countdown timer
 * 2. take photos from camera / gallery & upload them to firebase storage
 * 3. download images from firebase storage & display them
 * <p>
 * This activity is the opening activity to that demonstrate countdown timer
 * and pass to the next activity
 * </p>
 *
 * @author Levy Albert albert.school2015@gmail.com
 * @version 2.0
 * @since 01/12/2023
 */public class OpeningActivity extends AppCompatActivity {

    private TextView tVTimer;
    private CountDownTimer cDT;
    private long DURATION = 10*1000;
    private long seconds, centiSeconds;
    private String formattedTime;
    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        tVTimer = findViewById(R.id.tVTimer);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
        }

        cDT = new CountDownTimer(DURATION, 10) {
            // countDownInterval for centi-seconds = 10
            // countDownInterval for seconds = 1000
            @Override
            public void onTick(long mSecToFinish) {
                updateDidplay(mSecToFinish);
            }

            @Override
            public void onFinish() {
                Toast.makeText(OpeningActivity.this, "Passing to the next Activity", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OpeningActivity.this, MainActivity.class);
                startActivity(intent);
            }
        };
    }
    /**
     * onRequestPermissionsResult method
     * <p> Method triggered by other activities returning result of permissions request
     * </p>
     *
     * @param requestCode the request code triggered the activity
     * @param permissions the array of permissions granted
     * @param grantResults the array of permissions granted
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE_PERMISSION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * btnStart method
     * <p> Start the countdown timer for 10 seconds
     * </p>
     *
     * @param view the view that triggered the method
     */
    public void btnStart(View view) {
        cDT.start();
    }

    /**
     * updateDidplay method
     * <p> Update the display of the countdown timer
     * </p>
     *
     * @param mSecToFinish the milliseconds left to finish the timer
     */
    private void updateDidplay(long mSecToFinish) {
        seconds = mSecToFinish / 1000;
        centiSeconds = (mSecToFinish / 10) % 100;
        formattedTime = String.format("%02d:%02d", seconds, centiSeconds);
        tVTimer.setText(formattedTime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cDT != null) {
            cDT.cancel();
        }
    }
}
