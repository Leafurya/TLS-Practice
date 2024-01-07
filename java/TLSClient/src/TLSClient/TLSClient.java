package TLSClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

public class TLSClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SocketFactory factory=SSLSocketFactory.getDefault();
		try {
			Socket sock=factory.createSocket("192.168.1.70",2025);
			InputStream input=sock.getInputStream();
			OutputStream output=sock.getOutputStream();
			
			String msg="hello java TLS server!";
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
 */