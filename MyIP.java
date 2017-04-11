import java.net.*;
import java.io.*;
import java.util.Enumeration;
import java.util.regex.*;

public class MyIP {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(getLocalIP());
	}

	//gives the local address.
	public static String getLocalIP(){
		//ArrayList<InetAddress> addresses = new ArrayList<InetAddress>();
		Enumeration<NetworkInterface> e = null;
		try {
			e = NetworkInterface.getNetworkInterfaces();

		}
		catch (SocketException se) {
			System.err.println("Couldn't get interfaces");
		}
		while (e.hasMoreElements()) {
                        NetworkInterface ni = e.nextElement();
                        for(Enumeration<InetAddress> e2 = ni.getInetAddresses(); e2.hasMoreElements();) {
				InetAddress iAddr = e2.nextElement();
                                if (isOutgoingAddress(iAddr)) {
					return iAddr.getHostAddress();
				}
                        }
                }
		return "";
	}

	private static boolean isOutgoingAddress(InetAddress addr) {
		if (!addr.isLoopbackAddress()) {
			return Pattern.matches("\\d+\\.\\d+\\.\\d+\\.\\d+", addr.getHostAddress());
		}
		else {
			return false;
		}
	}
}
