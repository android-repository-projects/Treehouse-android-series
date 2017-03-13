package com.teamtreehouse.ribbit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends Activity {
	
	protected EditText mUserName, mPassword;
	protected Button mLoginUpButton;
	protected TextView mSignUpTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//action bar com progresso
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_login);
		
		mSignUpTextView=(TextView) findViewById(R.id.signUpText);
		mSignUpTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent= new Intent(LoginActivity.this,SignUpActivity.class);
				startActivity(intent);
			}
		});
		mUserName= (EditText) findViewById(R.id.usernameField);
		mPassword= (EditText) findViewById(R.id.passwordField);
		mLoginUpButton= (Button) findViewById(R.id.loginButton);
		mLoginUpButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String username= mUserName.getText().toString();
				String password= mPassword.getText().toString();
				
				//sem espaços
				username=username.trim();
				password=password.trim();
				//qeremos informar que algo esta mal
				if (username.isEmpty() || password.isEmpty()) {
					//criamos builder
					AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
					builder.setMessage(R.string.login_error_message);
					builder.setTitle(R.string.login_error_title);
					builder.setPositiveButton(android.R.string.ok, null);
					
					//associamos builder e mostramos
					AlertDialog dialog= builder.create();
					dialog.show();
				} else {
					//login user
					setProgressBarIndeterminateVisibility(true);
					ParseUser.logInInBackground(username, password, new LogInCallback() {
						
						@Override
						public void done(ParseUser user, ParseException e) {
							setProgressBarIndeterminateVisibility(true);
							if (e==null) {//sucess
								Intent intent= new Intent(LoginActivity.this, MainActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(intent);
								
							} else {
								//criamos builder
								AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
								builder.setMessage(R.string.login_error_message);
								builder.setTitle(R.string.login_error_title);
								builder.setPositiveButton(android.R.string.ok, null);
								
								//associamos builder e mostramos
								AlertDialog dialog= builder.create();
								dialog.show();
							}
						}
					});
				}
			}
		});
	}
}
