package sam.tour.guide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class StartWithMaskName extends Activity{
	
	EditText edRaster;
	EditText edPOI;
	Button button;
	Editable rasterPath;
	Editable POIPath;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        
      
        
        edRaster = (EditText)findViewById(R.id.fileRasterPath);
        edPOI = (EditText)findViewById(R.id.filePOIPath);
        
        button = (Button)findViewById(R.id.startButton);
        
        button.setOnClickListener(new Button.OnClickListener(){
        	
        	public void onClick(View view){
        		
        		rasterPath = edRaster.getText();
        		POIPath = edPOI.getText();
        		
        		startMainIntent();
        		
        	
        	}
        }
        
        );
   
}
	
	private void startMainIntent(){
		  Bundle b = new Bundle();
		  b.putString("rasterPath", rasterPath.toString());
		  b.putString("POIPath",POIPath.toString());
		  
		  Intent i = new Intent(this, Start.class);
		  i.putExtras(b);
		  startActivity(i);
		  
		
	}
}
