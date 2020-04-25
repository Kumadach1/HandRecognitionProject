package com.example.handgesterrecognition.ui.Predict;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.handgesterrecognition.R;
import com.example.handgesterrecognition.predictShowActivity;
import com.example.handgesterrecognition.ui.Predict2.Predict2Fragment;
import com.example.handgesterrecognition.ui.home.HomeViewModel;

//summary information page
public class PredictFragment extends Fragment {

    Button btn_prediction;
    private HomeViewModel PredictViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PredictViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_predict, container, false);

        btn_prediction = root.findViewById(R.id.button6);

        btn_prediction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**Predict2Fragment nextFrag= new Predict2Fragment();
                FragmentManager manager = getParentFragmentManager();
                manager.beginTransaction()
                        .replace(root.getId(),nextFrag,nextFrag.getTag())
                        .commit();
                 **/
                Intent intent = new Intent(getActivity(), predictShowActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }
}
