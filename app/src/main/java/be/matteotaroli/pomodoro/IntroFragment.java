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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Manages the introduction CardViews
 */
public class IntroFragment extends Fragment {
    private static final String PAGE = "page";

    private int mPage;
    private ViewPager mPager;
    private final int[][] pages = {
            {R.string.intro_welcome, R.string.intro_before_starting, R.drawable.pomodoro, R.string.logo_description},
            {R.string.what_is_it, R.string.pomodoro_explanation},
            {R.string.intro_easy, R.string.intro_explanation_1}
    };

    public static IntroFragment newInstance(int page) {

        Bundle args = new Bundle();
        args.putInt(PAGE, page);

        IntroFragment fragment = new IntroFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getArguments().containsKey(PAGE)) {
            throw new RuntimeException("Fragment should contain a page.");
        }
        mPage = getArguments().getInt(PAGE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_intro_page, container, false);

        mPager = ((IntroActivity) getActivity()).getPager();
        TextView titleTv = (TextView) view.findViewById(R.id.intro_title_textView);
        TextView textTv = (TextView) view.findViewById(R.id.intro_text_textView);
        ImageView pictureIV = (ImageView) view.findViewById(R.id.intro_picture_imageView);
        TextView skipTv = (TextView) view.findViewById(R.id.intro_skip);
        TextView nextTv = (TextView) view.findViewById(R.id.intro_next);

        View.OnClickListener skipIntro = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TimerActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        };
        View.OnClickListener next = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(mPage + 1);
            }
        };

        if (mPage == IntroAdapter.PAGE_COUNT - 1) {
            /* Last page */
            skipTv.setVisibility(View.INVISIBLE);
            nextTv.setText(R.string.lets_start);
            nextTv.setOnClickListener(skipIntro);

            titleTv.setText(R.string.intro_end_title);
            textTv.setText(R.string.intro_end_text);
            pictureIV.setImageResource(R.drawable.pomodoro);
            pictureIV.setContentDescription(getResources().getString(R.string.logo_description));
        } else {
            /* Any other page */
            skipTv.setOnClickListener(skipIntro);
            nextTv.setText(R.string.intro_next);
            nextTv.setOnClickListener(next);

            titleTv.setText(pages[mPage][0]);
            textTv.setText(pages[mPage][1]);

        /*  TODO  - v 0.1.0 - add pictures and uncomment those lines
            pictureIV.setImageResource(pages[mPage][2]);
            pictureIV.setContentDescription(getResources().getString(pages[mPage][3]));
        */
        }

        view.setTag(mPage);
        return view;
    }
}
