package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty;
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty;
import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository;
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository;
import com.openclassrooms.realestatemanager.data.remote.repository.GoogleMapsRepository;

import java.util.List;
import java.util.concurrent.Executor;

public class EditPropertyViewModel extends ViewModel {

    private final PropertiesRepository mPropertiesRepository;
    private final ImageRepository mImageRepository;
    private final GoogleMapsRepository mGoogleMapsRepository;
    private final Executor mExecutor;

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

    // Handle data
    public boolean updateImageOfProperty(ImageOfProperty imageOfProperty) {
        final boolean[] response = new boolean[1];
        mExecutor.execute(()->{
            int res = mImageRepository.updateImageOfProperty(imageOfProperty);
            response[0] = res == 0;
        });
        return response[0];
    }
    public void deleteImageOfProperty(int imageId) {
        mImageRepository.deletePropertyImage(imageId);
    }

    // Insert new image
    public boolean createImageOfProperty(ImageOfProperty imageOfProperty) {
        final boolean[] response = new boolean[1];
        mExecutor.execute(() -> {
            long res = mImageRepository.createPropertyImage(imageOfProperty);
            response[0] = res == -1;
        });
        return response[0];
    }

    // Save changes
    public boolean updateProperty(SingleProperty singleProperty) {
        final boolean[] response = new boolean[1];
        mExecutor.execute(() -> {
            int res = mPropertiesRepository.updateSingleProperty(singleProperty);
            response[0] = res == 0;
        });
        return response[0];
    }

}
