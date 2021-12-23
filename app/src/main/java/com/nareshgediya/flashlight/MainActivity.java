package com.nareshgediya.flashlight;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity {
    Button button, stopBtn;
    boolean state, on;
    SeekBar seekBar;
    long blinkDelay =10;
    SecondThread secondThread;
    TextView pText;
    Switch aSwitch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    button = findViewById(R.id.button1);
    stopBtn = findViewById(R.id.stopBtn);
    aSwitch= findViewById(R.id.switch1);

seekBar = findViewById(R.id.seekBar);
pText = findViewById(R.id.pText);


        Dexter.withContext(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                runFlashlight();

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(MainActivity.this, "Camera permission required.", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

            }
        }).check();

        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        secondThread = new SecondThread(cameraManager);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                blinkDelay = i;

                pText.setText(""+i+" milliSec.");
                secondThread.setSecondDelay(blinkDelay);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!secondThread.on){
                    CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                    secondThread = new SecondThread(cameraManager);
                    secondThread.setSecondDelay(blinkDelay);
                    secondThread.start();
                }
                on = true;
                secondThread.onOff(on);
            }});

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                on = false;
                secondThread.onOff(on);
                try {
                    String cameraId = cameraManager.getCameraIdList()[0];
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cameraManager.setTorchMode(cameraId, false);
                    }
                    state = true;
                } catch (CameraAccessException e) {

                }


            }
        });


    }

    private void runFlashlight() {

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (!secondThread.on) {

                    if (!b) {
                        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                        try {
                            String cameraId = cameraManager.getCameraIdList()[0];
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                cameraManager.setTorchMode(cameraId, false);
                                aSwitch.setText("Off ");
                            }

                        } catch (CameraAccessException e) {

                        }
                    } else {
                        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

                        try {
                            String cameraId = cameraManager.getCameraIdList()[0];
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                cameraManager.setTorchMode(cameraId, true);
                                aSwitch.setText("On ");
                            }

                        } catch (CameraAccessException e) {

                        }
                    }

                }else {
                    Toast.makeText(MainActivity.this, "Please turn off blinking", Toast.LENGTH_SHORT).show();
                }
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                blinkDelay = i;

                pText.setText(""+i);
                secondThread.setSecondDelay(blinkDelay);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (secondThread == null){
//                    CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
//                    secondThread = new SecondThread(cameraManager);
//                }
                on = true;
                secondThread.onOff(on);



            }
        });

    }

    private void blinkFlash()
    {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String myString = "01010101010101010101010101010101";
        //Delay in ms
        for (int i = 0; i < myString.length(); i++) {
            if (myString.charAt(i) == '0') {
                try {
                    String cameraId = cameraManager.getCameraIdList()[0];
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cameraManager.setTorchMode(cameraId, true);
                    }
                } catch (CameraAccessException e) {
                }
            } else {
                try {
                    String cameraId = cameraManager.getCameraIdList()[0];
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cameraManager.setTorchMode(cameraId, false);
                    }
                } catch (CameraAccessException e) {
                }
            }
            try {
                Thread.sleep(this.blinkDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }}

    private void startBlinking(CameraManager cameraManager, String cameraId) {



                for (int n=0; n<100; n++){

                    if (!state){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            try {
                                cameraManager.setTorchMode(cameraId, true);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }
                        state = true;
                        Log.d("TAG", "ON");
                        try {
                            Thread.sleep(200);
                            Log.d("TAG", "Sleep");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                    else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            try {
                                cameraManager.setTorchMode(cameraId, false);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }
                        state = false;
                        Log.d("TAG", "OFF");
                        try {
                            Thread.sleep(200);
                            Log.d("TAG", "Sleep");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }

    }
}

class SecondThread extends  Thread implements Runnable{
    long secondDelay;
    CameraManager cameraManager;
    boolean on;
    boolean state;

    public SecondThread( CameraManager cameraManager) {
        this.cameraManager = cameraManager;

    }


    public void onOff(boolean on){
        this.on = on;
        Log.d("TAG", "on offf "+on);
    }

    public void setSecondDelay(long secondDelay){
        this.secondDelay = secondDelay;
        Log.d("TAG", "second Delay"+secondDelay);
    }


    @Override
    public void run() {
      //  CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        String myString = "01010101010101010101010101010101";

        //Delay in ms
//        for (int i = 0; i < myString.length(); i++) {
//            if (myString.charAt(i) == '0') {
//                try {
//                    String cameraId = cameraManager.getCameraIdList()[0];
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        cameraManager.setTorchMode(cameraId, true);
//                    }
//                } catch (CameraAccessException e) {
//                }
//            } else {
//                try {
//                    String cameraId = cameraManager.getCameraIdList()[0];
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        cameraManager.setTorchMode(cameraId, false);
//                    }
//                } catch (CameraAccessException e) {
//                }
//            }
//            try {
//                Thread.sleep(secondDelay);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

        Log.d("TAG", "TAg before loop  "+on);
        while (on){
            if (!state){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        String cameraId = cameraManager.getCameraIdList()[0];
                        cameraManager.setTorchMode(cameraId, true);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                state = true;
              //  Log.d("TAG", "ON");
                try {
                    Thread.sleep(secondDelay);
                  //  Log.d("TAG", "Sleep");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        String cameraId = cameraManager.getCameraIdList()[0];
                        cameraManager.setTorchMode(cameraId, false);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                state = false;
            //    Log.d("TAG", "OFF");
                try {
                    Thread.sleep(secondDelay);
             //       Log.d("TAG", "Sleep");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}