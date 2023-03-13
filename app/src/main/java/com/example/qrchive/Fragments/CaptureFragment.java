package com.example.qrchive.Fragments;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoCapture;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.qrchive.Classes.FirebaseWrapper;
import com.example.qrchive.R;
import com.example.qrchive.databinding.ActivityMainBinding;
import com.google.common.util.concurrent.ListenableFuture;

import org.checkerframework.checker.units.qual.A;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CaptureFragment extends Fragment {

    private final ArrayList<String> REQUESTED_PERMISSIONS = new ArrayList<>(Arrays.asList(Manifest.permission.CAMERA));
    private ActivityMainBinding binding;
    private ImageCapture imageCapture;
    private VideoCapture<Recorder> videoCapture;
    private Recording recording;
    private final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";

    private final String TAG = "CameraX";
    private ExecutorService cameraExecutor;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private final int REQUEST_CODE = 10;

    private FirebaseWrapper fbw;
    public CaptureFragment(FirebaseWrapper fbw) {
        super(R.layout.fragment_capture);

        this.fbw = fbw;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraExecutor = Executors.newSingleThreadExecutor();

    }
    private void startCamera()
    {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder()
                        .build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                imageCapture = new ImageCapture.Builder().build();

                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                try {
                    cameraProvider.unbindAll();
                    cameraProvider.bindToLifecycle((LifecycleOwner)this,
                            cameraSelector, preview, imageCapture);
                } catch (Exception exception)
                {
                    Log.e(TAG, "Use case binding failed" + exception);
                }
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }
    private void takePhoto() {
        String name = new SimpleDateFormat(FILENAME_FORMAT, Locale.CANADA)
                .format(System.currentTimeMillis());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P)
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image");
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(requireContext().getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues).build();
        imageCapture.takePicture(
                outputFileOptions, ContextCompat.getMainExecutor(requireContext()),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        final String msg = "Photo capture succeeded: " +
                                outputFileResults.getSavedUri();
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
                        Log.d(TAG, msg);
                        getParentFragmentManager().beginTransaction().replace(R.id.fragment_container,new ScanFragment(fbw),null).commit();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Photo capture failed: " + exception);
                    }
                }
        );

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_capture, container, false);
        previewView = view.findViewById(R.id.capture_view);

        if (allPermissionGranted())
        {
            startCamera();
        }
        else {
            ActivityCompat.requestPermissions(requireActivity(),
                    REQUESTED_PERMISSIONS.toArray(new String[0]), REQUEST_CODE);
        }
        Button button = view.findViewById(R.id.take_photo);
        button.setOnClickListener(v -> takePhoto());
        return view;

    }
    private boolean allPermissionGranted() {
        boolean flag = true;
        for(String permission: REQUESTED_PERMISSIONS) {
            flag = flag && (ContextCompat.checkSelfPermission(requireContext(),permission) == PackageManager.PERMISSION_GRANTED);
        }
        return flag;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

}