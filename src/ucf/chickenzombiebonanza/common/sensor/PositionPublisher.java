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
package ucf.chickenzombiebonanza.common.sensor;

import java.util.ArrayList;
import java.util.List;

import ucf.chickenzombiebonanza.common.GeocentricCoordinate;

public class PositionPublisher extends Sensor {
	
	private GeocentricCoordinate currentPosition = new GeocentricCoordinate();
	
	private List<PositionListener> positionListeners = new ArrayList<PositionListener>();
	
	public PositionPublisher() {
		
	}
	
	public void updatePosition(GeocentricCoordinate coordinate) {
	    if(coordinate != null) {
    		this.setSensorState(SensorStateEnum.ACTIVE);
    		currentPosition = coordinate;
    		for(PositionListener i : positionListeners) {
    			i.receivePositionUpdate(currentPosition);
    		}
	    }
	}
	
	public GeocentricCoordinate getCurrentPosition() {
		return currentPosition;
	}
	
	public void registerForPositionUpdates(PositionListener listener) {
		positionListeners.add(listener);
	}
	
	public void unregisterForPositionUpdates(PositionListener listener) {
		positionListeners.remove(listener);
	}

    @Override
    public void onPause() {        
    }

    @Override
    public void onResume() {
    }

    @Override
    public void refresh() {       
    }  
}
