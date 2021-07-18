package com.openclassrooms.realestatemanager.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.data.viewModelFactory.ViewModelFactory;
import com.openclassrooms.realestatemanager.data.viewmodel.fragmentVM.EditPropertyViewModel;
import com.openclassrooms.realestatemanager.databinding.FragmentEditPropertyBinding;
import com.openclassrooms.realestatemanager.injection.Injection;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProperty#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProperty extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditPropertyViewModel mEditPropertyViewModel;
    private FragmentEditPropertyBinding binding;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditProperty() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditProperty.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProperty newInstance(String param1, String param2) {
        EditProperty fragment = new EditProperty();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initViewModel();
        binding = FragmentEditPropertyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void initViewModel() {
        ViewModelFactory vmF = Injection.sViewModelFactory(requireActivity());
        mEditPropertyViewModel = new ViewModelProvider(requireActivity(),vmF).get(EditPropertyViewModel.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}