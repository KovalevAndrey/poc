package com.pointrfsystems.poc.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.pointrfsystems.poc.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by a.kovalev on 01.06.16.
 */
public class RegistrationFragment extends Fragment {

    @Bind(R.id.password_edit)
    EditText password_edit;
    @Bind(R.id.username_edit)
    EditText username_edit;

    public static RegistrationFragment newInstance() {
        RegistrationFragment registrationFragment = new RegistrationFragment();
        Bundle bundle = new Bundle();
        registrationFragment.setArguments(bundle);
        return registrationFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
