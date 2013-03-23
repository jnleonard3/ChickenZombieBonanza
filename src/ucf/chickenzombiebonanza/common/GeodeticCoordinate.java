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
package ucf.chickenzombiebonanza.common;

public class GeodeticCoordinate {
	private double latitude, longitude, altitude;
	
	/**
	 * Constructor
	 * 
	 * @param latitude The latitude of the coordinate
	 * @param longitude The longitude of the coordinate
	 * @param altitude The altitude of the coordinate
	 */
	public GeodeticCoordinate(double latitude, double longitude, double altitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}
	
	/**
	 * Gets the latitude of the coordinate
	 * 
	 * @return double The latitude of the coordinate
	 */
	public double getLatitude() {
		return latitude;
	}
	
	/**
	 * Gets the longitude of the coordinate
	 * 
	 * @return double The longitude of the coordinate
	 */
	public double getLongitude() {
		return longitude;
	}
	
	/**
	 * Gets the altitude of the coordinate
	 * 
	 * @return double The altitude of the coordinate
	 */
	public double getAltitude() {
		return altitude;
	}
	
	/**
	 * Sets the latitude of the coordinate
	 * 
	 * @param latitude The latitude to be set to
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	/**
	 * Sets the longitude of the coordinate
	 * 
	 * @param longitude The longitude to be set to
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	/**
	 * Sets the altitude of the coordinate
	 * 
	 * @param altitude The altitude to be set to
	 */
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	
	/**
	 * Sets the coordinate to a new position
	 * 
	 * @param latitude The latitude to be set to
	 * @param longitude The longitude to be set to
	 * @param altitude The altitude to be set to
	 */
	public void setCoordinate(double latitude, double longitude, double altitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}
}
