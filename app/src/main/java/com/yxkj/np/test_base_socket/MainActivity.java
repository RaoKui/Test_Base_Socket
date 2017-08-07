package com.yxkj.np.test_base_socket;

import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnConnect;

    Button btnDisConnect;

    Button btnSend;

    TextView tvText;

    ImageView ivPic;

    MyThread myThread;

    Socket socket;

    /**
     * 服务器ip地址
     */
    public static final String HOST = "192.168.0.181";// "192.168.1.21";//
    /**
     * 服务器端口号
     */
    public static final int PORT = 5000;

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

        initSocket();


    }

    private void initSocket() {
        try {
            socket = new Socket(HOST,PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_connect:
                connect();
                break;
            case R.id.btn_disconnect:
                disconnect();
                break;
            case R.id.btn_send:
                send();
                break;
        }
    }

    private void connect() {
        myThread = new MyThread(socket);
    }

    private void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send() {

    }
}
