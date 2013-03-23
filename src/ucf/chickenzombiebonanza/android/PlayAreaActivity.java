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

import java.util.List;

import ucf.chickenzombiebonanza.common.GeocentricCoordinate;
import ucf.chickenzombiebonanza.game.GameManager;
import ucf.chickenzombiebonanza.game.GameStateEnum;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.app.AlertDialog;
import android.content.DialogInterface;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

public class PlayAreaActivity extends AbstractGameMapActivity {

	private MapController mapController;
	private MapView mapView;
	GeoPoint p;
	private boolean touchStarted = false;
	public static double latitude = 0, longitude = 0;
	public static double initX = 0, initY = 0;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.main);

		// create a map view
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(false);

		mapController = mapView.getController();
		mapController.setZoom(21); // Zoom 1 is world view

		mapView.setSatellite(!mapView.isSatellite());
		mapView.postInvalidate();
		centerOnPlayArea();

		mapOverlay myOverlay = new mapOverlay();
		List<Overlay> overlays = mapView.getOverlays();
		overlays.add(myOverlay);

	}

	class mapOverlay extends com.google.android.maps.Overlay {
		@Override
		public boolean onTouchEvent(MotionEvent event, MapView mapview) {

			if (event.getAction() == 0) {

				touchStarted = true;
				// GeoPoint
				// p=mapview.getProjection().fromPixels((int)event.getX(),
				// (int)event.getY());

			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {

				touchStarted = false;

			}

			else if (event.getAction() == MotionEvent.ACTION_UP) {

				if (touchStarted == true) {
					GeoPoint p = mapview.getProjection().fromPixels((int) event.getX(), (int) event.getY());
					latitude = p.getLatitudeE6() / 1E6;
					longitude = p.getLongitudeE6() / 1E6;

					// Create Dialog Box

					AlertDialog.Builder alertbox = new AlertDialog.Builder(PlayAreaActivity.this);

					// Set the message to display

					alertbox.setMessage("Would you like this to be your new center of the play area?");

					// Create a listener

					alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

						// Click Listener

						public void onClick(DialogInterface arg0, int arg1) {

							Toast.makeText(getApplicationContext(), "New center has been selected", Toast.LENGTH_SHORT).show();
							centerOnPlayArea();
						}

					});

					// Makes No button

					alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							latitude = initX;
							longitude = initY;
							Toast.makeText(getApplicationContext(), "No new center chosen", Toast.LENGTH_SHORT).show();
						}
					});

					// Display Box
					alertbox.show();

				}

			}
			return false;
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private void centerOnPlayArea() {

		if (latitude != 0 || longitude != 0 && (initX != 0 && initY != 0)) {

			mapController.animateTo(new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6)));
			initX = latitude;
			initY = longitude;
		}

		else if (initX == 0 && initY == 0) {
			GeocentricCoordinate pt = GameManager.getInstance().getPlayerEntity().getPosition();

			Gcc_To_Gdc_Converter.Init(new WE_Ellipsoid());
			Gcc_Coord_3d gcc = new Gcc_Coord_3d(pt.getX(), pt.getY(), pt.getZ());
			Gdc_Coord_3d gdc = new Gdc_Coord_3d();
			Gcc_To_Gdc_Converter.Convert(gcc, gdc);
			mapController.animateTo(new GeoPoint((int) (gdc.latitude * 1E6), (int) (gdc.longitude * 1E6)));

			latitude = gdc.latitude;
			longitude = gdc.longitude;
		}

	}

	private void centerOnLocation() {
		GeocentricCoordinate pt = GameManager.getInstance().getPlayerEntity().getPosition();
		Gcc_To_Gdc_Converter.Init(new WE_Ellipsoid());
		Gcc_Coord_3d gcc = new Gcc_Coord_3d(pt.getX(), pt.getY(), pt.getZ());
		Gdc_Coord_3d gdc = new Gdc_Coord_3d();
		Gcc_To_Gdc_Converter.Convert(gcc, gdc);
		mapController.animateTo(new GeoPoint((int) (gdc.latitude * 1E6), (int) (gdc.longitude * 1E6)));
	}

	// when you push the s key it will go to satellite view and back
	// when you push the c key, it will center back to the user
	public boolean onKeyDown(int keyCode, KeyEvent e) {
		if (keyCode == KeyEvent.KEYCODE_S) {
			mapView.setSatellite(!mapView.isSatellite());
			return true;

		}

		else if (keyCode == KeyEvent.KEYCODE_SPACE) {
			centerOnPlayArea();
			return true;
		}

		else if (keyCode == KeyEvent.KEYCODE_BACK) {
			GameManager.getInstance().updateGameState(GameStateEnum.GAME_SETTINGS);
			finish();
		}

		else if (keyCode == KeyEvent.KEYCODE_C) {
			centerOnLocation();
			return true;
		}

		return (super.onKeyDown(keyCode, e));
	}

}
