package com.softjourn.ubm.adapters;

/**
 * Created by Inet on 10.02.2016.
 */
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.softjourn.ubm.R;
import com.softjourn.ubm.database.DataBaseHelper;
import com.softjourn.ubm.utils.Utils;

public class NeedsListAdapter extends CursorAdapter {

    private final LayoutInflater mInflater;

    public NeedsListAdapter(Context context, Cursor mNeedsCursor) {
        super(context, mNeedsCursor);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.need_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        View needView = view;

        String needNameStr = cursor.getString(cursor.getColumnIndex(DataBaseHelper.NAME));
        String needImageStr = cursor.getString(cursor.getColumnIndex(DataBaseHelper.IMAGE));

        TextView needName = (TextView) needView.findViewById(R.id.need_name);
        needName.setText(needNameStr);

        ImageView needPoster = (ImageView) needView.findViewById(R.id.need_poster);
        Utils.getImageLoader().displayImage(Uri.decode(needImageStr), needPoster, Utils.getImageLoaderOptions());
    }
}
