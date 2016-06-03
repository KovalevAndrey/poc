package com.pointrfsystems.poc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.pointrfsystems.poc.data.LocalRepository;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by an.kovalev on 05.05.2016.
 */
public class BleidFragment extends Fragment {

    @Bind(R.id.enter_bleid)
    Button enter_bleid;
    @Bind(R.id.blied_edit)
    EditText bleid_edit;

    private LocalRepository localRepository;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bleid, container, false);
        ButterKnife.bind(this, view);
        localRepository = LocalRepository.getInstance(getContext());
        enter_bleid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bleid = bleid_edit.getText().toString();
                localRepository.storeBleid(bleid);
                ((MainActivity) getActivity()).showTrackingFragment(bleid);
            }
        });
        enter_bleid.setEnabled(false);
        bleid_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()==0) {
                    enter_bleid.setText(getString(R.string.enter_bleid));
                    enter_bleid.setEnabled(false);
                } else {
                    enter_bleid.setText(getString(R.string.bleid_go_to));
                    enter_bleid.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }
}
