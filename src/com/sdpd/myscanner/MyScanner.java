package com.sdpd.myscanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.client.android.CaptureActivity;
import com.sdpd.flashrace.R;

public class MyScanner extends CaptureActivity 
{
    /** Called when the activity is first created. */
	String output = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanlayout);
    }
    
    @Override 
    public void handleDecode(Result rawResult, Bitmap barcode) 
    {
    	//Toast.makeText(this.getApplicationContext(), "Scanned code " + rawResult.getText(), Toast.LENGTH_LONG).show();
    	output = rawResult.getText();
    	finish();
    }
    @Override
    public void finish() {
      // Prepare data intent 
      Intent data = new Intent();
      data.putExtra("scanResult", output);
      // Activity finished ok, return the data
      setResult(RESULT_OK, data);
      super.finish();
    } 
}