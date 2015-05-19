package com.bibek.engine2d;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    private SurfaceView mSurfaceView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSurfaceView = new SurfaceView(this);
        setContentView(mSurfaceView);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSurfaceView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSurfaceView.onResume();
    }
}