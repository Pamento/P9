package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM

import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository
import com.openclassrooms.realestatemanager.data.local.reposiotries.ImageRepository
import com.openclassrooms.realestatemanager.data.remote.repository.GoogleMapsRepository
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty
import com.openclassrooms.realestatemanager.data.local.entities.ImageOfProperty
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.data.remote.models.geocode.Location
import com.openclassrooms.realestatemanager.data.remote.models.geocode.Result
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.Executor

class AddPropertyViewModel(
    private val mPropertiesRepository: PropertiesRepository,
    private val mImageRepository: ImageRepository,
    private val mGoogleMapsRepository: GoogleMapsRepository,
    private val mExecutor: Executor
) : ViewModel() {
    private val mDisposable = CompositeDisposable()
    var singleProperty: SingleProperty? = null
    private val mImagesOfPropertyList: MutableList<ImageOfProperty> = ArrayList()
    private var mImagesOfProperty: MutableLiveData<List<ImageOfProperty>?>? = null
    private var mLocationOfAddress: MutableLiveData<Location>? = null
    private val mCreatePropertyResponse = MutableLiveData<Long>()
    private val mSaveImagesOfPropertyResponse = MutableLiveData<LongArray>()
    fun init() {
        singleProperty = SingleProperty()
        singleProperty!!.id = UUID.randomUUID().toString()
        mImagesOfProperty = MutableLiveData()
        mLocationOfAddress = MutableLiveData()
        if (mImagesOfPropertyList.size > 0) {
            mImagesOfPropertyList.clear()
        }
    }

    fun createNewProperty(
        type: String,
        description: String,
        surface: Int?,
        price: Int?,
        rooms: Int?,
        bedroom: Int?,
        bathroom: Int?,
        dateRegister: String?,
        dateSold: String?,
        address1: String?,
        address2: String?,
        city: String,
        quarter: String,
        postalCode: Int?,
        location: String?,
        amenities: String,
        agent: String?
    ) {
        singleProperty!!.type = if (type == "") null else type
        singleProperty!!.description = if (description == "") null else description
        singleProperty!!.surface = surface
        singleProperty!!.price = price
        singleProperty!!.rooms = rooms
        singleProperty!!.bedroom = bedroom
        singleProperty!!.bathroom = bathroom
        singleProperty!!.dateRegister = dateRegister
        singleProperty!!.dateSold = dateSold
        singleProperty!!.address1 = address1
        singleProperty!!.address2 = address2
        singleProperty!!.quarter = if (quarter == "") null else quarter
        singleProperty!!.city = if (city == "") null else city
        singleProperty!!.postalCode = postalCode
        singleProperty!!.location = location
        singleProperty!!.amenities = if (amenities == "") null else amenities
        singleProperty!!.agent = agent
    }

    fun createOneImageOfProperty(imageUri: String?) {
        val imgOfProperty = ImageOfProperty()
        imgOfProperty.propertyId = singleProperty!!.id
        imgOfProperty.path = imageUri
        mImagesOfPropertyList.add(imgOfProperty)
        mImagesOfProperty!!.value = mImagesOfPropertyList
    }

    fun removeOneImageOfProperty(imageOfProperty: ImageOfProperty) {
        mImagesOfPropertyList.remove(imageOfProperty)
        mImagesOfProperty!!.value = mImagesOfPropertyList
    }

    fun setImagesOfPropertyList(imagesOfPropertyList: List<ImageOfProperty>?) {
        mImagesOfPropertyList.clear()
        mImagesOfPropertyList.addAll(imagesOfPropertyList!!)
    }

    val imagesOfProperty: LiveData<List<ImageOfProperty>?>?
        get() = mImagesOfProperty

    fun resetImageOfProperty() {
        mImagesOfProperty!!.value = null
        mImagesOfPropertyList.clear()
    }

    fun getLocationFromAddress(address: String?) {
        mGoogleMapsRepository.getGoogleGeoCodeOfAddress(address)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Result> {
                override fun onSubscribe(d: Disposable) {
                    mDisposable.add(d)
                }

                override fun onNext(result: Result) {
                    mLocationOfAddress!!.value = result.geometry.location
                }

                override fun onError(e: Throwable) { /**/
                }

                override fun onComplete() { /**/
                }
            })
    }

    val geoLocationOfProperty: LiveData<Location>?
        get() = mLocationOfAddress

    // Save data
    fun createProperty() {
        mExecutor.execute {
            mCreatePropertyResponse.postValue(
                mPropertiesRepository.createSingleProperty(
                    singleProperty
                )
            )
        }
    }

    fun saveImagesOfProperty() {
        // If true, the insert method was felt
        if (mImagesOfProperty!!.value != null) {
            mExecutor.execute {
                mSaveImagesOfPropertyResponse.postValue(
                    mImageRepository.createPropertyImages(
                        mImagesOfProperty!!.value
                    )
                )
            }
        }
    }

    val saveImagesResponse: LiveData<LongArray>
        get() = mSaveImagesOfPropertyResponse
    val createPropertyResponse: LiveData<Long>
        get() = mCreatePropertyResponse

    fun disposeDisposable() {
        if (mDisposable.isDisposed) {
            mDisposable.dispose()
        }
    }
}