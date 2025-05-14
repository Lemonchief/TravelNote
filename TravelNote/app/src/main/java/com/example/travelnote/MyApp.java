package com.example.travelnote; // Убедись, что пакет совпадает с твоим

import android.app.Application;

import com.yandex.mapkit.MapKitFactory;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MapKitFactory.setApiKey("f83e4986-61f7-49fa-a44a-1aed7157d065");
        MapKitFactory.initialize(this);
    }
}
