package com.example.tlsclient;

import android.icu.util.Output;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class Communicate extends Thread {
	Socket sock;
	SocketFactory factory;
	Handler handler;
	SSLContext sslCntxt;
	TrustManagerFactory trustManagerFactory;

	public Communicate(Handler hanbler, KeyStore keyStore){
		this.handler=handler;
		try{
			sslCntxt=SSLContext.getInstance("TLS");
			trustManagerFactory=TrustManagerFactory.getInstance("X509");
			trustManagerFactory.init(keyStore);

			sslCntxt.init(null,trustManagerFactory.getTrustManagers(),null);

			sock=sslCntxt.getSocketFactory().createSocket("192.168.1.70",2025);
		}catch(IOException e){
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (KeyStoreException e) {
			throw new RuntimeException(e);
		} catch (KeyManagementException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public void run(){
		try {
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
		try {
			OutputStream output=sock.getOutputStream();
			output.write("hello TLS server!".getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
