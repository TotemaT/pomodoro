/*
    Pomodoro is a simple Pomodoro Technique app for Android
    Copyright (C) 2015 Matteo Taroli <contact@matteotaroli.be>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package be.matteotaroli.pomodoro;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Runs the timer and visually shows its progression.
 */

public class TimerActivity extends AppCompatActivity {
    private static final String TAG = "TimerActivity";
    private static final int NOTIFICATION_ID = 0;
    private static final String PREF_TOTAL_TIME = "be.matteotaroli.pomodoro.totalTime";

    /* UI elements */
    private TextView mMinutesTv;
    private TextView mSecondsTv;
    private CircleTimerView mCircleTimerView;

    /* Timer*/
    private Timer mTimer;

    @Override
    @TargetApi(21)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        /* Set status bar color on Lollipop+ */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.secondary_colour));
        }

        int totalTime = getPreferences(Context.MODE_PRIVATE)
                .getInt(PREF_TOTAL_TIME, Timer.TOTAL_TIME);

        mTimer = Timer.get(this, totalTime);
        mCircleTimerView = (CircleTimerView) findViewById(R.id.circle_timerview);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mTimer.isStarted()) {
                    startTimer();
                } else {
                    pauseTimer();
                }
            }
        });
        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                stopTimer();
                return true;
            }
        });
        mMinutesTv = (TextView) findViewById(R.id.minutes_textview);
        mSecondsTv = (TextView) findViewById(R.id.seconds_textview);
        updateUI(mTimer.getCurrentTime());
    }

    /**
     * Resets the timer to the start time.
     */
    public void resetUI() {
        updateUI(mTimer.getTotalTime());
        updateCircleView();
    }

    /**
     * Sets the timer to the actual time.
     *
     * @param timeInSeconds Actual time in seconds
     */
    public void updateUI(long timeInSeconds) {
        int minutes = (int) timeInSeconds / 60;
        int seconds = (int) timeInSeconds % 60;
        mMinutesTv.setText(String.format("%02d", minutes));
        mSecondsTv.setText(String.format("%02d", seconds));
    }

    /**
     * Starts the timer.
     */
    public void startTimer() {
        Log.d(TAG, "Start timer");
        mTimer.start();
    }

    /**
     * Pauses the timer, allowing to restart from the same time.
     */
    public void pauseTimer() {
        mTimer.pause();
    }

    /**
     * Stops the timer.
     */
    public void stopTimer() {
        Log.d(TAG, "Stop timer");
        mTimer.stop();
    }

    public void updateCircleView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                float sweepAngle = 360 * (float) mTimer.getCurrentTime() / (float) mTimer.getTotalTime();
                mCircleTimerView.setSweepAngle(sweepAngle);
            }
        });
    }


    public void showNotification() {
        Intent intent = new Intent(this, TimerActivity.class);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Resources resources = getResources();

        String minutes = String.format("%02d", mTimer.getCurrentTime() / 60);
        String seconds = String.format("%02d", mTimer.getCurrentTime() % 60);

        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(resources.getString(R.string.app_name))
                .setSmallIcon(R.drawable.pomodoro_notification)
                .setContentTitle(resources.getString(R.string.app_name))
                .setContentText(resources.getString(R.string.notification_text,minutes, seconds))
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .build();
        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(TAG, NOTIFICATION_ID, notification);
    }

    public void hideNotification() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(TAG, NOTIFICATION_ID);
    }
}