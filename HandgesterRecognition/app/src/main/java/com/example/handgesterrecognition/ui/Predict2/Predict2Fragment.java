package com.example.handgesterrecognition.ui.Predict2;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.handgesterrecognition.R;
import com.example.handgesterrecognition.ui.home.HomeViewModel;

public class Predict2Fragment extends Fragment {

    private HomeViewModel Predict2ViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Predict2ViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.predict2_fragment, container, false);

        return root;
    }


}
