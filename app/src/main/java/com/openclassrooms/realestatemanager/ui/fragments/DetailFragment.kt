package com.openclassrooms.realestatemanager.ui.fragments

import com.openclassrooms.realestatemanager.injection.Injection.sViewModelFactory
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.DetailViewModel
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.openclassrooms.realestatemanager.data.local.entities.PropertyWithImages
import com.openclassrooms.realestatemanager.util.texts.StringModifier
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.ui.adapters.ImageListOfDetailAdapter
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.util.dateTime.SQLTimeHelper
import com.bumptech.glide.Glide
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.data.remote.models.geocode.Location
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding
import com.openclassrooms.realestatemanager.util.Utils
import com.openclassrooms.realestatemanager.util.system.AdapterHelper
import java.util.ArrayList

class DetailFragment : Fragment() {
    private var isTwoFragmentLayout = false
    private var isFirstItemOfList = true
    private var mDetailViewModel: DetailViewModel? = null
    private var binding: FragmentDetailBinding? = null
    private var mSingleProperty: SingleProperty? = null
    private var recyclerV: RecyclerView? = null
    private val mImageOfPropertyList: MutableList<ImageOfProperty> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            isTwoFragmentLayout = requireArguments().getBoolean(LAYOUT_MODE)
            isFirstItemOfList = requireArguments().getBoolean(POSITION_OF_LIST)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initViewModel()
        // Inflate the layout for this fragment
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        setDetailRecyclerView()
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isTwoFragmentLayout) {
            mDetailViewModel!!.propertyId
        }
        setOnDataObservers()
    }

    private fun setOnDataObservers() {
        mDetailViewModel!!.singleProperty.observe(viewLifecycleOwner, getProperty)
        mDetailViewModel!!.imagesOfProperty.observe(viewLifecycleOwner, getImagesOfProperty)
    }

    private val getImagesOfProperty = Observer { imageOfProperties: List<ImageOfProperty>? ->
        if (imageOfProperties != null) {
            if (mImageOfPropertyList.size > 0) mImageOfPropertyList.clear()
            mImageOfPropertyList.addAll(imageOfProperties)
            displayDataOnRecyclerView()
        }
    }
    private val getProperty: Observer<SingleProperty> = Observer { singleProperty ->
        if (singleProperty != null) {
            mSingleProperty = singleProperty
            if (mSingleProperty!!.location == "") {
                locationFromAddress
            } else {
                mDetailViewModel!!.setUrlOfStaticMapOfProperty(singleProperty.location)
            }
        }
        updateUI()
    }

    private fun unsubscribeImagesOfProperty() {
        mDetailViewModel!!.imagesOfProperty.removeObserver(getImagesOfProperty)
    }

    private fun unsubscribeGetProperty() {
        mDetailViewModel!!.singleProperty.removeObserver(getProperty)
    }

