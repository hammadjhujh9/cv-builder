package com.example.cv_builder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ExperienceDatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "CVBuilderDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Name
    private static final String TABLE_EXPERIENCE = "experience";

    // Experience Table Columns
    private static final String KEY_EXPERIENCE_ID = "id";
    private static final String KEY_EXPERIENCE_COMPANY = "company";
    private static final String KEY_EXPERIENCE_POSITION = "position";
    private static final String KEY_EXPERIENCE_START_DATE = "start_date";
    private static final String KEY_EXPERIENCE_END_DATE = "end_date";
    private static final String KEY_EXPERIENCE_DESCRIPTION = "description";

    // Singleton instance
    private static ExperienceDatabaseHelper sInstance;

    public static synchronized ExperienceDatabaseHelper getInstance(Context context) {
        // Use the application context to avoid leaking activities
        if (sInstance == null) {
            sInstance = new ExperienceDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private ExperienceDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EXPERIENCE_TABLE = "CREATE TABLE " + TABLE_EXPERIENCE +
                "(" +
                KEY_EXPERIENCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_EXPERIENCE_COMPANY + " TEXT NOT NULL," +
                KEY_EXPERIENCE_POSITION + " TEXT NOT NULL," +
                KEY_EXPERIENCE_START_DATE + " TEXT," +
                KEY_EXPERIENCE_END_DATE + " TEXT," +
                KEY_EXPERIENCE_DESCRIPTION + " TEXT" +
                ")";

        db.execSQL(CREATE_EXPERIENCE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPERIENCE);
            onCreate(db);
        }
    }

    // Ensure table exists before any database operations
    public void ensureTableExists() {
        SQLiteDatabase db = getWritableDatabase();

        // Check if experience table exists
        Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                new String[]{TABLE_EXPERIENCE});

        boolean tableExists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }

        // If table doesn't exist, create it
        if (!tableExists) {
            onCreate(db);
        }
    }

    // CRUD OPERATIONS (Create, Read, Update, Delete)

    // Add a new experience
    public long addExperience(Experience experience) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        long id = -1;

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_EXPERIENCE_COMPANY, experience.getCompany());
            values.put(KEY_EXPERIENCE_POSITION, experience.getPosition());
            values.put(KEY_EXPERIENCE_START_DATE, experience.getStartDate());
            values.put(KEY_EXPERIENCE_END_DATE, experience.getEndDate());
            values.put(KEY_EXPERIENCE_DESCRIPTION, experience.getDescription());

            id = db.insertOrThrow(TABLE_EXPERIENCE, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return id;
    }

    // Get all experience entries
    public List<Experience> getAllExperience() {
        List<Experience> experienceList = new ArrayList<>();

        // Ensure table exists
        ensureTableExists();

        String EXPERIENCE_SELECT_QUERY = "SELECT * FROM " + TABLE_EXPERIENCE;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(EXPERIENCE_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Experience experience = new Experience();
                    experience.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_EXPERIENCE_ID)));
                    experience.setCompany(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EXPERIENCE_COMPANY)));
                    experience.setPosition(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EXPERIENCE_POSITION)));
                    experience.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EXPERIENCE_START_DATE)));
                    experience.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EXPERIENCE_END_DATE)));
                    experience.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EXPERIENCE_DESCRIPTION)));

                    experienceList.add(experience);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return experienceList;
    }

    // Update an experience entry
    public int updateExperience(Experience experience) {
        SQLiteDatabase db = getWritableDatabase();

        int rowsAffected = 0;

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_EXPERIENCE_COMPANY, experience.getCompany());
            values.put(KEY_EXPERIENCE_POSITION, experience.getPosition());
            values.put(KEY_EXPERIENCE_START_DATE, experience.getStartDate());
            values.put(KEY_EXPERIENCE_END_DATE, experience.getEndDate());
            values.put(KEY_EXPERIENCE_DESCRIPTION, experience.getDescription());

            rowsAffected = db.update(TABLE_EXPERIENCE, values,
                    KEY_EXPERIENCE_ID + " = ?",
                    new String[] { String.valueOf(experience.getId()) });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowsAffected;
    }

    // Delete an experience entry
    public void deleteExperience(long id) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.delete(TABLE_EXPERIENCE,
                    KEY_EXPERIENCE_ID + " = ?",
                    new String[] { String.valueOf(id) });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Delete all experience entries
    public void deleteAllExperience() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            db.delete(TABLE_EXPERIENCE, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
}