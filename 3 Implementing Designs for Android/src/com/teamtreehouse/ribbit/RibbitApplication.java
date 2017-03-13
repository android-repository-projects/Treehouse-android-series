package com.teamtreehouse.ribbit;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;
import com.teamtreehouse.ribbit.ui.MainActivity;
import com.teamtreehouse.ribbit.utils.ParseConstants;

public class RibbitApplication extends Application {
	
	@Override
	public void onCreate() { 
		super.onCreate();
	    Parse.initialize(this, 
	    	"249hIyXNAN8gunH1h2PmUohbhdpb5CYoAqmuVAh8", "vW2VgyKfaaiphT6FTCmYcoiJbTytQNeMK1C1F084");
	    
	    //PushService.setDefaultPushCallback(this, MainActivity.class);
	    PushService.setDefaultPushCallback(this, MainActivity.class, R.drawable.ic_stat_ic_launcher);
	}
	
	public static void updateParseInstalation(ParseUser user){
		ParseInstallation instalation= ParseInstallation.getCurrentInstallation();
		instalation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
		instalation.saveInBackground();
	}
}
