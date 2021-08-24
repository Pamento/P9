package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

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

import java.util.List;
import java.util.concurrent.Executor;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class EditPropertyViewModel extends ViewModel {

    private final PropertiesRepository mPropertiesRepository;
    private final ImageRepository mImageRepository;
    private final GoogleMapsRepository mGoogleMapsRepository;
    private final Executor mExecutor;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private final MutableLiveData<Location> mLocationOfAddress = new MutableLiveData<>();
    private final MutableLiveData<Integer> mUpdatePropertyResponse = new MutableLiveData<>();
    private final MutableLiveData<Long> mCreateImgResponse = new MutableLiveData();
    private final MutableLiveData<Integer> mUpdateImgResponse = new MutableLiveData<>();

    public EditPropertyViewModel(PropertiesRepository propertiesRepository,
                                 ImageRepository imageRepository,
                                 GoogleMapsRepository googleMapsRepository,
                                 Executor executor) {
        mPropertiesRepository = propertiesRepository;
        mImageRepository = imageRepository;
        mGoogleMapsRepository = googleMapsRepository;
        mExecutor = executor;
    }

    public LiveData<SingleProperty> getSingleProperty() {
        return mPropertiesRepository.getSingleProperty(null);
    }

    public LiveData<List<ImageOfProperty>> getImagesOfProperty() {
        return mImageRepository.getAllImagesOfProperty(null);
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

    // Handle data
    public void updateImageOfProperty(ImageOfProperty imageOfProperty) {
        mExecutor.execute(() -> {
            mUpdateImgResponse.postValue(mImageRepository.updateImageOfProperty(imageOfProperty));
        });
    }

    public void deleteImageOfProperty(int imageId) {
        mImageRepository.deletePropertyImage(imageId);
    }

    // Insert new image
    public void createImageOfProperty(ImageOfProperty imageOfProperty) {
        mExecutor.execute(() -> {
            mCreateImgResponse.postValue(mImageRepository.createPropertyImage(imageOfProperty));
        });
    }

    // Save changes
    public void updateProperty(SingleProperty singleProperty) {
        mExecutor.execute(() -> {
            mUpdatePropertyResponse.postValue(mPropertiesRepository.updateSingleProperty(singleProperty));
        });
    }

    public LiveData<Integer> getUpdatePropertyResponse() {
        return mUpdatePropertyResponse;
    }

    public LiveData<Long> getCreateImgResponse() {
        return mCreateImgResponse;
    }

    public LiveData<Integer> getUpdateImgResponse() {
        return mUpdateImgResponse;
    }

    public void disposeDisposable() {
        if (mDisposable.isDisposed()) mDisposable.dispose();
    }

}
