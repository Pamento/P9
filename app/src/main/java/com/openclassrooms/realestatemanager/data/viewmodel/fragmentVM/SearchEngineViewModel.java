package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

import androidx.lifecycle.ViewModel;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.openclassrooms.realestatemanager.data.local.models.RowQueryEstates;
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository;

import static com.openclassrooms.realestatemanager.util.Constants.ColumnName.*;

public class SearchEngineViewModel extends ViewModel {

    private final PropertiesRepository mPropertiesRepository;
    private SimpleSQLiteQuery mQuery;

    public SearchEngineViewModel(PropertiesRepository propertiesRepository) {
        mPropertiesRepository = propertiesRepository;
    }

    public void buildAndSendSearchEstateQuery(RowQueryEstates rowQueryEstates) {
        String query = buildQuery(rowQueryEstates);
        mQuery = new SimpleSQLiteQuery(query);
        sendRowEstateQuery();
    }

    private void sendRowEstateQuery() {
        mPropertiesRepository.setRowQueryEstates(mQuery);
    }

    private String buildQuery(RowQueryEstates rowQueryEstates) {
        StringBuilder query = new StringBuilder("SELECT * FROM property");
        if (!rowQueryEstates.getType().equals("")) {
            query.append(toAppendStr(query.length(), TYPE, rowQueryEstates.getType()));
        }
        if (rowQueryEstates.getMinSurface() > 0 || rowQueryEstates.getMaxSurface() > 0) {
            query.append(toAppendIntTwo(query.length(), SURFACE, rowQueryEstates.getMinSurface(), rowQueryEstates.getMaxSurface()));
        }
        if (rowQueryEstates.getMinPrice() > 0 || rowQueryEstates.getMaxPrice() > 0) {
            query.append(toAppendIntTwo(query.length(), PRICE, rowQueryEstates.getMinPrice(), rowQueryEstates.getMaxPrice()));
        }
        if (rowQueryEstates.getRooms() > 0) {
            query.append(toAppendIntOne(query.length(), ROOMS, rowQueryEstates.getRooms()));
        }
        if (rowQueryEstates.getBedroom() > 0) {
            query.append(toAppendIntOne(query.length(), BEDROOMS, rowQueryEstates.getBedroom()));
        }
        if (rowQueryEstates.getBathroom() > 0) {
            query.append(toAppendIntOne(query.length(), BATHROOMS, rowQueryEstates.getBathroom()));
        }
        if (!rowQueryEstates.getDateRegister().equals("")) {
            query.append(toAppendStr(query.length(), DATE_REGISTER, rowQueryEstates.getDateRegister()));
        }
        query.append(toAppendNotNull(query.length(), rowQueryEstates.isSoldEstateInclude()));
        if (!rowQueryEstates.getQuarter().equals("")) {
            query.append(toAppendStr(query.length(), QUARTER, rowQueryEstates.getQuarter()));
        }
        return query.toString();
    }

    private String toAppendStr(int sLong, String column, String q) {
        if (sLong == 22) return " WHERE " + column + " LIKE " + q;
        else return " AND " + column + " LIKE " + q;
    }

    private String toAppendIntTwo(int sLong, String column, int q1, int q2) {
        if (sLong == 22) return " WHERE " + column + " BETWEEN " + q1 + " AND " + q2;
        else return " AND " + column + " BETWEEN " + q1 + " AND " + q2;
    }

    private String toAppendIntOne(int sLong, String column, int q) {
        if (sLong == 22) return " WHERE " + column + " > " + q;
        else return " AND " + column + " > " + q;
    }

    private String toAppendNotNull(int sLong, boolean includeSoldEstate) {
        if (sLong == 22 && includeSoldEstate) return " WHERE " + DATE_SOLD + " IS NOT NULL";
        else if (sLong == 22) return " WHERE " + DATE_SOLD + " = NULL";
        else if (sLong > 22 && includeSoldEstate) return " AND " + DATE_SOLD + " IS NOT NULL";
        else return " AND " + DATE_SOLD + " = NULL";
    }
}
