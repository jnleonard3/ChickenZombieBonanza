/**
 * Copyright (c) 2011, Chicken Zombie Bonanza Project
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Chicken Zombie Bonanza Project nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CHICKEN ZOMBIE BONANZA PROJECT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ucf.chickenzombiebonanza.game;

public enum DifficultyEnum {
	EASY (10000, 5, 10, 100, 50, 1000),
	MEDIUM (7000, 10, 10, 75, 70, 2500),
	HARD (5000, 15, 10, 50, 100, 5000);
	
	private final int enemySpawnFrequency;
	
	private final int enemyHealth;
	
	private final int enemyCount;
	
	private final int playerHealth;
	
	private final int enemyDestroyedScore;
	
	private final int survivalScore;
	
	DifficultyEnum(int spawnFrequency, int enemyHealth, int enemyCount, int playerHealth, int enemyDestroyedScore, int survivalScore) {
		this.enemySpawnFrequency = spawnFrequency;
		this.enemyHealth = enemyHealth;
		this.enemyCount = enemyCount;
		this.playerHealth = playerHealth;
		this.enemyDestroyedScore = enemyDestroyedScore;
		this.survivalScore = survivalScore;
	}
	
	public int getEnemySpawnFrequency() {
		return enemySpawnFrequency;
	}
	
	public int getEnemyHealth() {
		return enemyHealth;
	}
	
	public int getEnemyCount() {
		return enemyCount;
	}
	
	public int getPlayerHealth() {
		return playerHealth;
	}
	
	public int getEnemyDestroyedScore() {
		return enemyDestroyedScore;
	}
	
	public int getSurvivalScore() {
		return survivalScore;
	}
}



