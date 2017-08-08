package com.yxkj.np.test_base_socket;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by 20151203 on 2017/8/8.
 */

public class BackService extends Service {

    private static final String TAG = "BackService";

    /**
     * 心跳频率
     */
    private static final long HEART_BEAT_RATE = 3 * 1000;
    /**
     * 服务器ip地址
     */
    public static final String HOST = "192.168.0.181";// "192.168.1.21";//
    /**
     * 服务器端口号
     */
    public static final int PORT = 5100;

    public WeakReference<Socket> mSocket;

    private Handler mHandler = new Handler();

    private MyThread myThread;

    private long send_time = 0L;

    private long i = 0;

    private LocalBroadcastManager manager;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() - send_time >= HEART_BEAT_RATE) {
                boolean isSuccess = sendMsg("HeartBeat" + i);
                i++;
                if (i == 1000000000) {
                    i = 0;
                }
                if (!isSuccess) {
                    mHandler.removeCallbacks(mRunnable);
                    myThread.release();
                    releaseLastSocket(mSocket);
                    new InitSocketThread().start();

                }
            }


            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };


    public boolean sendMsg(final String msg) {
        if (null == mSocket || null == mSocket.get()) {
            return false;
        }
        final Socket soc = mSocket.get();
        if (!soc.isClosed() && !soc.isOutputShutdown()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OutputStream os = soc.getOutputStream();
                        String message = msg + "\r\n";
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                        bw.write(message);
                        bw.flush();
//                        os.write(message.getBytes());
//                        os.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            send_time = System.currentTimeMillis();//每次发送成数据，就改一下最后成功发送的时间，节省心跳间隔时间
        } else {
            return false;
        }
        return true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBackService;
    }

    private IBackService.Stub iBackService = new IBackService.Stub() {
        @Override
        public boolean onReceiveMessage(String msg) throws RemoteException {
            return sendMsg(msg);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        new InitSocketThread().start();// 开启初始化socket线程

        manager = LocalBroadcastManager.getInstance(this);
    }

    class InitSocketThread extends Thread {
        @Override
        public void run() {
            super.run();
            initSocket();
        }
    }

    /**
     * 初始化socket
     */
    private void initSocket() {
        try {
            Socket so = new Socket(HOST, PORT);
            mSocket = new WeakReference<Socket>(so);
            myThread = new MyThread(so);
            myThread.start();
            mHandler.postDelayed(mRunnable, HEART_BEAT_RATE);//初始化成功后，就准备发送心跳包
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class MyThread extends Thread {

        private WeakReference<Socket> mWeakSocket;

        private boolean isStart = true;

        public MyThread(Socket socket) {
            this.mWeakSocket = new WeakReference<Socket>(socket);
        }

        @Override
        public void run() {
            super.run();
            Socket socket = mWeakSocket.get();
            while (true) {
                try {
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    int size = dis.readInt();
                    if (size == -1) {
                        char data[] = new char[1024 * 4];
                        InputStreamReader isr = new InputStreamReader(dis, "UTF-8");
                        isr.read(data);

                        String s = new String(data);
                        Intent intent = new Intent("string");
                        intent.putExtra("message", s);

                        manager.sendBroadcast(intent);
//                        onGetDataMessageListener.onString(s);
                    } else if (size == -2) {
                        char data[] = new char[1024 * 4];
                        InputStreamReader isr = new InputStreamReader(dis, "UTF-8");
                        isr.read(data);

                        String s = new String(data);
                        Log.i(TAG, "run: " + s);
                        Intent intent = new Intent("heart_beat");
                        manager.sendBroadcast(intent);
                    } else {
                        byte data[] = new byte[size];
                        int length = 0;
                        while (length < size) {
                            length += dis.read(data, length, size - length);
                        }
                        Intent intent = new Intent("picture");
                        intent.putExtra("byte", data);
                        manager.sendBroadcast(intent);
//                        onGetDataMessageListener.onPic(data);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        public void release() {
            isStart = false;
            releaseLastSocket(mWeakSocket);
        }

    }


    private void releaseLastSocket(WeakReference<Socket> mWeakSocket) {
        try {
            if (null != mSocket) {
                Socket sk = mSocket.get();
                if (!sk.isClosed()) {
                    sk.close();
                }
                sk = null;
                mSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
