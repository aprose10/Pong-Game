package com.alexrose.pong;
public class SaveGameState {
	public int ballLocationX;
	public int ballLocationY;
	public double velocityX;
	public double velocityY;
	public int humanScore;
	public int AiScore;
	public int humanPaddleX;
	public int AiPaddleX;
	public boolean HitHuman;
	
	public SaveGameState(int ballLocationX, int ballLocationY, double velocityX,
			double velocityY, int humanScore, int AiScore, int humanPaddleX,
			int AiPaddleX, boolean HitHuman){
		this.ballLocationX = ballLocationX;
		this.ballLocationY = ballLocationY;
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.humanScore = humanScore;
		this.AiScore = AiScore;
		this.humanPaddleX = humanPaddleX;
		this.AiPaddleX = AiPaddleX;
		this.HitHuman = HitHuman;
		
	}
}
