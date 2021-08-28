package com.hardy.mlkitlanguagedetection;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.mlkit.nl.languageid.IdentifiedLanguage;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;

import java.util.Locale;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    Button buttonSingle, buttonAll;
    ImageButton imageButton;
    TextView textViewSingle;
    private LanguageIdentifier languageIdentifier;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        languageIdentifier = LanguageIdentification.getClient();
        getLifecycle().addObserver(languageIdentifier);
        editText = findViewById(R.id.editText);
        buttonSingle = findViewById(R.id.buttonSingle);
        buttonAll = findViewById(R.id.buttonAll);
        textViewSingle = findViewById(R.id.textSingleResult);
        imageButton = findViewById(R.id.imageButton);
        scrollView = findViewById(R.id.scrollView);
        scrollView.setVisibility(View.INVISIBLE);

        imageButton.setOnClickListener(view -> {
            scrollView.setVisibility(View.INVISIBLE);
            editText.setText("");
            textViewSingle.setText("");
        });

        buttonSingle.setOnClickListener(view -> {
            hideKeyboard();
            String input = getInputText();
            if (input.isEmpty()) {
                return;
            }
            checkLanguage(input);
        });

        buttonAll.setOnClickListener(view -> {
            hideKeyboard();
            String input = getInputText();
            if (input.isEmpty()) {
                return;
            }
            checkAllLanguage(input);
        });
    }


    private String getInputText() {
        String input = editText.getText().toString();
        if (input.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter a String !!!", Toast.LENGTH_LONG).show();
            return input;
        }
        return input;
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    private void checkLanguage(String textLanguage) {
        scrollView.setVisibility(View.VISIBLE);
        languageIdentifier.identifyLanguage(textLanguage)
                .addOnSuccessListener(s -> {
                    if (s.equals("und")) {
                        textViewSingle.setText(getString(R.string.identificationFailed));
                    } else {

                        Locale locale = new Locale(s);
                        textViewSingle.setText("Language : " + locale.getDisplayLanguage());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(TAG, "Language identification error", e);
                textViewSingle.setText("Error : " + e);

            }
        });
    }


    private void checkAllLanguage(String input) {
        scrollView.setVisibility(View.VISIBLE);
        languageIdentifier
                .identifyPossibleLanguages(input)
                .addOnSuccessListener(
                        identifiedLanguages -> {

                            String output = "";
                            for (IdentifiedLanguage identifiedLanguage : identifiedLanguages) {
                                Locale locale = new Locale(identifiedLanguage.getLanguageTag().toString());
                                output += locale.getDisplayLanguage()
                                        + " ("
                                        + identifiedLanguage.getConfidence()
                                        + ")\n ";
                            }
                            textViewSingle.setText("Possible Languages :\n\n" + output);//.substring(0, output.length() - 2));
                        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.e(TAG, "Language identification error", e);
                textViewSingle.setText("Error : " + e);

            }
        });
    }
}