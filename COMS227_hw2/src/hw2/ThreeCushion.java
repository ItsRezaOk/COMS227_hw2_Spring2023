package hw2;

import api.BallType;
import api.PlayerPosition;
import static api.BallType.*;
import static api.PlayerPosition.*;

/**
 * Class that models the game of three-cushion billiards.
 * 
 * @author Reza C
 */
public class ThreeCushion {
	/**
	 * @author Reza C
	 * cueBall for the second player
	 */
	private BallType cueBall2;
	/**
	 * @author Reza C
	 * cueball for the first player
	 */
	private BallType cueBall;
	/**
	 * @author Reza C
	 * the given cue ball when asked
	 */
	private BallType currentCueBall;
	/**
	 * @author Reza C
	 * the player who won the ability to choose break
	 */
	private PlayerPosition winner;
	
	/**
	 * @author Reza C
	 * current inningPlayer
	 */
	private PlayerPosition inningPlayer;
	/**
	 * @author Reza C
	 * current inning at time requested
	 */
	private int currentInning;
	/**
	 * @author Reza C
	 * if the inning is in progress or not
	 */
	private boolean inningStatus;
	/**
	 * @author Reza C
	 * if the shot is the break shot or not
	 */
	private boolean breakShot;
	/**
	 * @author Reza C
	 * points for player a
	 */
	private int pointA;
	/**
	 * @author Reza C
	 * points for player b
	 */
	private int pointB;
	/**
	 * @author Reza C
	 * if the shot is in progress or not
	 */
	private boolean shotStatus;
	/**
	 *@author Reza C
	 * if there is enough cushion hits to get a point
	 */
	private boolean pointable;
	/**
	 * @author Reza C
	 * if there the cushion hits and the balls have been hit
	 */
	private boolean pointWithBall;
	/**
	 * @author Reza C
	 * keeps tracks of the cushion hits
	 */
	private int cushHit;
	/**
	 * @author Reza C
	 * keeps track how many balls hit
	 */
	private int ballTap;
	/**
	 * @author Reza C
	 * bankshot tracker
	 */
	private boolean bankShot = false;
	/**
	 * @author Reza C
	 * given points needed to win
	 */
	private int pointsToWin;
	/**
	 * @author Reza C
	 * keeps track of if the game is over
	 */
	private boolean gameOver = false;

	
	
	/**
	 * @author Reza C
	 * Creates a new game of three-cushion billiards with a given lag winner and 
	 * the predetermined number of points required to win the game.
	 * @param lagWinner
	 * @param pointsToWin
	 */
	public ThreeCushion(PlayerPosition lagWinner, int pointsToWin) {
		//all the aspect here just intialize the game
		currentInning = 1;
		inningStatus = false;
		breakShot = true;
		this.pointsToWin = pointsToWin;
		winner = lagWinner;
	}
	/**
	 * @author Reza C
	 * Returns a one-line string representation of the current game state. The
	 * format is:
	 * <p>
	 * <tt>Player A*: X Player B: Y, Inning: Z</tt>
	 * <p>
	 * The asterisks next to the player's name indicates which player is at the
	 * table this inning. The number after the player's name is their score. Z is
	 * the inning number. Other messages will appear at the end of the string.
	 * 
	 * @return one-line string representation of the game state
	 */
	public String toString() {
		String fmt = "Player A%s: %d, Player B%s: %d, Inning: %d %s%s";
		String playerATurn = "";
		String playerBTurn = "";
		String inningStatus = "";
		String gameStatus = "";
		if (getInningPlayer() == PLAYER_A) {
			playerATurn = "*";
		} else if (getInningPlayer() == PLAYER_B) {
			playerBTurn = "*";
		}
		if (isInningStarted()) {
			inningStatus = "started";
		} else {
			inningStatus = "not started";
		}
		if (isGameOver()) {
			gameStatus = ", game result final";
		}
		return String.format(fmt, playerATurn, getPlayerAScore(), playerBTurn, getPlayerBScore(), getInning(),
				inningStatus, gameStatus);
	}
	/**
	 * @author Reza C
	 * Indicates the given ball has impacted the given cushion
	 */
	public void cueBallImpactCushion() {
	//dictating if it hit
		cushHit += 1;
		if(cushHit >= 3) {
			pointable = true;
		} else if(cushHit < 3){
			pointable = false;
		}
	// I created this pointable boolean becasue i thought i would be easier to work with
		// if i could assing something to make it be a point
		if(cushHit >= 3 & ballTap == 0) {
			bankShot = true;
		}
		
	}
	
