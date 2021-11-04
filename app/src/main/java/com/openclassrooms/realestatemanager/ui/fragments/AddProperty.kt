package com.openclassrooms.realestatemanager.ui.fragments;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty;
import com.openclassrooms.realestatemanager.data.remote.models.geocode.Location;
import com.openclassrooms.realestatemanager.data.viewModelFactory.ViewModelFactory;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.AddPropertyViewModel;
import com.openclassrooms.realestatemanager.databinding.AmenitiesCheckboxesBinding;
import com.openclassrooms.realestatemanager.databinding.FormAddressPropertyBinding;
import com.openclassrooms.realestatemanager.databinding.FragmentAddPropertyBinding;
import com.openclassrooms.realestatemanager.injection.Injection;
import com.openclassrooms.realestatemanager.ui.activity.MainActivity;
import com.openclassrooms.realestatemanager.ui.adapters.ImageListOfAddPropertyAdapter;
import com.openclassrooms.realestatemanager.util.Utils;
import com.openclassrooms.realestatemanager.util.notification.NotificationsUtils;
import com.openclassrooms.realestatemanager.util.notification.NotifyBySnackBar;
import com.openclassrooms.realestatemanager.util.resources.AppResources;
import com.openclassrooms.realestatemanager.util.system.ImageFileUtils;
import com.openclassrooms.realestatemanager.util.texts.StringModifier;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.openclassrooms.realestatemanager.util.Constants.Constants.*;

