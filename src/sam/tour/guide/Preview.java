package sam.tour.guide;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Preview extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = "Preview";

	SurfaceHolder mHolder;
	public Camera camera;
	Parameters cameraParameters = null;
	List <String> list = new ArrayList<String>();
	private int[] currentCameraSize = new int[2];
	
	
	Preview(Context context) {
		super(context);

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		//the higher the getcamerasize number, the lower the resolution
		currentCameraSize[0] = getCameraSize(0)[1];
		currentCameraSize[1] = getCameraSize(0)[0];
	

		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, acquire the camera and tell it where
		// to draw.
		camera = Camera.open();
		try {
			camera.setPreviewDisplay(holder);
			camera.setPreviewCallback(new PreviewCallback() {
				public void onPreviewFrame(byte[] data, Camera arg1) {
					Preview.this.invalidate();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		camera.setDisplayOrientation(90);
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when we return, so stop the preview.
		// Because the CameraDevice object is not a shared resource, it's very
		// important to release it when the activity is paused.
		// setPreviewCallback as null in order to stop it crashing and calling callbacks after finish 
		camera.setPreviewCallback(null);
		camera.stopPreview();
		camera.release();
		
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// Now that the size is known, set up the camera parameters and begin
		// the preview.
		
		Camera.Parameters parameters = camera.getParameters();
		//parameters.setPreviewSize(currentCameraSize[0],currentCameraSize[1]);
		//.out.println(currentCameraSize[1] + " " + currentCameraSize[0]);
		parameters.set("orientation", "landscape");
		//parameters.setPreviewFormat(ImageFormat.JPEG);
		parameters.setJpegQuality(50);
		parameters.setPreviewSize(640,480);
		camera.setParameters(parameters);
		camera.startPreview();
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		Paint p = new Paint(Color.RED);
		Log.d(TAG, "draw");
		canvas.drawText("PREVIEW", canvas.getWidth() / 2,
				canvas.getHeight() / 2, p);
	}
	
	public int[] getCameraSize(int i){
		String [] camString = new String[3];
		int[] d  = new int[2];
		for (int j = 0; j < list.size(); j++){
			String tempString = list.get(j);
			camString = tempString.split(",");
			int e = Integer.parseInt(camString[0]);
			if (e == i){
				d[0] = Integer.parseInt(camString[1]);
				d[1] = Integer.parseInt(camString[2]);
			}
			
		}
		
		return d;
	}
	
	public Parameters getCameraParameters(){
		
		return camera.getParameters();
		
	}
	

}
