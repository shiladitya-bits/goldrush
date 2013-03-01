package com.sdpd.flashrace;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.sdpd.myscanner.MyScanner;

public class newGame extends Activity
{
    GestureDetector gestureDetector;
	int REQUEST_CODE = 1;
	QuestionDataSource datasource;
	Question q;
	int pos;
	String nick;
	String response;
	LocationManager locationManager;
	Location pointLocation;  
	LocationListener listen;
	ProgressDialog pd;
	String answer_md5;
	EditText et;
	ListView lvoption;
	ArrayAdapter adap;
	AlertDialog.Builder builder;
	Dialog d;
	public WindowManager.LayoutParams lp;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gamelayout);
		lp = getWindow().getAttributes();
		
	    gestureDetector = new GestureDetector(new SwipeDetector());
	    
	    ScrollView scroller = (ScrollView)findViewById(R.id.scrollView1);
	    scroller.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});
	    
	    pos = getIntent().getExtras().getInt("id");
		datasource = new QuestionDataSource(this);
		datasource.open();
		q = datasource.getQsAtId(pos);
		TextView tvid = (TextView)findViewById(R.id.tvtaskid);
		TextView tvqs = (TextView)findViewById(R.id.tvquestion);
		TextView tvpts = (TextView)findViewById(R.id.tvpoints);
		TextView tvstatus = (TextView)findViewById(R.id.tvstatus);
		tvid.setText("Task #"+q.getId());
		tvqs.setText(""+q.getQuestion());
		tvpts.setText("Gold Coins = "+q.getPoints());
		
		//ALTERNATE SCAN OPTION
		answer_md5 = md5(q.getAnswer()).toUpperCase();
		//Toast.makeText(this, answer_md5, Toast.LENGTH_LONG).show();
		
		//GOING TO NEXT OR PREV ACTIVITYs
		Button next = (Button)findViewById(R.id.buttonNext);
		Button prev = (Button)findViewById(R.id.buttonPrev);
		next.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				int n = prefs.getInt("count", 0);
			
		    	if(pos<n-1)
		    	{
		    		Intent i = new Intent(newGame.this,newGame.class);
		    		i.putExtra("id", pos+1);
		    		startActivity(i);
		    		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		    		finish();
		    	}else{
		    		Toast.makeText(newGame.this, "No next Question", Toast.LENGTH_SHORT).show();
		    	}
		    
			}
		});
		prev.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(pos>0)
				{
					Intent i = new Intent(newGame.this,newGame.class);
		    		i.putExtra("id", pos-1);
		    		startActivity(i);
		    		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		    		finish();
				}else{
					Toast.makeText(newGame.this, "No previous Question", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		//SHOW HINTS BUTTON
		Button hints = (Button)findViewById(R.id.buttonHints);
		hints.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) 
			{
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				int hint_stat = prefs.getInt("hint"+q.getqNum(), 0);
				if(hint_stat == 1)
				{
					showHints();
				}
				else
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(newGame.this);
					builder.setTitle("Oops!");
					builder.setMessage("Hints only available after you reach close " +
							"to your destination. \nMake sure your GPS is turned on.");
					builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							return;
						}
					});  //end of main dialog
					builder.show();
				}
				
			}
		});  //end of on click of hints button
		
		//SCAN QR CODE BUTTON
		Button scan = (Button)findViewById(R.id.btnScan);
		if(q.getStatus() == 0)
		{
			tvstatus.setText("Incomplete");
			tvstatus.setTextColor(0xffdd0000);
			scan.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) 
				{
					newGame.this.onClickQR();
					//TODO: dialog box maal
				}
			});
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			int hint_stat = prefs.getInt("hint"+q.getqNum(), 0);
			if(hint_stat == 0) //hint not yet unlocked
			{
				
				try
				{
					//TODO: give hints for user. load hint status from shared prefs
					locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
					pointLocation = new Location("ORIGIN");
					pointLocation.setLatitude(Double.parseDouble(q.getLatitude()));
					pointLocation.setLongitude(Double.parseDouble(q.getLongitute()));
			        listen = new MyLocationListener();
					locationManager.requestLocationUpdates(
			                        LocationManager.GPS_PROVIDER, 
			                        MyConsts.MINIMUM_TIME_BETWEEN_UPDATE, 
			                        MyConsts.MINIMUM_DISTANCECHANGE_FOR_UPDATE,
			                        listen);
				}
				catch(Exception e)
				{
					Log.i("proximity",e.getMessage());
					e.printStackTrace();
				}
			}
			else //hint already unlocked
			{
				//showHints();
			}
		}
		else
		{
			tvstatus.setText("Complete");
			tvstatus.setTextColor(0xff00aa00);
			scan.setVisibility(View.GONE);	
			hints.setVisibility(View.GONE);
		}
		//Toast.makeText(this, "question= "+q.getQuestion(), Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
	  if (resultCode == RESULT_OK) 
	  {
	    if (data.hasExtra("scanResult")) 
	    {
	    	//Toast.makeText(this, data.getExtras().getString("scanResult"),Toast.LENGTH_SHORT).show();
	    	if(q.getAnswer().equals(data.getExtras().getString("scanResult")))
	    	{
	    		Toast.makeText(this, "correct answer! :)", Toast.LENGTH_SHORT).show();
	    		//updatePoints();
	    		pd = ProgressDialog.show(newGame.this,"", "Uploading score");
	    		pd.setCancelable(true);
	    		PointUpdater task = new PointUpdater();
	    		task.execute();
	    		//datasource.close();
	    	}
	    	else
	    	{	
	    		Toast.makeText(this, "incorrect answer! :(", Toast.LENGTH_SHORT).show();
	    	}
	    }
	  }
	}
	public void analyzeOption(int pos)
	{
		if(pos == 0)
		{
			//Toast.makeText(this,"option 1" , Toast.LENGTH_SHORT).show();
			Intent i = new Intent(getBaseContext(),MyScanner.class);
			startActivityForResult(i,REQUEST_CODE);
	
		}
		else if(pos == 1)
		{
			//Toast.makeText(this,"option 2" , Toast.LENGTH_SHORT).show();
			AlertDialog.Builder builder = new AlertDialog.Builder(newGame.this);
			builder.setTitle("Scan Code");
			builder.setMessage("Type in the alternate code printed below QR code");
			et = new EditText(newGame.this);
			builder.setView(et);
			
			//ok button of dialog
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					String ret = et.getText().toString();
					if(ret.equals(answer_md5))
					{
						//updatePoints();
						pd = ProgressDialog.show(newGame.this,"", "Uploading score");
						pd.setCancelable(true);
						PointUpdater task = new PointUpdater();
						task.execute();
					}
					else
					{
						Toast.makeText(newGame.this, "Incorrect answer :(", Toast.LENGTH_SHORT).show();
					}
					return;
				}
			});
			//cancel button of dialog
			builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			});  //end of main dialog
			builder.show();
		}
	}
	public void onClickQR()
	{
		builder = new AlertDialog.Builder(newGame.this);
		lvoption = new ListView(newGame.this);
		String[] options = {"Capture QR code!","Type alternate code!"};
		adap = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,options);
		lvoption.setAdapter(adap);
		lvoption.setOnItemClickListener(new OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
			{
				d.cancel();
				newGame.this.analyzeOption(arg2);
			}
		});
		builder.setView(lvoption);
		d = builder.create();
		d.show();
		
	}
	void updatePointsUI()
	{
		
		Button btn = (Button)findViewById(R.id.btnScan);
		btn.setVisibility(View.GONE);
		Button btn3 = (Button)findViewById(R.id.buttonHints);
		btn3.setVisibility(View.GONE);
		
		TextView tvstatus = (TextView)findViewById(R.id.tvstatus);
		tvstatus.setText("Complete");
		tvstatus.setTextColor(0xff00aa00);
		q.setStatus(1);
		datasource.updateStatus(q);
		
	}
	public boolean updatePointsServer()
	{
		try
		{
			//update points of user in shared pref
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    		int pts  = prefs.getInt("points" ,0);    //get current points
    		nick = prefs.getString("nick", "null");
    		pts = pts + Integer.parseInt(q.getPoints());  //new points  sum
			int hint_stat = prefs.getInt("hint"+q.getqNum(), 0);
    		if(hint_stat == 1)
    		{
    			pts = pts -2;
    		}
			HttpClient httpclient = new DefaultHttpClient();
			String getURL = MyConsts.NETWORK_FOLDER+"updatepoints.php"+"?nick="+nick+"&points="+pts;
			HttpGet get = new HttpGet(getURL);
	        HttpResponse httpResponse = httpclient.execute(get);  
	        
			HttpEntity resEntityGet = httpResponse.getEntity();  
	        if (resEntityGet != null) 
	        {  
               response = EntityUtils.toString(resEntityGet);

                Log.d("response", response);
               
                if(response.trim().charAt(0) == '1')
    	        {	
            		Log.d("point update", "success");
            		SharedPreferences.Editor editor = prefs.edit();
            		editor.putInt("points", pts); 				//update points of user
            		editor.putInt("q"+q.getqNum(), 1);			//update questions answered status to true
            		editor.commit();
            		return true;
    	        }
    	        else
    	        {
            		Log.d("point update", "failure");
    	        	return false;
    	        }	             
	        }
	        else
	        {
        		Log.d("point update", "response null");
        		return false;
	        }
		} 
		catch(Exception e)
		{
			//Toast.makeText(this, "some android side problem:"+e.getMessage(), Toast.LENGTH_SHORT).show();
    		Log.d("point update", e.getMessage());
			e.printStackTrace();
		}
		Log.d("point update", "not sure what happened!");
		return false;

	}
	private class PointUpdater extends AsyncTask<String, Void,Boolean> 
	{
		    @Override
		    protected Boolean doInBackground(String... params) 
		    {
	    		Log.d("async task1","uploading points");
		    	boolean response = updatePointsServer(); 
		    	Log.d("async task1","updated response="+response);
		    	return response;
		    }

		    @Override
		    protected void onPostExecute(Boolean result) 
		    {
		    	if(pd.isShowing())
		    		pd.dismiss();
		    	Log.d("async task1","updating UI");
		    	if(result == true)
		    	{
		    		updatePointsUI();
		    		Log.d("async task1","ui updated");
		    		Toast.makeText(getApplicationContext(), "Successfully updated points", Toast.LENGTH_SHORT).show();
					//finish();
		    	}
		    	else
		    	{	
		    		Toast.makeText(getApplicationContext(), "Problem in updating points on server. Please try again", Toast.LENGTH_SHORT).show();
		    	}
		    }
	 }
	public class MyLocationListener implements LocationListener 
	{
        public void onLocationChanged(Location location) 
        {
            float distance = location.distanceTo(pointLocation);
            Toast.makeText(newGame.this,"Distance from Point:"+distance, Toast.LENGTH_SHORT).show();
            if(distance<=10000)
            {
            	Toast.makeText(newGame.this, "Hint unlocked!", Toast.LENGTH_SHORT).show();
            	NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                PendingIntent pendingIntent = PendingIntent.getActivity(newGame.this, 0, null, 0);        
                
                Notification notification = createNotification();
                notification.setLatestEventInfo(newGame.this, "GOLD RUSH: Hint!", "You have unlocked hint for your question.", pendingIntent);
                
                notificationManager.notify(MyConsts.NOTIFICATION_ID, notification);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				SharedPreferences.Editor editor = prefs.edit();
				editor.putInt("hint"+q.getqNum(), 1);
				editor.commit();
				//showHints();
                locationManager.removeUpdates(this);
            }
   
        }
        public void onStatusChanged(String s, int i, Bundle b) {            
        }
        public void onProviderDisabled(String s) {
        }
        public void onProviderEnabled(String s) {            
        }
    }
	void showHints()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		int seenStatus = prefs.getInt("hintSeen"+q.getqNum(), 0);
		if(seenStatus == 1)
		{
			Dialog d = new Dialog(newGame.this);
			d.setTitle(q.getHint());
			d.show();
			return;
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(newGame.this);
		builder.setTitle("Warning!");
		builder.setMessage("Are you sure you want to use the hint? This will lead to lesser coins.");
		//ok button
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				SharedPreferences.Editor editor = prefs.edit();
				editor.putInt("hintSeen"+q.getqNum(), 1);
				editor.commit();	
				
				Dialog d = new Dialog(newGame.this);
				d.setTitle(q.getHint());
				d.show();
			}
		});
		//cancel button
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});  //end of main dialog
		builder.show();
	}
    private Notification createNotification() 
    {
        Notification notification = new Notification();
        
        notification.icon = R.drawable.hint;
        notification.when = System.currentTimeMillis();
        
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        
        notification.ledARGB = Color.WHITE;
        notification.ledOnMS = 1;
        notification.ledOffMS = 0;
        
        return notification;
    } 
    @Override
    public boolean onTouchEvent(MotionEvent event) 
    {
    	return gestureDetector.onTouchEvent(event); 
    	
    }

    public void onLeftSwipe() 
    {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		int n = prefs.getInt("count", 0);
	
    	if(pos<n-1)
    	{
    		Intent i = new Intent(this,newGame.class);
    		i.putExtra("id", pos+1);
    		startActivity(i);
    		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    		
    		finish();
    	}else{
    		Toast.makeText(newGame.this, "No next Question", Toast.LENGTH_SHORT).show();
    	}
    }

    public void onRightSwipe() 
    {
    	if(pos>0)
		{
			Intent i = new Intent(this,newGame.class);
    		i.putExtra("id", pos-1);
    		startActivity(i);
    		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    		 		
    		finish();
		}else{
			Toast.makeText(newGame.this, "No previous Question", Toast.LENGTH_SHORT).show();
		}
    }

