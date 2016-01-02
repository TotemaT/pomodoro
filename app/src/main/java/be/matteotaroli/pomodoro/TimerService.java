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

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import java.util.Date;

/**
 * Handles the timer and broadcast the current time to TimerActivity.
 */

public class TimerService extends Service {

    private static final String TAG = "TimerService";
    private static final int NOTIFICATION_ID = 0;
    public static final String ACTION = "be.matteotaroli.pomodoro.timer";
    public static final String TIME_EXTRA = "totalTime";
    public static final String CURRENT_TIME_EXTRA = "currentTime";
    public static final String IS_OVER_EXTRA = "isOver";

    private Intent intent;

    /* Timer elements */
    private final Handler mHandler = new Handler();
    private final Runnable sendUpdatesToUI = new Runnable() {
        @Override
        public void run() {
            mTimeLeft = mTotalTime - (getTimeInSeconds() - mStartDate);

            intent.putExtra(CURRENT_TIME_EXTRA, mTimeLeft);
            if (mTimeLeft > 0) {
                showOngoingNotification();
                mHandler.postDelayed(this, 1000);
                intent.putExtra(IS_OVER_EXTRA, false);
            } else {
                mRunning = false;
                mPaused = false;
                intent.putExtra(IS_OVER_EXTRA, true);
                Vibrator mVibrator =
                        (Vibrator) getApplication().getSystemService(Activity.VIBRATOR_SERVICE);
                mVibrator.vibrate(TimerActivity.VIBRATOR_PATTERN, -1);
                Toast.makeText(TimerService.this, R.string.time_is_up, Toast.LENGTH_SHORT).show();
                showFinishedNotification();
            }
            sendBroadcast(intent);
        }
    };

    private long mStartDate;
    private long mPausedDate;
    private long mTimeLeft;
    /* TODO : In next version, allows the user to define the total time. */
    private long mTotalTime;
    private boolean mRunning;
    private boolean mPaused;

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(ACTION);
        mRunning = false;
        mPaused = false;
    }

    /*
        Used on older devices.
        On newer ones (API > 5), onStartCommand is called instead
    */
    @Override
    @SuppressWarnings("deprecation")
    public void onStart(Intent intent, int startId) {
        start(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        start(intent);
        return START_STICKY;
    }

    /**
     * Start the service.
     */
    private void start(Intent intent) {
        if (intent == null) {
            return;
        }
        if (!intent.getBooleanExtra(TimerActivity.IS_LONG_CLICK_EXTRA, false)) {
            /* Play / pause */
            if (!mRunning) {
                mRunning = true;
                if (!mPaused) {
                    mTotalTime = intent.getIntExtra(TIME_EXTRA, TimerActivity.TOTAL_TIME);
                    mStartDate = getTimeInSeconds();
                } else {
                    long delay = getTimeInSeconds() - mPausedDate;
                    mTotalTime += delay;
                    mPausedDate = 0;
                }
                mPaused = false;
                mHandler.post(sendUpdatesToUI);
            } else {
                mRunning = false;
                mPaused = true;
                mPausedDate = getTimeInSeconds();
                mHandler.removeCallbacks(sendUpdatesToUI);
                hideNotification();
            }
        } else {
            /* Stop */
            mRunning = false;
            mPaused = false;
            mStartDate = 0;
            mPausedDate = 0;
            mHandler.removeCallbacks(sendUpdatesToUI);
            hideNotification();
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        hideNotification();
        mHandler.removeCallbacks(sendUpdatesToUI);
    }


    /**
     * Create and show the notification saying the timer is running.
     */
    private void showOngoingNotification() {
        String minutes = String.format("%02d", mTimeLeft / 60);
        String seconds = String.format("%02d", mTimeLeft % 60);
        showNotification(getResources().getString(R.string.notification_text, minutes, seconds), false);
    }

    /**
     * Create and show the notification warning that the time is up.
     */
    private void showFinishedNotification() {
        showNotification(getResources().getString(R.string.time_is_up), true);
    }

    /**
     * Create a notification and add it to the notification center.
     *
     * @param content Content of the notification.
     * @param isOver  If the time is up, also tells if the notification should be dismissed
     *                when the user swipe it.
     */
    private void showNotification(String content, boolean isOver) {
        Intent intent = new Intent(this, TimerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(IS_OVER_EXTRA, isOver);

        PendingIntent pendingIntent = PendingIntent
                .getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Resources resources = getResources();

        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(resources.getString(R.string.app_name))
                .setSmallIcon(R.drawable.pomodoro_notification)
                .setContentTitle(resources.getString(R.string.app_name))
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .build();
        if (!isOver) {
            notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        }
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(TAG, NOTIFICATION_ID, notification);
    }

    /**
     * Hides any notification shown by the app.
     */
    private void hideNotification() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(TAG, NOTIFICATION_ID);
    }

    /**
     * Get current time in seconds.
     */
    private long getTimeInSeconds() {
        return new Date().getTime() / 1000;
    }
}
