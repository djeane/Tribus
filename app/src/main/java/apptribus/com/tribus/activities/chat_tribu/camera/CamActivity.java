package apptribus.com.tribus.activities.chat_tribu.camera;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.send_image.SendImageActivity;
import apptribus.com.tribus.activities.send_video.SendVideoActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.*;
import static apptribus.com.tribus.util.Constantes.TOPIC_KEY;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

public class CamActivity extends AppCompatActivity {

    //FIELDS
    @BindView(R.id.card_time)
    CardView mCardTime;

    @BindView(R.id.ib_video)
    FloatingActionButton mIbVideo;

    @BindView(R.id.ib_video_recording)
    FloatingActionButton mIbVideoRecording;

    @BindView(R.id.ib_photo)
    FloatingActionButton mIbPhoto;

    @BindView(R.id.texture)
    AutoFitTextureView mTextureView;

    @BindView(R.id.chronometer)
    Chronometer mChronometer;

    @BindView(R.id.arrow_back)
    ImageButton mBtnArrowBack;

    //VARIABLES VIDEO
    private static final String TAG = "CamTalkerActivity";
    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;
    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();

    private static final String FRAGMENT_DIALOG = "dialog";
    private static final int REQUEST_VIDEO_PERMISSIONS = 1;
    private static final String[] VIDEO_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    private CameraDevice mCameraDevice;
    private CameraCaptureSession mPreviewSession;
    private TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            synchronized (mCameraStateLock) {
                mPreviewSize = null;
            }
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private Size mPreviewSize;
    private Size mVideoSize;
    private MediaRecorder mMediaRecord;
    private boolean mIsRecordingVideo = false;
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    private static Integer mSensorOrientation;
    private String mNextVideoAbsolutePath;
    private File mImageFolder;
    private File mVideoFolder;
    private String mVideoFileName;
    private CaptureRequest.Builder mPreviewBuilder;


    //IMAGE RAW
    private static final int REQUEST_CAMERA_PERMISSIONS = 1;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    private static final long PRECAPTURE_TIMEOUT_MS = 1000;
    private static final double ASPECT_RATIO_TOLERANCE = 0.005;
    private static final int MAX_PREVIEW_WIDTH = 1920;
    private static final int MAX_PREVIEW_HEIGHT = 1080;
    private static final int STATE_CLOSED = 0;
    private static final int STATE_OPENED = 1;
    private static final int STATE_PREVIEW = 2;
    private static final int STATE_WAITING_FOR_3A_CONVERGENCE = 3;
    private OrientationEventListener mOrientationListener;

