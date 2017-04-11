import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.PrintStream;
import java.net.*;

public class generateTone extends Thread{

	ServerSocket ssocket;
	private static MapPort mapping = new MapPort();
	
	public generateTone(int port) {
		// TODO Auto-generated constructor stub
		try
		{
			ssocket=new ServerSocket(port);
		}
		catch(IOException e)
		{
			fail(e,"Could not start server.");
		}
		System.out.println("Server started...");
		this.start();
	}
	
	public static void fail(Exception e,String str)
	{
		System.out.println(str+"."+e);
	}
	
	public void run()
	{
		try
		{
			while(true)
			{
				Socket client=ssocket.accept();
				Connection con=new Connection(client);
			}
		}
		catch(IOException e)
		{
			fail(e,"Not Listening");
		}

	}
	// Code for this method is from: http://forums.sun.com/thread.jspa?threadID=5243872
	public static void generate(int hz, int volume, boolean addHarmonic)  throws LineUnavailableException {
	    byte[] buf;
	    AudioFormat af;
	    if (addHarmonic) {
	      buf = new byte[2];
	      af = new AudioFormat(globalToneInfo.Samples_Sec,8,2,true,false);
	    } 
	    else {
	      buf = new byte[1];
	      af = new AudioFormat(globalToneInfo.Samples_Sec,8,1,true,false);
	    }
	    SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
	    sdl = AudioSystem.getSourceDataLine(af);
	    sdl.open(af);
	    sdl.start();
	    for(int i=0; i<globalToneInfo._points * 3 ; i++){
	      double angle = i/(globalToneInfo.Samples_Sec/hz)*2.0*Math.PI;
	      buf[0]=(byte)(Math.sin(angle)*volume);
	 
	      if(addHarmonic) {
	        double angle2 = (i)/(globalToneInfo.Samples_Sec/hz)*2.0*Math.PI;
	        buf[1]=(byte)(Math.sin(2*angle2)*volume*0.6);
	        sdl.write(buf,0,2);
	      } else {
	        sdl.write(buf,0,1);
	      }
	    }
	    sdl.drain();
	    sdl.stop();
	    sdl.close();
	}
	/**
	 * @param args
	 */
	public static void generateSound(String IPport ){
		
		if(IPport.contains(".")){// it is an IP
			System.out.println("IP parts:");
			String binaryIP="";
			int i=0;
			String part="";
			while(i<IPport.length()){
				if(IPport.charAt(i)!='.'){
					part+=IPport.charAt(i);
				}
				else{
					part=(Integer.toBinaryString(Integer.parseInt(part)));
					binaryIP+=(part+'.');
					System.out.println(part);
					genHighLow(part);
					//genHighLow(".");
					part="";
				}				
				if(i==IPport.length()-1){
					part=(Integer.toBinaryString(Integer.parseInt(part)));
					binaryIP+=(part);
					System.out.println(part);
					genHighLow(part);
					part="";
				}
				i++;
			}
			System.out.println("Binray IP: "+ binaryIP);
		}
		else{//it is a port
			//System.out.println(Integer.toBinaryString(Integer.parseInt(IPport)));
			 String binaryPort= Integer.toBinaryString(Integer.parseInt(IPport));
			 System.out.println("Binary port: "+binaryPort);
			 genHighLow(binaryPort);			 
		}		
	}
	public static void genHighLow(String part){
		//boolean alternate = true;
		ReedSolomonEncoder abc = new ReedSolomonEncoder(GF256.QR_CODE_FIELD);
		int[] transmit = new int[4];
		char c;
		int currFreq;
		//System.out.println("time"+globalToneInfo.time+" "+globalToneInfo.volume+" "+globalToneInfo.harmonic );
		for (int i=0;i<part.length(); ){
			c=part.charAt(i);
			transmit[0] = c & 0xFF;
						
			i++;
			if(i<part.length())
			{
				c=part.charAt(i);
				transmit[1] = c & 0xFF;
			}
			else
			{
				transmit[1] = 0;
			}
			i++;
			
			
			//System.out.println("currreq="+currFreq);
			try{
				abc.encode(transmit,2);
				//generate(globalToneInfo.synFreq,globalToneInfo.volume,globalToneInfo.harmonic);
				for(int j = 0; j < transmit.length; j++)
				{
					
					currFreq=(transmit[j] & 0xF ) * globalToneInfo.freqBandWidth + globalToneInfo.lowFreq_first;
					//System.out.println("Freq1 "+currFreq);
					//generate(globalToneInfo.synFreq,globalToneInfo.volume,globalToneInfo.harmonic);
				//delay (100000); 
					generate(currFreq,globalToneInfo.volume,globalToneInfo.harmonic);
					currFreq=((transmit[j]>>4) & 0xF ) * globalToneInfo.freqBandWidth + globalToneInfo.lowFreq_second;
					//System.out.println("Freq2 "+currFreq);
					//generate(globalToneInfo.synFreq,globalToneInfo.volume,globalToneInfo.harmonic);
				//delay (100000); 
					generate(currFreq,globalToneInfo.volume,globalToneInfo.harmonic);
				//delay (100000);
				}
			}catch(LineUnavailableException lue){
	            System.out.println(lue);
	        }
			/*catch(ReedSolomonException rse)
			{
				System.out.println(rse);
			}*/
		}
	}
	
