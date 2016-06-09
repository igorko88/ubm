package com.softjourn.ubm.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Inet on 25.02.2016.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private final String LOG_TAG = getClass().getName();

    public static final String DATABASE_NAME = "ubm.db";
    public static final int DATABASE_VERSION = 1;

    //TABLE NEEDS
    public static final String TABLE_NEEDS = "needs";

    public static final String NEED_ID = "need_id";
    public static final String INTERNAT_ID = "internat_id";
    public static final String NAME = "name";
    public static final String DATE = "date";
    public static final String TEXT = "text";
    public static final String IMAGE = "image";
    public static final String SPECIAL_PARAMETERS = "special_parameters";
    public static final String SPECIAL_NUMBER = "_id";

    //TABLE INTERNATS
    public static final String TABLE_INTERNATS = "internats";

    public static final String INTERNATS_ID = "internat_id";
    public static final String INTERNATS_NAME = "internat_name";

    //TABLE ABOUT
    public static final String TABLE_ABOUT = "about";

    public static final String ABOUT_ID = "id";
    public static final String CITY = "city";
    public static final String STREET = "street";
    public static final String LATITUDE = "latitude";
    public static final String LONGTITUDE = "longtitude";
    public static final String ADDRESS = "address";
    public static final String POSTAL_CODE = "postal_code";
    public static final String EMAIL = "email";

    //TABLE PHONES
    public static final String TABLE_PHONES = "phones";

    public static final String PHONES_ID = "id";
    public static final String PHONE_ID = "phone_id";
    public static final String PHONE_NUMBER = "phone_number";

    // Table NEEDS creation SQL statement
    static final String TABLE_CREATE_NEEDS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NEEDS + "( "
            + SPECIAL_NUMBER + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NEED_ID + " INT, "
            + INTERNAT_ID + " INT NOT NULL, "
            + NAME + " TEXT, "
            + DATE + " INT, "
            + TEXT + " TEXT, "
            + IMAGE + " TEXT, "
            + SPECIAL_PARAMETERS + " TEXT NOT NULL, "
            + " UNIQUE (" + NEED_ID + "," + SPECIAL_PARAMETERS + "));";

    // Table INTERNATS creation SQL statement
    static final String TABLE_CREATE_INTERNATS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_INTERNATS + "( " + INTERNATS_ID + " INTEGER PRIMARY KEY NOT NULL, "
            + INTERNATS_NAME	+ " TEXT "
            + ");";

    // Table ABOUT creation SQL statement
    static final String TABLE_CREATE_ABOUT = "CREATE TABLE IF NOT EXISTS "
            + TABLE_ABOUT + "( "
            + ABOUT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CITY	+ " TEXT, "
            + STREET	+ " TEXT, "
            + LATITUDE	+ " NUMERIC, "
            + LONGTITUDE	+ " NUMERIC, "
            + ADDRESS	+ " TEXT, "
            + POSTAL_CODE + " INT, "
            + PHONE_ID	+ " INT, "
            + EMAIL	+ " TEXT "
            + ");";

    // Table PHONES creation SQL statement
    static final String TABLE_CREATE_PHONES = "CREATE TABLE IF NOT EXISTS "
            + TABLE_PHONES + "( "
            + PHONES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PHONE_NUMBER	+ " TEXT UNIQUE, "
            + PHONE_ID + " INT"
            + ");";

    private static DataBaseHelper mInstance;

    public static synchronized DataBaseHelper getHelper(Context context) {
        if (mInstance == null)
            mInstance = new DataBaseHelper(context);
        return mInstance;
    }

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, " --- onCreate database START --- ");
        db.execSQL(TABLE_CREATE_INTERNATS);
        db.execSQL(TABLE_CREATE_NEEDS);
        db.execSQL(TABLE_CREATE_ABOUT);
        db.execSQL(TABLE_CREATE_PHONES);

        db.execSQL("INSERT INTO " + TABLE_INTERNATS + " VALUES(0,'Залучанський дитячий будинок-інтернат');");
        db.execSQL("INSERT INTO " + TABLE_INTERNATS + " VALUES(1,'Коломийський дитячий будинок-інтернат');");
        db.execSQL("INSERT INTO " + TABLE_INTERNATS + " VALUES(2,'Рівненський обласний спеціалізований будинок дитини');");
        db.execSQL("INSERT INTO " + TABLE_INTERNATS + " VALUES(3,'Роздільський дитячий будинок-інтернат');");
        db.execSQL("INSERT INTO " + TABLE_INTERNATS + " VALUES(4,'Снятинський дитячий будинок-інтернат');");
        db.execSQL("INSERT INTO " + TABLE_INTERNATS + " VALUES(5,'Голобський дитячий будинок-інтернат');");

        db.execSQL("INSERT INTO " + TABLE_ABOUT + " VALUES(1,'м. Івано-Франківськ', 'вул. Тичини, 8А, офіс 218 (2 поверх)', '48.925859', '24.705933', 'БО Українська благодійницька мережа А/с 76, поштове відділення № 18, м. Івано-Франківськ', '76018', '1', 'ubm@ubm.org.ua');");

        db.execSQL("INSERT INTO " + TABLE_PHONES + " VALUES(0,'(067) 340-53-63', 1);");
        db.execSQL("INSERT INTO " + TABLE_PHONES + " VALUES(1,'(050) 338-99-94', 1);");
        db.execSQL("INSERT INTO " + TABLE_PHONES + " VALUES(2,'(098) 510-59-97', 1);");
        db.execSQL("INSERT INTO " + TABLE_PHONES + " VALUES(3,'(050) 735-04-71', 1);");

        Log.d(LOG_TAG, " --- onCreate database END --- ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LOG_TAG,"Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEEDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INTERNATS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ABOUT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHONES);
        onCreate(db);
    }
}
