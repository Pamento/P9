package com.openclassrooms.realestatemanager.ui.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

public class AgentAdapter extends ArrayAdapter<String> {
    public AgentAdapter(@NonNull Context context, int resource, @NonNull String[] objects) {
        super(context, resource, objects);
    }
}
