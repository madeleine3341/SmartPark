package com.team19.smartpark.adapters;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.team19.smartpark.R;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_layout, container, false);
        Button linkbtn = v.findViewById(R.id.linkBtn);
        linkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String spot = getArguments().getString("spot");
                String parent = getArguments().getString("parent");
                Log.d("TAG", "onClick: " + parent + "/spots/" + spot);
                dismiss();
            }
        });
        return v;
    }


}

