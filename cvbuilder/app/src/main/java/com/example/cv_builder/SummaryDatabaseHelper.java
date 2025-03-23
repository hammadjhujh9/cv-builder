package com.example.cv_builder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SummaryDatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "CVBuilderDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Name
    private static final String TABLE_PROFESSIONAL_SUMMARY = "professional_summaries";

    // Professional Summary Table Columns
    private static final String KEY_SUMMARY_ID = "id";
    private static final String KEY_SUMMARY_TITLE = "title";
    private static final String KEY_SUMMARY_CONTENT = "content";
    private static final String KEY_SUMMARY_IS_DEFAULT = "is_default";

    // Singleton instance
    private static SummaryDatabaseHelper sInstance;

    public static synchronized SummaryDatabaseHelper getInstance(Context context) {
        // Use the application context to avoid leaking activities
        if (sInstance == null) {
            sInstance = new SummaryDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private SummaryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PROFESSIONAL_SUMMARY_TABLE = "CREATE TABLE " + TABLE_PROFESSIONAL_SUMMARY +
                "(" +
                KEY_SUMMARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_SUMMARY_TITLE + " TEXT NOT NULL," +
                KEY_SUMMARY_CONTENT + " TEXT NOT NULL," +
                KEY_SUMMARY_IS_DEFAULT + " INTEGER DEFAULT 0" +
                ")";

        db.execSQL(CREATE_PROFESSIONAL_SUMMARY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFESSIONAL_SUMMARY);
            onCreate(db);
        }
    }

    // Ensure table exists before any database operations
    public void ensureTableExists() {
        SQLiteDatabase db = getWritableDatabase();

        // Check if professional summary table exists
        Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                new String[]{TABLE_PROFESSIONAL_SUMMARY});

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

    // Add or update professional summary
    public long saveProfessionalSummary(Summary summary) {
        SQLiteDatabase db = getWritableDatabase();
        long id;

        db.beginTransaction();
        try {
            // If this summary is set as default, unset any existing default
            if (summary.isDefault()) {
                ContentValues values = new ContentValues();
                values.put(KEY_SUMMARY_IS_DEFAULT, 0);
                db.update(TABLE_PROFESSIONAL_SUMMARY, values,
                        KEY_SUMMARY_IS_DEFAULT + "=?", new String[]{"1"});
            }

            // Check if we're updating existing or creating new
            if (summary.getId() > 0) {
                // Update existing record
                id = updateProfessionalSummary(summary);
            } else {
                // Insert new record
                id = addProfessionalSummary(summary);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return id;
    }

    // Add professional summary
    private long addProfessionalSummary(Summary summary) {
        SQLiteDatabase db = getWritableDatabase();
        long id = -1;

        ContentValues values = new ContentValues();
        values.put(KEY_SUMMARY_TITLE, summary.getTitle());
        values.put(KEY_SUMMARY_CONTENT, summary.getContent());
        values.put(KEY_SUMMARY_IS_DEFAULT, summary.isDefault() ? 1 : 0);

        id = db.insertOrThrow(TABLE_PROFESSIONAL_SUMMARY, null, values);
        return id;
    }

    // Get professional summary by ID
    public Summary getProfessionalSummaryById(long id) {
        // Ensure table exists
        ensureTableExists();

        String PROFESSIONAL_SUMMARY_SELECT_QUERY =
                "SELECT * FROM " + TABLE_PROFESSIONAL_SUMMARY +
                        " WHERE " + KEY_SUMMARY_ID + " = ?";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(PROFESSIONAL_SUMMARY_SELECT_QUERY, new String[]{String.valueOf(id)});
        Summary summary = null;

        try {
            if (cursor.moveToFirst()) {
                summary = cursorToSummary(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return summary;
    }

    // Get default professional summary
    public Summary getDefaultProfessionalSummary() {
        // Ensure table exists
        ensureTableExists();

        String DEFAULT_SUMMARY_SELECT_QUERY =
                "SELECT * FROM " + TABLE_PROFESSIONAL_SUMMARY +
                        " WHERE " + KEY_SUMMARY_IS_DEFAULT + " = 1 LIMIT 1";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(DEFAULT_SUMMARY_SELECT_QUERY, null);
        Summary summary = null;

        try {
            if (cursor.moveToFirst()) {
                summary = cursorToSummary(cursor);
            } else {
                // If no default is set, get the most recent one
                cursor.close();
                String RECENT_SUMMARY_SELECT_QUERY =
                        "SELECT * FROM " + TABLE_PROFESSIONAL_SUMMARY +
                                " ORDER BY " + KEY_SUMMARY_ID + " DESC LIMIT 1";
                cursor = db.rawQuery(RECENT_SUMMARY_SELECT_QUERY, null);

                if (cursor.moveToFirst()) {
                    summary = cursorToSummary(cursor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return summary;
    }

    // Get all professional summaries
    public List<Summary> getAllProfessionalSummaries() {
        // Ensure table exists
        ensureTableExists();

        String SUMMARIES_SELECT_QUERY =
                "SELECT * FROM " + TABLE_PROFESSIONAL_SUMMARY +
                        " ORDER BY " + KEY_SUMMARY_IS_DEFAULT + " DESC, " +
                        KEY_SUMMARY_ID + " DESC";

        List<Summary> summaries = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(SUMMARIES_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Summary summary = cursorToSummary(cursor);
                    summaries.add(summary);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return summaries;
    }

    // Helper method to convert cursor to Summary object
    private Summary cursorToSummary(Cursor cursor) {
        return new Summary(
                cursor.getLong(cursor.getColumnIndexOrThrow(KEY_SUMMARY_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(KEY_SUMMARY_TITLE)),
                cursor.getString(cursor.getColumnIndexOrThrow(KEY_SUMMARY_CONTENT)),
                cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SUMMARY_IS_DEFAULT)) == 1
        );
    }

    // Update professional summary
    private int updateProfessionalSummary(Summary summary) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SUMMARY_TITLE, summary.getTitle());
        values.put(KEY_SUMMARY_CONTENT, summary.getContent());
        values.put(KEY_SUMMARY_IS_DEFAULT, summary.isDefault() ? 1 : 0);

        return db.update(TABLE_PROFESSIONAL_SUMMARY, values,
                KEY_SUMMARY_ID + " = ?",
                new String[] { String.valueOf(summary.getId()) });
    }

    // Delete professional summary
    public void deleteProfessionalSummary(long summaryId) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            // Check if the summary being deleted is the default one
            Summary summary = getProfessionalSummaryById(summaryId);
            boolean wasDefault = summary != null && summary.isDefault();

            // Delete the summary
            db.delete(TABLE_PROFESSIONAL_SUMMARY,
                    KEY_SUMMARY_ID + " = ?",
                    new String[] { String.valueOf(summaryId) });

            // If the deleted summary was the default, set a new default if possible
            if (wasDefault) {
                String SELECT_MOST_RECENT =
                        "SELECT * FROM " + TABLE_PROFESSIONAL_SUMMARY +
                                " ORDER BY " + KEY_SUMMARY_ID + " DESC LIMIT 1";
                Cursor cursor = db.rawQuery(SELECT_MOST_RECENT, null);

                if (cursor.moveToFirst()) {
                    long newDefaultId = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_SUMMARY_ID));
                    ContentValues values = new ContentValues();
                    values.put(KEY_SUMMARY_IS_DEFAULT, 1);
                    db.update(TABLE_PROFESSIONAL_SUMMARY, values,
                            KEY_SUMMARY_ID + " = ?",
                            new String[] { String.valueOf(newDefaultId) });
                }
                cursor.close();
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    // Set a specific summary as the default
    public void setDefaultSummary(long summaryId) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            // First, unset all defaults
            ContentValues unsetValues = new ContentValues();
            unsetValues.put(KEY_SUMMARY_IS_DEFAULT, 0);
            db.update(TABLE_PROFESSIONAL_SUMMARY, unsetValues, null, null);

            // Then set the new default
            ContentValues setValues = new ContentValues();
            setValues.put(KEY_SUMMARY_IS_DEFAULT, 1);
            db.update(TABLE_PROFESSIONAL_SUMMARY, setValues,
                    KEY_SUMMARY_ID + " = ?",
                    new String[] { String.valueOf(summaryId) });

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}