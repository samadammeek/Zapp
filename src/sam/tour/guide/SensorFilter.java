

package sam.tour.guide;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;

import sam.dataparser.GetHeightICanSee;
import sam.dataparser.RasterReader;
import uk.me.jstott.jcoord.*;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("unused")
public class SensorFilter implements SensorEventListener, LocationListener{
	
	private static final String TAG = null;
		
	SensorManager sm = null;
	
	LocationManager lm;
	private double []GPSLocation = new double[3];
	private float  []SensorData = new float[3];
	private double []OSRef = new double[3];
	private FrameLayout fl;
	static double []headerData;
	private boolean targetInRange;
	private RasterReader rr;
	ArrayList <String> assetList;
	DrawOnTop dt;
	int l;
	String assetName;
	double assets[][];
	int k =0; 
	Context context;
	Vibrator vibrator;
	String [][] stringData;
	double [][] doubleData; 
	int i;
	Timer timer;
	String inRange[] = new String[3];
	int newURN = 0;
	String assetShortName;
	double myResult[] = new double[4];
	int timerDelay;
	Timer sensorTimer;
	float avOrientation;
	float Orientation;
	RasterReader POIrr;
	double [][]POIMask;
	double []POIMaskHeader;
	int POINumber;
	TextView tv;
	double targetArrayCoords[] = new double[2];
	static final float ALPHA = 0.15f;
	float smoothCompass;
	int counter;
	float oData;
	float oSmooth;
	private boolean LOS;
	private int arraySizeX;
	double distanceToTarget;
	String assetNameDist;
	double accelTol;
    double acceleration = 0;
	
	
	@SuppressWarnings("static-access")
	public SensorFilter(Activity activity, double GPSLocation[], float SensorData[], final Context context,
			RasterReader rr, final DrawOnTop dt, File file, RasterReader POIRaster){
		
		//variable to reduce shake:
		accelTol = -1;
	
		counter = 1;
		oData = 0;
		oSmooth = 0;
		GPSLocation = this.GPSLocation;
		SensorData = this.SensorData;
		headerData = new double[6];
		this.context = context;
		this.rr = rr;
		this.POIrr = POIRaster;
		
		arraySizeX = (int) (this.rr.getASCIIHeader()[0]* this.rr.getASCIIHeader()[4]);
		
		
		POIMaskHeader = new double[6];
		POIMaskHeader = POIRaster.getASCIIHeader();
		
		//get POIMask Data
		POIMask = new double[(int)headerData[0]][(int) headerData[1]];
		POIMask = POIrr.getASCIIData();
		
		//register the sensor manager
		
		sm = (SensorManager) activity.getSystemService(context.SENSOR_SERVICE);
		sm.registerListener(this,sm.getDefaultSensor(Sensor.TYPE_ORIENTATION), 
         SensorManager.SENSOR_DELAY_UI);
		sm.registerListener(this,sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		//sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
				//SensorManager.SENSOR_DELAY_NORMAL);
		
		//register the location manager
		
		lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		
		this.dt = dt;
		
		LOS = true;
		
		loadAssetList(file);
		
		timer = new Timer();
		
		timerDelay = 200;
		
		timer.scheduleAtFixedRate(new TimerTask(){
			public void run(){
				
				//System.out.println("Smooth = "+ oSmooth);
				//System.out.println("Rough = "+getOrientation()[0]);
				try{
				
					if(LOS == true){
					runGetHeight();
					
					}
					else{
						runGetHeight();
						dt.overrideCrossColor(2);
					}
					
				
				}
				catch (NullPointerException e){
					System.out.println("data not loaded");
				}
				
	
				}
				
			}, 4000, timerDelay);
		
		
		}
	
	
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		GPSLocation[0] = location.getLatitude();
		GPSLocation[1] = location.getLongitude();
		GPSLocation[2] = location.getAltitude();
		
		//convert LatLons to OSRef E, N
		LatLng latlng = new LatLng(location.getLatitude(), 
				location.getLongitude());
		
		//datum shift?
		latlng.toOSGB36();
		//extract OSGB references from latlng object
		OSRef[0] = latlng.toOSRef().getEasting();
		OSRef[1] = latlng.toOSRef().getNorthing();
		OSRef[2] = location.getAltitude();
		}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
		}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
		}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
		}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
		}

	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		//switch for whether named building appears or not
		 float [] accel = new float[3];
	 	 //System.out.println("a2 = " +this.getOrientation()[0]);

		
		 synchronized (this) {
			 	
		     if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
		    
		    	 
		    	 accel = event.values;
		    	 
		    	 double a = accel[0];
		    	 double b = accel[1];
		    	 double c = accel[2];
		    	 
		    	 acceleration = Math.sqrt(a*a + b*b + c*c) - SensorManager.GRAVITY_EARTH;
		    	 //System.out.println("a1 = "+acceleration);
		    	 
		     }
			 
			 if (event.sensor.getType() == Sensor.TYPE_ORIENTATION){
				 //System.out.println("a1 = "+acceleration);
				 if(acceleration > accelTol){
				 	SensorData = event.values;
				 	
				 	// System.out.println("a2 = " +this.getOrientation()[0]);
				 }
		        		//System.out.println(""+this.getOrientation()[0] +" "+ this.getOrientation()[1] +" "+ this.getOrientation()[2]);
		        			
		        	if(LOS == true ){
		     			tv.setText("Distance: " + distanceToTarget + " POI: " + assetShortName);
		        	}
		        	
		        	else{
		        		//change here to remove text
		        		tv.setText("Distance: + " + distanceToTarget + " POI: " + assetShortName);
		        		}
		        
		        	}
			 
			 System.out.println(acceleration + "," + SensorData[0] +"," + SensorData[1] + "," + SensorData[2]  );
		        				
		        }
		  	}
		        
		    
		 
	protected void onResume(){
		sm.registerListener(this,sm.getDefaultSensor(Sensor.TYPE_ORIENTATION), 
				SensorManager.SENSOR_DELAY_UI);
		sm.registerListener(this,sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
				SensorManager.SENSOR_DELAY_UI);
	
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		counter = 1;
		oData = 0;
		
		timer.scheduleAtFixedRate(new TimerTask(){
			public void run(){
				
				//System.out.println("Smooth = "+ oSmooth);
				//System.out.println("Rough = "+getOrientation()[0]);
				try{
				
					if(LOS == true){
					runGetHeight();
					
					}
					else{
						dt.overrideCrossColor(2);
					}
					
				
				}
				catch (NullPointerException e){
					System.out.println("data not loaded");
				}
				
	
				}
				
			}, 1000, timerDelay);
		
		
		}
	
	protected void onDestroy(){
		lm.removeUpdates(this);
		sm.unregisterListener(this);
		timer.cancel();
	
		}
	protected void onPause(){
		timer.cancel();
				
	}

	public double[] getOSRef(){
		double [] clone = new double[OSRef.length];
		clone = OSRef.clone();
		return clone;
		}
	
	public double[] getGPSLocation(){
		return GPSLocation;
		}
	
	public float[] getOrientation(){
		return SensorData;
		}
	
	public void setFrame(FrameLayout fl){
	this.fl = fl;	
		}
	
	private static double getMyx(double Easting){
		double i = ((Easting - headerData[2])) / headerData[4];
		return i;
		}
	
	//convert Northing to Array Coordinates
	private static double getMyy(double Northing){
		double i = (headerData[0] - ((Northing - headerData[3])) / headerData[4]);
		return i;
		}
	
	public double[] getArrayCoords(){
		double[] arrayCoords = new double[2];
		arrayCoords[0] = (getMyx(this.getOSRef()[0]))/headerData[0];
		arrayCoords[1] = (getMyy(this.getOSRef()[1]))/headerData[1];
		return arrayCoords;
		}
	
	 public void loadAssetList(File file){
	   	
		 try{
	    		BufferedReader br = null;
	    		
	    		br = new BufferedReader(new FileReader(file));
					
	    		String line;
	    		
	    		assetList = new ArrayList<String>();
	    		
	    		i = 0;
	   	   
					while ((line = br.readLine()) != null){
					
						if(i > 0){
							//System.out.println("getFileStuff" + line);
							//add to the array list to be saved into the file later
							//add to the array to be setup on the map
							assetList.add(line);
						}
						i++;
					}
				} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
				//attempt to read into arrays
				System.out.println(i);
				
				
				stringData = new String[assetList.size()][3];
				doubleData = new double[assetList.size()][5];
				i = i - 1;
				
				for(int j = 0; j < i; j++){
					String []arrayString = new String[7];
					String s  = assetList.get(j);
					arrayString = s.split(",");
					
					stringData[j][0] = arrayString[0];
					stringData[j][1] = arrayString[1];
					stringData[j][2] = arrayString[2];
					
					doubleData[j][0] = Double.parseDouble(arrayString[0]);
					doubleData[j][1] = Double.parseDouble(arrayString[3]);
					doubleData[j][2] = Double.parseDouble(arrayString[4]);
					doubleData[j][3] = Double.parseDouble(arrayString[5]);
					doubleData[j][4] = Double.parseDouble(arrayString[6]);
			}
		}
	 
	 private void runGetHeight(){
				 
	    String[] assetNameString = new String[3] ;
	   	 
		float fl = (this.getOrientation()[1] + 90) * -1;
 		
		int[] testOSRef = new int[2];
		//test data
		
		testOSRef[0] = (int) OSRef[0];
		testOSRef[1] = (int) OSRef[1];
		
		//testOSRef[0] = 326036;
		//testOSRef[1] = 521897;
		GetHeightICanSee gh = null;
		
		
		//gh = new GetHeightICanSee(testOSRef[0],testOSRef[1],rr,
				//this.getOrientation()[0],fl,5);
		
		gh = new GetHeightICanSee(testOSRef[0],testOSRef[1],rr,
		this.getOrientation()[0],fl,3);

		
 		myResult = gh.getMyResult();
 		
 	//	targetInRange = false;
 		dt.setCrossColor(targetInRange);
 		//distTextView.setText("Distance " + gh.getDistanceToTarget());
 		distanceToTarget = gh.getDistanceToTarget();
 		//set cross size here

 		//assetShortName = "" + gh.getDistanceToTarget();
 		dt.setCrossSize(gh.getDistanceToTarget(), arraySizeX);
 		
 		try{
 			POINumber = (int) POIMask[(int)gh.getMyArrayCoords()[1]]
  			                             [(int)gh.getMyArrayCoords()[0]];
 			targetArrayCoords[0] = gh.getMyArrayCoords()[1]/ POIMaskHeader[1];
 			targetArrayCoords[1] = gh.getMyArrayCoords()[0]/ POIMaskHeader[0];
 			
 			//System.out.println(targetArrayCoords[0] + " " + targeth.ArrayCoords[1]);
 		}
 		
 		catch(ArrayIndexOutOfBoundsException e){
 			POINumber = 0;
 		}
 			
 		if(myResult[2] != -1 && myResult[3] != -1 && POINumber!=0 && POINumber!=-9999){
 			
 			//System.out.println("POINumber " + POINumber);
 			
 			targetInRange = true;
 			dt.setCrossColor(targetInRange);
 			//assetNameDist = ""+ gh.getDistanceToTarget();
 			
 			
 			//System.out.println("CrossColor = 0") ;
 			//System.out.println("POINumber " + POINumber);
 			
 			for (int k = 0; k < stringData.length; k++){
 				
 				int URNString = Integer.parseInt(stringData[k][0]);
 				if (URNString == POINumber){
 					
 					l = (int)doubleData[k][1];
 					assetName = stringData[k][2];
 					//assetShortName = ""+gh.getDistanceToTarget();
 					//change what's in the target box
 					assetShortName = stringData[k][1];
 					
 					//System.out.println("POI Looked at " + assetShortName);
 				}
 				
 			}
 			
 		}
 		else{
 				assetShortName = "none";
 				targetInRange = false;
 				
 		}		
 	 }
 		
 	
	 public boolean getTargetRange(){
		 return targetInRange;
	 	}
	 
	 public int getAssetType(){
		 return l;
	 	}
	 
	 public String getAssetName(){
		 return assetName;
	 	}
	 	 
	 public double[][] getDoubleData(){
		 return doubleData;
	 }
	 public String getAssetShortName(){
		 if(assetShortName == ""){
			 return "none";
		 }
		 else{
			 return assetShortName;
		 }
		
	 }
	 
	 public void setTextView(TextView tv){
		 this.tv = tv;
	 }
	 
	 public double[] getTargetArrayCoords(){
		 return targetArrayCoords;
	 }
	 
	 public float compassFilter(int counter){
		float smoothCompass1 = 0;
		float compassReturn = 0;
		 
		 for(int i = 1; i < 11; i++){
			 
			 smoothCompass1 = (smoothCompass1 + SensorData[0]);
									 
		 }
		 
		 compassReturn = smoothCompass1/10;

		 return compassReturn;
	 }
	 
	 public double getoSmooth(){
		 return oSmooth;
	 }
	 
	 public void setLOStoGo(boolean LOS){
		 this.LOS = LOS;
	 }
	
	

}
