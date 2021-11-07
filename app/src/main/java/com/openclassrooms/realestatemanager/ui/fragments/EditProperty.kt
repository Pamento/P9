package com.openclassrooms.realestatemanager.ui.fragments

import com.openclassrooms.realestatemanager.injection.Injection.sViewModelFactory
import android.app.DatePickerDialog.OnDateSetListener
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.EditPropertyViewModel
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.ui.adapters.ImageListOfAddPropertyAdapter
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty
import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.util.resources.AppResources
import android.widget.ArrayAdapter
import com.openclassrooms.realestatemanager.util.dateTime.SQLTimeHelper
import com.openclassrooms.realestatemanager.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import android.content.Intent
import android.provider.MediaStore
import com.openclassrooms.realestatemanager.util.system.ImageFileUtils
import android.os.Build
import androidx.core.content.FileProvider
import android.content.ActivityNotFoundException
import android.app.Activity
import android.app.DatePickerDialog
import android.net.Uri
import com.openclassrooms.realestatemanager.util.texts.StringModifier
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.openclassrooms.realestatemanager.data.remote.models.geocode.Location
import com.openclassrooms.realestatemanager.databinding.AmenitiesCheckboxesBinding
import com.openclassrooms.realestatemanager.databinding.FormAddressPropertyBinding
import com.openclassrooms.realestatemanager.databinding.FragmentAddPropertyBinding
import com.openclassrooms.realestatemanager.util.notification.NotifyBySnackBar
import com.openclassrooms.realestatemanager.util.notification.NotificationsUtils
import com.openclassrooms.realestatemanager.ui.activity.MainActivity
import com.openclassrooms.realestatemanager.util.Constants.Constants
import com.openclassrooms.realestatemanager.util.Utils
import java.io.File
import java.util.*

class EditProperty : Fragment(), OnDateSetListener {
    private var mEditPropertyViewModel: EditPropertyViewModel? = null
    private var binding: FragmentAddPropertyBinding? = null
    private var amenitiesBinding: AmenitiesCheckboxesBinding? = null
    private var formAddressBinding: FormAddressPropertyBinding? = null
    private var mView: View? = null
    private var imagesRecycler: RecyclerView? = null
    private var mImageAdapter: ImageListOfAddPropertyAdapter? = null
    private var mSingleProperty: SingleProperty? = null
    private val mImageOfPropertyList: MutableList<ImageOfProperty?> = ArrayList()
    private val mImageOfPropertyListToCompare: MutableList<ImageOfProperty?> = ArrayList()
    private var mIsPropertySold = false
    private var mMillisOfRegisterProperty: Long = 0
    private var mMillisOfSoldDate: Long = 0
    private var photoFile: File? = null
    private var mLocation: Location? = null
    private var isDateRegister = false

