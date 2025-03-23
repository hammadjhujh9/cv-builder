// ProfilePictureDatabaseHelper.java
package com.example.cv_builder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProfilePictureDatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "CVBuilderDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Name
    private static final String TABLE_PROFILE_PICTURE = "profile_picture";

    // Table Columns
    private static final String KEY_ID = "id";
    private static final String KEY_IMAGE_PATH = "image_path";

    // Singleton instance
    private static ProfilePictureDatabaseHelper sInstance;

    public static synchronized ProfilePictureDatabaseHelper getInstance(Context context) {
        // Use the application context to avoid leaking activities
        if (sInstance == null) {
            sInstance = new ProfilePictureDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private ProfilePictureDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PROFILE_PICTURE_TABLE = "CREATE TABLE " + TABLE_PROFILE_PICTURE +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_IMAGE_PATH + " TEXT NOT NULL" +
                ")";

        db.execSQL(CREATE_PROFILE_PICTURE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE_PICTURE);
            onCreate(db);
        }
    }

    // Ensure table exists (similar to your Education helper)
    public void ensureTableExists() {
        SQLiteDatabase db = getWritableDatabase();

        // Check if profile_picture table exists
        Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                new String[]{TABLE_PROFILE_PICTURE});

        boolean tableExists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }

        // If table doesn't exist, create it
        if (!tableExists) {
            onCreate(db);
        }
    }

    // Save profile picture path
    public long saveProfilePicture(String imagePath) {
        // Ensure table exists
        ensureTableExists();

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        long id = -1;

        try {
            // First, delete any existing profile pictures
            db.delete(TABLE_PROFILE_PICTURE, null, null);

            // Insert the new profile picture path
            ContentValues values = new ContentValues();
            values.put(KEY_IMAGE_PATH, imagePath);

            id = db.insertOrThrow(TABLE_PROFILE_PICTURE, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return id;
    }

    // Get the current profile picture
    public ProfilePicture getProfilePicture() {
        // Ensure table exists
        ensureTableExists();

        SQLiteDatabase db = getReadableDatabase();
        ProfilePicture profilePicture = null;

        Cursor cursor = db.query(TABLE_PROFILE_PICTURE,
                new String[]{KEY_ID, KEY_IMAGE_PATH},
                null, null, null, null, null, "1");

        try {
            if (cursor.moveToFirst()) {
                profilePicture = new ProfilePicture();
                profilePicture.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)));
                profilePicture.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE_PATH)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return profilePicture;
    }
}