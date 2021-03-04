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
import static pl.dchruscinski.pilnujgrosza.ProfileAdapter.chosenProfileID;

public class DatabaseHelper extends SQLiteOpenHelper {

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

    public static final String TABLE_EXPENSE_CATEGORY = "expensecategory";
    public static final String COLUMN_EXPENSE_CATEGORY_ID = "expcatID";
    public static final String COLUMN_EXPENSE_CATEGORY_PROFILE_ID = "expcatProfID";
    public static final String COLUMN_EXPENSE_CATEGORY_NAME = "expcatName";

    public static final String TABLE_INCOME_CATEGORY = "incomecategory";
    public static final String COLUMN_INCOME_CATEGORY_ID = "inccatID";
    public static final String COLUMN_INCOME_CATEGORY_PROFILE_ID = "inccatProfID";
    public static final String COLUMN_INCOME_CATEGORY_NAME = "inccatName";

    public static final String TABLE_TRANSACTION = "transact";
    public static final String COLUMN_TRANSACTION_ID = "transID";
    public static final String COLUMN_TRANSACTION_PROFILE_ID = "transProfID";
    public static final String COLUMN_TRANSACTION_BUDGET_ID = "transBudID";
    public static final String COLUMN_TRANSACTION_CATEGORY_ID = "transCatID";
    public static final String COLUMN_TRANSACTION_TYPE = "transType";
    public static final String COLUMN_TRANSACTION_DESCRIPTION = "transDescription";
    public static final String COLUMN_TRANSACTION_VALUE = "transValue";
    public static final String COLUMN_TRANSACTION_DATE = "transDate";

