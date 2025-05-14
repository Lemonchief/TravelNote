package com.example.travelnote;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

public class MapPointPickerActivity extends AppCompatActivity {

    private MapView mapView;
    private MapObjectCollection mapObjects;
    private Point selectedPoint = null;

    private final InputListener inputListener = new InputListener() {
        @Override
        public void onMapTap(Map map, Point point) {
            Log.d("MapPointPicker", "Tapped at: " + point.getLatitude() + ", " + point.getLongitude());
            selectedPoint = point;
            mapObjects.clear(); // удаляем старую метку

            ImageProvider icon = ImageProvider.fromResource(
                    MapPointPickerActivity.this,
                    R.drawable.dot
            );

            PlacemarkMapObject placemark = mapObjects.addPlacemark(point, icon);
            placemark.setIconStyle(new IconStyle()
                    .setScale(0.05f) // масштаб иконки
                    .setAnchor(new PointF(0.5f, 0.5f)) // центр якоря
            );
        }

        @Override
        public void onMapLongTap(Map map, Point point) {
            // по желанию можно реализовать долгий тап
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_map_point_picker);

        mapView = findViewById(R.id.mapview2);
        mapObjects = mapView.getMap().getMapObjects().addCollection();

        // Центрируем карту на Москву или любую стартовую точку
        mapView.getMap().move(
                new CameraPosition(new Point(55.994446, 92.797586), 16.5f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 1),
                null
        );

        mapView.getMap().addInputListener(inputListener);

        ImageButton btnConfirm = findViewById(R.id.imageButtonSaveMap);
        btnConfirm.setOnClickListener(v -> {
            if (selectedPoint != null) {
                Intent result = new Intent();
                result.putExtra("latitude", selectedPoint.getLatitude());
                result.putExtra("longitude", selectedPoint.getLongitude());
                setResult(RESULT_OK, result);
                finish();
            } else {
                Toast.makeText(this, "Сначала выберите точку на карте", Toast.LENGTH_SHORT).show();
            }
        });
        // Кнопка назад (отмена)
        ImageButton buttonBack = findViewById(R.id.imageButtonBackMap);
        buttonBack.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }
}
