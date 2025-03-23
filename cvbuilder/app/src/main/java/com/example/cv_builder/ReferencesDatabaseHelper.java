package com.example.cv_builder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ReferencesDatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "CVBuilderDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Name
    private static final String TABLE_REFERENCES = "reference_entries";



    // References Table Columns
    private static final String KEY_REFERENCE_ID = "id";
    private static final String KEY_REFERENCE_NAME = "name";
    private static final String KEY_REFERENCE_EMAIL = "email";
    private static final String KEY_REFERENCE_PHONE = "phone";
    private static final String KEY_REFERENCE_ORGANIZATION = "organization";
    private static final String KEY_REFERENCE_POSITION = "position";

    // Singleton instance
    private static ReferencesDatabaseHelper sInstance;

    public static synchronized ReferencesDatabaseHelper getInstance(Context context) {
        // Use the application context to avoid leaking activities
        if (sInstance == null) {
            sInstance = new ReferencesDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private ReferencesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_REFERENCES_TABLE = "CREATE TABLE " + TABLE_REFERENCES +
                "(" +
                KEY_REFERENCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_REFERENCE_NAME + " TEXT NOT NULL," +
                KEY_REFERENCE_EMAIL + " TEXT," +
                KEY_REFERENCE_PHONE + " TEXT," +
                KEY_REFERENCE_ORGANIZATION + " TEXT," +
                KEY_REFERENCE_POSITION + " TEXT" +
                ")";

        db.execSQL(CREATE_REFERENCES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_REFERENCES);
            onCreate(db);
        }
    }

    // Ensure table exists before any database operations
    public void ensureTableExists() {
        SQLiteDatabase db = getWritableDatabase();

        // Check if references table exists
        Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                new String[]{TABLE_REFERENCES});

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

    // Add a new reference
    public long addReference(Reference reference) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        long id = -1;

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_REFERENCE_NAME, reference.getName());
            values.put(KEY_REFERENCE_EMAIL, reference.getEmail());
            values.put(KEY_REFERENCE_PHONE, reference.getPhone());
            values.put(KEY_REFERENCE_ORGANIZATION, reference.getOrganization());
            values.put(KEY_REFERENCE_POSITION, reference.getPosition());

            id = db.insertOrThrow(TABLE_REFERENCES, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return id;
    }

    // Get all reference entries
    public List<Reference> getAllReferences() {
        List<Reference> referenceList = new ArrayList<>();

        // Ensure table exists
        ensureTableExists();

        String REFERENCES_SELECT_QUERY = "SELECT * FROM " + TABLE_REFERENCES;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(REFERENCES_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Reference reference = new Reference();
                    reference.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_REFERENCE_ID)));
                    reference.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_REFERENCE_NAME)));
                    reference.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_REFERENCE_EMAIL)));
                    reference.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(KEY_REFERENCE_PHONE)));
                    reference.setOrganization(cursor.getString(cursor.getColumnIndexOrThrow(KEY_REFERENCE_ORGANIZATION)));
                    reference.setPosition(cursor.getString(cursor.getColumnIndexOrThrow(KEY_REFERENCE_POSITION)));

                    referenceList.add(reference);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return referenceList;
    }

    // Update a reference entry
    public int updateReference(Reference reference) {
        SQLiteDatabase db = getWritableDatabase();

        int rowsAffected = 0;

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_REFERENCE_NAME, reference.getName());
            values.put(KEY_REFERENCE_EMAIL, reference.getEmail());
            values.put(KEY_REFERENCE_PHONE, reference.getPhone());
            values.put(KEY_REFERENCE_ORGANIZATION, reference.getOrganization());
            values.put(KEY_REFERENCE_POSITION, reference.getPosition());

            rowsAffected = db.update(TABLE_REFERENCES, values,
                    KEY_REFERENCE_ID + " = ?",
                    new String[] { String.valueOf(reference.getId()) });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowsAffected;
    }

    // Delete a reference entry
    public void deleteReference(long id) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.delete(TABLE_REFERENCES,
                    KEY_REFERENCE_ID + " = ?",
                    new String[] { String.valueOf(id) });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Delete all reference entries
    public void deleteAllReferences() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            db.delete(TABLE_REFERENCES, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
}