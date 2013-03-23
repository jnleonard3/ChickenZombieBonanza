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
import ucf.chickenzombiebonanza.game.DifficultyEnum;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * 
 */

public final class GameSettingsActivity extends AbstractGameActivity {
	
	DifficultyEnum Difficulty = DifficultyEnum.EASY;
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.settings);

		Button backButton = (Button) findViewById(R.id.backButton);
		Button applyButton = (Button) findViewById(R.id.buttonApply);
		Button showPlayAreaButton = (Button) findViewById(R.id.playAreaButton);
		final EditText playAreaRadius = (EditText) findViewById(R.id.playAreaRadius);
		//EditText playAreaWidth = (EditText) findViewById(R.id.playAreaWidth); Gone now
		//final AlertDialog alertDialog;
		//alertDialog = new AlertDialog.Builder(GameSettingsActivity.this).create();
		//This probably shouldn't be here
		//GameManager.getInstance().getGameSettings().setPlayAreaRadius(5);
		
		
		
		 RadioGroup radioGroupDifficulty=(RadioGroup)findViewById(R.id.radioGroupDifficulty);
		 
		 radioGroupDifficulty.setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener()
		 {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				RadioButton rE=(RadioButton)findViewById(R.id.radioEasy);
		        RadioButton rM=(RadioButton)findViewById(R.id.radioMedium);
		        RadioButton rH=(RadioButton)findViewById(R.id.radioHard);
		        
		        //DifficultyEnum Difficulty = DifficultyEnum.EASY;//This probably shouldn't be here
		        //But it wouldn't work anywhere else. 
		        
		        if(rE.isChecked()==true){//Easy
		        //setContentView(R.layout.main);//No idea what this does.
		        Difficulty = DifficultyEnum.EASY;
		        	//setGameDifficulty(DifficultyEnum.EASY);
		        	//GameManager.getInstance().getGameSettings().setGameDifficulty(DifficultyEnum.EASY);
		        
		        }
		        else if(rM.isChecked()==true)//Medium
		        {
		        //setContentView(R.layout.main);
		        Difficulty = DifficultyEnum.MEDIUM;
		        	//setGameDifficulty(DifficultyEnum.MEDIUM);
		        	//GameManager.getInstance().getGameSettings().setGameDifficulty(DifficultyEnum.MEDIUM);
		        }
		        else if(rH.isChecked()==true)//Hard
		        {
		        //setContentView(R.layout.main);
		        Difficulty = DifficultyEnum.HARD;
		        	//setGameDifficulty(DifficultyEnum.HARD);
		        	//GameManager.getInstance().getGameSettings().setGameDifficulty(DifficultyEnum.HARD);
		        }
			}
		 });
		 
		
		
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				GameManager.getInstance().updateGameState(
						GameStateEnum.GAME_NAVIGATION);
				finish();
			}
		});
		
		showPlayAreaButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//When this works this is suppose to also turn on and off the visibility of the
				//text boxes
				//final EditText Level = (EditText) findViewById(R.id.Level);
				//final EditText score = (EditText) findViewById(R.id.score);			
				//Level.setVisibility(1);
				//score.setVisibility(1);
				GameManager.getInstance().updateGameState(
						GameStateEnum.GAME_PLAYAREA);
			}
		});
		
		applyButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				 // Create the dialog box

	            AlertDialog.Builder alertbox = new AlertDialog.Builder(GameSettingsActivity.this);
				
	            alertbox.setTitle("Settings saved");//THIS DOESNT SAVE ANYTHING YET!
				GameManager.getInstance().getGameSettings().setGameDifficulty(Difficulty);
				
				
				
				Editable radiusConvert = playAreaRadius.getText();//Gets the radius from the textbox
				
				String radiusString = radiusConvert.toString();//Converts the Radius to a String
				String blankTemp = "";
				if(radiusString.trim().equals(blankTemp));
				{
					radiusString = "5";
				}
				final Float radiusValue = new Float(radiusString);
				
					GameManager.getInstance().getGameSettings().setPlayAreaRadius(radiusValue);
				
				
				alertbox.setPositiveButton("Done", new DialogInterface.OnClickListener() {

	                // Click listener 

	                public void onClick(DialogInterface arg0, int arg1) {
	                	
	                	

	                    Toast.makeText(getApplicationContext(), "Saved...", Toast.LENGTH_SHORT).show();

	                }

	            });
				
				//GameManager.getInstance().getGameSettings().setPlayAreaRadius(radiusValue);//Sets the play area to the radius value.
				
				// Acquire a reference to the system Location Manager
				//LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
				
				alertbox.setMessage("The Difficulty is now "+ GameManager.getInstance().getGameSettings().getGameDifficulty() + "\n The Radius is now " 
				+ GameManager.getInstance().getGameSettings().getPlayAreaRadius());
				
				
				alertbox.show();
				
			}
		});
		
		/////////////////////////////////
		//When Ever we get the value of the radius, get the value or set value, a call will be made
		
	}

	@Override
	public void onBackPressed() {
		GameManager.getInstance()
				.updateGameState(GameStateEnum.GAME_NAVIGATION);
		
		//Level.setVisibility(0); They would turn back on here
		//score.setVisibility(0); As it would not be possbile to get back
	}
	
	public void onApplyPress(){
		//GameManager.getInstance()
		//.updateGameState(GameStateEnum.GAME_NAVIGATION);
		
		AlertDialog alertDialog;
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Packing List");
		alertDialog.setMessage("Could not find the file.");
		alertDialog.show();
		
		
	
	}

}
