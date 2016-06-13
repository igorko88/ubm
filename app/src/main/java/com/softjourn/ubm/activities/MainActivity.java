package com.softjourn.ubm.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.softjourn.ubm.R;
import com.softjourn.ubm.adapters.NeedsListAdapter;
import com.softjourn.ubm.application.AppController;
import com.softjourn.ubm.beans.Internat;
import com.softjourn.ubm.database.DataSource;
import com.softjourn.ubm.fragments.AboutFragment;
import com.softjourn.ubm.fragments.MainFragment;
import com.softjourn.ubm.requests.JsonRequests;
import com.softjourn.ubm.utils.Consts;
import com.softjourn.ubm.utils.ExtraNames;
import com.softjourn.ubm.utils.Navigation;
import com.softjourn.ubm.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, ExtraNames, Consts, NavigationView.OnNavigationItemSelectedListener, JsonRequests.OnTaskCompleted {

    private static final int PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 0;

    private Bundle mSavedInstanceState;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NavigationView mDrawerList;
    private SearchView mSearchView;
    private ArrayList<String> mLastUrls;
    private ArrayList<String> mLeftMenuItems;
    private ListView mNeedsListView;
    private View mFooterView;
    private NeedsListAdapter mNeedsAdapter;
    private List<Internat> mInternats;
    private DataSource mDataSource;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mSelectedNeedListPosition;
    private String mSelectedSearch;
    private Integer mInternatId = 0;
    private int mSelectedInternatPosition;
    private String mTitle;
    private Fragment mAboutFragment;
    private boolean mMenuClick;
    private DrawerLayout mDrawerLayout;
    private CoordinatorLayout mCoordinatorLayout;

    boolean isLoad = false;
    boolean isSearch = false;
    private Snackbar mSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        allowPermissions();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            mSavedInstanceState = savedInstanceState;
            mSelectedNeedListPosition = savedInstanceState.getInt(LIST_POSITION_EXTRA, 0);
            mSelectedSearch = savedInstanceState.getString(SEARCH_QUERY_EXTRA, "");
            mSelectedInternatPosition = savedInstanceState.getInt(INTERNAT_POSITION_EXTRA, 0);
            mInternatId = savedInstanceState.getInt(INTERNAT_ID, 0);
            isSearch = savedInstanceState.getBoolean(IS_SEARCH_EXTRA, false);
            mLastUrls = savedInstanceState.getStringArrayList(LAST_URL_LIST_EXTRA);
            mTitle = savedInstanceState.getString(TITLE_EXTRA);
            toolbar.setTitle(mTitle);

            if (mLastUrls != null) {
                setLastUrls(mLastUrls);

                Cursor allNeedsCursor = getAllNeedsCursor(mLastUrls);
                if (mNeedsAdapter == null) {
                    mNeedsAdapter = new NeedsListAdapter(MainActivity.this, allNeedsCursor);
                }

                mNeedsAdapter.changeCursor(allNeedsCursor);
                mNeedsAdapter.notifyDataSetChanged();
            }

            if (mSelectedInternatPosition == ABOUT_US) {
                Navigation.gotoAboutScreen(MainActivity.this);
            }

            Utils.hideKeyboard(this);
        }

        mDataSource = new DataSource(this);
        mDataSource.openRead();

        mNeedsListView = (ListView) findViewById(R.id.needs_list);
        mFooterView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_view, null, false);
        mNeedsListView.setEmptyView(findViewById(R.id.no_needs_to_display));
        addNeedsListPaddings();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                if (mSelectedInternatPosition == ABOUT_US) {
                    mSearchView.setVisibility(View.GONE);
                } else {
                    mSearchView.setVisibility(View.VISIBLE);
                }
            }

            public void onDrawerOpened(View drawerView) {
                mSearchView.setVisibility(View.GONE);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setRefreshing(true);

        mNeedsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Navigation.goToNeedActivity(MainActivity.this, position, getLastUrls());
            }
        });

        mNeedsListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int lastInScreen = firstVisibleItem + visibleItemCount;

                if ((totalItemCount > 2) && (lastInScreen == totalItemCount) && !isSearch) {
                    if (Utils.isOnline(getApplicationContext())) {

                        isLoad = true;

                        if (mNeedsListView.getFooterViewsCount() == 1) {
                            mNeedsListView.addFooterView(mFooterView);
                            new JsonRequests().loadMoreRequest(getApplicationContext(), MainActivity.this);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.offline_mode, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        if (Utils.isOnline(getApplicationContext())) {

            if (mSavedInstanceState != null) {
                initInternatsMenu();
                initNeedsList();
            } else {
                clearDB();
                new JsonRequests().loadInternstsRequest(getApplicationContext(), MainActivity.this);
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.offline_mode, Toast.LENGTH_SHORT).show();
            initNeedsList();
            initInternatsMenu();
        }

        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        if (!Utils.isOnline(AppController.getInstance())) {
            Toast.makeText(AppController.getInstance(), R.string.offline_mode, Toast.LENGTH_SHORT).show();
        } else {
            ArrayList<String> lastUrls = getLastUrls();
            if (lastUrls != null && lastUrls.size() > 0) {
                String updateUrl = lastUrls.get(0);
                new JsonRequests().updateRequest(getApplicationContext(), updateUrl, MainActivity.this);
            }
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return true;
    }

    private void selectItem(int menuPosition, boolean firstStart) {
        if (!firstStart && getLastUrls() != null && mSelectedSearch != null && mSelectedSearch.isEmpty()) {
            clearLastUrls();
        }

        if (firstStart) {
            ArrayList<String> lastUrls = new ArrayList<String>();
            lastUrls.add(ALL_NEEDS_URL);
            setLastUrls(lastUrls);
        }

        Fragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(MainFragment.ARG_NEED_NUMBER, menuPosition);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment).commitAllowingStateLoss();

        if (mLeftMenuItems != null) {
            setTitle(mLeftMenuItems.get(menuPosition));
        } else {
            if (mTitle != null && !mTitle.isEmpty()) {
                setTitle(mTitle);
            } else {
                setTitle(getString(R.string.all_internats));
            }
        }

        int allInternatsMenuItemPosition = 0;
        int aboutMenuItemPosition = mLeftMenuItems.size() - 1;

        if (menuPosition == allInternatsMenuItemPosition) {
            if(!isSearch) {
                showProgessDialog();
                if (Utils.isOnline(getApplicationContext())) {
                    new JsonRequests().loadAllNeedsRequest(getApplicationContext(), MainActivity.this);
                } else {
                    displayAllNeeds(getLastUrls());
                }
            }else{
                displayAllNeeds(getLastUrls());
            }

        } else if (menuPosition == aboutMenuItemPosition) {
            if (mSavedInstanceState == null && Utils.isOnline(MainActivity.this)) {
                new JsonRequests().loadAboutRequest(getApplicationContext(), MainActivity.this);
            } else {
                displayAboutInfo();
            }
        } else {

            mInternatId = menuPosition - 1;
            if (Utils.isOnline(getApplicationContext()) && !isSearch) {
                showProgessDialog();
                new JsonRequests().loadOneInternatNeedsRequest(getApplicationContext(), mInternatId, MainActivity.this);
            } else {
                displayOneInternatNeeds(getLastUrls(), mInternatId);
            }
        }

        mSelectedInternatPosition = menuPosition;

        if (mSearchView != null) {
            mSearchView.setQuery("", false);
            mSearchView.setIconified(true);
        }
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    private void addNeedsListPaddings() {
        View paddingsView = new View(this);
        paddingsView.setMinimumHeight(3);
        mNeedsListView.addHeaderView(paddingsView);
        mNeedsListView.addFooterView(paddingsView);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setQueryHint(getString(R.string.search_hint));
        SearchView.SearchAutoComplete theTextArea = (SearchView.SearchAutoComplete) mSearchView.findViewById(R.id.search_src_text);
        theTextArea.setTextColor(Color.WHITE);
        mSearchView.clearFocus();

        if (mSearchView != null && mSelectedSearch != null && !mSelectedSearch.isEmpty()) {
            mSearchView.setQuery(mSelectedSearch, false);
            mSearchView.performClick();
            mSearchView.setIconified(false);
            mSearchView.clearFocus();
        }

        if (mSelectedInternatPosition == ABOUT_US) {
            menu.removeItem(R.id.action_search);
        }

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Utils.hideKeyboard(getApplicationContext());

                if (!Utils.isOnline(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), R.string.offline_mode, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Знайти: " + query, Toast.LENGTH_SHORT).show();
                    new JsonRequests().searchRequest(getApplicationContext(), MainActivity.this);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    private void initNeedsListView(){

        if(!isSearch) {
            mNeedsListView.setAdapter(mNeedsAdapter);
        }

        if(mMenuClick) {
            mNeedsListView.setSelection(0);
        }else{
            mNeedsListView.setSelection(mSelectedNeedListPosition);
        }

        hideProgessDialog();
    }

    @Override
    public void onLoadAllNeedsCompleted(String allNeedsUrl) {
        ArrayList<String> lastUrls = getLastUrls();
        if(!lastUrls.contains(allNeedsUrl)) {
            lastUrls.add(allNeedsUrl);
        }

        displayAllNeeds(lastUrls);
        initNeedsListView();

        isSearch = false;
    }

    @Override
    public void onLoadOneInternatNeedsCompleted(String oneInternatNeeds, int internatId) {
        ArrayList<String> lastUrls = getLastUrls();
        if(!lastUrls.contains(oneInternatNeeds)) {
            lastUrls.add(oneInternatNeeds);
        }
        displayOneInternatNeeds(lastUrls, internatId);
        initNeedsListView();

        isSearch = false;
    }

    @Override
    public void onLoadMoreCompleted(String moreNeedsUrl) {
        ArrayList<String> lastUrls = getLastUrls();
        if(!lastUrls.contains(moreNeedsUrl)) {
            lastUrls.add(moreNeedsUrl);
        }

        if (mInternatId == 0) {
            displayAllNeeds(lastUrls);
        } else {
            displayOneInternatNeeds(lastUrls, mInternatId);
        }

        isLoad = false;
        isSearch = false;
        mNeedsListView.removeFooterView(mFooterView);
    }

    @Override
    public void onLoadSearchCompleted(String searchUrl) {
        ArrayList<String> urlList = new ArrayList<String>();
        urlList.add(searchUrl);

        Cursor foundNeedsCursor = getFoundNeedsCursor(urlList);
        mNeedsAdapter = new NeedsListAdapter(MainActivity.this, foundNeedsCursor);
        mNeedsListView.setAdapter(mNeedsAdapter);
        setLastUrls(urlList);

        hideProgessDialog();
        mSwipeRefreshLayout.setRefreshing(false);

        isSearch = true;
    }

    @Override
    public void onLoadInternatsDataComplited(String internatsUrl) {
        initInternatsMenu();
    }

    @Override
    public void onLoadUpdateCompleted(String lastUrl) {
        ArrayList<String> lastUrls = new ArrayList<String>();
        if(!lastUrls.contains(lastUrl)) {
            lastUrls.add(lastUrl);
        }
        displayAllNeeds(lastUrls);
        isSearch = false;
    }

    @Override
    public void onLoadAboutInfo() {
        displayAboutInfo();
    }

    private void initInternatsMenu() {
        mInternats = mDataSource.getAllInternats();
        mLeftMenuItems = new ArrayList<String>();

        mLeftMenuItems.add(getString(R.string.all_internats));
        for (int i = 1; i <= mInternats.size(); i++) {
            mLeftMenuItems.add(mInternats.get(i - 1).getName());
        }
        mLeftMenuItems.add(getString(R.string.about_us));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.mipmap.drawer_shadow, GravityCompat.START);
        mDrawerList = (NavigationView) findViewById(R.id.nav_view);

        mDrawerList.setNavigationItemSelectedListener(this);

        Menu menu = mDrawerList.getMenu();
        menu.clear();
        for (int i = 0; i < mLeftMenuItems.size(); i++) {
            MenuItem menuItem = menu.add("");
            menuItem.setActionView(R.layout.custom_internat_item)
                    .setOnMenuItemClickListener(menuItemClickListener(i));
            if (i != mLeftMenuItems.size() - 1) {
                menuItem.setIcon(R.drawable.ic_home);
            } else {
                menuItem.setIcon(android.R.drawable.ic_menu_help);
            }
            View view = menuItem.getActionView();
            TextView tv = (TextView) view.findViewById(R.id.item_title);
            tv.setText(mLeftMenuItems.get(i));
        }

        int position = (mSavedInstanceState == null) ? 0 : mSavedInstanceState.getInt(INTERNAT_POSITION_EXTRA, 0);

        if (mSavedInstanceState == null) {
            selectItem(position, true);
        } else {
            selectItem(position, false);
        }
    }

    private MenuItem.OnMenuItemClickListener menuItemClickListener(final int itemId) {
        return new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mSelectedSearch = "";
                mInternatId = itemId;
                isSearch = false;
                selectItem(itemId, false);
                mMenuClick = true;
                mNeedsListView.setSelection(0);
                return false;
            }
        };
    }

    private void initNeedsList() {
        if (mLastUrls == null) {
            String allNeedsUrl = ALL_NEEDS_URL;
            mLastUrls = new ArrayList();
            mLastUrls.add(allNeedsUrl);
        }

        Cursor allNeedsCursor = getAllNeedsCursor(mLastUrls);
        if (mNeedsAdapter == null) {
            mNeedsAdapter = new NeedsListAdapter(MainActivity.this, allNeedsCursor);
        }
        mNeedsAdapter.changeCursor(allNeedsCursor);
        mNeedsAdapter.notifyDataSetChanged();
        mNeedsListView.setAdapter(mNeedsAdapter);
        mSwipeRefreshLayout.setRefreshing(false);

        setSelectedNeed();

        setLastUrls(mLastUrls);
        hideProgessDialog();
    }

    private void setSelectedNeed(){
        if (mMenuClick) {
            mNeedsListView.setSelection(0);
        } else {
            mNeedsListView.setSelection(mSelectedNeedListPosition);
        }
    }


    private void displayAllNeeds(ArrayList<String> lastUrls) {
        Cursor allNeedsCursor;
        if(Utils.isOnline(getApplicationContext())){
            allNeedsCursor = getAllNeedsCursor(lastUrls);
        }else{
            allNeedsCursor = getAllNeedsCursor();
        }

        if (mNeedsAdapter == null) {
            mNeedsAdapter = new NeedsListAdapter(MainActivity.this, allNeedsCursor);
        }

        mNeedsAdapter.changeCursor(allNeedsCursor);

        setSelectedNeed();
        hideProgessDialog();
    }

    private void displayOneInternatNeeds(ArrayList<String> lastUrls, int internatId) {

        Cursor oneNeedsCursor;

        if(Utils.isOnline(getApplicationContext())){
            oneNeedsCursor = getCursorByUrlInternatId(lastUrls, internatId);
        }else{
            oneNeedsCursor = getCursorByInternatId(internatId);
        }

        if (mNeedsAdapter == null) {
            mNeedsAdapter = new NeedsListAdapter(MainActivity.this, oneNeedsCursor);
        }

        mNeedsAdapter.changeCursor(oneNeedsCursor);
        mNeedsAdapter.notifyDataSetChanged();
        setSelectedNeed();
    }

    private void displayAboutInfo() {
        mAboutFragment = AboutFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, mAboutFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDataSource.closeDB();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            int position = data.getIntExtra(LIST_POSITION_EXTRA, 0);
            mNeedsListView.setSelection(position);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mNeedsListView != null) {
            int selectedListItem = mNeedsListView.getFirstVisiblePosition();
            outState.putInt(LIST_POSITION_EXTRA, selectedListItem);

            if (mSearchView != null) {
                String searchQuery = mSearchView.getQuery().toString();
                outState.putString(SEARCH_QUERY_EXTRA, searchQuery);
            }
        }

        if (getLastUrls() != null) {
            outState.putStringArrayList(LAST_URL_LIST_EXTRA, getLastUrls());
        }

        outState.putInt(INTERNAT_POSITION_EXTRA, mSelectedInternatPosition);
        outState.putInt(INTERNAT_ID, mInternatId);
        outState.putString(TITLE_EXTRA, getTitle().toString());
        outState.putBoolean(IS_SEARCH_EXTRA, isSearch);
    }

    protected void allowPermissions(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showRequestPermissionExplanation();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_EXTERNAL_STORAGE);

                // PERMISSIONS_REQUEST_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    private void showRequestPermissionExplanation() {
        mSnackbar = Snackbar
                .make(mCoordinatorLayout, getString(R.string.permission_save_data_explanation), Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.btn_title_ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestAppRunTimePermissions();
                        mSnackbar.dismiss();
                    }
                });
        mSnackbar.show();
    }

    private void requestAppRunTimePermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}

