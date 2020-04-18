package com.example.handgesterrecognition.ui.Capture;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CaptureViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CaptureViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is capture fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}
