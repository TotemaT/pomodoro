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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Splash screen showing the logo before starting the main activity.
 */

public class SplashScreenActivity extends AppCompatActivity {
    /* Used to know is the introduction should be shown */
    private static final String PREF_FIRST_TIME = "be.matteotaroli.pomodoro.firstTime";
    private static final int SPLASH_TIMEOUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;

                if (getPreferences(Context.MODE_PRIVATE).getBoolean(PREF_FIRST_TIME, true)) {
                    getPreferences(Context.MODE_PRIVATE).edit()
                            .putBoolean(PREF_FIRST_TIME, false).apply();
                    intent = new Intent(SplashScreenActivity.this, IntroActivity.class);
                } else {
                    intent = new Intent(SplashScreenActivity.this, TimerActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIMEOUT);
    }
}
