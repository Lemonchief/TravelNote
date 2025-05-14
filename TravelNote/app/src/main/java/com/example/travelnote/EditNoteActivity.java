package com.example.travelnote;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travelnote.database.NoteDao;
import com.example.travelnote.model.Note;

public class EditNoteActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private TextView textViewLatitude;
    private TextView textViewLongitude;

    private int notePosition;
    private Note note; // Сам объект заметки

    public static final int RESULT_EDITED = RESULT_FIRST_USER + 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        editTextTitle = findViewById(R.id.editTitle2);
        editTextDescription = findViewById(R.id.editDescription2);
        textViewLatitude = findViewById(R.id.textLatitudeEdit);
        textViewLongitude = findViewById(R.id.textLongitudeEdit);

        // Получаем объект Note и позицию
        Intent intent = getIntent();
        note = (Note) intent.getSerializableExtra("note");
        notePosition = intent.getIntExtra("position", -1);

        if (note != null) {
            editTextTitle.setText(note.getTitle());
            editTextDescription.setText(note.getDescription());
            textViewLatitude.setText(String.valueOf(note.getLatitude()));
            textViewLongitude.setText(String.valueOf(note.getLongitude()));
        }

        // Кнопка сохранить
        ImageButton buttonSave = findViewById(R.id.imageButtonSave2);
        buttonSave.setOnClickListener(v -> {
            String newTitle = editTextTitle.getText().toString();
            String newDescription = editTextDescription.getText().toString();

            if (note != null) {
                note.setTitle(newTitle);
                note.setDescription(newDescription);

                NoteDao noteDao = new NoteDao(EditNoteActivity.this);
                noteDao.open();
                noteDao.updateNote(note);
                noteDao.close();
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("position", notePosition);
            resultIntent.putExtra("note", note);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        // Кнопка назад (отмена)
        ImageButton buttonBack = findViewById(R.id.imageButtonBack2);
        buttonBack.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}