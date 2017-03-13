package com.teamtreehouse.ribbit;

import java.util.List;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

public class FriendsFragment extends ListFragment {

	protected ParseRelation<ParseUser> mFriendsRelation;
	protected ParseUser mCurrrentUser;
	protected List<ParseUser> mFriends;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
		return rootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mCurrrentUser= ParseUser.getCurrentUser();
		mFriendsRelation= mCurrrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
		
		getActivity().setProgressBarIndeterminateVisibility(true);
		
		ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
		query.addAscendingOrder(ParseConstants.KEY_USERNAME);
		query.findInBackground(new FindCallback<ParseUser>() {
			
			@Override
			public void done(List<ParseUser> result, ParseException e) {
				getActivity().setProgressBarIndeterminateVisibility(false);
				if (e==null) {
					mFriends=result;
					//so qeremos usernames na lista
					String[] usernames= new String[mFriends.size()];
					for (int i = 0; i < usernames.length; i++) {
						usernames[i]= mFriends.get(i).getUsername();
					}
					ArrayAdapter<String> adapter= new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_1, usernames);
					setListAdapter(adapter);
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
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
}
