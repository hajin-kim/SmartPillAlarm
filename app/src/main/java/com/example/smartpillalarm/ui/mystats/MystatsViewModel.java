package com.example.smartpillalarm.ui.mystats;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MystatsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MystatsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is mystats fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}