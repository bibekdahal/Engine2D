package com.bibek.engine2d;

import android.opengl.GLES20;

public class Sprite {
    // A reference to the renderer
    private final GLRenderer mRenderer;

    // The texture to draw this sprite
    private int mTexture;

    // position and size of the sprite
    public float mX, mY, mW, mH;

    Sprite(GLRenderer renderer) { mRenderer = renderer; }

    // initialization code
    public void init(int textureResourceId, float x, float y, float w, float h) {
        mX = x; mY = y;
        mW = w; mH = h;
        mTexture = mRenderer.loadTexture(textureResourceId);
    }

    // draw the sprite
    public void draw() {
        mRenderer.setSpriteTransform(mX, mY, mW, mH);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);  // sample-0
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mRenderer.indexBuffer);
    }
}
