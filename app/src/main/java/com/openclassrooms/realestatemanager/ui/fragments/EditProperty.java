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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty;
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty;
import com.openclassrooms.realestatemanager.data.remote.models.geocode.Location;
import com.openclassrooms.realestatemanager.data.viewModelFactory.ViewModelFactory;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.EditPropertyViewModel;
import com.openclassrooms.realestatemanager.databinding.AmenitiesCheckboxesBinding;
import com.openclassrooms.realestatemanager.databinding.FormAddressPropertyBinding;
import com.openclassrooms.realestatemanager.databinding.FragmentAddPropertyBinding;
import com.openclassrooms.realestatemanager.injection.Injection;
import com.openclassrooms.realestatemanager.ui.activity.MainActivity;
import com.openclassrooms.realestatemanager.ui.adapters.ImageListOfAddPropertyAdapter;
import com.openclassrooms.realestatemanager.util.Utils;
import com.openclassrooms.realestatemanager.util.dateTime.SQLTimeHelper;
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

public class EditProperty extends Fragment implements DatePickerDialog.OnDateSetListener {

    private EditPropertyViewModel mEditPropertyViewModel;
    private FragmentAddPropertyBinding binding;
    private AmenitiesCheckboxesBinding amenitiesBinding;
    private FormAddressPropertyBinding formAddressBinding;
    private View mView;
    private RecyclerView imagesRecycler;
    private ImageListOfAddPropertyAdapter mImageAdapter;
    private SingleProperty mSingleProperty;
    private final List<ImageOfProperty> mImageOfPropertyList = new ArrayList<>();
    private final List<ImageOfProperty> mImageOfPropertyListToCompare = new ArrayList<>();
    private boolean mIsPropertySold = false;
    private long mMillisOfRegisterProperty = 0;
    private long mMillisOfSoldDate = 0;
    private File photoFile;
    private Location mLocation;
    private boolean isDateRegister;

    public EditProperty() {
        // Required empty public constructor
    }

    public static EditProperty newInstance() {
        return new EditProperty();
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
        setRecyclerView();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        setPropertyDataObservers();
        setPropertyTypeSpinner();
        setAgentSpinner();
    }

    private void initViewModel() {
        ViewModelFactory vmF = Injection.sViewModelFactory(requireActivity());
        mEditPropertyViewModel = new ViewModelProvider(requireActivity(), vmF).get(EditPropertyViewModel.class);
    }

