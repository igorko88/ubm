package com.softjourn.ubm.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.softjourn.ubm.R;
import com.softjourn.ubm.database.DataBaseHelper;
import com.softjourn.ubm.utils.ExtraNames;
import com.softjourn.ubm.utils.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NeedSlideFragment extends Fragment implements ExtraNames {

	private String mName;
	private long mDate;
	private String mImageUrl;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_need, container, false);

        Bundle bundle = getArguments();
		mName = bundle.getString(DataBaseHelper.NAME);
		mDate = bundle.getLong(DataBaseHelper.DATE);
		mImageUrl = bundle.getString(DataBaseHelper.IMAGE);

		TextView name = (TextView) rootView.findViewById(R.id.need_name);
		name.setText(mName);

		TextView date = (TextView) rootView.findViewById(R.id.need_date);

		Calendar calendar = Calendar.getInstance();
		int mLocalTimeZoneDiff = calendar.getTimeZone().getRawOffset();
		String dateTxt = new SimpleDateFormat("dd.MM.yyyy").format(new Date(mDate + mLocalTimeZoneDiff));
		date.setText(dateTxt);

		if(mImageUrl != null && !mImageUrl.isEmpty()) {
			ImageView poster_image = (ImageView) rootView.findViewById(R.id.need_poster);
			Utils.getImageLoader().displayImage(Uri.decode(mImageUrl), poster_image, Utils.getImageLoaderOptions());
		}

		WebView details = (WebView) rootView.findViewById(R.id.need_details);
		details.loadDataWithBaseURL(null, bundle.getString(DataBaseHelper.TEXT), "text/html", "UTF-8", null);
		
        return rootView;
    }
}