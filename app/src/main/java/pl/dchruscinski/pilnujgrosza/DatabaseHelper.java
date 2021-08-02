package pl.dchruscinski.pilnujgrosza;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Math.abs;
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
    public static final String COLUMN_TRANSACTION_CHANGE_INITIAL_BUDGET = "transChangeInitialBudget";

    public static final String TABLE_SCHEDULED_PAYMENT = "schpay";
    public static final String COLUMN_SCHEDULED_PAYMENT_ID = "schpayID";
    public static final String COLUMN_SCHEDULED_PAYMENT_PROFILE_ID = "schpayProfID";
    public static final String COLUMN_SCHEDULED_PAYMENT_BUDGET_ID = "schpayBudID";
    public static final String COLUMN_SCHEDULED_PAYMENT_CATEGORY_ID = "schpayCatID";
    public static final String COLUMN_SCHEDULED_PAYMENT_DESCRIPTION = "schpayDescription";
    public static final String COLUMN_SCHEDULED_PAYMENT_VALUE = "schpayValue";
    public static final String COLUMN_SCHEDULED_PAYMENT_DATE = "schpayDate";

    public static final String TABLE_BUDGET = "budget";
    public static final String COLUMN_BUDGET_ID = "budID";
    public static final String COLUMN_BUDGET_PROFILE_ID = "budProfID";
    public static final String COLUMN_BUDGET_INITIAL_AMOUNT = "budInitialAmount";
    public static final String COLUMN_BUDGET_AMOUNT = "budAmount";
    public static final String COLUMN_BUDGET_DESCRIPTION = "budDescription";
    public static final String COLUMN_BUDGET_START_DATE = "budStartDate";
    public static final String COLUMN_BUDGET_END_DATE = "budEndDate";

    public static final String TABLE_SETTINGS = "settings";
    public static final String COLUMN_SETTINGS_ID = "setID";
    public static final String COLUMN_SETTINGS_PROFILE_ID = "setProfID";
    public static final String COLUMN_SETTINGS_NAME = "setName";
    public static final String COLUMN_SETTINGS_VALUE = "setValue";

    public static final String TABLE_SHOPPING = "shopping";
    public static final String COLUMN_SHOPPING_ID = "shoID";
    public static final String COLUMN_SHOPPING_PROFILE_ID = "shoProfID";
    public static final String COLUMN_SHOPPING_RECEIPT_ID = "shoRecID";
    public static final String COLUMN_SHOPPING_EXPENSE_ID = "shoExpID";
    public static final String COLUMN_SHOPPING_NAME = "shoName";
    public static final String COLUMN_SHOPPING_DATE = "shoDate";
    public static final String COLUMN_SHOPPING_DESCRIPTION = "shoDesc";
    public static final String COLUMN_SHOPPING_VALUE = "shoValue";

    public static final String TABLE_SHOPPING_CONTENT = "shoppingcontent";
    public static final String COLUMN_SHOPPING_CONTENT_ID = "shoContID";
    public static final String COLUMN_SHOPPING_CONTENT_SHOPPING_ID = "shoContShoID";
    public static final String COLUMN_SHOPPING_CONTENT_NAME = "shoContName";
    public static final String COLUMN_SHOPPING_CONTENT_AMOUNT = "shoContAmount";
    public static final String COLUMN_SHOPPING_CONTENT_UNIT = "shoContUnit";
    public static final String COLUMN_SHOPPING_CONTENT_VALUE = "shoContValue";
    public static final String COLUMN_SHOPPING_CONTENT_STATUS = "shoContStatus";

    public static final String TIMEZONE = "Europe/Warsaw";
    static final int callbackId = 42;

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
                        COLUMN_TRANSACTION_CHANGE_INITIAL_BUDGET + " INTEGER," +
                        COLUMN_TRANSACTION_DESCRIPTION + " TEXT)";

        String createScheduledPaymentTableStatement =
                "CREATE TABLE " + TABLE_SCHEDULED_PAYMENT + " (" +
                        COLUMN_SCHEDULED_PAYMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_SCHEDULED_PAYMENT_PROFILE_ID + " INTEGER NOT NULL," +
                        COLUMN_SCHEDULED_PAYMENT_BUDGET_ID + " INTEGER," +
                        COLUMN_SCHEDULED_PAYMENT_CATEGORY_ID + " INTEGER NOT NULL," +
                        COLUMN_SCHEDULED_PAYMENT_DATE + " DATE," +
                        COLUMN_SCHEDULED_PAYMENT_VALUE + " INTEGER," +
                        COLUMN_SCHEDULED_PAYMENT_DESCRIPTION + " TEXT)";

        String createBudgetTableStatement =
                "CREATE TABLE " + TABLE_BUDGET + " (" +
                        COLUMN_BUDGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_BUDGET_PROFILE_ID + " INTEGER NOT NULL," +
                        COLUMN_BUDGET_INITIAL_AMOUNT + " INTEGER NOT NULL," +
                        COLUMN_BUDGET_AMOUNT + " INTEGER NOT NULL," +
                        COLUMN_BUDGET_DESCRIPTION + " TEXT," +
                        COLUMN_BUDGET_START_DATE + " DATE NOT NULL," +
                        COLUMN_BUDGET_END_DATE + " DATE NOT NULL)";

        String createSettingsTableStatement =
                "CREATE TABLE " + TABLE_SETTINGS + " (" +
                        COLUMN_SETTINGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_SETTINGS_PROFILE_ID + " INTEGER NOT NULL," +
                        COLUMN_SETTINGS_NAME + " TEXT NOT NULL," +
                        COLUMN_SETTINGS_VALUE + " TEXT NOT NULL)";

        String createShoppingTableStatement =
                "CREATE TABLE " + TABLE_SHOPPING + " (" +
                        COLUMN_SHOPPING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_SHOPPING_PROFILE_ID + " INTEGER NOT NULL," +
                        COLUMN_SHOPPING_RECEIPT_ID + " INTEGER," +
                        COLUMN_SHOPPING_EXPENSE_ID + " INTEGER," +
                        COLUMN_SHOPPING_NAME + " TEXT NOT NULL," +
                        COLUMN_SHOPPING_DATE + " DATE NOT NULL," +
                        COLUMN_SHOPPING_DESCRIPTION + " TEXT," +
                        COLUMN_SHOPPING_VALUE + " INTEGER)";

        String createShoppingContentTableStatement =
                "CREATE TABLE " + TABLE_SHOPPING_CONTENT + " (" +
                        COLUMN_SHOPPING_CONTENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_SHOPPING_CONTENT_SHOPPING_ID + " INTEGER NOT NULL," +
                        COLUMN_SHOPPING_CONTENT_NAME + " TEXT NOT NULL," +
                        COLUMN_SHOPPING_CONTENT_AMOUNT + " TEXT NOT NULL," +
                        COLUMN_SHOPPING_CONTENT_UNIT + " TEXT NOT NULL," +
                        COLUMN_SHOPPING_CONTENT_VALUE + " INTEGER," +
                        COLUMN_SHOPPING_CONTENT_STATUS + " BOOLEAN)";

        db.execSQL(createProfileTableStatement);
        db.execSQL(createExpenseCategoryTableStatement);
        db.execSQL(createIncomeCategoryTableStatement);
        db.execSQL(createTransactionTableStatement);
        db.execSQL(createScheduledPaymentTableStatement);
        db.execSQL(createBudgetTableStatement);
        db.execSQL(createSettingsTableStatement);
        db.execSQL(createShoppingTableStatement);
        db.execSQL(createShoppingContentTableStatement);
    }

    // making changes to existing table
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String upgradeProfileTableStatement = "DROP TABLE IF EXISTS " + TABLE_PROFILE;
        String upgradeExpenseCategoryTableStatement = "DROP TABLE IF EXISTS " + TABLE_EXPENSE_CATEGORY;
        String upgradeIncomeCategoryTableStatement = "DROP TABLE IF EXISTS " + TABLE_INCOME_CATEGORY;
        String upgradeTransactionTableStatement = "DROP TABLE IF EXISTS " + TABLE_TRANSACTION;
        String upgradeScheduledPaymentTableStatement = "DROP TABLE IF EXISTS " + TABLE_SCHEDULED_PAYMENT;
        String upgradeBudgetTableStatement = "DROP TABLE IF EXISTS " + TABLE_BUDGET;
        String upgradeSettingsTableStatement = "DROP TABLE IF EXISTS " + TABLE_SETTINGS;
        String upgradeShoppingTableStatement = "DROP TABLE IF EXISTS " + TABLE_SHOPPING;
        String upgradeShoppingContentTableStatement = "DROP TABLE IF EXISTS " + TABLE_SHOPPING_CONTENT;

        db.execSQL(upgradeProfileTableStatement);
        db.execSQL(upgradeExpenseCategoryTableStatement);
        db.execSQL(upgradeIncomeCategoryTableStatement);
        db.execSQL(upgradeTransactionTableStatement);
        db.execSQL(upgradeScheduledPaymentTableStatement);
        db.execSQL(upgradeBudgetTableStatement);
        db.execSQL(upgradeSettingsTableStatement);
        db.execSQL(upgradeShoppingTableStatement);
        db.execSQL(upgradeShoppingContentTableStatement);
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

        cv.clear();

        addDefaultSettings(db, profID);
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
        db.delete(TABLE_BUDGET, COLUMN_BUDGET_PROFILE_ID + " = " + profID, null);
        db.delete(TABLE_SCHEDULED_PAYMENT, COLUMN_SCHEDULED_PAYMENT_PROFILE_ID + " = " + profID, null);
        db.delete(TABLE_SETTINGS, COLUMN_SETTINGS_PROFILE_ID + " = " + profID, null);
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
        } else if (operationType.equals("subtract")) {
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
        dateFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
        Date date = new Date();

        return dateFormat.format(date);
    }

    public String getCurrentMonthYearDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
        Date date = new Date();

        return dateFormat.format(date);
    }

    public String getNextDayDate() {
        /*
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Warsaw"));
        Date today = new Date();
        Date tomorrow = new Date(today.getTime() + (1000 * 60 * 60 * 24));

        return dateFormat.format(tomorrow);
         */

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Warsaw"));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date date = cal.getTime();

        return dateFormat.format(date);
    }

    public String getNextMonthDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE));

        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 1);

        return dateFormat.format(c.getTime());
    }

    public String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
        Date date = new Date();

        return dateFormat.format(date);
    }

    public String getDateTimeForAccountLock() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE));

        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, MINUTES_OF_ACCOUNT_LOCK);

        return dateFormat.format(c.getTime());
    }

    public boolean compareLoginDateTime(int profID) throws ParseException {
        boolean compareLoginDateTime;
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE));

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

    public int getNumberOfMonthsBetweenDates(Date d1, Date d2){
        if (d1 == null || d2 == null) {
            return -1;
        }

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(d1);
        int nMonth1 = 12 * calendar.get(Calendar.YEAR) + calendar.get(Calendar.MONTH);

        calendar.setTime(d2);
        int nMonth2 = 12 * calendar.get(Calendar.YEAR) + calendar.get(Calendar.MONTH);

        return java.lang.Math.abs(nMonth2 - nMonth1) + 1;
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

    public boolean checkExistingProfileName(int profID, String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String checkDatabaseforNameStatement = "SELECT * FROM " + TABLE_PROFILE + " WHERE " + COLUMN_PROFILE_NAME + " = ? AND " + COLUMN_PROFILE_ID + " <> ?";

        Cursor cursor = db.rawQuery(checkDatabaseforNameStatement, new String[] {name, String.valueOf(profID)});
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

    public boolean checkExistingExpenseCategoryName(int expCatID, String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String checkDatabaseforNameStatement = "SELECT * FROM " + TABLE_EXPENSE_CATEGORY + " WHERE " + COLUMN_EXPENSE_CATEGORY_NAME + " = ? AND "
                + COLUMN_EXPENSE_CATEGORY_PROFILE_ID + " = " + chosenProfileID + " AND " + COLUMN_EXPENSE_CATEGORY_ID + " <> ?";

        Cursor cursor = db.rawQuery(checkDatabaseforNameStatement, new String[] {name, String.valueOf(expCatID)});
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

    public boolean checkExistingIncomeCategoryName(int incCatID, String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String checkDatabaseforNameStatement = "SELECT * FROM " + TABLE_INCOME_CATEGORY + " WHERE " + COLUMN_INCOME_CATEGORY_NAME + " = ? AND "
                + COLUMN_INCOME_CATEGORY_PROFILE_ID + " = " + chosenProfileID + " AND " + COLUMN_INCOME_CATEGORY_ID + " <> ?";

        Cursor cursor = db.rawQuery(checkDatabaseforNameStatement, new String[] {name, String.valueOf(incCatID)});
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

    public void addIncome(TransactionModel transactionModel, boolean changeBudgetInitialAmount) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TRANSACTION_PROFILE_ID, chosenProfileID);
        cv.put(COLUMN_TRANSACTION_BUDGET_ID, transactionModel.getTransBudID());
        cv.put(COLUMN_TRANSACTION_CATEGORY_ID, transactionModel.getTransCatID());
        cv.put(COLUMN_TRANSACTION_TYPE, "income");
        cv.put(COLUMN_TRANSACTION_DESCRIPTION, transactionModel.getTransDescription());
        cv.put(COLUMN_TRANSACTION_VALUE, transactionModel.getTransValue());
        cv.put(COLUMN_TRANSACTION_DATE, transactionModel.getTransDate());
        cv.put(COLUMN_TRANSACTION_CHANGE_INITIAL_BUDGET, transactionModel.getTransChangeInitialBudget());
        db.insert(TABLE_TRANSACTION, null, cv);

        updateProfileBalance(chosenProfileID, transactionModel.getTransValue(), "income", "add");
        if (changeBudgetInitialAmount && transactionModel.getTransBudID() != 0) {
            updateBudgetInitialAmount(transactionModel.getTransBudID(), transactionModel.getTransValue(), "add");
        }
    }

    public void updateIncome(int transID, TransactionModel transactionModel, boolean changeBudgetInitialAmount) {
        int transOldValue = 0, transNewValue;
        SQLiteDatabase db = this.getWritableDatabase();
        String getTransactionStatement = "SELECT transValue FROM " + TABLE_TRANSACTION + " WHERE " + COLUMN_TRANSACTION_ID + " = ?";

        Cursor cursor = db.rawQuery(getTransactionStatement, new String[] {Integer.toString(transID)});
        if (cursor.moveToFirst()) {
            do {
                transOldValue = cursor.getInt(cursor.getColumnIndex(COLUMN_TRANSACTION_VALUE));
            } while (cursor.moveToNext());
        }

        transNewValue = transactionModel.getTransValue();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TRANSACTION_DATE, transactionModel.getTransDate());
        cv.put(COLUMN_TRANSACTION_DESCRIPTION, transactionModel.getTransDescription());
        cv.put(COLUMN_TRANSACTION_CATEGORY_ID, transactionModel.getTransCatID());
        cv.put(COLUMN_TRANSACTION_BUDGET_ID, transactionModel.getTransBudID());
        cv.put(COLUMN_TRANSACTION_VALUE, transNewValue);
        cv.put(COLUMN_TRANSACTION_CHANGE_INITIAL_BUDGET, transactionModel.getTransChangeInitialBudget());
        db.update(TABLE_TRANSACTION, cv, COLUMN_TRANSACTION_ID + " = " + transID, null);

        if (transNewValue > transOldValue) {
            updateProfileBalance(chosenProfileID, transNewValue - transOldValue, "income", "add");
        } else {
            updateProfileBalance(chosenProfileID, transOldValue - transNewValue, "income", "subtract");
        }

        if (changeBudgetInitialAmount && transactionModel.getTransBudID() != 0) {
            if (transNewValue > transOldValue) {
                updateBudgetInitialAmount(transactionModel.getTransBudID(), transNewValue - transOldValue, "add");
            } else {
                updateBudgetInitialAmount(transactionModel.getTransBudID(), transOldValue - transNewValue, "subtract");
            }
        }
    }

    public void deleteIncome(int transID, boolean changeBudgetInitialAmount) {
        int transValue = 0, transBudID = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        String getTransactionStatement = "SELECT transValue, transBudID FROM " + TABLE_TRANSACTION + " WHERE " + COLUMN_TRANSACTION_ID + " = ?";

        Cursor cursor = db.rawQuery(getTransactionStatement, new String[] {Integer.toString(transID)});
        if (cursor.moveToFirst()) {
            do {
                transValue = cursor.getInt(cursor.getColumnIndex(COLUMN_TRANSACTION_VALUE));
                transBudID = cursor.getInt(cursor.getColumnIndex(COLUMN_TRANSACTION_BUDGET_ID));
            } while (cursor.moveToNext());
        }

        updateProfileBalance(chosenProfileID, transValue, "income", "subtract");

        if (changeBudgetInitialAmount) {
            updateBudgetInitialAmount(transBudID, transValue, "subtract");
        }

        db.delete(TABLE_TRANSACTION, COLUMN_TRANSACTION_ID + " = " + transID, null);
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
        cv.putNull(COLUMN_TRANSACTION_CHANGE_INITIAL_BUDGET);
        db.insert(TABLE_TRANSACTION, null, cv);

        updateProfileBalance(chosenProfileID, transactionModel.getTransValue(), "expense", "add");
        updateBudgetAmount(transactionModel.getTransBudID(), transactionModel.getTransValue(), "add");
    }

    public void updateExpense(int transID, TransactionModel transactionModel) {
        int transOldValue = 0, transNewValue;
        SQLiteDatabase db = this.getWritableDatabase();
        String getTransactionStatement = "SELECT transValue FROM " + TABLE_TRANSACTION + " WHERE " + COLUMN_TRANSACTION_ID + " = ?";

        Cursor cursor = db.rawQuery(getTransactionStatement, new String[] {Integer.toString(transID)});
        if (cursor.moveToFirst()) {
            do {
                transOldValue = cursor.getInt(cursor.getColumnIndex(COLUMN_TRANSACTION_VALUE));
            } while (cursor.moveToNext());
        }

        transNewValue = transactionModel.getTransValue();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TRANSACTION_DATE, transactionModel.getTransDate());
        cv.put(COLUMN_TRANSACTION_DESCRIPTION, transactionModel.getTransDescription());
        cv.put(COLUMN_TRANSACTION_CATEGORY_ID, transactionModel.getTransCatID());
        cv.put(COLUMN_TRANSACTION_BUDGET_ID, transactionModel.getTransBudID());
        cv.put(COLUMN_TRANSACTION_VALUE, transNewValue);
        cv.putNull(COLUMN_TRANSACTION_CHANGE_INITIAL_BUDGET);
        db.update(TABLE_TRANSACTION, cv, COLUMN_TRANSACTION_ID + " = " + transID, null);

        if (transNewValue > transOldValue) {
            updateProfileBalance(chosenProfileID, transNewValue - transOldValue, "expense", "add");
        } else {
            updateProfileBalance(chosenProfileID, transOldValue - transNewValue, "expense", "subtract");
        }

        if (transactionModel.getTransBudID() != 0) {
            if (transNewValue > transOldValue) {
                updateBudgetAmount(transactionModel.getTransBudID(), transNewValue - transOldValue, "add");
            } else {
                updateBudgetAmount(transactionModel.getTransBudID(), transOldValue - transNewValue, "subtract");
            }
        }
    }

    public void deleteExpense(int transID) {
        int transValue = 0, transBudID = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        String getTransactionStatement = "SELECT transValue, transBudID FROM " + TABLE_TRANSACTION + " WHERE " + COLUMN_TRANSACTION_ID + " = ?";

        Cursor cursor = db.rawQuery(getTransactionStatement, new String[] {Integer.toString(transID)});
        if (cursor.moveToFirst()) {
            do {
                transValue = cursor.getInt(cursor.getColumnIndex(COLUMN_TRANSACTION_VALUE));
                transBudID = cursor.getInt(cursor.getColumnIndex(COLUMN_TRANSACTION_BUDGET_ID));
            } while (cursor.moveToNext());
        }

        updateProfileBalance(chosenProfileID, transValue, "expense", "subtract");
        updateBudgetAmount(transBudID, transValue, "delete");
        db.delete(TABLE_TRANSACTION, COLUMN_TRANSACTION_ID + " = " + transID, null);
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
                int transChangeInitialBudget = cursor.getInt(cursor.getColumnIndex("transChangeInitialBudget"));

                TransactionModel transactionModel = new TransactionModel(transID, transType, transDescription, transValue, transDate, transProfID, transBudID, transCatID, transChangeInitialBudget);

                transactionsList.add(transactionModel);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return transactionsList;
    }

    public List<TransactionModel> getTransactionsListForBudget(int budID) {
        List<TransactionModel> transactionsList = new ArrayList<TransactionModel>();
        String getTransactionsListStatement = "SELECT * FROM " + TABLE_TRANSACTION + " WHERE " + COLUMN_TRANSACTION_PROFILE_ID +  " = " + chosenProfileID + " AND " + COLUMN_TRANSACTION_BUDGET_ID + " = " + budID + " ORDER BY " + COLUMN_TRANSACTION_DATE + " DESC, " + COLUMN_TRANSACTION_ID + " DESC";
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
                int transChangeInitialBudget = cursor.getInt(cursor.getColumnIndex("transChangeInitialBudget"));

                TransactionModel transactionModel = new TransactionModel(transID, transType, transDescription, transValue, transDate, transProfID, transBudID, transCatID, transChangeInitialBudget);

                transactionsList.add(transactionModel);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return transactionsList;
    }

    public void unlinkTransactions(int budID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv =  new ContentValues();
        cv.putNull(COLUMN_TRANSACTION_BUDGET_ID);
        db.update(TABLE_TRANSACTION, cv, COLUMN_TRANSACTION_BUDGET_ID + " = " + budID, null);
    }

    public void deleteTransactionsWithBudget(int budID) {
        int balance = 0;
        SQLiteDatabase db = this.getWritableDatabase();

        String getTransactionsValues = "SELECT " + COLUMN_TRANSACTION_ID + ", " + COLUMN_TRANSACTION_VALUE + ", " + COLUMN_TRANSACTION_TYPE + " FROM " + TABLE_TRANSACTION + " WHERE " + COLUMN_TRANSACTION_BUDGET_ID + " = ?";

        Cursor cursor = db.rawQuery(getTransactionsValues, new String[] {Integer.toString(budID)});
        if (cursor.moveToFirst()) {
            do {
                int transID = cursor.getInt(cursor.getColumnIndex(COLUMN_TRANSACTION_ID));
                int transValue = cursor.getInt(cursor.getColumnIndex(COLUMN_TRANSACTION_VALUE));
                String transactionType = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_TYPE));

                if (transactionType.equals("income")) {
                    balance += transValue;
                } else {
                    balance -= transValue;
                }

                db.delete(TABLE_TRANSACTION, COLUMN_TRANSACTION_ID + " = " + transID, null);
            } while (cursor.moveToNext());
        }

        if (balance > 0) {
            updateProfileBalance(chosenProfileID, abs(balance), "income", "subtract");
        } else if (balance < 0) {
            updateProfileBalance(chosenProfileID, abs(balance), "expense", "subtract");
        }
    }

    public void addScheduledPayment(ScheduledPaymentModel scheduledPaymentModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SCHEDULED_PAYMENT_PROFILE_ID, chosenProfileID);
        cv.put(COLUMN_SCHEDULED_PAYMENT_BUDGET_ID, scheduledPaymentModel.getSchpayBudID());
        cv.put(COLUMN_SCHEDULED_PAYMENT_CATEGORY_ID, scheduledPaymentModel.getSchpayCatID());
        cv.put(COLUMN_SCHEDULED_PAYMENT_DESCRIPTION, scheduledPaymentModel.getSchpayDescription());
        cv.put(COLUMN_SCHEDULED_PAYMENT_VALUE, scheduledPaymentModel.getSchpayValue());
        cv.put(COLUMN_SCHEDULED_PAYMENT_DATE, scheduledPaymentModel.getSchpayDate());
        db.insert(TABLE_SCHEDULED_PAYMENT, null, cv);
    }

    public void updateScheduledPayment(int schpayID, ScheduledPaymentModel scheduledPaymentModel) {
        int schpayOldValue = 0, schpayNewValue;
        SQLiteDatabase db = this.getWritableDatabase();
        String getTransactionStatement = "SELECT schpayValue FROM " + TABLE_SCHEDULED_PAYMENT + " WHERE " + COLUMN_SCHEDULED_PAYMENT_ID + " = ?";

        Cursor cursor = db.rawQuery(getTransactionStatement, new String[] {Integer.toString(schpayID)});
        if (cursor.moveToFirst()) {
            do {
                schpayOldValue = cursor.getInt(cursor.getColumnIndex(COLUMN_SCHEDULED_PAYMENT_VALUE));
            } while (cursor.moveToNext());
        }

        schpayNewValue = scheduledPaymentModel.getSchpayValue();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SCHEDULED_PAYMENT_BUDGET_ID, scheduledPaymentModel.getSchpayBudID());
        cv.put(COLUMN_SCHEDULED_PAYMENT_CATEGORY_ID, scheduledPaymentModel.getSchpayCatID());
        cv.put(COLUMN_SCHEDULED_PAYMENT_DESCRIPTION, scheduledPaymentModel.getSchpayDescription());
        cv.put(COLUMN_SCHEDULED_PAYMENT_VALUE, schpayNewValue);
        cv.put(COLUMN_SCHEDULED_PAYMENT_DATE, scheduledPaymentModel.getSchpayDate());
        db.update(TABLE_SCHEDULED_PAYMENT, cv, COLUMN_SCHEDULED_PAYMENT_ID + " = " + schpayID, null);
        cursor.close();
    }

    public void deleteScheduledPayment(int schpayID) {
        int schpayValue = 0, schpayBudID = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        String getScheduledPaymentStatement = "SELECT schpayValue, schpayBudID FROM " + TABLE_SCHEDULED_PAYMENT + " WHERE " + COLUMN_SCHEDULED_PAYMENT_ID + " = ?";

        Cursor cursor = db.rawQuery(getScheduledPaymentStatement, new String[] {Integer.toString(schpayID)});
        if (cursor.moveToFirst()) {
            do {
                schpayValue = cursor.getInt(cursor.getColumnIndex(COLUMN_SCHEDULED_PAYMENT_VALUE));
                schpayBudID = cursor.getInt(cursor.getColumnIndex(COLUMN_SCHEDULED_PAYMENT_BUDGET_ID));
            } while (cursor.moveToNext());
        }
        db.delete(TABLE_SCHEDULED_PAYMENT, COLUMN_SCHEDULED_PAYMENT_ID + " = " + schpayID, null);
        cursor.close();
    }

    public List<ScheduledPaymentModel> getScheduledPaymentsList() {
        List<ScheduledPaymentModel> scheduledPaymentList = new ArrayList<>();
        String getScheduledPaymentsListStatement = "SELECT * FROM " + TABLE_SCHEDULED_PAYMENT + " WHERE " + COLUMN_SCHEDULED_PAYMENT_PROFILE_ID +  " = " + chosenProfileID + " ORDER BY " + COLUMN_SCHEDULED_PAYMENT_DATE + " DESC, " + COLUMN_SCHEDULED_PAYMENT_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getScheduledPaymentsListStatement, null);
        if (cursor.moveToFirst()) {
            do {
                int schpayID = cursor.getInt(cursor.getColumnIndex("schpayID"));
                int schpayProfID = cursor.getInt(cursor.getColumnIndex("schpayProfID"));
                int schpayBudID = cursor.getInt(cursor.getColumnIndex("schpayBudID"));
                int schpayCatID = cursor.getInt(cursor.getColumnIndex("schpayCatID"));
                String schpayDescription = cursor.getString(cursor.getColumnIndex("schpayDescription"));
                int schpayValue = cursor.getInt(cursor.getColumnIndex("schpayValue"));
                String schpayDate = cursor.getString(cursor.getColumnIndex("schpayDate"));

                ScheduledPaymentModel scheduledPaymentModel = new ScheduledPaymentModel(schpayID, schpayDescription, schpayValue, schpayDate, schpayProfID, schpayBudID, schpayCatID);

                scheduledPaymentList.add(scheduledPaymentModel);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return scheduledPaymentList;
    }

    public int getLastScheduledPayment() {
        int lastScheduledPayment = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SCHEDULED_PAYMENT, null);
        if (cursor.moveToLast()){
            lastScheduledPayment = cursor.getInt(cursor.getColumnIndex(COLUMN_SCHEDULED_PAYMENT_ID));
        }

        cursor.close();
        return lastScheduledPayment;
    }

    public boolean checkStartDateInCreateDialog(int profID, String startDateFromDialog) throws ParseException {
        boolean checkStartDate;
        int betweenCounter = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Warsaw"));

        SQLiteDatabase db = this.getReadableDatabase();
        String getBudgetDates = "SELECT " + COLUMN_BUDGET_START_DATE + ", " + COLUMN_BUDGET_END_DATE + " FROM " + TABLE_BUDGET + " WHERE " + COLUMN_BUDGET_PROFILE_ID + " = ?";

        Cursor cursor = db.rawQuery(getBudgetDates, new String[] {Integer.toString(profID)});
        if (cursor.moveToFirst()) {
            do {
                Date startDateFromDB = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_START_DATE)));
                Date endDateFromDB = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_END_DATE)));

                // if start date set by user in dialog is between any date from DB - increase counter value

                if (dateFormat.parse(startDateFromDialog).compareTo(startDateFromDB) >= 0
                        && dateFormat.parse(startDateFromDialog).compareTo(endDateFromDB) <= 0) {

                /*
                if (dateFormat.parse(startDateFromDialog).after(startDateFromDB) && !dateFormat.parse(startDateFromDialog).equals(startDateFromDB)
                        && dateFormat.parse(startDateFromDialog).before(endDateFromDB) && !dateFormat.parse(startDateFromDialog).equals(endDateFromDB)) {

                 */
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
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Warsaw"));

        SQLiteDatabase db = this.getReadableDatabase();
        String getBudgetDates = "SELECT " + COLUMN_BUDGET_START_DATE + ", " + COLUMN_BUDGET_END_DATE + " FROM " + TABLE_BUDGET + " WHERE " + COLUMN_BUDGET_PROFILE_ID + " = ?";

        Cursor cursor = db.rawQuery(getBudgetDates, new String[] {Integer.toString(profID)});
        if (cursor.moveToFirst()) {
            do {
                Date startDateFromDB = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_START_DATE)));
                Date endDateFromDB = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_END_DATE)));

                // if end date set by user in dialog is between any date from DB - increase counter value
                if (dateFormat.parse(endDateFromDialog).compareTo(startDateFromDB) >= 0
                        && dateFormat.parse(endDateFromDialog).compareTo(endDateFromDB) <= 0) {

                /*
                if (dateFormat.parse(endDateFromDialog).after(startDateFromDB) && !dateFormat.parse(endDateFromDialog).equals(startDateFromDB)
                        && dateFormat.parse(endDateFromDialog).before(endDateFromDB) && !dateFormat.parse(endDateFromDialog).equals(endDateFromDB)) {
                */

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
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Warsaw"));

        SQLiteDatabase db = this.getReadableDatabase();
        String getBudgetDates = "SELECT " + COLUMN_BUDGET_START_DATE + ", " + COLUMN_BUDGET_END_DATE + " FROM " + TABLE_BUDGET + " WHERE " + COLUMN_BUDGET_PROFILE_ID + " = ?";

        Cursor cursor = db.rawQuery(getBudgetDates, new String[] {Integer.toString(profID)});
        if (cursor.moveToFirst()) {
            do {
                Date startDateFromDB = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_START_DATE)));
                Date endDateFromDB = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_END_DATE)));

                // if end date set by user in dialog is between any date from DB - increase counter value
                if (startDateFromDB.compareTo(dateFormat.parse(startDateFromDialog)) >= 0
                        && endDateFromDB.compareTo(dateFormat.parse(endDateFromDialog)) <= 0) {

                /*
                if (startDateFromDB.after(dateFormat.parse(startDateFromDialog)) && !startDateFromDB.equals(dateFormat.parse(startDateFromDialog))
                        && endDateFromDB.before(dateFormat.parse(endDateFromDialog)) && !endDateFromDB.equals(dateFormat.parse(endDateFromDialog))) {
                 */

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
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Warsaw"));

        SQLiteDatabase db = this.getReadableDatabase();
        String getBudgetDates = "SELECT " + COLUMN_BUDGET_START_DATE + ", " + COLUMN_BUDGET_END_DATE + " FROM " + TABLE_BUDGET + " WHERE " + COLUMN_BUDGET_PROFILE_ID + " = ? AND " + COLUMN_BUDGET_ID + " <> ?" ;

        Cursor cursor = db.rawQuery(getBudgetDates, new String[] {Integer.toString(profID), Integer.toString(budID)});
        if (cursor.moveToFirst()) {
            do {
                Date startDateFromDB = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_START_DATE)));
                Date endDateFromDB = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_END_DATE)));

                // if start date set by user in dialog is between any date from DB - increase counter value
                if (dateFormat.parse(startDateFromDialog).compareTo(startDateFromDB) >= 0
                        && dateFormat.parse(startDateFromDialog).compareTo(endDateFromDB) <= 0) {

                /*
                if (dateFormat.parse(startDateFromDialog).after(startDateFromDB) && !dateFormat.parse(startDateFromDialog).equals(startDateFromDB)
                        && dateFormat.parse(startDateFromDialog).before(endDateFromDB) && !dateFormat.parse(startDateFromDialog).equals(endDateFromDB)) {

                 */

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
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Warsaw"));

        SQLiteDatabase db = this.getReadableDatabase();
        String getBudgetDates = "SELECT " + COLUMN_BUDGET_START_DATE + ", " + COLUMN_BUDGET_END_DATE + " FROM " + TABLE_BUDGET + " WHERE " + COLUMN_BUDGET_PROFILE_ID + " = ? AND " + COLUMN_BUDGET_ID + " <> ?" ;

        Cursor cursor = db.rawQuery(getBudgetDates, new String[] {Integer.toString(profID), Integer.toString(budID)});
        if (cursor.moveToFirst()) {
            do {
                Date startDateFromDB = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_START_DATE)));
                Date endDateFromDB = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_END_DATE)));

                // if end date set by user in dialog is between any date from DB - increase counter value
                if (dateFormat.parse(endDateFromDialog).compareTo(startDateFromDB) >= 0
                        && dateFormat.parse(endDateFromDialog).compareTo(endDateFromDB) <= 0) {

                /*
                if (dateFormat.parse(endDateFromDialog).after(startDateFromDB) && !dateFormat.parse(endDateFromDialog).equals(startDateFromDB)
                        && dateFormat.parse(endDateFromDialog).before(endDateFromDB) && !dateFormat.parse(endDateFromDialog).equals(endDateFromDB)) {

                 */

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
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Warsaw"));

        SQLiteDatabase db = this.getReadableDatabase();
        String getBudgetDates = "SELECT " + COLUMN_BUDGET_START_DATE + ", " + COLUMN_BUDGET_END_DATE + " FROM " + TABLE_BUDGET + " WHERE " + COLUMN_BUDGET_PROFILE_ID + " = ? AND " + COLUMN_BUDGET_ID + " <> ?" ;

        Cursor cursor = db.rawQuery(getBudgetDates, new String[] {Integer.toString(profID), Integer.toString(budID)});
        if (cursor.moveToFirst()) {
            do {
                Date startDateFromDB = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_START_DATE)));
                Date endDateFromDB = dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_END_DATE)));

                // if end date set by user in dialog is between any date from DB - increase counter value
                if (startDateFromDB.compareTo(dateFormat.parse(startDateFromDialog)) >= 0
                        && endDateFromDB.compareTo(dateFormat.parse(endDateFromDialog)) <= 0) {

                /*
                if (startDateFromDB.after(dateFormat.parse(startDateFromDialog)) && !startDateFromDB.equals(dateFormat.parse(startDateFromDialog))
                        && endDateFromDB.before(dateFormat.parse(endDateFromDialog)) && !endDateFromDB.equals(dateFormat.parse(endDateFromDialog))) {
                 */

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
        dateFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE));

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

    public void updateBudgetInitialAmount(int budID, int transactionValue, String operationType) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv =  new ContentValues();
        if (operationType.equals("add")) {
            cv.put(COLUMN_BUDGET_INITIAL_AMOUNT, (getBudget(budID).getBudInitialAmount() + transactionValue));
        } else {
            cv.put(COLUMN_BUDGET_INITIAL_AMOUNT, (getBudget(budID).getBudInitialAmount() - transactionValue));
        }

        db.update(TABLE_BUDGET, cv, COLUMN_BUDGET_ID + " = " + budID, null);
    }

    public void deleteBudget(int budID) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_BUDGET, COLUMN_BUDGET_ID + " = " + budID, null);
    }

    public void addDefaultSettings(SQLiteDatabase db, long profID) {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_SETTINGS_PROFILE_ID, profID);
        cv.put(COLUMN_SETTINGS_NAME, "language");
        cv.put(COLUMN_SETTINGS_VALUE, "polish");
        db.insert(TABLE_SETTINGS, null, cv);
        cv.clear();

        cv.put(COLUMN_SETTINGS_PROFILE_ID, profID);
        cv.put(COLUMN_SETTINGS_NAME, "currency");
        cv.put(COLUMN_SETTINGS_VALUE, "zł");
        db.insert(TABLE_SETTINGS, null, cv);
        cv.clear();

        cv.put(COLUMN_SETTINGS_PROFILE_ID, profID);
        cv.put(COLUMN_SETTINGS_NAME, "theme");
        cv.put(COLUMN_SETTINGS_VALUE, "light");
        db.insert(TABLE_SETTINGS, null, cv);
        cv.clear();

        cv.put(COLUMN_SETTINGS_PROFILE_ID, profID);
        cv.put(COLUMN_SETTINGS_NAME, "notificationsTime");
        cv.put(COLUMN_SETTINGS_VALUE, "19:00");
        db.insert(TABLE_SETTINGS, null, cv);
        cv.clear();

        cv.put(COLUMN_SETTINGS_PROFILE_ID, profID);
        cv.put(COLUMN_SETTINGS_NAME, "peopleInHousehold");
        cv.put(COLUMN_SETTINGS_VALUE, "1");
        db.insert(TABLE_SETTINGS, null, cv);
        cv.clear();
    }

    public List<SettingsModel> getSettingsList() {
        List<SettingsModel> settingsList = new ArrayList<SettingsModel>();
        String getSettingsListStatement = "SELECT * FROM " + TABLE_SETTINGS + " WHERE " + COLUMN_SETTINGS_PROFILE_ID +  " = " + chosenProfileID;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getSettingsListStatement, null);
        if (cursor.moveToFirst()) {
            do {
                int setID = cursor.getInt(cursor.getColumnIndex(COLUMN_SETTINGS_ID));
                int setProfID = cursor.getInt(cursor.getColumnIndex(COLUMN_SETTINGS_PROFILE_ID));
                String setName = cursor.getString(cursor.getColumnIndex(COLUMN_SETTINGS_NAME));
                String setValue = cursor.getString(cursor.getColumnIndex(COLUMN_SETTINGS_VALUE));

                SettingsModel settingsModel = new SettingsModel(setID, setProfID, setName, setValue);

                settingsList.add(settingsModel);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return settingsList;
    }

    public String getCurrency(int profID) {
        String currency = "";
        String getCurrencyStatement = "SELECT " + COLUMN_SETTINGS_VALUE + " FROM " + TABLE_SETTINGS + " WHERE " + COLUMN_SETTINGS_PROFILE_ID + " = ? AND " + COLUMN_SETTINGS_NAME + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getCurrencyStatement, new String[] {Integer.toString(profID), "currency"});
        if (cursor.moveToFirst()) {
            do {
                currency = cursor.getString(cursor.getColumnIndex(COLUMN_SETTINGS_VALUE));

            } while (cursor.moveToNext());
        }

        cursor.close();
        return currency;
    }

    public String[] getNotificationsTime(int profID) {
        String notificationsTimeString = "";
        String notificationsTime[];
        String getCurrencyStatement = "SELECT " + COLUMN_SETTINGS_VALUE + " FROM " + TABLE_SETTINGS + " WHERE " + COLUMN_SETTINGS_PROFILE_ID + " = ? AND " + COLUMN_SETTINGS_NAME + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getCurrencyStatement, new String[] {Integer.toString(profID), "notificationsTime"});
        if (cursor.moveToFirst()) {
            do {
                notificationsTimeString = cursor.getString(cursor.getColumnIndex(COLUMN_SETTINGS_VALUE));

            } while (cursor.moveToNext());
        }

        notificationsTime = notificationsTimeString.split(":");

        cursor.close();
        return notificationsTime;
    }

    public int getPeopleInHousehold(int profID) {
        int peopleInHousehold = 0;
        String getCurrencyStatement = "SELECT " + COLUMN_SETTINGS_VALUE + " FROM " + TABLE_SETTINGS + " WHERE " + COLUMN_SETTINGS_PROFILE_ID + " = ? AND " + COLUMN_SETTINGS_NAME + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getCurrencyStatement, new String[] {Integer.toString(profID), "peopleInHousehold"});
        if (cursor.moveToFirst()) {
            do {
                peopleInHousehold = cursor.getInt(cursor.getColumnIndex(COLUMN_SETTINGS_VALUE));

            } while (cursor.moveToNext());
        }

        cursor.close();
        return peopleInHousehold;
    }

    public boolean getTheme(int profID) {
        String theme = "";
        boolean isDarkModeOn = false;
        String getCurrencyStatement = "SELECT " + COLUMN_SETTINGS_VALUE + " FROM " + TABLE_SETTINGS + " WHERE " + COLUMN_SETTINGS_PROFILE_ID + " = ? AND " + COLUMN_SETTINGS_NAME + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getCurrencyStatement, new String[] {Integer.toString(profID), "theme"});
        if (cursor.moveToFirst()) {
            do {
                theme = cursor.getString(cursor.getColumnIndex(COLUMN_SETTINGS_VALUE));

            } while (cursor.moveToNext());
        }

        cursor.close();
        if (theme.equals("dark")) isDarkModeOn = true;
        return isDarkModeOn;
    }

    public void updateSettings(SettingsModel settingsModel, String setName, int profID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SETTINGS_VALUE, settingsModel.getSetValue());
        db.update(TABLE_SETTINGS, cv, COLUMN_SETTINGS_PROFILE_ID + " = " + profID + " AND " + COLUMN_SETTINGS_NAME + " = '" + setName + "'", null);
    }

    public List<Map> getProfileOverallStatistics() throws ParseException {
        List<Map> profileStatistics = new ArrayList<>();
        Map<String, BigDecimal> bigIntegerStatistics = new HashMap<>();
        Map<String, String> stringStatistics = new HashMap<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Warsaw"));

        String startDate, endDate;
        startDate = endDate = "1900-01-01";

        int sumOfIncomes, sumOfExpenses, balance, numberOfIncomes, numberOfExpenses, maxIncome, maxExpense;
        sumOfIncomes = sumOfExpenses = numberOfIncomes = numberOfExpenses = maxIncome = maxExpense = 0;

        String getProfileStatisticsStatement = "SELECT * FROM " + TABLE_TRANSACTION + " WHERE " + COLUMN_TRANSACTION_PROFILE_ID +  " = " + chosenProfileID + " ORDER BY " + COLUMN_TRANSACTION_DATE + " DESC, " + COLUMN_TRANSACTION_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getProfileStatisticsStatement, null);
        if (cursor.moveToFirst()) {
            do {
                String transType = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_TYPE));
                int transValue = cursor.getInt(cursor.getColumnIndex(COLUMN_TRANSACTION_VALUE));
                String transDate = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_DATE));

                if (dateFormat.parse(transDate).compareTo(dateFormat.parse(startDate)) <= 0 || dateFormat.parse(startDate).compareTo(dateFormat.parse("1900-01-01")) == 0) startDate = transDate;
                if (dateFormat.parse(transDate).compareTo(dateFormat.parse(endDate)) >= 0) endDate = transDate;

                if (transType.equals("income")) {
                    sumOfIncomes += transValue;
                    numberOfIncomes++;
                    if (transValue > maxIncome) maxIncome = transValue;
                } else {
                    sumOfExpenses += transValue;
                    numberOfExpenses++;
                    if (transValue > maxExpense) maxExpense = transValue;
                }

            } while (cursor.moveToNext());

            balance = sumOfIncomes - sumOfExpenses;

            int monthsBetween = getNumberOfMonthsBetweenDates(dateFormat.parse(endDate), dateFormat.parse(startDate));
            if (numberOfIncomes + numberOfExpenses != 0 && monthsBetween == 0) monthsBetween = 1;

            bigIntegerStatistics.put("balance", BigDecimal.valueOf(balance));
            bigIntegerStatistics.put("numberOfIncomes", BigDecimal.valueOf(numberOfIncomes));
            bigIntegerStatistics.put("numberOfExpenses", BigDecimal.valueOf(numberOfExpenses));
            bigIntegerStatistics.put("numberOfTransactions", BigDecimal.valueOf(numberOfIncomes + numberOfExpenses));
            bigIntegerStatistics.put("sumOfIncomes", BigDecimal.valueOf(sumOfIncomes));
            bigIntegerStatistics.put("sumOfExpenses", BigDecimal.valueOf(sumOfExpenses));
            bigIntegerStatistics.put("maxIncome", BigDecimal.valueOf(maxIncome));
            bigIntegerStatistics.put("maxExpense", BigDecimal.valueOf(maxExpense));
            bigIntegerStatistics.put("averageIncome", BigDecimal.valueOf((numberOfIncomes == 0)? 0 : sumOfIncomes / numberOfIncomes));
            bigIntegerStatistics.put("averageExpense", BigDecimal.valueOf((numberOfExpenses == 0)? 0 : sumOfExpenses / numberOfExpenses));
            bigIntegerStatistics.put("numberOfMonths", BigDecimal.valueOf(monthsBetween));
            bigIntegerStatistics.put("averageIncomePerMonth", BigDecimal.valueOf((monthsBetween == 0)? 0 : sumOfIncomes / monthsBetween));
            bigIntegerStatistics.put("averageExpensePerMonth", BigDecimal.valueOf((monthsBetween == 0)? 0 : sumOfExpenses / monthsBetween));
            bigIntegerStatistics.put("averageCost", BigDecimal.valueOf(sumOfExpenses / getPeopleInHousehold(chosenProfileID)));

            stringStatistics.put("startDate", startDate);
            stringStatistics.put("endDate", endDate);
        }

        profileStatistics.add(bigIntegerStatistics);
        profileStatistics.add(stringStatistics);

        cursor.close();
        return profileStatistics;
    }

    public List<Map> getProfileOverallStatisticsForPieChart() {
        List<Map> profileStatisticsForPieChart = new ArrayList<>();
        Map<String, BigDecimal> incomeStatisticsForPieChart = new HashMap<>();
        Map<String, BigDecimal> expenseStatisticsForPieChart = new HashMap<>();
        BigDecimal value = BigDecimal.valueOf(0);
        String sumOfTransactionsColumn = "transSum";

        String getIncomeStatisticsStatement = "SELECT SUM(" + COLUMN_TRANSACTION_VALUE + ") AS " + sumOfTransactionsColumn + ", " + COLUMN_INCOME_CATEGORY_NAME + " FROM " + TABLE_TRANSACTION + " A INNER JOIN " + TABLE_INCOME_CATEGORY + " B ON "
                + COLUMN_TRANSACTION_CATEGORY_ID + " = " + COLUMN_INCOME_CATEGORY_ID + " WHERE " + COLUMN_TRANSACTION_PROFILE_ID +  " = " + chosenProfileID + " AND " + COLUMN_TRANSACTION_TYPE + " = ? GROUP BY 2";
        String getExpenseStatisticsStatement = "SELECT SUM(" + COLUMN_TRANSACTION_VALUE + ") AS " + sumOfTransactionsColumn + ", " + COLUMN_EXPENSE_CATEGORY_NAME + " FROM " + TABLE_TRANSACTION + " A INNER JOIN " + TABLE_EXPENSE_CATEGORY + " B ON "
                + COLUMN_TRANSACTION_CATEGORY_ID + " = " + COLUMN_EXPENSE_CATEGORY_ID + " WHERE " + COLUMN_TRANSACTION_PROFILE_ID +  " = " + chosenProfileID + " AND " + COLUMN_TRANSACTION_TYPE + " = ? GROUP BY 2";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor incomeCursor = db.rawQuery(getIncomeStatisticsStatement, new String[] {"income"});
        if (incomeCursor.moveToFirst()) {
            do {
                int transSum = incomeCursor.getInt(incomeCursor.getColumnIndex(sumOfTransactionsColumn));
                String transCategory = incomeCursor.getString(incomeCursor.getColumnIndex(COLUMN_INCOME_CATEGORY_NAME));

                value = BigDecimal.valueOf(transSum).divide(BigDecimal.valueOf(100));
                incomeStatisticsForPieChart.put(transCategory, value);

            } while (incomeCursor.moveToNext());
        }

        profileStatisticsForPieChart.add(incomeStatisticsForPieChart);
        incomeCursor.close();

        Cursor expenseCursor = db.rawQuery(getExpenseStatisticsStatement, new String[] {"expense"});
        if (expenseCursor.moveToFirst()) {
            do {
                int transSum = expenseCursor.getInt(expenseCursor.getColumnIndex(sumOfTransactionsColumn));
                String transCategory = expenseCursor.getString(expenseCursor.getColumnIndex(COLUMN_EXPENSE_CATEGORY_NAME));

                value = BigDecimal.valueOf(transSum).divide(BigDecimal.valueOf(100));
                expenseStatisticsForPieChart.put(transCategory, value);

            } while (expenseCursor.moveToNext());
        }

        profileStatisticsForPieChart.add(expenseStatisticsForPieChart);
        expenseCursor.close();

        return profileStatisticsForPieChart;
    }

    public List<Map> getProfileBudgetStatistics(int budID) throws ParseException {
        List<Map> profileStatistics = new ArrayList<>();
        Map<String, BigDecimal> bigIntegerStatistics = new HashMap<>();
        Map<String, String> stringStatistics = new HashMap<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Warsaw"));

        String startDate, endDate;
        startDate = endDate = "1900-01-01";

        int sumOfIncomes, sumOfExpenses, balance, numberOfIncomes, numberOfExpenses, maxIncome, maxExpense;
        sumOfIncomes = sumOfExpenses = numberOfIncomes = numberOfExpenses = maxIncome = maxExpense = 0;

        String getProfileStatisticsStatement = "SELECT * FROM " + TABLE_TRANSACTION + " WHERE " + COLUMN_TRANSACTION_PROFILE_ID +  " = " + chosenProfileID + " AND " + COLUMN_TRANSACTION_BUDGET_ID + " = " + budID + " ORDER BY " + COLUMN_TRANSACTION_DATE + " DESC, " + COLUMN_TRANSACTION_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getProfileStatisticsStatement, null);
        if (cursor.moveToFirst()) {
            do {
                String transType = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_TYPE));
                int transValue = cursor.getInt(cursor.getColumnIndex(COLUMN_TRANSACTION_VALUE));
                String transDate = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_DATE));

                if (dateFormat.parse(transDate).compareTo(dateFormat.parse(startDate)) <= 0 || dateFormat.parse(startDate).compareTo(dateFormat.parse("1900-01-01")) == 0) startDate = transDate;
                if (dateFormat.parse(transDate).compareTo(dateFormat.parse(endDate)) >= 0) endDate = transDate;

                if (transType.equals("income")) {
                    sumOfIncomes += transValue;
                    numberOfIncomes++;
                    if (transValue > maxIncome) maxIncome = transValue;
                } else {
                    sumOfExpenses += transValue;
                    numberOfExpenses++;
                    if (transValue > maxExpense) maxExpense = transValue;
                }

            } while (cursor.moveToNext());

            balance = sumOfIncomes - sumOfExpenses;

            int monthsBetween = getNumberOfMonthsBetweenDates(dateFormat.parse(endDate), dateFormat.parse(startDate));
            if (numberOfIncomes + numberOfExpenses != 0 && monthsBetween == 0) monthsBetween = 1;

            bigIntegerStatistics.put("balance", BigDecimal.valueOf(balance));
            bigIntegerStatistics.put("numberOfIncomes", BigDecimal.valueOf(numberOfIncomes));
            bigIntegerStatistics.put("numberOfExpenses", BigDecimal.valueOf(numberOfExpenses));
            bigIntegerStatistics.put("numberOfTransactions", BigDecimal.valueOf(numberOfIncomes + numberOfExpenses));
            bigIntegerStatistics.put("sumOfIncomes", BigDecimal.valueOf(sumOfIncomes));
            bigIntegerStatistics.put("sumOfExpenses", BigDecimal.valueOf(sumOfExpenses));
            bigIntegerStatistics.put("maxIncome", BigDecimal.valueOf(maxIncome));
            bigIntegerStatistics.put("maxExpense", BigDecimal.valueOf(maxExpense));
            bigIntegerStatistics.put("averageIncome", BigDecimal.valueOf((numberOfIncomes == 0)? 0 : sumOfIncomes / numberOfIncomes));
            bigIntegerStatistics.put("averageExpense", BigDecimal.valueOf((numberOfExpenses == 0)? 0 : sumOfExpenses / numberOfExpenses));
            bigIntegerStatistics.put("numberOfMonths", BigDecimal.valueOf(monthsBetween));
            bigIntegerStatistics.put("averageIncomePerMonth", BigDecimal.valueOf((monthsBetween == 0)? 0 : sumOfIncomes / monthsBetween));
            bigIntegerStatistics.put("averageExpensePerMonth", BigDecimal.valueOf((monthsBetween == 0)? 0 : sumOfExpenses / monthsBetween));
            bigIntegerStatistics.put("averageCost", BigDecimal.valueOf(sumOfExpenses / getPeopleInHousehold(chosenProfileID)));

            stringStatistics.put("startDate", startDate);
            stringStatistics.put("endDate", endDate);
        }

        profileStatistics.add(bigIntegerStatistics);
        profileStatistics.add(stringStatistics);

        cursor.close();
        return profileStatistics;
    }

    public List<Map> getProfileBudgetStatisticsForPieChart(int budID) {
        List<Map> profileStatisticsForPieChart = new ArrayList<>();
        Map<String, BigDecimal> incomeStatisticsForPieChart = new HashMap<>();
        Map<String, BigDecimal> expenseStatisticsForPieChart = new HashMap<>();
        BigDecimal value = BigDecimal.valueOf(0);
        String sumOfTransactionsColumn = "transSum";

        String getIncomeStatisticsStatement = "SELECT SUM(" + COLUMN_TRANSACTION_VALUE + ") AS " + sumOfTransactionsColumn + ", " + COLUMN_INCOME_CATEGORY_NAME + " FROM " + TABLE_TRANSACTION + " A INNER JOIN " + TABLE_INCOME_CATEGORY + " B ON "
                + COLUMN_TRANSACTION_CATEGORY_ID + " = " + COLUMN_INCOME_CATEGORY_ID + " WHERE " + COLUMN_TRANSACTION_PROFILE_ID +  " = " + chosenProfileID + " AND " + COLUMN_TRANSACTION_BUDGET_ID + " = " + budID + " AND " + COLUMN_TRANSACTION_TYPE + " = ? GROUP BY 2";
        String getExpenseStatisticsStatement = "SELECT SUM(" + COLUMN_TRANSACTION_VALUE + ") AS " + sumOfTransactionsColumn + ", " + COLUMN_EXPENSE_CATEGORY_NAME + " FROM " + TABLE_TRANSACTION + " A INNER JOIN " + TABLE_EXPENSE_CATEGORY + " B ON "
                + COLUMN_TRANSACTION_CATEGORY_ID + " = " + COLUMN_EXPENSE_CATEGORY_ID + " WHERE " + COLUMN_TRANSACTION_PROFILE_ID +  " = " + chosenProfileID + " AND " + COLUMN_TRANSACTION_BUDGET_ID + " = " + budID + " AND " + COLUMN_TRANSACTION_TYPE + " = ? GROUP BY 2";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor incomeCursor = db.rawQuery(getIncomeStatisticsStatement, new String[] {"income"});
        if (incomeCursor.moveToFirst()) {
            do {
                int transSum = incomeCursor.getInt(incomeCursor.getColumnIndex(sumOfTransactionsColumn));
                String transCategory = incomeCursor.getString(incomeCursor.getColumnIndex(COLUMN_INCOME_CATEGORY_NAME));

                value = BigDecimal.valueOf(transSum).divide(BigDecimal.valueOf(100));
                incomeStatisticsForPieChart.put(transCategory, value);

            } while (incomeCursor.moveToNext());
        }

        profileStatisticsForPieChart.add(incomeStatisticsForPieChart);
        incomeCursor.close();

        Cursor expenseCursor = db.rawQuery(getExpenseStatisticsStatement, new String[] {"expense"});
        if (expenseCursor.moveToFirst()) {
            do {
                int transSum = expenseCursor.getInt(expenseCursor.getColumnIndex(sumOfTransactionsColumn));
                String transCategory = expenseCursor.getString(expenseCursor.getColumnIndex(COLUMN_EXPENSE_CATEGORY_NAME));

                value = BigDecimal.valueOf(transSum).divide(BigDecimal.valueOf(100));
                expenseStatisticsForPieChart.put(transCategory, value);

            } while (expenseCursor.moveToNext());
        }

        profileStatisticsForPieChart.add(expenseStatisticsForPieChart);
        expenseCursor.close();

        return profileStatisticsForPieChart;
    }

    public List<Map> getProfileMonthStatistics(String month) throws ParseException {
        List<Map> profileStatistics = new ArrayList<>();
        Map<String, BigDecimal> bigIntegerStatistics = new HashMap<>();
        Map<String, String> stringStatistics = new HashMap<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Warsaw"));

        String startDate, endDate;
        startDate = endDate = "1900-01-01";

        int sumOfIncomes, sumOfExpenses, balance, numberOfIncomes, numberOfExpenses, maxIncome, maxExpense;
        sumOfIncomes = sumOfExpenses = numberOfIncomes = numberOfExpenses = maxIncome = maxExpense = 0;

        String getProfileStatisticsStatement = "SELECT * FROM " + TABLE_TRANSACTION + " WHERE " + COLUMN_TRANSACTION_PROFILE_ID +  " = " + chosenProfileID + " AND " + COLUMN_TRANSACTION_DATE + " BETWEEN '" + month + "-01' AND '" + month + "-31' ORDER BY " + COLUMN_TRANSACTION_DATE + " DESC, " + COLUMN_TRANSACTION_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getProfileStatisticsStatement, null);
        if (cursor.moveToFirst()) {
            do {
                String transType = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_TYPE));
                int transValue = cursor.getInt(cursor.getColumnIndex(COLUMN_TRANSACTION_VALUE));
                String transDate = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_DATE));

                if (dateFormat.parse(transDate).compareTo(dateFormat.parse(startDate)) <= 0 || dateFormat.parse(startDate).compareTo(dateFormat.parse("1900-01-01")) == 0) startDate = transDate;
                if (dateFormat.parse(transDate).compareTo(dateFormat.parse(endDate)) >= 0) endDate = transDate;

                if (transType.equals("income")) {
                    sumOfIncomes += transValue;
                    numberOfIncomes++;
                    if (transValue > maxIncome) maxIncome = transValue;
                } else {
                    sumOfExpenses += transValue;
                    numberOfExpenses++;
                    if (transValue > maxExpense) maxExpense = transValue;
                }

            } while (cursor.moveToNext());

            balance = sumOfIncomes - sumOfExpenses;

            int monthsBetween = getNumberOfMonthsBetweenDates(dateFormat.parse(endDate), dateFormat.parse(startDate));
            if (numberOfIncomes + numberOfExpenses != 0 && monthsBetween == 0) monthsBetween = 1;

            bigIntegerStatistics.put("balance", BigDecimal.valueOf(balance));
            bigIntegerStatistics.put("numberOfIncomes", BigDecimal.valueOf(numberOfIncomes));
            bigIntegerStatistics.put("numberOfExpenses", BigDecimal.valueOf(numberOfExpenses));
            bigIntegerStatistics.put("numberOfTransactions", BigDecimal.valueOf(numberOfIncomes + numberOfExpenses));
            bigIntegerStatistics.put("sumOfIncomes", BigDecimal.valueOf(sumOfIncomes));
            bigIntegerStatistics.put("sumOfExpenses", BigDecimal.valueOf(sumOfExpenses));
            bigIntegerStatistics.put("maxIncome", BigDecimal.valueOf(maxIncome));
            bigIntegerStatistics.put("maxExpense", BigDecimal.valueOf(maxExpense));
            bigIntegerStatistics.put("averageIncome", BigDecimal.valueOf((numberOfIncomes == 0)? 0 : sumOfIncomes / numberOfIncomes));
            bigIntegerStatistics.put("averageExpense", BigDecimal.valueOf((numberOfExpenses == 0)? 0 : sumOfExpenses / numberOfExpenses));
            bigIntegerStatistics.put("numberOfMonths", BigDecimal.valueOf(monthsBetween));
            bigIntegerStatistics.put("averageIncomePerMonth", BigDecimal.valueOf((monthsBetween == 0)? 0 : sumOfIncomes / monthsBetween));
            bigIntegerStatistics.put("averageExpensePerMonth", BigDecimal.valueOf((monthsBetween == 0)? 0 : sumOfExpenses / monthsBetween));
            bigIntegerStatistics.put("averageCost", BigDecimal.valueOf(sumOfExpenses / getPeopleInHousehold(chosenProfileID)));

            stringStatistics.put("startDate", startDate);
            stringStatistics.put("endDate", endDate);
        }

        profileStatistics.add(bigIntegerStatistics);
        profileStatistics.add(stringStatistics);

        cursor.close();
        return profileStatistics;
    }

    public List<Map> getProfileMonthStatisticsForPieChart(String month) {
        List<Map> profileStatisticsForPieChart = new ArrayList<>();
        Map<String, BigDecimal> incomeStatisticsForPieChart = new HashMap<>();
        Map<String, BigDecimal> expenseStatisticsForPieChart = new HashMap<>();
        BigDecimal value = BigDecimal.valueOf(0);
        String sumOfTransactionsColumn = "transSum";

        String getIncomeStatisticsStatement = "SELECT SUM(" + COLUMN_TRANSACTION_VALUE + ") AS " + sumOfTransactionsColumn + ", " + COLUMN_INCOME_CATEGORY_NAME + " FROM " + TABLE_TRANSACTION + " A INNER JOIN " + TABLE_INCOME_CATEGORY + " B ON "
                + COLUMN_TRANSACTION_CATEGORY_ID + " = " + COLUMN_INCOME_CATEGORY_ID + " WHERE " + COLUMN_TRANSACTION_PROFILE_ID +  " = " + chosenProfileID + " AND " + COLUMN_TRANSACTION_DATE + " BETWEEN '" + month + "-01' AND '" + month + "-31' AND " + COLUMN_TRANSACTION_TYPE + " = ? GROUP BY 2";
        String getExpenseStatisticsStatement = "SELECT SUM(" + COLUMN_TRANSACTION_VALUE + ") AS " + sumOfTransactionsColumn + ", " + COLUMN_EXPENSE_CATEGORY_NAME + " FROM " + TABLE_TRANSACTION + " A INNER JOIN " + TABLE_EXPENSE_CATEGORY + " B ON "
                + COLUMN_TRANSACTION_CATEGORY_ID + " = " + COLUMN_EXPENSE_CATEGORY_ID + " WHERE " + COLUMN_TRANSACTION_PROFILE_ID +  " = " + chosenProfileID + " AND " + COLUMN_TRANSACTION_DATE + " BETWEEN '" + month + "-01' AND '" + month + "-31' AND " + COLUMN_TRANSACTION_TYPE + " = ? GROUP BY 2";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor incomeCursor = db.rawQuery(getIncomeStatisticsStatement, new String[] {"income"});
        if (incomeCursor.moveToFirst()) {
            do {
                int transSum = incomeCursor.getInt(incomeCursor.getColumnIndex(sumOfTransactionsColumn));
                String transCategory = incomeCursor.getString(incomeCursor.getColumnIndex(COLUMN_INCOME_CATEGORY_NAME));

                value = BigDecimal.valueOf(transSum).divide(BigDecimal.valueOf(100));
                incomeStatisticsForPieChart.put(transCategory, value);

            } while (incomeCursor.moveToNext());
        }

        profileStatisticsForPieChart.add(incomeStatisticsForPieChart);
        incomeCursor.close();

        Cursor expenseCursor = db.rawQuery(getExpenseStatisticsStatement, new String[] {"expense"});
        if (expenseCursor.moveToFirst()) {
            do {
                int transSum = expenseCursor.getInt(expenseCursor.getColumnIndex(sumOfTransactionsColumn));
                String transCategory = expenseCursor.getString(expenseCursor.getColumnIndex(COLUMN_EXPENSE_CATEGORY_NAME));

                value = BigDecimal.valueOf(transSum).divide(BigDecimal.valueOf(100));
                expenseStatisticsForPieChart.put(transCategory, value);

            } while (expenseCursor.moveToNext());
        }

        profileStatisticsForPieChart.add(expenseStatisticsForPieChart);
        expenseCursor.close();

        return profileStatisticsForPieChart;
    }

    public List<Map> getProfilePeriodStatistics(String transStartDate, String transEndDate) throws ParseException {
        List<Map> profileStatistics = new ArrayList<>();
        Map<String, BigDecimal> bigIntegerStatistics = new HashMap<>();
        Map<String, String> stringStatistics = new HashMap<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Warsaw"));

        String startDate, endDate;
        startDate = endDate = "1900-01-01";

        int sumOfIncomes, sumOfExpenses, balance, numberOfIncomes, numberOfExpenses, maxIncome, maxExpense;
        sumOfIncomes = sumOfExpenses = numberOfIncomes = numberOfExpenses = maxIncome = maxExpense = 0;

        String getProfileStatisticsStatement = "SELECT * FROM " + TABLE_TRANSACTION + " WHERE " + COLUMN_TRANSACTION_PROFILE_ID +  " = " + chosenProfileID + " AND " + COLUMN_TRANSACTION_DATE + " BETWEEN '" + transStartDate + "' AND '" + transEndDate + "' ORDER BY " + COLUMN_TRANSACTION_DATE + " DESC, " + COLUMN_TRANSACTION_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getProfileStatisticsStatement, null);
        if (cursor.moveToFirst()) {
            do {
                String transType = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_TYPE));
                int transValue = cursor.getInt(cursor.getColumnIndex(COLUMN_TRANSACTION_VALUE));
                String transDate = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_DATE));

                if (dateFormat.parse(transDate).compareTo(dateFormat.parse(startDate)) <= 0 || dateFormat.parse(startDate).compareTo(dateFormat.parse("1900-01-01")) == 0) startDate = transDate;
                if (dateFormat.parse(transDate).compareTo(dateFormat.parse(endDate)) >= 0) endDate = transDate;

                if (transType.equals("income")) {
                    sumOfIncomes += transValue;
                    numberOfIncomes++;
                    if (transValue > maxIncome) maxIncome = transValue;
                } else {
                    sumOfExpenses += transValue;
                    numberOfExpenses++;
                    if (transValue > maxExpense) maxExpense = transValue;
                }

            } while (cursor.moveToNext());

            balance = sumOfIncomes - sumOfExpenses;

            int monthsBetween = getNumberOfMonthsBetweenDates(dateFormat.parse(endDate), dateFormat.parse(startDate));
            if (numberOfIncomes + numberOfExpenses != 0 && monthsBetween == 0) monthsBetween = 1;

            bigIntegerStatistics.put("balance", BigDecimal.valueOf(balance));
            bigIntegerStatistics.put("numberOfIncomes", BigDecimal.valueOf(numberOfIncomes));
            bigIntegerStatistics.put("numberOfExpenses", BigDecimal.valueOf(numberOfExpenses));
            bigIntegerStatistics.put("numberOfTransactions", BigDecimal.valueOf(numberOfIncomes + numberOfExpenses));
            bigIntegerStatistics.put("sumOfIncomes", BigDecimal.valueOf(sumOfIncomes));
            bigIntegerStatistics.put("sumOfExpenses", BigDecimal.valueOf(sumOfExpenses));
            bigIntegerStatistics.put("maxIncome", BigDecimal.valueOf(maxIncome));
            bigIntegerStatistics.put("maxExpense", BigDecimal.valueOf(maxExpense));
            bigIntegerStatistics.put("averageIncome", BigDecimal.valueOf((numberOfIncomes == 0)? 0 : sumOfIncomes / numberOfIncomes));
            bigIntegerStatistics.put("averageExpense", BigDecimal.valueOf((numberOfExpenses == 0)? 0 : sumOfExpenses / numberOfExpenses));
            bigIntegerStatistics.put("numberOfMonths", BigDecimal.valueOf(monthsBetween));
            bigIntegerStatistics.put("averageIncomePerMonth", BigDecimal.valueOf((monthsBetween == 0)? 0 : sumOfIncomes / monthsBetween));
            bigIntegerStatistics.put("averageExpensePerMonth", BigDecimal.valueOf((monthsBetween == 0)? 0 : sumOfExpenses / monthsBetween));
            bigIntegerStatistics.put("averageCost", BigDecimal.valueOf(sumOfExpenses / getPeopleInHousehold(chosenProfileID)));

            stringStatistics.put("startDate", startDate);
            stringStatistics.put("endDate", endDate);
        }

        profileStatistics.add(bigIntegerStatistics);
        profileStatistics.add(stringStatistics);

        cursor.close();
        return profileStatistics;
    }

    public List<Map> getProfilePeriodStatisticsForPieChart(String transStartDate, String transEndDate) {
        List<Map> profileStatisticsForPieChart = new ArrayList<>();
        Map<String, BigDecimal> incomeStatisticsForPieChart = new HashMap<>();
        Map<String, BigDecimal> expenseStatisticsForPieChart = new HashMap<>();
        BigDecimal value = BigDecimal.valueOf(0);
        String sumOfTransactionsColumn = "transSum";

        String getIncomeStatisticsStatement = "SELECT SUM(" + COLUMN_TRANSACTION_VALUE + ") AS " + sumOfTransactionsColumn + ", " + COLUMN_INCOME_CATEGORY_NAME + " FROM " + TABLE_TRANSACTION + " A INNER JOIN " + TABLE_INCOME_CATEGORY + " B ON "
                + COLUMN_TRANSACTION_CATEGORY_ID + " = " + COLUMN_INCOME_CATEGORY_ID + " WHERE " + COLUMN_TRANSACTION_PROFILE_ID +  " = " + chosenProfileID + " AND " + COLUMN_TRANSACTION_DATE + " BETWEEN '" + transStartDate + "' AND '" + transEndDate + "' AND " + COLUMN_TRANSACTION_TYPE + " = ? GROUP BY 2";
        String getExpenseStatisticsStatement = "SELECT SUM(" + COLUMN_TRANSACTION_VALUE + ") AS " + sumOfTransactionsColumn + ", " + COLUMN_EXPENSE_CATEGORY_NAME + " FROM " + TABLE_TRANSACTION + " A INNER JOIN " + TABLE_EXPENSE_CATEGORY + " B ON "
                + COLUMN_TRANSACTION_CATEGORY_ID + " = " + COLUMN_EXPENSE_CATEGORY_ID + " WHERE " + COLUMN_TRANSACTION_PROFILE_ID +  " = " + chosenProfileID + " AND " + COLUMN_TRANSACTION_DATE + " BETWEEN '" + transStartDate + "' AND '" + transEndDate + "' AND " + COLUMN_TRANSACTION_TYPE + " = ? GROUP BY 2";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor incomeCursor = db.rawQuery(getIncomeStatisticsStatement, new String[] {"income"});
        if (incomeCursor.moveToFirst()) {
            do {
                int transSum = incomeCursor.getInt(incomeCursor.getColumnIndex(sumOfTransactionsColumn));
                String transCategory = incomeCursor.getString(incomeCursor.getColumnIndex(COLUMN_INCOME_CATEGORY_NAME));

                value = BigDecimal.valueOf(transSum).divide(BigDecimal.valueOf(100));
                incomeStatisticsForPieChart.put(transCategory, value);

            } while (incomeCursor.moveToNext());
        }

        profileStatisticsForPieChart.add(incomeStatisticsForPieChart);
        incomeCursor.close();

        Cursor expenseCursor = db.rawQuery(getExpenseStatisticsStatement, new String[] {"expense"});
        if (expenseCursor.moveToFirst()) {
            do {
                int transSum = expenseCursor.getInt(expenseCursor.getColumnIndex(sumOfTransactionsColumn));
                String transCategory = expenseCursor.getString(expenseCursor.getColumnIndex(COLUMN_EXPENSE_CATEGORY_NAME));

                value = BigDecimal.valueOf(transSum).divide(BigDecimal.valueOf(100));
                expenseStatisticsForPieChart.put(transCategory, value);

            } while (expenseCursor.moveToNext());
        }

        profileStatisticsForPieChart.add(expenseStatisticsForPieChart);
        expenseCursor.close();

        return profileStatisticsForPieChart;
    }

    public boolean doesUserHaveAnyTransaction(int profID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String doesUserHaveAnyTransactionNameStatement = "SELECT * FROM " + TABLE_TRANSACTION + " WHERE " + COLUMN_TRANSACTION_PROFILE_ID + " = ?";

        Cursor cursor = db.rawQuery(doesUserHaveAnyTransactionNameStatement, new String[] {String.valueOf(profID)});
        cursor.moveToFirst();

        int transactionCounter = cursor.getCount();
        boolean doesUserHaveAnyTransaction = (transactionCounter > 0) ? TRUE : FALSE;

        cursor.close();
        return doesUserHaveAnyTransaction;
    }

    public boolean doesUserHaveAnyTransactionInBudget(int profID, int budID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String doesUserHaveAnyTransactionNameStatement = "SELECT * FROM " + TABLE_TRANSACTION + " WHERE " + COLUMN_TRANSACTION_PROFILE_ID + " = ? AND " + COLUMN_TRANSACTION_BUDGET_ID + " = ?";

        Cursor cursor = db.rawQuery(doesUserHaveAnyTransactionNameStatement, new String[] {String.valueOf(profID), String.valueOf(budID)});
        cursor.moveToFirst();

        int transactionCounter = cursor.getCount();
        boolean doesUserHaveAnyTransaction = (transactionCounter > 0) ? TRUE : FALSE;

        cursor.close();
        return doesUserHaveAnyTransaction;
    }

    public boolean doesUserHaveAnyTransactionInMonth(int profID, String month) {
        SQLiteDatabase db = this.getReadableDatabase();
        String doesUserHaveAnyTransactionNameStatement = "SELECT * FROM " + TABLE_TRANSACTION + " WHERE " + COLUMN_TRANSACTION_PROFILE_ID + " = ? AND " + COLUMN_TRANSACTION_DATE + " BETWEEN '" + month + "-01' AND '" + month + "-31'";

        Cursor cursor = db.rawQuery(doesUserHaveAnyTransactionNameStatement, new String[] {String.valueOf(profID)});
        cursor.moveToFirst();

        int transactionCounter = cursor.getCount();
        boolean doesUserHaveAnyTransaction = (transactionCounter > 0) ? TRUE : FALSE;

        cursor.close();
        return doesUserHaveAnyTransaction;
    }

    public boolean doesUserHaveAnyTransactionInPeriod(int profID, String startDate, String endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        String doesUserHaveAnyTransactionNameStatement = "SELECT * FROM " + TABLE_TRANSACTION + " WHERE " + COLUMN_TRANSACTION_PROFILE_ID + " = ? AND " + COLUMN_TRANSACTION_DATE + " BETWEEN '" + startDate + "' AND '" + endDate + "'";

        Cursor cursor = db.rawQuery(doesUserHaveAnyTransactionNameStatement, new String[] {String.valueOf(profID)});
        cursor.moveToFirst();

        int transactionCounter = cursor.getCount();
        boolean doesUserHaveAnyTransaction = (transactionCounter > 0) ? TRUE : FALSE;

        cursor.close();
        return doesUserHaveAnyTransaction;
    }

    public void addShopping(ShoppingModel shoppingModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SHOPPING_PROFILE_ID, chosenProfileID);
        cv.put(COLUMN_SHOPPING_NAME, shoppingModel.getShoName());
        cv.put(COLUMN_SHOPPING_DATE, shoppingModel.getShoDate());
        cv.put(COLUMN_SHOPPING_DESCRIPTION, shoppingModel.getShoDesc());
        cv.put(COLUMN_SHOPPING_VALUE, 0);
        db.insert(TABLE_SHOPPING, null, cv);
    }

    public List<ShoppingModel> getShoppingList() {
        List<ShoppingModel> shoppingList = new ArrayList<ShoppingModel>();
        String getShoppingListStatement = "SELECT * FROM " + TABLE_SHOPPING + " WHERE " + COLUMN_SHOPPING_PROFILE_ID +  " = " + chosenProfileID + " ORDER BY " + COLUMN_SHOPPING_DATE + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getShoppingListStatement, null);
        if (cursor.moveToFirst()) {
            do {
                int shoID = cursor.getInt(cursor.getColumnIndex("shoID"));
                int shoProfID = cursor.getInt(cursor.getColumnIndex("shoProfID"));
                int shoRecID = cursor.getInt(cursor.getColumnIndex("shoRecID"));
                int shoExpID = cursor.getInt(cursor.getColumnIndex("shoExpID"));
                String shoName = cursor.getString(cursor.getColumnIndex("shoName"));
                String shoDate = cursor.getString(cursor.getColumnIndex("shoDate"));
                String shoDesc = cursor.getString(cursor.getColumnIndex("shoDesc"));
                int shoValue = cursor.getInt(cursor.getColumnIndex("shoValue"));

                ShoppingModel shoppingModel = new ShoppingModel(shoID, shoProfID, shoRecID, shoExpID, shoName, shoDate, shoDesc, shoValue);

                shoppingList.add(shoppingModel);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return shoppingList;
    }

    public ShoppingModel getShopping(int shoID) {
        ShoppingModel shoppingModel = new ShoppingModel();
        String getShoppingStatement = "SELECT * FROM " + TABLE_SHOPPING + " WHERE " + COLUMN_SHOPPING_ID +  " = ?";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getShoppingStatement, new String[] {Integer.toString(shoID)});
        if (cursor.moveToFirst()) {
            do {
                int shoProfID = cursor.getInt(cursor.getColumnIndex("shoProfID"));
                int shoRecID = cursor.getInt(cursor.getColumnIndex("shoRecID"));
                int shoExpID = cursor.getInt(cursor.getColumnIndex("shoExpID"));
                String shoName = cursor.getString(cursor.getColumnIndex("shoName"));
                String shoDate = cursor.getString(cursor.getColumnIndex("shoDate"));
                String shoDesc = cursor.getString(cursor.getColumnIndex("shoDesc"));
                int shoValue = cursor.getInt(cursor.getColumnIndex("shoValue"));

                shoppingModel = new ShoppingModel(shoID, shoProfID, shoRecID, shoExpID, shoName, shoDate, shoDesc, shoValue);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return shoppingModel;
    }

    public void updateShopping(int shoID, ShoppingModel shoppingModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SHOPPING_RECEIPT_ID, shoppingModel.getShoRecID());
        cv.put(COLUMN_SHOPPING_EXPENSE_ID, shoppingModel.getShoExpID());
        cv.put(COLUMN_SHOPPING_NAME, shoppingModel.getShoName());
        cv.put(COLUMN_SHOPPING_DATE, shoppingModel.getShoDate());
        cv.put(COLUMN_SHOPPING_DESCRIPTION, shoppingModel.getShoDesc());
        cv.put(COLUMN_SHOPPING_VALUE, getValueOfProductsInShoppingList(shoID));
        db.update(TABLE_SHOPPING, cv, COLUMN_SHOPPING_ID + " = " + shoID, null);
    }

    public void updateShoppingContentValue(int shoID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SHOPPING_VALUE, getValueOfProductsInShoppingList(shoID));
        db.update(TABLE_SHOPPING, cv, COLUMN_SHOPPING_ID + " = " + shoID, null);
    }

    public void deleteShopping(int shoID) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_SHOPPING_CONTENT, COLUMN_SHOPPING_CONTENT_SHOPPING_ID + " = " + shoID, null);
        db.delete(TABLE_SHOPPING, COLUMN_SHOPPING_ID + " = " + shoID, null);
    }

    public int getNumberOfProductsInShoppingList(int shoID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String getNumberOfProductsInShoppingListStatement = "SELECT * FROM " + TABLE_SHOPPING_CONTENT + " WHERE " + COLUMN_SHOPPING_CONTENT_SHOPPING_ID + " = ?";

        Cursor cursor = db.rawQuery(getNumberOfProductsInShoppingListStatement, new String[] {String.valueOf(shoID)});
        cursor.moveToFirst();

        int productsCounter = cursor.getCount();

        cursor.close();
        return productsCounter;
    }

    public int getValueOfProductsInShoppingList(int shoID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String getValueOfProductsInShoppingListStatement = "SELECT SUM(" + COLUMN_SHOPPING_CONTENT_VALUE + ") FROM " + TABLE_SHOPPING_CONTENT + " WHERE " + COLUMN_SHOPPING_CONTENT_SHOPPING_ID + " = ?";

        Cursor cursor = db.rawQuery(getValueOfProductsInShoppingListStatement, new String[] {String.valueOf(shoID)});
        cursor.moveToFirst();

        int valueCounter = cursor.getInt(0);

        cursor.close();
        return valueCounter;
    }

    public void addShoppingContent(int shoID, ShoppingContentModel shoppingContentModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SHOPPING_CONTENT_SHOPPING_ID, shoID);
        cv.put(COLUMN_SHOPPING_CONTENT_NAME, shoppingContentModel.getShoContName());
        cv.put(COLUMN_SHOPPING_CONTENT_AMOUNT, shoppingContentModel.getShoContAmount());
        cv.put(COLUMN_SHOPPING_CONTENT_UNIT, shoppingContentModel.getShoContUnit());
        cv.put(COLUMN_SHOPPING_CONTENT_VALUE, shoppingContentModel.getShoContValue());
        cv.put(COLUMN_SHOPPING_CONTENT_STATUS, false);
        db.insert(TABLE_SHOPPING_CONTENT, null, cv);

        updateShoppingContentValue(shoID);
    }

    public List<ShoppingContentModel> getShoppingContentList() {
        List<ShoppingContentModel> shoppingContentList = new ArrayList<ShoppingContentModel>();
        String getShoppingContentListStatement = "SELECT * FROM " + TABLE_SHOPPING_CONTENT + " WHERE " + COLUMN_SHOPPING_PROFILE_ID +  " = " + chosenProfileID;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getShoppingContentListStatement, null);
        if (cursor.moveToFirst()) {
            do {
                int shoContID = cursor.getInt(cursor.getColumnIndex("shoContID"));
                int shoContShoID = cursor.getInt(cursor.getColumnIndex("shoContShoID"));
                String shoContName = cursor.getString(cursor.getColumnIndex("shoContName"));
                String shoContAmount = cursor.getString(cursor.getColumnIndex("shoContAmount"));
                String shoContUnit = cursor.getString(cursor.getColumnIndex("shoContUnit"));
                int shoContValue = cursor.getInt(cursor.getColumnIndex("shoContValue"));
                boolean shoContStatus = cursor.getInt(cursor.getColumnIndex("shoContStatus")) == 1;

                ShoppingContentModel shoppingContentModel = new ShoppingContentModel(shoContID, shoContShoID, shoContName, shoContAmount, shoContUnit, shoContValue, shoContStatus);

                shoppingContentList.add(shoppingContentModel);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return shoppingContentList;
    }

    public List<ShoppingContentModel> getShoppingContentForShoppingList(int shoID) {
        List<ShoppingContentModel> shoppingContentList = new ArrayList<ShoppingContentModel>();
        String getShoppingContentStatement = "SELECT * FROM " + TABLE_SHOPPING_CONTENT + " WHERE " + COLUMN_SHOPPING_CONTENT_SHOPPING_ID +  " = ?";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getShoppingContentStatement, new String[] {Integer.toString(shoID)});
        if (cursor.moveToFirst()) {
            do {
                int shoContID = cursor.getInt(cursor.getColumnIndex("shoContID"));
                int shoContShoID = cursor.getInt(cursor.getColumnIndex("shoContShoID"));
                String shoContName = cursor.getString(cursor.getColumnIndex("shoContName"));
                String shoContAmount = cursor.getString(cursor.getColumnIndex("shoContAmount"));
                String shoContUnit = cursor.getString(cursor.getColumnIndex("shoContUnit"));
                int shoContValue = cursor.getInt(cursor.getColumnIndex("shoContValue"));
                boolean shoContStatus = cursor.getInt(cursor.getColumnIndex("shoContStatus")) == 1;

                ShoppingContentModel shoppingContentModel = new ShoppingContentModel(shoContID, shoContShoID, shoContName, shoContAmount, shoContUnit, shoContValue, shoContStatus);

                shoppingContentList.add(shoppingContentModel);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return shoppingContentList;
    }

    public ShoppingContentModel getShoppingContent(int shoContID) {
        ShoppingContentModel shoppingContentModel = new ShoppingContentModel();
        String getShoppingContentStatement = "SELECT * FROM " + TABLE_SHOPPING_CONTENT + " WHERE " + COLUMN_SHOPPING_CONTENT_ID +  " = ?";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(getShoppingContentStatement, new String[] {Integer.toString(shoContID)});
        if (cursor.moveToFirst()) {
            do {
                int shoContShoID = cursor.getInt(cursor.getColumnIndex("shoContShoID"));
                String shoContName = cursor.getString(cursor.getColumnIndex("shoContName"));
                String shoContAmount = cursor.getString(cursor.getColumnIndex("shoContAmount"));
                String shoContUnit = cursor.getString(cursor.getColumnIndex("shoContUnit"));
                int shoContValue = cursor.getInt(cursor.getColumnIndex("shoContValue"));
                boolean shoContStatus = cursor.getInt(cursor.getColumnIndex("shoContStatus")) == 1;

                shoppingContentModel = new ShoppingContentModel(shoContID, shoContShoID, shoContName, shoContAmount, shoContUnit, shoContValue, shoContStatus);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return shoppingContentModel;
    }

    public int getShoppingIDForShoppingContent(int shoContID) {
        int shoID = 0;
        String getShoppingIDStatement = "SELECT * FROM " + TABLE_SHOPPING_CONTENT + " WHERE " + COLUMN_SHOPPING_CONTENT_ID +  " = ?";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(getShoppingIDStatement, new String[] {Integer.toString(shoContID)});
        if (cursor.moveToFirst()) {
            do {
                shoID = cursor.getInt(cursor.getColumnIndex("shoContShoID"));

            } while (cursor.moveToNext());
        }

        cursor.close();
        return shoID;
    }

    public void updateShoppingContentStatus(int shoContID, boolean status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SHOPPING_CONTENT_STATUS, status);
        db.update(TABLE_SHOPPING_CONTENT, cv, COLUMN_SHOPPING_CONTENT_ID + " = " + shoContID, null);
    }

    public void updateShoppingContent(int shoContID, ShoppingContentModel shoppingContentModel) {
        int shoID = 0;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SHOPPING_CONTENT_NAME, shoppingContentModel.getShoContName());
        cv.put(COLUMN_SHOPPING_CONTENT_AMOUNT, shoppingContentModel.getShoContAmount());
        cv.put(COLUMN_SHOPPING_CONTENT_UNIT, shoppingContentModel.getShoContUnit());
        cv.put(COLUMN_SHOPPING_CONTENT_VALUE, shoppingContentModel.getShoContValue());
        cv.put(COLUMN_SHOPPING_CONTENT_STATUS, shoppingContentModel.isShoContChecked());
        db.update(TABLE_SHOPPING_CONTENT, cv, COLUMN_SHOPPING_CONTENT_ID + " = " + shoContID, null);

        shoID = getShoppingIDForShoppingContent(shoContID);
        updateShoppingContentValue(shoID);
    }

    public void deleteShoppingContent(int shoContID) {
        int shoID = 0;
        SQLiteDatabase db = this.getWritableDatabase();

        // need to find shoID before deleting shoCont
        shoID = getShoppingIDForShoppingContent(shoContID);

        db.delete(TABLE_SHOPPING_CONTENT, COLUMN_SHOPPING_CONTENT_ID + " = " + shoContID, null);

        updateShoppingContentValue(shoID);
    }

    public boolean checkExistingProductNameInShoppingList(int shoID, int shoContID, String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String checkExistingProductNameInShoppingListStatement = "SELECT * FROM " + TABLE_SHOPPING_CONTENT + " WHERE " + COLUMN_SHOPPING_CONTENT_SHOPPING_ID + " = ? AND "
                + COLUMN_SHOPPING_CONTENT_NAME + " = ? AND " + COLUMN_SHOPPING_CONTENT_ID + " <> ?";

        Cursor cursor = db.rawQuery(checkExistingProductNameInShoppingListStatement, new String[] {String.valueOf(shoID), name, String.valueOf(shoContID)});
        cursor.moveToFirst();

        int productsCounter = cursor.getCount();
        boolean doesNameExistInDatabase = (productsCounter > 0) ? TRUE : FALSE;

        cursor.close();
        return doesNameExistInDatabase;
    }

}
