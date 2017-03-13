package com.teamtreehouse.ribbit;

import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class EditFriendsActivity extends ListActivity {
	
	protected List<ParseUser> mUsers;
	protected ParseRelation<ParseUser> mFriendsRelation;
	protected ParseUser mCurrrentUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_edit_friends);
		// Show the Up button in the action bar.
		setupActionBar();
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);//para podermos selecionar
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		mCurrrentUser= ParseUser.getCurrentUser();
		//se relacao n exitir criase uma senao obtem-se
		mFriendsRelation= mCurrrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
		
		setProgressBarIndeterminate(true);
		
		ParseQuery<ParseUser> query = ParseUser.getQuery();//lista users
		query.orderByAscending(ParseConstants.KEY_USERNAME);
		query.setLimit(1000);
		query.findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> users, ParseException e) {
				setProgressBarIndeterminate(false);
				
				if (e==null) {//sucess
					mUsers=users;
					String[] usernames= new String[users.size()];//so qeremos usernames na lista
					for (int i = 0; i < usernames.length; i++) {
						usernames[i]= mUsers.get(i).getUsername();
					}
					ArrayAdapter<String> adapter= new ArrayAdapter<String>(EditFriendsActivity.this, android.R.layout.simple_list_item_checked, usernames);
					setListAdapter(adapter);
					
					addFriendCheckmarks();
				} else {
					Log.d("andre", e.getMessage());
					AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
					builder.setMessage(e.getMessage());
					builder.setTitle(R.string.error_title);
					builder.setPositiveButton(android.R.string.ok, null);
					
					//associamos builder e mostramos
					AlertDialog dialog= builder.create();
					dialog.show();
				}
			}

		});
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (getListView().isItemChecked(position)) {
			//add friend
			mFriendsRelation.add(mUsers.get(position));
		} else {
			//remove friends
			mFriendsRelation.remove(mUsers.get(position));
		}
		mCurrrentUser.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				if (e!=null) {//erro
					Log.d("andre", e.getMessage());
				}
			}
		});
	}
	
	private void addFriendCheckmarks() {
		mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
			
			@Override
			public void done(List<ParseUser> friends, ParseException e) {
				if (e==null) {
					//list returned - look for a match
					for (int i = 0; i < mUsers.size(); i++) {
						ParseUser user=mUsers.get(i);
						
						for (ParseUser friend : friends) {
							if (friend.getObjectId().equals(user.getObjectId())) {
								getListView().setItemChecked(i, true);
							}
						}
					}
				} else {
					Log.d("andre", e.getMessage());
				}
			}
		});
	}
}
