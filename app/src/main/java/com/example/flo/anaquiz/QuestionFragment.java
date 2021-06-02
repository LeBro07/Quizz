package com.example.flo.anaquiz;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.Random;

public class QuestionFragment extends Fragment {

    private TextView questionView;
    private EditText answerText, answerText2, answerNumber, answerNumber2;
    private Button submitButton, quitButton;
    private SubsamplingScaleImageView imageView;
    private RadioGroup radio_g;
    private RadioButton rb1, rb2, rb3, rb4;
    private int numberOfQuestions;

    //Non-shallow copy des gemischten Fragen Arrays um Antworten lesen zu können
    private void genAnswers(String [][] arrFrom, String [][] arrTo) {
        for (int i = 0; i < arrFrom.length; i++) {
            for (int j = 0; j < arrFrom[i].length; j++) {
                arrTo[i][j] = arrFrom[i][j];
            }
        }
    }

    //Mischt Array 1 und Array 2 nach Zeilen, dann Array 1 nach Spalten (außer ersten Drei)
    private void shuffleArray(String[][] arr1, String [][] arr2) {
        Random rand = new Random();

        //Tauscht nach Zeilen - alle
        for (int i = arr1.length - 1; i > 0; i--) {

            int index = rand.nextInt(i + 1);

            for (int j = 0; j < arr1[i].length; j++) {
                String a = arr1[i][j];
                String b = arr2[i][j];
                arr1[i][j] = arr1[index][j];
                arr2[i][j] = arr2[index][j];
                arr1[index][j] = a;
                arr2[index][j] = b;
            }
        }

        //Tauscht nach Spalten (ab Spalte 3)
        for (int i = 0; i < arr1.length; i++) {

            for (int j = 2; j < arr1[i].length; j++) {
                int index = rand.nextInt(j - 1) + 2;
                String b = arr1[i][j];
                arr1[i][j] = arr1[i][index];
                arr1[i][index] = b;
            }
        }
    }


    //TODO - Überarbeiten und als Datenbank?
    /*  Liste der Fragen
        {"frage", "art", "antwort", "option 1", "option 2", "option 3"}
        [0] Frage
        [1] Art (0 = Multi, 1 = Text, 2 = Zahl, 3 = Bild)
        [2] Antwort
        [3]-[5] Optionen
     */
    private String[][] questionsMulti = {
            {"frage1", "0","antwort1", "11", "12", "13"},
            {"frage2", "0","antwort2", "21", "22", "23"},
            {"frage3", "0","antwort3", "31", "32", "33"},
            {"frage4", "0","antwort4", "41", "42", "43"},
            {"frage5", "0","antwort5", "51", "52", "53"},
            {"frage6", "0","antwort6", "61", "62", "63"},
            {"frage7", "0","antwort7", "71", "72", "73"},
            {"Was sagt man zur Begrüßung?", "1", "hallo", "", "", ""},
            {"Was sagt man zum Abschied?", "1", "tschüss", "", "", ""},
            {"Was sagt man an der See?", "1", "moin moin", "", "", ""},
            {"Der Sinn des Lebens?", "2","42", "", "", ""},
            {"Der wievielte Monat ist der Juli?", "2", "7", "", "", ""},
            {"Was ergibt 10 mal 10?", "2", "100", "", "", ""},
            {"Was ist auf dem Bild zu sehen?", "3", "stern", "", "", ""},
            {"Wieviele Knochen hat eine Hand?","2", "53", "", "", ""}
    };

    //Array für Antworten
    private String [][]  questionRight = new String[questionsMulti.length][questionsMulti[0].length];
    //Für Rundenanzahl
    private int flag = 0;
    //Antworten
    private static int correct = 0, wrong = 0;
    private boolean correctAnswer = false, checked = false;

