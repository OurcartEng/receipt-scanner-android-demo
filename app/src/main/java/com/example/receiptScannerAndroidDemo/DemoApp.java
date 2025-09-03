package com.example.receiptScannerAndroidDemo;

import android.app.Application;

import com.example.receiptScannerAndroidDemo.config.Config;
import com.ourcart.receiptscanner.ReceiptScanner;
import com.ourcart.receiptscanner.utils.ImageValidator;

public class DemoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ImageValidator.PreInitValidationConfig preValidationConfig = new ImageValidator.PreInitValidationConfig();
        preValidationConfig.apiKey = Config.API_KEY;
        ReceiptScanner.preValidationInit(this, preValidationConfig);
    }
}


