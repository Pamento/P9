package com.openclassrooms.realestatemanager.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty;
import com.openclassrooms.realestatemanager.data.viewModelFactory.ViewModelFactory;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.LoanSimulatorViewModel;
import com.openclassrooms.realestatemanager.databinding.FragmentLoanSimulatorBinding;
import com.openclassrooms.realestatemanager.injection.Injection;
import com.openclassrooms.realestatemanager.util.Utils;
import com.openclassrooms.realestatemanager.util.calculation.Calculation;
import com.openclassrooms.realestatemanager.util.resources.AppResources;
import com.openclassrooms.realestatemanager.util.texts.StringModifier;

public class LoanSimulator extends Fragment {
    private LoanSimulatorViewModel mViewModel;
    private FragmentLoanSimulatorBinding binding;
    private SingleProperty mSingleProperty;
    private String dollarPrice;
    private String euroPrice;
    private String dollarPerMonth;
    private String euroPerMonth;
    private String currencyToDisplay;
    private String loanCalculated = "0";

    public LoanSimulator() {
        // Required empty public constructor
    }

    public static LoanSimulator newInstance() {
        return new LoanSimulator();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dollarPerMonth = requireActivity().getResources().getString(R.string.loan_per_month_dollar);
        euroPerMonth = requireActivity().getResources().getString(R.string.loan_per_month_euro);
        dollarPrice = requireActivity().getResources().getString(R.string.price_dollar);
        euroPrice = requireActivity().getResources().getString(R.string.price_euro);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initViewModel();
        binding = FragmentLoanSimulatorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData();
        updateButtonDurationState();
        setOnButtonsDurationListener();
        setOnResultObserver();
    }

    private void setOnResultObserver() {
        mViewModel.getLoanCalculated().observe(getViewLifecycleOwner(), getLoanCalculated);
    }

    private void getData() {
        mViewModel.getSingleProperty().observe(getViewLifecycleOwner(), getSingleProperty);
    }

    private void unsubscribeResult() {
        mViewModel.getLoanCalculated().removeObserver(getLoanCalculated);
    }

    private void unsubscribeProperty() {
        mViewModel.getSingleProperty().removeObserver(getSingleProperty);
    }

    final Observer<String> getLoanCalculated = new Observer<String>() {
        @Override
        public void onChanged(String s) {
            if (s != null) {
                loanCalculated = s;
                String str = String.format(currencyToDisplay, s);
                binding.loanCalcResult.setText(str);
            }
        }
    };

    final Observer<SingleProperty> getSingleProperty = new Observer<SingleProperty>() {
        @Override
        public void onChanged(SingleProperty singleProperty) {
            if (singleProperty != null) {
                mSingleProperty = singleProperty;
                setOnContributionInputListener();
                setOnRatingInputListener();
                setOnDurationInputListener();
                updateUI();
            }
        }
    };

    private void updateUI() {
        currencyToDisplay = mViewModel.isDollar() ? dollarPerMonth : euroPerMonth;
        String priceToDisplay = mViewModel.isDollar() ? dollarPrice : euroPrice;
        binding.loanPropertyType.setText(mSingleProperty.getType());
        binding.loanPropertyQuarter.setText(mSingleProperty.getQuarter());
        String price = mViewModel.isDollar() ?
                String.valueOf(mSingleProperty.getPrice()) :
                String.valueOf(Utils.convertDollarToEuro(mSingleProperty.getPrice()));
        binding.loanPropertyPrice.setText(String.format(priceToDisplay, StringModifier.addComaInPrice(price)));
        binding.loanCalcInterest.setText(String.valueOf(mViewModel.getInterest()));
        binding.loanCalcResult.setText(String.format(currencyToDisplay, loanCalculated));
    }