    //Passt QuestionFragments Elemente an nächste Frage an, abhängig von Fragenart (siehe questionsMulti)
    private void nextQuestion() {
        questionView.setText(questionsMulti[flag][0]);
        rb1.setText(questionsMulti[flag][2]);
        rb1.setTextColor(Color.BLACK);
        rb1.setClickable(true);
        rb2.setText(questionsMulti[flag][3]);
        rb2.setTextColor(Color.BLACK);
        rb2.setClickable(true);
        rb3.setText(questionsMulti[flag][4]);
        rb3.setTextColor(Color.BLACK);
        rb3.setClickable(true);
        rb4.setText(questionsMulti[flag][5]);
        rb4.setTextColor(Color.BLACK);
        rb4.setClickable(true);
        radio_g.setClickable(true);
        answerNumber.setText("");
        answerNumber.setTextColor(Color.BLACK);
        EnableDisableEditText(true, answerNumber);
        answerNumber2.setText("");
        answerNumber2.setVisibility(View.GONE);
        answerText.setText("");
        answerText.setTextColor(Color.BLACK);
        EnableDisableEditText(true, answerText);
        answerText2.setText("");
        answerText2.setVisibility(View.GONE);
        radio_g.clearCheck();
        submitButton.setText(getString(R.string.buttonCheck));

        switch (questionsMulti[flag][1]) {
            case "0":
                answerNumber.setVisibility(View.GONE);
                answerText.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                rb1.setVisibility(View.VISIBLE);
                rb2.setVisibility(View.VISIBLE);
                rb3.setVisibility(View.VISIBLE);
                rb4.setVisibility(View.VISIBLE);
                break;

            case "1":
                answerNumber.setVisibility(View.GONE);
                answerText.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                rb1.setVisibility(View.GONE);
                rb2.setVisibility(View.GONE);
                rb3.setVisibility(View.GONE);
                rb4.setVisibility(View.GONE);
                break;

            case "2":
                answerNumber.setVisibility(View.VISIBLE);
                answerText.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                rb1.setVisibility(View.GONE);
                rb2.setVisibility(View.GONE);
                rb3.setVisibility(View.GONE);
                rb4.setVisibility(View.GONE);
                break;

            case "3":
                answerNumber.setVisibility(View.GONE);
                answerText.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                rb1.setVisibility(View.GONE);
                rb2.setVisibility(View.GONE);
                rb3.setVisibility(View.GONE);
                rb4.setVisibility(View.GONE);
                break;
        }
    }

    //Überprüfen der Antwort auf aktuelle Frage
    private void updateQuestion() {
        //nach falscher Antwort und Ausgabe der richtigen Antwort
        if (checked) {
            checked = false;
            flag++;
            //Überprüfen auf Ende der Runde
            if (flag < numberOfQuestions) {
                nextQuestion();
            } else {
                //Paket mit Ergebnissen (außer Zeit) an MainActivity
                Bundle result = new Bundle();
                result.putInt("wrongAnswers", wrong);
                result.putInt("rightAnswers", correct);
                result.putInt("allAnswers", numberOfQuestions);
                result.putBoolean("success", true);
                getParentFragmentManager().setFragmentResult("resultBundle1", result);
            }
        } else {
            //Richtig geantwortet
            if (correctAnswer) {
                correct++;
                flag++;
                //Überprüfen auf Ende der Runde
                if (flag < numberOfQuestions) {
                    nextQuestion();
                } else {
                    //Paket mit Ergebnissen (außer Zeit) an MainActivity
                    Bundle result = new Bundle();
                    result.putInt("wrongAnswers", wrong);
                    result.putInt("rightAnswers", correct);
                    result.putInt("allAnswers", numberOfQuestions);
                    result.putBoolean("success", true);
                    getParentFragmentManager().setFragmentResult("resultBundle1", result);
                }
            } else {
                //Falsch geantwortet
                submitButton.setText(getString(R.string.buttonNext));
                wrong++;
                //Ausgabe der richtigen Antwort
                switch (questionRight[flag][1]) {
                    case "0":
                        Toast.makeText(getContext(), "Falsche Antwort", Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < 4; i++) {
                            RadioButton radio = (RadioButton) radio_g.getChildAt(i);
                            RadioButton selected = getView().findViewById(radio_g.getCheckedRadioButtonId());
                            selected.setTextColor(Color.parseColor("#FF3333"));

                            if (radio.getText().toString().equalsIgnoreCase(questionRight[flag][2])) {
                                radio.setChecked(true);
                                radio.setTextColor(Color.parseColor("#088C1E"));

                                break;
                            }
                            rb1.setClickable(false);
                            rb2.setClickable(false);
                            rb3.setClickable(false);
                            rb4.setClickable(false);
                            radio_g.setClickable(false);
                        }
                        break;

                    case "1":
                        Toast.makeText(getContext(), "Falsche Antwort", Toast.LENGTH_SHORT).show();
                        answerText.setTextColor(Color.parseColor("#FF3333"));
                        EnableDisableEditText(false, answerText);
                        answerText2.setText(questionRight[flag][2]);
                        answerText2.setVisibility(View.VISIBLE);
                        break;

                    case "2":
                        Toast.makeText(getContext(), "Falsche Antwort", Toast.LENGTH_SHORT).show();
                        answerNumber.setTextColor(Color.parseColor("#FF3333"));
                        EnableDisableEditText(false, answerNumber);
                        answerNumber2.setText(questionRight[flag][2]);
                        answerNumber2.setVisibility(View.VISIBLE);
                        break;

                    case "3":
                        Toast.makeText(getContext(), "Falsche Antwort", Toast.LENGTH_SHORT).show();
                        answerText.setTextColor(Color.parseColor("#FF3333"));
                        EnableDisableEditText(false, answerText);
                        answerText2.setText(questionRight[flag][2]);
                        answerText2.setVisibility(View.VISIBLE);
                        break;
                }
                //Ausgabe ist erfolgt
                checked = true;
            }
        }
    }

