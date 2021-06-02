package com.example.flo.anaquiz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.preference.PreferenceManager;

public class ResultFragment extends Fragment {

    private int correct = 0, wrong = 0, all = 0;
    private long resultTime = 0, bestTime = 0;
    private boolean success = false, allRight = false;
    private SharedPreferences sharedPreferences;

    //Wandelt Rundenzahl in entsprechendes Wort um und liest Bestzeit aus
    private String getRoundSize() {
        switch (all) {
            case 10:
                bestTime = sharedPreferences.getLong("bestTimeSmall", 0);
                return "klein";
            case 15:
                bestTime = sharedPreferences.getLong("bestTimeNormal", 0);
                return "mittel";
            case 20:
                bestTime = sharedPreferences.getLong("bestTimeLarge", 0);
                return "groß";
            default:
                bestTime = 0;
                return "Fehler";
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.edit().putLong("bestTimeSmall", 100000).apply();

        //Versteckt Fragment beim Erstellen
        getParentFragmentManager().beginTransaction().hide(getParentFragmentManager().findFragmentByTag("RESULT_FRAGMENT")).commit();

        //Listener für resultBundle2 von MainActivity, enthält alle Ergebnisse
        getParentFragmentManager().setFragmentResultListener("resultBundle2", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                TextView correctView, wrongView, allView, allRightView, resultTimeView, bestView, newBestView, headerView;

                success = result.getBoolean("success");
                correct = result.getInt("rightAnswers");
                wrong = result.getInt("wrongAnswers");
                all = result.getInt("allAnswers");
                resultTime = result.getLong("time");
                resultTime = resultTime + wrong * 5000;
                allRight = correct == all;

                //Entfernt QuestionFragment und macht sich selbst sichtbar
                getParentFragmentManager().beginTransaction()
                        .remove(getParentFragmentManager().findFragmentByTag("QUESTION_FRAGMENT"))
                        .show(getParentFragmentManager().findFragmentByTag("RESULT_FRAGMENT"))
                        .setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.slide_out_left_to_right)
                        .commit();

                //Überschrift setzen
                headerView = getView().findViewById(R.id.resultHeader);
                if (success) {
                    headerView.setText(R.string.resultDone);
                } else {
                    headerView.setText(R.string.resultCanceled);
                }

                //Antworten auswerten
                correctView = getView().findViewById(R.id.resultRight);
                correctView.setText(getString(R.string.rightAns, correct));
                wrongView = getView().findViewById(R.id.resultWrong);
                wrongView.setText(getString(R.string.wrongAns, wrong));
                allView = getView().findViewById(R.id.resultAll);
                allView.setText(getString(R.string.allAns, getRoundSize()));
                allRightView = getView().findViewById(R.id.allRight);
                if (allRight) {
                    allRightView.setVisibility(View.VISIBLE);
                }
                else {
                    allRightView.setVisibility(View.INVISIBLE);
                }

                //Zeit auswerten
                int seconds = (int) (resultTime / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                resultTimeView = getView().findViewById(R.id.resultTimeView);
                resultTimeView.setText(getString(R.string.timeResult, minutes, seconds));

                bestView = getView().findViewById(R.id.bestTimeView);
                int secondsBest = (int) (bestTime / 1000);
                int minutesBest = secondsBest / 60;
                secondsBest = secondsBest % 60;
                bestView.setText(getString(R.string.timeBest, minutesBest, secondsBest));
                newBestView = getView().findViewById(R.id.newBest);

                //Erstmaliges Spielen
                if (minutesBest == 0 && secondsBest == 0) {
                    newBestView.setVisibility(View.VISIBLE);
                    switch (all) {
                        case 10:
                            sharedPreferences.edit().putLong("bestTimeSmall", resultTime).apply();
                            break;
                        case 15:
                            sharedPreferences.edit().putLong("bestTimeNormal", resultTime).apply();
                            break;
                        case 20:
                            sharedPreferences.edit().putLong("bestTimeLarge", resultTime).apply();
                            break;
                    }
                }
                else {
                    //Zeiten vergleichen, neue Bestzeit setzen
                    if (minutes < minutesBest && seconds < secondsBest) {
                        newBestView.setVisibility(View.VISIBLE);
                        switch (all) {
                            case 10:
                                sharedPreferences.edit().putLong("bestTimeSmall", resultTime).apply();
                                break;
                            case 15:
                                sharedPreferences.edit().putLong("bestTimeNormal", resultTime).apply();
                                break;
                            case 20:
                                sharedPreferences.edit().putLong("bestTimeLarge", resultTime).apply();
                                break;
                        }
                    } else {
                        newBestView.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }
}
