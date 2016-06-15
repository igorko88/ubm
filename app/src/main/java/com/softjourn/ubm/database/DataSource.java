package com.softjourn.ubm.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.softjourn.ubm.beans.Internat;
import com.softjourn.ubm.beans.Need;
import com.softjourn.ubm.beans.about.About;
import com.softjourn.ubm.beans.about.Phone;

import java.util.ArrayList;

public class DataSource {

    private final DataBaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private Context mContext;

    public DataSource(Context context) {
        mContext = context;
        mDbHelper = DataBaseHelper.getHelper(mContext);
    }

    public void openRead() throws SQLException {
        mDb = mDbHelper.getReadableDatabase();
    }

    public void openWrite() throws SQLException {
        mDb = mDbHelper.getWritableDatabase();
    }

    // closing database
    public void closeDB() {
        if (mDb != null && mDb.isOpen())
            mDb.close();
    }

    public void writeNeedsTodb(String updateUrl, ArrayList<Need> needs){
        if (needs != null) {
            //write data to db
            openRead();
            createNeedsList(needs, updateUrl);
            closeDB();
        }
    }

    public boolean createAbout(About about) {
        openWrite();
        mDb.beginTransaction();

        try {
            insertObjectAbout(about);
            mDb.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            mDb.endTransaction();
        }
        return true;
    }

    public boolean createInternatsList(ArrayList<Internat> internatsList) {
        openWrite();
        mDb.beginTransaction();

        try {
            for (Internat internat : internatsList) {
                insertObjectInernat(internat);
            }

            mDb.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            mDb.endTransaction();
        }
        return true;
    }

    public boolean createNeedsList(ArrayList<Need> needsList, String url) {
        openWrite();
        mDb.beginTransaction();

        try {
            for (Need need : needsList) {
                insertObjectNeed(need, url);
            }

            mDb.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            mDb.endTransaction();
        }
        return true;
    }

    private long insertObjectAbout(About about) {
        long insertId = (-2);

        ArrayList<Phone> phones = about.getPhones();
        if (phones != null) {
            insertId = insertOrUpdateTablePhones(phones);
        }

        insertId = insertOrUpdateTableAbout(about);

        return insertId;
    }

    private  long insertObjectInernat(Internat internat){
        long insertId = (-2);

        insertId = insertOrUpdateTableInternat(internat);

        return insertId;
    }

    private long insertObjectNeed(Need need, String url) {
        long insertId = (-2);

        insertId = insertOrUpdateTableNeeds(need, url);

        return insertId;
    }

