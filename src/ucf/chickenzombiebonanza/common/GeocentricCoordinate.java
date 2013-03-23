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

import java.util.Random;

/**
 * Represents a coordinate in the GCC coordinate space.
 * 
 * @author jleonard
 */
public class GeocentricCoordinate {
	private double x, y, z;
	
	/**
	 * Default Constructor.
	 */
	public GeocentricCoordinate() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param x The X component of the coordinate.
	 * @param y The Y component of the coordinate.
	 * @param z The Z component of the coordinate.
	 */
	public GeocentricCoordinate(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public GeocentricCoordinate(GeocentricCoordinate coordinate) {
		this.x = coordinate.getX();
		this.y = coordinate.getY();
		this.z = coordinate.getZ();
	}
	
	/**
	 * Gets the X component of the coordinate.
	 * 
	 * @return double The X component of the coordinate.
	 */
	public double getX() {
		return x;
	}
	
	/**
	 * Gets the Y component of the coordinate.
	 * 
	 * @return double The Y component of the coordinate.
	 */
	public double getY() {
		return y;
	}
	
	/**
	 * Gets the Z component of the coordinate.
	 * 
	 * @return double The Z component of the coordinate.
	 */
	public double getZ() {
		return z;
	}
	
	/**
	 * Sets the X component of the coordinate.
	 * 
	 * @param x The X component to be set to.
	 */
	public void setX(double x) {
		this.x = x;
	}
	
	/**
	 * Sets the Y component of the coordinate.
	 * 
	 * @param y The Y component to be set to.
	 */
	public void setY(double y) {
		this.y = y;
	}
	
	/**
	 * Sets the Z component of the coordinate.
	 * 
	 * @param z The Z component to be set to.
	 */
	public void setZ(double z) {
		this.z = z;
	}
	
	/**
	 * Sets the coordinate to a new position.
	 * 
	 * @param x The X component to be set to.
	 * @param y The Y component to be set to.
	 * @param z The Z component to be set to.
	 */
	public void setCoordinate(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public boolean isZero() {
	    return getX() == 0 && getY() == 0 && getZ() == 0;
	}
	
	public Vector3d toVector() {
	    return new Vector3d(getX(),getY(),getZ());
	}
	
	public GeocentricCoordinate applyOffset(Vector3d vec) {
	    return new GeocentricCoordinate(this.getX()+vec.u(),
	                                    this.getY()+vec.v(),
	                                    this.getZ()+vec.w());
	}
	
	public GeocentricCoordinate relativeTo(GeocentricCoordinate center) {
	    return new GeocentricCoordinate(center.getX()-this.getX(),
	                                    center.getY()-this.getY(),
	                                    center.getZ()-this.getZ());
	}
	
	/**
	 * Gets the distance between this coordinate and another.
	 * @param coordinate The other coordinate.
	 * @return The distance between this coordinate and the other.
	 */
	public double distanceFrom(GeocentricCoordinate coordinate) {
		return Math.sqrt(Math.pow(coordinate.getX()-this.getX(), 2)+
				Math.pow(coordinate.getY()-this.getY(), 2) +
				Math.pow(coordinate.getZ()-this.getZ(), 2));
	}
	
	public static GeocentricCoordinate randomPointAround(final GeocentricCoordinate pt, double maxRadius, double minRadius) {
	    Random generator = new Random();
	    double angle = generator.nextDouble()*(2.0*Math.PI);
	    double distance = (generator.nextDouble()*(maxRadius-minRadius))+minRadius;
	    
	    Vector3d upVector = pt.toVector().normalize();
	    
	    Vector3d surfaceVectorOne = new Vector3d(-upVector.v(),upVector.u(),upVector.w());
	    Vector3d surfaceVectorTwo = new Vector3d(upVector.w(),upVector.v(),-upVector.u());
	    
	    surfaceVectorOne = surfaceVectorOne.scale(Math.cos(angle));
	    surfaceVectorTwo = surfaceVectorTwo.scale(Math.sin(angle));
	    
	    Vector3d offsetVector = surfaceVectorOne.add(surfaceVectorTwo).scale(distance);
	    
	    return pt.applyOffset(offsetVector);	    
	}
	
	@Override
	public String toString() {
	    return new String("GeodeticCoordinate["+getX()+", " + getY() + ", " + getZ() + "]");
	    
	}
}
