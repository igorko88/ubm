package com.softjourn.ubm.fragments;

/**
 * Created by Inet on 22.03.2016.
 */

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.softjourn.ubm.R;

public class MainFragment extends Fragment {
    public static final String ARG_NEED_NUMBER = "need_number";

    public MainFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_needs, container, false);
        return rootView;
    }
}
