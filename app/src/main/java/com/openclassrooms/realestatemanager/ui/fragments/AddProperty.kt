package com.openclassrooms.realestatemanager.ui.fragments

import com.openclassrooms.realestatemanager.injection.Injection.sViewModelFactory
import android.app.DatePickerDialog.OnDateSetListener
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.AddPropertyViewModel
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty
import com.openclassrooms.realestatemanager.ui.adapters.ImageListOfAddPropertyAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.util.resources.AppResources
import android.widget.ArrayAdapter
import android.app.DatePickerDialog
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
import android.net.Uri
import android.text.TextWatcher
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import com.openclassrooms.realestatemanager.util.texts.StringModifier
import com.openclassrooms.realestatemanager.util.notification.NotifyBySnackBar
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
import com.openclassrooms.realestatemanager.util.notification.NotificationsUtils
import com.openclassrooms.realestatemanager.ui.activity.MainActivity
import com.openclassrooms.realestatemanager.util.Constants.Constants
import com.openclassrooms.realestatemanager.util.Utils
import java.io.File
import java.util.*

class AddProperty : Fragment(), OnDateSetListener {
    private final val TAG = "MainActivity-aDD"
    companion object {
        @JvmStatic
        fun newInstance(): AddProperty {
            return AddProperty()
        }
    }

