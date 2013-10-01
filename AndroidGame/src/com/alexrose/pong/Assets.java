package com.alexrose.pong;

import com.alexrose.framework.Image;
import com.alexrose.framework.Music;
import com.alexrose.framework.Sound;

public class Assets {
    
    public static Image startButton;
    public static Sound click;
    public static Music theme;
    public static Image splash;
	public static Image paddles;
	public static Image ball;
	public static Image menuBackground;
	public static Image step1;
	public static Image step2;
	public static Image step3;
	public static Image shopButton;
    
    public static void load(PongGame pongGame) {
        // TODO Auto-generated method stub
        theme = pongGame.getAudio().createMusic("mainTheme.mp3");
        theme.setLooping(true);
        theme.setVolume(0.60f);
        theme.play();
    }
}