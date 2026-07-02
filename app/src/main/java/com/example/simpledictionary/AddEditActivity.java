package com.example.simpledictionary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "com.example.simpledictionary.EXTRA_ID";
    public static final String EXTRA_WORD = "com.example.simpledictionary.EXTRA_WORD";
    public static final String EXTRA_MEANING = "com.example.simpledictionary.EXTRA_MEANING";

    private EditText etWord;
    private EditText etMeaning;
    private Button btnSave;
    private TextView titleText;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        etWord = findViewById(R.id.et_word);
        etMeaning = findViewById(R.id.et_meaning);
        btnSave = findViewById(R.id.btn_save);
        titleText = findViewById(R.id.title_text);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            titleText.setText("Edit Word");
            etWord.setText(intent.getStringExtra(EXTRA_WORD));
            etMeaning.setText(intent.getStringExtra(EXTRA_MEANING));
        } else {
            titleText.setText("Add Word");
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWord();
            }
        });
    }

    private void saveWord() {
        String word = etWord.getText().toString().trim();
        String meaning = etMeaning.getText().toString().trim();

        if (word.isEmpty()) {
            Toast.makeText(this, "Please enter a word", Toast.LENGTH_SHORT).show();
            return;
        }
        if (meaning.isEmpty()) {
            Toast.makeText(this, "Please enter a meaning", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_WORD, word);
        data.putExtra(EXTRA_MEANING, meaning);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            data.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, data);
        finish();
    }
}
