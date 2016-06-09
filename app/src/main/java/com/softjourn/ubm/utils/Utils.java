package com.softjourn.ubm.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.inputmethod.InputMethodManager;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.softjourn.ubm.R;

/**
 * Created by Inet on 23.02.2016.
 */
public class Utils {

    static ImageLoader mImageLoader = ImageLoader.getInstance();
    static DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisc(true)
            .resetViewBeforeLoading(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .showImageForEmptyUri(R.drawable.default_pictures)
            .showImageOnLoading(R.drawable.default_pictures)
            .build();

    public static ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public static DisplayImageOptions getImageLoaderOptions() {
        return mOptions;
    }

    public static void hideKeyboard(Context context) {
        ((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    // Check if onLine
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}
