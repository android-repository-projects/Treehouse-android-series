package com.teamtreehouse.ribbit;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ViewImageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_image);
		
		ImageView imageView= (ImageView) findViewById(R.id.imageView);
		Uri imageUri=getIntent().getData();
		//using picasso
		Picasso.with(this).load(imageUri.toString()).into(imageView);//INCRIVEL CARALHO!!!!!
		
		Timer timer= new Timer();
		timer.schedule(new TimerTask() {//THREAD
			@Override
			public void run() {
				finish();
			}
		}, 5*1000);//DADOS EM ms
	}
}
