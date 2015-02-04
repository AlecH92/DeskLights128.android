package com.hilltoprobotics.desklights128.phone;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AlertFragment extends Fragment {

    public Button updateButton;
    public TextView delayTime;
    private View thisView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_alertfragment, container, false);
        final MainActivity activity = (MainActivity) getActivity();

        updateButton = (Button) thisView.findViewById(R.id.updateButton);
        delayTime = (EditText) thisView.findViewById(R.id.delayTime);


        updateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String whatToSend = "alert?h=" + Integer.toHexString(activity.theColor).toUpperCase().substring(2) + "&d=" + delayTime.getText();
                activity.sendData(whatToSend);
            }
        });
        return thisView;
    }

}