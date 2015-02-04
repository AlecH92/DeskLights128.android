package com.hilltoprobotics.desklights128.phone;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class ColorAllFragment extends Fragment {

    public Button updateButton;
    private View thisView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView =inflater.inflate(R.layout.fragment_colorall, container, false);
        final MainActivity activity = (MainActivity) getActivity();

        updateButton = (Button) thisView.findViewById(R.id.updateButton);


        updateButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String whatToSend = "color?h=" + Integer.toHexString(activity.theColor).toUpperCase().substring(2);
                    activity.sendData(whatToSend);
                }
            });
            return thisView;
        }

}