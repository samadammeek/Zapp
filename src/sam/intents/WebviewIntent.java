package sam.intents;

import java.util.Timer;
import java.util.TimerTask;

import sam.tour.guide.R;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

public class WebviewIntent extends Activity {
	String html;
	RelativeLayout ll;
	WebView full;
	
	Bitmap bitmap;
	double[]targetArrayCoords = new double[2];
	
	private static final RelativeLayout.LayoutParams ZOOM_PARAMS = 
		new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

	/* Using WebView to display the full-screen image */
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.webview);
	        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	        
	        Bundle b = new Bundle();
	        
	        b = getIntent().getExtras();
	        
	        String fileName = b.getString("fileName");
	        
	        targetArrayCoords = b.getDoubleArray("targetArray");
	               
	        ll = (RelativeLayout)findViewById(R.id.RelativeLayout01);
	       
	        //WebView full = (WebView)findViewById(R.id.WebView01);
	        
	        full = new WebView(this);
	        
	        /**full.setWebViewClient(new WebViewClient(){
		    	
		    	@Override
		    	public void onPageFinished(WebView view, String url) {
		    		// TODO Auto-generated method stub
		    		super.onPageFinished(view, url);
		    		int x = (int) (bitmap.getWidth() * targetArrayCoords[0]);
			        int y = (int) (bitmap.getHeight() * targetArrayCoords[1]);
			        full.scrollTo(x, y);
			        System.out.println("x = " + x + " y = " +y);

		    	}
		       });**/
	        
	        /* Set up the Zoom controls */
	         
	        /* Create a new Html that contains the full-screen image */
	        
	        String imagePath = "/sdcard/Tour Guide/assets/" + fileName;
	        
	        System.out.println(imagePath);
	        
	        bitmap = BitmapFactory.decodeFile(imagePath);
	        
	        full.setLayoutParams(new LayoutParams(bitmap.getHeight(), bitmap.getWidth()));
	        	        
	        html = new String();
	        
	        html = ("<html><center&gt;<img src=\""+imagePath+"\"></html>");
	       	        	      	      	               
	        /* Finally, display the content using WebView */
	        	        
	        full.loadDataWithBaseURL("file:///sdcard/tour guide/assets/",html,
	                                                                "text/html",
	                                                                "utf-8",
	                                                                "");
	        
	        System.out.println(bitmap.getHeight() + " " + bitmap.getWidth());
	
	        int x = (int) (bitmap.getWidth() * targetArrayCoords[0]);
		    int y = (int) (bitmap.getHeight() * targetArrayCoords[1]);
		    full.scrollTo(x, y);
	       
	        ll.addView(full, new LayoutParams(bitmap.getHeight(), bitmap.getWidth()));
	        
	        full.setInitialScale(100);
		      
	        final View zoom = this.full.getZoomControls();
	      
	        ll.addView(zoom, ZOOM_PARAMS);
	        zoom.setVisibility(View.VISIBLE);
	        
	        
	 }
	 
	 
}