    private void bindIncludesLayouts() {
        View amenitiesView = binding.addFAmenities.getRoot();
        View addressFormView = binding.addFFormAddress.getRoot();
        amenitiesBinding = AmenitiesCheckboxesBinding.bind(amenitiesView);
        formAddressBinding = FormAddressPropertyBinding.bind(addressFormView);
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

    private void setPropertyDataObservers() {
        mEditPropertyViewModel.getSingleProperty().observe(getViewLifecycleOwner(), getProperty);
        mEditPropertyViewModel.getImagesOfProperty().observe(getViewLifecycleOwner(), getImagesOfProperty);
    }

    private void unsubscribeDataObservers() {
        mEditPropertyViewModel.getSingleProperty().removeObserver(getProperty);
        mEditPropertyViewModel.getImagesOfProperty().removeObserver(getImagesOfProperty);
    }

    final Observer<SingleProperty> getProperty = new Observer<SingleProperty>() {
        @Override
        public void onChanged(SingleProperty singleProperty) {
            if (singleProperty != null) {
                mSingleProperty = singleProperty;
                updateUi();
                updateUIAddressForm();
                updateUIAmenities();
            }
            setListenerDatePicker();
        }
    };
    final Observer<List<ImageOfProperty>> getImagesOfProperty = imagesOfProperty -> {
        mImageOfPropertyListToCompare.addAll(imagesOfProperty);
        mImageOfPropertyList.clear();
        mImageOfPropertyList.addAll(imagesOfProperty);
        displayImagesOnRecyclerView();
        setEventListener();
    };

    private void updateUi() {
        if (mSingleProperty.getType() != null)
            binding.addFTypeDropdown.setText(mSingleProperty.getType());
        if (mSingleProperty.getDescription() != null)
            binding.addFDescription.setText(mSingleProperty.getDescription());
        if (mSingleProperty.getSurface() != null)
            binding.addFInputSurface.setText(String.valueOf(mSingleProperty.getSurface()));
        if (mSingleProperty.getPrice() != null)
            binding.addFInputPrice.setText(String.valueOf(mSingleProperty.getPrice()));
        if (mSingleProperty.getRooms() != null)
            binding.addFInputRooms.setText(String.valueOf(mSingleProperty.getRooms()));
        if (mSingleProperty.getBedroom() != null)
            binding.addFInputBedrooms.setText(String.valueOf(mSingleProperty.getBedroom()));
        if (mSingleProperty.getBathroom() != null)
            binding.addFInputBathrooms.setText(String.valueOf(mSingleProperty.getBathroom()));
        if (mSingleProperty.getDateRegister() != null) {
            String dateRegister = SQLTimeHelper.getUSFormDateFromTimeInMillis(Long.parseLong(mSingleProperty.getDateRegister()));
            binding.addFDateSince.setText(dateRegister);
        }
        if (!mSingleProperty.getDateSold().equals("")) {
            binding.addFSoldSwitch.setChecked(true);
            String dateSold = SQLTimeHelper.getUSFormDateFromTimeInMillis(Long.parseLong(mSingleProperty.getDateSold()));
            binding.addFDateSoldOn.setText(dateSold);
            binding.addFDateSoldOn.setEnabled(false);
        }
        if (mSingleProperty.getAgent() != null)
            binding.addFAgent.setText(mSingleProperty.getAgent());
    }

    private void updateUIAddressForm() {
        if (mSingleProperty.getAddress1() != null)
            formAddressBinding.addAddress1FormAddress.setText(mSingleProperty.getAddress1());
        if (mSingleProperty.getAddress2() != null)
            formAddressBinding.addAddress2FormAddressSuite.setText(mSingleProperty.getAddress2());
        if (mSingleProperty.getCity() != null)
            formAddressBinding.addAddressFormCity.setText(mSingleProperty.getCity());
        if (mSingleProperty.getQuarter() != null)
            formAddressBinding.addAddressFormQuarter.setText(mSingleProperty.getQuarter());
        if (mSingleProperty.getPostalCode() != null)
            formAddressBinding.addAddressFormPostalCode.setText(String.valueOf(mSingleProperty.getPostalCode()));
    }

    private void updateUIAmenities() {
        String amenities = mSingleProperty.getAmenities();
        if (amenities.contains(SHOP)) binding.addFAmenities.amenitiesShop.setChecked(true);
        if (amenities.contains(PARK)) binding.addFAmenities.amenitiesPark.setChecked(true);
        if (amenities.contains(PLAYGROUND))
            binding.addFAmenities.amenitiesPlayground.setChecked(true);
        if (amenities.contains(SCHOOL)) binding.addFAmenities.amenitiesSchool.setChecked(true);
        if (amenities.contains(BUS)) binding.addFAmenities.amenitiesBus.setChecked(true);
        if (amenities.contains(SUBWAY)) binding.addFAmenities.amenitiesSubway.setChecked(true);
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

    private void handleSoldDateInput() {
        binding.addFDateSoldOn.setEnabled(mIsPropertySold);
    }

    private void setRecyclerView() {
        imagesRecycler = binding.addFImagesRecycler;
        imagesRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mSimpleCallback);
        itemTouchHelper.attachToRecyclerView(imagesRecycler);
    }

    private void displayImagesOnRecyclerView() {
        mImageAdapter = new ImageListOfAddPropertyAdapter(mImageOfPropertyList);
        imagesRecycler.setAdapter(mImageAdapter);
    }

    ItemTouchHelper.SimpleCallback mSimpleCallback = new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END,
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAbsoluteAdapterPosition();
            int toPosition = target.getAbsoluteAdapterPosition();
            Collections.swap(mImageOfPropertyList, fromPosition, toPosition);
            Objects.requireNonNull(imagesRecycler.getAdapter()).notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            //ImageOfProperty imageOfProperty = mImageAdapter.getImageOfPropertyAt(viewHolder.getAbsoluteAdapterPosition());
            mImageAdapter.removeDeletedImageFromList(viewHolder.getAbsoluteAdapterPosition());
        }
    };

    private void takePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            photoFile = ImageFileUtils.createImageFile();

            if (photoFile != null) {
                Uri fp;
                if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)){
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
                }
            }
        }
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Uri imageUri = Uri.fromFile(photoFile);
            createNewImageOfProperty(imageUri.toString());
        } else if (requestCode == PICK_IMAGE_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                createNewImageOfProperty(uri.toString());
            }
        }
    }

    private void createNewImageOfProperty(String imageUri) {
        ImageOfProperty ip = new ImageOfProperty();
        ip.setPath(imageUri);
        ip.setPropertyId(mSingleProperty.getId());
        mImageAdapter.addNewImage(ip);
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

    private void getGeoLocationOfProperty() {
        String address1 = formAddressBinding.addAddress1FormAddress.getEditableText().toString();
        String city = formAddressBinding.addAddressFormCity.getEditableText().toString();
        String quarter = formAddressBinding.addAddressFormQuarter.getEditableText().toString();
        String address = StringModifier.formatAddressToGeocoding(address1,city,quarter);
        mEditPropertyViewModel.getLocationFromAddress(address);
        setOnResponseObserver();
    }

    private void setOnResponseObserver() {
        mEditPropertyViewModel.getGeoLocationOfProperty().observe(getViewLifecycleOwner(), getLocation);
    }

    private void unsubscribeGetLocation() {
        mEditPropertyViewModel.getGeoLocationOfProperty().removeObserver(getLocation);
    }

    final Observer<Location> getLocation = new Observer<Location>() {
        @Override
        public void onChanged(Location location) {
            if (location != null) {
                mLocation = location;
                updateImagesOfProperty();
                unsubscribeGetLocation();
            }
        }
    };

    public void updateProperty() {
        mEditPropertyViewModel.getSingleProperty().removeObserver(getProperty);
        mEditPropertyViewModel.getImagesOfProperty().removeObserver(getImagesOfProperty);
        if (Utils.isInternetAvailable(requireContext())) {
            getGeoLocationOfProperty();
        } else {
            updateImagesOfProperty();
        }
    }

    private void updatePropertyData() {
        // Get inputs values:
        String type = binding.addFTypeDropdown.getText().toString();
        if (!type.equals("")) mSingleProperty.setType(type);
        String desc = binding.addFDescription.getEditableText().toString();
        if (!desc.equals("")) mSingleProperty.setDescription(desc);
        String surface = binding.addFInputSurface.getText().toString();
        if (!TextUtils.isEmpty(surface)) mSingleProperty.setSurface(Integer.parseInt(surface));
        String price = binding.addFInputPrice.getText().toString();
        if (!TextUtils.isEmpty(price)) mSingleProperty.setPrice(Integer.parseInt(price));
        String rooms = binding.addFInputRooms.getText().toString();
        if (!TextUtils.isEmpty(rooms)) mSingleProperty.setRooms(Integer.parseInt(rooms));
        String bedrooms = binding.addFInputBedrooms.getText().toString();
        if (!TextUtils.isEmpty(bedrooms)) mSingleProperty.setBedroom(Integer.parseInt(bedrooms));
        String bathrooms = binding.addFInputBathrooms.getText().toString();
        if (!TextUtils.isEmpty(bathrooms)) mSingleProperty.setBathroom(Integer.parseInt(bathrooms));
        String address1 = formAddressBinding.addAddress1FormAddress.getEditableText().toString();
        if (!address1.equals("")) mSingleProperty.setAddress1(address1);
        String address2 = formAddressBinding.addAddress2FormAddressSuite.getEditableText().toString();
        if (!address2.equals("")) mSingleProperty.setAddress2(address2);
        String city = formAddressBinding.addAddressFormCity.getEditableText().toString();
        if (!city.equals("")) mSingleProperty.setCity(city);
        String quarter = formAddressBinding.addAddressFormQuarter.getEditableText().toString();
        if (!quarter.equals("")) mSingleProperty.setQuarter(quarter);
        String postalCode = formAddressBinding.addAddressFormPostalCode.getEditableText().toString();
        if (!TextUtils.isEmpty(bedrooms)) mSingleProperty.setPostalCode(Integer.parseInt(postalCode));
        mSingleProperty.setLocation(mLocation == null ? "" :  formatLocationInString());
        mSingleProperty.setAmenities(getAmenities());
        if (mMillisOfRegisterProperty > 0)
            mSingleProperty.setDateRegister(String.valueOf(mMillisOfRegisterProperty));
        if (binding.addFSoldSwitch.isChecked() && mMillisOfSoldDate > 0) {
            mSingleProperty.setDateSold(String.valueOf(mMillisOfSoldDate));
        }
        String agent = binding.addFAgent.getText().toString();
        if (!agent.equals("")) mSingleProperty.setAgent(agent);
        saveDataAndNotifyUser();
    }

    private String formatLocationInString() {
        return String.valueOf(mLocation.getLat()) + "," + String.valueOf(mLocation.getLng());
    }

    private void updateImagesOfProperty() {
        int i;
        int errorRes = 0;
        List<Integer> index = new ArrayList<>();

        List<ImageOfProperty> temp = mImageAdapter.getImageOfPropertyList();
        for (ImageOfProperty ip: temp) {
            if (ip.getId() == null) {
                boolean res = mEditPropertyViewModel.createImageOfProperty(ip);
                if (res) {
                    errorRes += 1;
                }                
            } else {
                index.add(mImageOfPropertyListToCompare.indexOf(ip));
                boolean res = mEditPropertyViewModel.updateImageOfProperty(ip);
                if (res) {
                    errorRes += 1;
                }    
            }
        }
        if (index.size() < mImageOfPropertyListToCompare.size()) {
            i = mImageOfPropertyListToCompare.size();
            for (int k = 0; k < i; k++ ) {
                if (!index.contains(k)) {
                    mEditPropertyViewModel.deleteImageOfProperty(mImageOfPropertyListToCompare.get(k).getId());
                }
            }
        }
        if (errorRes != 0) {
            NotifyBySnackBar.showSnackBar(1, mView, SAVE_IMAGES_FAIL);
        }
        updatePropertyData();
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

    // Handle data
    private void saveDataAndNotifyUser() {
        boolean res = mEditPropertyViewModel.updateProperty(mSingleProperty);
        NotificationsUtils notify = new NotificationsUtils(requireContext());
        // fail if res = true
        // success if res = false
        if (res)
            notify.showWarning(requireContext(), SAVE_PROPERTY_FAIL);
        else
            notify.showWarning(requireContext(), SAVE_PROPERTY_OK);

        goBackToList();
    }

    private void goBackToList() {
        MainActivity ma = (MainActivity) requireActivity();
        ma.onBackPressed();
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
        amenitiesBinding = null;
        formAddressBinding = null;
        binding = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (mEditPropertyViewModel.getGeoLocationOfProperty().hasActiveObservers()) unsubscribeGetLocation();
        unsubscribeDataObservers();
        super.onDestroy();
    }
}