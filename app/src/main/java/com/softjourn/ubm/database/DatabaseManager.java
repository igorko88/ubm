package com.softjourn.ubm.database;

import android.database.Cursor;
import com.softjourn.ubm.application.AppController;
import com.softjourn.ubm.beans.Need;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Inet on 16.06.2016.
 */
public class DatabaseManager {

    private final DataSource mDataSource;

    public DatabaseManager(){
        mDataSource = new DataSource(AppController.getInstance().getApplicationContext());
    }

    public List<Need> getAllNeedsList(List<String> lastUrls) {
        Cursor needsCursor = mDataSource.getNeedsByUrl(lastUrls);

        List<Need> needsList = new ArrayList();
        if (needsCursor.moveToFirst()) {
            do {
                Need need = mDataSource.cursorToNeed(needsCursor);
                needsList.add(need);
            } while (needsCursor.moveToNext());
        }

        return needsList;
    }

    public List<Need> getOneInternatNeedsList(List<String> lastUrls, int internatId) {
        Cursor needsCursor = mDataSource.getNeedsUIByUrlInternatId(lastUrls, internatId);

        List<Need> needsList = new ArrayList();
        if (needsCursor.moveToFirst()) {
            do {
                Need need = mDataSource.cursorToNeed(needsCursor);
                needsList.add(need);
            } while (needsCursor.moveToNext());
        }

        return needsList;
    }



}
