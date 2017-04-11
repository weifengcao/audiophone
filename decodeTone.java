import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;


public class decodeTone {
	static int time=100;
	static int volume=40;
	static int highFreq=8000;
	static int lowFreq=100;
	static int mediumFreq=4000;
	static boolean harmonic=false;
	
	
	public decodeTone() {
		// TODO Auto-generated constructor stub
	}
	// Code for this method is from: http://forums.sun.com/thread.jspa?threadID=5243872
	public static void generate(int hz,int msecs, int volume, boolean addHarmonic)  throws LineUnavailableException {
 
	    float frequency = 44100;
	    byte[] buf;
	    AudioFormat af;
	    if (addHarmonic) {
	      buf = new byte[2];
	      af = new AudioFormat(frequency,8,2,true,false);
	    } 
	    else {
	      buf = new byte[1];
	      af = new AudioFormat(frequency,8,1,true,false);
	    }
	    SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
	    sdl = AudioSystem.getSourceDataLine(af);
	    sdl.open(af);
	    sdl.start();
	    for(int i=0; i<msecs*frequency/1000; i++){
	      double angle = i/(frequency/hz)*2.0*Math.PI;
	      buf[0]=(byte)(Math.sin(angle)*volume);
	 
	      if(addHarmonic) {
	        double angle2 = (i)/(frequency/hz)*2.0*Math.PI;
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
					genHighLow(".");
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
		char c;
		for (int i=0;i<part.length(); i++){
			c=part.charAt(i);
			try{
				if(c=='1'){
					generate(highFreq,time,volume,harmonic);
				}
				else if(c=='0'){
					generate(lowFreq,time,volume,harmonic);
				}
				else if( c=='.'){
					generate(mediumFreq,time,volume,harmonic);
				}
			}catch(LineUnavailableException lue){
	            System.out.println(lue);
	        }
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String IP= "149.141.137.01";
		String port="5678";
		generateSound(IP);
		genHighLow(".......");
		generateSound(port);
          
	}

}
