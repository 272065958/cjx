package com.model.cjx.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.model.cjx.R;
import com.model.cjx.util.Tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by cjx on 2016/9/1.
 */
public class CameraActivity extends BaseActivity implements SurfaceHolder.Callback, Camera.PictureCallback, Camera.AutoFocusCallback {
    final int REQUEST_CAMERA_PERMISSION = 2;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;

    Camera camera;
    int cameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        if (!checkCamerahardware()) {
            finish();
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return ;
        }
        camera = getCameraInstance();
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "无法使用相机功能", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                camera = getCameraInstance();
                surfaceView = (SurfaceView) findViewById(R.id.surface_view);
                surfaceHolder = surfaceView.getHolder();
                surfaceHolder.addCallback(this);
                surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            }
        }
    }

    @Override
    protected void onPause() {
        releaseCamera();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (camera == null) {
            camera = getCameraInstance();
            if (camera != null) {
                try {
                    camera.setPreviewDisplay(surfaceHolder);
                    camera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onClick(View view) {
        if (camera != null) {
            camera.autoFocus(this);
        }
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (success) {
            camera.takePicture(null, null, this);
        } else {
            camera.autoFocus(this);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("TAG", "camera surfaceCreated");
//        if (camera == null) {
//            return;
//        }
//        try {
//            initCameraParam(surfaceView.getWidth(), surfaceView.getHeight());
//            camera.setPreviewDisplay(surfaceHolder);
//            camera.startPreview();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        try {
            if (camera != null) {
                camera.stopPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            initCameraParam(width, height);
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        String action = getIntent().getAction();
        File mediaFile;
        if (action != null) {
            mediaFile = new File(getIntent().getAction());
        } else {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            mediaFile = new File(Tools.getTempPath(this), "IMG_" + timeStamp + ".jpg");
        }
        // Create a media file name

        if (mediaFile == null) {
            Log.e("TAG", "create pic file error");
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(mediaFile);
            fos.write(data);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setResult(RESULT_OK, new Intent(mediaFile.getAbsolutePath()));
        finish();
    }

    private void releaseCamera() {
        // stop and release camera
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    private Camera getCameraInstance() {
        Camera c = null;
        try {
            int cameraId = findBackFacingCamera();
            c = Camera.open(cameraId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    private void initCameraParam(int width, int height) {
        setCameraDisplayOrientation(cameraId, camera);

        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        List<Camera.Size> supportSizes = parameters.getSupportedPictureSizes();
        for (Camera.Size cs : supportSizes) {
            if(width * 0.1f / cs.height == height * 0.1f / cs.width){
                parameters.setPictureSize(cs.width, cs.height);
                break;
            }
        }
        camera.setParameters(parameters);
    }

    // 判断是否有摄像头
    private boolean checkCamerahardware() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private int findBackFacingCamera() {
        int cameraId = 0;
        //Search for the back facing camera
        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    public void setCameraDisplayOrientation(int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result = (info.orientation - degrees + 360) % 360;
        camera.setDisplayOrientation(result);
    }
}
