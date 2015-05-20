package sam.tour.guide;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.widget.FrameLayout;

public class DrawOnTop extends View {
public FrameLayout layout;
private int crossColor;
static Context context;
private int color;
double ratioX;
double ratioY;
Paint paint;

		public DrawOnTop(Context context, FrameLayout layout) {
			super(context);
			// TODO Auto-generated constructor stub
			this.layout = layout;
		}
		
		
		public DrawOnTop(Context context){
			super(context);
		}

	

		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub

			paint = new Paint();
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(crossColor);
			Rect yrect = new Rect();
			Rect xrect = new Rect();
			Rect trect = new Rect();
			Rect nrect = new Rect();
			//x and y for centre point
			int x = (int) (layout.getWidth()/2);
			int y = (int) (layout.getHeight()/2 - 30);
			//left, top, right, bottom
			yrect.set(x - 1, (int)((y * 2 + 65)-ratioY), x + 5,  (int)((y * 2 + 95)-ratioY));
			trect.set(x - 1,  (int)((ratioY) - 30), x + 5, (int)(ratioY + 30) -30);
			xrect.set((int)(-ratioX) - 30, y + 36, (int)(30 - ratioX) -30, y + 30);
			nrect.set((int)((x * 2)+ratioX+5), y + 36, (int)((x * 2 + 36) + ratioX), y + 30);
			
			canvas.drawRect(trect, paint);
			canvas.drawRect(yrect, paint);
			canvas.drawRect(xrect, paint);
			canvas.drawRect(nrect, paint);
			
			//draw static rect
			Paint Spaint = new Paint();
			Spaint.setStyle(Paint.Style.FILL);
			Spaint.setColor(Color.WHITE);
			Spaint.setAlpha(100);
			Rect arect = new Rect();
			Rect brect = new Rect();
			Rect crect = new Rect();
			Rect drect = new Rect();
			//x and y for centre point
			int dx = layout.getWidth()/2;
			int dy = layout.getHeight()/2 - 30;
			arect.set(dx,  dy + 60, dx + 5, dy + 90);
			brect.set(dx,  dy - 35, dx + 5, dy - 5 );
			crect.set(dx - 60, dy + 35, dx - 30, dy + 30);
			drect.set(dx + 60, dy + 35, dx + 30, dy + 30);
			
			canvas.drawRect(arect, Spaint);
			canvas.drawRect(brect, Spaint);
			canvas.drawRect(crect, Spaint);
			canvas.drawRect(drect, Spaint);
			
			
			

			super.onDraw(canvas);
		}
		
		public void setCrossColor(boolean targetInCross){
			
			if (targetInCross == true ){
				crossColor = Color.GREEN;
			}
			
			else{
				crossColor = Color.RED;
			}
			
				
			
		}
		
		public void overrideCrossColor(int color){
			this.color = color;
			switch(color){
				case 0:
					
					crossColor = Color.GREEN;
				break;
				case 1:
					
					crossColor = Color.RED;
				break;
				case 2: 
					
					crossColor = Color.BLUE;
				break;
				case 3:
					

					crossColor = Color.WHITE;
					
			}
		}
		
		public int getCrossColor(){
			return color;
		}
		
		public void setCrossSize(double distance, double arraySize){
			
			double maxX = layout.getWidth()/2;
			double maxY = layout.getHeight()/2;
			
			double ratio = distance/(arraySize/4);
			double adjRatio;
			
			
			if(ratio > 1){
				adjRatio = 1;
				
			}
			else{
				adjRatio = ratio;
			}
			ratioX = -(adjRatio * maxX);
			ratioY = (adjRatio * maxY);
			
			//System.out.println("distance + " + distance + " RatioX = " + ratioX + " ratioY = " +ratioY);
			
			
			
			
		}
	public void setDefault(){
		ratioX = 0.5;
		ratioY = 0.5;
	}

	}

