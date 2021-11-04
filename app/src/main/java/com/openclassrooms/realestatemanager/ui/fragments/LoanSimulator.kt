package com.openclassrooms.realestatemanager.ui.fragments

import com.openclassrooms.realestatemanager.injection.Injection.sViewModelFactory
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.LoanSimulatorViewModel
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty
import android.os.Bundle
import com.openclassrooms.realestatemanager.R
import android.view.LayoutInflater
import android.view.ViewGroup
import com.openclassrooms.realestatemanager.util.texts.StringModifier
import android.text.TextWatcher
import android.text.Editable
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.openclassrooms.realestatemanager.util.resources.AppResources
import com.openclassrooms.realestatemanager.util.calculation.Calculation
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.databinding.FragmentLoanSimulatorBinding
import com.openclassrooms.realestatemanager.util.Utils

class LoanSimulator : Fragment() {
    private var mViewModel: LoanSimulatorViewModel? = null
    private var binding: FragmentLoanSimulatorBinding? = null
    private var mSingleProperty: SingleProperty? = null
    private var dollarPrice: String? = null
    private var euroPrice: String? = null
    private var dollarPerMonth: String? = null
    private var euroPerMonth: String? = null
    private var currencyToDisplay: String? = ""
    private var loanCalculated = "0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dollarPerMonth = requireActivity().resources.getString(R.string.loan_per_month_dollar)
        euroPerMonth = requireActivity().resources.getString(R.string.loan_per_month_euro)
        dollarPrice = requireActivity().resources.getString(R.string.price_dollar)
        euroPrice = requireActivity().resources.getString(R.string.price_euro)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initViewModel()
        binding = FragmentLoanSimulatorBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        data
        updateButtonDurationState()
        setOnButtonsDurationListener()
        setOnResultObserver()
    }

    private fun setOnResultObserver() {
        mViewModel!!.getLoanCalculated().observe(viewLifecycleOwner, getLoanCalculated)
    }

    private val data: Unit
        get() {
            mViewModel!!.singleProperty.observe(viewLifecycleOwner, getSingleProperty)
        }

    private fun unsubscribeResult() {
        mViewModel!!.getLoanCalculated().removeObserver(getLoanCalculated)
    }

    private fun unsubscribeProperty() {
        mViewModel!!.singleProperty.removeObserver(getSingleProperty)
    }

    private val getLoanCalculated: Observer<String> = Observer { s ->
        if (s != null) {
            loanCalculated = s
            val str = String.format(currencyToDisplay!!, s)
            binding!!.loanCalcResult.text = str
        }
    }
    private val getSingleProperty: Observer<SingleProperty> = Observer { singleProperty ->
        if (singleProperty != null) {
            mSingleProperty = singleProperty
            setOnContributionInputListener()
            setOnRatingInputListener()
            setOnDurationInputListener()
            updateUI()
        }
    }

    private fun updateUI() {
        currencyToDisplay = if (mViewModel!!.isDollar) dollarPerMonth else euroPerMonth
        val priceToDisplay = if (mViewModel!!.isDollar) dollarPrice else euroPrice
        binding!!.loanPropertyType.text = mSingleProperty!!.type
        binding!!.loanPropertyQuarter.text = mSingleProperty!!.quarter
        val price =
            if (mViewModel!!.isDollar) mSingleProperty!!.price.toString() else Utils.convertDollarToEuro(
                mSingleProperty!!.price
            ).toString()
        binding!!.loanPropertyPrice.text =
            String.format(priceToDisplay!!, StringModifier.addComaInPrice(price))
        binding!!.loanCalcInterest.setText(java.lang.String.valueOf(mViewModel!!.interest))
        binding!!.loanCalcResult.text = String.format(currencyToDisplay!!, loanCalculated)
    }

    private fun updateButtonDurationState() {
        binding!!.loanCalcBtnYear.setBackgroundColor(
            if (mViewModel!!.isYearDuration) requireActivity().resources.getColor(
                R.color.colorWhite
            ) else requireActivity().resources.getColor(R.color.colorPrimary)
        )
        binding!!.loanCalcBtnYear.setTextColor(
            if (mViewModel!!.isYearDuration) requireActivity().resources.getColor(
                R.color.colorPrimary
            ) else requireActivity().resources.getColor(R.color.colorWhite)
        )
        // month
        binding!!.loanCalcBtnMonth.setBackgroundColor(
            if (mViewModel!!.isYearDuration) requireActivity().resources.getColor(
                R.color.colorPrimary
            ) else requireActivity().resources.getColor(R.color.colorWhite)
        )
        binding!!.loanCalcBtnMonth.setTextColor(
            if (mViewModel!!.isYearDuration) requireActivity().resources.getColor(
                R.color.colorWhite
            ) else requireActivity().resources.getColor(R.color.colorPrimary)
        )
    }

    private fun setOnButtonsDurationListener() {
        binding!!.loanCalcBtnYear.setOnClickListener { view: View? -> handButtonDurationState() }
        binding!!.loanCalcBtnMonth.setOnClickListener { view: View? -> handButtonDurationState() }
    }

    private fun handButtonDurationState() {
        mViewModel!!.setYearDuration()
        updateButtonDurationState()
    }

    private fun setOnContributionInputListener() {
        binding!!.loanCalcContribution.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) { /**/
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) { /**/
            }

            override fun afterTextChanged(editable: Editable) {
                mViewModel!!.contribution =
                    if (editable.toString() == "") 0 else editable.toString().toInt()
                calculateLoan()
            }
        })
    }

    private fun setOnRatingInputListener() {
        binding!!.loanCalcInterest.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) { /**/
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (AppResources.getInterestFixedRate(charSequence.toString()) == null) {
                    mViewModel!!.isInterestEdited = true
                }
            }

            override fun afterTextChanged(editable: Editable) {
                mViewModel!!.interest =
                    if (editable.toString() == "") AppResources.getInterestFixedRate(
                        mViewModel!!.duration
                    ) else editable.toString().toDouble()
                calculateLoan()
            }
        })
    }

    private fun setOnDurationInputListener() {
        binding!!.loanCalcDuration.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) { /**/
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) { /**/
            }

            override fun afterTextChanged(editable: Editable) {
                val duration = if (editable.toString() == "") "0" else editable.toString()
                mViewModel!!.duration =
                    if (mViewModel!!.isYearDuration) duration.toInt() * 12 else duration.toInt()
                if (!mViewModel!!.isInterestEdited) {
                    updateInterestAccordingDuration()
                }
                calculateLoan()
            }
        })
    }

    private fun updateInterestAccordingDuration() {
        val str = AppResources.getInterestFixedRate(mViewModel!!.duration)
        mViewModel!!.interest = str
        updateUI()
    }

    private fun calculateLoan() {
        if (mViewModel!!.duration > 0) {
            val price =
                if (mViewModel!!.isDollar) mSingleProperty!!.price else Utils.convertDollarToEuro(
                    mSingleProperty!!.price
                )
            mViewModel!!.setLoanCalculated(
                Calculation.calculateMonthlyLoan(
                    price,
                    mViewModel!!.contribution,
                    mViewModel!!.interest,
                    mViewModel!!.duration
                )
            )
        }
    }

    private fun initViewModel() {
        val vmF = sViewModelFactory(requireActivity())
        mViewModel = ViewModelProvider(this, vmF).get(LoanSimulatorViewModel::class.java)
    }

    fun handleCurrency(oneIsDollar: Int) {
        // Value of oneIsDollar = 1 -> $ (one is dollar)
        if (oneIsDollar == 2 && mViewModel!!.isDollar) mViewModel!!.isDollar =
            false else if (oneIsDollar == 1 && !mViewModel!!.isDollar) mViewModel!!.isDollar = true
        // For update the numbers on UI we need to recalculate the loan after currency change
        calculateLoan()
        updateUI()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        if (mViewModel!!.getLoanCalculated().hasActiveObservers()) unsubscribeResult()
        if (mViewModel!!.singleProperty.hasActiveObservers()) unsubscribeProperty()
        super.onDestroy()
    }

    companion object {
        @JvmStatic
        fun newInstance(): LoanSimulator {
            return LoanSimulator()
        }
    }
}