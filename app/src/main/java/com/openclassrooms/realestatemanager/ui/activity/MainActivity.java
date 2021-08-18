package com.openclassrooms.realestatemanager.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding;
import com.openclassrooms.realestatemanager.ui.fragments.AddProperty;
import com.openclassrooms.realestatemanager.ui.fragments.DetailFragment;
import com.openclassrooms.realestatemanager.ui.fragments.EditProperty;
import com.openclassrooms.realestatemanager.ui.fragments.ListProperty;
import com.openclassrooms.realestatemanager.ui.fragments.LoanSimulator;
import com.openclassrooms.realestatemanager.ui.fragments.MapFragment;
import com.openclassrooms.realestatemanager.ui.fragments.SearchEngine;
import com.openclassrooms.realestatemanager.util.Constants.Constants;
import com.openclassrooms.realestatemanager.util.enums.EFragments;
import com.openclassrooms.realestatemanager.util.notification.NotifyBySnackBar;

import java.util.Objects;

import static com.openclassrooms.realestatemanager.util.Constants.Constants.*;
import static com.openclassrooms.realestatemanager.util.enums.EFragments.*;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private View view;
    private ActivityMainBinding binding;
    private boolean mActivityHasTwoFragment = false;
    private FragmentManager mFragmentManager;
    private EFragments mEFragments = LIST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        setFragmentManager();
        setContentView(view);
        checkIfActivityHasDoubleFragment();
        initListFragment();
    }

    private void setFragmentManager() {
        mFragmentManager = getSupportFragmentManager();
    }

    private void checkIfActivityHasDoubleFragment() {
        FragmentContainerView fc = binding.mainActivityFragmentContainerSecondary;
        mActivityHasTwoFragment = fc != null;
    }

    private void setToolbarTitle(String toolbarTitle, boolean backUpButton) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(toolbarTitle);
        Log.i(TAG, "setToolbarTitle: backUpButton:: " + backUpButton);
        if (backUpButton) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowCustomEnabled(false);
        }
        // invalidateOptionsMenu() for recreate menu according to functions of current fragment
        this.invalidateOptionsMenu();
    }

    private void initListFragment() {
        // The function: initListFragment() is called only on the opening the application. She doesn't part of navigation.
        if (mFragmentManager != null) {
            FragmentTransaction fTransaction = mFragmentManager.beginTransaction();
            ListProperty listProperty = ListProperty.newInstance();
            fTransaction.replace(R.id.main_activity_fragment_container, listProperty, LIST_FRAGMENT).commit();

            Log.i(TAG, "initListFragment: mActivityHasTwoFragment:: " + mActivityHasTwoFragment);
            if (!mActivityHasTwoFragment) Log.i(TAG, "initListFragment: SECONDARY:: is ___NUll:: ");
            else {
                Log.i(TAG, "initListFragment: SECONDARY:: is ___WORK!");
                initDetailFragment(true);
            }
        } else {
            setFragmentManager();
            initListFragment();
        }
    }

    private void initDetailFragment(boolean firstPositionOfList) {
        Log.i(TAG, "MAIN__ initDetailFragment: run");
        // The function: initDetailFragment() is called only on the opening the application. She doesn't part of navigation.
        DetailFragment detail = DetailFragment.newInstance(mActivityHasTwoFragment, firstPositionOfList);
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.main_activity_fragment_container_secondary, detail, DETAIL_FRAGMENT).commit();
    }

    public void displayFragm(EFragments fragment, String toolbarTitle) {
        mEFragments = fragment;
        Log.i(TAG, "MAIN__ displayFragm: is:: " + mEFragments);
        if (mFragmentManager != null) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            //transaction.setReorderingAllowed(true);
            switch (fragment) {
                case LIST:
                    Log.i(TAG, "MAIN__ displayFragm ((onBackPressed)): LIST");
                    setToolbarTitle(getResources().getString(R.string.app_name), false);
                    ListProperty lp = ListProperty.newInstance();
                    transaction.replace(getFragmentContainer(fragment), lp, LIST_FRAGMENT);
                    if (mActivityHasTwoFragment) {
                        initDetailFragment(false);
                    }
                    break;
                case MAP:
                    Log.i(TAG, "MAIN__ displayFragm ((onBackPressed)): MAP");
                    if (isMapsServiceOk()) {
                        setToolbarTitle("New York", false);
                        MapFragment mf = MapFragment.newInstance(mActivityHasTwoFragment);
                        transaction.replace(getFragmentContainer(null), mf, MAP_FRAGMENT);
                    } else {
                        String msg = getResources().getString(R.string.error_msg_map_service_not_available);
                        NotifyBySnackBar.showSnackBar(1, view, msg);
                    }
                    break;
                case DETAIL:
                    Log.i(TAG, "MAIN__ displayFragm ((onBackPressed)): DETAIL");
                    setToolbarTitle(toolbarTitle, true);
                    DetailFragment df = DetailFragment.newInstance(mActivityHasTwoFragment, true);
                    // transaction.add add the fragment as a layer without removing the list
                    transaction.add(getFragmentContainer(null), df, DETAIL_FRAGMENT);
                    break;
                case ADD:
                    Log.i(TAG, "MAIN__ displayFragm ((onBackPressed)): ADD");
                    setToolbarTitle(toolbarTitle, false);
                    AddProperty ap = AddProperty.newInstance();
                    transaction.add(getFragmentContainer(null), ap, ADD_FRAGMENT);
                    break;
                case SEARCH:
                    Log.i(TAG, "MAIN__ displayFragm ((onBackPressed)): SEARCH");
                    setToolbarTitle(toolbarTitle, false);
                    SearchEngine se = SearchEngine.newInstance();
                    transaction.add(getFragmentContainer(null), se, SEARCH_FRAGMENT);
                    break;
                case SIMULATOR:
                    Log.i(TAG, "MAIN__ displayFragm ((onBackPressed)): SIMULATOR");
                    setToolbarTitle(toolbarTitle, true);
                    LoanSimulator ls = LoanSimulator.newInstance();
                    transaction.add(getFragmentContainer(null), ls, SIMULATOR_FRAGMENT);
                    break;
                case EDIT:
                    Log.i(TAG, "MAIN__ displayFragm ((onBackPressed)): EDIT");
                    setToolbarTitle(toolbarTitle, false);
                    EditProperty ep = EditProperty.newInstance();
                    transaction.add(getFragmentContainer(null), ep, EDIT_FRAGMENT);
                    break;
                default:
                    break;
            }
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(toolbarTitle).commit();
        }
    }

    private int getFragmentContainer(@Nullable EFragments fragment) {
        // if LIST && doubleFragment fragm
        if (fragment == LIST && mActivityHasTwoFragment) {
            return R.id.main_activity_fragment_container;
        } else if (fragment == null && mActivityHasTwoFragment) {
            return R.id.main_activity_fragment_container_secondary;
        } else {
            return R.id.main_activity_fragment_container;
        }
        // TODO the List can be called. Than, (?) need to add verification if mEFragments = LIST (?)
        //  or add param: fragment. If fragment == LIST -> {update RecyclerView of ListProperty)
        //int secondFragmentContainer = R.id.main_activity_fragment_container_secondary;
    }

    // Check if service Google Maps is available
    public boolean isMapsServiceOk() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, Constants.ERROR_DIALOG_REQUEST);
            if (dialog != null) dialog.show();
        } else {
            return false;
        }
        return false;
    }

    // for manage display of name of fragment like title in the Toolbar
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int count = mFragmentManager.getBackStackEntryCount();
        Log.i(TAG, "onBackPressed: mEFragments::  " + mEFragments + " & count::" + count);
        mEFragments = findFragmentItIs();
        Log.i(TAG, "onBackPressed: mEFragments::  " + mEFragments + " & count::" + count);
        switch (mEFragments) {
            case LIST:
                setToolbarTitle(getResources().getString(R.string.app_name), false);
                break;
            case MAP:
                Log.i(TAG, "onBackPressed: NEW YORK");
                break;
            case DETAIL:
                // TODO need to manage this case when the action of edit is cancel and we back to Detail Fragment.
                //  Which of item from LIST was chosen to Edit (To display his name in Toolbar title) ?
                setToolbarTitle("Detail is back", true);
                break;
        }
    }

    private EFragments findFragmentItIs() {
        Fragment fragment = this.getSupportFragmentManager().findFragmentById(R.id.main_activity_fragment_container);
        if (fragment instanceof ListProperty)
            return LIST;
        else if (fragment instanceof MapFragment)
            return MAP;
        else if (fragment instanceof DetailFragment)
            return DETAIL;
        else if (fragment instanceof EditProperty)
            return EDIT;
        else if (fragment instanceof LoanSimulator)
            return SIMULATOR;
        else if (fragment instanceof SearchEngine)
            return SEARCH;
        else return ADD;
    }

    // enable/display the flesh of backUp button
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Manage menu Toolbar for different fragments

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.i(TAG, "MAIN__ onPrepareOptionsMenu: RUN");
        Log.i(TAG, "onPrepareOptionsMenu: FRAGMENT:: " + mEFragments);
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        if (mEFragments.equals(LIST) || mEFragments.equals(MAP)) {
            inflater.inflate(R.menu.main_menu, menu);
        } else if (mEFragments.equals(ADD) || mEFragments.equals(EDIT) || mEFragments.equals(SEARCH)) {
            inflater.inflate(R.menu.cancel_save_menu, menu);
        } else if (mEFragments.equals(DETAIL)) {
            inflater.inflate(R.menu.detail_menu, menu);
        } else {
            inflater.inflate(R.menu.simulator_menu, menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        // switch method not allowed for R.id.*
        if (id == R.id.mi_add) {
            displayFragm(ADD, getResources().getString(R.string.frg_add_title));
        } else if (id == R.id.mi_edit) {
            displayFragm(EDIT, getResources().getString(R.string.frg_edit_title));
        } else if (id == R.id.mi_search) {
            displayFragm(SEARCH, getResources().getString(R.string.frg_search_title));
        } else if (id == R.id.mi_cancel) {
            onBackPressed();
        } else if (id == R.id.mi_save) {
            runCommand(0);
        } else if (id == R.id.mi_loan_calculator) {
            displayFragm(SIMULATOR, getResources().getString(R.string.frg_simulator_title));
        } else if (id == R.id.mi_dollar_to_euro) {
            runCommand(2);
        } else if (id == R.id.mi_euro_to_dollar) {
            runCommand(1);
        } else if (id == R.id.mi_reset_row_query) {
            runCommand(0);
        }
        return super.onOptionsItemSelected(item);
    }

    private void runCommand(int commandCode) {
        Log.i(TAG, "runCommand: mEFragments:: " + mEFragments);
        switch (mEFragments) {
            case ADD:
                AddProperty aF = (AddProperty) mFragmentManager.findFragmentByTag(ADD_FRAGMENT);
                if (aF != null) aF.checkFormValidityBeforeSave();
                break;
            case EDIT:
                EditProperty eF = (EditProperty) mFragmentManager.findFragmentByTag(EDIT_FRAGMENT);
                if (eF != null) eF.updateProperty();
                break;
            case SEARCH:
                SearchEngine sF = (SearchEngine) mFragmentManager.findFragmentByTag(SEARCH_FRAGMENT);
                if (sF != null) sF.searchProperties();
                break;
            case SIMULATOR:
                LoanSimulator lF = (LoanSimulator) mFragmentManager.findFragmentByTag(SIMULATOR_FRAGMENT);
                if (lF != null) lF.handleCurrency(commandCode);
            case LIST:
                Log.i(TAG, "runCommand: LIST::");
                ListProperty list = (ListProperty) mFragmentManager.findFragmentByTag(LIST_FRAGMENT);
                if (list != null) list.resetRowQuery();
                break;
            case MAP:
                MapFragment map = (MapFragment) mFragmentManager.findFragmentByTag(MAP_FRAGMENT);
                if (map != null) map.resetRowQuery();
                break;
            default:
                break;
        }
    }
}
