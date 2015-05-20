package sam.tour.guide;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import sam.dataparser.GetHeightICanSee;
import sam.dataparser.RasterReader;
import sam.intents.AudioIntent;
import sam.intents.ImageIntent;
import sam.intents.WebviewIntent;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Start extends Activity {
    /** Called when the activity is first created. */
	protected static final String TAG = null;
	private static final int MENU_EXIT = 2;
	private static final int MENU_CAPTURE = 0;
	private static final int MENU_SAVE = 1;
	private static final int MENU_LOAD = 3;
	public double[] sOSCoords;
	public float[] SensorData;
	public SensorFilter sf;
	double myHeight;
	Button filterButton;
	Context context;
	static double[] headerData;
	//double[][] dummyData;
	Preview preview;
	GetHeightICanSee gh;
	RasterReader rr;
	RasterReader POIrr;
	ArrayList<String> arrayCoords = new ArrayList<String>();
	ArrayList<String> OSCoords = new ArrayList<String>();
	double [] myPos = new double[2];
	File sdcard = Environment.getExternalStorageDirectory();
	String OSCoordsLine;
	int URN = 0;
	static FrameLayout fPreview;
	float [] cameraViewAngles;
	Bundle b;
	File tempFile;
	DrawOnTop dt;
	String annoString;
	LinearLayout ll;
	ArrayList <String> assetList;
	TextView tv;
	Boolean captureOrConsume;
	
	
	
	    
        
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Bundle b = getIntent().getExtras();
        
        captureOrConsume = true;
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
     
        fPreview = (FrameLayout) findViewById(R.id.preview);
       
        context = this;
        
        ll = (LinearLayout)findViewById(R.id.layout);
        
        tv = (TextView)findViewById(R.id.TextView01);
        
        
        //setup camera preview
        preview = new Preview(this);
                      
        //setup crosshair
        dt = new DrawOnTop(context, fPreview);
        
         
        //get Raster Paths
        String rasterPath = b.getString("rasterPath");
        String POIPath = b.getString("POIPath");
        
        //Read in Data
        rr = new RasterReader(rasterPath);
        
        //Read in POI data
        POIrr = new RasterReader(POIPath);
        
        //get data from intent which started the activity
                     
        headerData = new double[6];
        headerData = rr.getASCIIHeader();
        
      
        //dummyData = new double[(int)headerData[0]][(int) headerData[1]];
        //dummyData = rr.getASCIIData();
        
        File assetFile = new File("/sdcard/tour guide/assetList.csv");
                
        sf = new SensorFilter(this,sOSCoords, SensorData, this, rr, dt, assetFile, POIrr);
        sf.setTextView(tv);
        sf.setLOStoGo(true);

           
      	fPreview.addView(preview,new LayoutParams(
				fPreview.getLayoutParams().height,
    			fPreview.getLayoutParams().width));
	
    	fPreview.addView(dt,new LayoutParams(
				fPreview.getLayoutParams().height,
    			fPreview.getLayoutParams().width));
    	
    	
    

		//change GPS coords into OSGB36
        sOSCoords = new double[3];
        SensorData = new float[3];
        
        //startAudioIntent("vid(mp4 for nexus).mp4");
        tempFile = new File(sdcard,"Coords.txt");
    	LoadLastFile(tempFile);
          
                       
        //setup button to capture data
        filterButton = (Button) findViewById(R.id.buttonClick);
       
        filterButton.setOnClickListener(new Button.OnClickListener(){ 
           	
    		public void onClick(View arg0) {
    			if(captureOrConsume == true){
    				queryPoint();
    				System.out.println("Query Point");
    			}
    			
    			if(captureOrConsume == false){
    				capturePoint();
    				
    			}
    			}});
        }
    
   public void loadAssetList(File file){
    	try{
    		
    		BufferedReader br = null;
    		
    		br = new BufferedReader(new FileReader(file));
				
    		String line;
    		assetList = new ArrayList<String>();
    		
    		int i = 0;
   	   
				while ((line = br.readLine()) != null){
				
					if(i > 0){
						assetList.add(line);
					}
					
					i++;
									
				}
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
			Toast.makeText(context,"File Loaded",Toast.LENGTH_SHORT).show();
			
   	}
        
    protected void onResume(){
    	super.onResume();
    	
    }
        	
    protected void onPause(){
    	super.onPause();
    	
    	
    }
    
    protected void onDestroy(){
    	super.onDestroy();
    	sf.onDestroy();
    	finish();
    }
    
   
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(0, MENU_EXIT, 3, "Exit").setIcon(android.R.drawable.ic_menu_delete);
        menu.add(1, MENU_CAPTURE, 3, "Switch Mode").setIcon(android.R.drawable.ic_menu_add);
       // menu.add(2, MENU_SAVE, 3, "Save collected points").setIcon(android.R.drawable.ic_menu_save);
       // menu.add(3, MENU_LOAD, 3, "Load points from file").setIcon(android.R.drawable.ic_menu_upload);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item){
     
        boolean handled = false;

        switch (item.getItemId()){
      
        case MENU_EXIT:
        	onDestroy();
        	handled = true;
            break;
            
        case MENU_CAPTURE:
        	
        	if(captureOrConsume == true){
        		captureOrConsume = false;
        		sf.setLOStoGo(false);
        		//dt.overrideCrossColor(2);
        		filterButton.setText("Capture Point");
        		//dt.setVisibility(View.INVISIBLE);
        		Toast.makeText(context, "Capture Mode", Toast.LENGTH_SHORT).show();
        	}
        	
        	else{
        		captureOrConsume = true;
        		sf.setLOStoGo(true);
        		filterButton.setText("What is this?");
        		//dt.setVisibility(View.VISIBLE);
        		Toast.makeText(context, "Query Mode", Toast.LENGTH_SHORT).show();
        	}
        	
        	System.out.println("bool " + captureOrConsume);
        	break;
        case MENU_SAVE:
        	tempFile = new File(sdcard,"Coords.txt");
        	SavePointsToFile(OSCoords, tempFile);
            handled = true;
            break;
        case MENU_LOAD:
        	tempFile = new File(sdcard,"Coords.txt");
        	LoadLastFile(tempFile);
        	
            handled = true;
            break;
        }
        
        return handled;
    }
    
    public void startImageIntent(String fileName){
    	Bundle b = new Bundle();
    	b.putString("fileName", fileName);
    	Intent i = new Intent(context,ImageIntent.class);
		i.putExtras(b);
		startActivity(i);
    }
    
    public void startAudioIntent(String fileName){
    	Bundle b = new Bundle();
    	b.putString("fileName", fileName);
    	Intent i = new Intent(context, AudioIntent.class);
		i.putExtras(b);
		startActivity(i);
    }
    public void startWebIntent(String fileName, double[]coords){
    	Bundle b = new Bundle();
    	b.putString("fileName", fileName);
    	b.putDoubleArray("targetArray", coords);
    	Intent i = new Intent(context, WebviewIntent.class);
		i.putExtras(b);
		startActivity(i);
    }
    
    public void capturePoint(){

    	
    	sOSCoords = sf.getOSRef();
    	
    	System.out.println("Point Captured " + URN);
		//change sensor elevation so device vertical = 0
		float i = (sf.getOrientation()[1] + 90) * -1;
		//System.out.println("Compass baring = " + sf.getOrientation()[0] + " Elevation Angle = " + i);
		
		int Easting = (int)sOSCoords[0];
		int Northing = (int)sOSCoords[1];
		gh = new GetHeightICanSee(Easting, Northing,rr, sf.getOrientation()[0], (double)i,3);
		
		double [] myResult = new double[4];
		
		myResult = gh.getMyResult();
		
		//add points regardless of whether surface model hit or not.		    	
    	//if(myResult[2]!=-1){
		if(sOSCoords[0]!=0){

			OSCoordsLine = URN +"," + sOSCoords[0] + "," + sOSCoords[1] + "," + myResult[4] + "," 
			+ myResult[2] + "," + myResult[3]+","+ (myResult[1] - 1.5) + "," + sf.getOrientation()[0] + "," + i + "," + sf.getAssetShortName();
    		//add data to list for display later in ImageDisplay intent
        	//Order is URN, X, Y, Height
    		arrayCoords.add(URN + ","+ gh.getArrayCoords()[0] + "," 
        			+ gh.getArrayCoords()[1] + "," + myResult[1]);
        	OSCoords.add(OSCoordsLine);
        	myPos = gh.getMyPositionDraw();
    
    		preview.camera.takePicture(shutter, raw, jpeg);
        	
        	System.out.println(OSCoordsLine);
        	Toast.makeText(context, "Point Captured ! X =  " + myResult[2] + " Y = " + myResult[3] + " Height = "
    				+ myResult[1], Toast.LENGTH_SHORT).show();
    		 
    		try { Thread.sleep(2000); 
    		}
    		catch (InterruptedException ex) { 
    			
    			}
    		preview.camera.startPreview();
    		
    		tempFile = new File(sdcard,"Coords.txt");
        	SavePointsToFile(OSCoords, tempFile);
    		//StartMapIntent();
    		
    	}
		else{
			Toast.makeText(context, "Waiting for GPS Signal", Toast.LENGTH_LONG).show();
		}
    	
    	URN++;
    }
    
   
    public void queryPoint(){
    	boolean targetInRange;
		targetInRange = sf.getTargetRange();
		
		if (targetInRange == true){
			
			
			int l = sf.getAssetType();
			System.out.println("Point Queried " + sf.getAssetName());
			switch(l){
			
			case 1:
				//use webview intent
				startImageIntent(sf.getAssetName());
				//startWebIntent(sf.getAssetName(), sf.getTargetArrayCoords());
			break;
			
			case 2:
				startAudioIntent(sf.getAssetName()); 
			break;
			
			case 3:
				startImageIntent(sf.getAssetName());
		   			   				
			}
		}
		
    }

	 //photo data
   final ShutterCallback shutter = new ShutterCallback() {
		public void onShutter() {
			Log.d(TAG, "onShutter'd");
		}
	};

	/** Handles data for raw picture */
	final PictureCallback raw = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken - raw");
		}
	};

	/** Handles data for jpeg picture */
	final PictureCallback jpeg = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			
			FileOutputStream outStream = null;
			try {
				// write to local sandbox file system
				// outStream =
				// CameraDemo.this.openFileOutput(String.format("%d.jpg",
				// System.currentTimeMillis()), 0);
				// Or write to sdcard
				int k = URN -1;
				outStream = new FileOutputStream(String.format(
						"/sdcard/tour guide/Photos/" + k +".jpg", System.currentTimeMillis()));
				outStream.write(data);
				outStream.close();
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			Log.d(TAG, "onPictureTaken - jpeg");
		}
	};
	 public void LoadLastFile(File file){
	    	try{
	    		
	    		BufferedReader br = null;
	    		GetHeightICanSee tgh;
	    		br = new BufferedReader(new FileReader(file));
					
	    		String line;
	    	
	    		String [] tsa = new String[10];
	    		int i = 0;
	   	   
					while ((line = br.readLine()) != null){
					
						if(i > 0){
							//System.out.println("getFileStuff" + line);
							//add to the array list to be saved into the file later
							OSCoords.add(line);
							//add to the array to be setup on the map
						    tsa = line.split(",");
							/**double tE = Double.parseDouble(tsa[1]);
							double tN = Double.parseDouble(tsa[2]);
							double tO = Double.parseDouble(tsa[7]);
							double tEl = Double.parseDouble(tsa[8]);
							tgh = new GetHeightICanSee(tE,tN,rr,tO,tEl,3);
							
							//OSCoords.add(tsa[0] + "," + tE + "," + tN +"," + tsa[3] + "," + tgh.getMyResult()[2] 
							  //   + "," + tgh.getMyResult()[3] + "," + tgh.getMyHeight() + "," + tO + "," + tEl );
							
							System.out.println("File Load " + tsa[0] + ","+ tgh.getArrayCoords()[0] + "," 
			            			+ tgh.getArrayCoords()[1] + "," + tgh.getMyResult()[1]);
							arrayCoords.add(tsa[0] + ","+ tgh.getArrayCoords()[0] + "," 
			            			+ tgh.getArrayCoords()[1] + "," + tgh.getMyResult()[1]);
							
							float[] screenPoints = new float[6];
							//URN, pointx, pointy, point height, point myHeight
							screenPoints[0] = Float.parseFloat(tsa[0]);
							screenPoints[1] = Float.parseFloat(tsa[4]);
							screenPoints[2] = Float.parseFloat(tsa[5]);
							screenPoints[3] = Float.parseFloat(tsa[6]);
							screenPoints[4] = Float.parseFloat(tsa[3]);
																
							String screenPointString = screenPoints[0] + "," + screenPoints[1]
							        + "," +screenPoints[2] + "," +screenPoints[3] + "," + screenPoints[4];**/
						//;
							URN = Integer.parseInt(tsa[0])+1;
							//System.out.println(URN);
							}
						i++;
					}
				} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			Toast.makeText(context,"File Loaded",Toast.LENGTH_SHORT).show();
				
	   	}
          
	 public void SavePointsToFile(ArrayList<String> Points, File pointFile){
	    	try {
	    		FileOutputStream fout;
	    		
	    		String header = "URN,myX,myY,myHeight,Easting,Northing,Height,Orientation,Elevation,type";
	        	
	        		fout = new FileOutputStream (pointFile, false);
	        		PrintStream ps = new PrintStream(fout);
	    			ps.println(header);
	    			for (int i = 0; i < Points.size(); i++){
					 	ps.println(Points.get(i));
	    			}
	                     	
	    			Toast.makeText(context, "File Saved", Toast.LENGTH_SHORT).show();
				fout.close();
			} 
	        
	          catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("File not found " + e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("IO Exception");
			}
	    }
  
    
    
}