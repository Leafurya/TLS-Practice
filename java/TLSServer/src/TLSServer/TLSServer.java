package TLSServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

public class TLSServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.setProperty("javax.net.ssl.keyStore", "E:\\GitHub\\TLS-Practice\\java\\TLSServer\\keystore");
   		System.setProperty("javax.net.ssl.keyStorePassword", "mangokey");

		ServerSocketFactory factory=SSLServerSocketFactory.getDefault();
		ServerSocket servSock=null;
		
		try {
			servSock=factory.createServerSocket(2025);
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