package com.alexrose.pong;

import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.widget.ImageView;

import com.alexrose.framework.Game;
import com.alexrose.framework.Graphics;
import com.alexrose.framework.Image;
import com.alexrose.framework.Screen;
import com.alexrose.framework.Input.TouchEvent;
import com.alexrose.framework.implementation.AndroidGame;

public class GameScreen extends Screen {
	enum GameState {
		Ready, Running, Paused, GameOver
	}

	GameState state = GameState.Ready;

	// Variable Setup
	// You would create game objects here.

	int livesLeft = 1;
	int humanScore = 0;
	int aiScore = 0;
	int endGameScore = 3;
	Ball ball;
	Paint paint;
	Paddle humanPaddle;
	Paddle aiPaddle;
	boolean hitHuman = true;
	Paint gameOverPaint;


	public GameScreen(Game game) {
		super(game);

		//PongGame.activity.mIsPaddleColor = true;

		// Initialize game objects here

		// Defining a paint object
		paint = new Paint();
		paint.setTextSize(30);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);

		gameOverPaint = new Paint();
		Typeface tf = Typeface.create("Helvetica",Typeface.BOLD);
		gameOverPaint.setTypeface(tf);
		gameOverPaint.setTextSize(80);
		gameOverPaint.setTextAlign(Paint.Align.CENTER);
		gameOverPaint.setAntiAlias(true);
		gameOverPaint.setColor(Color.GREEN);

		ball = new Ball();
		humanPaddle = new Paddle(true);
		aiPaddle = new Paddle(false);

