package com.sdpd.flashrace;

import java.io.File;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	int backcount = 0;
	public static ProgressDialog prgdlg;
	public static boolean isBright = false;
	public static float initBright;
	public WindowManager.LayoutParams lp;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		lp = getWindow().getAttributes();
		initBright = lp.screenBrightness;
		checkBrightness();
		prgdlg = ProgressDialog.show(MainActivity.this, "", "loading");
		prgdlg.setCancelable(true);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String name  = prefs.getString("nick","guest" );
		//the following commented part is for Howdy, Name in main page
		TextView nick = (TextView)findViewById(R.id.tvuser);
		nick.setText("Howdy, "+name+"!");
        final Button play = (Button)findViewById(R.id.btnPlay);
        final Button rules = (Button)findViewById(R.id.btnRules);
        Button lead = (Button)findViewById(R.id.btnLeaders);
        Button about = (Button)findViewById(R.id.btnAbout);
        Button mystats = (Button)findViewById(R.id.btnStats);
        play.setOnClickListener(new View.OnClickListener() 
        {	
			public void onClick(View v) 
			{
				Intent i = new Intent(getApplicationContext(),TaskList.class);
				startActivity(i);
				overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
			}
		});
        
        //the following part is for notifying user about start time of event
        /*
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        String date = ""+today.monthDay+":"+today.month+":"+today.year;
        String time = ""+today.hour+":"+today.minute;
        //Toast.makeText(this, "Date:"+date+"\nTime:"+time, Toast.LENGTH_LONG).show();    
        //play.setClickable(false);
  		
        */
        lead.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				//prgdlg = ProgressDialog.show(MainActivity.this, "", "loading");
				Intent i = new Intent(getApplicationContext(),Leaderboard.class);
				startActivity(i);
			}
		});
        mystats.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				prgdlg = ProgressDialog.show(MainActivity.this, "", "loading");
				Intent i = new Intent(getApplicationContext(),MyStatActivity.class);
				startActivity(i);
			}
		});
        rules.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				RulesActivity.viewnum = 0;
				prgdlg = ProgressDialog.show(MainActivity.this, "", "loading");
				Intent i = new Intent(getApplicationContext(),RulesActivity.class);
				startActivity(i);
			}
		});
	    about.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) 
			{
				Intent i = new Intent(getApplicationContext(),AboutActivity.class);
				startActivity(i);
			}
		});
    }
        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        backcount = 0;
        return true;
    }
        
        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
        	switch (item.getItemId())
            {
            case R.id.menu_settings: 
            	createResetDilaog();
            	return true;
            case R.id.outdoorvis:
            	if(isBright){
            		lp.screenBrightness = initBright;
            		getWindow().setAttributes(lp);
            		isBright = false;
            	}else{
            		lp.screenBrightness = (float) 1;
            		getWindow().setAttributes(lp);
            		isBright = true;
            	}
            	return true;
            }
			return false;
        
        }
        
    @Override
    protected void onResume() {
    	backcount = 0;
    	super.onResume();
    	if(prgdlg.isShowing()){
    		prgdlg.dismiss();
    	}
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    }
    
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	if(backcount==0){
    		Toast.makeText(MainActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
    		backcount ++;
    		
    	}
    	else{
    		if(prgdlg.isShowing()){
        		prgdlg.dismiss();
        	}
    		super.onBackPressed();
    	}
    }
    
    public static void clearApplicationData(Context context) {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                File f = new File(appDir, s);
                if(deleteDir(f))
                    Log.i("deleting", String.format("**************** DELETED -> (%s) " +
                    		"*******************", f.getAbsolutePath()));
            }
        }
        
    }
    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
    
    private void createResetDilaog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Warning!!!")
				.setCancelable(true)
				.setPositiveButton("Yeah!", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id){
						killgame();
					}
				}).setNegativeButton("Nope!", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();						
					}
				}).setMessage("This will enable a new user to play the game and the game will be reset like new. " +
						"You can also see your statistics online at goldrush.in." +
						"\nAre you sure to reset game?");
		AlertDialog alert = builder.create();
		alert.show();

	}
    
    private void killgame() {
    	Toast.makeText(getApplicationContext(), "Game Reset, Restart " +
				"to begin with a new game", Toast.LENGTH_LONG).show();
    	clearApplicationData(getApplicationContext());
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Game has been reset.")
    		.setCancelable(false)
    		.setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			}).setMessage("Restart game to begin with a new user.");
    	AlertDialog alert = builder.create();
		alert.show(); 
        

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
        
}
