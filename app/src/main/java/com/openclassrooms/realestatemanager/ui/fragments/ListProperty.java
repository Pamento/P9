package com.openclassrooms.realestatemanager.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListProperty#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListProperty extends Fragment implements ListPropertyAdapter.OnItemPropertyListClickListener {

    private static final String TAG = "LIST_TO_MAP";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ListPropertyViewModel mListPropertyViewModel;
    private FragmentListPropertyBinding binding;
    private ListPropertyAdapter mAdapter;
    private List<SingleProperty> mProperties = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ListProperty() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListProperty.
     */
    // TODO: Rename and change types and number of parameters
    public static ListProperty newInstance(String param1, String param2) {
        ListProperty fragment = new ListProperty();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initViewModel();
        binding = FragmentListPropertyBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    private void initViewModel() {
        ViewModelFactory vmF = Injection.sViewModelFactory(requireActivity());
        mListPropertyViewModel = new ViewModelProvider(requireActivity(),vmF).get(ListPropertyViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setRecyclerView();
        setFabListener();
    }

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
        RecyclerView recyclerView = binding.listRecyclerView;
        mAdapter = new ListPropertyAdapter(mProperties,this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemPropertyListClickListener(int position) {
        SingleProperty prop = mProperties.get(position);
        String id = prop.getId();
        String propertyType = prop.getType();
        // setPropertyId in local data Repositories for Detail, Edit fragment and others: Loan simulator, ...
        mListPropertyViewModel.setPropertyId(id);
        startOtherFragment(DETAIL, propertyType);
        Log.i(TAG, "LIST__ onItemPropertyListClickListener:: " + propertyType);
    }
}