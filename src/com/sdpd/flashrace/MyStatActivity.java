package com.sdpd.flashrace;

import java.util.StringTokenizer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Contacts.SettingsColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MyStatActivity extends Activity
{
	int count = 0,myrank=0;
	String response;
	TextView tvnick,tvpoints,tvperc,tvrank;
	public WindowManager.LayoutParams lp;
	ProgressDialog pd;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		lp = getWindow().getAttributes();
		
		checkBrightness();
		//overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.mystat_layout);
        tvnick = (TextView)findViewById(R.id.textNick);
        tvpoints = (TextView)findViewById(R.id.textPoints);
        tvperc = (TextView)findViewById(R.id.textPerc);
        tvrank = (TextView)findViewById(R.id.textRank);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		tvnick.setText("User Nick: "+prefs.getString("nick", "null"));
		tvpoints.setText("Coins collected: "+prefs.getInt("points", 0));
		int len = prefs.getInt("count", 0);
		for(int i=1;i<=len;i++)
		{
			int stat = prefs.getInt("q"+i, 0);
			if(stat == 1)
			{
				count++;
			}
		}
		tvperc.setText("Tasks completed: "+count+" out of "+len+" Tasks");
		//setGlobalRank();
	
		GetGlobalRankTask task = new GetGlobalRankTask();
		pd = ProgressDialog.show(this,"", "Fetching global rank");
		pd.setCancelable(true);
		task.execute();
	}
	public int setGlobalRank()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String mynick = prefs.getString("nick", "dummy");
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			String getURL = MyConsts.NETWORK_FOLDER+"getrank.php"+"?nick="+mynick;
			HttpGet get = new HttpGet(getURL);
	        HttpResponse httpResponse = httpclient.execute(get);  
	        
			HttpEntity resEntityGet = httpResponse.getEntity();  
	        if (resEntityGet != null) 
	        {  
	        	response = EntityUtils.toString(resEntityGet);
                Log.i("GET RESPONSE",response);
                Log.d("response", response);            
	        }
	        StringTokenizer tmptoken = new StringTokenizer(response,":");
	        
	        myrank = Integer.parseInt(tmptoken.nextToken());
		} 
		catch(Exception e)
		{
			Toast.makeText(this, "Unable to fetch global rank: "+e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		return myrank;
		
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		finish();
		super.onPause();
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
   	 private class GetGlobalRankTask extends AsyncTask<String, Void, Integer> 
   	 {
   		    @Override
   		    protected Integer doInBackground(String... params) 
   		    {
   		    	int response =  setGlobalRank();
   		    	return response;
   		    }

   		    @Override
   		    protected void onPostExecute(Integer result) 
   		    {
   		    	//postLeaderData(result);
   		    	tvrank.setText("Global Rank : "+result);
   		    	if(pd.isShowing())
   		    		pd.dismiss();
   		    	
   		    }
   	 }
}
