package com.softjourn.ubm.adapters;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.softjourn.ubm.database.DataBaseHelper;
import com.softjourn.ubm.fragments.NeedSlideFragment;

public class CursorPagerAdapter<F extends Fragment>  extends FragmentStatePagerAdapter {

	private final Class<NeedSlideFragment> fragmentClass;
    private Cursor cursor;
 
    public CursorPagerAdapter(FragmentManager fm, Class<NeedSlideFragment> myClass, Cursor cursor) {
        super(fm);
        this.fragmentClass = myClass;
        this.cursor = cursor;
    }
 
	@Override
    public F getItem(int position) {
        if (this.cursor == null)
            return null;
 
        cursor.moveToPosition(position);
        
		int needId = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.NEED_ID));
		int internatId = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.INTERNAT_ID));
		String name = cursor.getString(cursor.getColumnIndex(DataBaseHelper.NAME));
		long date = cursor.getLong(cursor.getColumnIndex(DataBaseHelper.DATE));
		String text = cursor.getString(cursor.getColumnIndex(DataBaseHelper.TEXT));
        String image = cursor.getString(cursor.getColumnIndex(DataBaseHelper.IMAGE));

        F frag;
        try {
            frag = (F) fragmentClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        
        Bundle args = new Bundle();

        args.putInt(DataBaseHelper.NEED_ID, needId);
        args.putInt(DataBaseHelper.INTERNAT_ID, internatId);
        args.putString(DataBaseHelper.NAME, name);
        args.putLong(DataBaseHelper.DATE, date);
        args.putString(DataBaseHelper.TEXT, text);
        args.putString(DataBaseHelper.IMAGE, image);

        frag.setArguments(args);
        return frag;
    }
 
    @Override
    public int getCount() {
        if (this.cursor == null)
            return 0;
        else
            return cursor.getCount();
    }
}
