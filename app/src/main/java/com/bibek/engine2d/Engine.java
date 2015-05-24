package com.bibek.engine2d;

import android.opengl.GLES20;

public class Engine implements TimerCallback {
    // A reference to the renderer
    private final GLRenderer mRenderer;

    // Test sprite
    Sprite testSprite;

    // Timer with 60 FPS as target
    Timer mTimer = new Timer(60.0);

    public Engine(GLRenderer renderer) {
        mRenderer = renderer;
    }

    // called on surface creation
    public void init() {
        // Initialize the test sprite with android launcher icon as texture at position (50, 50) with size(200, 200)
        testSprite = new Sprite(mRenderer);
        testSprite.init(R.mipmap.ic_launcher, 50, 50, 50, 50);
    }

    // called on each frame
    public void newFrame() {
        // update with 1/60 as deltaTime (60 FPS)
        mTimer.Update(this);
        draw();
    }

    // update method for updating game logic and animations, which are time dependent
    @Override
    public void update(double deltaTime) {
        // Animate the sprite by moving it across screen
        // Always use delta-time to create animations and simulations
        testSprite.mX = (testSprite.mX + (float)deltaTime * 100) % (mRenderer.width+50);
    }

    // draw method for all rendering operations
    public void draw() {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Draw the test sprite
        testSprite.draw();
    }
}
