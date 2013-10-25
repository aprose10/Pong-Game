package com.alexrose.pong;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.alexrose.framework.Screen;
import com.alexrose.framework.implementation.AndroidGame;

public class PongGame extends AndroidGame {
	public static String map;
	public static PongGame activity;
	boolean firstTimeCreate = true;

	@Override
	public Screen getInitScreen() {

		if (firstTimeCreate) {
			Assets.load(this);
			firstTimeCreate = false;
			activity = this;
		}
		
		return new SplashLoadingScreen(this);

	}
	
	public static boolean hasPaddleColorUpgrade(){
		return activity.mIsPaddleColor;
	}

	@Override
	public void onBackPressed() {
		getCurrentScreen().backButton();
	}

	@Override
	public void onResume() {
		super.onResume();

		Assets.theme.play();

	}

	@Override
	public void onPause() {
		super.onPause();
		Assets.theme.pause();

	}
	
	public static void saveGame(SaveGameState gameState){
		SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putBoolean("savedGame", true);
		editor.putBoolean("HitHuman", gameState.HitHuman);
		editor.putInt("locationX", gameState.ballLocationX);
		editor.putInt("locationY", gameState.ballLocationY);
		editor.putFloat("VelocityX", (float) gameState.velocityX);
		editor.putFloat("VelocityY", (float) gameState.velocityY);
		editor.putInt("humanScore", gameState.humanScore);
		editor.putInt("AiScore", gameState.AiScore);
		editor.putInt("humanPaddleX", gameState.humanPaddleX);
		editor.putInt("AiPaddleX", gameState.AiPaddleX);
		editor.commit();
	}
	
	public static SaveGameState loadGame(){
		SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
		//int highScore = sharedPref.getInt("hello", 6);
		int ballLocationX = (int) sharedPref.getInt("locationX", 1);
		int ballLocationY = (int) sharedPref.getInt("locationY", 1);
		double ballVelocityX = (double)sharedPref.getFloat("VelocityX", 1);
		double ballVelocityY = (double) sharedPref.getFloat("VelocityY", 1);
		int humanScore = (int)sharedPref.getInt("humanScore", -1);
		int AiScore = (int) sharedPref.getInt("AiScore", -1);
		int humanPaddleX = (int) sharedPref.getInt("humanPaddleX", -1);
		int AiPaddleX = (int) sharedPref.getInt("AiPaddleX", -1);
		boolean saveGame = sharedPref.getBoolean("savedGame", false);
		boolean HitHuman = sharedPref.getBoolean("HitHuman", false);
		
		if(saveGame == true){
		SaveGameState newGameState = new SaveGameState(ballLocationX, ballLocationY, ballVelocityX, ballVelocityY, 
				humanScore, AiScore, humanPaddleX, AiPaddleX, HitHuman);
		SharedPreferences sharedPref1 = activity.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref1.edit();
		editor.putBoolean("savedGame", false);
		editor.commit();
		return newGameState;
		}
		else{
			return null;
		}
		
	}
}
