package com.pointrfsystems.mtu.views;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.pointrfsystems.mtu.data.LocalRepository;
import com.pointrfsystems.mtu.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by a.kovalev on 27.05.16.
 */
public class EditLinkFragment extends DialogFragment {

    @Bind(R.id.edit)
    EditText editText;
    @Bind(R.id.ok)
    Button ok;

    private LocalRepository localRepository;
    private static final String PATH = "path";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localRepository = LocalRepository.getInstance(getContext());
    }


    public static EditLinkFragment newInstance(String path) {
        EditLinkFragment editLinkFragment = new EditLinkFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PATH, path);
        editLinkFragment.setArguments(bundle);
        return editLinkFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_link, container, false);
        ButterKnife.bind(this, view);
        getDialog().setTitle("Edit link");

        Bundle bundle = getArguments();

        if (bundle != null) {
            String path = bundle.getString(PATH);
            if (path != null) {
                editText.setText(path);
            }
        }

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localRepository.storeApiLink(editText.getText().toString());
                dismiss();
            }
        });

        return view;

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
