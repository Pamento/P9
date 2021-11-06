package com.openclassrooms.realestatemanager.ui.fragments

import com.openclassrooms.realestatemanager.injection.Injection.sViewModelFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.MapViewModel
import com.google.android.gms.maps.GoogleMap
import com.openclassrooms.realestatemanager.data.local.entities.PropertyWithImages
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.SupportMapFragment
import com.openclassrooms.realestatemanager.R
import com.google.android.gms.maps.model.Marker
import com.openclassrooms.realestatemanager.util.enums.EFragments
import com.nabinbhandari.android.permissions.PermissionHandler
import android.annotation.SuppressLint
import android.location.Location
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.openclassrooms.realestatemanager.util.enums.QueryState
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.util.notification.NotifyBySnackBar
import com.openclassrooms.realestatemanager.util.system.LocationUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.nabinbhandari.android.permissions.Permissions
import com.openclassrooms.realestatemanager.databinding.FragmentMapBinding
import com.openclassrooms.realestatemanager.ui.activity.MainActivity
import com.openclassrooms.realestatemanager.util.Constants.Constants
import com.openclassrooms.realestatemanager.util.Utils
import java.util.*

class MapFragment : Fragment(), OnMapReadyCallback, OnMarkerClickListener {
    private var mMapViewModel: MapViewModel? = null
    private var binding: FragmentMapBinding? = null
    private var mGoogleMaps: GoogleMap? = null
    private val mPropertyWithImages: MutableList<PropertyWithImages> = ArrayList()
    private var isDoubleFragment = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            isDoubleFragment = requireArguments().getBoolean(FAB_DECIDER)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initViewModel()
        binding = FragmentMapBinding.inflate(inflater, container, false)
        initMap()
        currentUserLocation
        return binding!!.root
    }

    private fun initViewModel() {
        val vmF = sViewModelFactory(requireActivity())
        mMapViewModel = ViewModelProvider(this, vmF).get(MapViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //this.view = view
        if (isDoubleFragment) {
            binding!!.fabList.visibility = View.GONE
        } else {
            setFabListener()
        }
    }

    private fun initMap() {
        if (Utils.isInternetAvailable(requireContext())) {
            val supportMapFragment =
                childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            supportMapFragment?.getMapAsync(this@MapFragment)
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        marker.tag?.let {
            mMapViewModel!!.setPropertyId(marker.tag?.toString())
            marker.tag?.toString()?.let { it1 -> getPropertyType(it1) }?.let { it2 ->
                navigateToFragment(EFragments.DETAIL,
                    it2
                )
            }
        }
        return false
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMaps = googleMap
        mGoogleMaps!!.setOnMarkerClickListener(this)
        mMapViewModel!!.googleMap = googleMap
        Permissions.check(
            requireContext(),
            Constants.PERMISSIONS,
            null,
            null,
            object : PermissionHandler() {
                override fun onGranted() {
                    setMap()
                }
            })
    }

    private fun setMap() {
        Permissions.check(
            requireContext(),
            Constants.PERMISSIONS,
            null,
            null,
            object : PermissionHandler() {
                @SuppressLint("MissingPermission")
                override fun onGranted() {
                    if (mMapViewModel!!.currentUserLocation != null) {
                        moveCamera(mMapViewModel!!.currentUserLocation)
                        // display point bleu on the map
                        mGoogleMaps!!.isMyLocationEnabled = true
                        setQueryStateObserver()
                    }
                }
            })
    }

    private fun setQueryStateObserver() {
        mMapViewModel!!.queryState.observe(viewLifecycleOwner, queryStateObserver)
    }

    private fun unsubscribeQueryState() {
        mMapViewModel!!.queryState.removeObserver(queryStateObserver)
    }

    private val queryStateObserver = Observer { queryState: QueryState ->
        if (queryState == QueryState.NULL) {
            setPropertyObserver()
        } else {
            setRowQueryObserver()
        }
    }

    private fun setPropertiesMarkersOnMap() {
        // unsubscribe
        if (mMapViewModel!!.propertyWithImageQuery.hasActiveObservers()) unsubscribeRowQueryResponse()
        if (mGoogleMaps == null) mGoogleMaps = mMapViewModel!!.googleMap
        if (mGoogleMaps != null) {
            mGoogleMaps!!.clear()
            if (mPropertyWithImages.size != 0) {
                for (sp in mPropertyWithImages) {
                    var marker: Marker?
                    if (sp.mSingleProperty!!.location != "") {
                        val latLang = sp.mSingleProperty!!.location.split(",").toTypedArray()
                        val latLng = LatLng(latLang[0].toDouble(), latLang[1].toDouble())
                        marker = mGoogleMaps!!.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(sp.mSingleProperty!!.address1)
                        )
                        if (marker != null) marker.tag = sp.mSingleProperty!!.id
                    }
                }
            }
        }
    }

    private fun setPropertyObserver() {
        mMapViewModel!!.propertyWithImages.observe(viewLifecycleOwner, getPropertyWithImages)
    }

    private fun unsubscribeProperty() {
        mMapViewModel!!.propertyWithImages.removeObserver(getPropertyWithImages)
    }

    private fun setRowQueryObserver() {
        mMapViewModel!!.simpleSQLiteQuery.observe(viewLifecycleOwner, rowQueryObserver)
    }

    private fun unsubscribeRowQuery() {
        mMapViewModel!!.simpleSQLiteQuery.removeObserver(rowQueryObserver)
    }

    private fun unsubscribeRowQueryResponse() {
        mMapViewModel!!.propertyWithImageQuery.removeObserver(getPropertiesWithImagesFromQuery)
    }

    fun resetRowQuery() {
        mMapViewModel!!.setQueryState(QueryState.NULL)
        setQueryStateObserver()
    }

    private val rowQueryObserver: Observer<SimpleSQLiteQuery> = Observer { simpleSQLiteQuery ->
        if (simpleSQLiteQuery != null) {
            unsubscribeProperty()
            mPropertyWithImages.clear()
            mMapViewModel!!.propertiesWithImagesFromRowQuery
            mMapViewModel!!.propertyWithImageQuery.observe(
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
            mPropertyWithImages.addAll(propertyWithImages)
            setPropertiesMarkersOnMap()
        }
    private val getPropertyWithImages = Observer { propertyWithImages: List<PropertyWithImages>? ->
        if (propertyWithImages != null) {
            mPropertyWithImages.clear()
            mPropertyWithImages.addAll(propertyWithImages)
        }
        setPropertiesMarkersOnMap()
    }
    private val currentUserLocation: Unit
        get() {
            Permissions.check(
                requireContext(),
                Constants.PERMISSIONS,
                null,
                null,
                object : PermissionHandler() {
                    override fun onGranted() {
                        if (LocationUtils.isDeviceLocationEnabled()) {
                            Objects.requireNonNull(LocationUtils.getCurrentDeviceLocation())
                                .observe(
                                    viewLifecycleOwner, getLocation
                                )
                        } else {
                            val msg = "Please, switch ON location for use plenty of this app."
                            NotifyBySnackBar.showSnackBar(1, view, msg)
                        }
                    }
                })
        }
    private val getLocation: Observer<Location> = Observer { location ->
        if (location != null) {
            mMapViewModel!!.currentUserLocation = location
            if (mGoogleMaps != null) {
                setMap()
            }
        }
    }

    private fun moveCamera(loc: Location?) {
        val latLng = LatLng(loc!!.latitude, loc.longitude)
        mGoogleMaps!!.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng,
                Constants.DEFAULT_MAPS_ZOOM
            )
        )
    }

    private fun setFabListener() {
        binding!!.fabList.setOnClickListener {
            navigateToFragment(
                EFragments.LIST,
                ""
            )
        }
    }

    private fun navigateToFragment(fragment: EFragments, toolbarTitle: String) {
        val ma = requireActivity() as MainActivity
        if (fragment == EFragments.LIST) ma.onBackPressed() else ma.displayFragm(
            fragment,
            toolbarTitle
        )
    }

    private fun getPropertyType(propertyId: String): String {
        var pType = ""
        for (p in mPropertyWithImages) {
            if (p.mSingleProperty!!.id == propertyId) pType = p.mSingleProperty!!.type
        }
        return pType
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        if (mMapViewModel!!.queryState.hasActiveObservers()) unsubscribeQueryState()
        if (mMapViewModel!!.simpleSQLiteQuery.hasActiveObservers()) unsubscribeRowQuery()
        if (mMapViewModel!!.propertyWithImages.hasActiveObservers()) unsubscribeProperty()
        if (mMapViewModel!!.propertyWithImageQuery.hasActiveObservers()) unsubscribeRowQueryResponse()
        super.onDestroy()
    }

    companion object {
        const val FAB_DECIDER = "display_fab"
        @JvmStatic
        fun newInstance(isDoubleFragment: Boolean): MapFragment {
            val fragment = MapFragment()
            val args = Bundle()
            args.putBoolean(FAB_DECIDER, isDoubleFragment)
            fragment.arguments = args
            return fragment
        }
    }
}