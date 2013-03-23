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
package ucf.chickenzombiebonanza.android;

import ucf.chickenzombiebonanza.game.GameManager;
import ucf.chickenzombiebonanza.game.GameStateEnum;
import ucf.chickenzombiebonanza.game.GameStateListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * 
 */
public abstract class AbstractGameActivity extends Activity implements GameStateListener {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        AbstractGameActivity.onCreateStatic(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AbstractGameActivity.onDestroyStatic(this);
    }

    @Override
    public void gameStateChanged(GameStateEnum state) {
        AbstractGameActivity.gameStateChangedStatic(this, state);
    }

    public static void onCreateStatic(GameStateListener listener) {
        GameManager.getInstance().addStateListener(listener);
    }

    public static void onDestroyStatic(GameStateListener listener) {
        GameManager.getInstance().removeStateListener(listener);
    }

    public static void gameStateChangedStatic(Activity activity, GameStateEnum state) {
        if (state == GameStateEnum.GAME_NAVIGATION) {
            Intent navigationIntent = new Intent(activity, NavigationGameActivity.class);
            activity.startActivity(navigationIntent);
        } else if (state == GameStateEnum.GAME_SHOOTING) {
            Intent shootingIntent = new Intent(activity, ShootingGameActivity.class);
            activity.startActivity(shootingIntent);
        } else if (state == GameStateEnum.GAME_SETTINGS) {
            Intent settingsIntent = new Intent(activity, GameSettingsActivity.class);
            activity.startActivity(settingsIntent);
        } else if (state == GameStateEnum.GAME_PLAYAREA) {
        	Intent playareaIntent = new Intent(activity, PlayAreaActivity.class);
        	activity.startActivity(playareaIntent);
        }
    }
}
