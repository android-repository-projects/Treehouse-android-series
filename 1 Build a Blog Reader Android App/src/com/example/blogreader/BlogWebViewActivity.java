package com.example.blogreader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class BlogWebViewActivity extends Activity {
	
	protected String mUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blog_web_view);
		
		Intent intent= getIntent();
		Uri blogUri = intent.getData();
		mUrl=blogUri.toString();
		
		WebView webview = (WebView) findViewById(R.id.webView1);
		webview.loadUrl(mUrl);
	}
	
	//mostrar botao opçoes na action bar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.my_options_menu, menu);
		return true;
	}
	
	/*
	 * Listener para Action Bar Defenicoes
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId= item.getItemId();
		if (itemId==R.id.action_share) {
			sharePost();
		}
		return super.onOptionsItemSelected(item);
	}

	private void sharePost() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		//tipo dados q vamos partilhar
		shareIntent.setType("text/plain");//defenir tipo intent
		shareIntent.putExtra(Intent.EXTRA_TEXT,mUrl);//vamos partilhar extra URL texto que adicionamos como data na actividade anterior
		startActivity(Intent.createChooser(shareIntent, "How do you want to Share?"));
	}
}
