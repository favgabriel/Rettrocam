package com.example.rettrocam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pedro.vlc.VlcListener;
import com.pedro.vlc.VlcVideoLibrary;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

public class MediaActivity extends AppCompatActivity implements VlcListener{
    public static int portNumber=4444;// portnumber

    private Socket client;
    WifiManager wifiManager;
    WifiInfo wifiInfo;
    private OutputStreamWriter printwriter;
    private String message;
    private Toolbar toolbar;
    private Button left, right;
    private boolean iswifion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        SurfaceView surfaceView= findViewById(R.id.surfaceview);
        final Button streambtn=findViewById(R.id.streambtn);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        left= findViewById(R.id.btn_left);
        right= findViewById(R.id.btn_right);
        final VlcVideoLibrary vlcVideoLibrary= new VlcVideoLibrary(this,  this,surfaceView);
        final String url= Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("mediaurl")).toString();

        streambtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if media is not playing play
                if (!vlcVideoLibrary.isPlaying()) {
                    vlcVideoLibrary.play(url);
                    streambtn.setText("stop stream");
                }else
                    //if media is playing stop
                    vlcVideoLibrary.stop();
                    streambtn.setText("start Stream");
            }
        });

        streambtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                vlcVideoLibrary.pause();
                streambtn.setText("continue Stream");
                return true;
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnCamLeft();
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnCamRight();
            }
        } );
    }

    @Override
    public void onComplete() {
        Toast.makeText(this,"video is loading",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError() {
        Toast.makeText(this,"Error loading video",Toast.LENGTH_SHORT).show();
    }

    private void controlCam(final String ipAddress){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    client = new Socket(ipAddress, portNumber);
                    printwriter = new OutputStreamWriter(client
                            .getOutputStream(), "ISO-8859-1");
                    /*printwriter.write("Hardware is set for control");
                    printwriter.flush();*/
                    Toast.makeText(MediaActivity.this,"Hardware is connected",Toast.LENGTH_SHORT).show();
                }
                catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
        left.setVisibility(View.VISIBLE);
        right.setVisibility(View.VISIBLE);
    }

    private void turnCamLeft(){
        try {
            printwriter.write(1);
            printwriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void turnCamRight(){
        try {
            printwriter.write(2);
            printwriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setControlOff(){
        try {
            printwriter.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(getBaseContext(),"Hardware disconnected",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.connect) {
            if (!iswifion) {
                if (checkwificonnected()) {
                    wifiInfo = wifiManager.getConnectionInfo();
                    String ip = Formatter.formatIpAddress(wifiInfo.getIpAddress());
                    controlCam(ip);
                }
                iswifion = true;
            }else {
                setControlOff();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkwificonnected(){
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if (Objects.requireNonNull(wifiManager).isWifiEnabled()){
            //wifi adapter on
            wifiInfo = wifiManager.getConnectionInfo();
            return wifiInfo.getNetworkId() != -1;//connected to access point
        }else return false;// wifi adapter is off
    }
}
