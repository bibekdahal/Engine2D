package com.bibek.engine2d;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
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

    // Test sprite
    Sprite testSprite = new Sprite(this);

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);

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

        // Initialize the test sprite at position (50, 50) with size(200, 200)
        testSprite.init(50, 50, 200, 200);

    }

    @Override
    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Draw the test sprite
        testSprite.draw();
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        // Projection is orthographic
        Matrix.orthoM(mProjectionMatrix, 0, 0, width, height, 0, -100, 100);
    }

    // Set transform for drawing the rectangle.
    public void setSpriteTransform(float posX, float posY, float scaleX, float scaleY) {
        // calculate transformation matrix mMVPMatrix that will transform the vertices of the square

        // mModelMatrix = Translate(posX, posY) * Scale(scaleX, scaleY)
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, posX, posY, 0);
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

}
