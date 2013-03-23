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
package ucf.chickenzombiebonanza.android.sensor;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import ucf.chickenzombiebonanza.common.LocalOrientation;
import ucf.chickenzombiebonanza.common.Vector3d;
import ucf.chickenzombiebonanza.common.sensor.OrientationPublisher;
import ucf.chickenzombiebonanza.common.sensor.SensorStateEnum;

public class GyroscopeListener extends OrientationPublisher implements SensorEventListener {
    
    private Timer broadcastTimer = new Timer();
    
    private final SensorManager sensorManager;
    
    private float[] gravity = new float[3];
    private float[] geomag = new float[3];
    private float[] rotationMatrix;
	
	public GyroscopeListener(Context context) {
	    super();
		sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		
        broadcastTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                GyroscopeListener.this.broadcastOrientation();
            }
        }, 0, 100);	
        this.setSensorState(SensorStateEnum.PAUSED);
	}
		
	@Override
	public void onResume() {
	    sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
	    sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
	}

    @Override
    public void onPause() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        
    }

    @Override
    public void onSensorChanged(SensorEvent evt) {
        int type = evt.sensor.getType();

        // Smoothing the sensor data a bit
        if (type == Sensor.TYPE_MAGNETIC_FIELD) {
            geomag[0] = (geomag[0] * 1 + evt.values[0]) * 0.5f;
            geomag[1] = (geomag[1] * 1 + evt.values[1]) * 0.5f;
            geomag[2] = (geomag[2] * 1 + evt.values[2]) * 0.5f;
        } else if (type == Sensor.TYPE_ACCELEROMETER) {
            gravity[0] = (gravity[0] * 2 + evt.values[0]) * 0.33334f;
            gravity[1] = (gravity[1] * 2 + evt.values[1]) * 0.33334f;
            gravity[2] = (gravity[2] * 2 + evt.values[2]) * 0.33334f;
        }

        if ((type == Sensor.TYPE_MAGNETIC_FIELD) || (type == Sensor.TYPE_ACCELEROMETER)) {
            rotationMatrix = new float[16];
            
            //Gets the rotation matrix to convert device coordinates to world coordinates
            SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomag);
            
            SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, rotationMatrix);
            
            LocalOrientation orientation = new LocalOrientation(
                new Vector3d(rotationMatrix[2],rotationMatrix[6],rotationMatrix[10]),
                new Vector3d(rotationMatrix[1],rotationMatrix[5],rotationMatrix[9]));
            this.updateOrientation(orientation, false);
        }
    }

}
