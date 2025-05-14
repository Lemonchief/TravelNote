package com.example.travelnote;

import android.content.Intent;  // Для работы с Intent
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;  // Для View и View.OnClickListener
import android.widget.EditText;
import android.widget.ImageButton;  // Для использования ImageButton
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;

public class AddNoteActivity extends AppCompatActivity {
    private double latitude = 0.0;
    private double longitude= 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_note);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            getCurrentLocation();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Кнопка "Назад"
        ImageButton imageButtonBack = findViewById(R.id.imageButtonBack1);
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Кнопка "Сохранить"
        ImageButton imageButtonSave = findViewById(R.id.imageButtonSave1);
        imageButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получаем текст из полей
                EditText titleEdit = findViewById(R.id.editTitle1);
                EditText descEdit = findViewById(R.id.editDescription1);

                String title = titleEdit.getText().toString();
                String description = descEdit.getText().toString();

                // Проверка на пустые поля
                if (title.isEmpty() || description.isEmpty()) {
                    Toast.makeText(AddNoteActivity.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Получаем текущую дату
                String date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());


                // Передаём данные обратно в MainActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("title", title);
                resultIntent.putExtra("description", description);
                resultIntent.putExtra("date", date);
                resultIntent.putExtra("latitude", latitude);
                resultIntent.putExtra("longitude", longitude);

                setResult(RESULT_OK, resultIntent);
                finish(); // Закрываем AddNoteActivity
            }
        });

        ImageButton imageButtonSelectPoint = findViewById(R.id.btnSetLocation);
        imageButtonSelectPoint.setOnClickListener(v -> {
            Intent intent = new Intent(AddNoteActivity.this, MapPointPickerActivity.class);
            startActivityForResult(intent, 1001); // REQUEST_CODE = 1001
        });

        // Кнопка "Главная"
        ImageButton imageButton = findViewById(R.id.imageButtonHome);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переход в MainActivity
                Intent intent = new Intent(AddNoteActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Кнопка "Карта"
        ImageButton imageButton2 = findViewById(R.id.imageButtonMap);
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переход в MapActivity
                Intent intent = new Intent(AddNoteActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        // Кнопка "Настройки"
        ImageButton imageButton3 = findViewById(R.id.imageButtonSettings3);
        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переход в SettingsActivity
                Intent intent = new Intent(AddNoteActivity.this, SettingsActivity.class);
                startActivity(intent);
            }


        });
    }
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (location != null) {
//                latitude = location.getLatitude();
//                longitude = location.getLongitude();
                latitude = 55.994446;
                longitude = 92.797586;
                Log.d("location", "latitude is: " + latitude);
                Log.d("location", "latitude is: " + latitude);



                // Обновим UI

                TextView textLat = findViewById(R.id.textLatitude1);
                TextView textLon = findViewById(R.id.textLongitude1);
                textLat.setText(String.valueOf(latitude));
                textLon.setText(String.valueOf(longitude));


            } else {
                Toast.makeText(this, "Не удалось получить координаты", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            Toast.makeText(this, "Разрешение на геолокацию не получено", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            latitude = data.getDoubleExtra("latitude", 0.0);
            longitude = data.getDoubleExtra("longitude", 0.0);

            // Отобразим координаты в TextView
            TextView textLat = findViewById(R.id.textLatitude1);
            TextView textLon = findViewById(R.id.textLongitude1);
            textLat.setText(String.valueOf(latitude));
            textLon.setText(String.valueOf(longitude));
        }
    }


}