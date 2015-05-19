package com.bibek.engine2d;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class SurfaceView extends GLSurfaceView {

    public SurfaceView(Context context){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(new GLRenderer(context));
    }
}