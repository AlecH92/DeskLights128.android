package com.hilltoprobotics.desklights128.phone;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class ColorPixelFragment extends Fragment {

    public Button updateButton;
    public TextView xValue;
    public TextView yValue;
    private View thisView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_colorpixel, container, false);
        final MainActivity activity = (MainActivity) getActivity();

        updateButton = (Button) thisView.findViewById(R.id.updateButton);
        xValue = (EditText) thisView.findViewById(R.id.xValue);
        yValue = (EditText) thisView.findViewById(R.id.yValue);


        updateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String whatToSend = "pixel?h=" + Integer.toHexString(activity.theColor).toUpperCase().substring(2) + "&x=" + xValue.getText() + "&y=" + yValue.getText();
                activity.sendData(whatToSend);
            }
        });
        return thisView;
    }

}