import java.net.*;
import java.io.*;
import java.util.regex.*;
import java.util.Enumeration;
import java.net.NetworkInterface;

public class IPaddress {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//String myIPaddress="";
		//myIPaddress=getLocalIP();
		//System.out.println("My Local IP address is: "+myIPaddress);
		//myIPaddress=getRouterIP();
		//System.out.println("My Router (internal gateway) IP address is: "+myIPaddress);
		//myIPaddress=getMyIP();
		//System.out.println("My WAN IP address is: "+myIPaddress);
		punch_a_hole();
	}

	//gives the local address.
	private static String getLocalIP(){


		NetworkInterface iface = null;
		String myHostName = "";

		try{
			for(Enumeration ifaces = NetworkInterface.getNetworkInterfaces();ifaces.hasMoreElements();)
			{
				   iface = (NetworkInterface)ifaces.nextElement();
				   InetAddress ia = null;
				   for(Enumeration ips = iface.getInetAddresses();ips.hasMoreElements();)
				   {
					   ia = (InetAddress)ips.nextElement();
					   if( ia.getHostAddress().indexOf(":")==-1 && !ia.isLoopbackAddress())
					   {
						   	myHostName = ia.getHostAddress();
						   	break;
					   }
				   }
				   if(!myHostName.equals(""))
						break;
				}
			}
			catch(Exception e)
			{
				System.out.println("Exception : "+e);
			}
		return myHostName;
	}
	//gives the gateway address. But it is what I see from inside -> internal address of router.
	private static String getRouterIP(){

        String _255 = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
        String exIP = "(?:" + _255 + "\\.){3}" + _255;

        // Regexp to find the good line
        Pattern pat = Pattern.compile("^\\s*(?:0\\.0\\.0\\.0\\s*){1,2}("+exIP+").*");
        Process proc;
        try {

            // netstat
            proc = Runtime.getRuntime().exec("netstat -rn");

            InputStream inputstream =
                    proc.getInputStream();
            InputStreamReader inputstreamreader =
                new InputStreamReader(inputstream);
            BufferedReader bufferedreader =
                new BufferedReader(inputstreamreader);

            // Parsing the result
            String line;
            while ((line = bufferedreader.readLine()) != null) {
                Matcher m = pat.matcher(line);

                // This is the good line
                if(m.matches()){

                    // return the first group
                    return m.group(1);
                }
            }
        	// can't find netstat
        } catch (IOException ex) {
            System.out.println("Some issue when getting the gateway IP address");
        }

        	return null;
    	}

	//gets the WAN address by connecting to an external server who can see it.
	private static String getMyIP(){
		String internetIP="";
		try{
			URL getmyip = new URL("http://www.whatismyip.com/automation/n09230945.asp");
			BufferedReader in = new BufferedReader(
						new InputStreamReader(
						getmyip.openStream()));

			String inputLine;

			while ((inputLine = in.readLine()) != null)
			   // System.out.println(inputLine);
				internetIP=inputLine;

			in.close();

		}
		catch(IOException ex){
			System.out.println("Some issue when getting the gateway IP address");
		}
		return internetIP;
	}

	//trying to punch a hole. The connection is refused because
	//there is no server waiting for me at dante.cs.wisc.edu
	//but I dont know how to get the public port number that I have
	// been assigned to.
	private static void punch_a_hole(){

		Socket echoSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		InetAddress myAddress;

		try {
			// Get InetAddress of destination
        		myAddress = InetAddress.getByName("cs.wisc.edu");

			// Create socket and connect
			// if port is not open this will fail
        		echoSocket = new Socket(myAddress, 40000);

			// Local address and port from socket
			// will not get this far
			System.out.println("Address is: " + echoSocket.getLocalAddress());
			System.out.println("Port is: " + echoSocket.getLocalPort());
	        } catch (UnknownHostException e) {
        		System.err.println(e.getMessage());
			System.exit(1);
	        } catch (IOException e) {
        		System.err.println(e.getMessage());
	        	System.exit(1);
        	}
	}
}
