package TLSClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class TLSClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		SocketFactory factory=SSLSocketFactory.getDefault();
		SSLContext sslCntxt;
		try{
			InputStream storeFile=new FileInputStream("keystore");
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
			Socket sock=sslCntxt.getSocketFactory().createSocket("192.168.0.3",2025);
			InputStream input=sock.getInputStream();
			OutputStream output=sock.getOutputStream();
			
			String msg="MSG from client";
			System.out.println("send: "+msg);
			byte[] data=new byte[1024];
			
			output.write(msg.getBytes());
			input.read(data);
			System.out.println("recv: "+new String(data));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
/*
 * vm argument에 -Djavax.net.ssl.trustStore=경로 -Djavax.net.ssl.trustStorePassword=비번 추가할 것.
 * keytool로 서버의 인증서, 서버의 키스토어를 export 한다.
 * export한 파일을 클라쪽에서 keytool를 이용해 import 한다.
 * 그럼 됨.
 * -Djavax.net.ssl.trustStore=D:\GitHub\TLS-Practice\java\TLSClient\keystoreForCPP -Djavax.net.ssl.trustStorePassword=mangokey
 */