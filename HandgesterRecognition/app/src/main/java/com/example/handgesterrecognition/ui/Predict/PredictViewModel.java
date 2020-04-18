package com.example.handgesterrecognition.ui.Predict;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PredictViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PredictViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is predict fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}
