package com.pointrfsystems.mtu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.pointrfsystems.mtu.data.LocalRepository;
import com.pointrfsystems.mtu.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by an.kovalev on 04.05.2016.
 */
public class PasscodeFragment extends Fragment {

    private final char zero = '0';
    private final char one = '1';
    private final char two = '2';
    private final char three = '3';
    private final char four = '4';
    private final char five = '5';
    private final char six = '6';
    private final char seven = '7';
    private final char eight = '8';
    private final char nine = '9';
    private final char a = 'A';
    private final char b = 'B';
    private final char c = 'C';
    private final char d = 'D';

    @Bind(R.id.blied)
    TextView bleid;
    @Bind(R.id.number_0)
    TextView number_0;
    @Bind(R.id.number_1)
    TextView number_1;
    @Bind(R.id.number_2)
    TextView number_2;
    @Bind(R.id.number_3)
    TextView number_3;
    @Bind(R.id.number_4)
    TextView number_4;
    @Bind(R.id.number_5)
    TextView number_5;
    @Bind(R.id.number_6)
    TextView number_6;
    @Bind(R.id.number_7)
    TextView number_7;
    @Bind(R.id.number_8)
    TextView number_8;
    @Bind(R.id.number_9)
    TextView number_9;
    @Bind(R.id.number_a)
    TextView number_a;
    @Bind(R.id.number_b)
    TextView number_b;
    @Bind(R.id.number_c)
    TextView number_c;
    @Bind(R.id.number_d)
    TextView number_d;
    @Bind(R.id.delete)
    TextView delete;
    @Bind(R.id.passcode_1)
    View passcode_1;
    @Bind(R.id.passcode_2)
    View passcode_2;
    @Bind(R.id.passcode_3)
    View passcode_3;
    @Bind(R.id.passcode_4)
    View passcode_4;
    @Bind(R.id.passcode_5)
    View passcode_5;
    @Bind(R.id.nw_image)
    ImageView nw_image;
    @Bind(R.id.facility_image)
    ImageView facility_image;
    @Bind(R.id.pointrf_image)
    ImageView pointrf_image;
    @Bind(R.id.passcode_warning)
    TextView passcode_warning;


    private StringBuffer password;
    private Subscription subscription;
    private Observable<Void> sevenClicks;
    private LocalRepository localRepository;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passcode, container, false);
        ButterKnife.bind(this, view);
        ((MainActivity) getActivity()).setStatusBarColor(R.color.colorPrimary);
        bleid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).showNext();
            }
        });
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        password = new StringBuffer();
        number_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDigit(zero);
            }
        });
        number_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDigit(one);
            }
        });
        number_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDigit(two);
            }
        });
        number_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDigit(three);
            }
        });
        number_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDigit(four);
            }
        });
        number_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDigit(five);
            }
        });
        number_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDigit(six);
            }
        });
        number_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDigit(seven);
            }
        });
        number_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDigit(eight);
            }
        });
        number_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDigit(nine);
            }
        });
        number_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDigit(a);
            }
        });
        number_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDigit(b);
            }
        });
        number_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDigit(c);
            }
        });
        number_d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDigit(d);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLastDigit();
            }
        });

        sevenClicks = RxView.clicks(nw_image).skip(6);

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localRepository = LocalRepository.getInstance(getContext());
    }

    private void resolveBitmap() {
        String path = localRepository.getPointRfPath();
        if (!path.equals("")) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            pointrf_image.setImageBitmap(bitmap);
        } else {
            pointrf_image.setBackground(getResources().getDrawable(R.drawable.pintrf_logo));
        }

        String path1 = localRepository.getNowanderPath();
        if (!path1.equals("")) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(path1, options);
            nw_image.setImageBitmap(bitmap);
        } else {
            nw_image.setBackground(getResources().getDrawable(R.drawable.no_wander_logo));
        }

        String path2 = localRepository.getFacilityPath();
        if (!path2.equals("")) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(path2, options);
            facility_image.setImageBitmap(bitmap);
        }
    }

    private void addDigit(char digit) {
        if (password.length() > 5) {
            return;
        }
        if (password.length() < 5) {
            password.append(digit);
        }

        if (password.length() == 5) {
            if (passcode_warning.getVisibility() == View.VISIBLE) {
                password.delete(0, 4);
                passcode_warning.setVisibility(View.INVISIBLE);
            } else {
                checkPassword();
                if (!Utils.isNetworkAvailable(getContext())) {
                    ((MainActivity) getActivity()).showToast(getString(R.string.network_error));
                }
            }
        }
        checkVisibility();
    }

    private void checkPassword() {
        if (password.toString().equals(localRepository.getPassword())) {
            if (!localRepository.isAppRegistred()) {
                ((MainActivity) getActivity()).showRegistrationFragment();
            }
        } else {
            passcode_warning.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        resolveBitmap();
        subscription = sevenClicks.subscribe(new Subscriber<Void>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Void aVoid) {
                ((MainActivity) getActivity()).showSettingsFragment();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        subscription.unsubscribe();
    }

    private void checkVisibility() {
        int length = password.length();
        if (length == 0) {
            passcode_1.setVisibility(View.GONE);
            passcode_2.setVisibility(View.GONE);
            passcode_3.setVisibility(View.GONE);
            passcode_4.setVisibility(View.GONE);
            passcode_5.setVisibility(View.GONE);
        } else if (length == 1) {
            passcode_1.setVisibility(View.VISIBLE);
            passcode_2.setVisibility(View.GONE);
            passcode_3.setVisibility(View.GONE);
            passcode_4.setVisibility(View.GONE);
            passcode_5.setVisibility(View.GONE);
        } else if (length == 2) {
            passcode_1.setVisibility(View.VISIBLE);
            passcode_2.setVisibility(View.VISIBLE);
            passcode_3.setVisibility(View.GONE);
            passcode_4.setVisibility(View.GONE);
            passcode_5.setVisibility(View.GONE);
        } else if (length == 3) {
            passcode_1.setVisibility(View.VISIBLE);
            passcode_2.setVisibility(View.VISIBLE);
            passcode_3.setVisibility(View.VISIBLE);
            passcode_4.setVisibility(View.GONE);
            passcode_5.setVisibility(View.GONE);
        } else if (length == 4) {
            passcode_1.setVisibility(View.VISIBLE);
            passcode_2.setVisibility(View.VISIBLE);
            passcode_3.setVisibility(View.VISIBLE);
            passcode_4.setVisibility(View.VISIBLE);
            passcode_5.setVisibility(View.GONE);
        } else if (length == 5) {
            passcode_1.setVisibility(View.VISIBLE);
            passcode_2.setVisibility(View.VISIBLE);
            passcode_3.setVisibility(View.VISIBLE);
            passcode_4.setVisibility(View.VISIBLE);
            passcode_5.setVisibility(View.VISIBLE);
        }
    }

    private void deleteLastDigit() {
        if (password.length() == 0) {
            return;
        }
        password.deleteCharAt(password.length() - 1);
        checkVisibility();
        if (passcode_warning.getVisibility() == View.VISIBLE) {
            passcode_warning.setVisibility(View.INVISIBLE);
        }
    }
}
