package com.example.travelnote;

import android.content.Context;
import android.content.Intent;  // Для работы с Intent
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;  // Для View и View.OnClickListener
import android.widget.ImageButton;  // Для использования ImageButton
import android.Manifest;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.ScreenPoint;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapWindow;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.runtime.image.ImageProvider;

import com.example.travelnote.database.NoteDao;
import com.example.travelnote.model.Note;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MapActivity extends AppCompatActivity {
    private MapView mapView;
    private MapObjectCollection mapObjects;

    private View cardView;
    private TextView previewTitle, previewDesc;
    private Note activeNote = null;
    private Point activeNotePoint = null;

    public class MapManager {
        private final Context context;
        private final MapView mapView;

        public MapManager(Context context, MapView mapView) {
            this.context = context;
            this.mapView = mapView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
//            return;
//        }
//
//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        if (location == null) {
//            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        }

        mapView = findViewById(R.id.mapview);

//        if (location != null) {
//            double latitude = location.getLatitude();
//            double longitude = location.getLongitude();
//            mapView.getMap().move(
//                    new CameraPosition(new Point(latitude, longitude), 17.0f, 0.0f, 0.0f),
//                    new Animation(Animation.Type.SMOOTH, 1),
//                    null
//            );
//        }

        cardView = findViewById(R.id.notePreviewCard);
        previewTitle = findViewById(R.id.previewTitle);
        previewDesc = findViewById(R.id.previewDesc);

        cardView.setOnClickListener(v -> {
            if (activeNote != null) {
                Intent intent = new Intent(this, NoteActivity.class);
                intent.putExtra("note_id", activeNote.getId());
                startActivity(intent);
            }
        });

        mapView.getMap().move(
                new CameraPosition(new Point(55.994446, 92.797586), 16.5f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 1),
                null
        );

        mapView.getMap().addCameraListener(cameraListener);

        mapObjects = mapView.getMap().getMapObjects().addCollection();

        NoteDao noteDao = new NoteDao(this);
        noteDao.open(); // обязательно открываем базу
        List<Note> notes = noteDao.getAllNotes();
        noteDao.close(); // и закрываем после работы

        for (Note note : notes) {
            if (note.getLatitude() != 0 && note.getLongitude() != 0) {
                PlacemarkMapObject placemark = mapObjects.addPlacemark(
                        new Point(note.getLatitude(), note.getLongitude()),
                        ImageProvider.fromResource(this, R.drawable.dot),
                        new IconStyle().setScale(0.1f).setAnchor(new PointF(0.5f, 0.5f))
                );
                placemark.setUserData(note); // сохраняем объект, чтобы извлечь его в listener
                placemark.addTapListener(placemarkTapListener);
            }
        }


        // Находим кнопку и назначаем обработчик клика
        ImageButton imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переход в MainActivity
                Intent intent = new Intent(MapActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Находим кнопку и назначаем обработчик клика
        ImageButton imageButton3 = findViewById(R.id.imageButtonSettings4);
        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Переход в SettingsActivity
                Intent intent = new Intent(MapActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    private final CameraListener cameraListener = new CameraListener() {
        @Override
        public void onCameraPositionChanged(@NonNull Map map, @NonNull CameraPosition cameraPosition,
                                            @NonNull CameraUpdateReason cameraUpdateReason, boolean finished) {
            updateCardPosition(); // та самая функция, которую ты поправил
        }
    };


    private final MapObjectTapListener placemarkTapListener = (mapObject, point) -> {
        Note note = (Note) mapObject.getUserData();
        activeNote = note;
        activeNotePoint = point;

        previewTitle.setText(note.getTitle());
        previewDesc.setText(note.getDescription());
        cardView.setVisibility(View.VISIBLE);

        updateCardPosition();
        return true;
    };

    private void updateCardPosition() {
        if (activeNotePoint == null) return;

        ScreenPoint screenPoint = mapView.getMapWindow().worldToScreen(activeNotePoint);
        float x = (float) screenPoint.getX();
        float y = (float) screenPoint.getY();

        float offsetY = 200f;

        cardView.setX(x - cardView.getWidth() / 2f);
        cardView.setY(y - cardView.getHeight() + offsetY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            recreate();  // Перезапускаем activity, чтобы заново попытаться получить позицию
        }
    }

}