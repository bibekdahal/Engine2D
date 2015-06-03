package com.bibek.engine2d;

public class RenderSystem extends System {
    public RenderSystem() {
        super(new Class[]{SpriteComponent.class, TransformationComponent.class});
    }

    @Override
    public void update(double dt) {
        for (Entity entity : mEntities) {
            SpriteComponent sc = entity.getComponent(SpriteComponent.class);
            SpriteComponent.SpriteSheetData ssd = sc.spriteSheetData;

            // animate a sprite sheet by advancing the image index when required time has elapsed
            if (ssd != null && ssd.animationSpeed > 0) {
                ssd.timePassed += (float) dt;
                if (ssd.timePassed >= 1.0/ssd.animationSpeed) {
                    ssd.timePassed = 0;
                    ssd.index = (ssd.index+1)%(ssd.numCols*ssd.numRows);
                }
            }
        }
    }

    @Override
    public void draw() {
        for (Entity entity : mEntities) {
            SpriteComponent sc = entity.getComponent(SpriteComponent.class);
            TransformationComponent tc = entity.getComponent(TransformationComponent.class);

            if (sc.sprite == null)
                continue;;

            if (sc.spriteSheetData == null)
                sc.sprite.draw(tc.x, tc.y, tc.angle);
            else {
                SpriteComponent.SpriteSheetData ssd = sc.spriteSheetData;

                int col = ssd.index % ssd.numCols;
                int row = ssd.index / ssd.numCols;

                float clipX = (ssd.imgWidth + ssd.hSpacing) * col + ssd.offsetX;
                float clipY = (ssd.imgHeight + ssd.vShacing) * row + ssd.offsetY;

                clipX /= sc.sprite.mTexture.width;
                clipY /= sc.sprite.mTexture.height;

                float clipW = ssd.imgWidth / sc.sprite.mTexture.width;
                float clipH = ssd.imgHeight / sc.sprite.mTexture.height;

                sc.sprite.draw(tc.x, tc.y, tc.angle, clipX, clipY, clipW, clipH);
            }
        }
    }
}
