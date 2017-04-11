import net.sbbi.upnp.*;
import net.sbbi.upnp.jmx.*;
import net.sbbi.upnp.devices.*;
import net.sbbi.upnp.messages.*;
import net.sbbi.upnp.impls.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class MapPort {

	private String externalIP;
	private int mappedPort;
        private int discoveryTimeout;
	private InternetGatewayDevice natIGD;
	public MapPort() {
		externalIP = "";
		mappedPort = 0;
		discoveryTimeout = 5000;
		natIGD = null;
	}

	public boolean map() throws IOException, UPNPResponseException {
		InternetGatewayDevice[] IGDs = InternetGatewayDevice.getDevices(discoveryTimeout);
		if ( IGDs != null ) {
			// the first device found will work, perhaps iterate through until connection works
			natIGD = IGDs[0];
			//System.out.println( "Found device " + natIGD.getIGDRootDevice().getModelName());
			// now let's open the port
			String localHostIP = MyIP.getLocalIP();
			externalIP = natIGD.getExternalIPAddress();
			Random generator = new Random();
			int randInt = generator.nextInt(61000-49152) + 49152;
			//System.out.println( "private IP: " + localHostIP + " public IP: " + externalIP);
			// we assume that localHostIP is something else than 127.0.0.1
			boolean isMapped = false;
			int tries = 0;
			while (!isMapped && tries < 5) {
				isMapped = natIGD.addPortMapping("AudioNAT", null, randInt, randInt, localHostIP, 0, "TCP");
				tries++;
			}
			if (isMapped) {
				mappedPort = randInt;
				//System.out.println( "Port " + randInt + " mapped to " + localHostIP );
				return isMapped;
			}
			else {
				throw new IOException();
			}
		}
		else{
			throw new UPNPResponseException();
			//System.out.println("Cannot find Internet Gateway Device");
		}
	}

	public boolean unmap() throws IOException, UPNPResponseException {
		if (natIGD != null) {
			return natIGD.deletePortMapping( null, mappedPort, "TCP" );
		}
		return false;
	}

	public int getPort() {
		return mappedPort;
	}

	public String getPublicIP() {
		return externalIP;
	}
}
