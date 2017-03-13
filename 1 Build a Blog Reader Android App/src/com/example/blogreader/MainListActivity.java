package com.example.blogreader;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainListActivity extends ListActivity implements Communicator{

	/*
	 * public- scope 
	 * static- sigifica que podemos assesar esta variavel sem numhuma instancia a esta classe
	 * final- nao muda
	 */
	public static final int nPosts = 20;
	public static final String TAG = MainListActivity.class.getSimpleName();
	protected ProgressBar mProgressBar;
	public JSONObject mBlogData;
	
	private final String KEY_TITLE="title", KEY_AUTHOR="author";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_list);
		mProgressBar= (ProgressBar) findViewById(R.id.progressBar1);
		
		if (isNetworkAvaiable()) {
			mProgressBar.setVisibility(View.VISIBLE);
			GetBlogPostsTask getBlogPostsTask = new GetBlogPostsTask(this);
			getBlogPostsTask.execute();
		} else {
			Toast.makeText(this, "Network is unavailable!", Toast.LENGTH_SHORT).show();
		}
	}

	private boolean isNetworkAvaiable() {
		boolean isAvailable= false;
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (networkInfo!=null && networkInfo.isConnected()) {
			isAvailable=true;
		} 
		return isAvailable;
	}

	@Override
	public void handleBlogResponse(String result) {
		mProgressBar.setVisibility(View.INVISIBLE);
		try {
			mBlogData= new JSONObject(result);
			if (mBlogData==null) {
				updateDisplayForError();
			}
			JSONArray jsonPosts = mBlogData.getJSONArray("posts");
			ArrayList<HashMap<String, String>> blogPosts= new ArrayList<HashMap<String,String>>();
			
			for (int i = 0; i < jsonPosts.length(); i++) {
				JSONObject post = jsonPosts.getJSONObject(i);
				
				//nao termos problemas com caracteres especiais
				String title= post.getString(KEY_TITLE);
				title= Html.fromHtml(title).toString();

				//nao termos problemas com caracteres especiais
				String author= post.getString(KEY_AUTHOR);
				author= Html.fromHtml(author).toString();
				
				HashMap<String, String> blogPost = new HashMap<String, String>();
				blogPost.put(KEY_TITLE, title);
				blogPost.put(KEY_AUTHOR, author);
				blogPosts.add(blogPost);
			}
			//criar custom_row em java
			String[] keys={KEY_TITLE,KEY_AUTHOR}; //definimos nome campos
			int[] ids= {android.R.id.text1, android.R.id.text2};// associamos ids
			SimpleAdapter adapter = new SimpleAdapter(this, blogPosts, android.R.layout.simple_list_item_2, keys, ids);
			setListAdapter(adapter);	
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		try {
			JSONArray jsonPosts= mBlogData.getJSONArray("posts");
			JSONObject jsonPost = jsonPosts.getJSONObject(position);
			String url = jsonPost.getString("url");
			
			Intent intent = new Intent(this, BlogWebViewActivity.class);
			intent.setData(Uri.parse(url));
			startActivity(intent);
		} catch (JSONException e) {
			logExeception(e);
		}
	}

	private void logExeception(Exception e) {
		Log.i("andre", ""+e);
	}

	private void updateDisplayForError() {
		//criar alert box atravez design patern factory
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.title);
		builder.setMessage(R.string.message);
		builder.setPositiveButton(android.R.string.ok, null);
		
		//mostrar
		AlertDialog dialog= builder.create();
		dialog.show();
		
		TextView emptyTextView = (TextView) getListView().getEmptyView();
		emptyTextView.setText(getString(R.string.no_items));
	}
}
