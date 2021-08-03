package com.openclassrooms.realestatemanager.ui.fragments;

import android.app.DatePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.data.local.models.RowQueryEstates;
import com.openclassrooms.realestatemanager.data.viewModelFactory.ViewModelFactory;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.SearchEngineViewModel;
import com.openclassrooms.realestatemanager.databinding.FragmentSearchEngineBinding;
import com.openclassrooms.realestatemanager.injection.Injection;
import com.openclassrooms.realestatemanager.util.Utils;
import com.openclassrooms.realestatemanager.util.calculation.Calculation;
import com.openclassrooms.realestatemanager.util.notification.NotifyBySnackBar;
import com.openclassrooms.realestatemanager.util.resources.AppResources;

import java.util.Calendar;
import java.util.Date;

public class SearchEngine extends Fragment implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "SearchEngine";
    private SearchEngineViewModel mSearchEngineViewModel;
    private FragmentSearchEngineBinding binding;
    private View view;
    private long mMillisDateToSearchFrom = 0;
    private final RowQueryEstates mParamsForQuery = new RowQueryEstates();

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
        setPropertyTypeSpinner();
        setOnDateRegisterListener();
    }

    private void setPropertyTypeSpinner() {
        String[] types = AppResources.getPropertyType();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, types);
        binding.searchFType.setAdapter(adapter);
    }

    private void setOnDateRegisterListener() {
        setDateInputField(Utils.getTodayDate());
        binding.searchFOnMarketSince.setOnClickListener(view -> openDatePicker());
    }

    private void openDatePicker() {
        DatePickerDialog datePicker = new DatePickerDialog(
                requireActivity(), this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePicker.show();
    }

    private void setDateInputField(String date) {
        binding.searchFOnMarketSince.setText(date);
    }

    private void setMinMaxPrice() {
        binding.searchFMaxPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {/**/}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {/**/}

            @Override
            public void afterTextChanged(Editable editable) {
                if (binding.searchFMinPrice.getText() != null) {
                    int minPrice = Integer.parseInt(binding.searchFMinPrice.getText().toString());

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
                if (binding.searchFMinSurface.getText() != null) {
                    int minSurface = Integer.parseInt(binding.searchFMinSurface.getText().toString());
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
        mParamsForQuery.setType(binding.searchFType.getText().toString());
        if (binding.searchFMinSurface.getText() != null) {
            mParamsForQuery.setMinSurface(getIntValues(binding.searchFMinSurface.getText().toString()));
        }
        if (binding.searchFMaxSurface.getText() != null) {
            mParamsForQuery.setMaxSurface(getIntValues(binding.searchFMaxSurface.getText().toString()));
        }
        if (binding.searchFMinPrice.getText() != null) {
            mParamsForQuery.setMinPrice(getIntValues(binding.searchFMinPrice.getText().toString()));
        }
        if (binding.searchFMaxPrice.getText() != null) {
            mParamsForQuery.setMaxPrice(getIntValues(binding.searchFMaxPrice.getText().toString()));
        }
        if (binding.searchFRooms.getText() != null) {
            mParamsForQuery.setRooms(getIntValues(binding.searchFRooms.getText().toString()));
        }
        if (binding.searchFBedrooms.getText() != null) {
            mParamsForQuery.setBedroom(getIntValues(binding.searchFBedrooms.getText().toString()));
        }
        if (binding.searchFBathrooms.getText() != null) {
            mParamsForQuery.setBathroom(getIntValues(binding.searchFBathrooms.getText().toString()));
        }
        mParamsForQuery.setDateRegister(String.valueOf(mMillisDateToSearchFrom));
        mParamsForQuery.setQuarter(binding.searchFQuarter.getText().toString());
        mParamsForQuery.setSoldEstateInclude(binding.searchFSoldSwitch.isChecked());
        // TODO execute query
        sendQuery();
    }

    private int getIntValues(String value) {
        return value.equals("") ? 0 : Integer.parseInt(value);
    }

    private void sendQuery() {
        mSearchEngineViewModel.buildAndSendSearchEstateQuery(mParamsForQuery);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, i);
        calendar.set(Calendar.MONTH, i1);
        calendar.set(Calendar.DAY_OF_MONTH, i2);
        Date date = calendar.getTime();
        mMillisDateToSearchFrom = calendar.getTimeInMillis();
        setDateInputField(Utils.getUSFormatOfDate(date));
    }
}