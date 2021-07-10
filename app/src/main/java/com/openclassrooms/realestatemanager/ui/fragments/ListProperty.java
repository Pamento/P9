package com.openclassrooms.realestatemanager.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openclassrooms.realestatemanager.data.model.SingleProperty;
import com.openclassrooms.realestatemanager.databinding.FragmentListPropertyBinding;
import com.openclassrooms.realestatemanager.ui.activity.MainActivity;
import com.openclassrooms.realestatemanager.ui.adapters.ListPropertyAdapter;
import com.openclassrooms.realestatemanager.util.EFragments;

import java.util.ArrayList;
import java.util.List;

import static com.openclassrooms.realestatemanager.util.EFragments.DETAIL;
import static com.openclassrooms.realestatemanager.util.EFragments.MAP;

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
        // Inflate the layout for this fragment
        binding = FragmentListPropertyBinding.inflate(inflater,container,false);
        //return inflater.inflate(R.layout.fragment_list_property, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setRecyclerView();
        setFabListener();
    }

    private void startOtherFragment(EFragments fragment, String param) {
        MainActivity ma = (MainActivity) requireActivity();
        ma.displayFrak(fragment,param);
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
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onItemPropertyListClickListener(int position) {
        //SingleProperty prop = mProperties.get(position);
        String id = "prop.getId()";
        startOtherFragment(DETAIL, id);
        Log.i(TAG, "LIST__ onItemPropertyListClickListener:");
    }
}