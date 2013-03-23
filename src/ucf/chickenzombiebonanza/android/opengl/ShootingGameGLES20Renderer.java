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
package ucf.chickenzombiebonanza.android.opengl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ucf.chickenzombiebonanza.android.R;
import ucf.chickenzombiebonanza.android.ShootingGameActivity;
import ucf.chickenzombiebonanza.common.GeocentricCoordinate;
import ucf.chickenzombiebonanza.common.LocalOrientation;
import ucf.chickenzombiebonanza.common.Matrix44d;
import ucf.chickenzombiebonanza.game.entity.GameEntity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

/**
 * 
 */
public class ShootingGameGLES20Renderer implements GLSurfaceView.Renderer {

    private int simpleProgram, billboardProgram;
    private int mvpMatrixSimpleHandle, projMatrixBillboardHandle, viewMatrixBillboardHandle, modelMatrixBillboardHandle;
    private int samplerBillboardHandle;
    private int positionSimpleHandle, positionBillboardHandle;
    private int colorSimpleHandle, colorBillboardHandle;
    private int textureBillboardHandle;
    
    private float[] mMVPMatrix = new float[16];
    private float[] mVMatrix = new float[16];
    private float[] mProjMatrix = new float[16];
    
    private int numPoints = 0;

    private FloatBuffer floorVertexBuffer, floorColorBuffer, billboardVertexBuffer, billboardColorBuffer, billboardTextureBuffer;
    
    int[] textures = new int[2];
    
    private final ShootingGameActivity shootingGameContext;
    
    public ShootingGameGLES20Renderer(ShootingGameActivity shootingGameContext) {
        this.shootingGameContext = shootingGameContext;
    }
    
