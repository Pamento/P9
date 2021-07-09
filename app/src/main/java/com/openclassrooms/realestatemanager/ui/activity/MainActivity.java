package com.openclassrooms.realestatemanager.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding;
import com.openclassrooms.realestatemanager.ui.fragments.DetailFragment;
import com.openclassrooms.realestatemanager.ui.fragments.ListProperty;
import com.openclassrooms.realestatemanager.ui.fragments.MapFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        displayListFragment();
    }

    private void setToolbarTitle(String toolbarTitle, boolean backButton) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(toolbarTitle);
        if (backButton) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
        }
    }

    private void displayListFragment() {
        ListProperty listProperty = ListProperty.newInstance(null, null);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fTransaction = fm
                .beginTransaction();
        fTransaction.add(R.id.main_activity_fragment_container, listProperty).addToBackStack(null).commit();
    }

    public void displayFrak(String param) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setReorderingAllowed(true);
        if (param.equals("LIST")) {
            ListProperty lp = ListProperty.newInstance(null,null);
            transaction.replace(R.id.main_activity_fragment_container, lp);
            setToolbarTitle(getResources().getString(R.string.app_name),false);
        } else if (param.equals("MAP")) {
            MapFragment mf = MapFragment.newInstance(null, null);
            transaction.replace(R.id.main_activity_fragment_container, mf);
            setToolbarTitle("New York", false);
        } else {
            DetailFragment df = DetailFragment.newInstance(param,null);
            // transaction.add add the fragment as a layer without removing the list
            transaction.add(R.id.main_activity_fragment_container, df);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            setToolbarTitle(param, true);
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
}
