package be.matteotaroli.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Matt on 5/09/15.
 */
public class IntroFragment extends Fragment {
    private static final String PAGE = "page";

    private int mPage;

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
            throw new RuntimeException("Fragment should containt a page.");
        }
        mPage = getArguments().getInt(PAGE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        switch (mPage) {
            case 0:
                view = getActivity().getLayoutInflater()
                        .inflate(R.layout.fragment_intro_page1, container, false);
                break;
            case 1:
                view = getActivity().getLayoutInflater()
                        .inflate(R.layout.fragment_intro_page2, container, false);
                Button button = (Button) view.findViewById(R.id.intro_button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), TimerActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
                break;
            default:
                /* Shouldn't happen */
                view = super.onCreateView(inflater, container, savedInstanceState);
        }

        view.setTag(mPage);

        return view;
    }
}
