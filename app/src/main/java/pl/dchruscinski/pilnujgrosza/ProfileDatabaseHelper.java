package pl.dchruscinski.pilnujgrosza;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class ProfileDatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_PROFILE = "profile";
    public static final String COLUMN_PROFILE_ID = "profID";
    public static final String COLUMN_PROFILE_NAME = "profName";
    public static final String COLUMN_PROFILE_PIN = "profPIN";
    public static final String COLUMN_PROFILE_PIN_SALT = "profPINSalt";
    public static final String COLUMN_PROFILE_INITIAL_BALANCE = "profInitialBalance";
    public static final String COLUMN_PROFILE_BALANCE = "profBalance";
    public static final String COLUMN_PROFILE_CREATION_DATE = "profCreationDate";
    public static final String COLUMN_PROFILE_LAST_LOGIN_DATE = "profLastLoginDate";
    public static final String COLUMN_PROFILE_LAST_LOGIN_ATTEMPT = "profLastLoginAttempt";
    public static final String COLUMN_PROFILE_FAILED_LOGIN_ATTEMPTS = "profFailedLoginAttempts";
    public static final String COLUMN_PROFILE_HELPER_QUESTION = "profHelperQuestion";
    public static final String COLUMN_PROFILE_HELPER_ANSWER = "profHelperAnswer";
    public static final String COLUMN_PROFILE_HELPER_SALT = "profHelperSalt";
    public static final int MINUTES_OF_ACCOUNT_LOCK = 1;

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
                COLUMN_PROFILE_PIN + " TEXT NOT NULL," +
                COLUMN_PROFILE_PIN_SALT + " TEXT NOT NULL," +
                COLUMN_PROFILE_HELPER_QUESTION + " TEXT," +
                COLUMN_PROFILE_HELPER_ANSWER + " TEXT," +
                COLUMN_PROFILE_HELPER_SALT + " TEXT," +
                COLUMN_PROFILE_INITIAL_BALANCE + " INTEGER DEFAULT 0," +
                COLUMN_PROFILE_BALANCE + " INTEGER," +
                COLUMN_PROFILE_CREATION_DATE + " DATETIME," +
                COLUMN_PROFILE_LAST_LOGIN_DATE + " DATETIME," +
                COLUMN_PROFILE_LAST_LOGIN_ATTEMPT + " DATETIME," +
                COLUMN_PROFILE_FAILED_LOGIN_ATTEMPTS + " INTEGER DEFAULT 0)";
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
        cv.put(COLUMN_PROFILE_PIN, profileModel.getProfPIN());
        cv.put(COLUMN_PROFILE_PIN_SALT, profileModel.getProfPINSalt());
        cv.put(COLUMN_PROFILE_INITIAL_BALANCE, profileModel.getProfInitialBalance());
        cv.put(COLUMN_PROFILE_BALANCE, profileModel.getProfBalance());
        cv.put(COLUMN_PROFILE_CREATION_DATE, getCurrentDateTime());
        cv.putNull(COLUMN_PROFILE_LAST_LOGIN_DATE);
        cv.putNull(COLUMN_PROFILE_LAST_LOGIN_ATTEMPT);
        cv.putNull(COLUMN_PROFILE_FAILED_LOGIN_ATTEMPTS);
        cv.put(COLUMN_PROFILE_HELPER_QUESTION, profileModel.getProfHelperQuestion());
        cv.put(COLUMN_PROFILE_HELPER_ANSWER, profileModel.getProfHelperAnswer());
        cv.put(COLUMN_PROFILE_HELPER_SALT, profileModel.getProfHelperSalt());
        db.insert(TABLE_PROFILE, null, cv);

        db.close();
    }

    public String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        Date date = new Date();

        return dateFormat.format(date);
    }

    public String getDateTimeForAccountLock() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));

        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, MINUTES_OF_ACCOUNT_LOCK);

        return dateFormat.format(c.getTime());
    }

    public boolean compareLoginDateTime(int profID) throws ParseException {
        boolean compareLoginDateTime;
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));

        SQLiteDatabase db = this.getReadableDatabase();
        String getLastLoginAttempt = "SELECT profLastLoginAttempt FROM " + TABLE_PROFILE + " WHERE " + COLUMN_PROFILE_ID + " = ?";

        Cursor cursor = db.rawQuery(getLastLoginAttempt, new String[] {Integer.toString(profID)});
        cursor.moveToFirst();
        String lastLoginAttmept = cursor.getString(cursor.getColumnIndex("profLastLoginAttempt"));

        Date actualLoginDateTime = dateFormat.parse(getCurrentDateTime());
        Date loginDateTimeFromDB;
        if (lastLoginAttmept != null) {
            loginDateTimeFromDB = dateFormat.parse(lastLoginAttmept);
        } else {
            loginDateTimeFromDB = dateFormat.parse(getCurrentDateTime());
        }

        // if actualLoginDateTime > loginDateTimeFromDB (actualLoginDateTime is AFTER loginDateTimeFromDB) then user can logim
        if (actualLoginDateTime.compareTo(loginDateTimeFromDB) >= 0) {
            compareLoginDateTime = true;
        } else compareLoginDateTime = false;

        return compareLoginDateTime;
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
                String profPIN = cursor.getString(cursor.getColumnIndex("profPIN"));
                String profPINSalt = cursor.getString(cursor.getColumnIndex("profPINSalt"));
                String profCreationDate = cursor.getString(cursor.getColumnIndex("profCreationDate"));
                String profLastLoginDate = cursor.getString(cursor.getColumnIndex("profLastLoginDate"));
                String profLastLoginAttempt = cursor.getString(cursor.getColumnIndex("profLastLoginAttempt"));
                int profFailedLoginAttempts = cursor.getInt(cursor.getColumnIndex("profFailedLoginAttempts"));
                String profHelperQuestion = cursor.getString(cursor.getColumnIndex("profHelperQuestion"));
                String profHelperAnswer = cursor.getString(cursor.getColumnIndex("profHelperAnswer"));
                String profHelperSalt = cursor.getString(cursor.getColumnIndex("profHelperSalt"));
                int profInitialBalance = cursor.getInt(cursor.getColumnIndex("profInitialBalance"));
                int profBalance = cursor.getInt(cursor.getColumnIndex("profBalance"));

                ProfileModel profileModel = new ProfileModel(profID, profName, profPIN, profPINSalt,
                        profCreationDate, profLastLoginDate, profLastLoginAttempt, profFailedLoginAttempts, profHelperQuestion, profHelperAnswer, profHelperSalt, profInitialBalance, profBalance);

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

    public void updateProfilePIN(String PIN, String hashSalt, int profID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv =  new ContentValues();
        cv.put(COLUMN_PROFILE_PIN, PIN);
        cv.put(COLUMN_PROFILE_PIN_SALT, hashSalt);
        db.update(TABLE_PROFILE, cv, COLUMN_PROFILE_ID + " = " + profID, null);

        db.close();
    }

    public void updateLastLoginDate(int profID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv =  new ContentValues();
        cv.put(COLUMN_PROFILE_LAST_LOGIN_DATE, getCurrentDateTime());
        db.update(TABLE_PROFILE, cv, COLUMN_PROFILE_ID + " = " + profID, null);

        db.close();
    }

    public void updateHelperQuestionAndAnswer(String question, String answer, String salt, int profID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv =  new ContentValues();
        cv.put(COLUMN_PROFILE_HELPER_QUESTION, question);
        cv.put(COLUMN_PROFILE_HELPER_ANSWER, answer);
        cv.put(COLUMN_PROFILE_HELPER_SALT, salt);
        db.update(TABLE_PROFILE, cv, COLUMN_PROFILE_ID + " = " + profID, null);

        db.close();
    }

    public void resetFailedLoginAttempts(int profID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv =  new ContentValues();
        cv.put(COLUMN_PROFILE_FAILED_LOGIN_ATTEMPTS, 0);
        db.update(TABLE_PROFILE, cv, COLUMN_PROFILE_ID + " = " + profID, null);

        db.close();
    }

    public void addFailedLoginAttempt(int profID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String checkDatabaseforFailedAttempts = "SELECT profFailedLoginAttempts FROM " + TABLE_PROFILE + " WHERE " + COLUMN_PROFILE_ID + " = ?";

        Cursor cursor = db.rawQuery(checkDatabaseforFailedAttempts, new String[] {Integer.toString(profID)});
        cursor.moveToFirst();
        int failedLoginAttempts = cursor.getInt(cursor.getColumnIndex("profFailedLoginAttempts"));

        failedLoginAttempts++;
        if (failedLoginAttempts >= 3) {
            ContentValues cv =  new ContentValues();
            cv.put(COLUMN_PROFILE_FAILED_LOGIN_ATTEMPTS, failedLoginAttempts);
            cv.put(COLUMN_PROFILE_LAST_LOGIN_ATTEMPT, getDateTimeForAccountLock());
            db.update(TABLE_PROFILE, cv, COLUMN_PROFILE_ID + " = " + profID, null);
        } else {
            ContentValues cv =  new ContentValues();
            cv.put(COLUMN_PROFILE_FAILED_LOGIN_ATTEMPTS, failedLoginAttempts);
            cv.put(COLUMN_PROFILE_LAST_LOGIN_ATTEMPT, getCurrentDateTime());
            db.update(TABLE_PROFILE, cv, COLUMN_PROFILE_ID + " = " + profID, null);
        }

        cursor.close();
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

}
