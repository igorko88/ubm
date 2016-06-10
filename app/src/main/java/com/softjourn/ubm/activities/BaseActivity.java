package com.softjourn.ubm.activities;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.softjourn.ubm.R;
import com.softjourn.ubm.database.DataSource;

import java.util.ArrayList;

/**
 * Created by Inet on 15.03.2016.
 */
public class BaseActivity extends AppCompatActivity {

    protected DataSource mDataSource;
    protected Cursor mNeedsCursor;
    private ArrayList<String> mLastUrls;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataSource = new DataSource(getBaseContext());
    }

    public Cursor getFoundNeedsCursor(ArrayList<String> lastUrls){
        mNeedsCursor = mDataSource.getFoundNeewsByUrl(lastUrls);

        return mNeedsCursor;
    }

    public Cursor getAllNeedsCursor() {
        mNeedsCursor = mDataSource.getNeedUI();
        return mNeedsCursor;
    }

    public Cursor getAllNeedsCursor(ArrayList<String> lastUrls) {
        mNeedsCursor = mDataSource.getNeedUI(lastUrls);

        return mNeedsCursor;
    }

    public Cursor getCursorByUrlInternatId(ArrayList<String> lastUrls, int internatId){
        mNeedsCursor = mDataSource.getNeedsUIByUrlInternatId(lastUrls, internatId);

        return  mNeedsCursor;
    }

    public Cursor getCursorByInternatId(int internatId){
        mNeedsCursor = mDataSource.getNeedsByInternatId(internatId);

        return  mNeedsCursor;
    }

    public void clearDB(){
        mDataSource.removeAll();
    }

    public ArrayList<String> getLastUrls() {
        return mLastUrls;
    }

    public void setLastUrls(ArrayList<String> lastUrls) {
        mLastUrls = lastUrls;
    }

    public void clearLastUrls(){
        if(mLastUrls != null) {
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
