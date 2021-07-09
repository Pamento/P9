package com.openclassrooms.realestatemanager.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding;
import com.openclassrooms.realestatemanager.ui.fragments.DetailFragment;
import com.openclassrooms.realestatemanager.ui.fragments.ListProperty;
import com.openclassrooms.realestatemanager.ui.fragments.MapFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        displayListFragment();
    }

    private void displayListFragment() {
        ListProperty listProperty = ListProperty.newInstance(null, null);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fTransaction = fm
                .beginTransaction();
        fTransaction.add(R.id.main_activity_fragment_container, listProperty).addToBackStack(null).commit();
    }

    public void displayFrak(String fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setReorderingAllowed(true);
        if (fragment.equals("LIST")) {
            ListProperty lp = ListProperty.newInstance(null,null);
            transaction.replace(R.id.main_activity_fragment_container, lp);
        } else if (fragment.equals("MAP")) {
            MapFragment mf = MapFragment.newInstance(null, null);
            transaction.replace(R.id.main_activity_fragment_container, mf);
        } else {
            DetailFragment df = DetailFragment.newInstance(fragment,null);
            transaction.replace(R.id.main_activity_fragment_container, df);
        }
        transaction.commit();
    }
}
