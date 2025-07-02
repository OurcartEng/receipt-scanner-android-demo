package com.example.receiptScannerAndroidDemo.scanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.receiptScannerAndroidDemo.config.Config;
import com.example.receiptScannerAndroidDemo.databinding.FragmentScannerPreviewBinding;
import com.ourcart.receiptscanner.ReceiptScanner;
import com.ourcart.receiptscanner.enums.ValidationStatus;
import com.ourcart.receiptscanner.utils.FileService;
import com.ourcart.receiptscanner.utils.ImageEdgeDetector;
import com.ourcart.receiptscanner.utils.ImageValidator;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ScannerPreviewFragment extends Fragment {

    public static Uri pdfUri = null;
    public static List<Bitmap> bitmaps;
    private final String[] MIMES = new String[] {
            "application/pdf",
            "application/x-pdf",
            "application/acrobat",
            "binary/octet-stream",
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif"
    };
    private final Set<String> IMAGE_MIMES = new HashSet<>(Arrays.asList(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif"
    ));

    private FragmentScannerPreviewBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentScannerPreviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, null);

        updateView();

        binding.selectFileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.putExtra(Intent.EXTRA_MIME_TYPES, MIMES);

            someActivityResultLauncher.launch(Intent.createChooser(intent, "Select file"));
        });

        binding.findCropPointsBtn.setOnClickListener(v -> {
            if (!bitmaps.isEmpty()) {
                binding.findCropPointsBtn.setEnabled(false);
                ReceiptScanner.getEdgePointsData(bitmaps).thenAccept(edgeData -> {
                    LinearLayout previewScroll = binding.previewScroll;

                    int i = 0;
                    for (ImageEdgeDetector.EdgeData ed : edgeData) {
                        ConstraintLayout constraintLayout = (ConstraintLayout) previewScroll.getChildAt(i);
                        CropPointsView cropPointsView = (CropPointsView) constraintLayout.getChildAt(1);
                        cropPointsView.setLayoutParams(new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                constraintLayout.getHeight()
                        ));

                        float ratio = (float) cropPointsView.getWidth() / ed.bitmap.getWidth();
                        cropPointsView.setPoints(ed.borderPoints, ratio);

                        i++;
                    }

                });
            }
        });

        binding.preValidateBtn.setOnClickListener(v -> {
            v.setEnabled(false);
            Toast.makeText(
                    getContext(),
                    "ML model version checking...",
                    Toast.LENGTH_LONG
            ).show();

            ImageValidator.PreInitValidationConfig validationConfig = new ImageValidator.PreInitValidationConfig();
            validationConfig.isProduction = false;
            validationConfig.apiKey = Config.API_KEY;
            validationConfig.requireWifi = true;

            ReceiptScanner.preValidationInit(
                getContext(),
                validationConfig,
                (updatePreformed) -> {
                    Toast.makeText(
                            getContext(),
                            updatePreformed ? "New model downloaded, and updated" : "No update needed, newest ML model version already present",
                            Toast.LENGTH_LONG
                    ).show();

                    updateValidationBtn();
                    v.setEnabled(true);
                },
                (e) -> {
                    if (e instanceof ImageValidator.WifiDisabledException) {
                        Toast.makeText(getContext(), "Wifi needs to be enabled", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    v.setEnabled(true);
            });
        });

        ValidationResultsFragment newFragment = new ValidationResultsFragment();
        binding.validateBtn.setOnClickListener(v -> {
            if (pdfUri != null) {
                try {
                    v.setEnabled(false);
                    ReceiptScanner.validateReceipt(getContext(), pdfUri)
                            .thenAccept((results) -> {
                                newFragment.setValidationResult(results);
                                newFragment.show(getActivity().getSupportFragmentManager(), "validate");
                                v.setEnabled(true);
                            });
                } catch (FileService.FileTypeException e) {
                    Toast.makeText(getContext(), "Wrong file type", Toast.LENGTH_LONG).show();
                } catch (FileService.FileSizeException e) {
                    Toast.makeText(getContext(), "File too large", Toast.LENGTH_LONG).show();
                } catch (ImageValidator.ModelUnavailableException e) {
                    Toast.makeText(getContext(), "No model available", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Toast.makeText(getContext(), "Other error", Toast.LENGTH_LONG).show();
                }
            }
            if (!bitmaps.isEmpty()) {
                v.setEnabled(false);
                ReceiptScanner.validateReceipt(getContext(), bitmaps)
                        .thenAccept((results) -> {
                            newFragment.setValidationResult(results);
                            newFragment.show(getActivity().getSupportFragmentManager(), "validate");
                            v.setEnabled(true);
                        });
            }
        });

        ReceiptScanner.ApiConfig apiConfig = new ReceiptScanner.ApiConfig();
        apiConfig.isProd = false;
        apiConfig.apiKey = Config.API_KEY;
        apiConfig.clientCountry = Config.COUNTRY_CODE;
        apiConfig.clientCode = Config.CLIENT_CODE;
        apiConfig.clientUserId = Config.CLIENT_USER_ID;

        binding.sendBtn.setOnClickListener(v -> {
            if (pdfUri != null) {
                v.setEnabled(false);
                try {
                    ReceiptScanner.sendReceipt(getContext(), pdfUri, apiConfig).thenAccept(edgeData -> {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Receipt sended", Toast.LENGTH_LONG).show();
                            v.setEnabled(true);
                        });
                    }).exceptionally((e) -> {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            v.setEnabled(true);
                        });
                        return null;
                    });
                } catch (FileService.FileTypeException e) {
                    throw new RuntimeException(e);
                } catch (FileService.FileSizeException e) {
                    throw new RuntimeException(e);
                } catch (ReceiptScanner.MissingConfigException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
            if (!bitmaps.isEmpty()) {
                v.setEnabled(false);
                try {
                    ReceiptScanner.sendReceipt(bitmaps, apiConfig).thenAccept(edgeData -> {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Receipt sended", Toast.LENGTH_LONG).show();
                            v.setEnabled(true);
                        });
                    }).exceptionally((e) -> {

                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            v.setEnabled(true);
                        });
                        return null;
                    });
                } catch (ReceiptScanner.MissingConfigException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void updateView() {
        binding.previewScroll.removeAllViews();
        updateValidationBtn();

        if (pdfUri != null) {
            binding.findCropPointsBtn.setEnabled(false);
            binding.optionContainer.setVisibility(View.VISIBLE);
            binding.imageLayout.setVisibility(View.GONE);
            binding.nodataMessage.setVisibility(View.GONE);
            binding.pdfLoaded.setVisibility(View.VISIBLE);
            return;
        }
        Log.e("TAG", "2: bitmaps: " + bitmaps.size());
        if (!bitmaps.isEmpty()) {
            binding.findCropPointsBtn.setEnabled(true);
            binding.optionContainer.setVisibility(View.VISIBLE);
            binding.imageLayout.setVisibility(View.VISIBLE);
            binding.nodataMessage.setVisibility(View.GONE);
            binding.pdfLoaded.setVisibility(View.GONE);
            showBitmaps();
            return;
        }
        binding.optionContainer.setVisibility(View.GONE);
        binding.imageLayout.setVisibility(View.GONE);
        binding.nodataMessage.setVisibility(View.VISIBLE);
        binding.pdfLoaded.setVisibility(View.GONE);
    }

    private void updateValidationBtn() {
        binding.validateBtn.setEnabled(ReceiptScanner.getPreValidationStatus(getContext()) != ValidationStatus.NOT_AVAILABLE);
    }

    private void showBitmaps() {
        LinearLayout previewScroll = binding.previewScroll;
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        for (Bitmap bitmap : bitmaps) {
            ConstraintLayout constraintLayout = new ConstraintLayout(getContext());
            constraintLayout.setLayoutParams(layoutParams);

            ImageView imageView = new ImageView(getContext());
            imageView.setElevation(1);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(layoutParams);
            imageView.setImageBitmap(bitmap);

            CropPointsView cropPointsView = new CropPointsView(getContext());
            cropPointsView.setElevation(2);
            cropPointsView.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            ));
            cropPointsView.setBackgroundColor(Color.TRANSPARENT);

            constraintLayout.addView(imageView);
            constraintLayout.addView(cropPointsView);
            previewScroll.addView(constraintLayout);
        }
    }

    private boolean isImage(Uri selectedImageUri) {
        String mimeType = getContext().getContentResolver().getType(selectedImageUri).toLowerCase();
        return IMAGE_MIMES.contains(mimeType);
    }

    private boolean isPdf(Uri selectedImageUri) {
        DocumentFile documentFile = DocumentFile.fromSingleUri(getContext(), selectedImageUri);

        return documentFile != null &&
                documentFile.getName() != null &&
                documentFile.getName().toLowerCase().endsWith(".pdf");
    }

    private Bitmap getBitmapFromURI(Uri uri) throws IOException {
        Bitmap bitmap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().getContentResolver(), uri));
        } else {
            bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
        }
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        return bitmap;
    }

    private ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                try {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();

                        pdfUri = null;
                        bitmaps.clear();
                        if (data != null) {
                            if (data.getClipData() != null) {
                                // Multiple files selected
                                int count = data.getClipData().getItemCount();
                                if (count > 6) {
                                    Toast.makeText(getContext(), getText(com.ourcart.receiptscanner.R.string.OURCART_too_many_files), Toast.LENGTH_LONG).show();
                                    return;
                                }
                                for (int i = 0; i < count; i++) {
                                    Uri fileUri = data.getClipData().getItemAt(i).getUri();

                                    if (isImage(fileUri)) {
                                        Bitmap bitmap = getBitmapFromURI(fileUri);
                                        bitmaps.add(bitmap);
                                    } else if (isPdf(fileUri)) {
                                        bitmaps.clear();
                                        pdfUri = fileUri;
                                        break;
                                    }
                                }
                            } else if (data.getData() != null) {
                                // Single file selected
                                Uri fileUri = data.getData();
                                if (fileUri == null) {
                                    return;
                                }
                                if (isImage(fileUri)) {
                                    Bitmap bitmap = getBitmapFromURI(fileUri);
                                    bitmaps.add(bitmap);
                                } else if (isPdf(fileUri)) {
                                    pdfUri = fileUri;
                                }
                            }

                            updateView();
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // Important: Clear the binding when view is destroyed
    }

    public static class ValidationResultsFragment extends DialogFragment {
        private ImageValidator.ValidationResult validationResult;

        public void setValidationResult(ImageValidator.ValidationResult validationResult) {
            this.validationResult = validationResult;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            return new AlertDialog.Builder(requireContext())
                    .setMessage(validationResult.toString())
                    .setPositiveButton("Close", (dialog, which) -> {
                        getDialog().dismiss();
                    })
                    .create();
        }
    }

    public static class CropPointsView extends FrameLayout {
        private Map<Integer, PointF> borderPoints = new HashMap<>();
        private final Paint mPaint = new Paint();

        public CropPointsView(Context context) {
            super(context);
        }

        public void setPoints(Map<Integer, PointF> borderPoints, float ratio) {
            for (Map.Entry<Integer, PointF> entity : borderPoints.entrySet()) {
                this.borderPoints.put(entity.getKey(), new PointF(
                        entity.getValue().x * ratio,
                        entity.getValue().y * ratio
                ));
            }
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setStrokeWidth(2);
            mPaint.setColor(Color.WHITE);

            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawLine(borderPoints.get(0).x, borderPoints.get(0).y, borderPoints.get(1).x, borderPoints.get(1).y, mPaint);
            canvas.drawLine(borderPoints.get(0).x, borderPoints.get(0).y, borderPoints.get(2).x, borderPoints.get(2).y, mPaint);
            canvas.drawLine(borderPoints.get(3).x, borderPoints.get(3).y, borderPoints.get(1).x, borderPoints.get(1).y, mPaint);
            canvas.drawLine(borderPoints.get(3).x, borderPoints.get(3).y, borderPoints.get(2).x, borderPoints.get(2).y, mPaint);

            canvas.drawCircle(borderPoints.get(0).x, borderPoints.get(0).y, 20, mPaint);
            canvas.drawCircle(borderPoints.get(1).x, borderPoints.get(1).y, 20, mPaint);
            canvas.drawCircle(borderPoints.get(2).x, borderPoints.get(2).y, 20, mPaint);
            canvas.drawCircle(borderPoints.get(3).x, borderPoints.get(3).y, 20, mPaint);
        }
    }
}