package be.matteotaroli.pomodoro;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Matt on 5/09/15.
 */
public class IntroAdapter extends FragmentPagerAdapter {
    public IntroAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        return IntroFragment.newInstance(position);
    }
}
