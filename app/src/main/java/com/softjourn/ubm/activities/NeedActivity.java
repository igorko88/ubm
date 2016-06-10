package com.softjourn.ubm.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import com.softjourn.ubm.R;
import com.softjourn.ubm.adapters.CursorPagerAdapter;
import com.softjourn.ubm.database.DataSource;
import com.softjourn.ubm.fragments.NeedSlideFragment;
import com.softjourn.ubm.utils.Consts;
import com.softjourn.ubm.utils.ExtraNames;

import java.util.ArrayList;


/**
 * Created by Inet on 12.02.2016.
 */
public class NeedActivity extends BaseActivity implements ExtraNames, Consts {

    private ViewPager mPager;
    private CursorPagerAdapter<Fragment> mPagerAdapter;
    private int mPosition;
    private DataSource mDataSource;
    private Cursor mNeedsCursor;
    private ArrayList<String> mLastUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_needs);

        Bundle bundle = getIntent().getExtras();

        int listPosition = 0;
        if(bundle != null)
        {
            listPosition = bundle.getInt(LIST_POSITION_EXTRA);
            mLastUrls = bundle.getStringArrayList(LAST_URL_LIST_EXTRA);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbar_view = inflator.inflate(R.layout.actionbar_custom, null);
        actionBar.setCustomView(actionbar_view);

        mDataSource = new DataSource(this);
        mDataSource.openRead();
        mNeedsCursor = getAllNeedsCursor(mLastUrls);

        mPagerAdapter = new CursorPagerAdapter(getSupportFragmentManager(), NeedSlideFragment.class, mNeedsCursor);
        mPager = (ViewPager) findViewById(R.id.needs_pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(listPosition);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                backToNeedsList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            backToNeedsList();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void backToNeedsList(){
        Intent intent = new Intent();
        intent.putExtra(LIST_POSITION_EXTRA, mPosition + 1);
        setResult(RESULT_CODE, intent);
        finish();
    }
}

