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

public class Matrix44d {
	
	private final double matrix[] = new double[16];
	
	public Matrix44d() {
		
	}
	
	public void set(int row, int col, double val) {
		matrix[(4*row) + col] = val;
	}
	
	public static Matrix44d lookAt(Vector3d lookAt, Vector3d up) {
		Vector3d right = up.crossProduct(lookAt);
		Matrix44d matrix = new Matrix44d();
		matrix.set(0, 0, right.u());
		matrix.set(1, 0, right.v());
		matrix.set(2, 0, right.w());
		matrix.set(0, 1, up.u());
		matrix.set(1, 1, up.v());
		matrix.set(2, 1, up.w());
		matrix.set(0, 2, lookAt.u());
		matrix.set(1, 2, lookAt.v());
		matrix.set(2, 2, lookAt.w());
		matrix.set(3, 3, 1);
		return matrix;
	}

	public double[] getMatrix() {
		return matrix;
	}
	
	public double[] getMatrixOpenGLFormat() {
		return new double[]{matrix[0],matrix[4],matrix[8],matrix[12],
		                    matrix[1],matrix[5],matrix[9],matrix[13],
		                    matrix[2],matrix[6],matrix[10],matrix[14],
		                    matrix[3],matrix[7],matrix[11],matrix[15]};
	}
	
	public static float[] doubleArrayToFloatArray(double[] array) {
		float[] farray = new float[array.length];
		for(int i = 0; i < farray.length; ++i) {
			farray[i] = (float)array[i];
		}
		return farray;
	}

}
