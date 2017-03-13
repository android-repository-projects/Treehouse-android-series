package com.teamtreehouse.ribbit;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class RecipientsActivity extends ListActivity {

	protected ParseRelation<ParseUser> mFriendsRelation;
	protected ParseUser mCurrrentUser;
	protected List<ParseUser> mFriends;
	
	protected Uri mMediaUri;
	protected String mFileType;
	
	protected MenuItem mSendMenuItem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_recipients);
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);//para podermos selecionar
		
		mMediaUri= getIntent().getData();//obter local armanezamento file
		mFileType= getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);
	}

	@Override
	public void onResume() {
		super.onResume();
		mCurrrentUser = ParseUser.getCurrentUser();
		mFriendsRelation = mCurrrentUser
				.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

		setProgressBarIndeterminateVisibility(true);

		ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
		query.addAscendingOrder(ParseConstants.KEY_USERNAME);
		query.findInBackground(new FindCallback<ParseUser>() {

			@Override
			public void done(List<ParseUser> result, ParseException e) {
				setProgressBarIndeterminateVisibility(false);
				if (e == null) {
					mFriends = result;
					// so qeremos usernames na lista
					String[] usernames = new String[mFriends.size()];
					for (int i = 0; i < usernames.length; i++) {
						usernames[i] = mFriends.get(i).getUsername();
					}
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							getListView().getContext(),
							android.R.layout.simple_list_item_checked, usernames);
					setListAdapter(adapter);
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
					builder.setMessage(e.getMessage());
					builder.setTitle(R.string.error_title);
					builder.setPositiveButton(android.R.string.ok, null);

					// associamos builder e mostramos
					AlertDialog dialog = builder.create();
					dialog.show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.recipients, menu);
		mSendMenuItem= menu.getItem(0);//como n temos nenhum objecto metemos 0
		return true;
	}
	
	/*
	 * quando clicamos objecto lista pessoas
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (l.getCheckedItemCount()>0) {
			mSendMenuItem.setVisible(true);
		} else {
			mSendMenuItem.setVisible(false);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			break;
		case R.id.action_send:
			ParseObject message= createMessage();//esta variavel vaiser null se algo correr mal
			if (message==null) {//error
				AlertDialog.Builder builder= new AlertDialog.Builder(this);
				builder.setMessage(R.string.error_selecting_file);
				builder.setTitle(R.string.error_selecting_file_title);
				builder.setPositiveButton(android.R.string.ok, null);
				AlertDialog dialog= builder.create();
				dialog.show();
			} else {
				send(message);
				finish();//voltar a actividade anterior
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void send(ParseObject message) {//saving to the backend
		message.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e==null) {//success
					Toast.makeText(RecipientsActivity.this, R.string.success_message, Toast.LENGTH_SHORT).show();
				} else {
					AlertDialog.Builder builder= new AlertDialog.Builder(RecipientsActivity.this);
					builder.setMessage(R.string.error_sending_message);
					builder.setTitle(R.string.error_selecting_file_title);
					builder.setPositiveButton(android.R.string.ok, null);
					AlertDialog dialog= builder.create();
					dialog.show();
				}
			}
		});
	}

	protected ParseObject createMessage() {
		//criamos novo objecto
		ParseObject message= new ParseObject(ParseConstants.CLASS_MESSAGES);
		message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
		message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());//meter remetente
		message.put(ParseConstants.KEY_RECIPIENT_IDS, getFriendsToSend());//selected friends
		message.put(ParseConstants.KEY_FILE_TYPE, mFileType);//meter tipo mensagem
		
		byte[] fileBytes= FileHelper.getByteArrayFromFile(this, mMediaUri);
		if (fileBytes==null) {//ver se esta tudo bem
			return null;
		} else {
			if (mFileType.equals(ParseConstants.TYPE_IMAGE)) {
				fileBytes= FileHelper.reduceImageForUpload(fileBytes);//rezise image pq tamanho
			}
			String fileName= FileHelper.getFileName(this, mMediaUri, mFileType);//criar nome file
			ParseFile file= new ParseFile(fileName, fileBytes);//criar parse file paramandarmos server
			message.put(ParseConstants.KEY_FILE, file);//attach
		}
		
		return message;
	}

	private ArrayList<String> getFriendsToSend() {
		ArrayList<String> getFriendsToSend= new ArrayList<String>();
		for (int i = 0; i < getListView().getCount(); i++) {//numero amigos na lista
			if (getListView().isItemChecked(i)) {
				getFriendsToSend.add(mFriends.get(i).getObjectId());
			}
		}
		return getFriendsToSend;
	}
}