    private var mView: View? = null
    private var mAddPropertyViewModel: AddPropertyViewModel? = null
    private var _binding: FragmentAddPropertyBinding? = null
    private val binding get() = _binding!!
    private var amenitiesBinding: AmenitiesCheckboxesBinding? = null
    private var formAddressBinding: FormAddressPropertyBinding? = null
    private var imagesRecycler: RecyclerView? = null
    private var photoFile: File? = null
    private val imagesToAdd: MutableList<ImageOfProperty?> = ArrayList()
    private var mImageAdapter: ImageListOfAddPropertyAdapter? = null
    private var mIsPropertySold = false
    private var mMillisOfRegisterProperty: Long = 0
    private var mMillisOfSoldDate: Long = 0
    private var isDateRegister = false
    private var mLocation: Location? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initViewModel()
        _binding = FragmentAddPropertyBinding.inflate(inflater, container, false)
        bindIncludesLayouts()
        setEventListener()
        return binding.root
    }

    private fun initViewModel() {
        val vmF = sViewModelFactory(requireActivity())
        mAddPropertyViewModel = ViewModelProvider(this, vmF).get(
            AddPropertyViewModel::class.java
        )
        mAddPropertyViewModel!!.init()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView = view
        setRecyclerView()
        setPropertyTypeSpinner()
        setAgentSpinner()
        setCreationDate()
        handleSoldDateInput()
        setRecyclerViewObserver()
        setOnPriceInputListener()
    }

    private fun setRecyclerViewObserver() {
        mAddPropertyViewModel!!.imagesOfProperty!!.observe(viewLifecycleOwner, getImageOfProperty)
    }

    private fun unsubscribeRecyclerViewObserver() {
        mAddPropertyViewModel!!.imagesOfProperty!!.removeObserver(getImageOfProperty)
    }

    private val getImageOfProperty = Observer { imageOfProperties: List<ImageOfProperty?>? ->
        if (imageOfProperties != null) {
            if (imagesToAdd.size == 0) {
                imagesToAdd.addAll(imageOfProperties)
                setRecyclerView()
            } else {
                imagesToAdd.clear()
                imagesToAdd.addAll(imageOfProperties)
                updateImageAdapter(imageOfProperties)
            }
        }
    }

    private fun updateImageAdapter(ipsum: List<ImageOfProperty?>) {
        mImageAdapter!!.updateImagesList(ipsum)
    }

    private fun setPropertyTypeSpinner() {
        val types = AppResources.getPropertyType()
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line, types
        )
        binding.addFTypeDropdown.setAdapter(adapter)
    }

    private fun setAgentSpinner() {
        val agents = AppResources.getAGENTS()
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line, agents
        )
        binding.addFAgent.setAdapter(adapter)
    }

    // SET UI
    private fun setCreationDate() {
        mMillisOfRegisterProperty = System.currentTimeMillis()
        setDateInputField(Utils.getTodayDate(), 0)
        setListenerDatePicker()
    }

    private fun setListenerDatePicker() {
        binding.addFDateSince.setOnClickListener {
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

    private fun handleSoldDateInput() {
        binding.addFDateSoldOn.isEnabled = mIsPropertySold
    }

    private fun setEventListener() {
        binding.addFBtnAddImgCamera.setOnClickListener { takePictureIntent() }
        binding.addFBtnAddImgGallery.setOnClickListener { openGallery() }
        binding.addFSoldSwitch.setOnClickListener { handleSwitchEvent() }
    }

    private fun handleSwitchEvent() {
        mIsPropertySold = !mIsPropertySold
        if (mIsPropertySold) {
            setDateInputField(Utils.getTodayDate(), 1)
            mMillisOfSoldDate = System.currentTimeMillis()
            binding.addFDateSoldOn.setOnClickListener {
                isDateRegister = false
                showDatePickerDialog()
            }
        } else {
            setDateInputField(requireActivity().resources.getString(R.string.add_sold_on), 1)
            mMillisOfSoldDate = 0
            if (binding.addFDateSoldOn.hasOnClickListeners()) {
                binding.addFDateSoldOn.setOnClickListener(null)
            }
        }
        handleSoldDateInput()
    }

    private fun setDateInputField(date: String, mode: Int) {
        if (mode == 1) {
            binding.addFDateSoldOn.text = date
        } else {
            binding.addFDateSince.text = date
        }
    }

    private fun setRecyclerView() {
        imagesRecycler = binding.addFImagesRecycler
        mImageAdapter = ImageListOfAddPropertyAdapter(imagesToAdd)
        imagesRecycler!!.adapter = mImageAdapter
        imagesRecycler!!.setHasFixedSize(true)
        imagesRecycler!!.layoutManager = LinearLayoutManager(requireContext())
        val itemTouchHelper = ItemTouchHelper(mSimpleCallback)
        itemTouchHelper.attachToRecyclerView(imagesRecycler)
    }

    private var mSimpleCallback: ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(
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
                Collections.swap(imagesToAdd, fromPosition, toPosition)
                Objects.requireNonNull(imagesRecycler!!.adapter)
                    .notifyItemMoved(fromPosition, toPosition)
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                mAddPropertyViewModel!!.removeOneImageOfProperty(
                    mImageAdapter!!.getImageOfPropertyAt(
                        viewHolder.absoluteAdapterPosition
                    )
                )
            }
        }

    private fun bindIncludesLayouts() {
        val amenitiesView: View = binding.addFAmenities.root
        val addressFormView: View = binding.addFFormAddress.root
        amenitiesBinding = AmenitiesCheckboxesBinding.bind(amenitiesView)
        formAddressBinding = FormAddressPropertyBinding.bind(addressFormView)
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
                    // TODO display error state to the user
                }
            }
        }
    }

    private fun openGallery() {
        val gallery =
            Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, Constants.PICK_IMAGE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageUri = Uri.fromFile(photoFile)
            //String uri = ImageFilePathUtil.getRealPathFromURI_API19(requireContext(), imageUri);
            mAddPropertyViewModel!!.createOneImageOfProperty(imageUri.toString())
        } else if (requestCode == Constants.PICK_IMAGE_GALLERY && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val uri = data.data
                mAddPropertyViewModel!!.createOneImageOfProperty(uri.toString())
            }
        }
    }

    // Add coma to price during put in input
    private fun setOnPriceInputListener() {
        binding.addFInputPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) { /**/
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) { /**/
            }

            override fun afterTextChanged(editable: Editable) { /**/
                //https://www.google.com/search?client=opera&q=android+editable.setFilters&sourceid=opera&ie=UTF-8&oe=UTF-8
                //https://stackoverflow.com/questions/3349121/how-do-i-use-inputfilter-to-limit-characters-in-an-edittext-in-android
                editable.filters = arrayOf(priceFilter)
            }
        })
    }

    private val priceFilter =
        InputFilter { source: CharSequence, _: Int, _: Int, _: Spanned?, _: Int, _: Int ->
            StringModifier.addComaInPrice(source.toString())
        }

    // after click on save icon, main activity run this function to check if required fields was fill
    // If so, this function run save method
    fun checkFormValidityBeforeSave() {
        var checked = 0
        Log.i(TAG, "checkFormValidityBeforeSave: checked:: $checked")
        val isWifi: Boolean = Utils.isInternetAvailable(requireContext())
        Log.i(TAG,"checkFormValidityBeforeSave: wifi:: $isWifi")
        val requiredValues = BooleanArray(8)
        // image
        requiredValues[0] = imagesToAdd.size == 0
        // type
        requiredValues[1] = binding.addFTypeDropdown.text.toString() == ""
        // price
        requiredValues[2] = binding.addFInputPrice.text.toString() == ""
        // address1
        requiredValues[3] = binding.addFFormAddress.addAddress1FormAddress.text.toString() == ""
        // quarter
        requiredValues[4] = binding.addFFormAddress.addAddressFormQuarter.text.toString() == ""
        // city
        requiredValues[5] = binding.addFFormAddress.addAddressFormCity.text.toString() == ""
        // postalCode
        requiredValues[6] = binding.addFFormAddress.addAddressFormPostalCode.text.toString() == ""
        // agent
        requiredValues[7] = binding.addFAgent.text.toString() == ""
        for (value in requiredValues) {
            if (value) checked++
        }
        if (checked == 0) {
            mAddPropertyViewModel!!.setImagesOfPropertyList(mImageAdapter!!.imageOfPropertyList)
            if (Utils.isInternetAvailable(requireContext())) {
                geoLocationOfProperty()
            } else {
                createProperty()
            }
        } else {
            val msg = requireActivity().resources.getString(R.string.warning_missing_fields)
            NotifyBySnackBar.showSnackBar(1, mView, msg)
        }
    }

    private fun geoLocationOfProperty() {
            val address1 = formAddressBinding!!.addAddress1FormAddress.editableText.toString()
        val city = formAddressBinding!!.addAddressFormCity.editableText.toString()
        val quarter = formAddressBinding!!.addAddressFormQuarter.editableText.toString()
        val address = StringModifier.formatAddressToGeocoding(address1, city, quarter)
        mAddPropertyViewModel!!.getLocationFromAddress(address)
        Log.i(TAG, "geoLocationOfProperty: $address")
        setOnResponseObserver()
    }

    private fun setOnResponseObserver() {
        Log.i(TAG, "setOnResponseObserver: ")
        mAddPropertyViewModel!!.geoLocationOfProperty!!.observe(
            viewLifecycleOwner,
            { location ->
                location?.let {
                    mLocation = location
                    Log.i(TAG, "setOnResponseObserver: location:: $location")
                    createProperty()
                    // TODO Wi-Fi is "true" and must be "false" and location is null !! Why Wi-Fi is "true" ?!
                }
            })
    }

    private fun createProperty() {
        Log.i(TAG, "createProperty: ")
        // Get inputs values:
        val type = binding.addFTypeDropdown.text.toString()
        val desc = binding.addFDescription.editableText.toString()
        val surfaceStr = binding.addFInputSurface.text.toString()
        val surface = if (TextUtils.isEmpty(surfaceStr)) 0 else surfaceStr.toInt()
        val priceStr = binding.addFInputPrice.text.toString()
        val price = if (TextUtils.isEmpty(priceStr)) 0 else priceStr.toInt()
        val roomsStr = binding.addFInputRooms.text.toString()
        val rooms = if (TextUtils.isEmpty(roomsStr)) 0 else roomsStr.toInt()
        val bedroomsStr = binding.addFInputBedrooms.text.toString()
        val bedrooms = if (TextUtils.isEmpty(bedroomsStr)) 0 else bedroomsStr.toInt()
        val bathroomsStr = binding.addFInputBathrooms.text.toString()
        val bathrooms = if (TextUtils.isEmpty(bathroomsStr)) 0 else bathroomsStr.toInt()
        val address1 = formAddressBinding!!.addAddress1FormAddress.editableText.toString()
        val address2 = formAddressBinding!!.addAddress2FormAddressSuite.editableText.toString()
        val city = formAddressBinding!!.addAddressFormCity.editableText.toString()
        val quarter = formAddressBinding!!.addAddressFormQuarter.editableText.toString()
        val location = if (mLocation == null) "" else formatLocationInString()
        val postalCodeStr = formAddressBinding!!.addAddressFormPostalCode.editableText.toString()
        val postalCode = if (TextUtils.isEmpty(postalCodeStr)) 0 else postalCodeStr.toInt()
        val amenities = amenities
        val agent = binding.addFAgent.text.toString()
        val dateR = mMillisOfRegisterProperty.toString()
        val dateSold = if (mMillisOfSoldDate == 0L) "" else mMillisOfSoldDate.toString()
        mAddPropertyViewModel!!.createNewProperty(
            type,
            desc,
            surface,
            price,
            rooms,
            bedrooms,
            bathrooms,
            dateR,
            dateSold,
            address1,
            address2,
            city,
            quarter,
            postalCode,
            location,
            amenities,
            agent
        )
        saveDataAndNotifyUser()
    }

    private fun formatLocationInString(): String {
        return mLocation!!.lat.toString() + "," + mLocation!!.lng.toString()
    }

    private fun saveDataAndNotifyUser() {
        Log.i(TAG, "saveDataAndNotifyUser: ")
        mAddPropertyViewModel!!.createProperty()
        mAddPropertyViewModel!!.createPropertyResponse.observe(
            viewLifecycleOwner,
            createPropertyObserver
        )
    }

    private fun saveImagesOfProperty() {
        mAddPropertyViewModel!!.saveImagesOfProperty()
        mAddPropertyViewModel!!.saveImagesResponse.observe(viewLifecycleOwner, saveImagesObserver)
    }

    private val saveImagesObserver = Observer { longs: LongArray ->
        val res = readSaveImageResponse(longs)
        if (res) {
            val notify = NotificationsUtils(requireContext())
            notify.showWarning(requireContext(), Constants.SAVE_IMAGES_FAIL)
        } else {
            mAddPropertyViewModel!!.resetImageOfProperty()
            unsubscribeSavePropertyObserver()
            goBackToList()
        }
    }
    private val createPropertyObserver = Observer { aLong: Long ->
        val res = aLong == -1L
        Log.i(TAG, "createPropertyObserver:: response code:: $res ")
        val notify = NotificationsUtils(requireContext())
        if (res) {
            notify.showWarning(requireContext(), Constants.SAVE_PROPERTY_FAIL)
        } else {
            notify.showWarning(requireContext(), Constants.SAVE_PROPERTY_OK)
            saveImagesOfProperty()
        }
    }

    private fun unsubscribeSaveImagesObserver() {
        mAddPropertyViewModel!!.saveImagesResponse.removeObserver(saveImagesObserver)
    }

    private fun unsubscribeSavePropertyObserver() {
        mAddPropertyViewModel!!.createPropertyResponse.removeObserver(createPropertyObserver)
    }

    private fun readSaveImageResponse(longs: LongArray): Boolean {
        for (i in longs) {
            if (i == 0L) {
                return true
            }
        }
        return false
    }

    private fun goBackToList() {
        val ma = requireActivity() as MainActivity
        ma.onBackPressed()
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
        formAddressBinding = null
        amenitiesBinding = null
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        if (mAddPropertyViewModel!!.imagesOfProperty!!.hasActiveObservers()) unsubscribeRecyclerViewObserver()
        if (mAddPropertyViewModel!!.saveImagesResponse.hasActiveObservers()) unsubscribeSaveImagesObserver()
        mAddPropertyViewModel!!.disposeDisposable()
        super.onDestroy()
    }
}