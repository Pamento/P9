package com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM;

import androidx.lifecycle.ViewModel;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.openclassrooms.realestatemanager.data.local.models.RowQueryEstates;
import com.openclassrooms.realestatemanager.data.local.reposiotries.PropertiesRepository;

import java.util.ArrayList;
import java.util.List;

import static com.openclassrooms.realestatemanager.util.Constants.ColumnName.*;

public class SearchEngineViewModel extends ViewModel {

    private final PropertiesRepository mPropertiesRepository;
    private SimpleSQLiteQuery mQuery;
    private List<Object> args;

    public SearchEngineViewModel(PropertiesRepository propertiesRepository) {
        mPropertiesRepository = propertiesRepository;
    }

    public void buildAndSendSearchEstateQuery(RowQueryEstates rowQueryEstates) {
        String query = buildQuery(rowQueryEstates);
        mQuery = new SimpleSQLiteQuery(query, args.toArray());
        sendRowEstateQuery();
    }

    private void sendRowEstateQuery() {
        mPropertiesRepository.setRowQueryEstates(mQuery);
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
        if (sLong == 22 && includeSoldEstate) return " WHERE " + DATE_SOLD + " >= ''";
        else if (sLong == 22) return " WHERE " + DATE_SOLD + " IS NULL";
        else if (sLong > 22 && includeSoldEstate) return " AND " + DATE_SOLD + " >= ''";
        else return " AND " + DATE_SOLD + " = ''";
    }
}
