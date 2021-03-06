package com.pointrfsystems.mtu;

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
import android.widget.Toast;

import com.pointrfsystems.mtu.data.LocalRepository;
import com.pointrfsystems.mtu.views.CustomKeyboard;

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

    CustomKeyboard mCustomKeyboard;

    @Override
    public void onPause() {
        super.onPause();
        bleid_edit.requestFocus();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bleid, container, false);
        ButterKnife.bind(this, view);
        ((MainActivity) getActivity()).setToolbarVisibility(true);
        ((MainActivity) getActivity()).setToolbarName("BLEID");

        ((MainActivity) getActivity()).setStatusBarColor(R.color.toolbar_background);
        localRepository = LocalRepository.getInstance(getContext());
        enter_bleid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToTracking();
            }
        });
        enter_bleid.setEnabled(false);
        bleid_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
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
        mCustomKeyboard = new CustomKeyboard(getActivity(), view, R.id.keyboardview, R.xml.hexkbd, new EnterBleidListener() {
            @Override
            public void onClick() {
                goToTracking();
            }
        });

        mCustomKeyboard.registerEditText(R.id.blied_edit);


        return view;
    }

    private void goToTracking() {
        String bleid = bleid_edit.getText().toString();

        if (bleid.length() < 4) {
            Toast.makeText(getContext(), "Invalid BLE ID message", Toast.LENGTH_LONG).show();
            return;
        }
        localRepository.storeBleid(bleid);
        ((MainActivity) getActivity()).showTrackingFragment(bleid);
    }


    public boolean isCustomKeyboardVisible() {
        return mCustomKeyboard.isCustomKeyboardVisible();
    }

    public void hideCustomKeyboard() {
        mCustomKeyboard.hideCustomKeyboard();
    }

    public interface EnterBleidListener {

        void onClick();
    }
}
