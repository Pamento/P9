package com.openclassrooms.realestatemanager.ui.fragments

import com.openclassrooms.realestatemanager.injection.Injection.sViewModelFactory
import com.openclassrooms.realestatemanager.ui.adapters.ListPropertyAdapter.OnItemPropertyListClickListener
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.ListPropertyViewModel
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.data.local.entities.PropertyWithImages
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.util.enums.QueryState
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.util.notification.NotifyBySnackBar
import com.openclassrooms.realestatemanager.util.enums.EFragments
import com.openclassrooms.realestatemanager.ui.activity.MainActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DividerItemDecoration
import com.openclassrooms.realestatemanager.databinding.FragmentListPropertyBinding
import com.openclassrooms.realestatemanager.ui.adapters.ListPropertyAdapter
import java.util.ArrayList

class ListProperty : Fragment(), OnItemPropertyListClickListener {
    private var mListPropertyViewModel: ListPropertyViewModel? = null
    private var binding: FragmentListPropertyBinding? = null
    private var recyclerView: RecyclerView? = null
    private val mProperties: MutableList<PropertyWithImages> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initViewModel()
        binding = FragmentListPropertyBinding.inflate(inflater, container, false)
        setRecyclerView()
        setQueryStateObserver()
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFabListener()
    }

    private fun initViewModel() {
        val vmF = sViewModelFactory(requireActivity())
        mListPropertyViewModel = ViewModelProvider(this, vmF).get(
            ListPropertyViewModel::class.java
        )
    }

    private fun setQueryStateObserver() {
        mListPropertyViewModel!!.queryState.observe(viewLifecycleOwner, queryStateObserver)
    }

    private fun unsubscribeQueryState() {
        mListPropertyViewModel!!.queryState.removeObserver(queryStateObserver)
    }

    private val queryStateObserver = Observer { queryState: QueryState ->
        if (queryState == QueryState.NULL) {
            setPropertiesObserver()
        } else {
            subscribeRowQuery()
        }
    }

    private fun setPropertiesObserver() {
        mListPropertyViewModel!!.propertyWithImages.observe(viewLifecycleOwner, getProperties)
    }

    private fun unsubscribeProperties() {
        mListPropertyViewModel!!.propertyWithImages.removeObserver(getProperties)
    }

    private fun unsubscribeRowQuery() {
        mListPropertyViewModel!!.simpleSQLiteQuery.removeObserver(rowQueryObserver)
    }

    private fun unsubscribeRowQueryResponse() {
        mListPropertyViewModel!!.propertiesWithImagesFromQuery.removeObserver(
            getPropertiesWithImagesFromQuery
        )
    }

    private fun subscribeRowQuery() {
        mListPropertyViewModel!!.simpleSQLiteQuery.observe(viewLifecycleOwner, rowQueryObserver)
    }

    fun resetRowQuery() {
        mListPropertyViewModel!!.setQueryState(QueryState.NULL)
        setQueryStateObserver()
    }

    private val rowQueryObserver: Observer<SimpleSQLiteQuery> = Observer { simpleSQLiteQuery ->
        if (simpleSQLiteQuery != null) {
            unsubscribeProperties()
            mProperties.clear()
            mListPropertyViewModel!!.propertiesWithImagesFromRowQuery
            mListPropertyViewModel!!.propertiesWithImagesFromQuery.observe(
                viewLifecycleOwner,
                getPropertiesWithImagesFromQuery
            )
        }
    }
    private val getPropertiesWithImagesFromQuery =
        Observer<List<PropertyWithImages>> { propertyWithImages ->
            if (propertyWithImages.isEmpty()) {
                val msg = resources.getString(R.string.search_give_zero_data)
                NotifyBySnackBar.showSnackBar(1, view, msg)
            }
            mProperties.addAll(propertyWithImages)
            displayDataOnRecyclerView()
        }
    private val getProperties = Observer { propertyWithImages: List<PropertyWithImages>? ->
        if (propertyWithImages != null) {
            mProperties.clear()
            mProperties.addAll(propertyWithImages)
        }
        displayDataOnRecyclerView()
    }

    private fun startOtherFragment(fragment: EFragments, param: String) {
        val ma = requireActivity() as MainActivity
        ma.displayFragm(fragment, param)
    }

    private fun setFabListener() {
        binding!!.fabList.setOnClickListener {
            startOtherFragment(
                EFragments.MAP,
                ""
            )
        }
    }

    private fun setRecyclerView() {
        recyclerView = binding!!.listRecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(requireContext())
        recyclerView!!.addItemDecoration(
            DividerItemDecoration(
                recyclerView!!.context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun displayDataOnRecyclerView() {
        if (mListPropertyViewModel!!.propertiesWithImagesFromQuery.hasActiveObservers()) unsubscribeRowQueryResponse()
        val adapter = ListPropertyAdapter(mProperties, this)
        recyclerView!!.adapter = adapter
    }

    override fun onItemPropertyListClickListener(position: Int) {
        if (mProperties.size > 0) {
            val prop = mProperties[position]
            val id = prop.mSingleProperty!!.id
            val propertyType = prop.mSingleProperty!!.type
            // setPropertyId in local data Repositories for Detail, Edit fragment and others: Loan simulator, ...
            mListPropertyViewModel!!.setPropertyId(id)
            startOtherFragment(EFragments.DETAIL, propertyType)
        } else startOtherFragment(EFragments.DETAIL, "Property Type")
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        if (mListPropertyViewModel!!.queryState.hasActiveObservers()) unsubscribeQueryState()
        if (mListPropertyViewModel!!.simpleSQLiteQuery.hasActiveObservers()) unsubscribeRowQuery()
        if (mListPropertyViewModel!!.propertyWithImages.hasActiveObservers()) unsubscribeProperties()
        if (mListPropertyViewModel!!.propertiesWithImagesFromQuery.hasActiveObservers()) unsubscribeRowQueryResponse()
        super.onDestroy()
    }

    companion object {
        @JvmStatic
        fun newInstance(): ListProperty {
            return ListProperty()
        }
    }
}