    private final AtomicInteger mRequestCounter = new AtomicInteger();
    private final Object mCameraStateLock = new Object();
    private String mCameraId;
    private CameraCaptureSession mCaptureSession;
    private CameraCharacteristics mCharacteristics;
    private RefCountedAutoCloseable<ImageReader> mJpegImageReader;
    private boolean mNoAFRun = false;
    private int mPendingUserCaptures = 0;
    private final TreeMap<Integer, ImageSaver.ImageSaverBuilder> mJpegResultQueue = new TreeMap<>();
    private final TreeMap<Integer, ImageSaver.ImageSaverBuilder> mRawResultQueue = new TreeMap<>();
    private int mState = STATE_CLOSED;
    private long mCaptureTimer;
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here if
            // the TextureView displaying this has been set up.
            //startPreview();
            synchronized (mCameraStateLock) {
                mState = STATE_OPENED;
                mCameraOpenCloseLock.release();
                mCameraDevice = cameraDevice;

                // Start the preview session if the TextureView has been set up already.
                if (mPreviewSize != null && mTextureView.isAvailable()) {
                    createCameraPreviewSessionLocked();
                }
            }
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            synchronized (mCameraStateLock) {
                mState = STATE_CLOSED;
                mCameraOpenCloseLock.release();
                cameraDevice.close();
                mCameraDevice = null;
            }
        }

        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            synchronized (mCameraStateLock) {
                mState = STATE_CLOSED;
                mCameraOpenCloseLock.release();
                cameraDevice.close();
                mCameraDevice = null;
            }
            finish();

        }

    };

    private final ImageReader.OnImageAvailableListener mOnJpegImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            dequeueAndSaveImage(mJpegResultQueue, mJpegImageReader);
        }

    };

    private CameraCaptureSession.CaptureCallback mPreCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            synchronized (mCameraStateLock) {
                switch (mState) {
                    case STATE_PREVIEW: {
                        // We have nothing to do when the camera preview is running normally.
                        break;
                    }
                    case STATE_WAITING_FOR_3A_CONVERGENCE: {
                        boolean readyToCapture = true;
                        if (!mNoAFRun) {
                            Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                            if (afState == null) {
                                break;
                            }

                            // If auto-focus has reached locked state, we are ready to capture
                            readyToCapture =
                                    (afState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED ||
                                            afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED);
                        }

                        // If we are running on an non-legacy device, we should also wait until
                        // auto-exposure and auto-white-balance have converged as well before
                        // taking a picture.
                        if (!isLegacyLocked()) {
                            Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                            Integer awbState = result.get(CaptureResult.CONTROL_AWB_STATE);
                            if (aeState == null || awbState == null) {
                                break;
                            }

                            readyToCapture = readyToCapture &&
                                    aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED &&
                                    awbState == CaptureResult.CONTROL_AWB_STATE_CONVERGED;
                        }

                        // If we haven't finished the pre-capture sequence but have hit our maximum
                        // wait timeout, too bad! Begin capture anyway.
                        if (!readyToCapture && hitTimeoutLocked()) {
                            readyToCapture = true;
                        }

                        if (readyToCapture && mPendingUserCaptures > 0) {
                            // Capture once for each user tap of the "Picture" button.
                            while (mPendingUserCaptures > 0) {
                                captureStillPictureLocked();
                                mPendingUserCaptures--;
                            }
                            // After this, the camera will go back to the normal state of preview.
                            mState = STATE_PREVIEW;
                        }
                    }
                }
            }
        }

        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request,
                                        CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
                                       TotalCaptureResult result) {
            process(result);
        }

    };

    private final CameraCaptureSession.CaptureCallback mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request,
                                     long timestamp, long frameNumber) {
            String currentDateTime = generateTimestamp();
            File rawFile = new File(Environment.
                    getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                    "RAW_" + currentDateTime + ".dng");

            File jpegFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            mImageFolder = new File(jpegFile, "Tribus Imagens");
            if (!mImageFolder.exists()) {
                mImageFolder.mkdirs();
            }

            String currentTimestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String prepend = "JPEG_" + currentTimestamp + "_";
            File imageFile = null;
            try {
                imageFile = File.createTempFile(prepend, ".jpg", mImageFolder);
                String mImageFileName = imageFile.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ImageSaver.ImageSaverBuilder jpegBuilder;
            ImageSaver.ImageSaverBuilder rawBuilder;
            int requestId = (int) request.getTag();
            synchronized (mCameraStateLock) {
                jpegBuilder = mJpegResultQueue.get(requestId);
                rawBuilder = mRawResultQueue.get(requestId);
            }

            if (jpegBuilder != null) jpegBuilder.setFile(imageFile);
            if (rawBuilder != null) rawBuilder.setFile(rawFile);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
                                       TotalCaptureResult result) {
            int requestId = (int) request.getTag();
            ImageSaver.ImageSaverBuilder jpegBuilder;
            ImageSaver.ImageSaverBuilder rawBuilder;
            StringBuilder sb = new StringBuilder();

            // Look up the ImageSaverBuilder for this request and update it with the CaptureResult
            synchronized (mCameraStateLock) {
                jpegBuilder = mJpegResultQueue.get(requestId);
                rawBuilder = mRawResultQueue.get(requestId);

                if (jpegBuilder != null) {
                    jpegBuilder.setResult(result);
                    sb.append("Saving JPEG as: ");
                    sb.append(jpegBuilder.getSaveLocation());
                }
                if (rawBuilder != null) {
                    rawBuilder.setResult(result);
                    if (jpegBuilder != null) sb.append(", ");
                    sb.append("Saving RAW as: ");
                    sb.append(rawBuilder.getSaveLocation());
                }

                // If we have all the results necessary, save the image to a file in the background.
                handleCompletionLocked(requestId, jpegBuilder, mJpegResultQueue);
                handleCompletionLocked(requestId, rawBuilder, mRawResultQueue);

                finishedCaptureLocked();
            }

            showToast(sb.toString());
        }

        @Override
        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request,
                                    CaptureFailure failure) {
            int requestId = (int) request.getTag();
            synchronized (mCameraStateLock) {
                mJpegResultQueue.remove(requestId);
                mRawResultQueue.remove(requestId);
                finishedCaptureLocked();
            }
            showToast("Falha na captura da imagem.");
        }

    };

    private final Handler mMessageHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            //openActivitySendImage(Uri.fromFile(mFile), fileSize, mContext);
            //Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_SHORT).show();
        }
    };


    private CaptureRequest.Builder mPreviewRequestBuilder;


    //METHODS
    //CHOOSE VIDEO SIZE - ESTÁ SENDO USADO L1498
    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;

            }
        }
        return choices[choices.length - 1];
    }

    //FOR INTENT EXTRA
    private static String mTribusKey;
    private static String mTopicKey;

    //ON CREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam);
        ButterKnife.bind(this);

        mTribusKey = getIntent().getExtras().getString(TRIBU_UNIQUE_NAME);
        mTopicKey = getIntent().getExtras().getString(TOPIC_KEY);

        createVideoFolder();
        mOrientationListener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (mTextureView != null && mTextureView.isAvailable()) {
                    configureTransform(mTextureView.getWidth(), mTextureView.getHeight());
                }
            }
        };

        //LISTENER TO VIDEO BUTTON
        mIbVideo.setOnClickListener(v -> {
            if (!mIsRecordingVideo) {
                startRecordingVideo();
                startChronometer();
            }
        });

        mIbVideoRecording.setOnClickListener(v -> {
            if (mIsRecordingVideo) {
                stopRecordingVideo();
                stopChronometer();
            }
        });

        //LISTENER TO PHOTO BUTTON
        mIbPhoto.setOnClickListener(v -> {
            takePicture();
        });

        mBtnArrowBack.setOnClickListener(v -> {
            finish();
        });
    }

    //ON RESUME
    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        openCameraImage();//PARA AS IMAGENS - TEM QUE FICAR AQUI, SENÃO NÃO APARECE A IMAGEM


        if (mTextureView.isAvailable()) {
            configureTransform(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
        if (mOrientationListener != null && mOrientationListener.canDetectOrientation()) {
            mOrientationListener.enable();
        }
    }

    private void startChronometer(){
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mCardTime.setVisibility(VISIBLE);
        mChronometer.start();
    }

    private void stopChronometer(){
        mChronometer.stop();
        mCardTime.setVisibility(INVISIBLE);
    }


    //ON PAUSE
    @Override
    protected void onPause() {
        if (mOrientationListener != null) {
            mOrientationListener.disable();
        }
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    //ON WINDOW FOCUS CHANGED
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if (hasFocus) {
            decorView.setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | SYSTEM_UI_FLAG_FULLSCREEN
                    | SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

    //START BACKGROUND THREAD
    private void startBackgroundThread() {

        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        synchronized (mCameraStateLock) {
            mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        }
    }

    //STOP BACKGROUND THREAD
    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            synchronized (mCameraStateLock) {
                mBackgroundHandler = null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //SHOULD SHOW REQUEST PERMISSION RATIONALE
    private boolean shouldShowRequestPermissionRationale(String[] permissions) {
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(permission)) {
                    return true;
                }
            } else {
                return true;
            }

        }
        return false;
    }

    //ON REQUEST PERMISSION RESULT
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_VIDEO_PERMISSIONS) {
            if (grantResults.length == VIDEO_PERMISSIONS.length) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        showMissingPermissionError();
                        return;
                        //ErrorDialog.newInstance(getString(R.string.permission_request))
                        //.show(getSupportFragmentManager(), FRAGMENT_DIALOG);
                    }
                }
            } else {
                ErrorDialog.buildErrorDialog(getString(R.string.permission_request))
                        .show(getSupportFragmentManager(), FRAGMENT_DIALOG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    //OPEN CAMERA
    //IMAGE
    @SuppressWarnings("MissingPermission")
    private void requestCameraPermissions() {
        if (shouldShowRationale()) {
            PermissionConfirmationDialog.newInstance().show(getSupportFragmentManager(), "dialog");
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(VIDEO_PERMISSIONS, REQUEST_CAMERA_PERMISSIONS);
            }
        }
    }

    private boolean shouldShowRationale() {
        for (String permission : VIDEO_PERMISSIONS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(permission)) {
                    return true;
                }
            }

        }
        return false;
    }

    private void showMissingPermissionError() {
        Toast.makeText(this, R.string.permission_request, Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean hasAllPermissionsGranted() {
        for (String permission : VIDEO_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("MissingPermission")
    private void openCameraImage() {
        if (!setUpCameraOutputs()) {
            return;
        }
        if (!hasAllPermissionsGranted()) {
            requestCameraPermissions();
            return;
        }

        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            // Wait for any previously running session to finish.
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }

            String cameraId;
            Handler backgroundHandler;
            synchronized (mCameraStateLock) {
                cameraId = mCameraId;
                backgroundHandler = mBackgroundHandler;
            }

            // Attempt to open the camera. mStateCallback will be called on the background handler's
            // thread when this succeeds or fails.
            mMediaRecord = new MediaRecorder();
            manager.openCamera(cameraId, mStateCallback, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }

    //CLOSE CAMERA
    private void closeCamera() {

        try {
            mCameraOpenCloseLock.acquire();
            synchronized (mCameraStateLock) {

                // Reset state and clean up resources used by the camera.
                // Note: After calling this, the ImageReaders will be closed after any background
                // tasks saving Images from these readers have been completed.
                mPendingUserCaptures = 0;
                mState = STATE_CLOSED;
                if (null != mCaptureSession) {
                    mCaptureSession.close();
                    mCaptureSession = null;
                }
                if (null != mCameraDevice) {
                    mCameraDevice.close();
                    mCameraDevice = null;
                }
                if (null != mJpegImageReader) {
                    mJpegImageReader.close();
                    mJpegImageReader = null;
                }
                if(null != mMediaRecord){
                    mMediaRecord.release();
                    mMediaRecord = null;
                }
                if(null != mPreviewSession){
                    mPreviewSession.close();
                    mPreviewSession = null;
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }

    }

    //UPDATE PREVIEW
    private void updatePreview() {
        if (mCameraDevice == null) {
            return;
        }
        try {
            setUpCaptureRequestBuilder(mPreviewBuilder);
            HandlerThread thread = new HandlerThread("CameraPreview");
            thread.start();
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //SET UP CAPTURE REQUEST BUILDER
    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    }

    //SET UP MEDIA RECORD
    private void setUpMediaRecord() throws IOException {
        //mMediaRecord = new MediaRecorder();
        mMediaRecord.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecord.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecord.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        if (mNextVideoAbsolutePath == null || mNextVideoAbsolutePath.isEmpty()) {
            mNextVideoAbsolutePath = getVideoFilePath(this);
        }
        mMediaRecord.setOutputFile(mNextVideoAbsolutePath);
        mMediaRecord.setVideoEncodingBitRate(10000000);
        mMediaRecord.setVideoFrameRate(30);
        mMediaRecord.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
        mMediaRecord.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecord.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        switch (mSensorOrientation) {
            case SENSOR_ORIENTATION_DEFAULT_DEGREES:
                mMediaRecord.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
                break;
            case SENSOR_ORIENTATION_INVERSE_DEGREES:
                mMediaRecord.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
                break;
        }
        mMediaRecord.prepare();

        mMediaRecord.start();
    }

    //CREATE VIDEO FOLDER
    private void createVideoFolder() {
        File movieFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        mVideoFolder = new File(movieFile, "Tribus Vídeos");
        if (!mVideoFolder.exists()) {
            mVideoFolder.mkdirs();
        }

    }

    //GET VIDEO FILE PATH
    private String getVideoFilePath(Context context) throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String prepend = "VIDEO_" + timestamp + "_";
        File videoFile = File.createTempFile(prepend, ".mp4", mVideoFolder);
        //File videoFile = File.createTempFile(prepend, ".m3u8", mVideoFolder);
        return mVideoFileName = videoFile.getAbsolutePath();
    }

    //START RECORDING VIDEO
    private void startRecordingVideo() {
        if (mCameraDevice == null || !mTextureView.isAvailable() || mPreviewSize == null) {
            return;
        }

        try {
            setUpMediaRecord();
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            //assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            List<Surface> surfaces = new ArrayList<>();

            // Set up Surface for the camera preview
            Surface previewSurface = new Surface(texture);
            surfaces.add(previewSurface);
            mPreviewBuilder.addTarget(previewSurface);

            // Set up Surface for the MediaRecorder
            Surface recorderSurface = mMediaRecord.getSurface();
            surfaces.add(recorderSurface);
            mPreviewBuilder.addTarget(recorderSurface);

            // Start a capture session
            // Once the session starts, we can update the UI and start recording
            mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    mPreviewSession = session;
                    updatePreview();
                    runOnUiThread(() -> {
                        //UI
                        mIsRecordingVideo = true;
                        mIbVideo.setVisibility(GONE);
                        mIbVideoRecording.setVisibility(VISIBLE);

                        //mIbVideo.setImageResource(R.drawable.ic_action_video_recording);

                    });
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            }, mBackgroundHandler);

        } catch (CameraAccessException | IOException e) {
            e.printStackTrace();
        }
    }

    //STOP RECORDING VIDEO
    private void stopRecordingVideo() {
        //UI
        mIsRecordingVideo = false;
        //mIbVideo.setImageResource(R.drawable.ic_action_video_record);
        mIbVideo.setVisibility(VISIBLE);
        mIbVideoRecording.setVisibility(GONE);

        File file = new File(mVideoFileName);
        int fileSize = Integer.parseInt(String.valueOf(file.length() / 1024));

        Intent mediaUpdateVideo = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(new File(mVideoFileName)), CamActivity.this, SendVideoActivity.class);

        mediaUpdateVideo.putExtra("video_size", fileSize);
        mediaUpdateVideo.putExtra(TRIBU_UNIQUE_NAME, mTribusKey);
        mediaUpdateVideo.putExtra(TOPIC_KEY, mTopicKey);
        mediaUpdateVideo.putExtra("cameFrom", "fromCamActivityChatTribu");
        startActivity(mediaUpdateVideo);
        finish();


        mNextVideoAbsolutePath = null;
        closeCamera();
        openCameraImage();
        this.finish();
    }

    //CLASS COMPARE SIZES BY AREA
    private static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    //FRAGMENTS
    public static class ErrorDialog extends DialogFragment {

        private String mErrorMessage;

        public ErrorDialog() {
            mErrorMessage = "Ocorreu um erro desconhecido!";
        }

        // Build a dialog with a custom message (Fragments require default constructor).
        public static ErrorDialog buildErrorDialog(String errorMessage) {
            ErrorDialog dialog = new ErrorDialog();
            dialog.mErrorMessage = errorMessage;
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(mErrorMessage)
                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> activity.finish())
                    .create();
        }
    }

    public static class ConfirmationDialog extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.permission_request)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        requestPermissions(VIDEO_PERMISSIONS,
                                REQUEST_VIDEO_PERMISSIONS);
                    })
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                        getActivity().finish();
                    })
                    .create();
        }
    }


    private boolean setUpCameraOutputs() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if (manager == null) {
            //ErrorDialog.buildErrorDialog("This device doesn't support Camera2 API.").
                    //show();
            return false;
        }
        try {
            // Find a CameraDevice that supports RAW captures, and configure state.
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics
                        = manager.getCameraCharacteristics(cameraId);

                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                // For still image captures, we use the largest available size.
                Size largestJpeg = Collections.max(
                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                        new CompareSizesByArea());

                synchronized (mCameraStateLock) {
                    // Set up ImageReaders for JPEG and RAW outputs.  Place these in a reference
                    // counted wrapper to ensure they are only closed when all background tasks
                    // using them are finished.
                    if (mJpegImageReader == null || mJpegImageReader.getAndRetain() == null) {
                        mJpegImageReader = new RefCountedAutoCloseable<>(
                                ImageReader.newInstance(largestJpeg.getWidth(),
                                        largestJpeg.getHeight(), ImageFormat.JPEG, /*maxImages*/5));
                    }
                    mJpegImageReader.get().setOnImageAvailableListener(
                            mOnJpegImageAvailableListener, mBackgroundHandler);

                    mCharacteristics = characteristics;
                    mCameraId = cameraId;
                }
                return true;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        //If we found no suitable cameras for capturing RAW, warn the user.
        ErrorDialog.buildErrorDialog("This device doesn't support capturing RAW photos").
                show(getSupportFragmentManager(), "dialog");
        return false;
    }

    private void createCameraPreviewSessionLocked() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder
                    = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mJpegImageReader.get().getSurface()),
                    //mRawImageReader.get().getSurface())
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                            synchronized (mCameraStateLock) {
                                // The camera is already closed
                                if (null == mCameraDevice) {
                                    return;
                                }

                                try {
                                    setup3AControlsLocked(mPreviewRequestBuilder);
                                    // Finally, we start displaying the camera preview.
                                    cameraCaptureSession.setRepeatingRequest(
                                            mPreviewRequestBuilder.build(),
                                            mPreCaptureCallback, mBackgroundHandler);
                                    mState = STATE_PREVIEW;
                                } catch (CameraAccessException | IllegalStateException e) {
                                    e.printStackTrace();
                                    return;
                                }
                                // When the session is ready, we start displaying the preview.
                                mCaptureSession = cameraCaptureSession;
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                            showToast("Falha ao configurar a câmera.");
                        }
                    }, mBackgroundHandler
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setup3AControlsLocked(CaptureRequest.Builder builder) {
        // Enable auto-magical 3A run by camera device
        builder.set(CaptureRequest.CONTROL_MODE,
                CaptureRequest.CONTROL_MODE_AUTO);

        Float minFocusDist =
                mCharacteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);

        // If MINIMUM_FOCUS_DISTANCE is 0, lens is fixed-focus and we need to skip the AF run.
        mNoAFRun = (minFocusDist == null || minFocusDist == 0);

        if (!mNoAFRun) {
            // If there is a "continuous picture" mode available, use it, otherwise default to AUTO.
            if (contains(mCharacteristics.get(
                    CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES),
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)) {
                builder.set(CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            } else {
                builder.set(CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_AUTO);
            }
        }

        // If there is an auto-magical flash control mode available, use it, otherwise default to
        // the "on" mode, which is guaranteed to always be available.
        if (contains(mCharacteristics.get(
                CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES),
                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)) {
            builder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        } else {
            builder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON);
        }

        // If there is an auto-magical white balance control mode available, use it.
        if (contains(mCharacteristics.get(
                CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES),
                CaptureRequest.CONTROL_AWB_MODE_AUTO)) {
            // Allow AWB to run auto-magically if this device supports this
            builder.set(CaptureRequest.CONTROL_AWB_MODE,
                    CaptureRequest.CONTROL_AWB_MODE_AUTO);
        }
    }

    private void configureTransform(int viewWidth, int viewHeight) {
        synchronized (mCameraStateLock) {
            if (null == mTextureView) {
                return;
            }

            if(mCharacteristics != null) {
                StreamConfigurationMap map = mCharacteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);


                // For still image captures, we always use the largest available size.
                Size largestJpeg = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                        new CompareSizesByArea());

                // Find the rotation of the device relative to the native device orientation.
                int deviceRotation = getWindowManager().getDefaultDisplay().getRotation();
                Point displaySize = new Point();
                getWindowManager().getDefaultDisplay().getSize(displaySize);

                // Find the rotation of the device relative to the camera sensor's orientation.
                int totalRotation = sensorToDeviceRotation(mCharacteristics, deviceRotation);

                // Swap the view dimensions for calculation as needed if they are rotated relative to
                // the sensor.
                boolean swappedDimensions = totalRotation == 90 || totalRotation == 270;
                int rotatedViewWidth = viewWidth;
                int rotatedViewHeight = viewHeight;
                int maxPreviewWidth = displaySize.x;
                int maxPreviewHeight = displaySize.y;

                if (swappedDimensions) {
                    rotatedViewWidth = viewHeight;
                    rotatedViewHeight = viewWidth;
                    maxPreviewWidth = displaySize.y;
                    maxPreviewHeight = displaySize.x;
                }

                // Preview should not be larger than display size and 1080p.
                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                    maxPreviewWidth = MAX_PREVIEW_WIDTH;
                }

                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                    maxPreviewHeight = MAX_PREVIEW_HEIGHT;
                }

                // Find the best preview size for these view dimensions and configured JPEG size.
                Size previewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                        rotatedViewWidth, rotatedViewWidth, maxPreviewWidth, maxPreviewHeight,
                        largestJpeg);

                //FINISH VIDEO CONFIGURATION INSIDE THIS METHOD

                if (swappedDimensions) {
                    mTextureView.setAspectRatio(
                            previewSize.getHeight(), previewSize.getWidth());
                } else {
                    mTextureView.setAspectRatio(
                            previewSize.getWidth(), previewSize.getHeight());
                }

                // Find rotation of device in degrees (reverse device orientation for front-facing
                // cameras).
                int rotation = (mCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                        CameraCharacteristics.LENS_FACING_FRONT) ?
                        (360 + ORIENTATIONS.get(deviceRotation)) % 360 :
                        (360 - ORIENTATIONS.get(deviceRotation)) % 360;

                Matrix matrix = new Matrix();
                RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
                RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
                float centerX = viewRect.centerX();
                float centerY = viewRect.centerY();

                // Initially, output stream images from the Camera2 API will be rotated to the native
                // device orientation from the sensor's orientation, and the TextureView will default to
                // scaling these buffers to fill it's view bounds.  If the aspect ratios and relative
                // orientations are correct, this is fine.
                //
                // However, if the device orientation has been rotated relative to its native
                // orientation so that the TextureView's dimensions are swapped relative to the
                // native device orientation, we must do the following to ensure the output stream
                // images are not incorrectly scaled by the TextureView:
                //   - Undo the scale-to-fill from the output buffer's dimensions (i.e. its dimensions
                //     in the native device orientation) to the TextureView's dimension.
                //   - Apply a scale-to-fill from the output buffer's rotated dimensions
                //     (i.e. its dimensions in the current device orientation) to the TextureView's
                //     dimensions.
                //   - Apply the rotation from the native device orientation to the current device
                //     rotation.
                if (Surface.ROTATION_90 == deviceRotation || Surface.ROTATION_270 == deviceRotation) {
                    bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
                    matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
                    float scale = Math.max(
                            (float) viewHeight / previewSize.getHeight(),
                            (float) viewWidth / previewSize.getWidth());
                    matrix.postScale(scale, scale, centerX, centerY);

                }
                matrix.postRotate(rotation, centerX, centerY);

                mTextureView.setTransform(matrix);

                //START VIDEO CONFIGURATION INSIDE THIS METHOD
                mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
                ///mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                ///     rotatedViewWidth, rotatedViewWidth, maxPreviewWidth, maxPreviewHeight, mVideoSize);

                ///int orientation = getResources().getConfiguration().orientation;
                ///if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                //} else {
                //  mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
                //}
                //configureTransform(mVideoSize.getWidth(), mVideoSize.getHeight());
                //mMediaRecord = new MediaRecorder();


                // Start or restart the active capture session if the preview was initialized or
                // if its aspect ratio changed significantly.
                if (mPreviewSize == null || !checkAspectsEqual(previewSize, mPreviewSize)) {
                    mPreviewSize = previewSize;
                    if (mState != STATE_CLOSED) {
                        createCameraPreviewSessionLocked();
                    }
                }
            }
        }
    }

    private void takePicture() {
        synchronized (mCameraStateLock) {
            mPendingUserCaptures++;

            // If we already triggered a pre-capture sequence, or are in a state where we cannot
            // do this, return immediately.
            if (mState != STATE_PREVIEW) {
                return;
            }

            try {
                // Trigger an auto-focus run if camera is capable. If the camera is already focused,
                // this should do nothing.
                if (!mNoAFRun) {
                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                            CameraMetadata.CONTROL_AF_TRIGGER_START);
                }

                // If this is not a legacy device, we can also trigger an auto-exposure metering
                // run.
                if (!isLegacyLocked()) {
                    // Tell the camera to lock focus.
                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                            CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START);
                }

                // Update state machine to wait for auto-focus, auto-exposure, and
                // auto-white-balance (aka. "3A") to converge.
                mState = STATE_WAITING_FOR_3A_CONVERGENCE;

                // Start a timer for the pre-capture sequence.
                startTimerLocked();

                // Replace the existing repeating request with one with updated 3A triggers.
                mCaptureSession.capture(mPreviewRequestBuilder.build(), mPreCaptureCallback,
                        mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void captureStillPictureLocked() {
        try {
            if (null == mCameraDevice) {
                return;
            }
            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);

            captureBuilder.addTarget(mJpegImageReader.get().getSurface());
            //captureBuilder.addTarget(mRawImageReader.get().getSurface());

            // Use the same AE and AF modes as the preview.
            setup3AControlsLocked(captureBuilder);

            // Set orientation.
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION,
                    sensorToDeviceRotation(mCharacteristics, rotation));

            // Set request tag to easily track results in callbacks.
            captureBuilder.setTag(mRequestCounter.getAndIncrement());

            CaptureRequest request = captureBuilder.build();

            // Create an ImageSaverBuilder in which to collect results, and add it to the queue
            // of active requests.
            ImageSaver.ImageSaverBuilder jpegBuilder = new ImageSaver.ImageSaverBuilder(this)
                    .setCharacteristics(mCharacteristics);
            //ImageSaver.ImageSaverBuilder rawBuilder = new ImageSaver.ImageSaverBuilder(activity)
                    //.setCharacteristics(mCharacteristics);

            mJpegResultQueue.put((int) request.getTag(), jpegBuilder);
            //mRawResultQueue.put((int) request.getTag(), rawBuilder);

            mCaptureSession.capture(request, mCaptureCallback, mBackgroundHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void finishedCaptureLocked() {
        try {
            // Reset the auto-focus trigger in case AF didn't run quickly enough.
            if (!mNoAFRun) {
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                        CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);

                mCaptureSession.capture(mPreviewRequestBuilder.build(), mPreCaptureCallback,
                        mBackgroundHandler);

                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                        CameraMetadata.CONTROL_AF_TRIGGER_IDLE);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void dequeueAndSaveImage(TreeMap<Integer, ImageSaver.ImageSaverBuilder> pendingQueue,
                                     RefCountedAutoCloseable<ImageReader> reader) {
        synchronized (mCameraStateLock) {
            Map.Entry<Integer, ImageSaver.ImageSaverBuilder> entry =
                    pendingQueue.firstEntry();
            ImageSaver.ImageSaverBuilder builder = entry.getValue();

            // Increment reference count to prevent ImageReader from being closed while we
            // are saving its Images in a background thread (otherwise their resources may
            // be freed while we are writing to a file).
            if (reader == null || reader.getAndRetain() == null) {
                //Log.e(TAG, "Paused the activity before we could save the image," +
                  //      " ImageReader already closed.");
                pendingQueue.remove(entry.getKey());
                return;
            }

            Image image;
            try {
                image = reader.get().acquireNextImage();
            } catch (IllegalStateException e) {
                //Log.e(TAG, "Too many images queued for saving, dropping image for request: " +
                  //      entry.getKey());
                pendingQueue.remove(entry.getKey());
                return;
            }

            builder.setRefCountedReader(reader).setImage(image);

            handleCompletionLocked(entry.getKey(), builder, pendingQueue);
        }
    }

    private static class ImageSaver implements Runnable {

        /**
         * The image to save.
         */
        private final Image mImage;
        /**
         * The file we save the image into.
         */
        private final File mFile;

        /**
         * The CaptureResult for this image capture.
         */
        private final CaptureResult mCaptureResult;

        /**
         * The CameraCharacteristics for this camera device.
         */
        private final CameraCharacteristics mCharacteristics;

        /**
         * The Context to use when updating MediaStore with the saved images.
         */
        private AppCompatActivity mContext;

        /**
         * A reference counted wrapper for the ImageReader that owns the given image.
         */
        private final RefCountedAutoCloseable<ImageReader> mReader;

        private ImageSaver(Image image, File file, CaptureResult result,
                           CameraCharacteristics characteristics, AppCompatActivity context,
                           RefCountedAutoCloseable<ImageReader> reader) {
            mImage = image;
            mFile = file;
            mCaptureResult = result;
            mCharacteristics = characteristics;
            mContext = context;
            mReader = reader;
        }

        @Override
        public void run() {
            boolean success = false;
            int format = mImage.getFormat();
            switch (format) {
                case ImageFormat.JPEG: {
                    ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    FileOutputStream output = null;
                    try {
                        output = new FileOutputStream(mFile);
                        output.write(bytes);
                        success = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        mImage.close();
                        closeOutput(output);
                    }
                    break;
                }
                default: {
                    //Log.e(TAG, "Cannot save image, unexpected image format:" + format);
                    break;
                }
            }

            // Decrement reference count to allow ImageReader to be closed to free up resources.
            mReader.close();

            // If saving the file succeeded, update MediaStore.
            if (success) {
                MediaScannerConnection.scanFile(mContext, new String[]{mFile.getPath()},
                /*mimeTypes*/null, new MediaScannerConnection.MediaScannerConnectionClient() {
                            @Override
                            public void onMediaScannerConnected() {
                                // Do nothing
                            }

                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                //Log.i(TAG, "Scanned " + path + ":");
                                //Log.i(TAG, "-> uri=" + uri);
                            }
                        });
                int fileSize = Integer.parseInt(String.valueOf(mFile.length() / 1024));

                openActivitySendImage(Uri.fromFile(mFile), fileSize, mContext);
            }


        }

        private void openActivitySendImage(Uri uri, int fileSize, AppCompatActivity mContext) {

            Intent mediaUpdateImage = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    uri, mContext, SendImageActivity.class);

            mediaUpdateImage.putExtra("image_size", fileSize);
            mediaUpdateImage.putExtra(TRIBU_UNIQUE_NAME, mTribusKey);
            mediaUpdateImage.putExtra(TOPIC_KEY, mTopicKey);
            mediaUpdateImage.putExtra("cameFrom", "fromCamActivityChatTribu");
            mContext.startActivity(mediaUpdateImage);
            mContext.finish();
        }

        public static class ImageSaverBuilder {
            private Image mImage;
            private File mFile;
            private CaptureResult mCaptureResult;
            private CameraCharacteristics mCharacteristics;
            private AppCompatActivity mContext;
            private RefCountedAutoCloseable<ImageReader> mReader;

            /**
             * Construct a new ImageSaverBuilder using the given {@link Context}.
             *
             * @param context a {@link Context} to for accessing the
             *                {@link android.provider.MediaStore}.
             */
            public ImageSaverBuilder(final AppCompatActivity context) {
                mContext = context;
            }

            public synchronized ImageSaverBuilder setRefCountedReader(
                    RefCountedAutoCloseable<ImageReader> reader) {
                if (reader == null) throw new NullPointerException();

                mReader = reader;
                return this;
            }

            public synchronized ImageSaverBuilder setImage(final Image image) {
                if (image == null) throw new NullPointerException();
                mImage = image;
                return this;
            }

            public synchronized ImageSaverBuilder setFile(final File file) {
                if (file == null) throw new NullPointerException();
                mFile = file;
                return this;
            }

            public synchronized ImageSaverBuilder setResult(final CaptureResult result) {
                if (result == null) throw new NullPointerException();
                mCaptureResult = result;
                return this;
            }

            public synchronized ImageSaverBuilder setCharacteristics(
                    final CameraCharacteristics characteristics) {
                if (characteristics == null) throw new NullPointerException();
                mCharacteristics = characteristics;
                return this;
            }

            public synchronized ImageSaver buildIfComplete() {
                if (!isComplete()) {
                    return null;
                }
                return new ImageSaver(mImage, mFile, mCaptureResult, mCharacteristics, mContext,
                        mReader);
            }

            public synchronized String getSaveLocation() {
                return (mFile == null) ? "Unknown" : mFile.toString();
            }

            private boolean isComplete() {
                return mImage != null && mFile != null && mCaptureResult != null
                        && mCharacteristics != null;
            }
        }
    }

    public static class RefCountedAutoCloseable<T extends AutoCloseable> implements AutoCloseable {
        private T mObject;
        private long mRefCount = 0;

        /**
         * Wrap the given object.
         *
         * @param object an object to wrap.
         */
        public RefCountedAutoCloseable(T object) {
            if (object == null) throw new NullPointerException();
            mObject = object;
        }

        /**
         * Increment the reference count and return the wrapped object.
         *
         * @return the wrapped object, or null if the object has been released.
         */
        public synchronized T getAndRetain() {
            if (mRefCount < 0) {
                return null;
            }
            mRefCount++;
            return mObject;
        }

        /**
         * Return the wrapped object.
         *
         * @return the wrapped object, or null if the object has been released.
         */
        public synchronized T get() {
            return mObject;
        }

        /**
         * Decrement the reference count and release the wrapped object if there are no other
         * users retaining this object.
         */
        @Override
        public synchronized void close() {
            if (mRefCount >= 0) {
                mRefCount--;
                if (mRefCount < 0) {
                    try {
                        mObject.close();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        mObject = null;
                    }
                }
            }
        }
    }

    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth,
                                          int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                        option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            //Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    private static String generateTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.US);
        return sdf.format(new Date());
    }

    private static void closeOutput(OutputStream outputStream) {
        if (null != outputStream) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean contains(int[] modes, int mode) {
        if (modes == null) {
            return false;
        }
        for (int i : modes) {
            if (i == mode) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkAspectsEqual(Size a, Size b) {
        double aAspect = a.getWidth() / (double) a.getHeight();
        double bAspect = b.getWidth() / (double) b.getHeight();
        return Math.abs(aAspect - bAspect) <= ASPECT_RATIO_TOLERANCE;
    }

    private static int sensorToDeviceRotation(CameraCharacteristics c, int deviceOrientation) {
        mSensorOrientation = c.get(CameraCharacteristics.SENSOR_ORIENTATION);

        // Get device orientation in degrees
        deviceOrientation = ORIENTATIONS.get(deviceOrientation);

        // Reverse device orientation for front-facing cameras
        if (c.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
            deviceOrientation = -deviceOrientation;
        }

        // Calculate desired JPEG orientation relative to camera orientation to make
        // the image upright relative to the device orientation
        return (mSensorOrientation + deviceOrientation + 360) % 360;
    }

    private void showToast(String text) {
        // We show a Toast by sending request message to mMessageHandler. This makes sure that the
        // Toast is shown on the UI thread.
        Message message = Message.obtain();
        message.obj = text;
        mMessageHandler.sendMessage(message);
    }

    private void handleCompletionLocked(int requestId, ImageSaver.ImageSaverBuilder builder,
                                        TreeMap<Integer, ImageSaver.ImageSaverBuilder> queue) {
        if (builder == null) return;
        ImageSaver saver = builder.buildIfComplete();
        if (saver != null) {
            queue.remove(requestId);
            AsyncTask.THREAD_POOL_EXECUTOR.execute(saver);
        }
    }

    private boolean isLegacyLocked() {
        return mCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL) ==
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY;
    }

    private void startTimerLocked() {
        mCaptureTimer = SystemClock.elapsedRealtime();
    }

    private boolean hitTimeoutLocked() {
        return (SystemClock.elapsedRealtime() - mCaptureTimer) > PRECAPTURE_TIMEOUT_MS;
    }

    public static class PermissionConfirmationDialog extends DialogFragment {

        public static PermissionConfirmationDialog newInstance() {
            return new PermissionConfirmationDialog();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.permission_request)
                    .setPositiveButton(android.R.string.ok, (dialog, which) ->
                            requestPermissions(VIDEO_PERMISSIONS,
                                    REQUEST_CAMERA_PERMISSIONS))
                    .setNegativeButton(android.R.string.cancel,
                            (dialog, which) -> getActivity().finish())
                    .create();
        }

    }


    @Override
    public void onBackPressed() {
        finish();
    }

}
