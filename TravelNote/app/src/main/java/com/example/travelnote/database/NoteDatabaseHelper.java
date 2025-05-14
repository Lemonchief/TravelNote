// com/example/travelnote/database/NoteDatabaseHelper.java
package com.example.travelnote.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "notes.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "notes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_DATE + " TEXT," +
                    COLUMN_LATITUDE + " REAL," +
                    COLUMN_LONGITUDE + " REAL" +
                    ")";

    public NoteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // пока просто удаляем старую таблицу
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
