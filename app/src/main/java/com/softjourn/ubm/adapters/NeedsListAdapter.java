package com.softjourn.ubm.adapters;

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

    private List<Need> mNeedList;
    private Context mContext;

    public NeedsListAdapter(Context context, List<Need> needList) {
        mContext = context;
        mNeedList = needList;
    }

    @Override
    public int getCount() {
        return mNeedList.size();
    }

    @Override
    public Need getItem(int position) {
        return mNeedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mNeedList.get(position).getId();
    }

    public void setData(List<Need> needs){
        mNeedList = needs;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView needName;
        ImageView needPoster;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.need_list_item, viewGroup, false);
            viewHolder = new ViewHolder();

            viewHolder.needName = (TextView) convertView.findViewById(R.id.need_name);
            viewHolder.needPoster = (ImageView) convertView.findViewById(R.id.need_poster);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.needName.setText(getItem(position).getText());
        Utils.getImageLoader().displayImage(Uri.decode(getItem(position).getImageUrl()), viewHolder.needPoster, Utils.getImageLoaderOptions());

        return convertView;
    }
}
