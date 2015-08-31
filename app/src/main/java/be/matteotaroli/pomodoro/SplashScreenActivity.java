package be.matteotaroli.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Matt on 31/08/15.
 */
public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASHTIMEOUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen_layout);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreenActivity.this, TimerActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASHTIMEOUT);
    }
}
