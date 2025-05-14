package com.example.travelnote;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelnote.adapter.NoteAdapter;
import com.example.travelnote.database.NoteDao;
import com.example.travelnote.model.Note;
import com.example.travelnote.api.ApiClient;
import com.example.travelnote.api.WeatherApi;
import com.example.travelnote.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_VIEW_NOTE = 2;

    private ArrayList<Note> noteList;
    private NoteAdapter noteAdapter;
    private NoteDao noteDao;
    private TextView weatherTempTextView;
    private ImageView weatherIconImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noteDao = new NoteDao(this);
        noteDao.open();

        // Настройка RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewNotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(MainActivity.this, noteList);
        recyclerView.setAdapter(noteAdapter);

        // Заполнение заметками из базы данных
        loadNotesFromDatabase();

        weatherTempTextView = findViewById(R.id.textViewWeather);
        weatherIconImageView = findViewById(R.id.imageViewWeather);

        double latitude = 55.994446;
        double longitude = 92.797586;
        loadWeather(latitude, longitude);


        final boolean[] deleteMode = {false};
        ImageButton deleteButton = findViewById(R.id.imageButtonDelete1);
        deleteButton.setOnClickListener(v -> {
            deleteMode[0] = !deleteMode[0];
            noteAdapter.setDeleteMode(deleteMode[0]);

            if (deleteMode[0]) {
                Toast.makeText(MainActivity.this, "Режим удаления включён", Toast.LENGTH_SHORT).show();
                noteAdapter.setOnNoteDeleteListener((note, position) -> {
                    noteDao.deleteNote(note.getId()); // Удаление из базы данных
                    noteList.remove(position); // Удаление из списка
                    noteAdapter.notifyItemRemoved(position);
                    Toast.makeText(MainActivity.this, "Заметка удалена", Toast.LENGTH_SHORT).show();
                });
            } else {
                noteAdapter.setOnNoteDeleteListener(null); // отключаем обработку удаления
                Toast.makeText(MainActivity.this, "Режим удаления выключен", Toast.LENGTH_SHORT).show();
            }
        });

        // Кнопка добавления новой заметки
        ImageButton imageButtonAdd = findViewById(R.id.imageButtonAdd); // Кнопка +
        imageButtonAdd.setOnClickListener(v -> {
            noteAdapter.setDeleteMode(false);
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
        });

        // Кнопки снизу
        ImageButton imageButton2 = findViewById(R.id.imageButtonMap1);
        imageButton2.setOnClickListener(v -> {
            noteAdapter.setDeleteMode(false);
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
        });

        ImageButton imageButton9 = findViewById(R.id.imageButtonSettings1);
        imageButton9.setOnClickListener(v -> {
            noteAdapter.setDeleteMode(false);
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    // Получаем результат из AddNoteActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("title");
            String description = data.getStringExtra("description");
            String date = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(new Date());
            double latitude = data.getDoubleExtra("latitude", 0.0);
            double longitude = data.getDoubleExtra("longitude", 0.0);

            Note newNote = new Note(title, description, date, latitude, longitude);
            noteDao.insertNote(newNote);
            noteList.add(0, newNote);
            noteAdapter.notifyItemInserted(0);


            Toast.makeText(MainActivity.this, "Note added", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == REQUEST_CODE_VIEW_NOTE && resultCode == RESULT_OK && data != null) {
            int position = data.getIntExtra("position", -1);
            if (position != -1) {
                noteList.remove(position);
                noteAdapter.notifyItemRemoved(position);
            }
        }

        if (requestCode == REQUEST_CODE_VIEW_NOTE && resultCode == EditNoteActivity.RESULT_EDITED && data != null) {
            int position = data.getIntExtra("position", -1);
            Note updatedNote = (Note) data.getSerializableExtra("note");

            if (position != -1 && updatedNote != null) {
                noteList.set(position, updatedNote);
                noteAdapter.notifyItemChanged(position);
            }
        }
    }

    private void loadWeather(double latitude, double longitude) {
        WeatherApi api = ApiClient.getClient().create(WeatherApi.class);
        api.getCurrentWeather(latitude, longitude).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    double temperature = response.body().current_weather.temperature;
                    int code = response.body().current_weather.weathercode;

                    weatherTempTextView.setText(String.format(Locale.getDefault(), "%.0f°C", temperature));

                    // Установка иконки в зависимости от кода погоды
                    int iconRes = getWeatherIcon(code);
                    weatherIconImageView.setImageResource(iconRes);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                weatherTempTextView.setText("N/A");
            }
        });
    }

    // Подберите иконки под коды: https://open-meteo.com/en/docs
    private int getWeatherIcon(int code) {
        Log.d("Weather", "Weather is: " + code);
        if (code == 0) return R.drawable.sunny;
        else if (code >= 1 && code <= 3) return R.drawable.cloudy;
        else if (code >= 45 && code <= 48) return R.drawable.foggy;
        else if (code >= 51 && code <= 67) return R.drawable.rain;
        else if (code >= 71 && code <= 77) return R.drawable.snow;
        else if (code >= 80 && code <= 86) return R.drawable.shower;
        else if (code >= 95) return R.drawable.thunder;
        return R.drawable.cloudy;
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteAdapter.setDeleteMode(false); // сброс при возвращении
    }

    // Загрузка заметок из базы данных
    private void loadNotesFromDatabase() {
        noteList.clear();
        noteList.addAll(noteDao.getAllNotes());
        noteAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        noteDao.close();
    }
}
