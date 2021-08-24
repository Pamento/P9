package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository
import com.openclassrooms.realestatemanager.util.Constants.Constants

class LoanSimulatorViewModel(private val mPropertiesRepository: PropertiesRepository) : ViewModel() {
    private val loanCalculated = MutableLiveData<String>()
    var isDollar = true
    var isInterestEdited = false
    var isYearDuration = true
        private set
    var contribution = 0
    var interest = Constants.THIRTY_YEAR_RATE
    var duration = 0
    val singleProperty: LiveData<SingleProperty>
        get() = mPropertiesRepository.getSingleProperty(null)

    fun getLoanCalculated(): LiveData<String> {
        return loanCalculated
    }

    fun setLoanCalculated(loanCalculated: String) {
        this.loanCalculated.value = loanCalculated
    }

    fun setYearDuration() {
        isYearDuration = !isYearDuration
    }
}