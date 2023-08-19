package com.example.rettrocam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import be.teletask.onvif.OnvifManager;
import be.teletask.onvif.listeners.OnvifDeviceInformationListener;
import be.teletask.onvif.listeners.OnvifMediaProfilesListener;
import be.teletask.onvif.listeners.OnvifMediaStreamURIListener;
import be.teletask.onvif.listeners.OnvifResponseListener;
import be.teletask.onvif.listeners.OnvifServicesListener;
import be.teletask.onvif.models.OnvifDevice;
import be.teletask.onvif.models.OnvifDeviceInformation;
import be.teletask.onvif.models.OnvifMediaProfile;
import be.teletask.onvif.models.OnvifServices;
import be.teletask.onvif.responses.OnvifResponse;

public class AddressActivity extends AppCompatActivity implements OnvifResponseListener {
    TextView status;
    Button btn_conn;
    EditText hostinput,userinput,passinput;
    boolean recieved=false;
    OnvifManager onvifManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        //parameters

        status    =findViewById(R.id.status);
        btn_conn= findViewById(R.id.btn_conn);
        hostinput=findViewById(R.id.hostinput);
        userinput=findViewById(R.id.userinput);
        passinput=findViewById(R.id.passinput);

        onvifManager = new OnvifManager();
        onvifManager.setOnvifResponseListener( this);

        /*if (getIntent().getExtras()!=null){
            String ip=getIntent().getExtras().get("IP").toString();
            hostinput.setText(ip);
            hostinput.setEnabled(false);
        }else {}*/

        String ips=hostinput.getText().toString();
        String use=userinput.getText().toString();
        String pass=passinput.getText().toString();
        final OnvifDevice device = new OnvifDevice(ips);
        device.setUsername(use);
        device.setPassword(pass);

        btn_conn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recieved) {
                    onvifManager.getServices(device, new OnvifServicesListener() {
                        @Override
                        public void onServicesReceived(@NotNull OnvifDevice onvifDevice, OnvifServices services) {
                            onvifManager.getDeviceInformation(device, new OnvifDeviceInformationListener() {
                                @Override
                                public void onDeviceInformationReceived(@NotNull OnvifDevice device,
                                                                        @NotNull OnvifDeviceInformation deviceInformation) {
                                    status.setText("Model: " + deviceInformation.getModel() +
                                            " \n Firmware version: " + deviceInformation.getFirmwareVersion()
                                            + " \n HardwareId: " + deviceInformation.getHardwareId() +
                                            " \n Manufacturer: " + deviceInformation.getManufacturer() +
                                            " \n SerialNumber: " + deviceInformation.getSerialNumber());
                                    recieved = true;
                                    btn_conn.setText("stream");
                                }
                            });
                        }
                    });
                }else {
                    onvifManager.getMediaProfiles(device, new OnvifMediaProfilesListener() {
                        @Override
                        public void onMediaProfilesReceived(@NotNull OnvifDevice device,
                                                            @NotNull List<OnvifMediaProfile> mediaProfiles) {
                            onvifManager.getMediaStreams(device, mediaProfiles.get(0), new OnvifMediaStreamURIListener() {
                                @Override
                                public void onMediaStreamURIReceived(@NotNull OnvifDevice device,
                                                                     @NotNull OnvifMediaProfile profile, @NotNull String uri) {
                                    Intent intent= new Intent(AddressActivity.this,MediaActivity.class);
                                    intent.putExtra("mediaurl",uri);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);

                                }
                            });
                        }
                    });
                }
            }
        });


    }


    @Override
    public void onResponse(@NotNull OnvifDevice onvifDevice, @NotNull OnvifResponse response) {

    }

    @Override
    public void onError(@NotNull OnvifDevice onvifDevice, int errorCode, String errorMessage) {
        Toast.makeText(getApplicationContext(),"Address was unable to connect",Toast.LENGTH_SHORT).show();
    }
}
