package sam.intents;

import sam.tour.guide.R;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageIntent extends Activity{

	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.image);
	        
	        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	        
	        Bundle b = new Bundle();
	        
	        b = getIntent().getExtras();
	       	               
	        String fileName = b.getString("fileName");
	        
	        System.out.println(fileName);
	        
	        ImageView im = new ImageView(this);
	        
	        String imagePath = "/sdcard/Tour Guide/assets/" + fileName;
	        
	        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
	        
	        im.setImageBitmap(bitmap);
	        
	        setContentView(im);
	 
	 }
	 
	 public void onDestroy(){
		 super.onDestroy();
		 this.finish();
		 
	 }
	 
	 public void onPause(){
		 super.onPause();
		 this.finish();
	 }
}
