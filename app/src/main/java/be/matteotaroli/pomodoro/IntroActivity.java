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
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.viewpagerindicator.CirclePageIndicator;

/**
 * Shows a quick explaination of the way the app works, the first time the user launch the app
 */

public class IntroActivity extends AppCompatActivity {


    @Override
    @TargetApi(21)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

         /* Set status bar color on Lollipop+ */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.secondary_colour));
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.intro_viewPager);
        CirclePageIndicator pageIndicator = (CirclePageIndicator) findViewById(R.id.intro_indicator);

        viewPager.setAdapter(new IntroAdapter(getSupportFragmentManager()));
        pageIndicator.setViewPager(viewPager);

        Button button = (Button) findViewById(R.id.intro_button);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(IntroActivity.this, TimerActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}
