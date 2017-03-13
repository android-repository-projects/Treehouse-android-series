package com.teamtreehouse.ribbit;

import android.app.Application;

import com.parse.Parse;

public class RibbitApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		// set your application id and client key:
		Parse.initialize(this, "249hIyXNAN8gunH1h2PmUohbhdpb5CYoAqmuVAh8",
				"vW2VgyKfaaiphT6FTCmYcoiJbTytQNeMK1C1F084");
	}
}
