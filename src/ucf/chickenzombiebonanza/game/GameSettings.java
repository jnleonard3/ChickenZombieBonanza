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

import java.util.ArrayList;
import java.util.List;

import ucf.chickenzombiebonanza.common.GeocentricCoordinate;

/**
 * 
 */
public class GameSettings {

	private GeocentricCoordinate playAreaCenter = null;

	// In meters
	private float playAreaRadius = 200.0f;
	
	private DifficultyEnum gameDifficulty = DifficultyEnum.EASY;

	private final List<GameSettingsChangeListener> listeners = new ArrayList<GameSettingsChangeListener>();

	public GeocentricCoordinate getPlayAreaCenter() {
		return playAreaCenter;
	}

	public void setPlayAreaCenter(GeocentricCoordinate position) {
		playAreaCenter = position;
		for (GameSettingsChangeListener i : listeners) {
			i.onPlayAreaCenterChanged(playAreaCenter);
		}
	}

	public float getPlayAreaRadius() {
		return playAreaRadius;
	}

	public void setPlayAreaRadius(float radius) {
		playAreaRadius = radius;
		for (GameSettingsChangeListener i : listeners) {
			i.onPlayAreaRadiusChanged(playAreaRadius);
		}
	}
	
	public void setGameDifficulty(DifficultyEnum difficulty) {
		gameDifficulty = difficulty;
		for(GameSettingsChangeListener i : listeners) {
			i.onGameDifficultyChanged(difficulty);
		}
	}
	
	public DifficultyEnum getGameDifficulty() {
		return gameDifficulty;
	}

	public void registerGameSettingsChangeListener(
			GameSettingsChangeListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void unregisterGameSettingsChangeListener(
			GameSettingsChangeListener listener) {
		listeners.remove(listener);
	}

}