    public static final String TABLE_BUDGET = "budget";
    public static final String COLUMN_BUDGET_ID = "budID";
    public static final String COLUMN_BUDGET_PROFILE_ID = "budProfID";
    public static final String COLUMN_BUDGET_INITIAL_AMOUNT = "budInitialAmount";
    public static final String COLUMN_BUDGET_AMOUNT = "budAmount";
    public static final String COLUMN_BUDGET_DESCRIPTION = "budDescription";
    public static final String COLUMN_BUDGET_START_DATE = "budStartDate";
    public static final String COLUMN_BUDGET_END_DATE = "budEndDate";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "pilnujgrosza.db", null, 1);
    }

    // generating new database
    @Override
    public void onCreate(SQLiteDatabase db) {

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

        String createExpenseCategoryTableStatement =
                "CREATE TABLE " + TABLE_EXPENSE_CATEGORY + " (" +
                        COLUMN_EXPENSE_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_EXPENSE_CATEGORY_PROFILE_ID + " INTEGER, " +
                        COLUMN_EXPENSE_CATEGORY_NAME + " TEXT )";

        String createIncomeCategoryTableStatement =
                "CREATE TABLE " + TABLE_INCOME_CATEGORY + " (" +
                        COLUMN_INCOME_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_INCOME_CATEGORY_PROFILE_ID + " INTEGER, " +
                        COLUMN_INCOME_CATEGORY_NAME + " TEXT)";

        String createTransactionTableStatement =
                "CREATE TABLE " + TABLE_TRANSACTION + " (" +
                        COLUMN_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_TRANSACTION_PROFILE_ID + " INTEGER NOT NULL," +
                        COLUMN_TRANSACTION_BUDGET_ID + " INTEGER," +
                        COLUMN_TRANSACTION_CATEGORY_ID + " INTEGER NOT NULL," +
                        COLUMN_TRANSACTION_TYPE + " TEXT NOT NULL," +
                        COLUMN_TRANSACTION_DATE + " DATE," +
                        COLUMN_TRANSACTION_VALUE + " INTEGER," +
                        COLUMN_TRANSACTION_DESCRIPTION + " TEXT)";

        String createBudgetTableStatement =
                "CREATE TABLE " + TABLE_BUDGET + " (" +
                        COLUMN_BUDGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_BUDGET_PROFILE_ID + " INTEGER NOT NULL," +
                        COLUMN_BUDGET_INITIAL_AMOUNT + " INTEGER NOT NULL," +
                        COLUMN_BUDGET_AMOUNT + " INTEGER NOT NULL," +
                        COLUMN_BUDGET_DESCRIPTION + " TEXT," +
                        COLUMN_BUDGET_START_DATE + " DATE NOT NULL," +
                        COLUMN_BUDGET_END_DATE + " DATE NOT NULL)";

        db.execSQL(createProfileTableStatement);
        db.execSQL(createExpenseCategoryTableStatement);
        db.execSQL(createIncomeCategoryTableStatement);
        db.execSQL(createTransactionTableStatement);
        db.execSQL(createBudgetTableStatement);
    }

    // making changes to existing table
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String upgradeProfileTableStatement = "DROP TABLE IF EXISTS " + TABLE_PROFILE;
        String upgradeExpenseCategoryTableStatement = "DROP TABLE IF EXISTS " + TABLE_EXPENSE_CATEGORY;
        String upgradeIncomeCategoryTableStatement = "DROP TABLE IF EXISTS " + TABLE_INCOME_CATEGORY;
        String upgradeTransactionTableStatement = "DROP TABLE IF EXISTS " + TABLE_TRANSACTION;
        String upgradeBudgetTableStatement = "DROP TABLE IF EXISTS " + TABLE_BUDGET;

        db.execSQL(upgradeProfileTableStatement);
        db.execSQL(upgradeExpenseCategoryTableStatement);
        db.execSQL(upgradeIncomeCategoryTableStatement);
        db.execSQL(upgradeTransactionTableStatement);
        db.execSQL(upgradeBudgetTableStatement);
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
        long profID = db.insert(TABLE_PROFILE, null, cv);

        addDefaultExpenseCategory(db, profID);
        addDefaultIncomeCategory(db, profID);
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
        return profilesList;
    }

    public ProfileModel getProfile(int profID) {
        ProfileModel profileModel = new ProfileModel();
        String getProfileStatement = "SELECT * FROM " + TABLE_PROFILE + " WHERE " + COLUMN_PROFILE_ID + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getProfileStatement, new String[] {Integer.toString(profID)});
        if (cursor.moveToFirst()) {
            do {
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

                profileModel = new ProfileModel(profID, profName, profPIN, profPINSalt,
                        profCreationDate, profLastLoginDate, profLastLoginAttempt, profFailedLoginAttempts, profHelperQuestion, profHelperAnswer, profHelperSalt, profInitialBalance, profBalance);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return profileModel;
    }

    public void deleteProfile(int profID) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_PROFILE, COLUMN_PROFILE_ID + " = " + profID, null);
        db.delete(TABLE_TRANSACTION, COLUMN_TRANSACTION_PROFILE_ID + " = " + profID, null);
        db.delete(TABLE_INCOME_CATEGORY, COLUMN_INCOME_CATEGORY_PROFILE_ID + " = " + profID, null);
        db.delete(TABLE_EXPENSE_CATEGORY, COLUMN_EXPENSE_CATEGORY_PROFILE_ID + " = " + profID, null);
    }

    public void updateProfileName(String name, int profID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv =  new ContentValues();
        cv.put(COLUMN_PROFILE_NAME, name);
        db.update(TABLE_PROFILE, cv, COLUMN_PROFILE_ID + " = " + profID, null);
    }

    public void updateProfilePIN(String PIN, String hashSalt, int profID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv =  new ContentValues();
        cv.put(COLUMN_PROFILE_PIN, PIN);
        cv.put(COLUMN_PROFILE_PIN_SALT, hashSalt);
        db.update(TABLE_PROFILE, cv, COLUMN_PROFILE_ID + " = " + profID, null);
    }

    public void updateLastLoginDate(int profID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv =  new ContentValues();
        cv.put(COLUMN_PROFILE_LAST_LOGIN_DATE, getCurrentDateTime());
        db.update(TABLE_PROFILE, cv, COLUMN_PROFILE_ID + " = " + profID, null);
    }

    public void updateHelperQuestionAndAnswer(String question, String answer, String salt, int profID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv =  new ContentValues();
        cv.put(COLUMN_PROFILE_HELPER_QUESTION, question);
        cv.put(COLUMN_PROFILE_HELPER_ANSWER, answer);
        cv.put(COLUMN_PROFILE_HELPER_SALT, salt);
        db.update(TABLE_PROFILE, cv, COLUMN_PROFILE_ID + " = " + profID, null);
    }

    public void updateProfileBalance(int profID, int transactionValue, String transactionType, String operationType) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv =  new ContentValues();
        if (operationType.equals("add")) {
            if (transactionType.equals("expense")) {
                cv.put(COLUMN_PROFILE_BALANCE, (getProfile(profID).getProfBalance() - transactionValue));
            } else {
                cv.put(COLUMN_PROFILE_BALANCE, (getProfile(profID).getProfBalance() + transactionValue));
            }
        } else if (operationType.equals("delete")) {
            if (transactionType.equals("income")) {
                cv.put(COLUMN_PROFILE_BALANCE, (getProfile(profID).getProfBalance() - transactionValue));
            } else {
                cv.put(COLUMN_PROFILE_BALANCE, (getProfile(profID).getProfBalance() + transactionValue));
            }
        }

        db.update(TABLE_PROFILE, cv, COLUMN_PROFILE_ID + " = " + profID, null);
    }

    public String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        Date date = new Date();

        return dateFormat.format(date);
    }

    public String getNextDayDate() {
        /*
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        Date today = new Date();
        Date tomorrow = new Date(today.getTime() + (1000 * 60 * 60 * 24));

        return dateFormat.format(tomorrow);
         */

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date date = cal.getTime();

        return dateFormat.format(date);
    }

    public String getNextMonthDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));

        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 1);

        return dateFormat.format(c.getTime());
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

    public void resetFailedLoginAttempts(int profID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv =  new ContentValues();
        cv.put(COLUMN_PROFILE_FAILED_LOGIN_ATTEMPTS, 0);
        db.update(TABLE_PROFILE, cv, COLUMN_PROFILE_ID + " = " + profID, null);

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
    }

    public boolean checkExistingProfileName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String checkDatabaseforNameStatement = "SELECT * FROM " + TABLE_PROFILE + " WHERE " + COLUMN_PROFILE_NAME + " = ?";

        Cursor cursor = db.rawQuery(checkDatabaseforNameStatement, new String[] {name});
        cursor.moveToFirst();

        int nameOccurrencesCounter = cursor.getCount();
        boolean doesNameExistInDatabase = (nameOccurrencesCounter > 0) ? TRUE : FALSE;

        cursor.close();
        return doesNameExistInDatabase;
    }

    public void addExpenseCategory(ExpenseCategoryModel expenseCategoryModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_EXPENSE_CATEGORY_PROFILE_ID, chosenProfileID);
        cv.put(COLUMN_EXPENSE_CATEGORY_NAME, expenseCategoryModel.getExpcatName());
        db.insert(TABLE_EXPENSE_CATEGORY, null, cv);

    }

    public List<ExpenseCategoryModel> getExpenseCategoryList() {
        List<ExpenseCategoryModel> expenseCategoriesList = new ArrayList<ExpenseCategoryModel>();
        String getExpenseCategoriesListStatement = "SELECT * FROM " + TABLE_EXPENSE_CATEGORY + " WHERE " + COLUMN_EXPENSE_CATEGORY_PROFILE_ID +  " = " + chosenProfileID + " ORDER BY " + COLUMN_EXPENSE_CATEGORY_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getExpenseCategoriesListStatement, null);
        if (cursor.moveToFirst()) {
            do {
                int expcatID = cursor.getInt(cursor.getColumnIndex("expcatID"));
                int expcatProfID = cursor.getInt(cursor.getColumnIndex("expcatProfID"));
                String expcatName = cursor.getString(cursor.getColumnIndex("expcatName"));

                ExpenseCategoryModel expenseCategoryModel = new ExpenseCategoryModel(expcatID, expcatProfID, expcatName);

                expenseCategoriesList.add(expenseCategoryModel);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return expenseCategoriesList;
    }

    public ExpenseCategoryModel getExpenseCategory(int expcatID) {
        ExpenseCategoryModel expenseCategoryModel = new ExpenseCategoryModel();
        String getExpenseCategoryStatement = "SELECT * FROM " + TABLE_EXPENSE_CATEGORY + " WHERE " + COLUMN_EXPENSE_CATEGORY_ID +  " = ?";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getExpenseCategoryStatement, new String[] {Integer.toString(expcatID)});
        if (cursor.moveToFirst()) {
            do {
                int expcatProfID = cursor.getInt(cursor.getColumnIndex("expcatProfID"));
                String expcatName = cursor.getString(cursor.getColumnIndex("expcatName"));

                expenseCategoryModel = new ExpenseCategoryModel(expcatID, expcatProfID, expcatName);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return expenseCategoryModel;
    }

    public Cursor getExpenseCategoriesForSpinner() {
        String getExpenseCategoriesForSpinnerStatement = "SELECT " + COLUMN_EXPENSE_CATEGORY_ID + " AS _id, " + COLUMN_EXPENSE_CATEGORY_NAME + " FROM "
                + TABLE_EXPENSE_CATEGORY + " WHERE " + COLUMN_EXPENSE_CATEGORY_PROFILE_ID +  " = " + chosenProfileID + " ORDER BY " + COLUMN_EXPENSE_CATEGORY_ID;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getExpenseCategoriesForSpinnerStatement, null);

        return cursor;
    }

    public void deleteExpenseCategory(int expcatID) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_EXPENSE_CATEGORY, COLUMN_EXPENSE_CATEGORY_ID + " = " + expcatID, null);
    }

    public void updateExpenseCategoryName(String name, int expcatID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv =  new ContentValues();
        cv.put(COLUMN_EXPENSE_CATEGORY_NAME, name);
        db.update(TABLE_EXPENSE_CATEGORY, cv, COLUMN_EXPENSE_CATEGORY_ID + " = " + expcatID, null);
    }

    public boolean checkExistingExpenseCategoryName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String checkDatabaseforNameStatement = "SELECT * FROM " + TABLE_EXPENSE_CATEGORY + " WHERE " + COLUMN_EXPENSE_CATEGORY_NAME + " = ?" + " AND " + COLUMN_EXPENSE_CATEGORY_PROFILE_ID + " = " + chosenProfileID;

        Cursor cursor = db.rawQuery(checkDatabaseforNameStatement, new String[] {name});
        cursor.moveToFirst();

        int nameOccurrencesCounter = cursor.getCount();
        boolean doesNameExistInDatabase = (nameOccurrencesCounter > 0) ? TRUE : FALSE;

        cursor.close();
        return doesNameExistInDatabase;
    }

    public void addDefaultExpenseCategory(SQLiteDatabase db, long profID) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_EXPENSE_CATEGORY_PROFILE_ID, profID);
        cv.put(COLUMN_EXPENSE_CATEGORY_NAME, "Brak kategorii");
        db.insert(TABLE_EXPENSE_CATEGORY, null, cv);
        cv.clear();

        cv.put(COLUMN_EXPENSE_CATEGORY_PROFILE_ID, profID);
        cv.put(COLUMN_EXPENSE_CATEGORY_NAME, "Dom, mieszkanie");
        db.insert(TABLE_EXPENSE_CATEGORY, null, cv);
        cv.clear();

        cv.put(COLUMN_EXPENSE_CATEGORY_PROFILE_ID, profID);
        cv.put(COLUMN_EXPENSE_CATEGORY_NAME, "Podróże");
        db.insert(TABLE_EXPENSE_CATEGORY, null, cv);
        cv.clear();

        cv.put(COLUMN_EXPENSE_CATEGORY_PROFILE_ID, profID);
        cv.put(COLUMN_EXPENSE_CATEGORY_NAME, "Prezenty");
        db.insert(TABLE_EXPENSE_CATEGORY, null, cv);
        cv.clear();

        cv.put(COLUMN_EXPENSE_CATEGORY_PROFILE_ID, profID);
        cv.put(COLUMN_EXPENSE_CATEGORY_NAME, "Środki higieniczne");
        db.insert(TABLE_EXPENSE_CATEGORY, null, cv);
        cv.clear();

        cv.put(COLUMN_EXPENSE_CATEGORY_PROFILE_ID, profID);
        cv.put(COLUMN_EXPENSE_CATEGORY_NAME, "Samochód");
        db.insert(TABLE_EXPENSE_CATEGORY, null, cv);
        cv.clear();

        cv.put(COLUMN_EXPENSE_CATEGORY_PROFILE_ID, profID);
        cv.put(COLUMN_EXPENSE_CATEGORY_NAME, "Rachunki");
        db.insert(TABLE_EXPENSE_CATEGORY, null, cv);
        cv.clear();

        cv.put(COLUMN_EXPENSE_CATEGORY_PROFILE_ID, profID);
        cv.put(COLUMN_EXPENSE_CATEGORY_NAME, "Żywność");
        db.insert(TABLE_EXPENSE_CATEGORY, null, cv);
        cv.clear();

        cv.put(COLUMN_EXPENSE_CATEGORY_PROFILE_ID, profID);
        cv.put(COLUMN_EXPENSE_CATEGORY_NAME, "Rozrywka");
        db.insert(TABLE_EXPENSE_CATEGORY, null, cv);
        cv.clear();

        cv.put(COLUMN_EXPENSE_CATEGORY_PROFILE_ID, profID);
        cv.put(COLUMN_EXPENSE_CATEGORY_NAME, "Ubrania");
        db.insert(TABLE_EXPENSE_CATEGORY, null, cv);
        cv.clear();

        cv.put(COLUMN_EXPENSE_CATEGORY_PROFILE_ID, profID);
        cv.put(COLUMN_EXPENSE_CATEGORY_NAME, "Zwierzęta");
        db.insert(TABLE_EXPENSE_CATEGORY, null, cv);
        cv.clear();

        cv.put(COLUMN_EXPENSE_CATEGORY_PROFILE_ID, profID);
        cv.put(COLUMN_EXPENSE_CATEGORY_NAME, "Zdrowie");
        db.insert(TABLE_EXPENSE_CATEGORY, null, cv);
    }

    public void addIncomeCategory(IncomeCategoryModel incomeCategoryModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_INCOME_CATEGORY_PROFILE_ID, chosenProfileID);
        cv.put(COLUMN_INCOME_CATEGORY_NAME, incomeCategoryModel.getInccatName());
        db.insert(TABLE_INCOME_CATEGORY, null, cv);
    }

    public List<IncomeCategoryModel> getIncomeCategoryList() {
        List<IncomeCategoryModel> incomeCategoriesList = new ArrayList<IncomeCategoryModel>();
        String getIncomeCategoriesListStatement = "SELECT * FROM " + TABLE_INCOME_CATEGORY + " WHERE " + COLUMN_INCOME_CATEGORY_PROFILE_ID +  " = " + chosenProfileID + " ORDER BY " + COLUMN_INCOME_CATEGORY_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getIncomeCategoriesListStatement, null);
        if (cursor.moveToFirst()) {
            do {
                int inccatID = cursor.getInt(cursor.getColumnIndex("inccatID"));
                int inccatProfID = cursor.getInt(cursor.getColumnIndex("inccatProfID"));
                String inccatName = cursor.getString(cursor.getColumnIndex("inccatName"));

                IncomeCategoryModel incomeCategoryModel = new IncomeCategoryModel(inccatID, inccatProfID, inccatName);

                incomeCategoriesList.add(incomeCategoryModel);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return incomeCategoriesList;
    }

    public IncomeCategoryModel getIncomeCategory(int inccatID) {
        IncomeCategoryModel incomeCategoryModel = new IncomeCategoryModel();
        String getIncomeCategoryStatement = "SELECT * FROM " + TABLE_INCOME_CATEGORY + " WHERE " + COLUMN_INCOME_CATEGORY_ID +  " = ?";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getIncomeCategoryStatement, new String[] {Integer.toString(inccatID)});
        if (cursor.moveToFirst()) {
            do {
                int inccatProfID = cursor.getInt(cursor.getColumnIndex("inccatProfID"));
                String inccatName = cursor.getString(cursor.getColumnIndex("inccatName"));

                incomeCategoryModel = new IncomeCategoryModel(inccatID, inccatProfID, inccatName);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return incomeCategoryModel;
    }



    public Cursor getIncomeCategoriesForSpinner() {
        String getIncomeCategoriesForSpinnerStatement = "SELECT " + COLUMN_INCOME_CATEGORY_ID + " AS _id, " + COLUMN_INCOME_CATEGORY_NAME + " FROM "
                + TABLE_INCOME_CATEGORY + " WHERE " + COLUMN_INCOME_CATEGORY_PROFILE_ID +  " = " + chosenProfileID + " ORDER BY " + COLUMN_INCOME_CATEGORY_ID;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getIncomeCategoriesForSpinnerStatement, null);

        return cursor;
    }

    public void deleteIncomeCategory(int inccatID) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_INCOME_CATEGORY, COLUMN_INCOME_CATEGORY_ID + " = " + inccatID, null);
    }

    public void updateIncomeCategoryName(String name, int inccatID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv =  new ContentValues();
        cv.put(COLUMN_INCOME_CATEGORY_NAME, name);
        db.update(TABLE_INCOME_CATEGORY, cv, COLUMN_INCOME_CATEGORY_ID + " = " + inccatID, null);
    }

    public boolean checkExistingIncomeCategoryName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String checkDatabaseforNameStatement = "SELECT * FROM " + TABLE_INCOME_CATEGORY + " WHERE " + COLUMN_INCOME_CATEGORY_NAME + " = ?" + " AND " + COLUMN_INCOME_CATEGORY_PROFILE_ID + " = " + chosenProfileID;

        Cursor cursor = db.rawQuery(checkDatabaseforNameStatement, new String[] {name});
        cursor.moveToFirst();

        int nameOccurrencesCounter = cursor.getCount();
        boolean doesNameExistInDatabase = (nameOccurrencesCounter > 0) ? TRUE : FALSE;

        cursor.close();
        return doesNameExistInDatabase;
    }

    public void addDefaultIncomeCategory(SQLiteDatabase db, long profID) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_INCOME_CATEGORY_PROFILE_ID, profID);
        cv.put(COLUMN_INCOME_CATEGORY_NAME, "Brak kategorii");
        db.insert(TABLE_INCOME_CATEGORY, null, cv);
        cv.clear();

        cv.put(COLUMN_INCOME_CATEGORY_PROFILE_ID, profID);
        cv.put(COLUMN_INCOME_CATEGORY_NAME, "Wynagrodzenie");
        db.insert(TABLE_INCOME_CATEGORY, null, cv);
        cv.clear();

        cv.put(COLUMN_INCOME_CATEGORY_PROFILE_ID, profID);
        cv.put(COLUMN_INCOME_CATEGORY_NAME, "Prezenty");
        db.insert(TABLE_INCOME_CATEGORY, null, cv);
        cv.clear();

        cv.put(COLUMN_INCOME_CATEGORY_PROFILE_ID, profID);
        cv.put(COLUMN_INCOME_CATEGORY_NAME, "Oszczędności");
        db.insert(TABLE_INCOME_CATEGORY, null, cv);
        cv.clear();

        cv.put(COLUMN_INCOME_CATEGORY_PROFILE_ID, profID);
        cv.put(COLUMN_INCOME_CATEGORY_NAME, "Inne");
        db.insert(TABLE_INCOME_CATEGORY, null, cv);
        cv.clear();
    }

    public void addIncome(TransactionModel transactionModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TRANSACTION_PROFILE_ID, chosenProfileID);
        cv.put(COLUMN_TRANSACTION_BUDGET_ID, transactionModel.getTransBudID());
        cv.put(COLUMN_TRANSACTION_CATEGORY_ID, transactionModel.getTransCatID());
        cv.put(COLUMN_TRANSACTION_TYPE, "income");
        cv.put(COLUMN_TRANSACTION_DESCRIPTION, transactionModel.getTransDescription());
        cv.put(COLUMN_TRANSACTION_VALUE, transactionModel.getTransValue());
        cv.put(COLUMN_TRANSACTION_DATE, transactionModel.getTransDate());
        db.insert(TABLE_TRANSACTION, null, cv);

        updateProfileBalance(chosenProfileID, transactionModel.getTransValue(), "income", "add");
    }

    public void addExpense(TransactionModel transactionModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TRANSACTION_PROFILE_ID, chosenProfileID);
        cv.put(COLUMN_TRANSACTION_BUDGET_ID, transactionModel.getTransBudID());
        cv.put(COLUMN_TRANSACTION_CATEGORY_ID, transactionModel.getTransCatID());
        cv.put(COLUMN_TRANSACTION_TYPE, "expense");
        cv.put(COLUMN_TRANSACTION_DESCRIPTION, transactionModel.getTransDescription());
        cv.put(COLUMN_TRANSACTION_VALUE, transactionModel.getTransValue());
        cv.put(COLUMN_TRANSACTION_DATE, transactionModel.getTransDate());
        db.insert(TABLE_TRANSACTION, null, cv);

        updateProfileBalance(chosenProfileID, transactionModel.getTransValue(), "expense", "add");
        updateBudgetAmount(transactionModel.getTransBudID(), transactionModel.getTransValue(), "add");
    }

    public List<TransactionModel> getTransactionsList() {
        List<TransactionModel> transactionsList = new ArrayList<TransactionModel>();
        String getTransactionsListStatement = "SELECT * FROM " + TABLE_TRANSACTION + " WHERE " + COLUMN_TRANSACTION_PROFILE_ID +  " = " + chosenProfileID + " ORDER BY " + COLUMN_TRANSACTION_DATE + " DESC, " + COLUMN_TRANSACTION_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getTransactionsListStatement, null);
        if (cursor.moveToFirst()) {
            do {
                int transID = cursor.getInt(cursor.getColumnIndex("transID"));
                int transProfID = cursor.getInt(cursor.getColumnIndex("transProfID"));
                int transBudID = cursor.getInt(cursor.getColumnIndex("transBudID"));
                int transCatID = cursor.getInt(cursor.getColumnIndex("transCatID"));
                String transType = cursor.getString(cursor.getColumnIndex("transType"));
                String transDescription = cursor.getString(cursor.getColumnIndex("transDescription"));
                int transValue = cursor.getInt(cursor.getColumnIndex("transValue"));
                String transDate = cursor.getString(cursor.getColumnIndex("transDate"));

                TransactionModel transactionModel = new TransactionModel(transID, transType, transDescription, transValue, transDate, transProfID, transBudID, transCatID);

                transactionsList.add(transactionModel);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return transactionsList;
    }

    public boolean checkStartDateInCreateDialog(int profID, String startDateFromDialog) throws ParseException {
        boolean checkStartDate;
        int betweenCounter = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));

        SQLiteDatabase db = this.getReadableDatabase();
        String getBudgetDates = "SELECT " + COLUMN_BUDGET_START_DATE + ", " + COLUMN_BUDGET_END_DATE + " FROM " + TABLE_BUDGET + " WHERE " + COLUMN_BUDGET_PROFILE_ID + " = ?";

        Cursor cursor = db.rawQuery(getBudgetDates, new String[] {Integer.toString(profID)});
        if (cursor.moveToFirst()) {
            do {
                Date startDateFromDB = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_START_DATE)));
                Date endDateFromDB = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_END_DATE)));

                // if start date set by user in dialog is between any date from DB - increase counter value
                if (dateFormat.parse(startDateFromDialog).after(startDateFromDB) && dateFormat.parse(startDateFromDialog).before(endDateFromDB)) {
                    betweenCounter++;
                }
            } while (cursor.moveToNext());
        }

        // if there is no date set, where start date from dialog is between these dates then return 'true'
        if (betweenCounter > 0) {
            checkStartDate = false;
        } else {
            checkStartDate = true;
        }

        cursor.close();
        return checkStartDate;
    }

    public boolean checkEndDateInCreateDialog(int profID, String endDateFromDialog) throws ParseException {
        boolean checkEndDate;
        int betweenCounter = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));

        SQLiteDatabase db = this.getReadableDatabase();
        String getBudgetDates = "SELECT " + COLUMN_BUDGET_START_DATE + ", " + COLUMN_BUDGET_END_DATE + " FROM " + TABLE_BUDGET + " WHERE " + COLUMN_BUDGET_PROFILE_ID + " = ?";

        Cursor cursor = db.rawQuery(getBudgetDates, new String[] {Integer.toString(profID)});
        if (cursor.moveToFirst()) {
            do {
                Date startDateFromDB = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_START_DATE)));
                Date endDateFromDB = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_END_DATE)));

                // if end date set by user in dialog is between any date from DB - increase counter value
                if (dateFormat.parse(endDateFromDialog).after(startDateFromDB) && dateFormat.parse(endDateFromDialog).before(endDateFromDB)) {
                    betweenCounter++;
                }
            } while (cursor.moveToNext());
        }

        // if there is no date set, where end date from dialog is between these dates then return 'true'
        if (betweenCounter > 0) {
            checkEndDate = false;
        } else {
            checkEndDate = true;
        }

        cursor.close();
        return checkEndDate;
    }

    public boolean checkDatesBetweenStartAndEndDatesInCreateDialog(int profID, String startDateFromDialog, String endDateFromDialog) throws ParseException {
        boolean checkDatesBetween;
        int betweenCounter = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));

        SQLiteDatabase db = this.getReadableDatabase();
        String getBudgetDates = "SELECT " + COLUMN_BUDGET_START_DATE + ", " + COLUMN_BUDGET_END_DATE + " FROM " + TABLE_BUDGET + " WHERE " + COLUMN_BUDGET_PROFILE_ID + " = ?";

        Cursor cursor = db.rawQuery(getBudgetDates, new String[] {Integer.toString(profID)});
        if (cursor.moveToFirst()) {
            do {
                Date startDateFromDB = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_START_DATE)));
                Date endDateFromDB = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_END_DATE)));

                // if end date set by user in dialog is between any date from DB - increase counter value
                if (startDateFromDB.after(dateFormat.parse(startDateFromDialog)) && endDateFromDB.before(dateFormat.parse(endDateFromDialog))) {
                    betweenCounter++;
                }
            } while (cursor.moveToNext());
        }

        // if there is no date set, where end date from dialog is between these dates then return 'true'
        if (betweenCounter > 0) {
            checkDatesBetween = false;
        } else {
            checkDatesBetween = true;
        }

        cursor.close();
        return checkDatesBetween;
    }

    public boolean checkStartDateInEditDialog(int profID, int budID, String startDateFromDialog) throws ParseException {
        boolean checkStartDate;
        int betweenCounter = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));

        SQLiteDatabase db = this.getReadableDatabase();
        String getBudgetDates = "SELECT " + COLUMN_BUDGET_START_DATE + ", " + COLUMN_BUDGET_END_DATE + " FROM " + TABLE_BUDGET + " WHERE " + COLUMN_BUDGET_PROFILE_ID + " = ? AND " + COLUMN_BUDGET_ID + " <> ?" ;

        Cursor cursor = db.rawQuery(getBudgetDates, new String[] {Integer.toString(profID), Integer.toString(budID)});
        if (cursor.moveToFirst()) {
            do {
                Date startDateFromDB = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_START_DATE)));
                Date endDateFromDB = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_END_DATE)));

                // if start date set by user in dialog is between any date from DB - increase counter value
                if (dateFormat.parse(startDateFromDialog).after(startDateFromDB) && dateFormat.parse(startDateFromDialog).before(endDateFromDB)) {
                    betweenCounter++;
                }
            } while (cursor.moveToNext());
        }

        // if there is no date set, where start date from dialog is between these dates then return 'true'
        if (betweenCounter > 0) {
            checkStartDate = false;
        } else {
            checkStartDate = true;
        }

        cursor.close();
        return checkStartDate;
    }

    public boolean checkEndDateInEditDialog(int profID, int budID, String endDateFromDialog) throws ParseException {
        boolean checkEndDate;
        int betweenCounter = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));

        SQLiteDatabase db = this.getReadableDatabase();
        String getBudgetDates = "SELECT " + COLUMN_BUDGET_START_DATE + ", " + COLUMN_BUDGET_END_DATE + " FROM " + TABLE_BUDGET + " WHERE " + COLUMN_BUDGET_PROFILE_ID + " = ? AND " + COLUMN_BUDGET_ID + " <> ?" ;

        Cursor cursor = db.rawQuery(getBudgetDates, new String[] {Integer.toString(profID), Integer.toString(budID)});
        if (cursor.moveToFirst()) {
            do {
                Date startDateFromDB = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_START_DATE)));
                Date endDateFromDB = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_END_DATE)));

                // if end date set by user in dialog is between any date from DB - increase counter value
                if (dateFormat.parse(endDateFromDialog).after(startDateFromDB) && dateFormat.parse(endDateFromDialog).before(endDateFromDB)) {
                    betweenCounter++;
                }
            } while (cursor.moveToNext());
        }

        // if there is no date set, where end date from dialog is between these dates then return 'true'
        if (betweenCounter > 0) {
            checkEndDate = false;
        } else {
            checkEndDate = true;
        }

        cursor.close();
        return checkEndDate;
    }

    public boolean checkDatesBetweenStartAndEndDatesInEditDialog(int profID, int budID, String startDateFromDialog, String endDateFromDialog) throws ParseException {
        boolean checkDatesBetween;
        int betweenCounter = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));

        SQLiteDatabase db = this.getReadableDatabase();
        String getBudgetDates = "SELECT " + COLUMN_BUDGET_START_DATE + ", " + COLUMN_BUDGET_END_DATE + " FROM " + TABLE_BUDGET + " WHERE " + COLUMN_BUDGET_PROFILE_ID + " = ? AND " + COLUMN_BUDGET_ID + " <> ?" ;

        Cursor cursor = db.rawQuery(getBudgetDates, new String[] {Integer.toString(profID), Integer.toString(budID)});
        if (cursor.moveToFirst()) {
            do {
                Date startDateFromDB = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_START_DATE)));
                Date endDateFromDB = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_END_DATE)));

                // if end date set by user in dialog is between any date from DB - increase counter value
                if (startDateFromDB.after(dateFormat.parse(startDateFromDialog)) && endDateFromDB.before(dateFormat.parse(endDateFromDialog))) {
                    betweenCounter++;
                }
            } while (cursor.moveToNext());
        }

        // if there is no date set, where end date from dialog is between these dates then return 'true'
        if (betweenCounter > 0) {
            checkDatesBetween = false;
        } else {
            checkDatesBetween = true;
        }

        cursor.close();
        return checkDatesBetween;
    }

    public boolean checkStartAndEndDateInDialog(String startDate, String endDate) throws ParseException {
        boolean checkStartAndEndDate = false;
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));

        checkStartAndEndDate = dateFormat.parse(startDate).compareTo(dateFormat.parse(endDate)) <= 0;

        return checkStartAndEndDate;
    }

    public void addBudget(BudgetModel budgetModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BUDGET_PROFILE_ID, chosenProfileID);
        cv.put(COLUMN_BUDGET_INITIAL_AMOUNT, budgetModel.getBudInitialAmount());
        cv.put(COLUMN_BUDGET_AMOUNT, 0);
        cv.put(COLUMN_BUDGET_DESCRIPTION, budgetModel.getBudDescription());
        cv.put(COLUMN_BUDGET_START_DATE, budgetModel.getBudStartDate());
        cv.put(COLUMN_BUDGET_END_DATE, budgetModel.getBudEndDate());
        db.insert(TABLE_BUDGET, null, cv);
    }

    public List<BudgetModel> getBudgetsList() {
        List<BudgetModel> budgetsList = new ArrayList<BudgetModel>();
        String getBudgetsListStatement = "SELECT * FROM " + TABLE_BUDGET + " WHERE " + COLUMN_BUDGET_PROFILE_ID +  " = " + chosenProfileID + " ORDER BY " + COLUMN_BUDGET_END_DATE + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getBudgetsListStatement, null);
        if (cursor.moveToFirst()) {
            do {
                int budID = cursor.getInt(cursor.getColumnIndex(COLUMN_BUDGET_ID));
                int budProfID = cursor.getInt(cursor.getColumnIndex(COLUMN_BUDGET_PROFILE_ID));
                int budInitialAmount = cursor.getInt(cursor.getColumnIndex(COLUMN_BUDGET_INITIAL_AMOUNT));
                int budAmount = cursor.getInt(cursor.getColumnIndex(COLUMN_BUDGET_AMOUNT));
                String budDescription = cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_DESCRIPTION));
                String budStartDate = cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_START_DATE));
                String budEndDate = cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_END_DATE));

                BudgetModel budgetModel = new BudgetModel(budID, budProfID, budInitialAmount, budAmount, budDescription, budStartDate, budEndDate);

                budgetsList.add(budgetModel);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return budgetsList;
    }

    public BudgetModel getBudget(int budID) {
        BudgetModel budgetModel = new BudgetModel();
        String getBudgetStatement = "SELECT * FROM " + TABLE_BUDGET + " WHERE " + COLUMN_BUDGET_ID + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getBudgetStatement, new String[] {Integer.toString(budID)});
        if (cursor.moveToFirst()) {
            do {
                int budProfID = cursor.getInt(cursor.getColumnIndex(COLUMN_BUDGET_PROFILE_ID));
                int budInitialAmount = cursor.getInt(cursor.getColumnIndex(COLUMN_BUDGET_INITIAL_AMOUNT));
                int budAmount = cursor.getInt(cursor.getColumnIndex(COLUMN_BUDGET_AMOUNT));
                String budDescription = cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_DESCRIPTION));
                String budStartDate = cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_START_DATE));
                String budEndDate = cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_END_DATE));

                budgetModel = new BudgetModel(budID, budProfID, budInitialAmount, budAmount, budDescription, budStartDate, budEndDate);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return budgetModel;
    }

    public Cursor getBudgetsForSpinner() {
        String getBudgetsForSpinnerStatement = "SELECT " + COLUMN_BUDGET_ID + " AS _id, " + COLUMN_BUDGET_START_DATE + " || ' <> ' || " + COLUMN_BUDGET_END_DATE + " AS budDates FROM "
                + TABLE_BUDGET + " WHERE " + COLUMN_BUDGET_PROFILE_ID +  " = " + chosenProfileID + " ORDER BY " + COLUMN_BUDGET_START_DATE;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getBudgetsForSpinnerStatement, null);

        return cursor;
    }

    public void updateBudget(BudgetModel budgetModel, int budID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BUDGET_INITIAL_AMOUNT, budgetModel.getBudInitialAmount());
        cv.put(COLUMN_BUDGET_DESCRIPTION, budgetModel.getBudDescription());
        cv.put(COLUMN_BUDGET_START_DATE, budgetModel.getBudStartDate());
        cv.put(COLUMN_BUDGET_END_DATE, budgetModel.getBudEndDate());
        db.update(TABLE_BUDGET, cv, COLUMN_BUDGET_ID + " = " + budID, null);
    }

    public void updateBudgetAmount(int budID, int transactionValue, String operationType) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv =  new ContentValues();
        if (operationType.equals("add")) {
            cv.put(COLUMN_BUDGET_AMOUNT, (getBudget(budID).getBudAmount() + transactionValue));
        } else {
            cv.put(COLUMN_BUDGET_AMOUNT, (getBudget(budID).getBudAmount() - transactionValue));
        }

        db.update(TABLE_BUDGET, cv, COLUMN_BUDGET_ID + " = " + budID, null);
    }

    public void deleteBudget(int budID) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_BUDGET, COLUMN_BUDGET_ID + " = " + budID, null);
    }

}
