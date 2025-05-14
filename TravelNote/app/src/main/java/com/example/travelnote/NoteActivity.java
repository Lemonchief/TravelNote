package com.example.travelnote;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.travelnote.database.NoteDao;
import com.example.travelnote.model.Note;

public class NoteActivity extends AppCompatActivity {

    private Note note;
    private int notePosition;

    public static final int REQUEST_CODE_EDIT_NOTE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Получаем данные
        Intent intent = getIntent();
        note = (Note) intent.getSerializableExtra("note");
        notePosition = intent.getIntExtra("position", -1);

        TextView textViewTitle = findViewById(R.id.textTitle);
        TextView textViewDescription = findViewById(R.id.textDescription);
        TextView textViewLatitude = findViewById(R.id.textLatitude2);
        TextView textViewLongitude = findViewById(R.id.textLongitude2);

        if (note != null) {
            textViewTitle.setText(note.getTitle());
            textViewDescription.setText(note.getDescription());
            textViewLatitude.setText(String.valueOf(note.getLatitude()));
            textViewLongitude.setText(String.valueOf(note.getLongitude()));
        }

        // Удаление заметки
        ImageButton imageButtonDelete = findViewById(R.id.imageButtonDelete2);
        imageButtonDelete.setOnClickListener(v -> {
            NoteDao noteDao = new NoteDao(NoteActivity.this);
            noteDao.open();
            noteDao.deleteNote(note.getId());
            noteDao.close();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("position", notePosition);
            resultIntent.putExtra("deleted", true);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        // Редактирование заметки
        ImageButton imageButtonEdit = findViewById(R.id.imageButtonEdit);
        imageButtonEdit.setOnClickListener(v -> {
            Intent editIntent = new Intent(NoteActivity.this, EditNoteActivity.class);
            editIntent.putExtra("note", note);
            editIntent.putExtra("position", notePosition);
            startActivityForResult(editIntent, REQUEST_CODE_EDIT_NOTE);
        });

        // Кнопка домой
        ImageButton imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(v -> {
            Intent goHomeIntent = new Intent(NoteActivity.this, MainActivity.class);
            startActivity(goHomeIntent);
        });

        // Кнопка карта
        ImageButton imageButton2 = findViewById(R.id.imageButton2);
        imageButton2.setOnClickListener(v -> {
            Intent intentMap = new Intent(NoteActivity.this, MapActivity.class);
            startActivity(intentMap);
        });

        // Кнопка настройки
        ImageButton imageButton3 = findViewById(R.id.imageButtonSettings2);
        imageButton3.setOnClickListener(v -> {
            Intent intentSettings = new Intent(NoteActivity.this, SettingsActivity.class);
            startActivity(intentSettings);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_EDIT_NOTE && resultCode == RESULT_OK && data != null) {
            Note updatedNote = (Note) data.getSerializableExtra("note");
            int position = data.getIntExtra("position", -1);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("note", updatedNote);
            resultIntent.putExtra("position", position);
            setResult(EditNoteActivity.RESULT_EDITED, resultIntent);
            finish();
        }
    }
}
