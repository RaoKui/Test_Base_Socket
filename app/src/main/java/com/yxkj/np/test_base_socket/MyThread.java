package com.yxkj.np.test_base_socket;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

        while (true) {
            try {
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                int size = dis.readInt();
                if (size == -1) {
                    char data[] = new char[1024 * 4];
                    InputStreamReader isr = new InputStreamReader(dis, "UTF-8");
                    isr.read(data);

                    String s = new String(data);
                    onGetDataMessageListener.onString(s);
                } else {
                    byte data[] = new byte[size];
                    int length = 0;
                    while (length < size) {
                        length += dis.read(data, length, size - length);
                    }
                    onGetDataMessageListener.onPic(data);
                }


//                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                String message = null;
//                while ((message = br.readLine()) != null) {
//                    onGetDataMessageListener.onString(message);
//                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public interface OnGetDataMessageListener {
        void onString(String message);

        void onPic(byte[] data);
    }

    private OnGetDataMessageListener onGetDataMessageListener;

    public void setOnGetDataMessageListener(OnGetDataMessageListener getDataMessageListener) {
        this.onGetDataMessageListener = getDataMessageListener;
    }

}
