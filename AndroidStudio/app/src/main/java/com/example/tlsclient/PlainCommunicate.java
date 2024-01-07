package com.example.tlsclient;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class PlainCommunicate extends Thread {
    Socket sock;
    Handler handler;

    public PlainCommunicate(Handler handler){
            this.handler=handler;
    }
    @Override
    public void run(){
        try {
            sock=new Socket("172.30.1.35",2025);
            InputStream input=sock.getInputStream();
            Message msg=handler.obtainMessage();
            byte[] data=new byte[1024];
            input.read(data);
            msg.obj=new String(data);
            handler.sendMessage(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public void Send(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    OutputStream output=sock.getOutputStream();
                    output.write("hello TLS server!".getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();


    }
}
