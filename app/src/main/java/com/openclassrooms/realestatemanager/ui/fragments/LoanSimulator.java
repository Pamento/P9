package com.openclassrooms.realestatemanager.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import static com.openclassrooms.realestatemanager.util.Constants.THIRTY_YEAR_RATE;

public class LoanSimulator extends Fragment {
    private LoanSimulatorViewModel mLoanSimulatorViewModel;
    private FragmentLoanSimulatorBinding binding;
    private SingleProperty mSingleProperty;
    private String dollarPrice;
    private String euroPrice;
    private String dollarPerMonth;
    private String euroPerMonth;
    private String currencyToDisplay;
    private String priceToDisplay;
    private boolean isDollar = true;
    private String loanCalculated = "0";
    private boolean isInterestEdited = false;
    private boolean isYearDuration = true;
    private int mContribution = 0;
    private double mInterest = THIRTY_YEAR_RATE;
    private int mDuration = 0;
    private static final String TAG = "LOAN_SIMULATOR";

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
    }

    private void getData() {
        mLoanSimulatorViewModel.getSingleProperty()
                .observe(getViewLifecycleOwner(), singleProperty -> {
                    mSingleProperty = singleProperty;
                    //if (singleProperty != null) {
                    setOnContributionInputListener();
                    setOnRatingInputListener();
                    setOnDurationInputListener();
                    //updateUI();
                    //}
                    updateUI();
                });
    }

    private void updateUI() {
        Log.i(TAG, "updateUI: isDollar:: " + isDollar);
        currencyToDisplay = isDollar ? dollarPerMonth : euroPerMonth;
        priceToDisplay = isDollar ? dollarPrice : euroPrice;
//        binding.loanPropertyType.setText(mSingleProperty.getType());
//        binding.loanPropertyQuarter.setText(mSingleProperty.getQuarter());
//        String price = isDollar ? mSingleProperty.getPrice() : Utils.convertDollarToEuro(mSingleProperty.getPrice()):
//        binding.loanPropertyPrice.setText(String.format(priceToDisplay ,StringModifier.addComaInPrice(price)));
        binding.loanCalcInterest.setText(String.valueOf(mInterest));
        binding.loanCalcResult.setText(String.format(currencyToDisplay, loanCalculated));

        binding.loanPropertyType.setText("Flat");
        binding.loanPropertyQuarter.setText("Manhattan");
        String price = isDollar ? "1200000" : String.valueOf(Utils.convertDollarToEuro(1200000));
        binding.loanPropertyPrice.setText(String.format(priceToDisplay ,StringModifier.addComaInPrice(price)));

    }

    private void updateButtonDurationState() {
        binding.loanCalcBtnYear.setBackgroundColor(isYearDuration ?
                requireActivity().getResources().getColor(R.color.colorWhite) :
                requireActivity().getResources().getColor(R.color.colorPrimary));
        binding.loanCalcBtnYear.setTextColor(isYearDuration ?
                requireActivity().getResources().getColor(R.color.colorPrimary) :
                requireActivity().getResources().getColor(R.color.colorWhite));
        // month
        binding.loanCalcBtnMonth.setBackgroundColor(isYearDuration ?
                requireActivity().getResources().getColor(R.color.colorPrimary) :
                requireActivity().getResources().getColor(R.color.colorWhite));
        binding.loanCalcBtnMonth.setTextColor(isYearDuration ?
                requireActivity().getResources().getColor(R.color.colorWhite) :
                requireActivity().getResources().getColor(R.color.colorPrimary));
    }

    private void setOnButtonsDurationListener() {
        binding.loanCalcBtnYear.setOnClickListener(view -> handButtonDurationState());
        binding.loanCalcBtnMonth.setOnClickListener(view -> handButtonDurationState());
    }

    private void handButtonDurationState() {
        isYearDuration = !isYearDuration;
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
                mContribution = editable.toString().equals("") ? 0 : Integer.parseInt(editable.toString());
                calculateLoan();
            }
        });
    }

    private void setOnRatingInputListener() {
        binding.loanCalcInterest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {/**/}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {/**/}

            @Override
            public void afterTextChanged(Editable editable) {
                if (binding.loanCalcDuration.hasFocus()) {
                    Log.i(TAG, "afterTextChanged: change:: " + isInterestEdited);
                    isInterestEdited = true;
                }
                Log.i(TAG, "afterTextChanged: EDITABLE_INTEREST:: " + editable.toString());
                mInterest = editable.toString().equals("") ?
                        AppResources.getInterestFixedRate(mDuration) :
                        Double.parseDouble(editable.toString());
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
                Log.i(TAG, "afterTextChanged: EDITABLE_DURATION:: " + editable.toString());
                String duration = editable.toString().equals("") ? "0" : editable.toString();
                mDuration = isYearDuration ? Integer.parseInt(duration) * 12 : Integer.parseInt(duration);
                Log.i(TAG, "afterTextChanged: isInterestEdited  ??:: " + isInterestEdited);
                if (!isInterestEdited) {
                    Log.i(TAG, "afterTextChanged: in");
                    updateInterestAccordingDuration();
                }
                calculateLoan();
            }
        });
    }

    private void updateInterestAccordingDuration() {
        Log.i(TAG, "updateInterestAccordingDuration: CHANGE_INTEREST_RATE____§§____CHANGE isInterestEdited-????:: " + isInterestEdited);
        mInterest = AppResources.getInterestFixedRate(mDuration);
        updateUI();
    }

    private void calculateLoan() {
        Log.i(TAG, "calculateLoan: RUN " + mDuration);
        if (mDuration > 0) {
            //Integer price = isDollar ? mSingleProperty.getPrice() : Utils.convertDollarToEuro(mSingleProperty.getPrice()):
            //String loanCalculated = Calculation.calculateMonthlyLoan(price,mContribution,mInterest,mDuration);
            int price;
            price = isDollar ? 1200000 : Utils.convertDollarToEuro(1200000);
            loanCalculated = Calculation.calculateMonthlyLoan(price, mContribution, mInterest, mDuration);

            binding.loanCalcResult.setText(String.format(currencyToDisplay, loanCalculated));
        }
    }

    private void initViewModel() {
        ViewModelFactory vmF = Injection.sViewModelFactory(requireActivity());
        mLoanSimulatorViewModel = new ViewModelProvider(requireActivity(), vmF).get(LoanSimulatorViewModel.class);
    }

    public void handleCurrency(int oneIsDollar) {
        // Value of oneIsDollar = 1 -> $ (one is dollar)
        if (oneIsDollar == 2 && isDollar) isDollar = false;
        else if (oneIsDollar == 1 && !isDollar) isDollar = true;
        // For update the numbers on UI we need to recalculate the loan after currency change
        calculateLoan();
        updateUI();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}