    private void updateButtonDurationState() {
        binding.loanCalcBtnYear.setBackgroundColor(mViewModel.isYearDuration() ?
                requireActivity().getResources().getColor(R.color.colorWhite) :
                requireActivity().getResources().getColor(R.color.colorPrimary));
        binding.loanCalcBtnYear.setTextColor(mViewModel.isYearDuration() ?
                requireActivity().getResources().getColor(R.color.colorPrimary) :
                requireActivity().getResources().getColor(R.color.colorWhite));
        // month
        binding.loanCalcBtnMonth.setBackgroundColor(mViewModel.isYearDuration() ?
                requireActivity().getResources().getColor(R.color.colorPrimary) :
                requireActivity().getResources().getColor(R.color.colorWhite));
        binding.loanCalcBtnMonth.setTextColor(mViewModel.isYearDuration() ?
                requireActivity().getResources().getColor(R.color.colorWhite) :
                requireActivity().getResources().getColor(R.color.colorPrimary));
    }

    private void setOnButtonsDurationListener() {
        binding.loanCalcBtnYear.setOnClickListener(view -> handButtonDurationState());
        binding.loanCalcBtnMonth.setOnClickListener(view -> handButtonDurationState());
    }

    private void handButtonDurationState() {
        mViewModel.setYearDuration();
        updateButtonDurationState();
    }

    private void setOnContributionInputListener() {
        binding.loanCalcContribution.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {/**/}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {/**/}

            @Override
            public void afterTextChanged(Editable editable) {
                mViewModel.setContribution(editable.toString().equals("") ? 0 : Integer.parseInt(editable.toString()));
                calculateLoan();
            }
        });
    }

    private void setOnRatingInputListener() {
        binding.loanCalcInterest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {/**/}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (AppResources.getInterestFixedRate(charSequence.toString()) == null) {
                    mViewModel.setInterestEdited(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mViewModel.setInterest(editable.toString().equals("") ?
                        AppResources.getInterestFixedRate(mViewModel.getDuration()) :
                        Double.parseDouble(editable.toString()));
                calculateLoan();
            }
        });
    }

    private void setOnDurationInputListener() {
        binding.loanCalcDuration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {/**/}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {/**/}

            @Override
            public void afterTextChanged(Editable editable) {
                String duration = editable.toString().equals("") ? "0" : editable.toString();
                mViewModel.setDuration(mViewModel.isYearDuration() ? Integer.parseInt(duration) * 12 : Integer.parseInt(duration));
                if (!mViewModel.isInterestEdited()) {
                    updateInterestAccordingDuration();
                }
                calculateLoan();
            }
        });
    }

    private void updateInterestAccordingDuration() {
        double str = AppResources.getInterestFixedRate(mViewModel.getDuration());
        mViewModel.setInterest(str);
        updateUI();
    }

    private void calculateLoan() {
        if (mViewModel.getDuration() > 0) {
            int price = mViewModel.isDollar() ? mSingleProperty.getPrice() : Utils.convertDollarToEuro(mSingleProperty.getPrice());
            mViewModel.setLoanCalculated(Calculation.calculateMonthlyLoan(price, mViewModel.getContribution(), mViewModel.getInterest(), mViewModel.getDuration()));
        }
    }

    private void initViewModel() {
        ViewModelFactory vmF = Injection.sViewModelFactory(requireActivity());
        mViewModel = new ViewModelProvider(requireActivity(), vmF).get(LoanSimulatorViewModel.class);
    }

    public void handleCurrency(int oneIsDollar) {
        // Value of oneIsDollar = 1 -> $ (one is dollar)
        if (oneIsDollar == 2 && mViewModel.isDollar()) mViewModel.setDollar(false);
        else if (oneIsDollar == 1 && !mViewModel.isDollar()) mViewModel.setDollar(true);
        // For update the numbers on UI we need to recalculate the loan after currency change
        calculateLoan();
        updateUI();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (mViewModel.getLoanCalculated().hasActiveObservers()) unsubscribeResult();
        if (mViewModel.getSingleProperty().hasActiveObservers()) unsubscribeProperty();
        super.onDestroy();
    }
}