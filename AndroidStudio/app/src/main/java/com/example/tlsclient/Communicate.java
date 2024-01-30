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
import java.security.cert.CertificateException;

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

	public Communicate(Handler handler,InputStream keyStoreFile){
		try{
			KeyStore keyStore=KeyStore.getInstance("BKS");
			keyStore.load(keyStoreFile,"mangokey".toCharArray());
			sslCntxt=SSLContext.getInstance("TLS");

			trustManagerFactory=TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(keyStore);

			sslCntxt.init(null,trustManagerFactory.getTrustManagers(),null);

			this.handler=handler;
		}catch(IOException e){
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (KeyStoreException e) {
			throw new RuntimeException(e);
		} catch (KeyManagementException e) {
			throw new RuntimeException(e);
		} catch (CertificateException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public void run(){
		try {
			sock=sslCntxt.getSocketFactory().createSocket("192.168.1.70",2025);
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
					output.write("MSG from client".getBytes());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
	}
}
