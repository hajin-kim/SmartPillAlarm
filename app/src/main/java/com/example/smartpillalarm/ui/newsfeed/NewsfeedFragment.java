package com.example.smartpillalarm.ui.newsfeed;

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

public class NewsfeedFragment extends Fragment {

    private NewsfeedViewModel newsfeedViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        newsfeedViewModel =
                ViewModelProviders.of(this).get(NewsfeedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_newsfeed, container, false);
        final TextView textView = root.findViewById(R.id.text_newsfeed);
        newsfeedViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}