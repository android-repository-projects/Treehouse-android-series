package com.andremlsantos.crystal.ball;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andremlsantos.crystal.ball.ShakeDetector.OnShakeListener;

public class MainActivity extends Activity {

	private CrystalBall mCrystalBall = new CrystalBall();
	private TextView mAnswerLabel;
	private Button mAnwserButton;
	private ImageView mCrystalBallImage;
	
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private ShakeDetector mShakeDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Declare our View Variables
		mAnswerLabel = (TextView) findViewById(R.id.textView1);
		mAnwserButton = (Button) findViewById(R.id.button1);
		mCrystalBallImage = (ImageView) findViewById(R.id.imageView1);
		mAnwserButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {handleNewAnswer();}});
		
		//this gives us the system service that manages sensors
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		//retorna aceleramotro
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mShakeDetector = new ShakeDetector(new OnShakeListener() {
			
			@Override
			public void onShake() {
				handleNewAnswer();
			}
		});
		
//		Toast welcome= Toast.makeText(this, "Yay! our activity was created!", Toast.LENGTH_SHORT);
//		welcome.setGravity(Gravity.TOP, 0, 0);
//		welcome.show();
		
		Log.d("andre", "We're logging from the onCreate() method!");
	}
	
	/*
	 * como acelerometro pertence android system services ele corre sempre em background
	 * para o usarmos nao basta metermos lystener temos que registalo na nossa APP
	 * 1 listener
	 * 2 sensor que qeremos ouvir
	 * 3 intervalo tempo
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//desintalar serviços para nao gastar bateria
		mSensorManager.unregisterListener(mShakeDetector);
	}
	
	//fazer som
	private void playSound() {
		MediaPlayer player =new MediaPlayer().create(this, R.raw.crystal_ball);
		player.start();
		//metemos um listener que corre quandomusica para
		//recomendado pela google
		player.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
			}
		});
	}
	
	// fazer animacao texto com twen animation
	private void animateAnswer() {
		// start, stop
		AlphaAnimation fadeInAnimation = new AlphaAnimation(0, 1);
		fadeInAnimation.setDuration(1500);
		// mudanças ficam depois de animacao terminar
		fadeInAnimation.setFillAfter(true);
		mAnswerLabel.setAnimation(fadeInAnimation);
	}

	// fazer animacao bola
	private void animateCrystalBall() {
		mCrystalBallImage.setImageResource(R.drawable.ball_animation);
		AnimationDrawable ballAnimation = (AnimationDrawable) mCrystalBallImage
				.getDrawable();
		if (ballAnimation.isRunning()) {
			ballAnimation.stop();
		}
		ballAnimation.start();
	}

	private void handleNewAnswer() {
		String answer = mCrystalBall.getAnAnswer();
		mAnswerLabel.setText(answer);
		animateCrystalBall();
		animateAnswer();
		playSound();
	}
}