    // variable util for update new change after EditProperty ands.
    private var errorRes = 0
    private val index: MutableList<Int> = ArrayList()
    private val temp: MutableList<ImageOfProperty> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initViewModel()
        binding = FragmentAddPropertyBinding.inflate(inflater, container, false)
        bindIncludesLayouts()
        setRecyclerView()
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView = view
        setPropertyDataObservers()
        setPropertyTypeSpinner()
        setAgentSpinner()
    }

    private fun initViewModel() {
        val vmF = sViewModelFactory(requireActivity())
        mEditPropertyViewModel = ViewModelProvider(this, vmF).get(
            EditPropertyViewModel::class.java
        )
    }

    private fun bindIncludesLayouts() {
        val amenitiesView: View = binding!!.addFAmenities.root
        val addressFormView: View = binding!!.addFFormAddress.root
        amenitiesBinding = AmenitiesCheckboxesBinding.bind(amenitiesView)
        formAddressBinding = FormAddressPropertyBinding.bind(addressFormView)
    }

    private fun setPropertyTypeSpinner() {
        val types = AppResources.getPropertyType()
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line, types
        )
        binding!!.addFTypeDropdown.setAdapter(adapter)
    }

    private fun setAgentSpinner() {
        val agents = AppResources.getAGENTS()
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line, agents
        )
        binding!!.addFAgent.setAdapter(adapter)
    }

    private fun setPropertyDataObservers() {
        mEditPropertyViewModel!!.singleProperty.observe(viewLifecycleOwner, getProperty)
        mEditPropertyViewModel!!.imagesOfProperty.observe(viewLifecycleOwner, getImagesOfProperty)
    }

    private fun unsubscribeDataObservers() {
        mEditPropertyViewModel!!.singleProperty.removeObserver(getProperty)
        mEditPropertyViewModel!!.imagesOfProperty.removeObserver(getImagesOfProperty)
    }

    private val getProperty: Observer<SingleProperty> by lazy {
        Observer { singleProperty ->
        if (singleProperty != null) {
            mSingleProperty = singleProperty
            updateUi()
            updateUIAddressForm()
            updateUIAmenities()
        }
        setListenerDatePicker()
    }
    }
    private val getImagesOfProperty = Observer { imagesOfProperty: List<ImageOfProperty?>? ->
        mImageOfPropertyListToCompare.addAll(
            imagesOfProperty!!
        )
        mImageOfPropertyList.clear()
        mImageOfPropertyList.addAll(imagesOfProperty)
        displayImagesOnRecyclerView()
        setEventListener()
    }

    private fun updateUi() {
        binding!!.addFTypeDropdown.setText(mSingleProperty!!.type)
        binding!!.addFDescription.setText(mSingleProperty!!.description)
        binding!!.addFInputSurface.setText(mSingleProperty!!.surface.toString())
        binding!!.addFInputPrice.setText(mSingleProperty!!.price.toString())
        binding!!.addFInputRooms.setText(mSingleProperty!!.rooms.toString())
        binding!!.addFInputBedrooms.setText(mSingleProperty!!.bedroom.toString())
        binding!!.addFInputBathrooms.setText(mSingleProperty!!.bathroom.toString())
        val dateRegister = SQLTimeHelper.getUSFormDateFromTimeInMillis(
            mSingleProperty!!.dateRegister.toLong()
        )
        binding!!.addFDateSince.text = dateRegister
        if (mSingleProperty!!.dateSold != "") {
            binding!!.addFSoldSwitch.isChecked = true
            val dateSold = SQLTimeHelper.getUSFormDateFromTimeInMillis(
                mSingleProperty!!.dateSold.toLong()
            )
            binding!!.addFDateSoldOn.text = dateSold
            binding!!.addFDateSoldOn.isEnabled = false
        }
        binding!!.addFAgent.setText(mSingleProperty!!.agent)
    }

    private fun updateUIAddressForm() {
        formAddressBinding!!.addAddress1FormAddress.setText(mSingleProperty!!.address1)
        formAddressBinding!!.addAddress2FormAddressSuite.setText(mSingleProperty!!.address2)
        formAddressBinding!!.addAddressFormCity.setText(mSingleProperty!!.city)
        formAddressBinding!!.addAddressFormQuarter.setText(mSingleProperty!!.quarter)
        formAddressBinding!!.addAddressFormPostalCode.setText(mSingleProperty!!.postalCode.toString())
    }

    private fun updateUIAmenities() {
        val amenities = mSingleProperty!!.amenities
        if (amenities.contains(Constants.SHOP)) binding!!.addFAmenities.amenitiesShop.isChecked =
            true
        if (amenities.contains(Constants.PARK)) binding!!.addFAmenities.amenitiesPark.isChecked =
            true
        if (amenities.contains(Constants.PLAYGROUND)) binding!!.addFAmenities.amenitiesPlayground.isChecked =
            true
        if (amenities.contains(Constants.SCHOOL)) binding!!.addFAmenities.amenitiesSchool.isChecked =
            true
        if (amenities.contains(Constants.BUS)) binding!!.addFAmenities.amenitiesBus.isChecked = true
        if (amenities.contains(Constants.SUBWAY)) binding!!.addFAmenities.amenitiesSubway.isChecked =
            true
    }

    private fun setEventListener() {
        binding!!.addFBtnAddImgCamera.setOnClickListener { takePictureIntent() }
        binding!!.addFBtnAddImgGallery.setOnClickListener { openGallery() }
        binding!!.addFSoldSwitch.setOnClickListener { handleSwitchEvent() }
    }

    private fun handleSwitchEvent() {
        mIsPropertySold = !mIsPropertySold
        if (mIsPropertySold) {
            setDateInputField(Utils.todayDate, 1)
            mMillisOfSoldDate = System.currentTimeMillis()
            binding!!.addFDateSoldOn.setOnClickListener {
                isDateRegister = false
                showDatePickerDialog()
            }
        } else {
            setDateInputField(requireActivity().resources.getString(R.string.add_sold_on), 1)
            mMillisOfSoldDate = 0
            if (binding!!.addFDateSoldOn.hasOnClickListeners()) {
                binding!!.addFDateSoldOn.setOnClickListener(null)
            }
        }
        handleSoldDateInput()
    }

    private fun setDateInputField(date: String, mode: Int) {
        if (mode == 1) {
            binding!!.addFDateSoldOn.text = date
        } else {
            binding!!.addFDateSince.text = date
        }
    }

    private fun handleSoldDateInput() {
        binding!!.addFDateSoldOn.isEnabled = mIsPropertySold
    }

    private fun setRecyclerView() {
        imagesRecycler = binding!!.addFImagesRecycler
        imagesRecycler!!.layoutManager = LinearLayoutManager(requireContext())
        val itemTouchHelper = ItemTouchHelper(mSimpleCallback)
        itemTouchHelper.attachToRecyclerView(imagesRecycler)
    }

    private fun displayImagesOnRecyclerView() {
        mImageAdapter = ImageListOfAddPropertyAdapter(mImageOfPropertyList)
        imagesRecycler!!.adapter = mImageAdapter
    }

    private var mSimpleCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.absoluteAdapterPosition
            val toPosition = target.absoluteAdapterPosition
            Collections.swap(mImageOfPropertyList, fromPosition, toPosition)
            Objects.requireNonNull(imagesRecycler!!.adapter)
                .notifyItemMoved(fromPosition, toPosition)
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            mImageAdapter!!.removeDeletedImageFromList(viewHolder.absoluteAdapterPosition)
        }
    }

    private fun takePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireContext().packageManager) != null) {
            photoFile = ImageFileUtils.createImageFile()
            if (photoFile != null) {
                val fp: Uri = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    FileProvider.getUriForFile(
                        requireActivity(),
                        "com.openclassrooms.realestatemanager.fileprovider",
                        photoFile!!
                    )
                } else {
                    Uri.fromFile(photoFile)
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fp)
                try {
                    startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE)
                } catch (e: ActivityNotFoundException) {
                    Log.e("ERROR", "takePictureIntent: ", e)
                }
            }
        }
    }

    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, Constants.PICK_IMAGE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageUri = Uri.fromFile(photoFile)
            createNewImageOfProperty(imageUri.toString())
        } else if (requestCode == Constants.PICK_IMAGE_GALLERY && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val uri = data.data
                createNewImageOfProperty(uri.toString())
            }
        }
    }

    private fun createNewImageOfProperty(imageUri: String) {
        val ip = ImageOfProperty()
        ip.path = imageUri
        ip.propertyId = mSingleProperty!!.id
        mImageAdapter!!.addNewImage(ip)
    }

    private fun setListenerDatePicker() {
        binding!!.addFDateSince.setOnClickListener {
            showDatePickerDialog()
            isDateRegister = true
        }
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerDialog(
            requireActivity(), this,
            Calendar.getInstance()[Calendar.YEAR],
            Calendar.getInstance()[Calendar.MONTH],
            Calendar.getInstance()[Calendar.DAY_OF_MONTH]
        )
        datePicker.show()
    }

    private val geoLocationOfProperty: Unit
        get() {
            val address1 = formAddressBinding!!.addAddress1FormAddress.editableText.toString()
            val city = formAddressBinding!!.addAddressFormCity.editableText.toString()
            val quarter = formAddressBinding!!.addAddressFormQuarter.editableText.toString()
            val address = StringModifier.formatAddressToGeocoding(address1, city, quarter)
            mEditPropertyViewModel!!.getLocationFromAddress(address)
            setOnResponseObserver()
        }

    private fun setOnResponseObserver() {
        mEditPropertyViewModel!!.geoLocationOfProperty.observe(viewLifecycleOwner, getLocation)
    }

    private fun unsubscribeGetLocation() {
        mEditPropertyViewModel!!.geoLocationOfProperty.removeObserver(getLocation)
    }

    private val getLocation: Observer<Location> = Observer { location ->
        if (location != null) {
            mLocation = location
            allImageFromAdapter
            setIndexImageToDelete()
            updateImagesOfProperty()
            unsubscribeGetLocation()
        }
    }

    fun updateProperty() {
        mEditPropertyViewModel!!.singleProperty.removeObserver(getProperty)
        mEditPropertyViewModel!!.imagesOfProperty.removeObserver(getImagesOfProperty)
        if (Utils.isInternetAvailable(requireContext())) {
            geoLocationOfProperty
        } else {
            allImageFromAdapter
            setIndexImageToDelete()
            updateImagesOfProperty()
        }
    }

    private fun updatePropertyData() {
        // Get inputs values:
        val type = binding!!.addFTypeDropdown.text.toString()
        if (type != "") mSingleProperty!!.type = type
        val desc = binding!!.addFDescription.editableText.toString()
        if (desc != "") mSingleProperty!!.description = desc
        val surface = binding!!.addFInputSurface.text.toString()
        if (!TextUtils.isEmpty(surface)) mSingleProperty!!.surface = surface.toInt()
        val price = binding!!.addFInputPrice.text.toString()
        if (!TextUtils.isEmpty(price)) mSingleProperty!!.price = price.toInt()
        val rooms = binding!!.addFInputRooms.text.toString()
        if (!TextUtils.isEmpty(rooms)) mSingleProperty!!.rooms = rooms.toInt()
        val bedrooms = binding!!.addFInputBedrooms.text.toString()
        if (!TextUtils.isEmpty(bedrooms)) mSingleProperty!!.bedroom = bedrooms.toInt()
        val bathrooms = binding!!.addFInputBathrooms.text.toString()
        if (!TextUtils.isEmpty(bathrooms)) mSingleProperty!!.bathroom = bathrooms.toInt()
        val address1 = formAddressBinding!!.addAddress1FormAddress.editableText.toString()
        if (address1 != "") mSingleProperty!!.address1 = address1
        val address2 = formAddressBinding!!.addAddress2FormAddressSuite.editableText.toString()
        if (address2 != "") mSingleProperty!!.address2 = address2
        val city = formAddressBinding!!.addAddressFormCity.editableText.toString()
        if (city != "") mSingleProperty!!.city = city
        val quarter = formAddressBinding!!.addAddressFormQuarter.editableText.toString()
        if (quarter != "") mSingleProperty!!.quarter = quarter
        val postalCode = formAddressBinding!!.addAddressFormPostalCode.editableText.toString()
        if (!TextUtils.isEmpty(bedrooms)) mSingleProperty!!.postalCode = postalCode.toInt()
        mSingleProperty!!.location = if (mLocation == null) "" else formatLocationInString()
        mSingleProperty!!.amenities = amenities
        if (mMillisOfRegisterProperty > 0) mSingleProperty!!.dateRegister =
            mMillisOfRegisterProperty.toString()
        if (binding!!.addFSoldSwitch.isChecked && mMillisOfSoldDate > 0) {
            mSingleProperty!!.dateSold = mMillisOfSoldDate.toString()
        }
        val agent = binding!!.addFAgent.text.toString()
        if (agent != "") mSingleProperty!!.agent = agent
        saveDataAndNotifyUser()
    }

    private fun formatLocationInString(): String {
        return mLocation!!.lat.toString() + "," + mLocation!!.lng.toString()
    }

    private val allImageFromAdapter: Unit
        get() {
            temp.addAll(mImageAdapter!!.imageOfPropertyList)
        }

    private fun updateImagesOfProperty() {
        if (temp.size > 0) {
            val img = temp[0]
            if (img.id == 0) {
                mEditPropertyViewModel!!.createImageOfProperty(img)
                mEditPropertyViewModel!!.createImgResponse.observe(
                    viewLifecycleOwner,
                    createImageRes
                )
            } else {
                mEditPropertyViewModel!!.updateImageOfProperty(img)
                mEditPropertyViewModel!!.updateImgResponse.observe(
                    viewLifecycleOwner,
                    updateImageRes
                )
            }
        } else {
            deleteImages()
        }
    }

    private fun setIndexImageToDelete() {
        if (temp.size > 0) {
            for (ip in temp) {
                if (ip.id != 0) {
                    index.add(mImageOfPropertyListToCompare.indexOf(ip))
                }
            }
        }
    }

    private val createImageRes = Observer { integer: Long ->
        if (integer == -1L) errorRes += 1
        deleteFirstPositionInTempVar(true)
    }
    private val updateImageRes = Observer { integer: Int ->
        if (integer == 0) errorRes += 1
        deleteFirstPositionInTempVar(false)
    }

    private fun deleteFirstPositionInTempVar(isCreateImage: Boolean) {
        if (isCreateImage) mEditPropertyViewModel!!.createImgResponse.removeObserver(createImageRes) else mEditPropertyViewModel!!.updateImgResponse.removeObserver(
            updateImageRes
        )
        // Remove first element. Run like while loop.
        temp.removeAt(0)
        updateImagesOfProperty()
    }

    private fun deleteImages() {
        if (index.size < mImageOfPropertyListToCompare.size) {
            val i = mImageOfPropertyListToCompare.size
            for (k in 0 until i) {
                if (!index.contains(k)) {
                    mEditPropertyViewModel!!.deleteImageOfProperty(mImageOfPropertyListToCompare[k]!!.id)
                }
            }
        }
        if (errorRes != 0) {
            NotifyBySnackBar.showSnackBar(1, mView, Constants.SAVE_IMAGES_FAIL)
        }
        updatePropertyData()
    }

    private val amenities: String
        get() {
            val amenities = arrayOfNulls<String>(6)
            amenities[0] =
                if (amenitiesBinding!!.amenitiesShop.isChecked) Constants.SHOP else Constants.NULL
            amenities[1] =
                if (amenitiesBinding!!.amenitiesPark.isChecked) Constants.PARK else Constants.NULL
            amenities[2] =
                if (amenitiesBinding!!.amenitiesPlayground.isChecked) Constants.PLAYGROUND else Constants.NULL
            amenities[3] =
                if (amenitiesBinding!!.amenitiesSchool.isChecked) Constants.SCHOOL else Constants.NULL
            amenities[4] =
                if (amenitiesBinding!!.amenitiesBus.isChecked) Constants.BUS else Constants.NULL
            amenities[5] =
                if (amenitiesBinding!!.amenitiesSubway.isChecked) Constants.SUBWAY else Constants.NULL
            return StringModifier.arrayToSingleString(amenities)
        }

    // Handle data
    private fun saveDataAndNotifyUser() {
        mEditPropertyViewModel!!.updateProperty(mSingleProperty)
        mEditPropertyViewModel!!.updateImgResponse.observe(
            viewLifecycleOwner,
            updatePropertyObserver
        )
    }

    private val updatePropertyObserver = Observer { integer: Int ->
        val notify = NotificationsUtils(requireContext())
        // fail if integer = 0
        if (integer == 0) notify.showWarning(requireContext(), Constants.SAVE_PROPERTY_FAIL) else {
            notify.showWarning(requireContext(), Constants.SAVE_PROPERTY_OK)
            goBackToDetail()
        }
    }

    private fun goBackToDetail() {
        val ma = requireActivity() as MainActivity
        ma.onBackPressed()
    }

    override fun onDateSet(datePicker: DatePicker, i: Int, i1: Int, i2: Int) {
        val calendar = Calendar.getInstance()
        calendar[Calendar.YEAR] = i
        calendar[Calendar.MONTH] = i1
        calendar[Calendar.DAY_OF_MONTH] = i2
        val date = calendar.time
        if (isDateRegister) {
            mMillisOfRegisterProperty = calendar.timeInMillis
            setDateInputField(Utils.getUSFormatOfDate(date), 0)
        } else {
            mMillisOfSoldDate = calendar.timeInMillis
            if (mMillisOfSoldDate >= mMillisOfRegisterProperty) {
                setDateInputField(Utils.getUSFormatOfDate(date), 1)
            } else {
                val msg =
                    requireActivity().resources.getString(R.string.warning_date_sold_smaller_than_register)
                NotifyBySnackBar.showSnackBar(1, mView, msg)
            }
        }
    }

    override fun onDestroyView() {
        amenitiesBinding = null
        formAddressBinding = null
        binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        if (mEditPropertyViewModel!!.geoLocationOfProperty.hasActiveObservers()) unsubscribeGetLocation()
        unsubscribeDataObservers()
        mEditPropertyViewModel!!.disposeDisposable()
        super.onDestroy()
    }

    companion object {
        @JvmStatic
        fun newInstance(): EditProperty {
            return EditProperty()
        }
    }
}