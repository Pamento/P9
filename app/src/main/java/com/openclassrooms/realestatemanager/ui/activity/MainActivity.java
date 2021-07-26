package com.openclassrooms.realestatemanager.ui.activity;

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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.data.viewModelFactory.ViewModelFactory;
import com.openclassrooms.realestatemanager.data.viewmodel.MainActivityViewModel;
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding;
import com.openclassrooms.realestatemanager.injection.Injection;
import com.openclassrooms.realestatemanager.ui.fragments.AddProperty;
import com.openclassrooms.realestatemanager.ui.fragments.DetailFragment;
import com.openclassrooms.realestatemanager.ui.fragments.EditProperty;
import com.openclassrooms.realestatemanager.ui.fragments.ListProperty;
import com.openclassrooms.realestatemanager.ui.fragments.LoanSimulator;
import com.openclassrooms.realestatemanager.ui.fragments.MapFragment;
import com.openclassrooms.realestatemanager.ui.fragments.SearchEngine;
import com.openclassrooms.realestatemanager.util.enums.EFragments;

import java.util.Objects;

import static com.openclassrooms.realestatemanager.util.Constants.ADD_FRAGMENT;
import static com.openclassrooms.realestatemanager.util.Constants.DETAIL_FRAGMENT;
import static com.openclassrooms.realestatemanager.util.Constants.EDIT_FRAGMENT;
import static com.openclassrooms.realestatemanager.util.Constants.LIST_FRAGMENT;
import static com.openclassrooms.realestatemanager.util.Constants.MAP_FRAGMENT;
import static com.openclassrooms.realestatemanager.util.Constants.SEARCH_FRAGMENT;
import static com.openclassrooms.realestatemanager.util.Constants.SIMULATOR_FRAGMENT;
import static com.openclassrooms.realestatemanager.util.enums.EFragments.*;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "AddProperty";
    private MainActivityViewModel mMainViewModel;
    private ActivityMainBinding binding;
    private FragmentManager mFragmentManager;
    private EFragments mEFragments = LIST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setFragmentManager();
        setContentView(view);
        displayListFragment();
    }

    private void setFragmentManager() {
        mFragmentManager = getSupportFragmentManager();
    }

    private void initViewModel() {
        ViewModelFactory vmF = Injection.sViewModelFactory(this);
        mMainViewModel = new ViewModelProvider(this, vmF).get(MainActivityViewModel.class);
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

        if (mFragmentManager != null) {
            FragmentTransaction fTransaction = mFragmentManager
                    .beginTransaction();
            fTransaction.replace(R.id.main_activity_fragment_container, listProperty).commit();
        } else {
            setFragmentManager();
            displayListFragment();
        }
    }

    public void displayFragm(EFragments fragment, String param) {
        mEFragments = fragment;
        Log.i(TAG, "MAIN__ displayFragm: is:: " + mEFragments);
        if (mFragmentManager != null) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            //transaction.setReorderingAllowed(true);
            switch (fragment) {
                case LIST:
                    setToolbarTitle(getResources().getString(R.string.app_name), false);
                    ListProperty lp = ListProperty.newInstance(null, null);
                    transaction.replace(R.id.main_activity_fragment_container, lp, LIST_FRAGMENT);
                    break;
                case MAP:
                    setToolbarTitle("New York", false);
                    MapFragment mf = MapFragment.newInstance(null, null);
                    transaction.replace(R.id.main_activity_fragment_container, mf, MAP_FRAGMENT);
                    break;
                case DETAIL:
                    setToolbarTitle(param, true);
                    DetailFragment df = DetailFragment.newInstance();
                    // transaction.add add the fragment as a layer without removing the list
                    transaction.add(R.id.main_activity_fragment_container, df, DETAIL_FRAGMENT);
                    break;
                case ADD:
                    setToolbarTitle(param, false);
                    AddProperty ap = AddProperty.newInstance();
                    transaction.add(R.id.main_activity_fragment_container, ap, ADD_FRAGMENT);
                    break;
                case SEARCH:
                    setToolbarTitle(param, false);
                    SearchEngine se = SearchEngine.newInstance(null, null);
                    transaction.add(R.id.main_activity_fragment_container, se, SEARCH_FRAGMENT);
                    break;
                case SIMULATOR:
                    setToolbarTitle(param, true);
                    LoanSimulator ls = LoanSimulator.newInstance();
                    transaction.add(R.id.main_activity_fragment_container, ls, SIMULATOR_FRAGMENT);
                    break;
                case EDIT:
                    setToolbarTitle(param, false);
                    EditProperty ep = EditProperty.newInstance();
                    transaction.add(R.id.main_activity_fragment_container, ep);
                    break;
                default:
                    break;
            }
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(param).commit();
        }
    }

    // for manage display of name of fragment like title in the Toolbar
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment fragment = this.getSupportFragmentManager().findFragmentById(R.id.main_activity_fragment_container);
        if (fragment instanceof ListProperty) {
            mEFragments = LIST;
            Log.i(TAG, "onBackPressed: " + getResources().getString(R.string.app_name));
            setToolbarTitle(getResources().getString(R.string.app_name), false);
        } else if (fragment instanceof MapFragment) {
            mEFragments = MAP;
            Log.i(TAG, "onBackPressed: NEW YORK");
        } else if (fragment instanceof DetailFragment) {
            mEFragments = DETAIL;
            // TODO need to manage this case when the action of edit is cancel and we back to Detail Fragment.
            //  Which of item from LIST was chosen to Edit (To display his name in Toolbar title) ?
            Log.i(TAG, "onBackPressed: From ...");
            setToolbarTitle("Detail is back", true);
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
            default:
                break;
        }
    }
}