    private int loadShaderFromFile(int type, int resourceId) {
        
        InputStream is = shootingGameContext.getResources().openRawResource(resourceId);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        
        StringBuilder shaderCode = new StringBuilder();
        String readLine;
        
        try {
            while ((readLine = br.readLine()) != null) {
                shaderCode.append(readLine);
                shaderCode.append('\n');
            }
        } catch (IOException e) {
        }

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);
        
        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode.toString());
        GLES20.glCompileShader(shader);

        return shader;
    }
    
    private static void gluPerspective(float[] matrix, float fovy, float aspect, float zNear, float zFar) {
        float top = zNear * (float) Math.tan(fovy * (Math.PI / 360.0));
        float bottom = -top;
        float left = bottom * aspect;
        float right = top * aspect;
        Matrix.frustumM(matrix, 0, left, right, bottom, top, zNear, zFar); 
    }
    
    private void initFloor() {
        
        float floorVertexArray[] = new float[]{
              0.0f,   0.0f, -1.0f, 1.0f,
              0.0f,  10.0f, -1.0f, 1.0f,
             10.0f,   0.0f, -1.0f, 1.0f,
              0.0f, -10.0f, -1.0f, 1.0f,
            -10.0f,   0.0f, -1.0f, 1.0f,
              0.0f,  10.0f, -1.0f, 1.0f,
        };

        ByteBuffer vbb = ByteBuffer.allocateDirect(floorVertexArray.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        floorVertexBuffer = vbb.asFloatBuffer();
        floorVertexBuffer.put(floorVertexArray);
        floorVertexBuffer.position(0);

        float floorColorArray[] = new float[]{
            0.2f, 0.6f, 0.0f, 1.0f,
            0.2f, 0.6f, 0.0f, 0.0f,
            0.2f, 0.6f, 0.0f, 0.0f,
            0.2f, 0.6f, 0.0f, 0.0f,
            0.2f, 0.6f, 0.0f, 0.0f,
            0.2f, 0.6f, 0.0f, 0.0f,
        };

        ByteBuffer cbb = ByteBuffer.allocateDirect(floorColorArray.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        floorColorBuffer = cbb.asFloatBuffer();
        floorColorBuffer.put(floorColorArray);
        floorColorBuffer.position(0);
        
        numPoints = 6;
    }
    
    private void initBillboard() {
        float billboardVertexArray[] = new float[]{
            0.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 0.5f, 1.0f,
            0.0f, 0.5f, 0.0f, 1.0f,
            0.0f, 0.5f, 0.5f, 1.0f,
      };
        
        ByteBuffer vbb = ByteBuffer.allocateDirect(billboardVertexArray.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        billboardVertexBuffer = vbb.asFloatBuffer();
        billboardVertexBuffer.put(billboardVertexArray);
        billboardVertexBuffer.position(0);

        float billboardColorArray[] = new float[]{
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
        };

        ByteBuffer cbb = ByteBuffer.allocateDirect(billboardColorArray.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        billboardColorBuffer = cbb.asFloatBuffer();
        billboardColorBuffer.put(billboardColorArray);
        billboardColorBuffer.position(0);
        
        float billboardTextureArray[] = new float[]{
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
        };

        ByteBuffer tbb = ByteBuffer.allocateDirect(billboardTextureArray.length * 4);
        tbb.order(ByteOrder.nativeOrder());
        billboardTextureBuffer = tbb.asFloatBuffer();
        billboardTextureBuffer.put(billboardTextureArray);
        billboardTextureBuffer.position(0);
        
        GLES20.glGenTextures(2, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        
        Bitmap bitmap = BitmapFactory.decodeResource(shootingGameContext.getResources(), R.drawable.chickenfrontimagetexture);
        
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bitmap.getWidth() * bitmap.getHeight() * 4);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        IntBuffer ib = byteBuffer.asIntBuffer();

        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for(int i=0; i<pixels.length; i++){
            ib.put(pixels[i] << 8 | pixels[i] >>> 24);
        }

        bitmap.recycle();

        byteBuffer.position(0);

        GLES20.glTexImage2D ( GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bitmap.getWidth(), bitmap.getHeight(), 0,
                              GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, byteBuffer );

        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR );
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR );
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE );
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE );
        
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[1]);
        
        bitmap = BitmapFactory.decodeResource(shootingGameContext.getResources(), R.drawable.chickensideimagetexture);
        
        byteBuffer = ByteBuffer.allocateDirect(bitmap.getWidth() * bitmap.getHeight() * 4);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        ib = byteBuffer.asIntBuffer();

        pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for(int i=0; i<pixels.length; i++){
            ib.put(pixels[i] << 8 | pixels[i] >>> 24);
        }

        bitmap.recycle();

        byteBuffer.position(0);

        GLES20.glTexImage2D ( GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bitmap.getWidth(), bitmap.getHeight(), 0,
                              GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, byteBuffer );

        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR );
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR );
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE );
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE );
        
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glClearColor(0f, 0f, 0f, 1.0f);

        int simpleVertexShader = loadShaderFromFile(GLES20.GL_VERTEX_SHADER, R.raw.simplevertshader);
        int simpleFragmentShader = loadShaderFromFile(GLES20.GL_FRAGMENT_SHADER, R.raw.simplefragshader);

        simpleProgram = GLES20.glCreateProgram(); // create empty OpenGL Program
        GLES20.glAttachShader(simpleProgram, simpleVertexShader); // add the vertex shader to program
        GLES20.glAttachShader(simpleProgram, simpleFragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(simpleProgram); // creates OpenGL program executables
                
        int billboardVertexShader = loadShaderFromFile(GLES20.GL_VERTEX_SHADER, R.raw.billboardvertshader);
        int billboardFragmentShader = loadShaderFromFile(GLES20.GL_FRAGMENT_SHADER, R.raw.billboardfragshader);
        
        billboardProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(billboardProgram, billboardVertexShader);
        GLES20.glAttachShader(billboardProgram, billboardFragmentShader);
        GLES20.glLinkProgram(billboardProgram);
        
        mvpMatrixSimpleHandle = GLES20.glGetUniformLocation(simpleProgram, "uMVPMatrix");
        positionSimpleHandle = GLES20.glGetAttribLocation(simpleProgram, "vPosition");
        colorSimpleHandle = GLES20.glGetAttribLocation(simpleProgram, "vColor");
        
        projMatrixBillboardHandle = GLES20.glGetUniformLocation(billboardProgram, "projMat");
        viewMatrixBillboardHandle = GLES20.glGetUniformLocation(billboardProgram, "viewMat");
        modelMatrixBillboardHandle = GLES20.glGetUniformLocation(billboardProgram, "modelMat");
        samplerBillboardHandle = GLES20.glGetUniformLocation(billboardProgram, "textureSampler");
        positionBillboardHandle = GLES20.glGetAttribLocation(billboardProgram, "vPosition");
        colorBillboardHandle = GLES20.glGetAttribLocation(billboardProgram, "vColor");
        textureBillboardHandle = GLES20.glGetAttribLocation(billboardProgram, "vTextureCoord");
        
        initFloor();
        
        initBillboard();
    }
    
    private void renderFloor() {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        
        GLES20.glUseProgram(simpleProgram);
        GLES20.glUniformMatrix4fv(mvpMatrixSimpleHandle, 1, false, mMVPMatrix, 0);
        
        GLES20.glVertexAttribPointer(positionSimpleHandle, 4, GLES20.GL_FLOAT, false, 16, floorVertexBuffer);
        GLES20.glEnableVertexAttribArray(positionSimpleHandle);
        GLES20.glVertexAttribPointer(colorSimpleHandle, 4, GLES20.GL_FLOAT, false, 16, floorColorBuffer);
        GLES20.glEnableVertexAttribArray(colorSimpleHandle);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, numPoints);
        
        GLES20.glDisable(GLES20.GL_BLEND);
    }
    
    private float[] getModelMatrix(GameEntity entity) {
        GeocentricCoordinate relative = entity.getPosition().relativeTo(shootingGameContext.getGameLocation());
        return new float[]{
            1.0f, 0.0f,  0.0f, 0.0f,
            0.0f, 1.0f,  0.0f, 0.0f,
            0.0f, 0.0f,  1.0f, 0.0f,
            (float)relative.getX(), (float)relative.getY(), -1.0f, 1.0f
        };
    }
    
    private void renderBillboard() {
        
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
                
        GLES20.glUseProgram(billboardProgram);
        GLES20.glUniformMatrix4fv(projMatrixBillboardHandle, 1, false, mProjMatrix, 0);
        GLES20.glUniformMatrix4fv(viewMatrixBillboardHandle, 1, false, mVMatrix, 0);
        GLES20.glUniform1i(samplerBillboardHandle, 0);
        
        GLES20.glVertexAttribPointer(positionBillboardHandle, 4, GLES20.GL_FLOAT, false, 16, billboardVertexBuffer);
        GLES20.glEnableVertexAttribArray(positionBillboardHandle);
        GLES20.glVertexAttribPointer(colorBillboardHandle, 4, GLES20.GL_FLOAT, false, 16, billboardColorBuffer);
        GLES20.glEnableVertexAttribArray(colorBillboardHandle);
        GLES20.glVertexAttribPointer(textureBillboardHandle, 2, GLES20.GL_FLOAT, false, 8, billboardTextureBuffer);
        GLES20.glEnableVertexAttribArray(textureBillboardHandle);
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[1]);
        
        synchronized (shootingGameContext.getGameEntities()) {        
            for(GameEntity i : shootingGameContext.getGameEntities()) {
                float[] modelMatrix = getModelMatrix(i);
                
                GLES20.glUniformMatrix4fv(modelMatrixBillboardHandle, 1, false, modelMatrix, 0);
    
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            }
        }
        
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        
        GLES20.glDisable(GLES20.GL_BLEND);
    }
	
    @Override
    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
        
        renderFloor();
        
        renderBillboard();
        
        GLES20.glUseProgram(0);
    }

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		
		float ratio = (float) width / height;
        
		gluPerspective(mProjMatrix, 45.0f, ratio, 0.5f, 20.0f);
	}

    public void receiveOrientationUpdate(LocalOrientation orientation) {
    	double[] lookAtMatrix = Matrix44d.lookAt(orientation.getLookAt(), orientation.getUp()).getMatrix();
    	mVMatrix = Matrix44d.doubleArrayToFloatArray(lookAtMatrix);
    }

}
