package com.openclassrooms.realestatemanager.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.sqlite.db.SimpleSQLiteQuery;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.data.local.entities.PropertyWithImages;
import com.openclassrooms.realestatemanager.data.viewModelFactory.ViewModelFactory;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.ListPropertyViewModel;
import com.openclassrooms.realestatemanager.databinding.FragmentListPropertyBinding;
import com.openclassrooms.realestatemanager.injection.Injection;
import com.openclassrooms.realestatemanager.ui.activity.MainActivity;
import com.openclassrooms.realestatemanager.ui.adapters.ListPropertyAdapter;
import com.openclassrooms.realestatemanager.util.enums.EFragments;
import com.openclassrooms.realestatemanager.util.enums.QueryState;
import com.openclassrooms.realestatemanager.util.notification.NotifyBySnackBar;

import java.util.ArrayList;
import java.util.List;

import static com.openclassrooms.realestatemanager.util.enums.EFragments.DETAIL;
import static com.openclassrooms.realestatemanager.util.enums.EFragments.MAP;

public class ListProperty extends Fragment implements ListPropertyAdapter.OnItemPropertyListClickListener {

    private static final String TAG = "ListProperty";
    private ListPropertyViewModel mListPropertyViewModel;
    private View view;
    private FragmentListPropertyBinding binding;
    private RecyclerView recyclerView;
    private final List<PropertyWithImages> mProperties = new ArrayList<>();

    public ListProperty() {
        // Required empty public constructor
    }

    public static ListProperty newInstance() {
        return new ListProperty();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initViewModel();
        binding = FragmentListPropertyBinding.inflate(inflater, container, false);
        setRecyclerView();
        setQueryStateObserver();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        setFabListener();
    }

    private void initViewModel() {
        ViewModelFactory vmF = Injection.sViewModelFactory(requireActivity());
        mListPropertyViewModel = new ViewModelProvider(requireActivity(), vmF).get(ListPropertyViewModel.class);
    }

    private void setQueryStateObserver() {
        Log.i(TAG, "LIST__ setQueryStateObserver: RUN");
        mListPropertyViewModel.getQueryState().observe(getViewLifecycleOwner(), queryStateObserver);
    }

    private void unsubscribeQueryState() {
        mListPropertyViewModel.getQueryState().removeObserver(queryStateObserver);
    }

    final Observer<QueryState> queryStateObserver = queryState -> {
        if (queryState.equals(QueryState.NULL)) {
            Log.i(TAG, "LIST__ queryStateObserver: IF:: all properties ");
            setPropertiesObserver();
        } else {
            Log.i(TAG, "LIST__ queryStateObserver: ELSE:: row query");
            subscribeRowQuery();
        }
    };

    private void setPropertiesObserver() {
        Log.i(TAG, "setPropertyObserver");
        mListPropertyViewModel.getPropertyWithImages().observe(getViewLifecycleOwner(), getProperties);
    }

    private void unsubscribeProperties() {
        mListPropertyViewModel.getPropertyWithImages().removeObserver(getProperties);
    }

    private void unsubscribeRowQuery() {
        mListPropertyViewModel.getSimpleSQLiteQuery().removeObserver(rowQueryObserver);
    }

    private void subscribeRowQuery() {
        mListPropertyViewModel.getSimpleSQLiteQuery().observe(getViewLifecycleOwner(), rowQueryObserver);
    }

    public void resetRowQuery() {
        Log.i(TAG, "LIST__ resetRowQuery: ");
        mListPropertyViewModel.setQueryState(QueryState.NULL);
        setQueryStateObserver();
    }

    final Observer<SimpleSQLiteQuery> rowQueryObserver = new Observer<SimpleSQLiteQuery>() {
        @Override
        public void onChanged(SimpleSQLiteQuery simpleSQLiteQuery) {
            Log.i(TAG, "LIST__ onChanged: rowQueryObserver RUN");
            if (simpleSQLiteQuery != null) {
                unsubscribeProperties();
                mProperties.clear();
                List<PropertyWithImages> fromRowQuery = mListPropertyViewModel.getPropertiesWithImagesFromRowQuery();
                Log.i(TAG, "onChanged: PropertiesFromRowQuery:: " + fromRowQuery.size());
                if (fromRowQuery.size() == 0) {
                    String msg = getResources().getString(R.string.search_give_zero_data);
                    NotifyBySnackBar.showSnackBar(1, view, msg);
                }
                mProperties.addAll(fromRowQuery);
                displayDataOnRecyclerView();
            }
        }
    };

    final Observer<List<PropertyWithImages>> getProperties = propertyWithImages -> {
        if (propertyWithImages != null) {
            Log.i(TAG, "LIST__ getProperties_OBSERVER:: " + propertyWithImages.size());
            mProperties.clear();
            Log.i(TAG, "LIST__ getProperties_OBSERVER:: " + mProperties.size());
            mProperties.addAll(propertyWithImages);
            Log.i(TAG, "LIST__ getProperties_OBSERVER:: " + mProperties.size());
        }
        displayDataOnRecyclerView();
    };

    private void startOtherFragment(EFragments fragment, String param) {
        MainActivity ma = (MainActivity) requireActivity();
        ma.displayFragm(fragment, param);
    }

    private void setFabListener() {
        binding.fabList.setOnClickListener(view -> {
            Log.i(TAG, "onClick: LIST _ fab;;");
            startOtherFragment(MAP, "");
        });
    }

    private void setRecyclerView() {
        recyclerView = binding.listRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void displayDataOnRecyclerView() {
        ListPropertyAdapter adapter = new ListPropertyAdapter(mProperties, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemPropertyListClickListener(int position) {
        if (mProperties.size() > 0) {
            PropertyWithImages prop = mProperties.get(position);
            String id = prop.mSingleProperty.getId();
            String propertyType = prop.mSingleProperty.getType();
            // setPropertyId in local data Repositories for Detail, Edit fragment and others: Loan simulator, ...
            mListPropertyViewModel.setPropertyId(id);
            startOtherFragment(DETAIL, propertyType);
            Log.i(TAG, "LIST__ onItemPropertyListClickListener:: " + propertyType);
            Log.i(TAG, "LIST__ onItemPropertyListClickListener__ propertyID:: " + id);
        } else startOtherFragment(DETAIL, "Property Type");
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "LIST__ onDestroy ");
        if (mListPropertyViewModel.getQueryState().hasActiveObservers())
            unsubscribeQueryState();
        if (mListPropertyViewModel.getSimpleSQLiteQuery().hasActiveObservers())
            unsubscribeRowQuery();
        if (mListPropertyViewModel.getPropertyWithImages().hasActiveObservers())
            unsubscribeProperties();
        super.onDestroy();
    }
}