package com.alexrose.pong;


import com.alexrose.framework.Game;
import com.alexrose.framework.Graphics;
import com.alexrose.framework.Screen;
import com.alexrose.framework.Graphics.ImageFormat;


public class LoadingScreen extends Screen {
    public LoadingScreen(Game game) {
        super(game);
    }


    @Override
    public void update(float deltaTime) {
        Graphics g = game.getGraphics();
        Assets.startButton = g.newImage("start.png", ImageFormat.RGB565);
        Assets.click = game.getAudio().createSound("click.ogg");
        Assets.paddles = g.newImage("paddles.jpg", ImageFormat.RGB565);
        Assets.ball = g.newImage("ball.png", ImageFormat.ARGB4444);
        Assets.menuBackground = g.newImage("menuBackground.jpg", ImageFormat.RGB565);
        Assets.step1 = g.newImage("mario-firstimage.png", ImageFormat.ARGB4444);
        Assets.step2 = g.newImage("mario-secondimage.png", ImageFormat.ARGB4444);
        Assets.step3 = g.newImage("mario-thirdimage.png", ImageFormat.ARGB4444);
        Assets.shopButton = g.newImage("shopButton.jpg", ImageFormat.RGB565);
        Assets.newPongPaddle = g.newImage("newPongPaddle.jpg", ImageFormat.RGB565);
        Assets.facebookButton = g.newImage("facebook_button.png", ImageFormat.ARGB4444);
        Assets.facebookLogout = g.newImage("fb_logout.png", ImageFormat.ARGB4444);
        Assets.postButton = g.newImage("postButton.jpg", ImageFormat.RGB565);
        
        game.setScreen(new MainMenuScreen(game));


    }


    @Override
    public void paint(float deltaTime) {
    	 Graphics g = game.getGraphics();
         g.drawImage(Assets.splash, 0, 0);

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


    }
}