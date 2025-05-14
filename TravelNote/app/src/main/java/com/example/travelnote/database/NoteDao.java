// com/example/travelnote/database/NoteDao.java
package com.example.travelnote.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.travelnote.model.Note;

import java.util.ArrayList;

public class NoteDao {


    private final NoteDatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public NoteDao(Context context) {
        dbHelper = new NoteDatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insertNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(NoteDatabaseHelper.COLUMN_TITLE, note.getTitle());
        values.put(NoteDatabaseHelper.COLUMN_DESCRIPTION, note.getDescription());
        values.put(NoteDatabaseHelper.COLUMN_DATE, note.getDate());
        values.put(NoteDatabaseHelper.COLUMN_LATITUDE, note.getLatitude());
        values.put(NoteDatabaseHelper.COLUMN_LONGITUDE, note.getLongitude());

        long result = database.insert(NoteDatabaseHelper.TABLE_NAME, null, values);
        if (result == -1) {
            Log.e("DB_INSERT", "Ошибка при вставке заметки");
        } else {
            Log.d("DB_INSERT", "Заметка успешно добавлена с id=" + result);
        }
    }

    public void deleteNote(int id) {
        database.delete(NoteDatabaseHelper.TABLE_NAME, NoteDatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void updateNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(NoteDatabaseHelper.COLUMN_TITLE, note.getTitle());
        values.put(NoteDatabaseHelper.COLUMN_DESCRIPTION, note.getDescription());
        values.put(NoteDatabaseHelper.COLUMN_DATE, note.getDate());
        values.put(NoteDatabaseHelper.COLUMN_LATITUDE, note.getLatitude());
        values.put(NoteDatabaseHelper.COLUMN_LONGITUDE, note.getLongitude());
        database.update(NoteDatabaseHelper.TABLE_NAME, values, NoteDatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(note.getId())});
    }

    public ArrayList<Note> getAllNotes() {
        ArrayList<Note> notes = new ArrayList<>();

        Cursor cursor = database.query(
                NoteDatabaseHelper.TABLE_NAME,
                null, null, null, null, null,
                NoteDatabaseHelper.COLUMN_ID + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(NoteDatabaseHelper.COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(NoteDatabaseHelper.COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(NoteDatabaseHelper.COLUMN_DESCRIPTION));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(NoteDatabaseHelper.COLUMN_DATE));
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(NoteDatabaseHelper.COLUMN_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(NoteDatabaseHelper.COLUMN_LONGITUDE));

                notes.add(new Note(id, title, description, date, latitude, longitude));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return notes;
    }
}
