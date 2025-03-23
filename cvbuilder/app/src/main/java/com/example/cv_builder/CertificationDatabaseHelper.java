package com.example.cv_builder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class CertificationDatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "CVBuilderDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Name
    private static final String TABLE_CERTIFICATIONS = "certifications";

    // Certification Table Columns
    private static final String KEY_CERTIFICATION_ID = "id";
    private static final String KEY_CERTIFICATION_TITLE = "title";
    private static final String KEY_CERTIFICATION_START_DATE = "start_date";
    private static final String KEY_CERTIFICATION_END_DATE = "end_date";

    // Singleton instance
    private static CertificationDatabaseHelper sInstance;

    public static synchronized CertificationDatabaseHelper getInstance(Context context) {
        // Use the application context to avoid leaking activities
        if (sInstance == null) {
            sInstance = new CertificationDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private CertificationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CERTIFICATIONS_TABLE = "CREATE TABLE " + TABLE_CERTIFICATIONS +
                "(" +
                KEY_CERTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_CERTIFICATION_TITLE + " TEXT NOT NULL," +
                KEY_CERTIFICATION_START_DATE + " TEXT," +
                KEY_CERTIFICATION_END_DATE + " TEXT" +
                ")";

        db.execSQL(CREATE_CERTIFICATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CERTIFICATIONS);
            onCreate(db);
        }
    }

    // Add this method to check and create the table if it doesn't exist
    public void ensureTableExists() {
        SQLiteDatabase db = getWritableDatabase();

        // Check if certifications table exists
        Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                new String[]{TABLE_CERTIFICATIONS});

        boolean tableExists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }

        // If table doesn't exist, create it
        if (!tableExists) {
            String CREATE_CERTIFICATIONS_TABLE = "CREATE TABLE " + TABLE_CERTIFICATIONS +
                    "(" +
                    KEY_CERTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_CERTIFICATION_TITLE + " TEXT NOT NULL," +
                    KEY_CERTIFICATION_START_DATE + " TEXT," +
                    KEY_CERTIFICATION_END_DATE + " TEXT" +
                    ")";

            db.execSQL(CREATE_CERTIFICATIONS_TABLE);
        }
    }

    // CRUD OPERATIONS (Create, Read, Update, Delete)

    // Add a new certification
    public long addCertification(Certification certification) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        long id = -1;

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_CERTIFICATION_TITLE, certification.getTitle());
            values.put(KEY_CERTIFICATION_START_DATE, certification.getStartDate());
            values.put(KEY_CERTIFICATION_END_DATE, certification.getEndDate());

            id = db.insertOrThrow(TABLE_CERTIFICATIONS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return id;
    }

    // Get all certifications
    public List<Certification> getAllCertifications() {
        List<Certification> certifications = new ArrayList<>();

        String CERTIFICATIONS_SELECT_QUERY = "SELECT * FROM " + TABLE_CERTIFICATIONS;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(CERTIFICATIONS_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Certification certification = new Certification();
                    certification.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_CERTIFICATION_ID)));
                    certification.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CERTIFICATION_TITLE)));
                    certification.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CERTIFICATION_START_DATE)));
                    certification.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CERTIFICATION_END_DATE)));

                    certifications.add(certification);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return certifications;
    }

    // Update a certification
    public int updateCertification(Certification certification) {
        SQLiteDatabase db = getWritableDatabase();

        int rowsAffected = 0;

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_CERTIFICATION_TITLE, certification.getTitle());
            values.put(KEY_CERTIFICATION_START_DATE, certification.getStartDate());
            values.put(KEY_CERTIFICATION_END_DATE, certification.getEndDate());

            rowsAffected = db.update(TABLE_CERTIFICATIONS, values,
                    KEY_CERTIFICATION_ID + " = ?",
                    new String[] { String.valueOf(certification.getId()) });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowsAffected;
    }

    // Delete a certification
    public void deleteCertification(long id) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.delete(TABLE_CERTIFICATIONS,
                    KEY_CERTIFICATION_ID + " = ?",
                    new String[] { String.valueOf(id) });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Delete all certifications
    public void deleteAllCertifications() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            db.delete(TABLE_CERTIFICATIONS, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
}