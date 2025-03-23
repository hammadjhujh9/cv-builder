package com.example.cv_builder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PersonalDetailsDatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "CVBuilderDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Name
    private static final String TABLE_PERSONAL_DETAILS = "personal_details";

    // Personal Details Table Columns
    private static final String KEY_PERSONAL_ID = "id";
    private static final String KEY_PERSONAL_FULL_NAME = "full_name";
    private static final String KEY_PERSONAL_EMAIL = "email";
    private static final String KEY_PERSONAL_PHONE = "phone";
    private static final String KEY_PERSONAL_ADDRESS = "address";
    private static final String KEY_PERSONAL_LINKEDIN = "linkedin";
    // Singleton instance
    private static PersonalDetailsDatabaseHelper sInstance;

    public static synchronized PersonalDetailsDatabaseHelper getInstance(Context context) {
        // Use the application context to avoid leaking activities
        if (sInstance == null) {
            sInstance = new PersonalDetailsDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private PersonalDetailsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PERSONAL_DETAILS_TABLE = "CREATE TABLE " + TABLE_PERSONAL_DETAILS +
                "(" +
                KEY_PERSONAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_PERSONAL_FULL_NAME + " TEXT NOT NULL," +
                KEY_PERSONAL_EMAIL + " TEXT," +
                KEY_PERSONAL_PHONE + " TEXT," +
                KEY_PERSONAL_ADDRESS + " TEXT," +
                KEY_PERSONAL_LINKEDIN + " TEXT" +
                ")";

        db.execSQL(CREATE_PERSONAL_DETAILS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSONAL_DETAILS);
            onCreate(db);
        }
    }

    // Ensure table exists before any database operations
    public void ensureTableExists() {
        SQLiteDatabase db = getWritableDatabase();

        // Check if personal details table exists
        Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                new String[]{TABLE_PERSONAL_DETAILS});

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

    // Add or update personal details
    public long savePersonalDetails(PersonalDetails personalDetails) {
        SQLiteDatabase db = getWritableDatabase();

        // There should only be one row in the personal details table
        // Let's check if we already have a record
        PersonalDetails existingDetails = getPersonalDetails();
        if (existingDetails != null) {
            // Update existing record
            personalDetails.setId(existingDetails.getId());
            updatePersonalDetails(personalDetails);
            return existingDetails.getId();
        } else {
            // Insert new record
            return addPersonalDetails(personalDetails);
        }
    }

    // Add personal details
    private long addPersonalDetails(PersonalDetails personalDetails) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        long id = -1;

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_PERSONAL_FULL_NAME, personalDetails.getFullName());
            values.put(KEY_PERSONAL_EMAIL, personalDetails.getEmail());
            values.put(KEY_PERSONAL_PHONE, personalDetails.getPhone());
            values.put(KEY_PERSONAL_ADDRESS, personalDetails.getAddress());
            values.put(KEY_PERSONAL_LINKEDIN, personalDetails.getLinkedin());

            id = db.insertOrThrow(TABLE_PERSONAL_DETAILS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return id;
    }

    // Get personal details
    public PersonalDetails getPersonalDetails() {
        // Ensure table exists
        ensureTableExists();

        String PERSONAL_DETAILS_SELECT_QUERY = "SELECT * FROM " + TABLE_PERSONAL_DETAILS + " LIMIT 1";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(PERSONAL_DETAILS_SELECT_QUERY, null);
        PersonalDetails personalDetails = null;

        try {
            if (cursor.moveToFirst()) {
                personalDetails = new PersonalDetails();
                personalDetails.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_PERSONAL_ID)));
                personalDetails.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PERSONAL_FULL_NAME)));
                personalDetails.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PERSONAL_EMAIL)));
                personalDetails.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PERSONAL_PHONE)));
                personalDetails.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PERSONAL_ADDRESS)));
                personalDetails.setLinkedin(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PERSONAL_LINKEDIN)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return personalDetails;
    }

    // Update personal details
    private int updatePersonalDetails(PersonalDetails personalDetails) {
        SQLiteDatabase db = getWritableDatabase();

        int rowsAffected = 0;

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_PERSONAL_FULL_NAME, personalDetails.getFullName());
            values.put(KEY_PERSONAL_EMAIL, personalDetails.getEmail());
            values.put(KEY_PERSONAL_PHONE, personalDetails.getPhone());
            values.put(KEY_PERSONAL_ADDRESS, personalDetails.getAddress());
            values.put(KEY_PERSONAL_LINKEDIN, personalDetails.getLinkedin());

            rowsAffected = db.update(TABLE_PERSONAL_DETAILS, values,
                    KEY_PERSONAL_ID + " = ?",
                    new String[] { String.valueOf(personalDetails.getId()) });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowsAffected;
    }

    // Delete personal details
    public void deletePersonalDetails() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            db.delete(TABLE_PERSONAL_DETAILS, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
}