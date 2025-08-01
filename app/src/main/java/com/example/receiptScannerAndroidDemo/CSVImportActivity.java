package com.example.receiptScannerAndroidDemo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.receiptScannerAndroidDemo.config.Config;
import com.ourcart.receiptscanner.ReceiptScanner;
import com.ourcart.receiptscanner.enums.ValidationStatus;
import com.ourcart.receiptscanner.utils.ImageValidator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class CSVImportActivity extends AppCompatActivity {

    private static final int PICK_CSV_FILE = 1;
    private Button importButton, exportButton;
    private TextView resultLabel;
    private List<String[]> parsedRows = new ArrayList<>();
    private List<String[]> validatedResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csv_import);

        importButton = findViewById(R.id.import_csv_button);
        exportButton = findViewById(R.id.export_csv_button);
        resultLabel = findViewById(R.id.result_label);

        importButton.setOnClickListener(v -> openFilePicker());
        exportButton.setOnClickListener(v -> exportResults());

        ImageValidator.PreInitValidationConfig validationConfig = new ImageValidator.PreInitValidationConfig();
        validationConfig.isProduction = false;
        validationConfig.apiKey = Config.API_KEY;
        validationConfig.requireWifi = true;

        ReceiptScanner.preValidationInit(
            this,
            validationConfig,
            (hasUpdatePerformed) -> {
                // Set resultLabel based on model status
                ValidationStatus status = ReceiptScanner.getPreValidationStatus(this);
                runOnUiThread(() -> {
                    switch (status) {
                        case AVAILABLE:
                            resultLabel.setText("✅ Processing is ready. Select a CSV to begin.");
                            break;
                        case AVAILABLE_UPDATING:
                            resultLabel.setText("⏳ Downloading latest model...");
                            break;
                        case NOT_AVAILABLE:
                            resultLabel.setText("❌ Model is not available. Check your connection.");
                            break;
                    }
                });
                Toast.makeText(
                    this,
                    hasUpdatePerformed ? "New model downloaded and updated" : "Model already up-to-date",
                    Toast.LENGTH_LONG
                ).show();
            },
            (e) -> {
                if (e instanceof ImageValidator.WifiDisabledException) {
                    Toast.makeText(this, "Wifi needs to be enabled", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "❌ Pre-validation init failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        );
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimeTypes = {"text/csv", "application/vnd.ms-excel", "text/comma-separated-values"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(intent, "Select CSV File"), PICK_CSV_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CSV_FILE && resultCode == Activity.RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                processCSV(fileUri);
            }
        }
    }

    private void processCSV(Uri fileUri) {
        try {
            // At the very start, set resultLabel to "Processing..."
            runOnUiThread(() -> resultLabel.setText("🔄 Processing..."));
            getContentResolver().takePersistableUriPermission(fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            BufferedReader reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(fileUri)));

            validatedResults.clear();
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            AtomicInteger handleCounter = new AtomicInteger(1);
            String line;

            while ((line = reader.readLine()) != null) {
                String[] cols = line.split(",");
                if (cols.length == 0) continue;

                String handleId = cols[0]; // Use handle_id from CSV input

                List<CompletableFuture<Void>> imageLoadFutures = new ArrayList<>();
                List<android.graphics.Bitmap> images = new ArrayList<>();

                for (int i = 1; i <= 4; i++) {
                    if (i < cols.length && !cols[i].isEmpty()) {
                        String imageUrl = cols[i];
                        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
                            try {
                                java.io.InputStream is = new java.net.URL(imageUrl).openStream();
                                android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeStream(is);
                                if (bitmap != null) {
                                    synchronized (images) {
                                        images.add(bitmap);
                                    }
                                } else {
                                    android.util.Log.w("CSVImport", "Image decode returned null for URL: " + imageUrl);
                                }
                            } catch (Exception e) {
                                android.util.Log.w("CSVImport", "Failed to load image: " + imageUrl, e);
                            }
                        });
                        imageLoadFutures.add(imageFuture);
                    }
                }

                CompletableFuture<Void> allImagesLoaded = CompletableFuture.allOf(imageLoadFutures.toArray(new CompletableFuture[0]));
                CompletableFuture<Void> validationFuture = allImagesLoaded.thenRun(() -> {
                    if (images.isEmpty()) {
                        android.util.Log.w("CSVImport", "❌ No images loaded for " + handleId + ", skipping. Columns: " + java.util.Arrays.toString(cols));
                        android.util.Log.w("CSVImport", "No valid images for: " + handleId);
                        return;
                    }

                    if (ReceiptScanner.getPreValidationStatus(this) != ValidationStatus.NOT_AVAILABLE) {
                        try {
                            CompletableFuture<Void> future = ReceiptScanner.validateReceipt(this, images)
                                .thenAccept(validationResult -> {
                                    synchronized (validatedResults) {
                                        android.util.Log.i("Validation", "✅ " + handleId + ": retailer=" + validationResult.retailerFound
                                                + ", date=" + validationResult.dateFound
                                                + ", time=" + validationResult.timeFound
                                                + ", total=" + validationResult.receiptTotalFound);
                                        validatedResults.add(new String[]{
                                            handleId,
                                            String.valueOf(validationResult.retailerFound),
                                            String.valueOf(validationResult.dateFound),
                                            String.valueOf(validationResult.timeFound),
                                            String.valueOf(validationResult.receiptTotalFound)
                                        });
                                    }
                                }).exceptionally(e -> {
                                    android.util.Log.e("Validation", "Validation failed for " + handleId, e);
                                    return null;
                                });

                            synchronized (futures) {
                                futures.add(future);
                            }
                        } catch (ImageValidator.ModelUnavailableException e) {
                            runOnUiThread(() -> Toast.makeText(this, "No model available", Toast.LENGTH_LONG).show());
                        }
                    }
                });
                futures.add(validationFuture);
            }

            reader.close();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
                runOnUiThread(() -> {
                    resultLabel.setText("✅ Process completed. You can now download the results.");
                    exportButton.setEnabled(true);
                });
            });

        } catch (Exception e) {
            Toast.makeText(this, "❌ Failed to process CSV: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void exportResults() {
        android.util.Log.i("ExportResults", "📦 Exporting " + validatedResults.size() + " validated results.");
        String fileName = "ValidationResults.csv";
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("handle_id,retailer_found,date_found,time_found,total_found\n");
        for (String[] row : validatedResults) {
            csvBuilder.append(String.join(",", row)).append("\n");
        }
        String csvData = csvBuilder.toString();

        try {
            // Use MediaStore to write into Downloads folder (Android Q+)
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, "text/csv");
            values.put(MediaStore.Downloads.IS_PENDING, 1);

            Uri collection = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            }
            Uri fileUri = getContentResolver().insert(collection, values);

            if (fileUri != null) {
                try (OutputStream out = getContentResolver().openOutputStream(fileUri)) {
                    out.write(csvData.getBytes());
                }

                // Mark the file as not pending (visible to the user)
                values.clear();
                values.put(MediaStore.Downloads.IS_PENDING, 0);
                getContentResolver().update(fileUri, values, null, null);

                Toast.makeText(this, "✅ Exported to Downloads/" + fileName, Toast.LENGTH_LONG).show();
            } else {
                throw new Exception("Failed to create file URI");
            }
        } catch (Exception e) {
            Toast.makeText(this, "❌ Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
