package com.example.cv_builder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class EducationDatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "CVBuilderDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Name
    private static final String TABLE_EDUCATION = "education";

    // Education Table Columns
    private static final String KEY_EDUCATION_ID = "id";
    private static final String KEY_EDUCATION_DEGREE_LEVEL = "degree_level";
    private static final String KEY_EDUCATION_INSTITUTION = "institution";
    private static final String KEY_EDUCATION_START_DATE = "start_date";
    private static final String KEY_EDUCATION_END_DATE = "end_date";

    // Singleton instance
    private static EducationDatabaseHelper sInstance;

    public static synchronized EducationDatabaseHelper getInstance(Context context) {
        // Use the application context to avoid leaking activities
        if (sInstance == null) {
            sInstance = new EducationDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private EducationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EDUCATION_TABLE = "CREATE TABLE " + TABLE_EDUCATION +
                "(" +
                KEY_EDUCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_EDUCATION_DEGREE_LEVEL + " TEXT NOT NULL," +
                KEY_EDUCATION_INSTITUTION + " TEXT NOT NULL," +
                KEY_EDUCATION_START_DATE + " TEXT," +
                KEY_EDUCATION_END_DATE + " TEXT" +
                ")";

        db.execSQL(CREATE_EDUCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EDUCATION);
            onCreate(db);
        }
    }

    // CRUD OPERATIONS (Create, Read, Update, Delete)

    // Add a new education
    public long addEducation(Education education) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        long id = -1;

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_EDUCATION_DEGREE_LEVEL, education.getDegreeLevel());
            values.put(KEY_EDUCATION_INSTITUTION, education.getInstitution());
            values.put(KEY_EDUCATION_START_DATE, education.getStartDate());
            values.put(KEY_EDUCATION_END_DATE, education.getEndDate());

            id = db.insertOrThrow(TABLE_EDUCATION, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return id;
    }

    // Get all education entries
    public List<Education> getAllEducation() {
        List<Education> educationList = new ArrayList<>();

        String EDUCATION_SELECT_QUERY = "SELECT * FROM " + TABLE_EDUCATION;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(EDUCATION_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Education education = new Education();
                    education.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_EDUCATION_ID)));
                    education.setDegreeLevel(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EDUCATION_DEGREE_LEVEL)));
                    education.setInstitution(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EDUCATION_INSTITUTION)));
                    education.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EDUCATION_START_DATE)));
                    education.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_EDUCATION_END_DATE)));

                    educationList.add(education);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return educationList;
    }

    // Update an education entry
    public int updateEducation(Education education) {
        SQLiteDatabase db = getWritableDatabase();

        int rowsAffected = 0;

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_EDUCATION_DEGREE_LEVEL, education.getDegreeLevel());
            values.put(KEY_EDUCATION_INSTITUTION, education.getInstitution());
            values.put(KEY_EDUCATION_START_DATE, education.getStartDate());
            values.put(KEY_EDUCATION_END_DATE, education.getEndDate());

            rowsAffected = db.update(TABLE_EDUCATION, values,
                    KEY_EDUCATION_ID + " = ?",
                    new String[] { String.valueOf(education.getId()) });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowsAffected;
    }

    // Delete an education entry
    public void deleteEducation(long id) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.delete(TABLE_EDUCATION,
                    KEY_EDUCATION_ID + " = ?",
                    new String[] { String.valueOf(id) });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Delete all education entries
    public void deleteAllEducation() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            db.delete(TABLE_EDUCATION, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
    // Add this method to EducationDatabaseHelper
    public void ensureTableExists() {
        SQLiteDatabase db = getWritableDatabase();

        // Check if education table exists
        Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                new String[]{TABLE_EDUCATION});

        boolean tableExists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }

        // If table doesn't exist, create it
        if (!tableExists) {
            String CREATE_EDUCATION_TABLE = "CREATE TABLE " + TABLE_EDUCATION +
                    "(" +
                    KEY_EDUCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_EDUCATION_DEGREE_LEVEL + " TEXT NOT NULL," +
                    KEY_EDUCATION_INSTITUTION + " TEXT NOT NULL," +
                    KEY_EDUCATION_START_DATE + " TEXT," +
                    KEY_EDUCATION_END_DATE + " TEXT" +
                    ")";

            db.execSQL(CREATE_EDUCATION_TABLE);
        }
    }
}