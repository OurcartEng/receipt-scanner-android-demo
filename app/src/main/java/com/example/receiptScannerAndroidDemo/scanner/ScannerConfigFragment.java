package com.example.receiptScannerAndroidDemo.scanner;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.receiptScannerAndroidDemo.R;
import com.example.receiptScannerAndroidDemo.ScannerActivity;
import com.example.receiptScannerAndroidDemo.databinding.FragmentScannerConfigBinding;
import com.github.dhaval2404.colorpicker.ColorPickerDialog;
import com.github.dhaval2404.colorpicker.listener.ColorListener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class ScannerConfigFragment extends Fragment {

    private FragmentScannerConfigBinding binding;
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentScannerConfigBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setBooleanCheckbox(binding.showHelpIcon, ScannerActivity.uiSettings.showHelpIcon, (v, b) -> ScannerActivity.uiSettings.showHelpIcon = b);
        setBooleanCheckbox(binding.showTargetBorder, ScannerActivity.uiSettings.showTargetBorder, (v, b) -> ScannerActivity.uiSettings.showTargetBorder = b);

        Drawable smileyIcon = getResources().getDrawable(R.drawable.smiley_icon);
        setDrawableCheckbox(binding.closeDrawable, ScannerActivity.uiSettings.closeDrawable, (v, b) -> ScannerActivity.uiSettings.closeDrawable = (b ? smileyIcon : null));
        setDrawableCheckbox(binding.torchOnDrawable, ScannerActivity.uiSettings.torchOnDrawable, (v, b) -> ScannerActivity.uiSettings.torchOnDrawable = (b ? smileyIcon : null));
        setDrawableCheckbox(binding.torchOffDrawable, ScannerActivity.uiSettings.torchOffDrawable, (v, b) -> ScannerActivity.uiSettings.torchOffDrawable = (b ? smileyIcon : null));
        setDrawableCheckbox(binding.helpDrawable, ScannerActivity.uiSettings.helpDrawable, (v, b) -> ScannerActivity.uiSettings.helpDrawable = (b ? smileyIcon : null));
        setDrawableCheckbox(binding.automaticCaptureDrawable, ScannerActivity.uiSettings.automaticCaptureDrawable, (v, b) -> ScannerActivity.uiSettings.automaticCaptureDrawable = (b ? smileyIcon : null));
        setDrawableCheckbox(binding.manualCaptureDrawable, ScannerActivity.uiSettings.manualCaptureDrawable, (v, b) -> ScannerActivity.uiSettings.manualCaptureDrawable = (b ? smileyIcon : null));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Typeface newFont = getResources().getFont(R.font.pochaevsk_regular);
            setFontCheckbox(binding.modeBtnsFontFamily, ScannerActivity.uiSettings.modeBtnsFontFamily, (v, b) -> ScannerActivity.uiSettings.modeBtnsFontFamily = (b ? newFont : null));
            setFontCheckbox(binding.nextBtnFontFamily, ScannerActivity.uiSettings.nextBtnFontFamily, (v, b) -> ScannerActivity.uiSettings.nextBtnFontFamily = (b ? newFont : null));
            setFontCheckbox(binding.imageCounterFontFamily, ScannerActivity.uiSettings.imageCounterFontFamily, (v, b) -> ScannerActivity.uiSettings.imageCounterFontFamily = (b ? newFont : null));
            setFontCheckbox(binding.feedbackFontFamily, ScannerActivity.uiSettings.feedbackFontFamily, (v, b) -> ScannerActivity.uiSettings.feedbackFontFamily = (b ? newFont : null));
        }

        setFontSizeInput(binding.modeBtnsFontSize, ScannerActivity.uiSettings.modeBtnsFontSize, 13, val -> ScannerActivity.uiSettings.modeBtnsFontSize = val);
        setFontSizeInput(binding.nextBtnFontSize, ScannerActivity.uiSettings.nextBtnFontSize, 16, val -> ScannerActivity.uiSettings.nextBtnFontSize = val);
        setFontSizeInput(binding.imageCounterFontSize, ScannerActivity.uiSettings.imageCounterFontSize, 14, val -> ScannerActivity.uiSettings.imageCounterFontSize = val);
        setFontSizeInput(binding.feedbackFontSize, ScannerActivity.uiSettings.feedbackFontSize, 14, val -> ScannerActivity.uiSettings.feedbackFontSize = val);


        int primaryColor = getResources().getColor(R.color.ourcartPrimaryColor);
        int textColor = getResources().getColor(R.color.ourcartTextColor);
        int whiteColor = Color.WHITE;
        int feedbackBackgroundColor = getResources().getColor(R.color.ourcartScannerFeedbackBackgroundColor);
        int automaticCaptureSnapBtnBackgroundColor = getResources().getColor(R.color.ourcartScannerAutomaticCaptureSnapBtnBackgroundColor);
        int manualCaptureSnapBtnBackgroundColor = getResources().getColor(R.color.ourcartScannerManualCaptureSnapBtnBackgroundColor);

        setColorPicker(binding.modeBtnActiveBackgroundColor, ScannerActivity.uiSettings.modeBtnActiveBackgroundColor, primaryColor, (color, colorHex) -> {
            ScannerActivity.uiSettings.modeBtnActiveBackgroundColor = color;
            binding.modeBtnActiveBackgroundColor.setBackgroundColor(color);
        });
        setColorPicker(binding.modeBtnActiveFontColor, ScannerActivity.uiSettings.modeBtnActiveFontColor, whiteColor, (color, colorHex) -> {
            ScannerActivity.uiSettings.modeBtnActiveFontColor = color;
            binding.modeBtnActiveFontColor.setBackgroundColor(color);
        });
        setColorPicker(binding.modeBtnInactiveFontColor, ScannerActivity.uiSettings.modeBtnInactiveFontColor, textColor, (color, colorHex) -> {
            ScannerActivity.uiSettings.modeBtnInactiveFontColor = color;
            binding.modeBtnInactiveFontColor.setBackgroundColor(color);
        });
        setColorPicker(binding.modeBtnsBackgroundColor, ScannerActivity.uiSettings.modeBtnsBackgroundColor, whiteColor, (color, colorHex) -> {
            ScannerActivity.uiSettings.modeBtnsBackgroundColor = color;
            binding.modeBtnsBackgroundColor.setBackgroundColor(color);
        });
        setColorPicker(binding.snapBtnAutomaticCaptureModeColor, ScannerActivity.uiSettings.snapBtnAutomaticCaptureModeColor, automaticCaptureSnapBtnBackgroundColor, (color, colorHex) -> {
            ScannerActivity.uiSettings.snapBtnAutomaticCaptureModeColor = color;
            binding.snapBtnAutomaticCaptureModeColor.setBackgroundColor(color);
        });
        setColorPicker(binding.snapBtnAutomaticCaptureModeRingColor, ScannerActivity.uiSettings.snapBtnAutomaticCaptureModeRingColor, automaticCaptureSnapBtnBackgroundColor, (color, colorHex) -> {
            ScannerActivity.uiSettings.snapBtnAutomaticCaptureModeRingColor = color;
            binding.snapBtnAutomaticCaptureModeRingColor.setBackgroundColor(color);
        });
        setColorPicker(binding.snapBtnManualCaptureModeColor, ScannerActivity.uiSettings.snapBtnManualCaptureModeColor, manualCaptureSnapBtnBackgroundColor, (color, colorHex) -> {
            ScannerActivity.uiSettings.snapBtnManualCaptureModeColor = color;
            binding.snapBtnManualCaptureModeColor.setBackgroundColor(color);
        });
        setColorPicker(binding.snapBtnManualCaptureModeRingColor, ScannerActivity.uiSettings.snapBtnManualCaptureModeRingColor, manualCaptureSnapBtnBackgroundColor, (color, colorHex) -> {
            ScannerActivity.uiSettings.snapBtnManualCaptureModeRingColor = color;
            binding.snapBtnManualCaptureModeRingColor.setBackgroundColor(color);
        });
        setColorPicker(binding.snapBtnAutoCapturingColor, ScannerActivity.uiSettings.snapBtnAutoCapturingColor, whiteColor, (color, colorHex) -> {
            ScannerActivity.uiSettings.snapBtnAutoCapturingColor = color;
            binding.snapBtnAutoCapturingColor.setBackgroundColor(color);
        });
        setColorPicker(binding.snapBtnAutoCapturingRingColor, ScannerActivity.uiSettings.snapBtnAutoCapturingRingColor, primaryColor, (color, colorHex) -> {
            ScannerActivity.uiSettings.snapBtnAutoCapturingRingColor = color;
            binding.snapBtnAutoCapturingRingColor.setBackgroundColor(color);
        });
        setColorPicker(binding.nextBtnBackgroundColor, ScannerActivity.uiSettings.nextBtnBackgroundColor, primaryColor, (color, colorHex) -> {
            ScannerActivity.uiSettings.nextBtnBackgroundColor = color;
            binding.nextBtnBackgroundColor.setBackgroundColor(color);
        });
        setColorPicker(binding.nextBtnFontColor, ScannerActivity.uiSettings.nextBtnFontColor, whiteColor, (color, colorHex) -> {
            ScannerActivity.uiSettings.nextBtnFontColor = color;
            binding.nextBtnFontColor.setBackgroundColor(color);
        });
        setColorPicker(binding.imageCounterBackgroundColor, ScannerActivity.uiSettings.imageCounterBackgroundColor, primaryColor, (color, colorHex) -> {
            ScannerActivity.uiSettings.imageCounterBackgroundColor = color;
            binding.imageCounterBackgroundColor.setBackgroundColor(color);
        });
        setColorPicker(binding.imageCounterFontColor, ScannerActivity.uiSettings.imageCounterFontColor, whiteColor, (color, colorHex) -> {
            ScannerActivity.uiSettings.imageCounterFontColor = color;
            binding.imageCounterFontColor.setBackgroundColor(color);
        });
        setColorPicker(binding.feedbackBackgroundColor, ScannerActivity.uiSettings.feedbackBackgroundColor, feedbackBackgroundColor, (color, colorHex) -> {
            ScannerActivity.uiSettings.feedbackBackgroundColor = color;
            binding.feedbackBackgroundColor.setBackgroundColor(color);
        });
        setColorPicker(binding.feedbackFontColor, ScannerActivity.uiSettings.feedbackFontColor, primaryColor, (color, colorHex) -> {
            ScannerActivity.uiSettings.feedbackFontColor = color;
            binding.feedbackFontColor.setBackgroundColor(color);
        });
        setColorPicker(binding.capturingProgressBorderColor, ScannerActivity.uiSettings.capturingProgressBorderColor, primaryColor, (color, colorHex) -> {
            ScannerActivity.uiSettings.capturingProgressBorderColor = color;
            binding.capturingProgressBorderColor.setBackgroundColor(color);
        });
        setColorPicker(binding.receiptShadowColor, ScannerActivity.uiSettings.receiptShadowColor, whiteColor, (color, colorHex) -> {
            ScannerActivity.uiSettings.receiptShadowColor = Color.argb(
                    90,
                    Color.red(color),
                    Color.green(color),
                    Color.blue(color)
            );
            binding.receiptShadowColor.setBackgroundColor(color);
        });
        setColorPicker(binding.receiptShadowBorderColor, ScannerActivity.uiSettings.receiptShadowBorderColor, whiteColor, (color, colorHex) -> {
            ScannerActivity.uiSettings.receiptShadowBorderColor = color;
            binding.receiptShadowBorderColor.setBackgroundColor(color);
        });
    }

    private void setColorPicker(Button btnView, Integer currentValue, Integer defaultValue, ColorListener onColorChange) {
        btnView.setBackgroundColor(currentValue == null ? defaultValue : currentValue);

        btnView.setOnClickListener(v -> new ColorPickerDialog.Builder(getContext())
                .setDefaultColor(String.format("#%06X", (0xFFFFFF & btnView.getDrawingCacheBackgroundColor())))
                .setColorListener(onColorChange)
                .show());
    }

    private void setDrawableCheckbox(CheckBox checkboxView, Drawable defaultValue, CompoundButton.OnCheckedChangeListener onChange) {
        checkboxView.setChecked(defaultValue != null);
        checkboxView.setOnCheckedChangeListener(onChange);
    }

    private void setBooleanCheckbox(CheckBox checkboxView, boolean defaultValue, CompoundButton.OnCheckedChangeListener onChange) {
        checkboxView.setChecked(defaultValue);
        checkboxView.setOnCheckedChangeListener(onChange);
    }

    private void setFontCheckbox(CheckBox checkboxView, Typeface defaultValue, CompoundButton.OnCheckedChangeListener onChange) {
        checkboxView.setChecked(defaultValue != null);
        checkboxView.setOnCheckedChangeListener(onChange);
    }

    private void setFontSizeInput(EditText editTextView, Integer currentValue, Integer defaultValue, IntegerFunction watcher) {
        editTextView.setText(String.valueOf(currentValue == null ? defaultValue : currentValue));
        editTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (str.isEmpty()) {
                    watcher.run(null);
                } else {
                    try {
                        watcher.run(Integer.parseInt(str));
                    } catch (Exception e) {
                        watcher.run(null);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            };
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // Important: Clear the binding when view is destroyed
    }

    interface IntegerFunction {
        void run(Integer str);
    }
}