package com.pointrfsystems.poc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by an.kovalev on 05.05.2016.
 */
public class BleidFragment extends Fragment {

    @Bind(R.id.enter_bleid)
    Button enter_bleid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bleid, container, false);
        ButterKnife.bind(this, view);
        enter_bleid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).showNext();
            }
        });
        return view;
    }
}
