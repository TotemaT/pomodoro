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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
    private static final String PREF_TOTAL_TIME = "be.matteotaroli.pomodoro.totalTime";
    public static final String IS_LONG_CLICK_EXTRA = "isLongCLick";

    /* UI elements */
    private TextView mTimerTv;
    private CircleTimerView mCircleTimerView;

    /* Timer constants */
    public static final long[] VIBRATOR_PATTERN = {0, 750, 500, 750, 500, 750};
    public static final int TOTAL_TIME = 25 * 60;

    /* Timer */
    private int mTotalTime;
    private Intent timerIntent;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(TimerService.IS_OVER_EXTRA, false)) {
                showEndDialog();
            }
            updateUI(intent.getLongExtra(TimerService.CURRENT_TIME_EXTRA, 0));
        }
    };

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

        if (getIntent().getBooleanExtra(TimerService.IS_OVER_EXTRA, false)) {
            showEndDialog();
        }

        mTotalTime = getPreferences(Context.MODE_PRIVATE)
                .getInt(PREF_TOTAL_TIME, TOTAL_TIME);
        timerIntent = new Intent(this, TimerService.class);
        timerIntent.putExtra(TimerService.TIME_EXTRA, mTotalTime);

        mCircleTimerView = (CircleTimerView) findViewById(R.id.circle_timerview);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerIntent.putExtra(IS_LONG_CLICK_EXTRA, false);
                startService(timerIntent);
            }
        });
        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                timerIntent.putExtra(IS_LONG_CLICK_EXTRA, true);
                startService(timerIntent);
                resetUI();
                return true;
            }
        });
        mTimerTv = (TextView) findViewById(R.id.timer_textview);
        updateUI(timerIntent.getIntExtra(TimerService.TIME_EXTRA, mTotalTime));
    }

    private void showEndDialog() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.time_is_up)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        resetUI();
                    }
                })
                .create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBroadcastReceiver, new IntentFilter(TimerService.ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Resets the timer to the start time.
     */
    public void resetUI() {
        updateUI(TOTAL_TIME);
    }

    /**
     * Sets the timer to the actual time.
     *
     * @param timeInSeconds Actual time in seconds
     */
    public void updateUI(long timeInSeconds) {
        int minutes = (int) timeInSeconds / 60;
        int seconds = (int) timeInSeconds % 60;

        String timer = getString(R.string.timer_text,
                String.format("%02d", minutes), String.format("%02d", seconds));
        mTimerTv.setText(timer);

        updateCircleView(timeInSeconds);
    }

    /**
     * Update the circle view with the correct angle corresponding to the time left.
     *
     * @param timeInSeconds Time left in seconds.
     */
    public void updateCircleView(final long timeInSeconds) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                float sweepAngle =
                        360 * (float) timeInSeconds / (float) mTotalTime;
                mCircleTimerView.setSweepAngle(sweepAngle);
            }
        });
    }
}