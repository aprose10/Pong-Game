package com.alexrose.framework.implementation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Point;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.alexrose.framework.Audio;
import com.alexrose.framework.FileIO;
import com.alexrose.framework.Game;
import com.alexrose.framework.Graphics;
import com.alexrose.framework.Input;
import com.alexrose.framework.Screen;
import com.alexrose.pong.inAppBilling.IabHelper;
import com.alexrose.pong.inAppBilling.IabResult;
import com.alexrose.pong.inAppBilling.Inventory;
import com.alexrose.pong.inAppBilling.Purchase;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;


public abstract class AndroidGame extends Activity implements Game {
	AndroidFastRenderView renderView;
	Graphics graphics;
	Audio audio;
	Input input;
	FileIO fileIO;
	Screen screen;
	WakeLock wakeLock;
	public static boolean loggedIn = false;
	public IabHelper mHelper;
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = 
			new Session.StatusCallback() {
		@Override
		public void call(Session session, 
				SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	public boolean mIsPaddleColor = false;
	public static AndroidGame androidGame;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		androidGame = this;

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApbjgh2Qulj36uFgWeOoph6e5iAo6HcZ7LmKOA8uZinb9pQ4oSPnGXeWM2q25kd0W+ujB5u2x9FfU7VGdL5JjXzWiI5VVPa9RLagfpQOIE4eUs3fM33+HE78tX1GbFmRKFIHUSAjyBECFsA/gUSmJiKyyaoB7zZP0X26p3UgwCul7391oz1ynj2+HesuhHHkKawXhG/dqIdZCc8xAhKNcHJcDGzlyfPD0Y+bBk2wsK+BZzHN6OtcbMq6jJswIBtXWBrKZp4nQULUDsOl1yXQERT1qBBx/swgfBfrRSFb+iIiHsK8J4niis6kkO8EGzQTZ3lw3NG3giOICzv2tCgE5JQIDAQAB";


		Log.d("YOLO", "Creating IAB helper.");
		mHelper = new IabHelper(this, base64EncodedPublicKey);

		Log.d("YOLO", "Starting setup.");
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				Log.d("YOLO", "Setup finished.");

				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					complain("Problem setting up in-app billing: " + result);
					return;
				}

				// Hooray, IAB is fully seFt up. Now, let's get an inventory of stuff we own.
				Log.d("YOLO", "Setup successful. Querying inventory.");
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});



		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
		int frameBufferWidth = isPortrait ? 480: 800;
		int frameBufferHeight = isPortrait ? 800: 480;
		Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth,
				frameBufferHeight, Config.RGB_565);

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		float scaleX = (float) frameBufferWidth
				/ size.x;
		float scaleY = (float) frameBufferHeight
				/ size.y;

		renderView = new AndroidFastRenderView(this, frameBuffer);
		graphics = new AndroidGraphics(getAssets(), frameBuffer);
		fileIO = new AndroidFileIO(this);
		audio = new AndroidAudio(this);
		input = new AndroidInput(this, renderView, scaleX, scaleY);
		screen = getInitScreen();
		setContentView(renderView);

		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MyGame");
		//WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

		Session session = Session.getActiveSession();
		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
			}
			if (session == null) {
				session = new Session(this);
			}
			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
			}
		}
	}

	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
			Log.d("YOLO", "Query inventory finished.");
			if (result.isFailure()) {
				complain("Failed to query inventory: " + result);
				return;
			}

			Log.d("YOLO", "Query inventory was successful.");

			/*
			 * Check for items we own. Notice that for each purchase, we check
			 * the developer payload to see if it's correct! See
			 * verifyDeveloperPayload().
			 */

			// Do we have the premium upgrade?
			Purchase paddleColorPurchase = inventory.getPurchase("paddlecolor");
			mIsPaddleColor = (paddleColorPurchase != null && verifyDeveloperPayload(paddleColorPurchase));
			Log.d("YOLO", "User is " + (mIsPaddleColor ? "PADDLE COLOR" : "NO PADDLE COLOR"));
			//String paddleColorPrice = inventory.getSkuDetails("paddlecolor").getPrice();

			//This is where we add logic that user has already purchased the color upgrade because they already own it



			//updateUi();
			//setWaitScreen(false);
			Log.d("YOLO", "Initial inventory query finished; enabling main UI.");
		}
	};

	public IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			Log.d("YOLO", "Purchase finished: " + result + ", purchase: " + purchase);
			if (result.isFailure()) {
				complain("Error purchasing: " + result);
				//setWaitScreen(false);
				return;
			}
			if (!verifyDeveloperPayload(purchase)) {
				complain("Error purchasing. Authenticity verification failed.");
				//setWaitScreen(false);
				return;
			}
			Log.d("YOLO", "Purchase successful.");

			if (purchase.getSku().equals("paddlecolor")) {
				// bought 1/4 tank of gas. So consume it.
				Log.d("YOLO", "Purchase is paddle color");
				alert("Thank you for purchasing a new paddle color!");
				mIsPaddleColor = true;
				//updateUi();
				//setWaitScreen(false);

			}
		}

	};

	boolean verifyDeveloperPayload(Purchase p) {
		String payload = p.getDeveloperPayload();

		/*
		 * TODO: verify that the developer payload of the purchase is correct. It will be
		 * the same one that you sent when initiating the purchase.
		 * 
		 * WARNING: Locally generating a random string when starting a purchase and 
		 * verifying it here might seem like a good approach, but this will fail in the 
		 * case where the user purchases an item on one device and then uses your app on 
		 * a different device, because on the other device you will not have access to the
		 * random string you originally generated.
		 *
		 * So a good developer payload has these characteristics:
		 * 
		 * 1. If two different users purchase an item, the payload is different between them,
		 *    so that one user's purchase can't be replayed to another user.
		 * 
		 * 2. The payload must be such that you can verify it even when the app wasn't the
		 *    one who initiated the purchase flow (so that items purchased by the user on 
		 *    one device work on other devices owned by the user).
		 * 
		 * Using your own server to store and verify developer payloads across app
		 * installations is recommended.
		 */

		return true;
	}

	void complain(String message) {
		Log.e("YOLO", "**** TrivialDrive Error: " + message);
		alert("Error: " + message);
	}

	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", null);
		Log.d("YOLO", "Showing alert dialog: " + message);
		bld.create().show();
	}

	@Override
	public void onResume() {
		super.onResume();
		wakeLock.acquire();
		screen.resume();
		renderView.resume();
		uiHelper.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		wakeLock.release();
		renderView.pause();
		screen.pause();
		uiHelper.onPause();

		if (isFinishing())
			screen.dispose();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public Input getInput() {
		return input;
	}

	@Override
	public FileIO getFileIO() {
		return fileIO;
	}

	@Override
	public Graphics getGraphics() {
		return graphics;
	}

	@Override
	public Audio getAudio() {
		return audio;
	}

	@Override
	public void setScreen(Screen screen) {
		if (screen == null)
			throw new IllegalArgumentException("Screen must not be null");

		this.screen.pause();
		this.screen.dispose();
		screen.resume();
		screen.update(0);
		this.screen = screen;
	}

	public Screen getCurrentScreen() {

		return screen;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mHelper != null) mHelper.dispose();
		mHelper = null;
		uiHelper.onDestroy();
	}

	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state, Exception exception) {

		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	public void onClickLogin() {
		Session session = Session.getActiveSession();
		if (!session.isOpened() && !session.isClosed()) {
			session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
		} else {
			Session.openActiveSession(this, true, statusCallback);
		}
	}

	public void onClickLogout() {
		Session session = Session.getActiveSession();
		if (!session.isClosed()) {
			session.closeAndClearTokenInformation();
		}
	}
	
	

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (state.isOpened()) {
			// If the session state is open:
			// Show the authenticated fragment
			loggedIn = true;
		} else if (state.isClosed()) {
			// If the session state is closed:
			loggedIn = false;
		}
	}
	
	public static boolean loggedIn(){
		return loggedIn;
	}
}
