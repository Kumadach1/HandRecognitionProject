package com.example.handgesterrecognition.ui.parameter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ParameterViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ParameterViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}