	/**
	 * @author Reza C
	 * @param ball
	 * Indicates the player's cue ball has struck the given ball
	 */
	public void cueBallStrike(BallType ball) {
	int redCount = 0;
	
	if(!gameOver) {
		if(ball == RED) {
			redCount += 1;
		}		
		if(cushHit < 3 && ballTap == 2) {
			pointable = false;
			endShot();
		}
		if(ballTap == 2 && cushHit == 0) {
			pointable = false;
			endShot();
		}
		if(currentCueBall != ball) {
				ballTap += 1;
				if(redCount > 1) {
					ballTap -= 1;
				}
				if (pointable && ballTap == 2) {
					pointWithBall = true;
					ballTap = 0;
					cushHit = 0;
					//I created the point with ball so I could mainpulate more with both booleans
				}
			}
		}
	}
	/**
	 * @author Reza C
	 * @param ball
	 * Indicates the cue stick has struck the given ball
	 */
	public void cueStickStrike(BallType ball) {
		//assigning the ball based on who the player is
		if(inningPlayer == PLAYER_B) {
			currentCueBall = cueBall2;
		} else if(inningPlayer == PLAYER_A) {
			currentCueBall = cueBall;
		}
		//its a foul if you hit the wrong ball
		if(ball != currentCueBall) {
			foul();
		}
		if(gameOver) {
			shotStatus = false;
			inningStatus = false;
		} else {
			inningStatus = true;
			shotStatus = true;
		}
	}
	/**
	 * @author Reza C
	 * Indicates that all balls have stopped motion
	 */
	public void endShot() {
		breakShot = false;
		ballTap = 0;
		cushHit = 0;
		if(shotStatus){
			//if a player doesn't get a point and the shot ends
			//the following should happen
			if(!pointWithBall && inningPlayer == PLAYER_A) {
				currentInning += 1;
				shotStatus = false;
				inningPlayer = PLAYER_B;
				if(inningPlayer == PLAYER_B) {
					currentCueBall = cueBall2;
				}
				inningStatus = false;
			}//if its a vaild point they should get a point
			else if(pointWithBall && inningPlayer == PLAYER_A){
				pointA += 1;
				if(pointA == pointsToWin) {
					gameOver = true;
				}
			}else if(!pointWithBall && inningPlayer == PLAYER_B) {
				inningPlayer = PLAYER_A;
				currentInning += 1;
				shotStatus = false;
				inningStatus = false;
				if(inningPlayer == PLAYER_A) {
					currentCueBall = cueBall;
				}
			}else if(pointWithBall && inningPlayer == PLAYER_B){
				pointB += 1;
				if(pointB == pointsToWin) {
					gameOver = true;
				}
			}
		}
			shotStatus = false;
	}
	/**
	 * @author Reza C
	 * A foul immediately ends the player's inning, 
	 * even if the current shot has not yet ended.
	 */
	public void foul() {
		inningStatus = false;
		shotStatus = false;
		if(inningPlayer == PLAYER_A) {
			inningPlayer = PLAYER_B;
			if(!gameOver) {
			currentInning += 1;
			}
			if(inningPlayer == PLAYER_B) {
				currentCueBall = cueBall2;
			}
		} else if(inningPlayer == PLAYER_B) {
			inningPlayer = PLAYER_A;
			if(!gameOver) {
				currentInning += 1;
			}
			if(inningPlayer == PLAYER_A) {
				currentCueBall = cueBall;
			}
		}
	}
	/**
	 * @author Reza C
	 * @return the current cueball
	 */
	public BallType getCueBall() {
		return currentCueBall;
	}
	/**
	 * @author Reza C
	 * @return current inning
	 */
	public int getInning() {
		return currentInning;
	}
	/**
	 * @author Reza C
	 * @return current inning player
	 */
	public PlayerPosition getInningPlayer() {
		return inningPlayer;
	}
	/**
	 * @author Reza C
	 * @return player a score
	 */
	public int getPlayerAScore() {
		return pointA;
	}
	/**
	 * @author Reza C
	 * @return player b score
	 */
	public int getPlayerBScore() {
		return pointB;
	}
	/**
	 * @author Reza C
	 * @return if its a bank shot
	 */
	public boolean isBankShot() {
		return bankShot;
	}
	/**
	 * @author Reza C
	 * @return is a break shot
	 */
	public boolean isBreakShot() {
		return breakShot;
		
	}
	/**
	 * @author Reza C
	 * @return is the game over
	 */
	public boolean isGameOver() {
		return gameOver;
	}
	/**
	 * @author Reza C
	 * @return the status of the inning
	 */
	public boolean isInningStarted() {
		return inningStatus;
	}
	/**
	 * @author Reza C
	 * @return the status of the shot
	 */
	public boolean isShotStarted() {
		return shotStatus;
	}
	/**
	 * @author Reza C
	 * @param selfBreak if the winner wants to self break
	 * @param cueBall what ball the winner wants
	 * Sets whether the player that won the lag chooses to break (take first shot)
	 *  or chooses the other player to break.
	 */
	public void lagWinnerChooses(boolean selfBreak, BallType cueBall) {
		/*
		 * all of the code here is just assinging the lagWinner the ability to break
		 * so if they don't want to break the other player breaks and then the winner picks a ball
		 * and the other player get the other ball
		 */
		if(winner == PLAYER_A) {
			if(cueBall == WHITE) {
				this.cueBall = cueBall;
				cueBall2 = YELLOW;
			} else if(cueBall == YELLOW) {
				cueBall2 = WHITE;
				this.cueBall = cueBall;
			}
			if(selfBreak) {
				inningPlayer = PLAYER_A;
				currentCueBall = this.cueBall;
			} else {
				inningPlayer = PLAYER_B;
				currentCueBall = cueBall2;
			}
		}
		if(winner == PLAYER_B) {
			if(cueBall == WHITE) {
				cueBall2 = WHITE;
				this.cueBall = YELLOW;
			} else if(cueBall == YELLOW) {
				cueBall2 = YELLOW;
				this.cueBall = WHITE;
			}
			if(selfBreak) {
				inningPlayer = PLAYER_B;
				currentCueBall = cueBall2;
			} else {
				inningPlayer = PLAYER_A;
				currentCueBall = this.cueBall;
			}
			
		}
			
		
	}
	
}
