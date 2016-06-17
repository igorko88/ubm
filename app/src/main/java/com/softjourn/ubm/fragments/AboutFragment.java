package com.softjourn.ubm.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.softjourn.ubm.R;
import com.softjourn.ubm.beans.about.About;
import com.softjourn.ubm.beans.about.Phone;
import com.softjourn.ubm.database.DataSource;
import com.softjourn.ubm.utils.Navigation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Inet on 12.04.2016.
 */
public class AboutFragment extends Fragment implements OnMapReadyCallback {

    private DataSource mDataSource;
    private ListView mPhonesListView;
    private About mAboutInfo;
    private TextView mCity;
    private TextView mAddress;
    private TextView mPostalCode;
    private String[] mPhoneNumbers;
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;

    public static Fragment newInstance() {
        AboutFragment aboutFragment = new AboutFragment();
        aboutFragment.setHasOptionsMenu(true);
        return aboutFragment;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.removeItem(R.id.action_search);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup aboutView = (ViewGroup) inflater.inflate(R.layout.fragment_about, container, false);
        mCity = (TextView) aboutView.findViewById(R.id.city);
        mAddress = (TextView) aboutView.findViewById(R.id.address);
        mPostalCode = (TextView) aboutView.findViewById(R.id.postal_code);
        mPhonesListView = (ListView) aboutView.findViewById(R.id.phone_list);

        mDataSource = new DataSource(getActivity());
        mDataSource.openRead();
        mAboutInfo = mDataSource.getAboutInfo();

        if (mAboutInfo != null) {
            mCity.setText(mAboutInfo.getCity());
            mAddress.setText(mAboutInfo.getAddress());
            mPostalCode.setText(String.valueOf(mAboutInfo.getPostal_code()));

            ArrayList<String> phoneNumbersList = new ArrayList<String>();

            if (mAboutInfo.getPhones() != null) {
                List<Phone> phones = mAboutInfo.getPhones();

                if (phones != null) {
                    for (Phone phone : phones) {
                        phoneNumbersList.add(phone.getPhoneNumber());
                    }
                }
            }

            ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, phoneNumbersList);
            mPhonesListView.setAdapter(adapter);
            mPhoneNumbers = phoneNumbersList.toArray(new String[phoneNumbersList.size()]);
        }

        final ArrayAdapter<String> phonesAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_list_item_1, mPhoneNumbers) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                return view;
            }
        };

        mPhonesListView.setAdapter(phonesAdapter);
        mPhonesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                final String finalPhoneNumber = String.valueOf(adapter.getItemAtPosition(position));
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                Navigation.goToPhoneNumberActivity(getActivity(), finalPhoneNumber);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.are_you_sure).setPositiveButton(R.string.yes, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener).show();
            }
        });

        return aboutView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentManager fm = getChildFragmentManager();

        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, mMapFragment).commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMap == null) {
            mMapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null && mAboutInfo != null) {
            LatLng officeCoordinates = new LatLng(mAboutInfo.getLatitude(), mAboutInfo.getLongtitude());
            mMap.addMarker(new MarkerOptions()
                    .position(officeCoordinates)
                    .title(getString(R.string.our_address)));
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);

            MapsInitializer.initialize(this.getActivity());

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(officeCoordinates, 17.0f);
            mMap.animateCamera(cameraUpdate);
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng arg0) {
                    Navigation.goToMapActivity(getActivity(), mAboutInfo.getLatitude(), mAboutInfo.getLongtitude());
                }
            });
        }
    }
}
