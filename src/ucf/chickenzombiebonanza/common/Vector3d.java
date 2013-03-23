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

/**
 * 
 */
public class Vector3d {

    private double u, v, w;

    public Vector3d() {
        this.u = 0;
        this.v = 0;
        this.w = 0;
    }

    public Vector3d(double u, double v, double w) {
        this.u = u;
        this.v = v;
        this.w = w;
    }

    public double u() {
        return u;
    }

    public double v() {
        return v;
    }

    public double w() {
        return w;
    }
    
    public double magnitude() {
        return Math.sqrt(Math.pow(u, 2)+Math.pow(v, 2)+Math.pow(w, 2));
    }
    
    public Vector3d add(Vector3d rhs) {
    	return new Vector3d(u()+rhs.u(),v()+rhs.v(),w()+rhs.w());
    }
    
    public Vector3d scale(double val) {
        return new Vector3d(val*u(),val*v(),val*w());
    }
    
    public Vector3d normalize() {
        double mag = this.magnitude();
        return this.scale(1/mag);
    }
    
    public double dotProduct(Vector3d rhs) {
    	return u()*rhs.u() + v()*rhs.v() + w()*rhs.w();
    }
    
    public Vector3d crossProduct(Vector3d rhs) {
    	return new Vector3d(v()*rhs.w()-w()*rhs.v(),
    						w()*rhs.u()-u()*rhs.w(),
    						u()*rhs.v()-v()*rhs.u());
    }
    
    public String toString() {
        return "Vector3d[" + u + ", " + v + ", " + w + "]";
    }
}
