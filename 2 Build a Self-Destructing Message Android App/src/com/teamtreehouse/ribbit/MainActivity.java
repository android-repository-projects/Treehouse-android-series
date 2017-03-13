package com.teamtreehouse.ribbit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	public static final int TAKE_PHOTO_REQUEST =0;
	public static final int TAKE_VIDEO_REQUEST =1;
	public static final int PICK_PHOTO_REQUEST =2;
	public static final int PICK_VIDEO_REQUEST =3;
	
	public static final int MEDIA_TYPE_IMAGE=4;
	public static final int MEDIA_TYPE_VIDEO=5;
	
	public static final int FILE_SIZE_LIMIT=10*1024*1024;//1MB=1024kb=1024b
	
	protected Uri mMediaUri;//path do file in android files
	
	protected DialogInterface.OnClickListener mDialogListener= new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int position) {
			switch (position) {
			case 0:
				//take picture
				Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				mMediaUri= getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
				if (mMediaUri==null) {
					//display error
					Toast.makeText(MainActivity.this, "Problema ao acessar disco", Toast.LENGTH_SHORT).show();
				} else {
					takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);//guardar foto
					startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);//resultado vai ser retornado apos tirar foto
				}
				break;
			case 1:
				//take video
				Intent videoIntent= new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
				mMediaUri= getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
				if (mMediaUri==null) {
					//display error
					Toast.makeText(MainActivity.this, "Problema ao acessar disco", Toast.LENGTH_SHORT).show();
				} else {
					videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);//guardar foto
					videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);//10s
					videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);//low quality
					startActivityForResult(videoIntent, TAKE_VIDEO_REQUEST);//resultado vai ser retornado apos tirar foto
				}
				break;
			case 2:
				//choose picture
				Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
				choosePhotoIntent.setType("image/*");//tipo file
				startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);
				break;
			case 3:
				//choose video
				Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
				chooseVideoIntent.setType("video/*");//tipo file
				Toast.makeText(MainActivity.this, "The selected video must be less than 10 MB", Toast.LENGTH_SHORT).show();
				startActivityForResult(chooseVideoIntent, PICK_VIDEO_REQUEST);
				break;
			}
		}

		private Uri getOutputMediaFileUri(int mediaType) {
			// To be safe, you should check that the SDCard is mounted
		    // using Environment.getExternalStorageState() before doing this.
			if (isExternalStorageAvailable()) {
				//get Uri
				//1 get external storage directory
				String appName=MainActivity.this.getString(R.string.app_name);
				File mediaStorageDir= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName);
				//2 crete sub directory
				if (!mediaStorageDir.exists()) {
					if(mediaStorageDir.mkdirs()){
						Log.d("andre", "Failed to create directory");
						return null;
					}
				}
				//3 create file name + date and join together
				//4 create file
				File mediaFile = null;
				Date now= new Date();
				String timestamp= new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(now);
				
				String path= mediaStorageDir.getPath()+File.separator;
				if (mediaType==MEDIA_TYPE_IMAGE) {
					mediaFile= new File(path+"IMG_"+timestamp+".jpg");
				} else if (mediaType==MEDIA_TYPE_VIDEO) {
					mediaFile= new File(path+"VID_"+timestamp+".mp4");
				}
				//5 return file
				Log.d("andre", "File "+Uri.fromFile(mediaFile));
				return Uri.fromFile(mediaFile);
			} else {
				return null;
			}
		}
		
		private boolean isExternalStorageAvailable(){
			String state= Environment.getExternalStorageState();
			if (state.equals(Environment.MEDIA_MOUNTED)) {//is available
				return true;
			} else {
				return false;
			}
		}
	};
	
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);//pq friendsFragment
		setContentView(R.layout.activity_main);
		
		ParseAnalytics.trackAppOpened(getIntent());
		ParseUser currentUser= ParseUser.getCurrentUser();//obter sessao actual
		if (currentUser==null) {
			navigateToLogin();
		} else {
			Log.i("andre", currentUser.getUsername());
		}
		
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);//modo navegacao

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
		}
	}
	
	/*
	 * REGISTAR FOTO NA GALERIA
	 * atravez listener como broadcast receiver
	 * registamos o file para velo e usalo na galeria
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode==RESULT_OK) {
			if (requestCode==PICK_PHOTO_REQUEST || requestCode==PICK_VIDEO_REQUEST) {
				if (data==null) {
					Toast.makeText(this, "ERRO", Toast.LENGTH_SHORT).show();
				} else {
					mMediaUri= data.getData(); //ober uri
				}
				//ver tamanho file
				if (requestCode==PICK_VIDEO_REQUEST) {
					//file < 10 mb
					int fileSize=0;
					//atencao é retornado um content uri
					InputStream is = null;
					try {
						is= getContentResolver().openInputStream(mMediaUri);
						fileSize=is.available();//tamanho file em bytes
					} catch (FileNotFoundException e) {
						Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
						return;//sair deste metodo
					} catch (IOException e) {
						Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
						return;//sair deste metodo
					}
					finally{
						try {
							is.close();
						} catch (IOException e) {
							Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
							return;//sair deste metodo
						}
					}
					if (fileSize>=FILE_SIZE_LIMIT) {
						Toast.makeText(MainActivity.this, "The selected file was too large! Selected a new one!", Toast.LENGTH_SHORT).show();
						return;//sair deste metodo
					}
				}
			} else {
				Intent mediaScanIntent= new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				mediaScanIntent.setData(mMediaUri);
				sendBroadcast(mediaScanIntent);
			}
			Intent recipientsIntent= new Intent(this, RecipientsActivity.class);
			recipientsIntent.setData(mMediaUri);
			
			String fileType;
			if (requestCode== PICK_PHOTO_REQUEST || requestCode==TAKE_PHOTO_REQUEST) {
				fileType= ParseConstants.TYPE_IMAGE;
			} else {
				fileType= ParseConstants.TYPE_VIDEO;
			}
			recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE, fileType);
			startActivity(recipientsIntent);
		} else if (resultCode!=RESULT_CANCELED) {
			Toast.makeText(this, "Sorry, there was an error", Toast.LENGTH_SHORT).show();
		}
	}

	private void navigateToLogin() {
		Intent intent = new Intent(this, LoginActivity.class);
		//fazemos isto para n acesarmos indevidamente com back button á main activity
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/*
	 * opçao action bar selecionada
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		case R.id.action_logout:
			ParseUser.logOut();
			navigateToLogin();
			break;
		case R.id.action_edit_friends:
			Intent intent = new Intent(this, EditFriendsActivity.class);
			startActivity(intent);
			break;
		case R.id.action_camera:
			//criar builder
			AlertDialog.Builder builder= new AlertDialog.Builder(this);
			builder.setItems(R.array.camera_choices, mDialogListener);
			//criar dialog a associar
			AlertDialog dialog= builder.create();
			dialog.show();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
}
