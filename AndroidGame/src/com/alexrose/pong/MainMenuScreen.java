package com.alexrose.pong;

import java.util.List;

import com.alexrose.framework.Game;
import com.alexrose.framework.Graphics;
import com.alexrose.framework.Image;
import com.alexrose.framework.Screen;
import com.alexrose.framework.Input.TouchEvent;


public class MainMenuScreen extends Screen {
	
	Animation anim;
	int marioXlocation = 40;
	public MainMenuScreen(Game game) {
		super(game);
		
		Image step1, step2, step3, currentSprite;
		
		step1 = Assets.step1;
		step2 = Assets.step2;
		step3 = Assets.step3;
		
		anim = new Animation();
		anim.addFrame(step1,7);
		anim.addFrame(step2,7);
		anim.addFrame(step3,7);
		anim.addFrame(step2,7);
		
		currentSprite = anim.getImage();
		
	}


	@Override
	public void update(float deltaTime) {
		Graphics g = game.getGraphics();
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();


		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {


				if (inBounds(event, 165, 346, 150, 108)) {
					//START GAME
					game.setScreen(new GameScreen(game));               
				}


			}
		}
		
	}


	private boolean inBounds(TouchEvent event, int x, int y, int width,
			int height) {
		if (event.x > x && event.x < x + width - 1 && event.y > y
				&& event.y < y + height - 1)
			return true;
		else
			return false;
	}


	@Override
	public void paint(float deltaTime) {
		changeMarioXlocation(deltaTime);
		Graphics g = game.getGraphics();
		g.drawImage(Assets.menuBackground, 0, 0);
		g.drawImage(Assets.startButton, 165, 346);
		g.drawImage(anim.getImage(), marioXlocation, 700);
		
		anim.update(deltaTime);
	}
	
	public void changeMarioXlocation(float deltaTime){
		marioXlocation = (int) (marioXlocation + deltaTime * 1.0);
		
		if(marioXlocation >= 480){
			marioXlocation = -40;
		}
	}


	@Override
	public void pause() {
	}


	@Override
	public void resume() {


	}


	@Override
	public void dispose() {


	}


	@Override
	public void backButton() {
		//Display "Exit Game?" Box
				android.os.Process.killProcess(android.os.Process.myPid());

	}
}