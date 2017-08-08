package com.yxkj.np.test_base_socket;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnConnect;

    Button btnDisConnect;

    Button btnSend;

    TextView tvText;

    ImageView ivPic;

    IBackService iBackService;

    Intent serviceIntent;

    LocalBroadcastManager broadcastManager;

    long i = 0;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case "picture":
                    byte data[] = intent.getByteArrayExtra("byte");
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                    ivPic.setImageBitmap(bitmap);
                    break;
                case "string":
                    String message = intent.getStringExtra("message");
                    tvText.setText(message);
                    break;
                case "heart_beat":
                    i++;
                    tvText.setText("心跳连接：来之服务器的答复" + i );
                    break;
            }
        }
    };

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iBackService = IBackService.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            iBackService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConnect = (Button) findViewById(R.id.btn_connect);
        btnConnect.setOnClickListener(this);

        btnDisConnect = (Button) findViewById(R.id.btn_disconnect);
        btnDisConnect.setOnClickListener(this);

        btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);

        tvText = (TextView) findViewById(R.id.tv_text);

        ivPic = (ImageView) findViewById(R.id.iv_pic);

        serviceIntent = new Intent(this, BackService.class);

        broadcastManager = LocalBroadcastManager.getInstance(this);


    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter();
        filter.addAction("string");
        filter.addAction("heart_beat");
        filter.addAction("picture");
        broadcastManager.registerReceiver(mReceiver, filter);

        bindService(serviceIntent, conn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(conn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        broadcastManager.unregisterReceiver(mReceiver);
    }

    private void initListener() {
//        myThread.setOnGetDataMessageListener(new MyThread.OnGetDataMessageListener() {
//            @Override
//            public void onString(final String message) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        tvText.setText(message);
//                    }
//                });
//
//            }
//
//            @Override
//            public void onPic(byte[] data) {
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ivPic.setImageBitmap(bitmap);
//
//                    }
//                });
//            }
//        });


    }

//    private void initSocket() {
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    socket = new Socket(HOST, PORT);
//
//                    myThread = new MyThread(socket);
//
//                    myThread.start();
//
//                    initListener();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
//
//
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_connect:
                connect();
                break;
            case R.id.btn_disconnect:
//                disconnect();
                break;
            case R.id.btn_send:
                send();
                break;
        }
    }

    private void connect() {

//        initSocket();


    }

//    private void disconnect() {
//        try {
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void send() {

    }

}