private class SwipeDetector  extends SimpleOnGestureListener {
    // Swipe properties, you can change it to make the swipe
    // longer or shorter and speed
    private static final int SWIPE_MIN_DISTANCE = 100;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2,float velocityX, float velocityY) 
    {
    	Log.d("on fling", "on fling detected");
    	try 
    	{
	        float diffAbs = Math.abs(e1.getY() - e2.getY());
	        float diff = e1.getX() - e2.getX();

	        if (diffAbs > SWIPE_MAX_OFF_PATH)
	          return false;
	       
	        // Left swipe
	        if (diff > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) 
	        {
	        	Log.d("Swipe Dewtected", "left");
	        	newGame.this.onLeftSwipe();
	        	
	        // Right swipe
	        } 
	        else if (-diff > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) 
	        {
	        	Log.d("Swipe Dewtected", "right");
	        	newGame.this.onRightSwipe();
	        }
	        return true;
	      } 
	      catch (Exception e) 
	      {
	        Log.e("gesture detector", "Error on gestures");
	      }
	      return false;
	    }
	}
	@Override
	public void onBackPressed() 
	{
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
	}
	public String md5(String s) 
	{	
	    try 
	    {
	        // Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();

	        // Create Hex String
	        StringBuffer hexString = new StringBuffer();
	        for (int i = 0; i < messageDigest.length; i++) 
	        {	
	            String h = Integer.toHexString(0xFF & messageDigest[i]);
	            while (h.length() < 2)
	                h = "0" + h;
	            hexString.append(h);
	        }
	        return hexString.substring(0, 8).toString();  

	    }
	    catch (NoSuchAlgorithmException e) 
	    {
	        e.printStackTrace();
	    }
	    return "";
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
