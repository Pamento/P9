package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty;
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty;
import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository;
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository;
import com.openclassrooms.realestatemanager.data.remote.models.geocode.Location;
import com.openclassrooms.realestatemanager.data.remote.models.geocode.Result;
import com.openclassrooms.realestatemanager.data.remote.repository.GoogleMapsRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executor;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddPropertyViewModel extends ViewModel {
    private static final String TAG = "AddProperty";
    private final PropertiesRepository mPropertiesRepository;
    private final ImageRepository mImageRepository;
    private final GoogleMapsRepository mGoogleMapsRepository;
    private final Executor mExecutor;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private SingleProperty mSingleProperty;
    private final List<ImageOfProperty> mImagesOfPropertyList = new ArrayList<>();
    private MutableLiveData<List<ImageOfProperty>> mImagesOfProperty;
    private ImageOfProperty mImgOfProperty;
    private MutableLiveData<com.openclassrooms.realestatemanager.data.remote.models.geocode.Location> mLocationOfAddress;

    public AddPropertyViewModel(PropertiesRepository propertiesRepository,
                                ImageRepository imageRepository,
                                GoogleMapsRepository googleMapsRepository,
                                Executor executor) {
        mPropertiesRepository = propertiesRepository;
        mImageRepository = imageRepository;
        mGoogleMapsRepository = googleMapsRepository;
        mExecutor = executor;
    }

    public void init() {
        mSingleProperty = new SingleProperty();
        mSingleProperty.setId(UUID.randomUUID().toString());
        mImagesOfProperty = new MutableLiveData<>();
        mLocationOfAddress = new MutableLiveData<>();
    }

    public SingleProperty getSingleProperty() {
        return mSingleProperty;
    }

    public void setSingleProperty(SingleProperty singleProperty) {
        mSingleProperty = singleProperty;
    }

    public void setImgOfProperty(ImageOfProperty imgOfProperty) {
        mImgOfProperty = imgOfProperty;
    }

    public void createNewProperty(String type,
                                  String description,
                                  Integer surface,
                                  Integer price,
                                  Integer rooms,
                                  Integer bedroom,
                                  Integer bathroom,
                                  String dateRegister,
                                  String dateSold,
                                  String address1,
                                  String address2,
                                  String city,
                                  String quarter,
                                  Integer postalCode,
                                  String location,
                                  String amenities,
                                  String agent) {
        mSingleProperty.setType(type.equals("") ? null : type);
        mSingleProperty.setDescription(description.equals("") ? null : description);
        mSingleProperty.setSurface(surface);
        mSingleProperty.setPrice(price);
        mSingleProperty.setRooms(rooms);
        mSingleProperty.setBedroom(bedroom);
        mSingleProperty.setBathroom(bathroom);
        mSingleProperty.setDateRegister(dateRegister);
        mSingleProperty.setDateSold(dateSold);
        mSingleProperty.setAddress1(address1);
        mSingleProperty.setAddress2(address2);
        mSingleProperty.setQuarter(quarter.equals("") ? null : quarter);
        mSingleProperty.setCity(city.equals("") ? null : city);
        mSingleProperty.setPostalCode(postalCode);
        mSingleProperty.setLocation(location);
        mSingleProperty.setAmenities(amenities.equals("") ? null : amenities);
        mSingleProperty.setAgent(agent);
    }

    public void createOneImageOfProperty(String imageUri) {
        mImgOfProperty = new ImageOfProperty();
        mImgOfProperty.setPropertyId(mSingleProperty.getId());
        mImgOfProperty.setPath(imageUri);
        Log.i(TAG, "createOneImageOfProperty: imageUri:: " + imageUri);
        mImagesOfPropertyList.add(mImgOfProperty);
        mImagesOfProperty = new MutableLiveData<>();
        mImagesOfProperty.postValue(mImagesOfPropertyList);
            mImgOfProperty = null;
    }

    public void removeOneImageOfProperty(ImageOfProperty imageOfProperty) {
        mImagesOfPropertyList.remove(imageOfProperty);
        mImagesOfProperty = new MutableLiveData<>();
        mImagesOfProperty.postValue(mImagesOfPropertyList);
    }

//    public void addDescriptionToImage(int position, String description) {
//        ImageOfProperty iOP = mImagesOfPropertyList.get(position);
//        iOP.setDescription(description);
//        mImagesOfPropertyList.set(position, iOP);
//        mImagesOfProperty.setValue(mImagesOfPropertyList);
//    }

    public void setImagesOfPropertyList(List<ImageOfProperty> imagesOfPropertyList) {
        mImagesOfPropertyList.addAll(imagesOfPropertyList);
    }

    public LiveData<List<ImageOfProperty>> getImagesOfProperty() {
        return mImagesOfProperty;
    }



    public void getLocationFromAddress(String address) {
        mGoogleMapsRepository.getGoogleGeoCodeOfAddress(address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Result>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull Result result) {
                        mLocationOfAddress.setValue(result.getGeometry().getLocation());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {/**/}

                    @Override
                    public void onComplete() {/**/}
                });
    }

    public LiveData<Location> getGeoLocationOfProperty() {
        return mLocationOfAddress;
    }

    // Save data
    public boolean createSingleProperty() {
        final boolean[] response = new boolean[1];
        mExecutor.execute(() -> {
            long res = mPropertiesRepository.createSingleProperty(mSingleProperty);
            Log.i(TAG, "run: createSingleProperty::rowId -> response:: " + res);
            response[0] = res == -1;
        });
        return response[0];
    }

    // Save list of images
    public boolean createImagesOfProperty() {
        // If true, the insert method was felt
        final boolean[] response = new boolean[1];
        mExecutor.execute(() -> {
            long[] res = mImageRepository.createPropertyImages(mImagesOfProperty.getValue());
            Log.i(TAG, "createImagesOfProperty: RUN:-> rowID:: " + Arrays.toString(res));
            for (long i: res) {
                if (i == 0) {
                    response[0] = true;
                    break;
                }
            }
            response[0] = false;
        });
        return response[0];
    }

    // Save single image
    public boolean createImageOfProperty() {
        final boolean[] response = new boolean[1];
        mExecutor.execute(() -> {
            long res = mImageRepository.createPropertyImage(Objects.requireNonNull(mImagesOfProperty.getValue()).get(0));
            response[0] = res == -1;
        });
        return response[0];
    }
}
