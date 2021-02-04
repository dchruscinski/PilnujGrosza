package com.example.pilnujgrosza;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class ProfileDatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_PROFILE = "profile";
    public static final String COLUMN_PROFILE_ID = "profID";
    public static final String COLUMN_PROFILE_NAME = "profName";
    public static final String COLUMN_PROFILE_EMAIL = "profEmail";
    public static final String COLUMN_PROFILE_PIN = "profPIN";
    public static final String COLUMN_PROFILE_PIN_SALT = "profPINSalt";
    public static final String COLUMN_PROFILE_INITIAL_BALANCE = "profInitialBalance";
    public static final String COLUMN_PROFILE_BALANCE = "profBalance";
    public static final String COLUMN_PROFILE_CREATION_DATE = "profCreationDate";
    public static final String COLUMN_PROFILE_LAST_LOGIN_DATE = "profLastLoginDate";
    public static final String COLUMN_PROFILE_LAST_LOGIN_ATTEMPT = "profLastLoginAttempt";

    public ProfileDatabaseHelper(@Nullable Context context) {
        super(context, "pilnujgrosza.db", null, 1);
    }

    // generating new database
    @Override
    public void onCreate(SQLiteDatabase db) {

        // for testing reasons
        String upgradeProfileTableStatement = "DROP TABLE IF EXISTS " + TABLE_PROFILE;
        db.execSQL(upgradeProfileTableStatement);

        String createProfileTableStatement =
                "CREATE TABLE " + TABLE_PROFILE + " (" +
                COLUMN_PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PROFILE_NAME + " TEXT UNIQUE," +
                COLUMN_PROFILE_EMAIL + " TEXT UNIQUE," +
                COLUMN_PROFILE_PIN + " TEXT NOT NULL," +
                COLUMN_PROFILE_PIN_SALT + " TEXT NOT NULL," +
                COLUMN_PROFILE_INITIAL_BALANCE + " INTEGER DEFAULT 0," +
                COLUMN_PROFILE_BALANCE + " INTEGER," +
                COLUMN_PROFILE_CREATION_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                COLUMN_PROFILE_LAST_LOGIN_DATE + " DATETIME," +
                COLUMN_PROFILE_LAST_LOGIN_ATTEMPT + " DATETIME)";
        db.execSQL(createProfileTableStatement);
    }

    // making changes to existing table
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String upgradeProfileTableStatement = "DROP TABLE IF EXISTS " + TABLE_PROFILE;
        db.execSQL(upgradeProfileTableStatement);
        onCreate(db);
    }

    public void addProfile(ProfileModel profileModel) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PROFILE_NAME, profileModel.getProfName());
        cv.put(COLUMN_PROFILE_EMAIL, profileModel.getProfEmail());
        cv.put(COLUMN_PROFILE_PIN, profileModel.getProfPIN());
        cv.put(COLUMN_PROFILE_PIN_SALT, profileModel.getProfPINSalt());
        cv.put(COLUMN_PROFILE_INITIAL_BALANCE, profileModel.getProfInitialBalance());
        cv.put(COLUMN_PROFILE_BALANCE, profileModel.getProfBalance());
        cv.putNull(COLUMN_PROFILE_CREATION_DATE);
        cv.putNull(COLUMN_PROFILE_LAST_LOGIN_DATE);
        cv.putNull(COLUMN_PROFILE_LAST_LOGIN_ATTEMPT);

        long insert = db.insert(TABLE_PROFILE, null, cv);
        db.close();
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public List<ProfileModel> getProfileList() {
        List<ProfileModel> profilesList = new ArrayList<ProfileModel>();
        String getProfileListStatement = "SELECT * FROM " + TABLE_PROFILE + " ORDER BY " + COLUMN_PROFILE_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getProfileListStatement, null);
        if (cursor.moveToFirst()) {
            do {
                int profID = cursor.getInt(cursor.getColumnIndex("profID"));
                String profName = cursor.getString(cursor.getColumnIndex("profName"));
                String profEmail = cursor.getString(cursor.getColumnIndex("profEmail"));
                String profPIN = cursor.getString(cursor.getColumnIndex("profPIN"));
                String profPINSalt = cursor.getString(cursor.getColumnIndex("profPINSalt"));
                String profCreationDate = cursor.getString(cursor.getColumnIndex("profCreationDate"));
                String profLastLoginDate = cursor.getString(cursor.getColumnIndex("profLastLoginDate"));
                String profLastLoginAttempt = cursor.getString(cursor.getColumnIndex("profLastLoginAttempt"));
                int profInitialBalance = cursor.getInt(cursor.getColumnIndex("profInitialBalance"));
                int profBalance = cursor.getInt(cursor.getColumnIndex("profBalance"));

                ProfileModel profileModel = new ProfileModel(profID, profName, profEmail, profPIN, profPINSalt,
                        profCreationDate, profLastLoginDate, profLastLoginAttempt, profInitialBalance, profBalance);

                profilesList.add(profileModel);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return profilesList;
    }

    public void deleteProfile(int profID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROFILE, COLUMN_PROFILE_ID + " = " + profID, null);
        db.close();
    }

    public void updateProfileName(String name, int profID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv =  new ContentValues();
        cv.put(COLUMN_PROFILE_NAME, name);
        db.update(TABLE_PROFILE, cv, COLUMN_PROFILE_ID + " = " + profID, null);
        db.close();
    }

    public void updateProfileEmail(String email, int profID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv =  new ContentValues();
        cv.put(COLUMN_PROFILE_EMAIL, email);
        db.update(TABLE_PROFILE, cv, COLUMN_PROFILE_ID + " = " + profID, null);
        db.close();
    }

    public void updateProfilePIN(String PIN, String hashSalt, int profID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv =  new ContentValues();
        cv.put(COLUMN_PROFILE_PIN, PIN);
        cv.put(COLUMN_PROFILE_PIN_SALT, hashSalt);
        db.update(TABLE_PROFILE, cv, COLUMN_PROFILE_ID + " = " + profID, null);
        db.close();
    }

    public void updateLastLoginDate(int profID) {
        System.out.println("profID: " + profID);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv =  new ContentValues();
        cv.put(COLUMN_PROFILE_LAST_LOGIN_DATE, getDateTime());
        db.update(TABLE_PROFILE, cv, COLUMN_PROFILE_ID + " = " + profID, null);
        db.close();
    }

    public boolean checkExistingName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String checkDatabaseforNameStatement = "SELECT * FROM " + TABLE_PROFILE + " WHERE " + COLUMN_PROFILE_NAME + " = ?";

        Cursor cursor = db.rawQuery(checkDatabaseforNameStatement, new String[] {name});
        cursor.moveToFirst();

        int nameOccurrencesCounter = cursor.getCount();
        boolean doesNameExistInDatabase = (nameOccurrencesCounter > 0) ? TRUE : FALSE;

        cursor.close();
        db.close();
        return doesNameExistInDatabase;
    }

    public boolean checkExistingEmail(String mail) {
        SQLiteDatabase db = this.getReadableDatabase();
        String checkDatabaseforNameStatement = "SELECT * FROM " + TABLE_PROFILE + " WHERE " + COLUMN_PROFILE_EMAIL + " = ?";

        Cursor cursor = db.rawQuery(checkDatabaseforNameStatement, new String[] {mail});
        cursor.moveToFirst();

        int emailOccurrencesCounter = cursor.getCount();
        boolean doesEmailExistInDatabase = (emailOccurrencesCounter > 0) ? TRUE : FALSE;

        cursor.close();
        db.close();
        return doesEmailExistInDatabase;
    }

}