    //Freigabe/Sperre eines Eingabefeldes
    private void EnableDisableEditText(boolean isEnabled, EditText editText) {
        editText.setFocusable(isEnabled);
        editText.setFocusableInTouchMode(isEnabled);
        editText.setClickable(isEnabled);
        editText.setLongClickable(isEnabled);
        editText.setCursorVisible(isEnabled);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Listener für Rundenanzahl von MainFragment
        getParentFragmentManager().setFragmentResultListener("roundNumber", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                numberOfQuestions = result.getInt("roundNumber");
            }
        });
        correct = 0;
        wrong = 0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        //Mischen der Fragen und erzeugen der passenden Antworten
        genAnswers(questionsMulti, questionRight);
        shuffleArray(questionsMulti, questionRight);

        submitButton = getView().findViewById(R.id.buttonNext);
        quitButton = getView().findViewById(R.id.buttonQuit);

        questionView = getView().findViewById(R.id.questionView);

        radio_g = getView().findViewById(R.id.radioAnswer);
        rb1 = getView().findViewById(R.id.radioButton);
        rb2 = getView().findViewById(R.id.radioButton2);
        rb3 = getView().findViewById(R.id.radioButton3);
        rb4 = getView().findViewById(R.id.radioButton4);

        answerText = getView().findViewById(R.id.answerText);
        answerText2 = getView().findViewById(R.id.answerText2);
        EnableDisableEditText(false, answerText2);
        answerNumber = getView().findViewById(R.id.answerNumber);
        answerNumber2 = getView().findViewById(R.id.answerNumber2);
        EnableDisableEditText(false, answerNumber2);

        imageView = getView().findViewById(R.id.picture);

        //Nächste Frage anzeigen
        nextQuestion();

        //Click auf "Nächste Frage"
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Entscheiden welche Art von Frage aktuell angezeigt wird und Fehlermeldung, wenn nichts eingetragen/ausgewählt ist
                //Lesen der Antwort aus questionRight
                switch (questionsMulti[flag][1]) {
                    //Multiplechoice
                    case "0":
                        if (radio_g.getCheckedRadioButtonId() == -1) {
                            Toast.makeText(getContext(), "Bitte wähle etwas aus!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        RadioButton selected = getView().findViewById(radio_g.getCheckedRadioButtonId());
                        String selectedText = selected.getText().toString();

                        correctAnswer = selectedText.equals(questionRight[flag][2]);
                        break;

                    //Texte
                    case "1":
                        String answerString = answerText.getText().toString();
                        if (answerString.equalsIgnoreCase("")) {
                            Toast.makeText(getContext(), "Bitte gibt etwas ein!1", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        correctAnswer = answerString.equalsIgnoreCase(questionRight[flag][2]);
                        break;

                    //Zahlen
                    case "2":
                        String answerInt = answerNumber.getText().toString();
                        if (answerInt.equalsIgnoreCase("")) {
                            Toast.makeText(getContext(), "Bitte gibt etwas ein!2", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int number = Integer.parseInt(answerInt);
                        int answer = Integer.parseInt(questionRight[flag][2]);
                        correctAnswer = number == answer;
                        break;

                    case "3":
                        String answerPic = answerText.getText().toString();
                        if (answerPic.equalsIgnoreCase("")) {
                            Toast.makeText(getContext(), "Bitte gibt etwas ein!3", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        correctAnswer = answerPic.equalsIgnoreCase(questionRight[flag][2]);
                        break;
                }

                //Überpfüfung der Antwort
                updateQuestion();
            }
        });

        //Click auf "Abbrechen"
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Paket mit Ergebnissen (außer Zeit) an MainActivity
                Bundle result = new Bundle();
                result.putInt("wrongAnswers", wrong);
                result.putInt("rightAnswers", correct);
                result.putInt("allAnswers", numberOfQuestions);
                result.putBoolean("success", false);
                getParentFragmentManager().setFragmentResult("resultBundle1", result);
            }
        });
    }
}