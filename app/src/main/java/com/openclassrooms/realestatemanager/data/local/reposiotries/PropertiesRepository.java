package com.openclassrooms.realestatemanager.data.local.reposiotries;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.openclassrooms.realestatemanager.data.local.dao.SinglePropertyDao;
import com.openclassrooms.realestatemanager.data.local.entities.PropertyWithImages;
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty;
import com.openclassrooms.realestatemanager.data.local.models.RowQueryEstates;
import com.openclassrooms.realestatemanager.util.enums.QueryState;

import java.util.ArrayList;
import java.util.List;

import static com.openclassrooms.realestatemanager.util.Constants.ColumnName.BATHROOMS;
import static com.openclassrooms.realestatemanager.util.Constants.ColumnName.BEDROOMS;
import static com.openclassrooms.realestatemanager.util.Constants.ColumnName.DATE_REGISTER;
import static com.openclassrooms.realestatemanager.util.Constants.ColumnName.DATE_SOLD;
import static com.openclassrooms.realestatemanager.util.Constants.ColumnName.PRICE;
import static com.openclassrooms.realestatemanager.util.Constants.ColumnName.QUARTER;
import static com.openclassrooms.realestatemanager.util.Constants.ColumnName.ROOMS;
import static com.openclassrooms.realestatemanager.util.Constants.ColumnName.SURFACE;
import static com.openclassrooms.realestatemanager.util.Constants.ColumnName.TYPE;

public class PropertiesRepository {

    private static volatile PropertiesRepository instance;
    private final SinglePropertyDao mSinglePropertyDao;
    // data
    private final LiveData<List<PropertyWithImages>> mAllProperties;
    private String PROPERTY_ID = "";
    private final MutableLiveData<SimpleSQLiteQuery> mRowQueryEstates;
    private final MutableLiveData<QueryState> mQueryState = new MutableLiveData<>();
    private List<Object> args;

    public PropertiesRepository(SinglePropertyDao singlePropertyDao) {
        mSinglePropertyDao = singlePropertyDao;
        mAllProperties = mSinglePropertyDao.getPropertyWithImages();
        mRowQueryEstates = new MutableLiveData<>();
        mQueryState.setValue(QueryState.NULL);
    }

    public static synchronized PropertiesRepository getInstance(SinglePropertyDao singlePropertyDao) {
        if (instance == null) {
            instance = new PropertiesRepository(singlePropertyDao);
        }
        return instance;
    }

    // Methods
    public LiveData<List<PropertyWithImages>> getAllPropertiesWithImages() {
        return mAllProperties;
    }

    public List<PropertyWithImages> getPropertiesWithImagesQuery() {
        return mSinglePropertyDao.getPropertyWithImageQuery(mRowQueryEstates.getValue());
    }

    public LiveData<SingleProperty> getSingleProperty(@Nullable String propertyId) {
        // If param: propertyId is null, the cal came for DetailFragment
        // On click on the item of List of Properties, the PROPERTY_ID is set
        if (propertyId != null) return mSinglePropertyDao.getSingleProperty(propertyId);
        else return mSinglePropertyDao.getSingleProperty(PROPERTY_ID);
    }

    public long createSingleProperty(SingleProperty singleProperty) {
        return mSinglePropertyDao.createSingleProperty(singleProperty);
    }

    public int updateSingleProperty(SingleProperty singleProperty) {
        return mSinglePropertyDao.updateProperty(singleProperty);
    }

    public void setPROPERTY_ID(String PROPERTY_ID) {
        this.PROPERTY_ID = PROPERTY_ID;
    }

    public String getPROPERTY_ID() {
        return PROPERTY_ID;
    }

    public LiveData<QueryState> getQueryState() {
        return mQueryState;
    }

    public void setQueryState(QueryState queryState) {
        mQueryState.setValue(queryState);
    }

    public LiveData<SimpleSQLiteQuery> getRowQueryProperties() {
        return mRowQueryEstates;
    }

    public void setRowQueryEstates(SimpleSQLiteQuery rowQueryEstates) {
        mQueryState.setValue(QueryState.QUERY);
        mRowQueryEstates.setValue(rowQueryEstates);
    }

    // Build query in according to values set by user
    public void buildAndSetSearchEstateQuery(RowQueryEstates rowQueryEstates) {
        String queryBuild = buildQuery(rowQueryEstates);
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryBuild, args.toArray());
        setRowQueryEstates(query);
    }

    private String buildQuery(RowQueryEstates rowQueryEstates) {
        args = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM property");
        if (!rowQueryEstates.getType().equals("")) {
            query.append(toAppendStr(query.length(), TYPE));
            args.add(rowQueryEstates.getType().toLowerCase());
        }
        if (rowQueryEstates.getMinSurface() > 0 || rowQueryEstates.getMaxSurface() > 0) {
            query.append(toAppendIntTwo(query.length(), SURFACE));
            args.add(rowQueryEstates.getMinSurface());
            args.add(rowQueryEstates.getMaxSurface());
        }
        if (rowQueryEstates.getMinPrice() > 0 || rowQueryEstates.getMaxPrice() > 0) {
            query.append(toAppendIntTwo(query.length(), PRICE));
            args.add(rowQueryEstates.getMinPrice());
            args.add(rowQueryEstates.getMaxPrice());
        }
        if (rowQueryEstates.getRooms() > 0) {
            query.append(toAppendIntOne(query.length(), ROOMS));
            args.add(rowQueryEstates.getRooms());
        }
        if (rowQueryEstates.getBedroom() > 0) {
            query.append(toAppendIntOne(query.length(), BEDROOMS));
            args.add(rowQueryEstates.getBedroom());
        }
        if (rowQueryEstates.getBathroom() > 0) {
            query.append(toAppendIntOne(query.length(), BATHROOMS));
            args.add(rowQueryEstates.getBathroom());
        }
        if (!rowQueryEstates.getDateRegister().equals("0")) {
            query.append(toAppendStr(query.length(), DATE_REGISTER));
            args.add(rowQueryEstates.getDateRegister());
        }
        query.append(toAppendNotNull(query.length(), rowQueryEstates.isSoldEstateInclude()));
        if (!rowQueryEstates.getQuarter().equals("")) {
            query.append(toAppendStr(query.length(), QUARTER));
            args.add(rowQueryEstates.getQuarter());
        }
        return query.toString();
    }

    private String toAppendStr(int sLong, String column) {
        if (sLong == 22) return " WHERE property." + column + " LIKE ?";
        else return " AND property." + column + " LIKE ?";
    }

    private String toAppendIntTwo(int sLong, String column) {
        if (sLong == 22) return " WHERE property." + column + " BETWEEN ? AND ?";
        else return " AND property." + column + " BETWEEN ? AND ?";
    }

    private String toAppendIntOne(int sLong, String column) {
        if (sLong == 22) return " WHERE property." + column + " >= ?";
        else return " AND property." + column + " >= ?";
    }

    private String toAppendNotNull(int sLong, boolean includeSoldEstate) {
        if (sLong == 22 && includeSoldEstate) return "";
        else if (sLong == 22) return " WHERE " + DATE_SOLD + " = ''";
        else if (sLong > 22 && includeSoldEstate) return "";
        else return " AND " + DATE_SOLD + " = ''";
    }
}
