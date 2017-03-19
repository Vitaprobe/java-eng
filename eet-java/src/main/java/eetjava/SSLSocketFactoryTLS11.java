package eetjava;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSLSocketFactoryTLS11 extends SSLSocketFactory {

	private SSLSocketFactory delegate;	
	private String[] enableProtocols;
	public SSLSocketFactoryTLS11(SSLSocketFactory delegate, String[] enableProtocols) {
		this.delegate=delegate;
		this.enableProtocols=enableProtocols;
	}
	
	@Override
	public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
		System.out.println("Create socket 1 -------------------------- Entered here");
		System.out.println(socket);
		System.out.println(host);
		System.out.println(port);
		System.out.println(autoClose);
		return enableTLSOnSocket(delegate.createSocket(socket, host, port, autoClose));
	}

	@Override
	public String[] getDefaultCipherSuites() {
		return delegate.getDefaultCipherSuites();
	}

	@Override
	public String[] getSupportedCipherSuites() {
		return delegate.getSupportedCipherSuites();
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
		System.out.println("Create socket 2 -------------------------- Entered here");
		return enableTLSOnSocket(delegate.createSocket(host, port));
	}

	@Override
	public Socket createSocket(InetAddress host, int port) throws IOException {
		System.out.println("Create socket 3 -------------------------- Entered here");
		return enableTLSOnSocket(delegate.createSocket(host, port));
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
			throws IOException, UnknownHostException {
		System.out.println("Create socket 4 -------------------------- Entered here");
		return enableTLSOnSocket(delegate.createSocket(host, port, localHost, localPort));
	}

	@Override
	public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
		System.out.println("Create socket 5 -------------------------- Entered here");
		return enableTLSOnSocket(delegate.createSocket(address, port, localAddress, localPort));
	}

	@Override
	public Socket createSocket() throws IOException {
		System.out.println("Create socket 6 -------------------------- Entered here");
		System.out.println("|___ empty Socket creation, moving on... \n");
		return new Socket();
	}
	
    private Socket enableTLSOnSocket(Socket socket) {
        if(socket != null && (socket instanceof SSLSocket)) {
            ((SSLSocket)socket).setEnabledProtocols(enableProtocols);
        }
        return socket;
    }

}
