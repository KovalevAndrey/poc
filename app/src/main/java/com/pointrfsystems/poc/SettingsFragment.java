package com.pointrfsystems.poc;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pointrfsystems.poc.data.LocalRepository;
import com.pointrfsystems.poc.utils.Utils;
import com.pointrfsystems.poc.views.EditLinkFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by a.kovalev on 23.05.16.
 */
public class SettingsFragment extends Fragment implements DialogInterface.OnDismissListener {

    @Bind(R.id.link)
    TextView link;
    @Bind(R.id.edit_link)
    Button edit_link;
    @Bind(R.id.logo_pointrf)
    ImageView logo_pointrf;
    @Bind(R.id.logo_nowander)
    ImageView logo_nowander;
    @Bind(R.id.pick_image_pontrf)
    Button image_pick_pointrf;
    @Bind(R.id.pick_image_nowander)
    Button image_pick_nowander;
    @Bind(R.id.mute)
    RadioButton mute;
    @Bind(R.id.vibro)
    RadioButton vibro;
    @Bind(R.id.normal)
    RadioButton normal;
    @Bind(R.id.radio)
    RadioGroup radioGroup;
    @Bind(R.id.register_device)
    Button register_device;

    private static int POINT_RF_IMAGE = 0;
    private static int NO_WANDER = 1;

    private String userChosenTask;

    private final int REQUEST_CAMERA = 0;
    private final int SELECT_FILE = 1;

    private final String TAKE_PHOTO = "Take Photo";
    private final String CHOOSE_FROM_LIBRARY = "Choose from Library";
    private final String CANCEL = "Cancel";
    private LocalRepository localRepository;
    private int currentImage = -1;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localRepository = LocalRepository.getInstance(getContext());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        link.setText(localRepository.getApiLink());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        image_pick_pointrf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentImage = POINT_RF_IMAGE;
                selectImage();
            }
        });
        image_pick_nowander.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 currentImage = NO_WANDER;
                selectImage();
            }
        });
        resolveBitmap();
        resolveVolume();
        resolveLink();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id;
                if (checkedId == mute.getId()) {
                    id = LocalRepository.MUTE;
                } else if (checkedId == vibro.getId()) {
                    id = LocalRepository.VIBRO;
                } else {
                    id = LocalRepository.NORMAL;
                }
                localRepository.storeVolumeSettings(id);
            }
        });

        edit_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getChildFragmentManager();
                EditLinkFragment editLinkFragment = EditLinkFragment.newInstance(link.getText().toString());
                editLinkFragment.show(fm, "edit_link");
            }
        });

        register_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).showRegistrationFragment();
            }
        });

        return view;
    }

    private void resolveLink() {
        link.setText(localRepository.getApiLink());
    }

    private void resolveVolume() {
        int settingsVolume = localRepository.getVolumeSettings();

        if (settingsVolume == LocalRepository.MUTE) {
            radioGroup.check(mute.getId());
        } else if (settingsVolume == LocalRepository.VIBRO) {
            radioGroup.check(vibro.getId());
        } else if (settingsVolume == LocalRepository.NORMAL) {
            radioGroup.check(normal.getId());
        }

    }

    private void resolveBitmap() {
        String path = localRepository.getPointRfPath();
        if (!path.equals("")) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            logo_pointrf.setImageBitmap(bitmap);
        }

        String path1 = localRepository.getNowanderPath();
        if (!path.equals("")) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(path1, options);
            logo_nowander.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Utils.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChosenTask.equals(TAKE_PHOTO)) {
                        cameraIntent();
                    } else if (userChosenTask.equals(CHOOSE_FROM_LIBRARY)) {
                        galleryIntent();
                    } else {
                        //DO ANYTHING
                    }
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA) {
                onSelectFromCameraResult(data);
            }
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bitmap = null;
        if (data != null) {
            try {
                bitmap = Bitmap.createBitmap(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData()));
                Uri uri = data.getData();
                String path = getRealPathFromURI(uri);
                if (currentImage == POINT_RF_IMAGE)
                    localRepository.storePointrfPath(path);
                else
                    localRepository.storeNowanderPath(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (currentImage == POINT_RF_IMAGE) {
            logo_pointrf.setImageBitmap(bitmap);
        } else {
            logo_nowander.setImageBitmap(bitmap);
        }

    }

    private String getRealPathFromURI(Uri contentUri) {
        String result;
        Cursor cursor = getContext().getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            result = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void onSelectFromCameraResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destionation = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");


        FileOutputStream fo;

        try {
            destionation.createNewFile();
            fo = new FileOutputStream(destionation);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (currentImage == POINT_RF_IMAGE) {
            localRepository.storePointrfPath(destionation.getPath());
            logo_pointrf.setImageBitmap(thumbnail);
        } else {
            localRepository.storeNowanderPath(destionation.getPath());
            logo_nowander.setImageBitmap(thumbnail);
        }
    }


    private void selectImage() {
        final CharSequence[] items = {CHOOSE_FROM_LIBRARY, CANCEL};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utils.checkPermission(getActivity());

                if (items[item].equals(TAKE_PHOTO)) {
                    userChosenTask = TAKE_PHOTO;
                    if (result) {
                        cameraIntent();
                    }
                } else if (items[item].equals(CHOOSE_FROM_LIBRARY)) {
                    userChosenTask = CHOOSE_FROM_LIBRARY;
                    if (result) {
                        galleryIntent();
                    }
                } else if (items[item].equals(CANCEL)) {
                    dialog.dismiss();
                }

            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

}
