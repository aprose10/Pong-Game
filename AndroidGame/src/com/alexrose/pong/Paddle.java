package com.alexrose.pong;

import android.graphics.Rect;

public class Paddle {
	private int locationX;
	private int locationY;
	private double moveSpeed = 2;
	private int movingDirection; //right is 0, left is 1, stop is 2

	public Paddle(boolean humanPaddle){
		if(humanPaddle == true){
			locationX = 215;
			locationY = 779;
		}
		else{
			locationX = 215;
			locationY = 0;
			moveSpeed = 1.5;
		}
	}

	public int getLocationX(){
		return locationX;
	}

	public int getLocationY(){
		return locationY;
	}
	
	public void setLocationX(int locationX){
		this.locationX = locationX;
	}

	public Rect getRect(){

		return new Rect(locationX, locationY, locationX + 50, locationY + 21);
	}

	public void moveDirection(int moveDirection){
		movingDirection = moveDirection;
	}

	public void move(float deltaTime){
		if(movingDirection == 0 && locationX <= 430){
			locationX = (int) (locationX + moveSpeed * deltaTime);
		}

		if(movingDirection == 1 && locationX >= 0){
			locationX = (int) (locationX - moveSpeed * deltaTime);
		}

	}

}