public class AddProperty extends Fragment implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "AddProperty";
    private View mView;
    private AddPropertyViewModel mAddPropertyViewModel;
    private FragmentAddPropertyBinding binding;
    private AmenitiesCheckboxesBinding amenitiesBinding;
    private FormAddressPropertyBinding formAddressBinding;
    private RecyclerView imagesRecycler;
    private File photoFile;
    private final List<ImageOfProperty> imagesToAdd = new ArrayList<>();
    private ImageListOfAddPropertyAdapter mImageAdapter;
    private boolean mIsPropertySold = false;
    private long mMillisOfRegisterProperty = 0;
    private long mMillisOfSoldDate = 0;
    private boolean isDateRegister;
    private Location mLocation;

    public AddProperty() {
        // Required empty public constructor
    }

    public static AddProperty newInstance() {
        return new AddProperty();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initViewModel();
        binding = FragmentAddPropertyBinding.inflate(inflater, container, false);
        bindIncludesLayouts();

        setEventListener();
        return binding.getRoot();
    }

    private void initViewModel() {
        ViewModelFactory vmF = Injection.sViewModelFactory(requireActivity());
        mAddPropertyViewModel = new ViewModelProvider(this, vmF).get(AddPropertyViewModel.class);
        mAddPropertyViewModel.init();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        setRecyclerView();
        setPropertyTypeSpinner();
        setAgentSpinner();
        setCreationDate();
        handleSoldDateInput();
        setRecyclerViewObserver();
        setOnPriceInputListener();
    }

    private void setRecyclerViewObserver() {
        mAddPropertyViewModel.getImagesOfProperty().observe(getViewLifecycleOwner(), getImageOfProperty);
    }

    private void unsubscribeRecyclerViewObserver() {
        mAddPropertyViewModel.getImagesOfProperty().removeObserver(getImageOfProperty);
    }

    final Observer<List<ImageOfProperty>> getImageOfProperty = imageOfProperties -> {
        if (imageOfProperties != null) {
            if (imagesToAdd.size() == 0) {
                imagesToAdd.addAll(imageOfProperties);
                setRecyclerView();
            } else {
                imagesToAdd.clear();
                imagesToAdd.addAll(imageOfProperties);
                updateImageAdapter(imageOfProperties);
            }
        }
    };

    private void updateImageAdapter(List<ImageOfProperty> ipsum) {
        mImageAdapter.updateImagesList(ipsum);
    }

    private void setPropertyTypeSpinner() {
        String[] types = AppResources.getPropertyType();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, types);
        binding.addFTypeDropdown.setAdapter(adapter);
    }

    private void setAgentSpinner() {
        String[] agents = AppResources.getAGENTS();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, agents);
        binding.addFAgent.setAdapter(adapter);
    }

    // SET UI
    private void setCreationDate() {
        mMillisOfRegisterProperty = System.currentTimeMillis();
        setDateInputField(Utils.getTodayDate(), 0);
        setListenerDatePicker();
    }

    private void setListenerDatePicker() {
        binding.addFDateSince.setOnClickListener(view -> {
            showDatePickerDialog();
            isDateRegister = true;
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePicker = new DatePickerDialog(
                requireActivity(), this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePicker.show();
    }

    private void handleSoldDateInput() {
        binding.addFDateSoldOn.setEnabled(mIsPropertySold);
    }

    private void setEventListener() {
        binding.addFBtnAddImgCamera.setOnClickListener(view -> takePictureIntent());
        binding.addFBtnAddImgGallery.setOnClickListener(view -> openGallery());
        binding.addFSoldSwitch.setOnClickListener(view -> handleSwitchEvent());
    }

    private void handleSwitchEvent() {
        mIsPropertySold = !mIsPropertySold;
        if (mIsPropertySold) {
            setDateInputField(Utils.getTodayDate(), 1);
            mMillisOfSoldDate = System.currentTimeMillis();
            binding.addFDateSoldOn.setOnClickListener(view -> {
                isDateRegister = false;
                showDatePickerDialog();
            });
        } else {
            setDateInputField(requireActivity().getResources().getString(R.string.add_sold_on), 1);
            mMillisOfSoldDate = 0;
            if (binding.addFDateSoldOn.hasOnClickListeners()) {
                binding.addFDateSoldOn.setOnClickListener(null);
            }
        }
        handleSoldDateInput();
    }

    private void setDateInputField(String date, int mode) {
        if (mode == 1) {
            binding.addFDateSoldOn.setText(date);
        } else {
            binding.addFDateSince.setText(date);
        }
    }

    private void setRecyclerView() {
        imagesRecycler = binding.addFImagesRecycler;
        mImageAdapter = new ImageListOfAddPropertyAdapter(imagesToAdd);
        imagesRecycler.setAdapter(mImageAdapter);
        imagesRecycler.setHasFixedSize(true);
        imagesRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mSimpleCallback);
        itemTouchHelper.attachToRecyclerView(imagesRecycler);
    }

    ItemTouchHelper.SimpleCallback mSimpleCallback = new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END,
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAbsoluteAdapterPosition();
            int toPosition = target.getAbsoluteAdapterPosition();
            Collections.swap(imagesToAdd, fromPosition, toPosition);
            Objects.requireNonNull(imagesRecycler.getAdapter()).notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            mAddPropertyViewModel.removeOneImageOfProperty(mImageAdapter.getImageOfPropertyAt(viewHolder.getAbsoluteAdapterPosition()));
        }
    };

    private void bindIncludesLayouts() {
        View amenitiesView = binding.addFAmenities.getRoot();
        View addressFormView = binding.addFFormAddress.getRoot();
        amenitiesBinding = AmenitiesCheckboxesBinding.bind(amenitiesView);
        formAddressBinding = FormAddressPropertyBinding.bind(addressFormView);
    }

    private void takePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            photoFile = ImageFileUtils.createImageFile();

            if (photoFile != null) {
                Uri fp;
                if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)) {
                    fp = FileProvider.getUriForFile(
                            requireActivity(),
                            "com.openclassrooms.realestatemanager.fileprovider",
                            photoFile);
                } else {
                    fp = Uri.fromFile(photoFile);
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fp);

                try {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    Log.e("ERROR", "takePictureIntent: ", e);
                    // TODO display error state to the user
                }
            }
        }
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Uri imageUri = Uri.fromFile(photoFile);
            //String uri = ImageFilePathUtil.getRealPathFromURI_API19(requireContext(), imageUri);
            mAddPropertyViewModel.createOneImageOfProperty(imageUri.toString());
        } else if (requestCode == PICK_IMAGE_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                mAddPropertyViewModel.createOneImageOfProperty(uri.toString());
            }
        }
    }

    // Add coma to price during put in input
    private void setOnPriceInputListener() {
        binding.addFInputPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {/**/}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {/**/}

            @Override
            public void afterTextChanged(Editable editable) {/**/
                //https://www.google.com/search?client=opera&q=android+editable.setFilters&sourceid=opera&ie=UTF-8&oe=UTF-8
                //https://stackoverflow.com/questions/3349121/how-do-i-use-inputfilter-to-limit-characters-in-an-edittext-in-android
                editable.setFilters(new InputFilter[]{priceFilter});
            }
        });
    }

    private final InputFilter priceFilter = (source, start, end, dest, dstart, dend) -> StringModifier.addComaInPrice(source.toString());

    // after click on save icon, main activity run this function to check if required fields was fill
    // If so, this function run save method
    public void checkFormValidityBeforeSave() {
        int checked = 0;
        final boolean[] requiredValues = new boolean[8];
        // image
        requiredValues[0] = imagesToAdd.size() == 0;
        // type
        requiredValues[1] = binding.addFTypeDropdown.getText().toString().equals("");
        // price
        requiredValues[2] = binding.addFInputPrice.getText().toString().equals("");
        // address1
        requiredValues[3] = Objects.requireNonNull(binding.addFFormAddress.addAddress1FormAddress.getText()).toString().equals("");
        // quarter
        requiredValues[4] = Objects.requireNonNull(binding.addFFormAddress.addAddressFormQuarter.getText()).toString().equals("");
        // city
        requiredValues[5] = Objects.requireNonNull(binding.addFFormAddress.addAddressFormCity.getText()).toString().equals("");
        // postalCode
        requiredValues[6] = Objects.requireNonNull(binding.addFFormAddress.addAddressFormPostalCode.getText()).toString().equals("");
        // agent
        requiredValues[7] = binding.addFAgent.getText().toString().equals("");

        for (boolean value : requiredValues) {
            if (value) checked++;
        }

        if (checked == 0) {
            mAddPropertyViewModel.setImagesOfPropertyList(mImageAdapter.getImageOfPropertyList());
            if (Utils.isInternetAvailable(requireContext())) {
                getGeoLocationOfProperty();
            } else {
                createProperty();
            }
        } else {
            String msg = requireActivity().getResources().getString(R.string.warning_missing_fields);
            NotifyBySnackBar.showSnackBar(1, mView, msg);
        }
    }

    private void getGeoLocationOfProperty() {
        String address1 = formAddressBinding.addAddress1FormAddress.getEditableText().toString();
        String city = formAddressBinding.addAddressFormCity.getEditableText().toString();
        String quarter = formAddressBinding.addAddressFormQuarter.getEditableText().toString();
        String address = StringModifier.formatAddressToGeocoding(address1, city, quarter);
        mAddPropertyViewModel.getLocationFromAddress(address);
        setOnResponseObserver();
    }

    private void setOnResponseObserver() {
        mAddPropertyViewModel.getGeoLocationOfProperty().observe(getViewLifecycleOwner(), location -> {
            if (location != null) {
                mLocation = location;
                createProperty();
            }
        });
    }

    public void createProperty() {
        // Get inputs values:
        String type = binding.addFTypeDropdown.getText().toString();
        String desc = binding.addFDescription.getEditableText().toString();
        String surfaceStr = binding.addFInputSurface.getText().toString();
        int surface = TextUtils.isEmpty(surfaceStr) ? 0 : Integer.parseInt(surfaceStr);
        String priceStr = binding.addFInputPrice.getText().toString();
        int price = TextUtils.isEmpty(priceStr) ? 0 : Integer.parseInt(priceStr);
        String roomsStr = binding.addFInputRooms.getText().toString();
        int rooms = TextUtils.isEmpty(roomsStr) ? 0 : Integer.parseInt(roomsStr);
        String bedroomsStr = binding.addFInputBedrooms.getText().toString();
        int bedrooms = TextUtils.isEmpty(bedroomsStr) ? 0 : Integer.parseInt(bedroomsStr);
        String bathroomsStr = binding.addFInputBathrooms.getText().toString();
        int bathrooms = TextUtils.isEmpty(bathroomsStr) ? 0 : Integer.parseInt(bathroomsStr);
        String address1 = formAddressBinding.addAddress1FormAddress.getEditableText().toString();
        String address2 = formAddressBinding.addAddress2FormAddressSuite.getEditableText().toString();
        String city = formAddressBinding.addAddressFormCity.getEditableText().toString();
        String quarter = formAddressBinding.addAddressFormQuarter.getEditableText().toString();
        String location = mLocation == null ? "" : formatLocationInString();
        String postalCodeStr = formAddressBinding.addAddressFormPostalCode.getEditableText().toString();
        int postalCode = TextUtils.isEmpty(postalCodeStr) ? 0 : Integer.parseInt(postalCodeStr);
        String amenities = getAmenities();
        String agent = binding.addFAgent.getText().toString();
        String dateR = String.valueOf(mMillisOfRegisterProperty);
        String dateSold = mMillisOfSoldDate == 0 ? "" : String.valueOf(mMillisOfSoldDate);
        mAddPropertyViewModel.createNewProperty(type, desc, surface, price, rooms, bedrooms, bathrooms, dateR, dateSold, address1, address2, city, quarter, postalCode, location, amenities, agent);
        saveDataAndNotifyUser();
    }

    private String formatLocationInString() {
        return String.valueOf(mLocation.getLat()) + "," + String.valueOf(mLocation.getLng());
    }

    private void saveDataAndNotifyUser() {
        mAddPropertyViewModel.createProperty();
        mAddPropertyViewModel.getCreatePropertyResponse().observe(getViewLifecycleOwner(), createPropertyObserver);
    }

    private void saveImagesOfProperty() {
        mAddPropertyViewModel.saveImagesOfProperty();
        mAddPropertyViewModel.getSaveImagesResponse().observe(getViewLifecycleOwner(), saveImagesObserver);
    }

    final Observer<long[]> saveImagesObserver = longs -> {
        boolean res = readSaveImageResponse(longs);
        if (res) {
            NotificationsUtils notify = new NotificationsUtils(requireContext());
            notify.showWarning(requireContext(), SAVE_IMAGES_FAIL);
        } else {
            mAddPropertyViewModel.resetImageOfProperty();
            unsubscribeSavePropertyObserver();
            goBackToList();
        }
    };

    final Observer<Long> createPropertyObserver = aLong -> {
        boolean res = aLong == -1;
        NotificationsUtils notify = new NotificationsUtils(requireContext());
        if (res) {
            notify.showWarning(requireContext(), SAVE_PROPERTY_FAIL);
        } else {
            notify.showWarning(requireContext(), SAVE_PROPERTY_OK);
            saveImagesOfProperty();
        }
    };
    private void unsubscribeSaveImagesObserver() {
        mAddPropertyViewModel.getSaveImagesResponse().removeObserver(saveImagesObserver);
    }

    private void unsubscribeSavePropertyObserver() {
        mAddPropertyViewModel.getCreatePropertyResponse().removeObserver(createPropertyObserver);
    }

    private boolean readSaveImageResponse(long[] longs) {
        for (long i : longs) {
            if (i == 0) {
                return true;
            }
        }
        return false;
    }

    private void goBackToList() {
        MainActivity ma = (MainActivity) requireActivity();
        ma.onBackPressed();
    }

    private String getAmenities() {
        String[] amenities = new String[6];
        amenities[0] = amenitiesBinding.amenitiesShop.isChecked() ? SHOP : NULL;
        amenities[1] = amenitiesBinding.amenitiesPark.isChecked() ? PARK : NULL;
        amenities[2] = amenitiesBinding.amenitiesPlayground.isChecked() ? PLAYGROUND : NULL;
        amenities[3] = amenitiesBinding.amenitiesSchool.isChecked() ? SCHOOL : NULL;
        amenities[4] = amenitiesBinding.amenitiesBus.isChecked() ? BUS : NULL;
        amenities[5] = amenitiesBinding.amenitiesSubway.isChecked() ? SUBWAY : NULL;
        return StringModifier.arrayToSingleString(amenities);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, i);
        calendar.set(Calendar.MONTH, i1);
        calendar.set(Calendar.DAY_OF_MONTH, i2);
        Date date = calendar.getTime();
        if (isDateRegister) {
            mMillisOfRegisterProperty = calendar.getTimeInMillis();
            setDateInputField(Utils.getUSFormatOfDate(date), 0);
        } else {
            mMillisOfSoldDate = calendar.getTimeInMillis();
            if (mMillisOfSoldDate >= mMillisOfRegisterProperty) {
                setDateInputField(Utils.getUSFormatOfDate(date), 1);
            } else {
                String msg = requireActivity().getResources().getString(R.string.warning_date_sold_smaller_than_register);
                NotifyBySnackBar.showSnackBar(1, mView, msg);
            }
        }
    }

    @Override
    public void onDestroyView() {
        formAddressBinding = null;
        amenitiesBinding = null;
        binding = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (mAddPropertyViewModel.getImagesOfProperty().hasActiveObservers())
            unsubscribeRecyclerViewObserver();
        if (mAddPropertyViewModel.getSaveImagesResponse().hasActiveObservers()) unsubscribeSaveImagesObserver();
        mAddPropertyViewModel.disposeDisposable();
        super.onDestroy();
    }
}