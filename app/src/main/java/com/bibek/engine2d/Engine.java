package com.bibek.engine2d;

import android.opengl.GLES20;

public class Engine implements TimerCallback {
    // A reference to the renderer
    private final GLRenderer mRenderer;
    // Timer with 60 FPS as target
    private Timer mTimer = new Timer(60.0);

    // The list of all system
    private System[] mSystems = new System[] { new RenderSystem() };
    // Method to add entity to all valid systems
    public void addEntity(Entity entity) {
        for (System system : mSystems) {
            system.addEntity(entity);
        }
    }

    // The test entities
    private Entity testEntity1 = new Entity(), testEntity2 = new Entity();

    public Engine(GLRenderer renderer) {
        mRenderer = renderer;
    }

    // called on surface creation
    public void init() {

        // Create first test entity with an yellow sprite and rotated initial transformation
        testEntity1.addComponent(
                new SpriteComponent(new Sprite(mRenderer, null, new float[]{1, 1, 0, 1}, 64, 128))
        );
        testEntity1.addComponent(
                new TransformationComponent(50, 50, 20)
        );
        addEntity(testEntity1);

        // Create second test entity with animated sprite-sheet
        // The sprite-sheet data has been obtained with the help of an image editing program
        SpriteComponent.SpriteSheetData ssd = new SpriteComponent.SpriteSheetData();
        ssd.offsetX = 40;
        ssd.hSpacing = 150-124;
        ssd.numCols =  5;
        ssd.imgWidth = 124-40;
        ssd.imgHeight = 106;
        ssd.index = 1;
        ssd.animationSpeed = 6;
        testEntity2.addComponent(
                new SpriteComponent(
                        new Sprite(mRenderer, mRenderer.loadTexture(R.drawable.test_spr), 124-40, 106), ssd
                )
        );
        testEntity2.addComponent(
                new TransformationComponent(150, 150, 0)
        );
        addEntity(testEntity2);

        // initialize all systems
        for (System system : mSystems) {
            system.init();
        }
    }

    // called on each frame
    public void newFrame() {
        mTimer.Update(this);
        draw();
    }

    // update method for updating game logic and animations, which are time dependent
    @Override
    public void update(double deltaTime) {
        // update all systems
        for (System system : mSystems) {
            system.update(deltaTime);
        }
    }

    // draw method for all rendering operations
    public void draw() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // call draw method of all systems
        for (System system : mSystems) {
            system.draw();
        }
    }
}
