package com.openclassrooms.realestatemanager.ui.fragments

import com.openclassrooms.realestatemanager.injection.Injection.sViewModelFactory
import android.app.DatePickerDialog.OnDateSetListener
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.SearchEngineViewModel
import com.openclassrooms.realestatemanager.data.local.models.RowQueryEstates
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.openclassrooms.realestatemanager.util.resources.AppResources
import android.widget.ArrayAdapter
import android.app.DatePickerDialog
import android.text.TextWatcher
import android.text.Editable
import android.view.View
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.util.calculation.Calculation
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.util.notification.NotifyBySnackBar
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.databinding.FragmentSearchEngineBinding
import com.openclassrooms.realestatemanager.ui.activity.MainActivity
import com.openclassrooms.realestatemanager.util.Utils
import java.util.*

class SearchEngine : Fragment(), OnDateSetListener {
    private var mSearchEngineViewModel: SearchEngineViewModel? = null
    private var binding: FragmentSearchEngineBinding? = null
    private var mMillisDateToSearchFrom: Long = 0
    private val mParamsForQuery = RowQueryEstates()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initViewModel()
        binding = FragmentSearchEngineBinding.inflate(inflater, container, false)
        setMinMaxSurface()
        setMinMaxPrice()
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPropertyTypeSpinner()
        setOnDateRegisterListener()
    }

    private fun setPropertyTypeSpinner() {
        val types = AppResources.getPropertyType()
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line, types
        )
        binding!!.searchFType.setAdapter(adapter)
    }

    private fun setOnDateRegisterListener() {
        setDateInputField(Utils.todayDate)
        binding!!.searchFOnMarketSince.setOnClickListener { openDatePicker() }
    }

    private fun openDatePicker() {
        val datePicker = DatePickerDialog(
            requireActivity(), this,
            Calendar.getInstance()[Calendar.YEAR],
            Calendar.getInstance()[Calendar.MONTH],
            Calendar.getInstance()[Calendar.DAY_OF_MONTH]
        )
        datePicker.show()
    }

    private fun setDateInputField(date: String) {
        binding!!.searchFOnMarketSince.text = date
    }

    private fun setMinMaxPrice() {
        binding!!.searchFMaxPrice.addTextChangedListener(object : TextWatcher {
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
                if (binding!!.searchFMinPrice.text != null && editable.toString() != "") {
                    val minPrice =
                        if (binding!!.searchFMinPrice.text.toString() == "") 0 else binding!!.searchFMinPrice.text.toString()
                            .toInt()
                    if (Calculation.isMinGreaterMaxValue(minPrice, editable.toString().toInt())) {
                        val msg = requireActivity().resources.getString(R.string.min_greater_max)
                        NotifyBySnackBar.showSnackBar(1, view, msg)
                    }
                }
            }
        })
    }

    private fun setMinMaxSurface() {
        binding!!.searchFMaxSurface.addTextChangedListener(object : TextWatcher {
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
                if (binding!!.searchFMinSurface.text != null && editable.toString() != "") {
                    val minSurface =
                        if (binding!!.searchFMinSurface.text.toString() == "") 0 else binding!!.searchFMinSurface.text.toString()
                            .toInt()
                    if (Calculation.isMinGreaterMaxValue(minSurface, editable.toString().toInt())) {
                        val msg = requireActivity().resources.getString(R.string.min_greater_max)
                        NotifyBySnackBar.showSnackBar(1, view, msg)
                    }
                }
            }
        })
    }

    private fun initViewModel() {
        val vmF = sViewModelFactory(requireActivity())
        mSearchEngineViewModel = ViewModelProvider(this, vmF).get(
            SearchEngineViewModel::class.java
        )
    }

    fun searchProperties() {
        values
    }

    private val values: Unit
        get() {
            mParamsForQuery.type = binding!!.searchFType.text.toString()
            if (binding!!.searchFMinSurface.text != null) {
                mParamsForQuery.minSurface =
                    getIntValues(binding!!.searchFMinSurface.text.toString())
            }
            if (binding!!.searchFMaxSurface.text != null) {
                mParamsForQuery.maxSurface =
                    getIntValues(binding!!.searchFMaxSurface.text.toString())
            }
            if (binding!!.searchFMinPrice.text != null) {
                mParamsForQuery.minPrice = getIntValues(binding!!.searchFMinPrice.text.toString())
            }
            if (binding!!.searchFMaxPrice.text != null) {
                mParamsForQuery.maxPrice = getIntValues(binding!!.searchFMaxPrice.text.toString())
            }
            if (binding!!.searchFRooms.text != null) {
                mParamsForQuery.rooms = getIntValues(binding!!.searchFRooms.text.toString())
            }
            if (binding!!.searchFBedrooms.text != null) {
                mParamsForQuery.bedroom = getIntValues(binding!!.searchFBedrooms.text.toString())
            }
            if (binding!!.searchFBathrooms.text != null) {
                mParamsForQuery.bathroom = getIntValues(binding!!.searchFBathrooms.text.toString())
            }
            mParamsForQuery.dateRegister = mMillisDateToSearchFrom.toString()
            mParamsForQuery.quarter = binding!!.searchFQuarter.text.toString()
            mParamsForQuery.isSoldEstateInclude = binding!!.searchFSoldSwitch.isChecked
            checkMinMaxV()
        }

    private fun checkMinMaxV() {
        val price = mParamsForQuery.maxPrice >= mParamsForQuery.minPrice
        val surface = mParamsForQuery.maxSurface >= mParamsForQuery.minSurface
        if (price && surface) sendQuery() else {
            val msg = resources.getString(R.string.search_min_max_velues_error)
            NotifyBySnackBar.showSnackBar(1, view, msg)
        }
    }

    private fun getIntValues(value: String): Int {
        return if (value == "") 0 else value.toInt()
    }

    private fun sendQuery() {
        mSearchEngineViewModel!!.buildAndSendSearchEstateQuery(mParamsForQuery)
        val ma = requireActivity() as MainActivity
        ma.onBackPressed()
    }

    override fun onDateSet(datePicker: DatePicker, i: Int, i1: Int, i2: Int) {
        val calendar = Calendar.getInstance()
        calendar[Calendar.YEAR] = i
        calendar[Calendar.MONTH] = i1
        calendar[Calendar.DAY_OF_MONTH] = i2
        val date = calendar.time
        mMillisDateToSearchFrom = calendar.timeInMillis
        setDateInputField(Utils.getUSFormatOfDate(date))
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(): SearchEngine {
            return SearchEngine()
        }
    }
}