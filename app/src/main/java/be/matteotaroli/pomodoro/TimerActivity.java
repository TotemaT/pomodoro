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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
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

    /* UI elements */
    private TextView mMinutesTv;
    private TextView mSecondsTv;
    private CircleTimerView mCircleTimerView;

    /* Timer elements */
    private Handler mHandler;
    private Runnable mRunnable;
    private boolean mTimerStarted;

    private int mTimerCurrentTime;

    /* Timer constants */
    private static final int TIMER_START_TIME = 25 * 60;
    private static final long[] VIBRATOR_PATTERN = {0, 1500, 500, 1500, 500, 1500};

    @Override
    @TargetApi(21)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        /* Set status bar color on KitKat+ */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.secondary_colour));
        }

        mCircleTimerView = (CircleTimerView) findViewById(R.id.circle_timerview);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer();
            }
        });
        mMinutesTv = (TextView) findViewById(R.id.minutes_textview);
        mSecondsTv = (TextView) findViewById(R.id.seconds_textview);

        mTimerCurrentTime = TIMER_START_TIME;
        mTimerStarted = false;

        resetUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Resets the timer to the start time.
     */
    public void resetUI() {
        updateUI(TIMER_START_TIME);
        updateCircleView();
    }

    /**
     * Sets the timer to the actual time.
     *
     * @param timeInSeconds Actual time in seconds
     */
    public void updateUI(long timeInSeconds) {
        int minutes = (int) timeInSeconds / 60;
        int seconds = (int) timeInSeconds - minutes * 60;
        mMinutesTv.setText(String.format("%02d", minutes));
        mSecondsTv.setText(String.format("%02d", seconds));
    }

    /**
     * Starts or stops the timer according to its current state.
     */
    public void timer() {
        if (!mTimerStarted) {
            startTimer();
        } else {
            stopTimer();
        }
    }

    /**
     * Starts the timer.
     */
    public void startTimer() {
        Log.d(TAG, "Start timer");
        mTimerStarted = true;
        mTimerCurrentTime = TIMER_START_TIME;
        resetUI();
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mTimerCurrentTime--;
                updateCircleView();
                if (mTimerCurrentTime <= 0) {
                    updateUI(0);
                    Vibrator mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    mVibrator.vibrate(VIBRATOR_PATTERN, -1);
                    stopTimer();
                } else {
                    updateUI(mTimerCurrentTime);
                    mHandler.postDelayed(mRunnable, 1000);
                }
            }
        };
        mHandler.postDelayed(mRunnable, 1000);
    }

    /**
     * Stops the timer.
     */
    public void stopTimer() {
        Log.d(TAG, "Stop timer");
        mTimerStarted = false;
        mHandler.removeCallbacks(mRunnable);
    }

    private void updateCircleView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                float sweepAngle = 360 * (float) mTimerCurrentTime / (float) TIMER_START_TIME;
                mCircleTimerView.setSweepAngle(sweepAngle);
            }
        });
    }
}