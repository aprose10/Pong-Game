package com.alexrose.pong;

import android.graphics.Rect;

public class Ball {
	private int locationX;
	private int locationY;
	private double speedMult;
	private double velocityX;
	private double velocityY;

	
	public Ball(){
		locationX = 240;
		locationY = 400;
		velocityX = 0.6;
		velocityY = 2;
		speedMult = 1.03;
	}
	
	public int getLocationX(){
		return locationX;
	}
	
	public int getLocationY(){
		return locationY;
	}
	
	public double getVelocityY(){
		return velocityY;
	}
	public double getVelocityX(){
		return velocityX;
	}
	
	public void setLocationX(int locationX){
		this.locationX = locationX;
	}
	public void setLocationY(int locationY){
		this.locationY = locationY;
	}
	public void setVelocityX(double velocityX){
		this.velocityX = velocityX;
	}
	public void setVelocityY(double velocityY){
		this.velocityY = velocityY;
	}
	
	public void move(float deltaTime){
		locationX = (int) Math.round(locationX + velocityX * deltaTime);
		locationY = (int) Math.round(locationY + velocityY * deltaTime);
	}
	
	public void bounceOffWalls(){
		if(locationX <= 0 ){
			//left side
			velocityX = velocityX * -1;
			locationX = 1;
		}
		
		if(locationX >= 460 ){
			//right side
			velocityX = velocityX * -1;
			locationX = 459;
		}
		
	}
	
	public void bounce(){
		velocityY =velocityY * -1 * speedMult;;
		velocityX = velocityX * speedMult;
	}
	public Rect getRect(){
		
		return new Rect(locationX, locationY, locationX + 20, locationY + 20);
	}
	
	public void reset(){
		locationX = 240;
		locationY = 400;
		if (velocityX >= 0){
		velocityX = 0.6;
		}
		else{
			velocityX = -0.6;
		}
		if(velocityY >= 0){
		velocityY = 2;
		}
		else{
			velocityY = -2;
		}
	}

}
