package com.example.blogreader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

public class GetBlogPostsTask extends AsyncTask<Object, Void, JSONObject> {

	Communicator comm;
	public GetBlogPostsTask(Context context){
		comm=(Communicator) context;
	}

	@Override
	protected JSONObject doInBackground(Object... params) {
		int responseCode = -1;
		JSONObject jsonResponse = null;
		
		try {
			// criar url,associar url a conexao e conectar
			URL blogFeedUrl = new URL("http://blog.teamtreehouse.com/api/get_recent_summary/?count="+MainListActivity.nPosts);
			HttpURLConnection connection = (HttpURLConnection) blogFeedUrl.openConnection();
			connection.connect();

			// obter codigo http
			responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				String line;
				StringBuilder builder = new StringBuilder();
				
				// os dados sao guardados numa input stream em bites
				// precisamos reader para ler bits
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				
				while((line = reader.readLine()) != null) {
					 builder.append(line);
				}
								
				// criar objecto JSON
				jsonResponse = new JSONObject(builder.toString());
			} else {
				Log.e("andre", "Unsuccessful HTTP Response Code: "+ responseCode);
			}
		} catch (MalformedURLException e) {
			Log.e("andre", "MalformedURLException : " + e);
		} catch (IOException e) {
			Log.e("andre", "IOException : " + e);
		} catch (JSONException e) {
			Log.e("andre", "JSONException : " + e);
		}
		return jsonResponse;
	}
	
	@Override
	protected void onPostExecute(JSONObject result) {
		super.onPostExecute(result);
		comm.handleBlogResponse(result.toString());
	}
}
