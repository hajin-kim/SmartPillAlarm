package com.example.smartpillalarm.ui.mystats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.smartpillalarm.R;

public class MystatsFragment extends Fragment {

    private MystatsViewModel mystatsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mystatsViewModel =
                ViewModelProviders.of(this).get(MystatsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_mystats, container, false);
        final TextView textView = root.findViewById(R.id.text_mystats);
        mystatsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}