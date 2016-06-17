package com.softjourn.ubm.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.softjourn.ubm.R;
import com.softjourn.ubm.beans.Need;
import com.softjourn.ubm.utils.Utils;

import java.util.List;

/**
 * Created by Inet on 16.06.2016.
 */


public class NeedsListAdapter extends BaseAdapter {
    private Activity mContext;
    private List<Need> mNeedList;

    public NeedsListAdapter(Activity context, List<Need> list) {
        mContext = context;
        mNeedList = list;
    }

    @Override
    public int getCount() {
        return mNeedList.size();
    }

    @Override
    public Object getItem(int pos) {
        return mNeedList.get(pos);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateList(List<Need> needs) {
        mNeedList = needs;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.need_list_item, null);
            viewHolder = new ViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }
        viewHolder.needName.setText(mNeedList.get(position).getName());
        Utils.getImageLoader().displayImage(Uri.decode(mNeedList.get(position).getImageUrl()), viewHolder.needPoster, Utils.getImageLoaderOptions());
        return v;
    }
}

class ViewHolder {
    TextView needName;
    ImageView needPoster;

    public ViewHolder(View base) {
        needName = (TextView) base.findViewById(R.id.need_name);
        needPoster = (ImageView) base.findViewById(R.id.need_poster);
    }
}