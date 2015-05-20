package sam.intents;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import sam.tour.guide.R;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.VideoView;

public class AudioIntent extends Activity {
	private static final String TAG = "VideoViewDemo";

	private VideoView mVideoView;

	private Button mPlay;
	private Button mPause;
	private Button mReset;
	private Button mStop;
	private String current;
	private int currentPosition;
	private String filePath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audio);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		mVideoView = (VideoView) findViewById(R.id.surface_view);

		Bundle b = getIntent().getExtras();
		
		String fileName = b.getString("fileName");
		
		filePath = "/sdcard/tour guide/assets/" + fileName;

		mPlay = (Button) findViewById(R.id.play);
		mPause = (Button) findViewById(R.id.pause);
		mReset = (Button) findViewById(R.id.reset);
		mStop = (Button) findViewById(R.id.stop);

		mPlay.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if(currentPosition != 0){
					mVideoView.seekTo(currentPosition);
					mVideoView.start();
				}
				else{
				playVideo();
				}
				
			}
		});
		mPause.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (mVideoView != null) {
					mVideoView.pause();
					currentPosition = mVideoView.getCurrentPosition();
				}
			
			}
		});
		mReset.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (mVideoView != null) {
					mVideoView.seekTo(0);
					mVideoView.getCurrentPosition();
				}
			}
		});
		mStop.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (mVideoView != null) {
					mVideoView.stopPlayback();
				}
			}
		});
		runOnUiThread(new Runnable(){
			public void run() {
				playVideo();
				
			}
			
		});
	}

	private void playVideo() {
		
				// If the path has not changed, just start the media player
				
				try {
					mVideoView.setVideoPath(getDataSource(filePath));
					current = filePath;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mVideoView.start();
				mVideoView.requestFocus();

			}
	
	

	private String getDataSource(String path) throws IOException {
		if (!URLUtil.isNetworkUrl(path)) {
			return path;
		} else {
			URL url = new URL(path);
			URLConnection cn = url.openConnection();
			cn.connect();
			InputStream stream = cn.getInputStream();
			if (stream == null)
				throw new RuntimeException("stream is null");
			File temp = File.createTempFile("mediaplayertmp", "dat");
			temp.deleteOnExit();
			String tempPath = temp.getAbsolutePath();
			FileOutputStream out = new FileOutputStream(temp);
			byte buf[] = new byte[128];
			do {
				int numread = stream.read(buf);
				if (numread <= 0)
					break;
				out.write(buf, 0, numread);
			} while (true);
			try {
				stream.close();
			} catch (IOException ex) {
				Log.e(TAG, "error: " + ex.getMessage(), ex);
			}
			return tempPath;
		}
	}
	
	protected void onPause(){
		super.onPause();
		System.out.println("onPause");
		mVideoView.stopPlayback();
		
	}
	
	protected void onDestroy(){
		super.onDestroy();
		System.out.println("onDestroy");
		mVideoView.stopPlayback();
		
		
	}
}
