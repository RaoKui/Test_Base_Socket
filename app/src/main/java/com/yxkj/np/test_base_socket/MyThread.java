package com.yxkj.np.test_base_socket;

import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by 20151203 on 2017/8/7.
 */

public class MyThread extends Thread {

    Socket socket;

    public MyThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        super.run();

        while(true){
            try {
                InputStream is = socket.getInputStream();
                int size = is.read();
                byte data[] = new byte[size];

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public interface OnGetDataMessageListener {
        void onString(String message);

        void onPic();
    }

    private OnGetDataMessageListener onGetDataMessageListener;

    public void setOnGetDataMessageListener(OnGetDataMessageListener getDataMessageListener) {
        this.onGetDataMessageListener = getDataMessageListener;
    }

}