//    private val allProperties: Unit
//        get() {
//            mDetailViewModel!!.allProperties.observe(viewLifecycleOwner, getPropertyWithImages)
//        }

    private val getPropertyWithImages: Observer<List<PropertyWithImages>> =
        Observer { propertyWithImages ->
            if (propertyWithImages != null) {
                val property: PropertyWithImages = if (isFirstItemOfList) {
                    propertyWithImages[0]
                } else {
                    propertyWithImages[propertyWithImages.size - 1]
                }
                mSingleProperty = property.mSingleProperty
                if (property.mSingleProperty!!.location == "" && Utils.isInternetAvailable(
                        requireContext()
                    )
                ) {
                    locationFromAddress
                } else {
                    mDetailViewModel!!.setUrlOfStaticMapOfProperty(property.mSingleProperty!!.location)
                }
                updateUI()
                if (mImageOfPropertyList.size > 0) mImageOfPropertyList.clear()
                mImageOfPropertyList.addAll(property.ImagesOfProperty!!)
                displayDataOnRecyclerView()
            }
        }

    private fun unsubscribeGetProperties() {
        mDetailViewModel!!.allProperties.removeObserver(getPropertyWithImages)
    }

    private val locationFromAddress: Unit
        get() {
            if (Utils.isInternetAvailable(requireContext())) {
                val address1 = mSingleProperty!!.address1
                val city = mSingleProperty!!.city
                val quarter = mSingleProperty!!.quarter
                val address = StringModifier.formatAddressToGeocoding(address1, city, quarter)
                mDetailViewModel!!.getLocationFromAddress(address)
                setResponseObserver()
            }
        }

    private fun setResponseObserver() {
        mDetailViewModel!!.geoLocationOfProperty.observe(viewLifecycleOwner, getLocation)
    }

    private fun unsubscribeGetLocation() {
        mDetailViewModel!!.geoLocationOfProperty.removeObserver(getLocation)
    }

    private val getLocation: Observer<Location> = Observer { location ->
        if (location != null) {
            val l = location.lat.toString() + "," + location.lng.toString()
            mDetailViewModel!!.setUrlOfStaticMapOfProperty(l)
            setStaticMapOfProperty()
            mSingleProperty!!.location = l
            updatePropertyInRoom()
            unsubscribeGetLocation()
        }
    }

    private fun setDetailRecyclerView() {
        recyclerV = binding!!.detailImgRecyclerView
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerV!!.setHasFixedSize(true)
        recyclerV!!.layoutManager = layoutManager
    }

    private fun displayDataOnRecyclerView() {
        val adapter = ImageListOfDetailAdapter(mImageOfPropertyList)
        recyclerV!!.adapter = adapter
    }

    private fun updateUI() {
        if (mSingleProperty != null) {
            if (mSingleProperty!!.dateSold != "") binding!!.detailAvailable.setText(R.string.detail_text_sold) else binding!!.detailAvailable.setText(
                R.string.detail_estate_available
            )
            // continue
            binding!!.detailDateRegister.text = SQLTimeHelper.getUSFormDateFromTimeInMillis(
                mSingleProperty!!.dateRegister.toLong()
            )
            val surfaceMetre =
                requireActivity().resources.getString(R.string.detail_surface_integer)
            binding!!.detailSurface.text = String.format(surfaceMetre, mSingleProperty!!.surface)
            val estatePrice =
                if (mDetailViewModel!!.isDollar) requireActivity().resources.getString(R.string.price_dollar) else requireActivity().resources.getString(
                    R.string.price_euro
                )
            val price =
                if (mDetailViewModel!!.isDollar) mSingleProperty!!.price else Utils.convertDollarToEuro(
                    mSingleProperty!!.price
                )
            val priceComa = StringModifier.addComaInPrice(price.toString())
            binding!!.detailPrice.text = String.format(estatePrice, priceComa)
            val estateRooms = requireActivity().resources.getString(R.string.detail_rooms)
            binding!!.detailRoomsNumber.text = String.format(estateRooms, mSingleProperty!!.rooms)
            val estateBathroom = requireActivity().resources.getString(R.string.detail_bathroom)
            binding!!.detailBathroomsNumber.text =
                String.format(estateBathroom, mSingleProperty!!.bathroom)
            val estateBedroom = requireActivity().resources.getString(R.string.detail_bedroom)
            binding!!.detailBedroomNumber.text =
                String.format(estateBedroom, mSingleProperty!!.bedroom)
            binding!!.detailDescription.text = mSingleProperty!!.description
            // Address form
            binding!!.detailAddress1.text = mSingleProperty!!.address1
            binding!!.detailAddress2.text = mSingleProperty!!.address2
            binding!!.detailAddressQuarter.text = mSingleProperty!!.quarter
            binding!!.detailAddressCity.text = mSingleProperty!!.city
            binding!!.detailAddressPostalCode.text = mSingleProperty!!.postalCode.toString()
            val country = requireActivity().resources.getString(R.string.detail_united_states)
            binding!!.detailAddress6.text = country
            setStaticMapOfProperty()
            setAmenitiesView()
            binding!!.detailAgent.text = mSingleProperty!!.agent
        }
    }

    val propertyType: String
        get() = if (mSingleProperty != null) mSingleProperty!!.type else "..."

    fun handleCurrency(oneIsDollar: Int) {
        // Value of oneIsDollar = 1 -> $ (one is dollar)
        if (oneIsDollar == 2 && mDetailViewModel!!.isDollar) mDetailViewModel!!.isDollar =
            false else if (oneIsDollar == 1 && !mDetailViewModel!!.isDollar) mDetailViewModel!!.isDollar =
            true
        updateUI()
    }

    private fun setStaticMapOfProperty() {
        Glide.with(requireContext())
            .load(mDetailViewModel!!.urlOfStaticMapOfProperty)
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_not_found_square)
            .transform(RoundedCornersTransformation(2, 2))
            .into(binding!!.detailSmallStaticMap)
    }

    private fun setAmenitiesView() {
        val amenities = StringModifier.singleStringToArrayString(
            mSingleProperty!!.amenities
        )
        val adapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, amenities)
        binding!!.detailAmenitiesListView.adapter = adapter
        AdapterHelper.setListViewHeightBasedOnChildren(
            binding!!.detailAmenitiesListView
        )
    }

    private fun initViewModel() {
        val vmF = sViewModelFactory(requireActivity())
        mDetailViewModel = ViewModelProvider(this, vmF).get(DetailViewModel::class.java)
    }

    private fun updatePropertyInRoom() {
        mDetailViewModel!!.updateSingleProperty(mSingleProperty)
    }

    override fun onDestroyView() {
        binding = null
        mDetailViewModel!!.clearDisposable()
        super.onDestroyView()
    }

    override fun onDestroy() {
        if (mDetailViewModel!!.geoLocationOfProperty.hasActiveObservers()) unsubscribeGetLocation()
        if (mDetailViewModel!!.allProperties.hasActiveObservers()) unsubscribeGetProperties()
        if (mDetailViewModel!!.singleProperty.hasActiveObservers()) unsubscribeGetProperty()
        if (mDetailViewModel!!.imagesOfProperty.hasActiveObservers()) unsubscribeImagesOfProperty()
        mDetailViewModel!!.disposeDisposable()
        super.onDestroy()
    }

    companion object {
        private const val LAYOUT_MODE = "double"
        private const val POSITION_OF_LIST = "position_list"
        @JvmStatic
        fun newInstance(
            isTwoFragmentsToDisplay: Boolean,
            displayFirstItem: Boolean
        ): DetailFragment {
            val fragment = DetailFragment()
            val args = Bundle()
            args.putBoolean(LAYOUT_MODE, isTwoFragmentsToDisplay)
            args.putBoolean(POSITION_OF_LIST, displayFirstItem)
            fragment.arguments = args
            return fragment
        }
    }
}