package com.bibek.engine2d;

import android.opengl.GLES20;

public class Engine {
    // A reference to the renderer
    private final GLRenderer mRenderer;

    // Test sprite
    Sprite testSprite;

    public Engine(GLRenderer renderer) {
        mRenderer = renderer;
    }

    // called on surface creation
    public void init() {
        // Initialize the test sprite with android launcher icon as texture at position (50, 50) with size(200, 200)
        testSprite = new Sprite(mRenderer);
        testSprite.init(R.mipmap.ic_launcher, 50, 50, 200, 200);
    }

    // called on each frame
    public void newFrame() {
        // update with 1/60 as deltaTime (60 FPS)
        update(1.0/60.0);
        draw();
    }

    // update method for updating game logic and animations, which are time dependent
    public void update(double deltaTime) {

    }

    // draw method for all rendering operations
    public void draw() {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Draw the test sprite
        testSprite.draw();
    }
}
