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

import geotransform.coords.Gcc_Coord_3d;
import geotransform.coords.Gdc_Coord_3d;
import geotransform.ellipsoids.WE_Ellipsoid;
import geotransform.transforms.Gcc_To_Gdc_Converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ucf.chickenzombiebonanza.common.GeocentricCoordinate;
import ucf.chickenzombiebonanza.common.sensor.PositionListener;
import ucf.chickenzombiebonanza.game.DifficultyEnum;
import ucf.chickenzombiebonanza.game.GameManager;
import ucf.chickenzombiebonanza.game.GameSettingsChangeListener;
import ucf.chickenzombiebonanza.game.GameStateEnum;
import ucf.chickenzombiebonanza.game.ScoreListener;
import ucf.chickenzombiebonanza.game.entity.GameEntity;
import ucf.chickenzombiebonanza.game.entity.GameEntityListener;
import ucf.chickenzombiebonanza.game.entity.GameEntityTagEnum;
import ucf.chickenzombiebonanza.game.entity.LifeformHealthListener;
import ucf.chickenzombiebonanza.game.item.HealthInventoryObject;
import ucf.chickenzombiebonanza.game.item.InventoryObject;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 */
public class NavigationGameActivity extends AbstractGameMapActivity implements PositionListener, GameEntityListener, GameSettingsChangeListener, LifeformHealthListener, ScoreListener {
    /** Called when the activity is first created. */

    private MapController mapController;
    private MapView mapView;

    private List<Overlay> mapOverlays;
    private PlayerNavigationOverlay playerOverlay;
    private WaypointNavigationOverlay waypointOverlay;
    private SimpleNavigationGameOverlay powerUpOverlay;

    private Drawable playerDrawableIcon;
    private Drawable waypointDrawableIcon;
    private Drawable powerUpDrawableIcon;
    
    private TextView healthTextView;
    private TextView scoreTextView;
    
    private LinearLayout itemLayout;

