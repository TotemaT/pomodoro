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
import android.os.Handler;
import android.os.Vibrator;
import android.widget.Toast;

/**
 * Handles the timer mechanisms
 */
public class Timer {
    /* Single instance */
    private static Timer mInstance = null;

    /* Timer elements */
    private Handler mHandler;
    private Runnable mRunnable;
    private boolean mStarted;

    private int mCurrentTime;
    private int mTotalTime;
    private boolean mTimeUp;

    /* Timer constants */
    private static final long[] VIBRATOR_PATTERN = {0, 1500, 500, 1500, 500, 1500};
    public static final int TOTAL_TIME = 25 * 60;

    /* Activity using the timer */
    private TimerActivity mActivity;

    private Timer(TimerActivity activity, int totalTime) {
        mActivity = activity;
        mTotalTime = totalTime;
        mCurrentTime = mTotalTime;
        mStarted = false;
        mTimeUp = false;
        mHandler = new Handler();
    }

    public static Timer get(TimerActivity activity, int totalTime) {
        if (mInstance == null) {
            mInstance = new Timer(activity, totalTime);
        } else if (!mInstance.getActivity().equals(activity)
                || mInstance.getTotalTime() != totalTime) {
            /* user clicked on the notification or changed total time in preferences */
            mInstance.setTotalTime(totalTime);
            mInstance.setActivity(activity);
        }
        return mInstance;
    }

    private TimerActivity getActivity() {
        return mActivity;
    }

    private void setActivity(TimerActivity activity) {
        mActivity = activity;
    }

    public int getTotalTime() {
        return mTotalTime;
    }

    public void setTotalTime(int totalTime) {
        mTotalTime = totalTime;
    }

    public void start() {
        mStarted = true;
        if (mTimeUp) {
            mTimeUp = false;
            mActivity.resetUI();
        }
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mCurrentTime--;
                mActivity.updateCircleView();
                if (mCurrentTime <= 0) {
                    mActivity.updateUI(0);
                    Vibrator mVibrator = (Vibrator) mActivity.getSystemService(Activity.VIBRATOR_SERVICE);
                    mVibrator.vibrate(VIBRATOR_PATTERN, -1);
                    stop();
                    Toast.makeText(mActivity, R.string.time_is_up, Toast.LENGTH_LONG).show();
                } else {
                    mActivity.updateUI(mCurrentTime);
                    mHandler.postDelayed(mRunnable, 1000);
                    mActivity.showNotification();
                }
            }
        };
        mActivity.showNotification();
        mHandler.postDelayed(mRunnable, 1000);
    }

    public void pause() {
        mStarted = false;
        mHandler.removeCallbacks(mRunnable);
        mActivity.hideNotification();
    }

    public void stop() {
        pause();
        mCurrentTime = mTotalTime;
        mTimeUp = true;
    }

    public boolean isStarted() {
        return mStarted;
    }

    public int getCurrentTime() {
        return mCurrentTime;
    }
}
