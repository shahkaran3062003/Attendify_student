package com.example.attendify_student;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ArrayDropDownAdapter extends ArrayAdapter<ClassModel> {

    Context context;
    private ArrayList<ClassModel> classList;
    public ArrayDropDownAdapter(@NonNull Context context,ArrayList<ClassModel> classList) {
        super(context, 0,classList);
        this.context = context;
        this.classList = classList;

    }
    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

       return createView(position,convertView,parent);

    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position,convertView,parent);
    }

    public View createView(int position,View convertView,ViewGroup parent){
        View view = LayoutInflater.from(context).inflate(R.layout.class_drop_down_item,parent,false);
        ClassModel classItem = classList.get(position);


        TextView textView =  view.findViewById(R.id.dropDrownItem);
        textView.setText(classItem.getClassTitle());
        textView.setTag(classItem.getId());

        parent.setTag(classItem.getId());

        return view;
    }
}
