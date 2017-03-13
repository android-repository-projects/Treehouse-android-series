package com.teamtreehouse.ribbit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends Activity {
	
	protected EditText mUserName, mPassword, mEmail;
	protected Button mSignUpButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_sign_up);
		
		mUserName= (EditText) findViewById(R.id.usernameField);
		mPassword= (EditText) findViewById(R.id.passwordField);
		mEmail= (EditText) findViewById(R.id.emailField);
		mSignUpButton= (Button) findViewById(R.id.signUpButton);
		mSignUpButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String username= mUserName.getText().toString();
				String password= mPassword.getText().toString();
				String email= mEmail.getText().toString();
				
				//sem espaços
				username=username.trim();
				password=password.trim();
				email=email.trim();
				//qeremos informar que algo esta mal
				if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
					//criamos builder
					AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
					builder.setMessage(R.string.signUpErrorMessage);
					builder.setTitle(R.string.signup_error_title);
					builder.setPositiveButton(android.R.string.ok, null);
					
					//associamos builder e mostramos
					AlertDialog dialog= builder.create();
					dialog.show();
				} else {
					//create user
					setProgressBarIndeterminateVisibility(true);
					ParseUser newUser= new ParseUser();
					newUser.setUsername(username);
					newUser.setPassword(password);
					newUser.setEmail(email);
					newUser.signUpInBackground(new SignUpCallback() {//este metodo échamado assim que registo fica 100%
						
						@Override
						public void done(ParseException e) {
							setProgressBarIndeterminateVisibility(false);
							if (e==null) {//success
								//criamos loggin
								Intent intent= new Intent(SignUpActivity.this, MainActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(intent);
							} else {
								//criamos builder
								AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
								builder.setMessage(e.getMessage());
								builder.setTitle(R.string.signup_error_title);
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
