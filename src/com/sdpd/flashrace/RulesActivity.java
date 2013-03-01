package com.sdpd.flashrace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class RulesActivity extends Activity{
	static Button nextbtn;
	static public int viewnum=0;
	ImageView imgview;
	int list[] = {R.drawable.htp1 ,R.drawable.htp2, R.drawable.htp3, R.drawable.htp4};
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	private ViewFlipper viewFlipper;
	public static WindowManager.LayoutParams lp;

	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.rules_layout); 
		lp = getWindow().getAttributes();
        checkBrightness();
        nextbtn = (Button) findViewById(R.id.buttonHowNext);
        imgview = (ImageView) findViewById(R.id.imageView1);
        if(viewnum==3){
        	nextbtn.setText("Play game");
        }
        new Thread(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				imgview.setImageResource(list[viewnum]);
			}
		}).run();
        
        viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
		slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
		slideLeftOut = AnimationUtils
				.loadAnimation(this, R.anim.slide_out_left);
		slideRightIn = AnimationUtils
				.loadAnimation(this, R.anim.slide_in_right);
		slideRightOut = AnimationUtils.loadAnimation(this,
				R.anim.slide_out_right);

		gestureDetector = new GestureDetector(new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		};
        
        nextbtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				nextrule();
			}
		});
       
    }
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		viewnum = 0;
		finish();
		overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
	}
	
	public void nextrule(){
		Intent i;
		if(viewnum==3){
			viewnum=0;
			i = new Intent(RulesActivity.this, MainActivity.class);
			imgview.destroyDrawingCache();
			finish();
			startActivity(i);
		}else{
			i = new Intent(RulesActivity.this, RulesActivity.class);
			viewnum = viewnum + 1;
			imgview.destroyDrawingCache();
			startActivity(i);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			finish();
		}
	}
	
	public void prevrule(){
		Intent i;
		if(viewnum==0){
			return;
		}else{
			i = new Intent(RulesActivity.this, RulesActivity.class);
			viewnum = viewnum - 1;
			imgview.destroyDrawingCache();
			startActivity(i);
			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			finish();
		}
	}
	
	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					nextrule();
					//Toast.makeText(RulesActivity.this, "Swype1", Toast.LENGTH_LONG).show();
					//viewFlipper.setInAnimation(slideLeftIn);
					//viewFlipper.setOutAnimation(slideLeftOut);
					//viewFlipper.showNext();
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					prevrule();
					//Toast.makeText(RulesActivity.this, "Swype2", Toast.LENGTH_LONG).show();
					//viewFlipper.setInAnimation(slideRightIn);
					//viewFlipper.setOutAnimation(slideRightOut);
					//viewFlipper.showPrevious();
				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event))
			return true;
		else
			return false;
	}

	public void checkBrightness()
        {
        	if(!MainActivity.isBright)
        	{
        		lp.screenBrightness = MainActivity.initBright;
        		getWindow().setAttributes(lp);
        		MainActivity.isBright = false;
        	}
        	else
        	{
        		lp.screenBrightness = (float) 1;
        		getWindow().setAttributes(lp);
        		MainActivity.isBright = true;
        	}

        }
	@Override
	protected void onResume() 
	{
		checkBrightness();
		super.onResume();
	}
	
}
