package com.example.rettrocam;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.videolan.libvlc.Dialog;

import java.util.ArrayList;
import java.util.List;

import be.teletask.onvif.DiscoveryManager;
import be.teletask.onvif.DiscoveryMode;
import be.teletask.onvif.listeners.DiscoveryListener;
import be.teletask.onvif.models.Device;

public class MainActivity extends AppCompatActivity {
    private WifiManager.MulticastLock lock;
    private Button manual,discover;
    private ListView listView;
    private ArrayList<String> arrayList;
    DiscoveryManager manager;
    //Credentials credentials;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manual=findViewById(R.id.manual);

        listView=findViewById(R.id.listView);

        manager = new DiscoveryManager();
        manager.setDiscoveryTimeout(10000);
        arrayList= new ArrayList<>();

        final ArrayAdapter<String> adapter= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,android.R.id.text1,arrayList);
        listView.setAdapter(adapter);
        discover();
        setAnimation();
        lock.release();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ipadress = (String) listView.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this,AddressActivity.class);
                intent.putExtra("Ip",ipadress);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

        manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddressActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Ip","");
                startActivity(intent);

            }
        });
    }

    private void setAnimation() {
        AnimationSet set = new AnimationSet(true);
        Animation fade= new AlphaAnimation(0.0f,1.0f);
        fade.setDuration(800);
        fade.setFillAfter(true);
        set.addAnimation(fade);
        LayoutAnimationController controller = new LayoutAnimationController(set,0.2f);
        listView.setLayoutAnimation(controller);
    }

    private void lockMulticast() {
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi == null)
            return;
        lock = wifi.createMulticastLock("ONVIF");
        lock.acquire();
    }

    private void discover(){
        lockMulticast();
        manager.discover(DiscoveryMode.ONVIF,new DiscoveryListener() {
            @Override
            public void onDiscoveryStarted() {
                Log.w("mesg","Discovery started");
            }

            @Override
            public void onDevicesFound(List<Device> devices) {
                for (Device device : devices) {
                    //System.out.println("Devices found: " + device.getHostName());
                //    credentials= new Credentials(device.getHostName(),device.getUsername(),device.getPassword());
                    arrayList.add(device.getHostName());
                }
            }
        });
    }
}
