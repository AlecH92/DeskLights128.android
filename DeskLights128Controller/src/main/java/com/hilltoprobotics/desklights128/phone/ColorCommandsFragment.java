package com.hilltoprobotics.desklights128.phone;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class ColorCommandsFragment extends Fragment {

    public Button rainbowButton;
    public Button randomButton;
    public Button kittButton;
    private View thisView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_colorcommands, container, false);
        final MainActivity activity = (MainActivity) getActivity();

        rainbowButton = (Button) thisView.findViewById(R.id.rainbowButton);
        randomButton = (Button) thisView.findViewById(R.id.randomButton);
        kittButton = (Button) thisView.findViewById(R.id.kittButton);


        rainbowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String whatToSend = "default?id=1";
                activity.sendData(whatToSend);
            }
        });
        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String whatToSend = "default?id=2";
                activity.sendData(whatToSend);
            }
        });
        kittButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String whatToSend = "default?id=3";
                activity.sendData(whatToSend);
            }
        });
        return thisView;
    }

}