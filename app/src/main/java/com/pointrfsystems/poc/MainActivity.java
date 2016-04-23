package com.pointrfsystems.poc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;


public class MainActivity extends Activity {

    Button button;
    ImageView arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Animation animationRotateCenter = AnimationUtils.loadAnimation(
                this, R.anim.rotate);

        arrow = (ImageView) findViewById(R.id.arrow);

        button = (Button) findViewById(R.id.go);

        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                arrow.startAnimation(animationRotateCenter);
            }
        });


        initialize();

    }


    private void initialize() {
        Uart uart = new Uart(new UartCallback() {
            @Override
            public void bleMsg(String bleId, byte rssi, byte[] macAddr, byte[] rawMsg) {
                Log.e("APP", rssi + " ");
            }
        });

        uart.start();

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
