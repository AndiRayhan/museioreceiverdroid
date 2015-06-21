package com.interaxon.muse.museioreceiver;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.interaxon.muse.museioreceiver.MuseIOReceiver.MuseConfig;
import com.interaxon.muse.museioreceiver.MuseIOReceiver.MuseDataListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This activity is meant to display the alpha, beta, theta and delta values
 * from only one headband.
 */
public class BrainwaveValuesActivity extends Activity implements
		MuseDataListener {


	private MuseIOReceiver museReceiver;

	float[] currAlpha = new float[4];
	float[] currBeta = new float[4];
	float[] currDelta = new float[4];
	float[] currTheta = new float[4];

	int alphaCount = 0;
	int betaCount = 0;
	int deltaCount = 0;
	int thetaCount = 0;

	float[] ratioSum = new float[4];
	float[] thetaAlphaSum = new float[4];

	public boolean isReady = false;
	//final Button startButton;

	private TextView ratio1;
	private TextView ratio2;
	private TextView ratio3;
	private TextView ratio4;

	private TextView TA_1;
	private TextView TA_2;
	private TextView TA_3;
	private TextView TA_4;

	private TextView result;

	private Button startButton;

	public void setZero(float[] array){
		for(int i =0;i<array.length;i++){
			array[i]= (float)0.0;
		}
	}

	public void onClickStartButton(View view){
		isReady = true;
		ratio1.setVisibility(View.VISIBLE);
		ratio2.setVisibility(View.VISIBLE);
		ratio3.setVisibility(View.VISIBLE);
		ratio4.setVisibility(View.VISIBLE);

		TA_4.setVisibility(View.VISIBLE);
		TA_3.setVisibility(View.VISIBLE);
		TA_2.setVisibility(View.VISIBLE);
		TA_1.setVisibility(View.VISIBLE);

		startButton.setVisibility(View.INVISIBLE);

		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		for(int i = 1;i<31;i++) {
			final int j = i;
			scheduler.schedule
					(new Runnable() {
						public void run() {
							// call service
							timerMethod(j);
						}
					}, 2 * i, TimeUnit.SECONDS);
		}
	}

	public void timerMethod(final int j){
		if(j==30){
			BrainwaveValuesActivity.this.runOnUiThread(valueUpdater);
			BrainwaveValuesActivity.this.isReady = false;
			BrainwaveValuesActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					result.setVisibility(View.VISIBLE);

					String r1 = String.format("%.2f", ratioSum[0]/(float)30);
					String r2 = String.format("%.2f", ratioSum[1]/(float)30);
					String r3 = String.format("%.2f", ratioSum[2]/(float)30);
					String r4 = String.format("%.2f", ratioSum[3]/(float)30);

					String[] myarr = {r1,r2,r3,r4};
					Log.i("totalArray", Arrays.toString(myarr));

					ratio1.setText(
							String.format("%.2f", (ratioSum[0]/((float)30))));
					ratio2.setText(
							String.format("%.2f", (ratioSum[1]/((float)30))));
					ratio3.setText(
							String.format("%.2f", (ratioSum[2]/((float)30))));
					ratio4.setText(
							String.format("%.2f", (ratioSum[3]/((float)30))));

					TA_1.setText(
							String.format("%.2f", (thetaAlphaSum[0]/((float)30))));
					TA_2.setText(
							String.format("%.2f", (thetaAlphaSum[1]/((float)30))));
					TA_3.setText(
							String.format("%.2f", (thetaAlphaSum[2]/((float)30))));
					TA_4.setText(
							String.format("%.2f", (thetaAlphaSum[3]/((float)30))));

					for(int i =0;i<4;i++){
						Log.i("total array thetaBeta"+i, ""+(ratioSum[i]/((float)30)));
						Log.i("total array thetaAlpha"+i, ""+(thetaAlphaSum[i]/((float)30)));
					}

					return;

				}

			});
		}
		BrainwaveValuesActivity.this.runOnUiThread(valueUpdater);
	}

	Runnable valueUpdater = new Runnable(){
		public void run(){
			isReady = false;

			getAvgArray(currAlpha, alphaCount);
			getAvgArray(currTheta, thetaCount);
			getAvgArray(currBeta, betaCount);
			getAvgArray(currDelta, deltaCount);

			float r1 = (float)currTheta[0]/currBeta[0];
			float r2 = (float)currTheta[1]/currBeta[1];
			float r3 = (float)currTheta[2]/currBeta[2];
			float r4 = (float)currTheta[3]/currBeta[3];

			float ta1 = (float)currTheta[0]/currAlpha[0];
			float ta2 = (float)currTheta[1]/currAlpha[1];
			float ta3 = (float)currTheta[2]/currAlpha[2];
			float ta4 = (float)currTheta[3]/currAlpha[3];

			if(Float.isNaN(r1)) r1 = (float) 0.0;
			if(Float.isNaN(r2)) r2 = (float) 0.0;
			if(Float.isNaN(r3)) r3 = (float) 0.0;
			if(Float.isNaN(r4)) r4 = (float) 0.0;

			if(Float.isNaN(ta1)) ta1 = (float) 0.0;
			if(Float.isNaN(ta2)) ta2 = (float) 0.0;
			if(Float.isNaN(ta3)) ta3 = (float) 0.0;
			if(Float.isNaN(ta4)) ta4 = (float) 0.0;

			ratioSum[0] += r1;
			ratioSum[1] += r2;
			ratioSum[2] += r3;
			ratioSum[3] += r4;

			thetaAlphaSum[0] += ta1;
			thetaAlphaSum[1] += ta2;
			thetaAlphaSum[2] += ta3;
			thetaAlphaSum[3] += ta4;

			Log.i("total array", Arrays.toString(ratioSum));
			Log.i("total array", "theta Alpha: "+Arrays.toString(thetaAlphaSum));

			ratio1.setText(String.format("%.2f", r1));
			ratio2.setText(String.format("%.2f", r2));
			ratio3.setText(String.format("%.2f", r3));
			ratio4.setText(String.format("%.2f", r4));

			TA_1.setText(String.format("%.2f", ta1));
			TA_2.setText(String.format("%.2f", ta2));
			TA_3.setText(String.format("%.2f", ta3));
			TA_4.setText(String.format("%.2f", ta4));

			setZero(currAlpha);
			setZero(currBeta);
			setZero(currDelta);
			setZero(currTheta);

			alphaCount = 0;
			betaCount = 0;
			thetaCount = 0;
			deltaCount = 0;

			isReady = true;

		}
	};

	public void addArray(float[] arr1, float[] arr2){
		for(int i=0;i<arr1.length && i<arr2.length;i++)
			arr1[i]+=arr2[i];
	}

	public float sumArray(float[] arr){
		float toReturn = 0;
		for(float f : arr){
			toReturn+=f;
		}
		return toReturn;
	}

	public void getAvgArray(float[] arr, int count){
		for(int i = 0 ; i < arr.length ; i++){
			arr[i]/=count;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.main);
		ratio1 = ((TextView) BrainwaveValuesActivity.this.findViewById(R.id.ratioch1));
		ratio2 = ((TextView) BrainwaveValuesActivity.this.findViewById(R.id.ratioch2));
		ratio3 = ((TextView) BrainwaveValuesActivity.this.findViewById(R.id.ratioch3));
		ratio4 = ((TextView) BrainwaveValuesActivity.this.findViewById(R.id.ratioch4));

		TA_1 = ((TextView) BrainwaveValuesActivity.this.findViewById(R.id.TACH1));
		TA_2 = ((TextView) BrainwaveValuesActivity.this.findViewById(R.id.TACH2));
		TA_3 = ((TextView) BrainwaveValuesActivity.this.findViewById(R.id.TACH3));
		TA_4 = ((TextView) BrainwaveValuesActivity.this.findViewById(R.id.TACH4));

		result = ((TextView) BrainwaveValuesActivity.this.findViewById(R.id.result));

		startButton = (Button) findViewById(R.id.button);

		this.museReceiver = new MuseIOReceiver(5005, true);
		this.museReceiver.registerMuseDataListener(this);

		isReady = false;

		ratio1.setVisibility(View.INVISIBLE);
		ratio2.setVisibility(View.INVISIBLE);
		ratio3.setVisibility(View.INVISIBLE);
		ratio4.setVisibility(View.INVISIBLE);

		TA_4.setVisibility(View.INVISIBLE);
		TA_3.setVisibility(View.INVISIBLE);
		TA_2.setVisibility(View.INVISIBLE);
		TA_1.setVisibility(View.INVISIBLE);

		startButton.setVisibility(View.VISIBLE);
		result.setVisibility(View.INVISIBLE);

	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			this.museReceiver.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		this.museReceiver.disconnect();
	}

	@Override
	public void receiveMuseElementsAlpha(MuseConfig config, final float[] alpha) {
		if(!isReady) {
			return;
		}
		/*this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				((TextView) BrainwaveValuesActivity.this
						.findViewById(R.id.ratioch1)).setText(String.format(
						"%.2f", alpha[0]));
				((TextView) BrainwaveValuesActivity.this
						.findViewById(R.id.ratioch2)).setText(String.format(
						"%.2f", alpha[1]));
				((TextView) BrainwaveValuesActivity.this
						.findViewById(R.id.ratioch3)).setText(String.format(
						"%.2f", alpha[2]));
				((TextView) BrainwaveValuesActivity.this
						.findViewById(R.id.ratioch4)).setText(String.format(
						"%.2f", alpha[3]));
				Log.i("alpha array", Arrays.toString(alpha));
			}
		});*/
		float [] temp = Arrays.copyOf(alpha,4);
		addArray(currAlpha, temp);
		alphaCount++;
		Log.i("waves", "alpha" + isReady);
	}

	@Override
	public void receiveMuseElementsBeta(MuseConfig config, final float[] beta) {
		if(!isReady) {
			return;
		}
		Log.i("waves","beta"+isReady);
		/*this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				((TextView) BrainwaveValuesActivity.this
						.findViewById(R.id.beta_ch1)).setText(String.format(
						"%.2f", beta[0]));
				((TextView) BrainwaveValuesActivity.this
						.findViewById(R.id.beta_ch2)).setText(String.format(
						"%.2f", beta[1]));
				((TextView) BrainwaveValuesActivity.this
						.findViewById(R.id.beta_ch3)).setText(String.format(
						"%.2f", beta[2]));
				((TextView) BrainwaveValuesActivity.this
						.findViewById(R.id.beta_ch4)).setText(String.format(
						"%.2f", beta[3]));
			}
		});*/
		float [] temp = Arrays.copyOf(beta,4);
		addArray(currBeta,temp);
		betaCount++;
		Log.i("waves", "beta");
	}

	@Override
	public void receiveMuseElementsTheta(MuseConfig config, final float[] theta) {
		if(!isReady) {
			return;
		}
		/*this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				((TextView) BrainwaveValuesActivity.this
						.findViewById(R.id.theta_ch1)).setText(String.format(
						"%.2f", theta[0]));
				((TextView) BrainwaveValuesActivity.this
						.findViewById(R.id.theta_ch2)).setText(String.format(
						"%.2f", theta[1]));
				((TextView) BrainwaveValuesActivity.this
						.findViewById(R.id.theta_ch3)).setText(String.format(
						"%.2f", theta[2]));
				((TextView) BrainwaveValuesActivity.this
						.findViewById(R.id.theta_ch4)).setText(String.format(
						"%.2f", theta[3]));
			}
		});
		currTheta = Arrays.copyOf(theta,4);
		this.runOnUiThread(new Runnable(){
			@Override
			public void run(){
				float ratio1,ratio2,ratio3,ratio4;

				ratio1 = currTheta[0]/currBeta[0];
				ratio2 = currTheta[1]/currBeta[1];
				ratio3 = currTheta[2]/currBeta[2];
				ratio4 = currTheta[3]/currBeta[3];
				if(Float.isNaN(ratio1))ratio1 = (float) 0.0;
				if(Float.isNaN(ratio2))ratio2 = (float) 0.0;
				if(Float.isNaN(ratio3))ratio3 = (float) 0.0;
				if(Float.isNaN(ratio4))ratio4 = (float) 0.0;
				((TextView)BrainwaveValuesActivity.this
						.findViewById(R.id.ratioch1)).setText(String.format("%.2f",ratio1));
				((TextView)BrainwaveValuesActivity.this
						.findViewById(R.id.ratioch2)).setText(String.format("%.2f",ratio2));
				((TextView)BrainwaveValuesActivity.this
						.findViewById(R.id.ratioch3)).setText(String.format("%.2f",ratio3));
				((TextView)BrainwaveValuesActivity.this
						.findViewById(R.id.ratioch4)).setText(String.format("%.2f",ratio4));
			}
		});*/
		float [] temp = Arrays.copyOf(theta,4);
		addArray(currTheta, temp);
		thetaCount++;
		Log.i("waves", "theta");
	}

	@Override
	public void receiveMuseElementsDelta(MuseConfig config, final float[] delta) {
		if(!isReady) {
			Log.i("thewaves",""+isReady);
			return;
		}
		/*this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				((TextView) BrainwaveValuesActivity.this
						.findViewById(R.id.delta_ch1)).setText(String.format(
						"%.2f", delta[0]));
				((TextView) BrainwaveValuesActivity.this
						.findViewById(R.id.delta_ch2)).setText(String.format(
						"%.2f", delta[1]));
				((TextView) BrainwaveValuesActivity.this
						.findViewById(R.id.delta_ch3)).setText(String.format(
						"%.2f", delta[2]));
				((TextView) BrainwaveValuesActivity.this
						.findViewById(R.id.delta_ch4)).setText(String.format(
						"%.2f", delta[3]));
			}
		});*/
		float[] temp = Arrays.copyOf(delta,4);
		addArray(currDelta, temp);
		deltaCount++;
		Log.i("waves", "delta");
	}

	@Override
	public void receiveMuseEeg(MuseConfig config, float[] eeg) {
		// Do nothing
	}

	@Override
	public void receiveMuseAccel(MuseConfig config, float[] accel) {
		// Do nothing		
	}

	@Override
	public void receiveMuseBattery(MuseConfig config, int[] battery) {
		// Do nothing
	}

}
