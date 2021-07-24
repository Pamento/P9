package com.openclassrooms.realestatemanager.ui.fragments;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty;
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty;
import com.openclassrooms.realestatemanager.data.viewModelFactory.ViewModelFactory;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.EditPropertyViewModel;
import com.openclassrooms.realestatemanager.databinding.AmenitiesCheckboxesBinding;
import com.openclassrooms.realestatemanager.databinding.FormAddressPropertyBinding;
import com.openclassrooms.realestatemanager.databinding.FragmentAddPropertyBinding;
import com.openclassrooms.realestatemanager.injection.Injection;
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
import static com.openclassrooms.realestatemanager.util.Constants.BUS;
import static com.openclassrooms.realestatemanager.util.Constants.NULL;
import static com.openclassrooms.realestatemanager.util.Constants.PARK;
import static com.openclassrooms.realestatemanager.util.Constants.PICK_IMAGE_GALLERY;
import static com.openclassrooms.realestatemanager.util.Constants.PLAYGROUND;
import static com.openclassrooms.realestatemanager.util.Constants.REQUEST_IMAGE_CAPTURE;
import static com.openclassrooms.realestatemanager.util.Constants.SAVE_IMAGES_FAIL;
import static com.openclassrooms.realestatemanager.util.Constants.SAVE_PROPERTY_FAIL;
import static com.openclassrooms.realestatemanager.util.Constants.SAVE_PROPERTY_OK;
import static com.openclassrooms.realestatemanager.util.Constants.SCHOOL;
import static com.openclassrooms.realestatemanager.util.Constants.SHOP;
import static com.openclassrooms.realestatemanager.util.Constants.SUBWAY;