		SaveGameState gameState = PongGame.loadGame();
		if(gameState != null){
			ball.setLocationX(gameState.ballLocationX);
			ball.setLocationY(gameState.ballLocationY);
			ball.setVelocityX(gameState.velocityX);
			ball.setVelocityY(gameState.velocityY);
			humanScore = gameState.humanScore;
			aiScore = gameState.AiScore;
			humanPaddle.setLocationX(gameState.humanPaddleX);
			aiPaddle.setLocationX(gameState.AiPaddleX);
			hitHuman = gameState.HitHuman;
		}
		else{

		}

	}

	@Override
	public void update(float deltaTime) {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();

		// We have four separate update methods in this example.
		// Depending on the state of the game, we call different update methods.
		// Refer to Unit 3's code. We did a similar thing without separating the
		// update methods.

		if (state == GameState.Ready)
			updateReady(touchEvents);
		if (state == GameState.Running)
			updateRunning(touchEvents, deltaTime);
		if (state == GameState.Paused)
			updatePaused(touchEvents);
		if (state == GameState.GameOver)
			updateGameOver(touchEvents);
	}

	private void updateReady(List<TouchEvent> touchEvents) {

		// This example starts with a "Ready" screen.
		// When the user touches the screen, the game begins. 
		// state now becomes GameState.Running.
		// Now the updateRunning() method will be called!

		if (touchEvents.size() > 0)
			state = GameState.Running;
	}

	private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {

		//This is identical to the update() method from our Unit 2/3 game.


		// 1. All touch input is handled here:
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);

			if (event.type == TouchEvent.TOUCH_DOWN) {

				if (event.x < 239) {
					// Move left.

					humanPaddle.moveDirection(1);
				}

				else if (event.x > 240) {
					// Move right.
					humanPaddle.moveDirection(0);
				}

			}

			if (event.type == TouchEvent.TOUCH_UP) {

				humanPaddle.moveDirection(2);
			}


		}

		// 2. Check miscellaneous events like death:

		if (livesLeft == 0) {
			state = GameState.GameOver;
		}


		// 3. Call individual update() methods here.
		// This is where all the game updates happen.
		// For example, robot.update();
		ball.move(deltaTime);
		humanPaddle.move(deltaTime);
		aiPaddle.move(deltaTime);
		ball.bounceOffWalls();
		scoring();

		Rect ballRect = ball.getRect();
		Rect humanRect = humanPaddle.getRect();
		Rect aiRect = aiPaddle.getRect();

		if(ballRect.intersect(aiRect) && hitHuman == false){
			ball.bounce();
			hitHuman = true;

		}
		if(ballRect.intersect(humanRect) && hitHuman == true){
			ball.bounce();
			hitHuman = false;
		}
		if(ball.getLocationX() <= aiPaddle.getLocationX()){
			aiPaddle.moveDirection(1);
		}

		else if(ball.getLocationX() >= aiPaddle.getLocationX() + 30){
			aiPaddle.moveDirection(0);
		}
		else{
			aiPaddle.moveDirection(2);
		}

	}

	private void updatePaused(List<TouchEvent> touchEvents) {
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				if(event.x >= 170 && event.x <= 230 && event.y >= 270 && event.y <= 310 ){
					//yes save info and kill app
					SaveGameState gs = new SaveGameState(ball.getLocationX(), ball.getLocationY(), 
							ball.getVelocityX(), ball.getVelocityY(), humanScore, aiScore, humanPaddle.getLocationX(), aiPaddle.getLocationX(), hitHuman);
					PongGame.saveGame(gs);
					android.os.Process.killProcess(android.os.Process.myPid());
				}

				else if(event.x >= 260 && event.x <= 300 && event.y >= 270 && event.y <= 310 ){
					//no
					state = GameState.Running;
				
				}
			}
		}
	}

	private void updateGameOver(List<TouchEvent> touchEvents) {
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				nullify();
				game.setScreen(new MainMenuScreen(game));
				return;
			}
		}

	}

	@Override
	public void paint(float deltaTime) {
		Graphics g = game.getGraphics();
		g.drawRect(0, 0, 480, 800, Color.GREEN);
		if(PongGame.hasPaddleColorUpgrade() == true){
			g.drawImage(Assets.newPongPaddle, humanPaddle.getLocationX(), humanPaddle.getLocationY() );
		}
		else{
			g.drawImage(Assets.paddles, humanPaddle.getLocationX(), humanPaddle.getLocationY() );
		}

		// First draw the game elements.
		g.drawImage(Assets.ball, ball.getLocationX(), ball.getLocationY());
		g.drawImage(Assets.paddles, aiPaddle.getLocationX(), aiPaddle.getLocationY() );

		// Example:
		// g.drawImage(Assets.background, 0, 0);
		// g.drawImage(Assets.character, characterX, characterY);

		// Secondly, draw the UI above the game elements.
		if (state == GameState.Ready)
			drawReadyUI();
		if (state == GameState.Running)
			drawRunningUI();
		if (state == GameState.Paused)
			drawPausedUI();
		if (state == GameState.GameOver)
			drawGameOverUI();

	}

	public void scoring(){
		if(ball.getLocationY() <= 0){
			humanScore = humanScore + 1;

			ball.reset();

			if(humanScore == endGameScore){
				state = GameState.GameOver;
			}
		}

		else if(ball.getLocationY() >= 800){
			aiScore = aiScore + 1;

			ball.reset();

			if(aiScore == endGameScore){
				state = GameState.GameOver;
			}
		}	
	}

	private void nullify() {

		// Set all variables to null. You will be recreating them in the
		// constructor.
		paint = null;

		// Call garbage collector to clean up memory.
		System.gc();
	}

	private void drawReadyUI() {
		Graphics g = game.getGraphics();

		g.drawARGB(155, 0, 0, 0);
		g.drawString("Tap to start the game.", 240, 400, paint);

	}

	private void drawRunningUI() {
		Graphics g = game.getGraphics();
		g.drawString("" + humanScore, 15, 790, paint);
		g.drawString("" + aiScore, 460, 30, paint);

	}

	private void drawPausedUI() {
		Graphics g = game.getGraphics();
		// Darken the entire screen so you can display the Paused screen.
		g.drawARGB(155, 0, 0, 0);
		g.drawString("Save and Quit Pong?", 240, 200, paint);
		drawLineRect(g, 170, 270, 60, 40);
		drawLineRect(g, 250, 270, 60, 40);
		g.drawString("Yes", 200, 300, paint);
		g.drawString("No", 280, 300, paint);

	}

	public void drawLineRect(Graphics g, int x, int y, int width, int height){
		g.drawLine(x, y, x + width, y, Color.WHITE);
		g.drawLine(x + width, y + height, x + width, y, Color.WHITE);
		g.drawLine(x, y + height, x, y, Color.WHITE);
		g.drawLine(x, y + height, x + width, y + height, Color.WHITE);
	}

	private void drawGameOverUI() {
		Graphics g = game.getGraphics();
		g.drawRect(0, 0, 1281, 801, Color.BLACK);
		if(humanScore < aiScore){
			g.drawString("YOU LOSE", 240, 200, gameOverPaint);
		}
		else{
			g.drawString("YOU WIN!!!", 240, 200, gameOverPaint);
		}

		g.drawString("Tap to go to main screen", 240, 600, paint);
		g.drawString("" + humanScore + " - " + aiScore, 240, 400, paint);
		if(AndroidGame.androidGame.loggedIn == true){
			g.drawImage(Assets.postButton, 240, 300);
			 
		}
	}

	@Override
	public void pause() {
		if (state == GameState.Running)
			state = GameState.Paused;

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}

	@Override
	public void backButton() {
		goToPause();
	}

	public void goToPause(){
		state = GameState.Paused;
	}
}