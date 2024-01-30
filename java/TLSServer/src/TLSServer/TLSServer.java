package TLSServer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class TLSServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		ServerSocketFactory factory=SSLServerSocketFactory.getDefault();
		ServerSocket servSock=null;
		
		SSLContext sslCntxt;
		try{
			InputStream storeFile=new FileInputStream("D:\\GitHub\\TLS-Practice\\java\\TLSServer\\keystore");
			
			KeyStore store=KeyStore.getInstance(KeyStore.getDefaultType());
			store.load(storeFile,"mangokey".toCharArray());
			sslCntxt=SSLContext.getInstance("TLS");
			
			TrustManagerFactory trustManagerFactory;
			trustManagerFactory=TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(store);

			sslCntxt.init(null,trustManagerFactory.getTrustManagers(),null);
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
		
		try {
			servSock=sslCntxt.getServerSocketFactory().createServerSocket(2025);
			System.out.println("server on at port 2025");
		}catch(IOException e) {
			e.printStackTrace();
			return;
		}
		Socket clntSock=null;
		try {
			clntSock=servSock.accept();
			InputStream input=clntSock.getInputStream();
			OutputStream output=clntSock.getOutputStream();
			byte[] data=new byte[1024];
			
			int recvSize=input.read(data);
			System.out.println("recvSize: "+recvSize+"\ndata: "+new String(data));
			output.write(data);
			
		}catch(IOException e) {
			e.printStackTrace();
		}
		try {
			clntSock.close();
			servSock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
/*
 * keytool을 이용해 keystore를 생성할 것.
 * vm argument에 -Djavax.net.ssl.keyStore="keystore 경로" -Djavax.net.ssl.keyStorePassword=비밀번호
 * 를 추가할 것.
 * -Djavax.net.ssl.keyStore=D:\GitHub\TLS-Practice\java\TLSServer\keystore -Djavax.net.ssl.keyStorePassword=mangokey
 * */