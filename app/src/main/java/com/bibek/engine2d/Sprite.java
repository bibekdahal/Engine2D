package com.bibek.engine2d;

import android.opengl.GLES20;

public class Sprite {

    // A reference to the renderer
    private final GLRenderer mRenderer;

    // The color to draw this sprite: yellow by default
    private float[] mColor = new float[]{ 1, 1, 0, 1 };

    // position and size of the sprite
    public float mX, mY, mW, mH;

    Sprite(GLRenderer renderer) { mRenderer = renderer; }

    // initialization code
    public void init(float x, float y, float w, float h) {
        mX = x; mY = y;
        mW = w; mH = h;
    }

    // draw the sprite
    public void draw() {
        mRenderer.setSpriteTransform(mX, mY, mW, mH);
        GLES20.glUniform4fv(mRenderer.mColorHandle, 1, mColor, 0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mRenderer.indexBuffer);
    }
}
