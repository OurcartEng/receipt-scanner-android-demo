package com.example.receiptScannerAndroidDemo.flow;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.receiptScannerAndroidDemo.MainActivity;
import com.example.receiptScannerAndroidDemo.R;
import com.example.receiptScannerAndroidDemo.ScannerActivity;
import com.example.receiptScannerAndroidDemo.config.Config;
import com.example.receiptScannerAndroidDemo.databinding.FragmentFirstBinding;
import com.ourcart.receiptscanner.ReceiptScannerFlow;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FirstFragment extends Fragment {
    private static final String TAG = FirstFragment.class.getSimpleName();
    private FragmentFirstBinding binding;

    private static final Set<Integer> selectedCheckboxes = new HashSet<>();

    private ReceiptScannerFlow receiptScanner = new ReceiptScannerFlow(
            false,
            Config.API_KEY,
            Config.COUNTRY_CODE,
            Config.CLIENT_CODE,
            Config.CLIENT_USER_ID
    )
            .setCloseListener(context -> {
                Intent i = new Intent(context, MainActivity.class);
                startActivity(i);
            })
            .setUserInteractionListener(s -> {
                Log.i(TAG, "Event triggered: " + s);
            });

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        receiptScanner.setDoneListener(() -> {
            Log.i(TAG, "Done Clicked");
            receiptScanner.reset();
        });

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(v -> {
            Intent in = new Intent(getContext(), ScannerActivity.class);
            startActivity(in);
        });


        binding.checkboxInitialScreenHeading.setOnCheckedChangeListener((v, b) -> {
            toggleSelectedCheckbox(v, b);
            receiptScanner.setInitialScreenHeading(b ? "Test initial heading test" : null);
        });

        binding.checkboxInitialScreenSubHeading.setOnCheckedChangeListener((v, b) -> {
            toggleSelectedCheckbox(v, b);
            receiptScanner.setInitialScreenSubHeading(b ? "Test initial sub heading test\nnew lines are rendered" : null);
        });

        binding.checkboxFinalScreenHeading.setOnCheckedChangeListener((v, b) -> {
            toggleSelectedCheckbox(v, b);
            receiptScanner.setFinalScreenHeading(b ? "Test final heading test" : null);
        });

        binding.checkboxFinalScreenSubHeading.setOnCheckedChangeListener((v, b) -> {
            toggleSelectedCheckbox(v, b);
            receiptScanner.setFinalScreenSubHeading(b ? "Test final sub heading test\nnew lines are rendered" : null);
        });

        binding.checkboxFinalScreenManualReviewHeading.setOnCheckedChangeListener((v, b) -> {
            toggleSelectedCheckbox(v, b);
            receiptScanner.setFinalScreenManualReviewHeading(b ? "Custom header" : null);
        });

        binding.checkboxFinalScreenManualReviewSubHeading.setOnCheckedChangeListener((v, b) -> {
            toggleSelectedCheckbox(v, b);
            receiptScanner.setFinalScreenManualReviewSubHeading(b ? "Your receipt has been send to <b>manual review</b>" : null);
        });

        binding.checkboxWaitForRequestResponse.setChecked(true);
        toggleSelectedCheckbox(binding.checkboxWaitForRequestResponse, true);
        binding.checkboxWaitForRequestResponse.setOnCheckedChangeListener((v, b) -> {
            toggleSelectedCheckbox(v, b);
            receiptScanner.setPreValidation(b);
        });

        binding.checkboxTutorialText.setOnCheckedChangeListener((v, b) -> {
            toggleTutorialOverwrite();
            toggleSelectedCheckbox(v, b);

            receiptScanner.setTutorialStrings(
                    b ? new String[]{"Step 1 text Overwrite", "Step 2 text Overwrite", "Step 3 text Overwrite", "Step 4 text Overwrite"} : null
            );
        });

        binding.checkboxTutorialDrawables.setOnCheckedChangeListener((v, b) -> {
            toggleTutorialOverwrite();
            toggleSelectedCheckbox(v, b);
            Resources resources = getResources();

            receiptScanner.setTutorialDrawables(
                    b ? new Drawable[]{
                            resources.getDrawable(R.drawable.file_upload),
                            resources.getDrawable(R.drawable.camera),
                            resources.getDrawable(R.drawable.file_upload),
                            resources.getDrawable(R.drawable.camera),
                    } : null
            );
        });

        binding.checkboxTutorialOverwrite.setOnCheckedChangeListener((v, b) -> receiptScanner.setTutorialConfigOverride(b));

        binding.buttonStart.setOnClickListener(view1 -> receiptScanner.start(getContext()));


        Iterator<Integer> iterator = selectedCheckboxes.iterator();
        while (iterator.hasNext()) {
            ((CheckBox) view.findViewById(iterator.next())).setChecked(true);
        }

        toggleTutorialOverwrite();
    }

    private void toggleTutorialOverwrite() {
            if (binding.checkboxTutorialText.isChecked() && binding.checkboxTutorialDrawables.isChecked()) {
                binding.checkboxTutorialOverwrite.setEnabled(true);
            } else {
                binding.checkboxTutorialOverwrite.setEnabled(false);
                receiptScanner.setTutorialConfigOverride(false);
            }
    }

    private void toggleSelectedCheckbox(View checkbox, boolean picked) {
        if (picked) {
            selectedCheckboxes.add(checkbox.getId());
        } else {
            selectedCheckboxes.remove(checkbox.getId());
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}