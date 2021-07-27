package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty;
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository;

import static com.openclassrooms.realestatemanager.util.Constants.THIRTY_YEAR_RATE;

public class LoanSimulatorViewModel extends ViewModel {

    private MutableLiveData<String> loanCalculated = new MutableLiveData<>();
    private boolean isDollar = true;
    private boolean isInterestEdited = false;
    private boolean isYearDuration = true;
    private int mContribution = 0;
    private double mInterest = THIRTY_YEAR_RATE;
    private int mDuration = 0;

    private final PropertiesRepository mPropertiesRepository;

    public LoanSimulatorViewModel(PropertiesRepository propertiesRepository) {
        mPropertiesRepository = propertiesRepository;
    }

    public LiveData<SingleProperty> getSingleProperty() {
        return mPropertiesRepository.getSingleProperty(null);
    }

    public boolean isDollar() {
        return isDollar;
    }

    public void setDollar(boolean dollar) {
        isDollar = dollar;
    }

    public LiveData<String> getLoanCalculated() {
        return loanCalculated;
    }

    public void setLoanCalculated(String loanCalculated) {
        this.loanCalculated.setValue(loanCalculated);
    }

    public boolean isInterestEdited() {
        return isInterestEdited;
    }

    public void setInterestEdited(boolean interestEdited) {
        Log.i("LOAN_SIMULATOR", "setInterestEdited: " + interestEdited);
        isInterestEdited = interestEdited;
    }

    public boolean isYearDuration() {
        return isYearDuration;
    }

    public void setYearDuration() {
        isYearDuration = !isYearDuration;
    }

    public int getContribution() {
        return mContribution;
    }

    public void setContribution(int contribution) {
        mContribution = contribution;
    }

    public double getInterest() {
        return mInterest;
    }

    public void setInterest(double interest) {
        mInterest = interest;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }
}
