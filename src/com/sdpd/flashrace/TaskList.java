package com.sdpd.flashrace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TaskList extends Activity implements OnItemClickListener
{
	private QuestionDataSource datasource;
	SimpleAdapter simp;
	List<HashMap<String,Object>> alist;
	ArrayList<Long> values;
	public static WindowManager.LayoutParams lp;

	int[] drawsint = {R.drawable.incomplete,R.drawable.complete};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tasklist);
		lp = getWindow().getAttributes();

		checkBrightness();
		ListView lv = (ListView)findViewById(R.id.listViewTasks);
		lv.setOnItemClickListener(this);
		datasource = new QuestionDataSource(this);
		datasource.open();
		values = datasource.getAllIds();	
		alist = new ArrayList<HashMap<String,Object>>();
		for(int ptr=0;ptr<values.size();ptr++)
		{
			Question q = datasource.getQsAtId(ptr);
			Log.d("tlist","pts="+q.getPoints()+"status= "+q.getStatus()+"q="+q.getQuestion());
			HashMap<String,Object> hmap = new HashMap<String, Object>();
			hmap.put("status", drawsint[q.getStatus()]);
			hmap.put("taskid", "Task "+(ptr+1));
			hmap.put("points", " "+q.getPoints());
			alist.add(hmap);
		}
		String[] from = {"status","taskid","points"};
		int[] to = {R.id.imgbtnStatus,R.id.colTaskId,R.id.colPts};
		simp = new SimpleAdapter(getBaseContext(), alist, R.layout.tasklist_row, from, to);
		lv.setAdapter(simp);
		lv.setCacheColorHint(0x00000000);

	}
	@Override
	protected void onResume() 
	{
		checkBrightness();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		for(int i=0;i<values.size();i++)
		{
			Question q = datasource.getQsAtId(i);
			int stat = prefs.getInt("q"+q.getqNum(), 0);
			
			if(stat == 1)
			{
				HashMap<String,Object> hmap = alist.get(i);
				hmap.put("status",drawsint[1]);
				//alist.add(hmap);
			}
		}
		simp.notifyDataSetChanged();
		super.onResume();
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent i = new Intent(this,newGame.class);
		i.putExtra("id", arg2);
		startActivity(i);
		overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
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
    	
}
