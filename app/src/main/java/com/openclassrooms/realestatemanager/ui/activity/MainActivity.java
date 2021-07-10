package com.openclassrooms.realestatemanager.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding;
import com.openclassrooms.realestatemanager.ui.fragments.DetailFragment;
import com.openclassrooms.realestatemanager.ui.fragments.ListProperty;
import com.openclassrooms.realestatemanager.ui.fragments.MapFragment;
import com.openclassrooms.realestatemanager.util.EFragments;

import java.util.Objects;

import static com.openclassrooms.realestatemanager.util.EFragments.*;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private EFragments mEFragments = LIST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        displayListFragment();
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

    private void displayListFragment() {
        ListProperty listProperty = ListProperty.newInstance(null, null);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fTransaction = fm
                .beginTransaction();
        fTransaction.add(R.id.main_activity_fragment_container, listProperty).addToBackStack(null).commit();
    }

    public void displayFrak(EFragments fragment, String param) {
        mEFragments = fragment;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        //transaction.setReorderingAllowed(true);
        if (fragment.equals(LIST)) {
            setToolbarTitle(getResources().getString(R.string.app_name),false);
            ListProperty lp = ListProperty.newInstance(null,null);
            transaction.replace(R.id.main_activity_fragment_container, lp);
        } else if (fragment.equals(MAP)) {
            setToolbarTitle("New York", false);
            MapFragment mf = MapFragment.newInstance(null, null);
            transaction.replace(R.id.main_activity_fragment_container, mf);
        } else {
            setToolbarTitle(param, true);
            DetailFragment df = DetailFragment.newInstance(param,null);
            // transaction.add add the fragment as a layer without removing the list
            transaction.add(R.id.main_activity_fragment_container, df);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        }
        transaction.addToBackStack(param).commit();
    }

    // for manage display of name of fragment like title in the Toolbar
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment fragment = this.getSupportFragmentManager().findFragmentById(R.id.main_activity_fragment_container);
        if (fragment instanceof ListProperty) {
            Log.i(TAG, "onBackPressed: " + getResources().getString(R.string.app_name));
            setToolbarTitle(getResources().getString(R.string.app_name),false);
        } else if (fragment instanceof MapFragment) {
            Log.i(TAG, "onBackPressed: NEW YORK");
        }
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
