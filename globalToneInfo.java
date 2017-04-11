import java.io.Serializable;


public class globalToneInfo {
	
	public static boolean capture_low = true;
	public static boolean end_capture = false;
	public static String matchString = "101010";
	public static int volume = 50;
	public static int gap = 400;
	public static int double_gap = 1000;
	public static int numFreqBands = 16; //how many different frequencies, can be 2, 4, 8, 16, ...
	public static int lowFreq_first  = (numFreqBands - 1) * 80;						//1200
	public static int highFreq_first = (numFreqBands - 1) * 50 + lowFreq_first;		// 1950
	public static int lowFreq_second  = (numFreqBands - 1) * 30 +  highFreq_first;  // 2400
	public static int highFreq_second = (numFreqBands - 1) * 50 + lowFreq_second;   //3150
	//public static int synFreq = highFreq_second + (numFreqBands - 1) * 10;  //frequency used for synchronization
	public static int start1 = highFreq_first + (numFreqBands - 1) * 10;  //  2100 frequency used for synchronization
	//public static int sync1	= highFreq_second + (numFreqBands - 1) * 30;
	public static int start2 = highFreq_first + (numFreqBands - 1) * 20;  //  2250 frequency used for synchronization
	public static int end_tone = highFreq_second + (numFreqBands - 1) * 4; // 3210
	//public static int sync2 = highFreq_second + (numFreqBands - 1) * 50;
	public static int freqBandWidth = (highFreq_first-lowFreq_first)/(numFreqBands-1); //width of each frequency band
	public static int errorBandWidth = 15;  //allowable range of frequency
	public static boolean harmonic = false;
	public static float Samples_Sec = 44100; //44100 samples per second
	//public static int handShakeTime= 1000;
	public static final int FFT_POINTS = 256 * 4; 
	public static final int CHANNELS = 1;
	//public static int waitTime = 1000;
	public static int _points =  FFT_POINTS * 4; // 4K
	//public static float time = time_transform();
	
	/**/
	public static float time_transform()
	{
		return (_points * 1000 / Samples_Sec);   
	}
	/*
	public static void main(String args[])
	{
		int a = time_transform();
		System.out.print(a);
	}*/
	
}

class Data_Object extends Object implements Serializable
{
	public String name;
	public String message;
}