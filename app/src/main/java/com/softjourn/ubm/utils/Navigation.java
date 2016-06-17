package com.softjourn.ubm.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.softjourn.ubm.R;
import com.softjourn.ubm.activities.MainActivity;
import com.softjourn.ubm.activities.NeedActivity;
import com.softjourn.ubm.fragments.AboutFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Inet on 12.02.2016.
 */
public class Navigation implements ExtraNames, Consts {

    public static void goToNeedActivity(Activity activity, int position, List<String> lastUrls) {
        Intent intent = new Intent(activity, NeedActivity.class);
        intent.putExtra(LIST_POSITION_EXTRA, position - 1);
        intent.putExtra(LAST_URL_LIST_EXTRA, (ArrayList)lastUrls);
        activity.startActivityForResult(intent, RESULT_CODE);
    }

    public static void goToMapActivity(FragmentActivity activity, double latitude, double longitude) {
        String geoUri = activity.getString(R.string.geo)+ latitude + "," + longitude;
        Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(geoUri));
        activity.startActivity(i);

        String officeLabel = activity.getString(R.string.our_address);
        final Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("geo:0,0?q="+latitude+","+longitude+"&z=16 (" + officeLabel + ")"));
        activity.startActivity(intent);
    }

    public static void goToPhoneNumberActivity(FragmentActivity activity, String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        activity.startActivity(callIntent);
    }

    public static void gotoAboutScreen(MainActivity activity){
        Fragment aboutFragment = AboutFragment.newInstance();
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, aboutFragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