public class EditProperty extends Fragment implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "EditProperty";
    private EditPropertyViewModel mEditPropertyViewModel;
    private FragmentAddPropertyBinding binding;
    private AmenitiesCheckboxesBinding amenitiesBinding;
    private FormAddressPropertyBinding formAddressBinding;
    private View mView;
    private RecyclerView imagesRecycler;
    private ImageListOfAddPropertyAdapter mImageAdapter;
    private SingleProperty mSingleProperty;
    private final List<ImageOfProperty> mImageOfPropertyList = new ArrayList<>();
    private int mMillisOfRegisterProperty = 0;
    private File photoFile;

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
        binding.addFTypeDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //adapterView.getItemAtPosition(i);
                Log.i(TAG, "onItemSelected: item selected:: " + adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { /**/ }
        });
    }

    private void setAgentSpinner() {
        String[] agents = AppResources.getAGENTS();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, agents);
        binding.addFAgent.setAdapter(adapter);
        binding.addFAgent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(TAG, "onItemSelected: AGENT SELECTED");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {/**/}
        });
    }

    private void setPropertyDataObservers() {
        mEditPropertyViewModel.getSingleProperty()
                .observe(getViewLifecycleOwner(), singleProperty -> {
                    if (singleProperty!=null) {
                        mSingleProperty = singleProperty;
                        updateUi();
                        updateUIAddressForm();
                        updateUIAmenities();
                    }
                    setListenerDatePicker();
                });
        mEditPropertyViewModel.getImagesOfProperty().observe(getViewLifecycleOwner(), imageOfProperties -> {
            mImageOfPropertyList.addAll(imageOfProperties);
            setRecyclerView();
            setEventListener();
        });
    }

    private void updateUi() {
        if (mSingleProperty.getType() != null)
            binding.addFTypeDropdown.setText(mSingleProperty.getType());
        if (mSingleProperty.getDescription() != null)
            binding.addFDescription.setText(mSingleProperty.getDescription());
        if (mSingleProperty.getSurface() != null)
            binding.addFInputSurface.setText(mSingleProperty.getSurface());
        if (mSingleProperty.getPrice() != null)
            binding.addFInputPrice.setText(mSingleProperty.getPrice());
        if (mSingleProperty.getRooms() != null)
            binding.addFInputRooms.setText(mSingleProperty.getRooms());
        if (mSingleProperty.getBedroom() != null)
            binding.addFInputBedrooms.setText(mSingleProperty.getBedroom());
        if (mSingleProperty.getBathroom() != null)
            binding.addFInputBathrooms.setText(mSingleProperty.getBathroom());
        if (mSingleProperty.getDateRegister() != null) {
            String dateRegister = SQLTimeHelper.getUSFormDateFromTimeInMillis(mSingleProperty.getDateRegister());
            binding.addFDateSince.setText(dateRegister);
        }
        if (mSingleProperty.getDateSold() != null) {
            binding.addFSoldSwitch.setChecked(true);
            String dateSold = SQLTimeHelper.getUSFormDateFromTimeInMillis(mSingleProperty.getDateSold());
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
            formAddressBinding.addAddressFormPostalCode.setText(mSingleProperty.getPostalCode());
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
    }

    private void setRecyclerView() {
        Log.i(TAG, "ADD__ setRecyclerView: imageToAdd.size():: " + mImageOfPropertyList.size());
        imagesRecycler = binding.addFImagesRecycler;
        mImageAdapter = new ImageListOfAddPropertyAdapter(mImageOfPropertyList);
        imagesRecycler.setAdapter(mImageAdapter);
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
            Collections.swap(mImageOfPropertyList, fromPosition, toPosition);
            // TODO? updateImageAdapter();
            Objects.requireNonNull(imagesRecycler.getAdapter()).notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            ImageOfProperty imageOfProperty = mImageAdapter.getImageOfPropertyAt(viewHolder.getAbsoluteAdapterPosition());
            // TODO swiped image don't necessary make part of mImageOfPropertyList.
            //  Before, need to check if swiped/deleted image is in
            if (mImageOfPropertyList.contains(imageOfProperty)) {
                mImageOfPropertyList.remove(imageOfProperty);
                mEditPropertyViewModel.deleteImageOfProperty(imageOfProperty.getId());
            }
            mImageAdapter.removeDeletedImageFromList(imageOfProperty);
        }
    };

    private void takePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            photoFile = ImageFileUtils.createImageFile();

            if (photoFile != null) {
                Uri fp = FileProvider.getUriForFile(
                        requireActivity(),
                        "com.openclassrooms.realestatemanager.fileprovider",
                        photoFile);
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
        mImageOfPropertyList.add(ip);
        mEditPropertyViewModel.createImageOfProperty(ip);
    }

    private void setListenerDatePicker() {
        binding.addFDateSince.setOnClickListener(view -> showDatePickerDialog());
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

    private void setDateInputField(String date) {
        binding.addFDateSince.setText(date);
    }

    public void updateProperty() {
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
        if (!TextUtils.isEmpty(bedrooms)) mSingleProperty.setRooms(Integer.parseInt(bedrooms));
        String bathrooms = binding.addFInputBathrooms.getText().toString();
        if (!TextUtils.isEmpty(bedrooms)) mSingleProperty.setRooms(Integer.parseInt(bathrooms));
        String address1 = formAddressBinding.addAddress1FormAddress.getEditableText().toString();
        if (!address1.equals("")) mSingleProperty.setAddress1(address1);
        String address2 = formAddressBinding.addAddress2FormAddressSuite.getEditableText().toString();
        if (!address2.equals("")) mSingleProperty.setAddress2(address2);
        String city = formAddressBinding.addAddressFormCity.getEditableText().toString();
        if (!city.equals("")) mSingleProperty.setCity(city);
        String quarter = formAddressBinding.addAddressFormQuarter.getEditableText().toString();
        if (!quarter.equals("")) mSingleProperty.setQuarter(quarter);
        String postalCode = formAddressBinding.addAddressFormPostalCode.getEditableText().toString();
        if (!TextUtils.isEmpty(bedrooms)) mSingleProperty.setRooms(Integer.parseInt(postalCode));
        String amenities = getAmenities();
        mSingleProperty.setAmenities(amenities);
        if (mMillisOfRegisterProperty > 0)
            mSingleProperty.setDateRegister(mMillisOfRegisterProperty);
        String agent = binding.addFAgent.getText().toString();
        if (!agent.equals("")) mSingleProperty.setAgent(agent);
        saveDataAndNotifyUser();
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
        Log.i(TAG, "saveDataAndNotifyUser: res & resImg:: " + res + " _:: ");
        NotificationsUtils notify = new NotificationsUtils(requireContext());
        // fail if res = true
        // success if res = false
        if (res)
            notify.showWarning(requireContext(), SAVE_PROPERTY_FAIL);
        else
            notify.showWarning(requireContext(), SAVE_PROPERTY_OK);
    }

    private void saveUpdatedImagesOfProperty() {
        int errorRes = 0;
        List<ImageOfProperty> imageOfPropertiesFromAdapter = mImageAdapter.getImageOfPropertyList();
        for (ImageOfProperty image: imageOfPropertiesFromAdapter) {
            boolean res = mEditPropertyViewModel.updateImageOfProperty(image);
            if (res) errorRes++;
        }
        if (errorRes != 0) {
            NotifyBySnackBar.showSnackBar(1,mView,SAVE_IMAGES_FAIL);
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, i);
        calendar.set(Calendar.MONTH, i1);
        calendar.set(Calendar.DAY_OF_MONTH, i2);
        Date date = calendar.getTime();
        mMillisOfRegisterProperty = (int) calendar.getTimeInMillis();
        setDateInputField(Utils.getUSFormatOfDate(date));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        amenitiesBinding = null;
        formAddressBinding = null;
        binding = null;
    }
}