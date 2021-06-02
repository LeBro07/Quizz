package com.example.flo.anaquiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MainFragment extends Fragment {

    int roundSize;

    //Errechnet Rundenanzahl mit id als Vielfaches von 5
    private int getRoundSize(int id) {
       return roundSize = (id + 1) * 5 + 5;
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       //Auswahl der Rundengröße
        final Spinner roundSelection = getView().findViewById(R.id.roundSelect);
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.roundArray, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assert roundSelection != null;
        roundSelection.setAdapter(arrayAdapter);

        roundSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Rückgabe der Rundenanzahl an QuestionsFragment
                Bundle result = new Bundle();
                result.putInt("roundNumber", getRoundSize(position));
                getParentFragmentManager().setFragmentResult("roundNumber", result);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


}
