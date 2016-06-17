package com.softjourn.ubm.activities;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.softjourn.ubm.R;
import com.softjourn.ubm.beans.Need;
import com.softjourn.ubm.database.DataSource;
import com.softjourn.ubm.database.DatabaseManager;

import java.util.List;

/**
 * Created by Inet on 15.03.2016.
 */
public class BaseActivity extends AppCompatActivity {

    protected DataSource mDataSource;
    protected Cursor mNeedsCursor;
    private List<String> mLastUrls;
    private ProgressDialog mProgressDialog;
    private DatabaseManager mDatabaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataSource = new DataSource(BaseActivity.this);
        mDatabaseManager = new DatabaseManager();
    }

    protected List<Need> getNeeds(List<String> lastUrls) {
        return mDatabaseManager.getAllNeedsList(lastUrls);
    }

    protected List<Need> getOneInternatNeeds(List<String> lastUrls, int internatId) {
        return mDatabaseManager.getOneInternatNeedsList(lastUrls, internatId);
    }

    public Cursor getAllNeeds(List<String> lastUrls) {
        mNeedsCursor = mDataSource.getAllNeeds(lastUrls);

        return mNeedsCursor;
    }

    public void clearDB() {
        mDataSource.removeAll();
    }

    public List<String> getLastUrls() {
        return mLastUrls;
    }

    public void setLastUrls(List<String> lastUrls) {
        mLastUrls = lastUrls;
    }

    public void clearLastUrls() {
        if (mLastUrls != null) {
            mLastUrls.clear();
        }
    }

    public void showProgessDialog() {
        mProgressDialog = new ProgressDialog(BaseActivity.this);
        mProgressDialog.setTitle(getString(R.string.app_name));
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    public void hideProgessDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDataSource.closeDB();
    }
}
