package com.example.receiptScannerAndroidDemo;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.receiptScannerAndroidDemo.scanner.ScannerConfigFragment;
import com.example.receiptScannerAndroidDemo.scanner.ScannerPreviewFragment;
import com.ourcart.receiptscanner.ReceiptScanner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class ScannerActivity extends AppCompatActivity {

    private static final String[] REQUIRED_PERMISSIONS;
    static {
        List<String> permissionsStorage = Arrays.asList(
                android.Manifest.permission.CAMERA,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                        ? android.Manifest.permission.READ_MEDIA_IMAGES
                        : Manifest.permission.READ_EXTERNAL_STORAGE
        );

        REQUIRED_PERMISSIONS = permissionsStorage.toArray(new String[0]);
    }

    private NavController navController;

    public static ReceiptScanner.UISettings uiSettings = new ReceiptScanner.UISettings();
    public static ReceiptScanner.ScannerConfig scannerConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scanner_main);

        Button configBtn = findViewById(R.id.config_btn);
        Button previewBtn = findViewById(R.id.preview_btn);

        scannerConfig = new ReceiptScanner.ScannerConfig() {
            @Override
            public void onHelpClick(Context ctx) {
                Log.e("TAG", "onHelpClick");

                Toast.makeText(ScannerActivity.this,
                        "onHelpClick",
                        Toast.LENGTH_LONG).show();

                Intent in = new Intent(ctx, ScannerActivity.class);
                startActivity(in);
            }

            @Override
            public void onReceiptSnapped(List<Bitmap> bitmaps, Context ctx) {
                ScannerPreviewFragment.pdfUri = null;
                ScannerPreviewFragment.bitmaps = bitmaps;
                Intent in = new Intent(ctx, ScannerActivity.class);
                in.putExtra("preview", true);
                startActivity(in);
            }

            @Override
            public void onCloseClicked(Context ctx) {
                Log.e("TAG", "onCloseClicked");

                Intent in = new Intent(ctx, ScannerActivity.class);
                startActivity(in);
            }
        };

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        navController = navHostFragment.getNavController();


        if (getIntent().getBooleanExtra("preview", false)) {
            configBtn.setEnabled(true);
            previewBtn.setEnabled(false);
            navController.navigate(R.id.action_goto_PreviewFragment);
        }

        findViewById(R.id.button_back).setOnClickListener(v -> {
            Intent in = new Intent(ScannerActivity.this, MainActivity.class);
            startActivity(in);
        });

        configBtn.setOnClickListener(v -> {
            configBtn.setEnabled(false);
            previewBtn.setEnabled(true);
            navController.navigate(R.id.action_goto_ConfigFragment);
        });

        findViewById(R.id.scanner_btn).setOnClickListener(v -> {
            ReceiptScanner.startScanner(ScannerActivity.this, uiSettings, scannerConfig);
        });

        previewBtn.setOnClickListener(v -> {
            configBtn.setEnabled(true);
            previewBtn.setEnabled(false);
            navController.navigate(R.id.action_goto_PreviewFragment);
        });

        if (!isPermissionsGranted(REQUIRED_PERMISSIONS)) {
            activityResultCameraLauncher.launch(REQUIRED_PERMISSIONS);
        }
    }

    private boolean isPermissionsGranted(String[] requestedPermissions) {
        for (String permission : requestedPermissions) {
            if (ContextCompat.checkSelfPermission(
                    ScannerActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private final ActivityResultLauncher<String[]> activityResultCameraLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
        int permissionGranted = 0;
        for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
            if (Arrays.asList(REQUIRED_PERMISSIONS).contains(entry.getKey()) && entry.getValue()) {
                permissionGranted++;
            }
        }

        if (REQUIRED_PERMISSIONS.length != permissionGranted) {
            Toast.makeText(ScannerActivity.this,
                "Permission request denied",
                Toast.LENGTH_SHORT).show();
            Intent in = new Intent(ScannerActivity.this, MainActivity.class);
            startActivity(in);
            ScannerActivity.this.finish();
        }
    });
}