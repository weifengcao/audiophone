/*File AudioCapture01.java
This program demonstrates the capture
and subsequent playback of audio data.

A GUI appears on the screen containing
the following buttons:
Capture
Stop
Playback

Input data from a microphone is
captured and saved in a
ByteArrayOutputStream object when the
user clicks the Capture button.

Data capture stops when the user clicks
the Stop button.

Playback begins when the user clicks
the Playback button.

**************************************/

import javax.swing.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Calendar;

import javax.sound.sampled.*;

public class AudioCapture01 extends JFrame{

  boolean stopCapture = false;

  public static int scale = 0;
  public static String toData = "";

  ByteArrayOutputStream byteArrayOutputStream;
  AudioFormat audioFormat;
  TargetDataLine targetDataLine;
  AudioInputStream audioInputStream;
  SourceDataLine sourceDataLine;

  public static void main(String args[])
  {
    new AudioCapture01();
  }//end main

  public AudioCapture01(){//constructor
    final JButton captureBtn = new JButton("Capture");
    final JButton stopBtn = new JButton("Stop");
    final JButton playBtn = new JButton("Playback");

    captureBtn.setEnabled(true);
    stopBtn.setEnabled(false);
    playBtn.setEnabled(false);

    //Register anonymous listeners
    captureBtn.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e){
          captureBtn.setEnabled(false);
          stopBtn.setEnabled(true);
          playBtn.setEnabled(false);
          //Capture input data from the
          // microphone until the Stop
          // button is clicked.
          captureAudio();
        }//end actionPerformed
      }//end ActionListener
    );//end addActionListener()
    getContentPane().add(captureBtn);

    stopBtn.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e){
          captureBtn.setEnabled(true);
          stopBtn.setEnabled(false);
          playBtn.setEnabled(true);
          //Terminate the capturing of
          // input data from the
          // microphone.
          stopCapture = true;
        }//end actionPerformed
      }//end ActionListener
    );//end addActionListener()
    getContentPane().add(stopBtn);

    playBtn.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e){
          //Play back all of the data
          // that was saved during
          // capture.
          playAudio();
        }//end actionPerformed
      }//end ActionListener
    );//end addActionListener()
    getContentPane().add(playBtn);

    getContentPane().setLayout(new FlowLayout());
    setTitle("Capture/Playback Demo");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(250,70);
    setVisible(true);
  }//end constructor

  //This method captures audio input
  // from a microphone and saves it in
  // a ByteArrayOutputStream object.
  private void captureAudio(){
    try{
      //Get everything set up for
      // capture
      audioFormat = getAudioFormat();
      DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class,audioFormat);
      targetDataLine = (TargetDataLine)AudioSystem.getLine(dataLineInfo);
      targetDataLine.open(audioFormat);
      targetDataLine.start();

      //Create a thread to capture the
      // microphone data and start it
      // running.  It will run until
      // the Stop button is clicked.
      Thread captureThread = new Thread(new CaptureThread());
      captureThread.start();
    } catch (Exception e) {
      System.out.println(e);
      System.exit(0);
    }//end catch
  }//end captureAudio method

  //This method plays back the audio
  // data that has been saved in the
  // ByteArrayOutputStream
  private void playAudio() {
    try{
      //Get everything set up for
      // playback.
      //Get the previously-saved data
      // into a byte array object.
      byte audioData[] = byteArrayOutputStream.toByteArray();
      //Get an input stream on the
      // byte array containing the data
      InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
      AudioFormat audioFormat = getAudioFormat();
      audioInputStream = new AudioInputStream(byteArrayInputStream, audioFormat, audioData.length/audioFormat.getFrameSize());
      DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
      sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo);
      sourceDataLine.open(audioFormat);
      sourceDataLine.start();

      //Create a thread to play back
      // the data and start it
      // running.  It will run until
      // all the data has been played
      // back.
      Thread playThread = new Thread(new PlayThread());
      playThread.start();
    } catch (Exception e) {
      System.out.println(e);
      System.exit(0);
    }//end catch
  }//end playAudio

  //This method creates and returns an
  // AudioFormat object for a given set
  // of format parameters.  If these
  // parameters don't work well for
  // you, try some of the other
  // allowable parameter values, which
  // are shown in comments following
  // the declarations.
  private AudioFormat getAudioFormat(){
    float sampleRate = (float)(globalToneInfo.Samples_Sec);
    int sampleSizeInBits = 8;    	//8,16
    int channels = globalToneInfo.CHANNELS;    		//1,2
    boolean signed = true;    		//true,false
    boolean bigEndian = false;		//true,false
    return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
  }//end getAudioFormat
  //===================================//

  //Inner class to capture data from
  // microphone
  class CaptureThread extends Thread{
	  //An arbitrary-size temporary holding
	  // buffer
	  String server_info = "";
	  int _sampleRate = (int)globalToneInfo.Samples_Sec;
	  int _logPoints;
	  int _sqrtPoints;

	  int _aBitRev[] = new int[globalToneInfo._points];
	  Complex _X[] = new Complex[globalToneInfo._points];
	  Complex _W[][] = new Complex[_logPoints + 1][globalToneInfo._points];

	  FileOutputStream out = null;
	  byte tempBuffer[] = new byte[globalToneInfo._points]; //new byte[10000];
	  double _tape[] = new double[globalToneInfo._points];

  double GetIntensity (int i)
  {
      assert (i < globalToneInfo._points);
      return _X[i].mod()/_sqrtPoints;
  }

  int Points () { return globalToneInfo._points; }

	// return frequency in Hz of a given point
  int GetFrequency (int point)
  {
      assert (point < globalToneInfo._points);
      long x =_sampleRate * point;
      return (int)(x / globalToneInfo._points);
  }

  int HzToPoint (int freq)
  {
      return globalToneInfo._points * freq / _sampleRate;
  }
  int PointToHz (int point)
  {
      return point* _sampleRate/globalToneInfo._points;
  }
  int MaxFreq() { return _sampleRate; }

  void DataIn(int[] data , int count)
  {
		//copy (_tape.begin () + data.length, _tape.end (), _tape.begin ());
	    //copy1(0 + data.length, _tape.length, 0, _tape);
	  //System.out.println(count);
	  	for(int i = 0; i < _tape.length - count; i++)
		{
			_tape[i] = _tape[count + i];
		}
		//copy (data.begin (), data.end (), _tape.begin () + (globalToneInfo._points - data.length));
	  	for(int i = 0; i < count; i++)
		{
			_tape[i + (globalToneInfo._points - count)] = data[i];
		}
	    // Initialize the FFT buffer
	    for (int i = 0; i != globalToneInfo._points; ++i)
	    {
	    	//PutAt (i, _tape [i]); means  _X [_aBitRev[i]] = Complex (val);
	    	_X [_aBitRev[i]] = new Complex (_tape[i],0);   // confuse here
	    }

  }

  void Transform (int[] data, int count)
  {
		if (count > globalToneInfo._points)
			throw new ArrayIndexOutOfBoundsException();

		DataIn (data, count);
	    // make space for samples at the end of tape
	    // shifting previous samples towards the beginning
		// to           from
		// v-------------|
		// xxxxxxxxxxxxxxyyyyyyyyy
		// yyyyyyyyyoooooooooooooo
		// <- old -><- free tail->
		//String.copy (_tape.begin () + data.size (), _tape.end (), _tape.begin ());
		//std::copy (data.begin (), data.end (), _tape.begin () + (globalToneInfo._points - data.size ()));

	    // Initialize the FFT buffer
	//    for (int i = 0; i != globalToneInfo._points; ++i)
	  //      PutAt (i, _tape [i]);
      // step = 2 ^ (level-1)
      // increm = 2 ^ level;
      int step = 1;
      for (int level = 1; level <= _logPoints; level++)
      {
          int increm = step * 2;
          for (int j = 0; j < step; j++)
          {
              // U = exp ( - 2 PI j / 2 ^ level )
              Complex U = _W [level][j];
              for (int i = j; i < globalToneInfo._points; i += increm)
              {
                  // butterfly
                  Complex T = U;
                  T = T.times(_X [i+step]);
                  _X [i+step] = _X[i];
                  _X [i+step] = _X [i+step].minus(T);
                  _X [i] = _X [i].plus(T);
              }
          }
          step *= 2;
      }
  }

  int getBit(String Status, int Endtime){

	  return 0;
  }

  int abc = 0;

  public void run(){
    byteArrayOutputStream = new ByteArrayOutputStream();
    stopCapture = false;
    String filename="output.txt";

    _sqrtPoints = (int)Math.sqrt(globalToneInfo._points);
    // calculate binary log
    _logPoints = 0;
    int points = globalToneInfo._points;
    points--;
    while (points != 0)
    {
        points >>= 1;
        _logPoints++;
    }

    _aBitRev = new int[globalToneInfo._points];
    _X = new Complex[globalToneInfo._points];
    _W = new Complex[_logPoints + 1][globalToneInfo._points];
    // Precompute complex exponentials
    int _2_l = 2;
    for (int l = 1; l <= _logPoints; l++)
    {
        //_W[l].resize (globalToneInfo._points);

        for (int i = 0; i != globalToneInfo._points; i++ )
        {
        	//System.out.println("Ashish "+ l + " " + i);
            double re =  Math.cos (2. * Math.PI * i / _2_l);
            double im = -Math.sin (2. * Math.PI * i / _2_l);
            _W[l][i] = new Complex (re, im);
        }
        _2_l *= 2;
    }

    int rev = 0;
    int halfPoints = globalToneInfo._points/2;
    for (int i = 0; i < globalToneInfo._points - 1; i++)
    {
         _aBitRev [i] = rev;
         int mask = halfPoints;
         // add 1 backwards
         while (rev >= mask)
         {
             rev -= mask; // turn off this bit
             mask >>= 1;
         }
         rev += mask;
    }
    _aBitRev [globalToneInfo._points-1] = globalToneInfo._points-1;

    try{//Loop until stopCapture is set
        // by another thread that
        // services the Stop button.

    	//out = new FileOutputStream("outagain1.txt");
    	File myFile = new File(filename);
    	myFile.delete();

        BufferedWriter output=null;
        try {
      		output = new BufferedWriter(new FileWriter(filename, true));

        } catch (IOException e1) {
      		// TODO Auto-generated catch block
      		e1.printStackTrace();
        }

   long endtime=0;

   ReedSolomonDecoder abc1 = new ReedSolomonDecoder(GF256.QR_CODE_FIELD);
   int bound = 2;
   int[] transmit = new int[2 + bound];

   int trans_i = 0;
   boolean read_low = false, read_high = false; // error = false;
   //boolean first = true;
   boolean syn = true;
   //boolean setup = false;
   int[] avg = new int[10];
   int avg_i = 0;
   String synch_string = "";
   int prev_freq = globalToneInfo.highFreq_first;

   boolean started = false;
   int starttime = 0;
   int prev_i = 0;
   while(!stopCapture && !globalToneInfo.end_capture){
        //Read data from the internal
        // buffer of the data line.
       //endtime=getLongTime()+ globalToneInfo.waitTime;

       //while(getLongTime() < endtime)
       {
		    	int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
		    	//System.out.println("cnt="+cnt); //4057
		    	//System.out.println(tempBuffer.length);

		        int outBuf[] = new int[cnt];

		        for(int i = 0; i < cnt; i++ )
		        {
		        	outBuf[i] = (tempBuffer[i] - 128) * 64;
		        	//outBuf[i] = (tempBuffer[i]);
		        }

		        Transform(outBuf, cnt);

		        int pts = cnt-1;
		    	//int pts = globalToneInfo._points/ 2;
		    	boolean new1 = true;
		        /*System.out.print(HzToPoint(2000));
		        System.out.println();
		        System.out.println(PointToHz(186));
		        */

		    	int max = 1;
		    	double intsy;
		    	for(int i = 1; i<pts ;i++)
		    	{
		    		intsy = GetIntensity(i + 1);
		    		if (GetIntensity(i + 1) > GetIntensity(max)  && intsy /128.0 < 1600.0)
		    			max = i + 1;
		    	}
		    	int f = GetFrequency(max);

		    	if(GetIntensity(max) / 128.0 < 8.0)
		    		continue;

		    	String time=getTime();
		    	int sample_time = Integer.parseInt(time);
		    	if(!started && syn &&  Math.abs(globalToneInfo.start1 - f) < globalToneInfo.errorBandWidth)
		    	{
		    		//System.out.println("start1");
		    		starttime = sample_time;
		    		syn = false;
		    		synch_string += "1";
		    	}
		    	else if(!started && !syn &&  Math.abs(globalToneInfo.start2 - f) < globalToneInfo.errorBandWidth)
		    	{
		    		//System.out.println("start2");
		    		int t = sample_time;
		    		if( t < starttime)
		    			t += 100000;

		    		avg[avg_i] = t - starttime;
		    		avg_i++;
		    		syn = true;
		    		synch_string += "0";
		    		starttime = sample_time;
		    	}
		    	else if(started &&  Math.abs(globalToneInfo.end_tone - f) < globalToneInfo.errorBandWidth)
		    	{
		    		globalToneInfo.end_capture = true;
		    	}
		    	else if (started && (Math.abs(f % globalToneInfo.freqBandWidth - globalToneInfo.freqBandWidth) < globalToneInfo.errorBandWidth
		    			|| Math.abs(f % globalToneInfo.freqBandWidth) < globalToneInfo.errorBandWidth)
		    			&& ((f >= globalToneInfo.lowFreq_first - globalToneInfo.errorBandWidth && f <= globalToneInfo.highFreq_first + globalToneInfo.errorBandWidth) ||
		    			    (f >= globalToneInfo.lowFreq_second - globalToneInfo.errorBandWidth && f <= globalToneInfo.highFreq_second + globalToneInfo.errorBandWidth)	)
		    			 && ( sample_time - starttime > globalToneInfo.gap || sample_time - starttime < 0 ||
		    			      ( prev_freq < globalToneInfo.highFreq_first + globalToneInfo.errorBandWidth && f > globalToneInfo.lowFreq_second - globalToneInfo.errorBandWidth)
		    			      || ( prev_freq > globalToneInfo.lowFreq_second - globalToneInfo.errorBandWidth && f < globalToneInfo.highFreq_first + globalToneInfo.errorBandWidth)
		    			      )
		    			 )

		    		{   //&& ( Integer.parseInt(time) - starttime > globalToneInfo.gap || Integer.parseInt(time) - starttime < 0 )
		    			prev_freq = f;

		    			if(globalToneInfo.capture_low)
		    			{

		    				if(/*!read_low &&*/ f >= globalToneInfo.lowFreq_first - globalToneInfo.errorBandWidth && f <= globalToneInfo.highFreq_first + globalToneInfo.errorBandWidth)
		    				{
		    					//System.out.println("Time1 : " +(sample_time - starttime)+" Freq1 : "+f);
		    					f -= globalToneInfo.lowFreq_first;
		    					int bit = ((f%globalToneInfo.freqBandWidth) > globalToneInfo.freqBandWidth/2) ? (f/globalToneInfo.freqBandWidth+1) : (f/globalToneInfo.freqBandWidth);
		    					transmit[trans_i] = bit;
		    					globalToneInfo.capture_low = !globalToneInfo.capture_low;
		    					read_low = true;
		    					read_high = false;
		    					//error = false;
		    				}
		    				else if(/*!read_low  &&*/ f >= globalToneInfo.lowFreq_second - globalToneInfo.errorBandWidth && f <= globalToneInfo.highFreq_second + globalToneInfo.errorBandWidth) //&& !error
		    				{
		    					//System.out.println("Erasure1");
		    					read_low = false;
		    					read_high = true;
		    					//error = true;
		    					//System.out.println("Time1 : " +(sample_time - starttime)+" Freq1 : "+f);
		    					f -= globalToneInfo.lowFreq_second;

		    					int bit = ((f%globalToneInfo.freqBandWidth) > globalToneInfo.freqBandWidth/2) ? (f/globalToneInfo.freqBandWidth+1) : (f/globalToneInfo.freqBandWidth);
		    					transmit[trans_i] = (bit<<4) + 0;
		    					trans_i++;
		    					if(trans_i == transmit.length)
		    					{
		    						printData(transmit, bound, abc1);
		    						trans_i = 0;
		    					}

		    				}

		    				int t = (sample_time - starttime);
		    	  			if( t < 0)
		    	  				t += 100000;
		    	  			starttime = sample_time;

		    			}
		    			else
		    			{

		    				if(/*!read_high && */f >= globalToneInfo.lowFreq_second - globalToneInfo.errorBandWidth && f <= globalToneInfo.highFreq_second + globalToneInfo.errorBandWidth)
		    				{
		    					//System.out.println("Time2 : " +(sample_time - starttime)+" Freq2 : "+f);
		    					f -= globalToneInfo.lowFreq_second;
		    					int bit = ((f%globalToneInfo.freqBandWidth) > globalToneInfo.freqBandWidth/2) ? (f/globalToneInfo.freqBandWidth+1) : (f/globalToneInfo.freqBandWidth);
		    					transmit[trans_i] += (bit<<4);

		    					trans_i++;
		    					if(trans_i == transmit.length)
		    					{
		    						trans_i = 0;
		    						printData(transmit, bound, abc1);
		    					}

		    					globalToneInfo.capture_low = !globalToneInfo.capture_low;
		    					read_high = true;
		    					read_low = false;
		    					//error = false;
		    				}
		    				else if(/*!read_high &&*/ f >= globalToneInfo.lowFreq_first - globalToneInfo.errorBandWidth && f <= globalToneInfo.highFreq_first + globalToneInfo.errorBandWidth) // && !error
		    				{
		    					trans_i++;
		    					if(trans_i == transmit.length)
		    					{
		    						trans_i = 0;
		    						printData(transmit, bound, abc1);
		    					}

		    					//System.out.println("Erasure2");
		    					//System.out.println("Time2 : " +(sample_time - starttime)+" Freq2 : "+f);
		    					f -= globalToneInfo.lowFreq_first;
		    					int bit = ((f%globalToneInfo.freqBandWidth) > globalToneInfo.freqBandWidth/2) ? (f/globalToneInfo.freqBandWidth+1) : (f/globalToneInfo.freqBandWidth);
		    					transmit[trans_i] = bit;
		    					read_high = false;
		    					read_low = true;
		    					//error = true;
		    				}

		    				int t = (sample_time - starttime);
		    	  			if( t < 0)
		    	  				t += 100000;
		    	  				starttime = sample_time;

		    			}
		    	}
		    	else {
		    	}

		    	if(synch_string.endsWith(globalToneInfo.matchString) && !started)
		    	{
		    		int average = 0;

	  	   			for(int k = 0; k < avg_i; k++)
	  	   				average += avg[k];

	  	   			average /= avg_i;

	  	   			globalToneInfo.gap = average * 3 / 2;
	  	   			//globalToneInfo.double_gap = globalToneInfo.gap * 5 / 2;
	  	   			//System.out.println("Average : " + average);
		    		started = true;
		    	}
		        if(cnt > 0){
		          //Save data in output stream
		          // object.
		          //byteArrayOutputStream.write(tempBuffer, 0, cnt);
		        }//end if
      }//end time while
       // if majority of the frequencies in the buffer are closer to 1 than 0 then it is a one.
       //otherwise it is a 0.
     //int dat= computehighlow(averageBuf, averageCount);
     //System.out.println("dat"+ dat);
    }//end capture while

   		try{
   			Data_Object data_transfer = new Data_Object();
   	   		data_transfer.name = "Hello";
   	   		data_transfer.message = "Hi";

   	   		System.out.println("fdasd"+server_info);
   	   		String[] info = server_info.split(":");

   	   		int a1 = Integer.parseInt(info[0].substring(0, 2),16);
   	   		int a2 = Integer.parseInt(info[0].substring(2, 4),16);
   	   		int a3 = Integer.parseInt(info[0].substring(4, 6),16);
   	   		int a4 = Integer.parseInt(info[0].substring(6, 8),16);

   	   		System.out.println(a1+"."+a2+"."+a3+"."+a4);
   	   		info[0] = a1+"."+a2+"."+a3+"."+a4;

   	   		System.out.println(info[0]);
	   		System.out.println(info[1]);

   			Socket toServer=new Socket(info[0],Integer.parseInt(info[1],16));
   			ObjectOutputStream toser=new ObjectOutputStream(toServer.getOutputStream());
   			toser.writeObject(data_transfer);

   			BufferedReader fromser=new BufferedReader(new InputStreamReader(toServer.getInputStream()));
   			//String status=fromser.readLine();
   			System.out.println(fromser.readLine());

   			toser.close();
   			fromser.close();
		}
		catch(InvalidClassException a)
		{
			System.out.println("The Data class is invalid"+a);
		}
		catch(NotSerializableException a)
		{
			System.out.println("The Object is not Serializable"+a);
		}
		catch(IOException a)
		{
			System.out.println("Cannot write to the server."+a);
		}
		catch(Exception e)
		{
			System.out.println("General Exception : "+e);
		}

    	try {
    		output.close();
   	  	} catch (IOException e) {
   		// TODO Auto-generated catch block
   		e.printStackTrace();
   	  }
    	byteArrayOutputStream.close();
     }catch (Exception e) {
      System.out.println(e);
      System.exit(0);
    }//end catch
  }//end run

  public void printData(int[] arr, int bound, ReedSolomonDecoder decode) throws ReedSolomonException
  {
	 decode.decode(arr, bound);
	 System.out.print("Chars : ");
	 for(int i =0; i < arr.length - bound; i++ )
	 {
		 char ch = (char)arr[i];

		 server_info += ch;

		 System.out.print(arr[i]+" Ch :" +ch+" ser_info : "+server_info+" " );
	 }
	 System.out.println("");
  }
  }//end inner class CaptureThread
  //===================================//
  //Inner class to play back the data
  // that was saved.

  class PlayThread extends Thread{
  byte tempBuffer[] = new byte[10000];

  public void run(){
    try{
      int cnt;
      //Keep looping until the input
      // read method returns -1 for
      // empty stream.
      while((cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1){
        if(cnt > 0){
          //Write data to the internal
          // buffer of the data line
          // where it will be delivered
          // to the speaker.
          sourceDataLine.write(tempBuffer, 0, cnt);
        }//end if
      }//end while
      //Block and wait for internal
      // buffer of the data line to
      // empty.

      sourceDataLine.drain();
      sourceDataLine.close();
    }catch (Exception e) {
      System.out.println(e);
      System.exit(0);
    }//end catch
   }//end run
  }//end inner class PlayThread
  //===================================//

public long getLongTime(){
	Calendar now = Calendar.getInstance();
	long militime=now.getTimeInMillis();
	return militime;
}
public String getTime(){
	Calendar now = Calendar.getInstance();
	Long militime=now.getTimeInMillis();
	return (militime.toString()).substring(8);

}
}//end outer class AudioCapture01.java
