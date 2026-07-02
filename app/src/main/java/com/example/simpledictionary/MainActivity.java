package com.example.simpledictionary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_WORD_REQUEST = 1;
    public static final int EDIT_WORD_REQUEST = 2;
    public static final String KEY_WORD_LIST = "word_list";
    private static final String SHARED_PREFS_NAME = "simple_dictionary_prefs";
    private static final String KEY_WORDS_JSON = "words_json";

    private ArrayList<Word> wordList;
    private WordAdapter adapter;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gson = new Gson();

        if (savedInstanceState != null) {
            wordList = (ArrayList<Word>) savedInstanceState.getSerializable(KEY_WORD_LIST);
        } else {
            loadData();
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new WordAdapter(wordList);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                startActivityForResult(intent, ADD_WORD_REQUEST);
            }
        });

        adapter.setOnItemClickListener(new WordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                intent.putExtra(AddEditActivity.EXTRA_ID, position);
                intent.putExtra(AddEditActivity.EXTRA_WORD, wordList.get(position).getWord());
                intent.putExtra(AddEditActivity.EXTRA_MEANING, wordList.get(position).getMeaning());
                startActivityForResult(intent, EDIT_WORD_REQUEST);
            }

            @Override
            public void onItemLongClick(int position) {
                wordList.remove(position);
                adapter.notifyItemRemoved(position);
                saveData();
                Toast.makeText(MainActivity.this, R.string.word_deleted, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_WORD_LIST, wordList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_WORD_REQUEST && resultCode == RESULT_OK && data != null) {
            String word = data.getStringExtra(AddEditActivity.EXTRA_WORD);
            String meaning = data.getStringExtra(AddEditActivity.EXTRA_MEANING);

            wordList.add(new Word(word, meaning));
            adapter.notifyItemInserted(wordList.size() - 1);
            saveData();
            Toast.makeText(this, R.string.word_added, Toast.LENGTH_SHORT).show();
        } else if (requestCode == EDIT_WORD_REQUEST && resultCode == RESULT_OK && data != null) {
            int position = data.getIntExtra(AddEditActivity.EXTRA_ID, -1);
            if (position != -1) {
                String word = data.getStringExtra(AddEditActivity.EXTRA_WORD);
                String meaning = data.getStringExtra(AddEditActivity.EXTRA_MEANING);

                wordList.get(position).setWord(word);
                wordList.get(position).setMeaning(meaning);
                adapter.notifyItemChanged(position);
                saveData();
                Toast.makeText(this, R.string.word_updated, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadData() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(KEY_WORDS_JSON, null);
        
        if (json != null) {
            Type type = new TypeToken<ArrayList<Word>>() {}.getType();
            wordList = gson.fromJson(json, type);
        } else {
            wordList = new ArrayList<>();
        }
    }

    private void saveData() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        String json = gson.toJson(wordList);
        editor.putString(KEY_WORDS_JSON, json);
        editor.apply();
    }
}