    private final Map<GameEntity, OverlayItem> entityToOverlayItemMap = new HashMap<GameEntity, OverlayItem>();
    private final Map<InventoryObject, ImageButton> inventoryObjectToButton = new HashMap<InventoryObject, ImageButton>();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.main);

        // create a map view
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setSatellite(true);

        mapController = mapView.getController();
        mapController.setZoom(21); // Zoom 1 is world view

        final ProgressDialog dialog = ProgressDialog.show(this, "Waiting for player position", "Waiting for GPS position...", true);
        Thread loadThread = new Thread() {
            @Override
            public void run() {
                GeocentricCoordinate position = GameManager.getInstance().getPlayerEntity().getPosition();
                while (position == null || position.isZero()) {
                    synchronized (this) {
                        try {

                            this.wait(10);
                        } catch (InterruptedException e) {
                        }
                        GameManager.getInstance().getPlayerEntity().getPositionPublisher().refresh();
                    }
                    position = GameManager.getInstance().getPlayerEntity().getPosition();
                }
                dialog.dismiss();
                
                itemLayout = (LinearLayout) findViewById(R.id.itemsLayout);

                playerDrawableIcon = NavigationGameActivity.this.getResources().getDrawable(R.drawable.playermapicon);
                waypointDrawableIcon = NavigationGameActivity.this.getResources().getDrawable(R.drawable.waypointmapicon);
                powerUpDrawableIcon = NavigationGameActivity.this.getResources().getDrawable(R.drawable.powerupmapicon);

                mapOverlays = mapView.getOverlays();
                playerOverlay = new PlayerNavigationOverlay(playerDrawableIcon);
                waypointOverlay = new WaypointNavigationOverlay(waypointDrawableIcon);
                powerUpOverlay = new SimpleNavigationGameOverlay(powerUpDrawableIcon, "Power Up", "A player power up.");

                mapOverlays.add(waypointOverlay);
                mapOverlays.add(powerUpOverlay);
                mapOverlays.add(playerOverlay);

                playerOverlay.updatePlayerOverlayItem(geocentricToGeopoint(position));

                // this will refresh map i think?
                mapView.postInvalidate();

                if (GameManager.getInstance().getGameSettings().getPlayAreaCenter() == null) {
                    GameManager.getInstance().getGameSettings().setPlayAreaCenter(position);
                    regeneratePlayArea();
                }

            }
        };

        loadThread.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        GameManager.getInstance().registerGameEntityListener(this);
        GameManager.getInstance().getGameSettings().registerGameSettingsChangeListener(this);
        GameManager.getInstance().getPlayerEntity().registerHealthListener(this);
        GameManager.getInstance().getPlayerEntity().updateHealthListener(this);
        GameManager.getInstance().registerScoreListener(this);
        GameManager.getInstance().updateScoreListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        GameManager.getInstance().getPlayerEntity().getPositionPublisher().pauseSensor();
        GameManager.getInstance().getPlayerEntity().getPositionPublisher().unregisterForPositionUpdates(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        GameManager.getInstance().getPlayerEntity().getPositionPublisher().resumeSensor();
        GameManager.getInstance().getPlayerEntity().getPositionPublisher().registerForPositionUpdates(this);
        if (GameManager.getInstance().getPlayerEntity().isDead()) {
            onGameOver();
        }
        refreshItems();
    }

    @Override
    public void onStop() {
        super.onStop();
        GameManager.getInstance().unregisterGameEntityListener(this);
        GameManager.getInstance().getGameSettings().unregisterGameSettingsChangeListener(this);
        GameManager.getInstance().getPlayerEntity().unregisterHealthListener(this);
        GameManager.getInstance().unregisterScoreListener(this);
    }

    public boolean onKeyDown(int keyCode, KeyEvent e) {
        // When you push the s key it will go to satellite view and back
        if (keyCode == KeyEvent.KEYCODE_S) {
            mapView.setSatellite(!mapView.isSatellite());
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_C) {
            centerOnLocation();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_SPACE) {
            centerOnLocation();
            return true;
        }

        return super.onKeyDown(keyCode, e);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settingsoption:
                GameManager.getInstance().updateGameState(GameStateEnum.GAME_SETTINGS);
                return true;
            case R.id.launchshootinggame:
                GameManager.getInstance().updateGameState(GameStateEnum.GAME_SHOOTING);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    @Override
    public void receivePositionUpdate(GeocentricCoordinate pt) {
        GeoPoint playerPosition = geocentricToGeopoint(pt);
        if (playerOverlay != null) {
            playerOverlay.updatePlayerOverlayItem(playerPosition);
        }
        if (mapController != null) {
        	try {
        		mapController.animateTo(playerPosition);
        	} catch (Exception e) {
        		
        	}
        }
    }

    private void centerOnLocation() {
        GeocentricCoordinate pt = GameManager.getInstance().getPlayerEntity().getPosition();
        if (mapController != null) {
            mapController.animateTo(geocentricToGeopoint(pt));
        }
    }

    private void onGameOver() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("You have died.\n");
        strBuilder.append("Your score this session was ");
        strBuilder.append(GameManager.getInstance().getCurrentScore());
        strBuilder.append(".\n");
        strBuilder.append("Would you like to try again?");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(strBuilder.toString()).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GameManager.getInstance().restart();
            }
        }).setNegativeButton("Surrender", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GameManager.getInstance().restart();
                NavigationGameActivity.this.moveTaskToBack(true);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onGameEntityAdded(GameEntity entity) {
        OverlayItem overlayItem = null;
        if (entity.getTag() == GameEntityTagEnum.WAYPOINT) {
            overlayItem = waypointOverlay.addOverlayItem(geocentricToGeopoint(entity.getPosition()));
        } else if (entity.getTag() == GameEntityTagEnum.POWER_UP) {
            overlayItem = powerUpOverlay.addOverlayItem(geocentricToGeopoint(entity.getPosition()));
        }
        if (overlayItem != null) {
            synchronized (entityToOverlayItemMap) {
                entityToOverlayItemMap.put(entity, overlayItem);
            }
        }
    }

    @Override
    public void onGameEntityDeleted(GameEntity entity) {
        synchronized (entityToOverlayItemMap) {
            entityToOverlayItemMap.remove(entity);
        }
    }

    private static GeoPoint geocentricToGeopoint(GeocentricCoordinate coord) {
        Gcc_To_Gdc_Converter.Init(new WE_Ellipsoid());
        Gcc_Coord_3d gcc = new Gcc_Coord_3d(coord.getX(), coord.getY(), coord.getZ());
        Gdc_Coord_3d gdc = new Gdc_Coord_3d();
        Gcc_To_Gdc_Converter.Convert(gcc, gdc);
        return new GeoPoint((int) (gdc.latitude * 1E6), (int) (gdc.longitude * 1E6));
    }

    private class NavigationGameOverlay extends ItemizedOverlay<OverlayItem> {

        private final List<OverlayItem> overlayItems = new ArrayList<OverlayItem>();

        public NavigationGameOverlay(Drawable defaultMarker) {
            super(boundCenter(defaultMarker));
            populate();
        }

        public void removeOverlayItem(OverlayItem overlayItem) {
            synchronized (overlayItems) {
                overlayItems.remove(overlayItem);
            }
            populate();
        }

        public void addOverlayItem(OverlayItem overlayItem) {
            synchronized (overlayItems) {
                overlayItems.add(overlayItem);
            }
            populate();
        }

        @Override
        protected OverlayItem createItem(int i) {
            return overlayItems.get(i);
        }

        // Returns present number of items in list
        @Override
        public int size() {
            return overlayItems.size();
        }
    }

    private class SimpleNavigationGameOverlay extends NavigationGameOverlay {

        private final String overlayItemTitle, overlayItemSnippet;

        public SimpleNavigationGameOverlay(Drawable defaultMarker, String overlayItemTitle, String overlayItemSnippet) {
            super(boundCenter(defaultMarker));
            this.overlayItemTitle = overlayItemTitle;
            this.overlayItemSnippet = overlayItemSnippet;
            populate();
        }

        public OverlayItem addOverlayItem(GeoPoint position) {
            OverlayItem newItem = new OverlayItem(position, overlayItemTitle, overlayItemSnippet);
            addOverlayItem(newItem);
            return newItem;
        }
    }

    private class PlayerNavigationOverlay extends SimpleNavigationGameOverlay {

        private final static String PLAYER_TITLE = "Player";

        private final static String PLAYER_SNIPPET = "The player.";

        OverlayItem playerOverlayItem = null;

        public PlayerNavigationOverlay(Drawable defaultMarker) {
            super(boundCenter(defaultMarker), PLAYER_TITLE, PLAYER_SNIPPET);
            populate();
        }

        public void updatePlayerOverlayItem(GeoPoint position) {
            removeOverlayItem(playerOverlayItem);
            playerOverlayItem = addOverlayItem(position);
        }
    }

    private class WaypointNavigationOverlay extends SimpleNavigationGameOverlay {
        private final static String WAYPOINT_TITLE = "Waypoint";

        private final static String WAYPOINT_SNIPPET = "A player objective.";

        public WaypointNavigationOverlay(Drawable defaultMarker) {
            super(boundCenter(defaultMarker), WAYPOINT_TITLE, WAYPOINT_SNIPPET);
            populate();
        }

        @Override
        protected boolean onTap(int index) {
            return true;
        }
    }

    private void regeneratePlayArea() {
        List<GameEntity> toRemove = new ArrayList<GameEntity>();
        synchronized (entityToOverlayItemMap) {
            for (GameEntity i : entityToOverlayItemMap.keySet()) {
                if (i.getTag() == GameEntityTagEnum.WAYPOINT) {
                    waypointOverlay.removeOverlayItem(entityToOverlayItemMap.get(i));
                    toRemove.add(i);
                } else if (i.getTag() == GameEntityTagEnum.POWER_UP) {
                    powerUpOverlay.removeOverlayItem(entityToOverlayItemMap.get(i));
                    toRemove.add(i);
                }
            }
        }
        for (GameEntity i : toRemove) {
            i.destroyEntity();
        }
        GameManager.getInstance().addWaypoint();
        GameManager.getInstance().addWaypoint();
        GameManager.getInstance().addPowerUp();
        GameManager.getInstance().addPowerUp();
        GameManager.getInstance().addPowerUp();
    }

    @Override
    public void onPlayAreaCenterChanged(GeocentricCoordinate position) {
        regeneratePlayArea();
    }

    @Override
    public void onPlayAreaRadiusChanged(float radius) {
        regeneratePlayArea();
    }

    @Override
    public void onGameDifficultyChanged(DifficultyEnum difficulty) {
        // Do nothing
    }

    @Override
    public void onLifeformHealthChange(int currentHealth, int maxHealth) {
        if (healthTextView == null) {
            healthTextView = (TextView) findViewById(R.id.healthTextView);
        }
        healthTextView.setText("Health: " + currentHealth + " / " + maxHealth);
        healthTextView.invalidate();
    }

    @Override
    public void onScoreUpdated(int score) {
        if(scoreTextView == null) {
            scoreTextView = (TextView) findViewById(R.id.scoreTextView);
        }
        scoreTextView.setText("Score: " + score);
        scoreTextView.invalidate();        
    }
    
    public void refreshItems() {
        List<HealthInventoryObject> objects = GameManager.getInstance().getPlayerEntity().getHealthPacks();
        synchronized(inventoryObjectToButton) {
            for(HealthInventoryObject i : objects) {
                final HealthInventoryObject healthObject = i;
                if(i.getCount() <= 0) {
                    if(inventoryObjectToButton.containsKey(i)) {
                        if(itemLayout == null) {
                            itemLayout = (LinearLayout) findViewById(R.id.itemsLayout);
                        }
                        itemLayout.removeView(inventoryObjectToButton.get(i));
                        inventoryObjectToButton.remove(i);
                    }
                } else if (!inventoryObjectToButton.containsKey(i)) {
                    ImageButton newButton = new ImageButton(this);
                    if(i.equals(HealthInventoryObject.SMALL_HEALTH_KIT)) {
                        newButton.setImageResource(R.drawable.smallmedkiticon);
                    } else if (i.equals(HealthInventoryObject.MEDIUM_HEALTH_KIT)) {
                        newButton.setImageResource(R.drawable.mediummedkiticon);
                    } else if(i.equals(HealthInventoryObject.LARGE_HEALTH_KIT)) {
                        newButton.setImageResource(R.drawable.largemedkiticon);
                    }
                    newButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            GameManager.getInstance().getPlayerEntity().useItem(healthObject);
                            refreshItems();
                        }
                        
                    });
                    inventoryObjectToButton.put(healthObject, newButton);
                    if(itemLayout == null) {
                        itemLayout = (LinearLayout) findViewById(R.id.itemsLayout);
                    }
                    itemLayout.addView(newButton);
                }
            }
        }
    }
}