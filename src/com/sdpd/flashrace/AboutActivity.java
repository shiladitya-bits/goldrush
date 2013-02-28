package com.sdpd.flashrace;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

public class AboutActivity extends Activity
{
	public WindowManager.LayoutParams lp;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		//getWindow().setWindowAnimations(android.R.anim.bounce_interpolator);
		this.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_right);
		setContentView(R.layout.about_layout);
		lp = getWindow().getAttributes();
		
		checkBrightness();
	}

	@Override
	protected void onPause() 
	{
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_bottom);
		super.onPause();
		finish();
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
        
        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
        	switch (item.getItemId())
            {
            case R.id.menu_settings: 
            	Toast.makeText(this, "Can be done through Home page only.", Toast.LENGTH_SHORT).show();
            	return true;
            case R.id.outdoorvis:
            	if(MainActivity.isBright)
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

            	return true;
            }
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
