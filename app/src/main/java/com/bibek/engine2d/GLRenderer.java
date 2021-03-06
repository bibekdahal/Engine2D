package com.bibek.engine2d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer {

    // We store the context of the MainActivity, which can be useful
    // in many Android functions
    Context mContext;
    GLRenderer(Context context) { mContext = context; }

    // GLSL program object
    public int mProgram;

    // Uniform handles
    public int mColorHandle;
    public int mMVPMatrixHandle;
    public int mClipHandle;

    // vertex buffer data
    private static float squareCoords[] = {
            0, 1,
            0, 0,
            1, 0,
            1, 1,
    };
    // index buffer data
    private static short drawOrder[] = { 0, 2, 1, 0, 3, 2 };
    //vertex and index buffer objects
    public FloatBuffer vertexBuffer;
    public ShortBuffer indexBuffer;

    // Transformation matrices
    public final float[] mMVPMatrix = new float[16];
    public final float[] mProjectionMatrix = new float[16];
    public final float[] mModelMatrix = new float[16];

    // The main engine
    private Engine mEngine = new Engine(this);

    public Texture mWhiteTexture;

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Enable blending for transparency
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // Enable scissoring
        GLES20.glEnable(GLES20.GL_SCISSOR_TEST);

        // Set the background frame color
        GLES20.glClearColor(100.0f/255, 149.0f/255, 237.0f/255, 1.0f);

        // Compile the sprite shaders
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, getRawFileText(R.raw.vs_sprite));
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, getRawFileText(R.raw.fs_sprite));

        // Link the shaders to a program
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
        GLES20.glUseProgram(mProgram);

        // Get the attribute and uniform handles
        int positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "uColor");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mClipHandle = GLES20.glGetUniformLocation(mProgram, "uClip");

        // Create the vertex buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length*4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // Create the index buffer
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        indexBuffer = dlb.asShortBuffer();
        indexBuffer.put(drawOrder);
        indexBuffer.position(0);

        // point the vertex-position attribute to the vertex buffer data
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 2*4, vertexBuffer);

        // get the texture uniform handle and set it to use the sample-0
        int texHandle = GLES20.glGetUniformLocation(mProgram, "uTexture");
        GLES20.glUniform1i(texHandle, 0);

        mWhiteTexture = loadTexture(R.drawable.white);

        // Initialize the Engine
        mEngine.init();
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        mEngine.newFrame();
    }

    public final int width = 480, height = 320;

    @Override
    public void onSurfaceChanged(GL10 unused, int dev_width, int dev_height) {
        float ar = (float)dev_width/(float)dev_height;
        float cx = 0, cy = 0, scale;

        // Fit one dimension and maintain the ratio with other dimension
        float aspect_ratio = (float) width / (float) height;
        // if aspect-ratio of device is greater than what is desired, fit the height
        if (ar > aspect_ratio) {
            scale = (float)dev_height/(float)height;
            cx = (dev_width - width*scale)/2.0f;
        }
        // otherwise fit the width
        else {
            scale = (float)dev_width/(float)width;
            cy = (dev_height - height*scale)/2.0f;
        }

        // Set the viewport
        GLES20.glViewport((int) cx, (int) cy, (int) (width * scale), (int) (height * scale));

        // Set the orthographic projection matrix.
        Matrix.orthoM(mProjectionMatrix, 0, 0, width, height, 0, -100, 100);

        // Scissor the viewport
        GLES20.glScissor((int)cx, (int)cy, (int)(width*scale), (int)(height*scale));
    }

    // Set transform for drawing the rectangle.
    public void setSpriteTransform(float posX, float posY, float scaleX, float scaleY, float angle, float originX, float originY) {
        // calculate transformation matrix mMVPMatrix that will transform the vertices of the square

        // mModelMatrix = Translate(posX, posY) * Rotate(angle) * Translate(-originX, -originY) * Scale(scaleX, scaleY)
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, posX, posY, 0);
        Matrix.rotateM(mModelMatrix, 0, angle, 0, 0, 1);
        Matrix.translateM(mModelMatrix, 0, -originX, -originY, 0);
        Matrix.scaleM(mModelMatrix, 0, scaleX, scaleY, 1);

        // mMVPMatrix = mProjectionMatrix * mModelMatrix
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mModelMatrix, 0);

        // finally set the value of mvpMatrix uniform to the mMVPMatrix
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
    }

    // Read text from a raw resource file
    public String getRawFileText(int rawResId) {
        InputStream inputStream  = mContext.getResources().openRawResource(rawResId);
        String s = new java.util.Scanner(inputStream).useDelimiter("\\A").next();

        try {
            inputStream.close();
        } catch (IOException e) {
            Log.e("Raw File read Error", e.getMessage());
        }
        return s;
    }

    // Create shader object from shader program
    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    // load texture from a resource file
    public Texture loadTexture(int resourceId)
    {
        int[] textureHandle = new int[1];
        int width, height;
        GLES20.glGenTextures(1, textureHandle, 0);
        if (textureHandle[0] != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resourceId);
            if (bitmap == null) {
                throw new RuntimeException("Error decoding bitmap");
            }
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            width = bitmap.getWidth();
            height = bitmap.getHeight();
            bitmap.recycle();
        }
        else {
            throw new RuntimeException("Error loading texture.");
        }

        return new Texture(textureHandle[0], width, height);
    }
}