    private long insertOrUpdateTableAbout(About about) {

        ContentValues contentValues = new ContentValues();

        //TABLE ABOUT
        contentValues.put(DataBaseHelper.ABOUT_ID, about.getId());
        contentValues.put(DataBaseHelper.CITY, about.getCity());
        contentValues.put(DataBaseHelper.STREET, about.getStreet());
        contentValues.put(DataBaseHelper.LATITUDE, about.getLatitude());
        contentValues.put(DataBaseHelper.LONGTITUDE, about.getLongtitude());
        contentValues.put(DataBaseHelper.ADDRESS, about.getAddress());
        contentValues.put(DataBaseHelper.POSTAL_CODE, about.getPostal_code());
        contentValues.put(DataBaseHelper.PHONE_ID, about.getPhoneId());
        contentValues.put(DataBaseHelper.EMAIL, about.getEmail());

        insertOrUpdateTablePhones(about.getPhones());

        long returnedId = mDb.insertWithOnConflict(DataBaseHelper.TABLE_ABOUT, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        if (returnedId == (-1)) {
            returnedId = (long) mDb.update(DataBaseHelper.TABLE_ABOUT, contentValues, DataBaseHelper.ABOUT_ID + "=?", new String[]{String.valueOf(about.getId())});
        }
        return returnedId;
    }

    private long insertOrUpdateTablePhones(ArrayList <Phone> phones) {
        long returnedId = 0;
        ContentValues contentValues = new ContentValues();

        for (Phone phone : phones) {

            contentValues.put(DataBaseHelper.PHONE_NUMBER, phone.getPhoneNumber());
            contentValues.put(DataBaseHelper.PHONE_ID, phone.getPhoneId());

            returnedId = mDb.insertWithOnConflict(DataBaseHelper.TABLE_PHONES, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
            if (returnedId == (-1)) {
                returnedId = (long) mDb.update(DataBaseHelper.TABLE_PHONES, contentValues, DataBaseHelper.PHONES_ID + "=?", new String[]{String.valueOf(phone.getId())});
            }
        }
        return returnedId;
    }

    public About getAboutInfo() {

        openRead();
        String selectQuery = "SELECT  * FROM " + DataBaseHelper.TABLE_ABOUT;
        Cursor cursor = mDb.rawQuery(selectQuery, null);
        About about = null;
        if (cursor.moveToFirst()) {
            about = cursorToAboutInfo(cursor);
            closeDB();
        }

        return about;
    }

    private long insertOrUpdateTableNeeds(Need need, String url) {

        ContentValues contentValues = new ContentValues();

        //TABLE NEEDS
        contentValues.put(DataBaseHelper.NEED_ID, need.getId());
        contentValues.put(DataBaseHelper.INTERNAT_ID, need.getInternat_id());
        contentValues.put(DataBaseHelper.DATE, need.getDate());
        contentValues.put(DataBaseHelper.NAME, need.getName());
        contentValues.put(DataBaseHelper.TEXT, need.getText());
        contentValues.put(DataBaseHelper.IMAGE, need.getImageUrl());
        contentValues.put(DataBaseHelper.SPECIAL_PARAMETERS, url);

        long returnedId = mDb.insertWithOnConflict(DataBaseHelper.TABLE_NEEDS, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        if (returnedId == (-1)) {
            returnedId = (long) mDb.update(DataBaseHelper.TABLE_NEEDS, contentValues,
                    DataBaseHelper.NEED_ID + "=? AND " + DataBaseHelper.SPECIAL_PARAMETERS + "=?", new String[]{String.valueOf(need.getId()), url});
        }
        return returnedId;
    }

    private long insertOrUpdateTableInternat(Internat internat) {

        ContentValues contentValues = new ContentValues();

        //TABLE INTERNATS
        contentValues.put(DataBaseHelper.INTERNATS_ID, internat.getInternat_id());
        contentValues.put(DataBaseHelper.INTERNATS_NAME, internat.getName());

        long returnedId = mDb.insertWithOnConflict(DataBaseHelper.TABLE_INTERNATS, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        if (returnedId == (-1)) {
            returnedId = (long) mDb.update(DataBaseHelper.TABLE_INTERNATS, contentValues, DataBaseHelper.INTERNATS_ID + "=?", new String[]{String.valueOf(internat.getInternat_id())});
        }
        return returnedId;
    }

    private About cursorToAboutInfo(Cursor cursor) {
        About about = new About();

        if (cursor != null && cursor.getCount() != 0) {
            about.setId(cursor.getInt(cursor.getColumnIndex(DataBaseHelper.ABOUT_ID)));
            about.setCity(cursor.getString(cursor.getColumnIndex(DataBaseHelper.CITY)));
            about.setStreet(cursor.getString(cursor.getColumnIndex(DataBaseHelper.STREET)));
            about.setLatitude(cursor.getDouble(cursor.getColumnIndex(DataBaseHelper.LATITUDE)));
            about.setLongtitude(cursor.getDouble(cursor.getColumnIndex(DataBaseHelper.LONGTITUDE)));
            about.setAddress(cursor.getString(cursor.getColumnIndex(DataBaseHelper.ADDRESS)));
            about.setPostal_code(cursor.getInt(cursor.getColumnIndex(DataBaseHelper.POSTAL_CODE)));
            about.setPhoneId(cursor.getInt(cursor.getColumnIndex(DataBaseHelper.PHONE_ID)));
            about.setEmail(cursor.getString(cursor.getColumnIndex(DataBaseHelper.EMAIL)));
            about.setPhones(getPhoneList());
        }
        return about;
    }

    private ArrayList<Phone> getPhoneList(){
        String selectQuery = "SELECT * FROM " + DataBaseHelper.TABLE_PHONES;
        Cursor phoneCursor = mDb.rawQuery(selectQuery, null);
        ArrayList<Phone> phonesList = new ArrayList<Phone>();
        if (phoneCursor.moveToFirst()) {
            do {
                Phone phone = cursorToPhone(phoneCursor);
                phonesList.add(phone);
            } while (phoneCursor.moveToNext());
        }

        return phonesList;
    }

    private Phone cursorToPhone(Cursor cursor) {
        if (cursor != null && cursor.getCount() != 0) {

            Phone phone = new Phone();
            phone.setId(cursor.getInt(cursor.getColumnIndex(DataBaseHelper.PHONES_ID)));
            phone.setPhoneId(cursor.getInt(cursor.getColumnIndex(DataBaseHelper.PHONE_ID)));
            phone.setPhoneNumber(cursor.getString(cursor.getColumnIndex(DataBaseHelper.PHONE_NUMBER)));
            return phone;

        } else return null;
    }

    private Internat cursorToInternat(Cursor cursor) {

        if (cursor != null && cursor.getCount() != 0) {

            Internat internat = new Internat();
            internat.setInternat_id(cursor.getInt(cursor.getColumnIndex(DataBaseHelper.INTERNATS_ID)));
            internat.setName(cursor.getString(cursor.getColumnIndex(DataBaseHelper.INTERNATS_NAME)));
            return internat;

        } else return null;
    }

    private Need cursorToNeed(Cursor cursor) {

        if (cursor != null && cursor.getCount() != 0) {
            Need need = new Need();
            need.setId(cursor.getInt(cursor.getColumnIndex(DataBaseHelper.NEED_ID)));
            need.setInternat_id(cursor.getInt(cursor.getColumnIndex(DataBaseHelper.INTERNAT_ID)));
            need.setName(cursor.getString(cursor.getColumnIndex(DataBaseHelper.NAME)));
            need.setDate(cursor.getLong(cursor.getColumnIndex(DataBaseHelper.DATE)));
            need.setText(cursor.getString(cursor.getColumnIndex(DataBaseHelper.TEXT)));
            need.setImage(cursor.getString(cursor.getColumnIndex(DataBaseHelper.IMAGE)));
            return need;
        } else return null;
    }

    public ArrayList<Internat> getAllInternats() {

        ArrayList<Internat> internatList = new ArrayList<Internat>();
        String selectQuery = "SELECT  * FROM " + DataBaseHelper.TABLE_INTERNATS;

        openRead();
        Cursor cursor = mDb.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Internat internat = cursorToInternat(cursor);
                internatList.add(internat);
            } while (cursor.moveToNext());
        }

        return internatList;
    }

    public Cursor getFoundNeewsByUrl(ArrayList<String> urls) {
        openRead();
        String query = "SELECT * FROM needs";

        String subQuery = "";

        for (int i = 0; i < urls.size(); i++) {
            if (i == 0) {
                subQuery += " WHERE needs.special_parameters='" + urls.get(i) + "'";
            } else {
                subQuery += " OR needs.special_parameters='" + urls.get(i) + "'";
            }
        }

        String cursorQuery = query + subQuery + ";";
        Cursor cursor = mDb.rawQuery(cursorQuery, null);
        return cursor;
    }

    public Cursor getNeedsByInternatId(long internat_id) {
        openRead();
        Cursor cursor;

        String query = "SELECT * FROM needs";
        String whereQuery = " WHERE needs.internat_id='" + internat_id + "'";

        String cursorQuery = query + whereQuery + ";";
        cursor = mDb.rawQuery(cursorQuery, null);
        return cursor;
    }

    public Cursor getNeedsUIByUrlInternatId(ArrayList<String> lastUrls, long internat_id) {
        ArrayList<Need> needsList = new ArrayList<Need>();

        openRead();
        Cursor cursor;

        if (internat_id == 0) {
            String query = "SELECT * FROM needs";
            String subQuery = "";

            for (int i = 0; i < lastUrls.size(); i++) {
                if (i == 0) {
                    subQuery += " WHERE needs.special_parameters='" + lastUrls.get(i) + "'";
                } else {
                    subQuery += " OR needs.special_parameters='" + lastUrls.get(i) + "'";
                }
            }

            String cursorQuery = query + subQuery + ";";
            cursor = mDb.rawQuery(cursorQuery, null);
        } else {
            String query = "SELECT * FROM needs";
            String whereQuery = " WHERE needs.internat_id='" + internat_id + "'";

            String cursorQuery = query + whereQuery + ";";
            cursor = mDb.rawQuery(cursorQuery, null);
        }

        cursor.moveToFirst();

        Need need = null;
        while (!cursor.isAfterLast()) {
            need = cursorToNeed(cursor);
            needsList.add(need);
            cursor.moveToNext();
        }
        return cursor;
    }

    public Cursor getNeedUI() {
        openRead();

        String selectQuery = "SELECT  * FROM " + DataBaseHelper.TABLE_NEEDS;
        Cursor cursor = mDb.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getNeedUI(ArrayList<String> urls) {
        openRead();

        String query = "SELECT * FROM needs";

        String subQuery = "";

        for (int i = 0; i < urls.size(); i++) {
            if (i == 0) {
                subQuery += " WHERE needs.special_parameters='" + urls.get(i) + "'";
            } else {
                subQuery += " OR needs.special_parameters='" + urls.get(i) + "'";
            }
        }

        String cursorQuery = query + subQuery + ";";
        Cursor cursor = mDb.rawQuery(cursorQuery, null);

        return cursor;
    }

    public void removeAll() {
        openRead();
        mDb.delete(DataBaseHelper.TABLE_NEEDS, null, null);
        mDb.delete(DataBaseHelper.TABLE_INTERNATS, null, null);
    }
}