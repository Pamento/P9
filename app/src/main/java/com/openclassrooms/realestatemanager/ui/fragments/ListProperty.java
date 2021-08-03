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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openclassrooms.realestatemanager.data.local.entities.PropertyWithImages;
import com.openclassrooms.realestatemanager.data.viewModelFactory.ViewModelFactory;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.ListPropertyViewModel;
import com.openclassrooms.realestatemanager.databinding.FragmentListPropertyBinding;
import com.openclassrooms.realestatemanager.injection.Injection;
import com.openclassrooms.realestatemanager.ui.activity.MainActivity;
import com.openclassrooms.realestatemanager.ui.adapters.ListPropertyAdapter;
import com.openclassrooms.realestatemanager.util.enums.EFragments;

import java.util.ArrayList;
import java.util.List;

import static com.openclassrooms.realestatemanager.util.enums.EFragments.DETAIL;
import static com.openclassrooms.realestatemanager.util.enums.EFragments.MAP;

public class ListProperty extends Fragment implements ListPropertyAdapter.OnItemPropertyListClickListener {

    private static final String TAG = "LIST_TO_MAP";
    private ListPropertyViewModel mListPropertyViewModel;
    private FragmentListPropertyBinding binding;
    private RecyclerView recyclerView;
    private ListPropertyAdapter mAdapter;
    private List<PropertyWithImages> mProperties = new ArrayList<>();

    public ListProperty() {
        // Required empty public constructor
    }

    public static ListProperty newInstance() {
        return new ListProperty();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initViewModel();
        binding = FragmentListPropertyBinding.inflate(inflater,container,false);
        setRecyclerView();
        setPropertyObserver();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        setFabListener();
    }

    private void initViewModel() {
        ViewModelFactory vmF = Injection.sViewModelFactory(requireActivity());
        mListPropertyViewModel = new ViewModelProvider(requireActivity(),vmF).get(ListPropertyViewModel.class);
    }

    private void setPropertyObserver() {
        if (mListPropertyViewModel.getSimpleSQLiteQuery() != null) {
            mProperties = mListPropertyViewModel.getPropertiesWithImagesFromRowQuery();
            if (mProperties.size() > 0) displayDataOnRecyclerView();
        } else {
            mListPropertyViewModel.getPropertyWithImages().observe(getViewLifecycleOwner(), getProperties);
        }
    }

    final Observer<List<PropertyWithImages>> getProperties = propertyWithImages -> {
        if (propertyWithImages != null) mProperties = propertyWithImages;
        displayDataOnRecyclerView();
    };

    private void startOtherFragment(EFragments fragment, String param) {
        MainActivity ma = (MainActivity) requireActivity();
        ma.displayFragm(fragment,param);
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
        mAdapter = new ListPropertyAdapter(mProperties,this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemPropertyListClickListener(int position) {
        if ( mProperties.size() > 0) {
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
        super.onDestroyView();
        binding = null;
    }
}