	public static void delay (int howLong) // delay function to waste time
	{
	for (int i = 1 ; i <= howLong ; i++)
	{
	double garbage = Math.PI * Math.PI;
	}
	}

	public static Long ipToInt(String addr) {
        String[] addrArray = addr.split("\\.");

        long num = 0;
        for (int i=0;i<addrArray.length;i++) {
            int power = 3-i;

            num += ((Integer.parseInt(addrArray[i])%256 * Math.pow(256,power)));
        }
        return num;
    }
	
    public static long ipToLong(String ipAddress) {
        long result = 0;
        String[] atoms = ipAddress.split("\\.");

        for (int i = 3; i >= 0; i--) {
                result |= (Long.parseLong(atoms[3 - i]) << (i * 8));
        }

        return result & 0xFFFFFFFF;
    }
    
	public static String toHex(String ipAddress) {
        return Long.toHexString(ipToLong(ipAddress));
	}
	
	public static void main(String[] args) throws LineUnavailableException{
			//readfile("input.txt");
		
		try {
			/*if(!mapping.map()) {
				System.out.println("Could not map port to UPnP device");
			}
			else 
			*/{
				String ipAddr = MyIP.getLocalIP();//mapping.getPublicIP();
				int port = 10000;//mapping.getPort();
				generateTone server = new generateTone(port);

				String ip_hex = toHex(ipAddr);
				System.out.println(toHex(ipAddr));
				//System.out.println(ipToInt(ipAddr));
				
				for( int i = 0; i < 7 ; i++)
				{
					generate(globalToneInfo.start1,globalToneInfo.volume,globalToneInfo.harmonic);
					//generate(globalToneInfo.sync1,globalToneInfo.volume,globalToneInfo.harmonic);
					generate(globalToneInfo.start2,globalToneInfo.volume,globalToneInfo.harmonic);
					//generate(globalToneInfo.sync1,globalToneInfo.volume,globalToneInfo.harmonic);
				}

				//genHighLow("0123456701234567012345670123456701234567");
				String abc = ip_hex+":"+Integer.toHexString(port)+":";
				if( abc.length() % 2 == 1)
					abc += "0";
				genHighLow(abc);

				for(int i = 0; i < 2 ;i++)
					generate(globalToneInfo.end_tone,globalToneInfo.volume,globalToneInfo.harmonic);
				//generateSound(port);
			}
		}
		catch (Exception ex) {
			System.out.println("Got an exception while mapping Port and generating tone");
		}
		
		/*   FileInputStream in = null;
    	   //FileOutputStream out = null;
    	   try {
            in = new FileInputStream("Input.txt");
            //out = new FileOutputStream("outagain.txt");
            int c;

            while ((c = in.read()) != -1) {
            	String chr = Integer.toBinaryString(c);
            	genHighLow(chr);
            	
                //out.write(c);
            }
            
            in.close();
            //out.close();
            genHighLow(".");
    	  } 
          catch(Exception e)
          {
             System.out.println("File Exception "+e); 	
          }*/    
	}
	
	public static void readfile(String filename) {
		System.out.println(filename);
		FileInputStream in;
	    File input;
	    genHighLow("0101010101010101010101010101"); //handshake
	    try {
			    input=new File(filename);
			    if(!input.exists()&& input.length()<0)
			    System.out.println("The specified file does not exist");
			    
			    in = new FileInputStream(input);
			    int c;
			    while ((c = in.read()) != -1) {
			    	String chr = Integer.toBinaryString(c);
			    	System.out.println(chr);
			    }
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	class Connection extends Thread
	{
		protected Socket netClient;
		protected ObjectInputStream fromClient;
		protected PrintStream toClient;

		public Connection(Socket client)
		{
			netClient=client;
			try
			{
				fromClient=new ObjectInputStream(netClient.getInputStream());
				toClient=new PrintStream(netClient.getOutputStream());
			}
			catch(IOException e)
			{
				try
				{
					netClient.close();
				}
				catch(IOException e1)
				{
					System.err.println("Unable to setup streams "+e1);
					return;
				}
			}
			this.start();
		}

		public void run()
		{
			Data_Object clientMessage;
			try
			{
				for(;;)
				{
					clientMessage=(Data_Object)fromClient.readObject();
					if(clientMessage==null)
						break;
					toClient.println("Recieved from : "+clientMessage.name);
				}
			}
			catch(IOException e)
			{}
			catch(ClassNotFoundException e)
			{
				System.out.println("Error in reading object "+e);
			}
			finally
			{
				try
				{
					mapping.unmap();
					netClient.close();
				}
				catch(IOException e)
				{}
				catch(Exception ex2)
				{}
			}
		}
	}
}
