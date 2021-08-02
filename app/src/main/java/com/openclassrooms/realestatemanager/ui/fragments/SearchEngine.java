package com.openclassrooms.realestatemanager.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.data.viewModelFactory.ViewModelFactory;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.SearchEngineViewModel;
import com.openclassrooms.realestatemanager.databinding.FragmentSearchEngineBinding;
import com.openclassrooms.realestatemanager.injection.Injection;
import com.openclassrooms.realestatemanager.util.calculation.Calculation;
import com.openclassrooms.realestatemanager.util.notification.NotifyBySnackBar;

import java.util.Objects;

public class SearchEngine extends Fragment {
    private static final String TAG = "SearchEngine";
    private SearchEngineViewModel mSearchEngineViewModel;
    private FragmentSearchEngineBinding binding;
    private View view;

    public SearchEngine() {
        // Required empty public constructor
    }

    public static SearchEngine newInstance() {
        return new SearchEngine();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initViewModel();
        binding = FragmentSearchEngineBinding.inflate(inflater, container, false);
        setMinMaxSurface();
        setMinMaxPrice();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
    }

    private void setMinMaxPrice() {
        binding.searchFMaxPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {/**/}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {/**/}

            @Override
            public void afterTextChanged(Editable editable) {
                int minPrice = 0;
                if (binding.searchFMinPrice.getText() != null) {
                    minPrice = Integer.parseInt(binding.searchFMinPrice.getText().toString());

                    if (Calculation.isMinGreaterMaxValue(minPrice, Integer.parseInt(editable.toString()))) {
                        String msg = requireActivity().getResources().getString(R.string.min_greater_max);
                        NotifyBySnackBar.showSnackBar(1, view, msg);
                    }
                }
            }
        });
    }

    private void setMinMaxSurface() {
        binding.searchFMaxSurface.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {/**/}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {/**/}

            @Override
            public void afterTextChanged(Editable editable) {
                int minSurface = 0;
                if (binding.searchFMinSurface.getText() != null) {
                    minSurface = Integer.parseInt(binding.searchFMinSurface.getText().toString());
                    if (Calculation.isMinGreaterMaxValue(minSurface, Integer.parseInt(editable.toString()))) {
                        String msg = requireActivity().getResources().getString(R.string.min_greater_max);
                        NotifyBySnackBar.showSnackBar(1, view, msg);
                    }
                }
            }
        });

    }

    private void initViewModel() {
        ViewModelFactory vmF = Injection.sViewModelFactory(requireActivity());
        mSearchEngineViewModel = new ViewModelProvider(requireActivity(), vmF).get(SearchEngineViewModel.class);
    }

    // TODO compare if max value is greater than min value

    public void searchProperties() {
        getValues();
    }

    private void getValues() {

        // TODO execute query
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}