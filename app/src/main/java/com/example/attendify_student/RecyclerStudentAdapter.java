package com.example.attendify_student;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class RecyclerStudentAdapter extends RecyclerView.Adapter<RecyclerStudentAdapter.ViewHolder> {

    Context context;
    ArrayList<StudentModel> studentList;

    private String baseImagePath = "https://testing306.000webhostapp.com/";


    RecyclerStudentAdapter(Context context,ArrayList<StudentModel> studentList){
        this.context = context;
        this.studentList = studentList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.student_row,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtStudentName.setText(studentList.get(position).name);
        holder.txtStudentRollNo.setText(studentList.get(position).rollNo);

        String imgPath = studentList.get(position).profilePicPath;
        imgPath = imgPath.substring(6);
        imgPath = baseImagePath+imgPath;

        int errorDrawable;

        if((context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)== Configuration.UI_MODE_NIGHT_YES){
            errorDrawable = R.drawable.student_profile_dark;
        }else{
            errorDrawable = R.drawable.student_profile_light;
        }


        Log.d("API", "onBindViewHolder: "+imgPath);

        Glide.with(context)
                .load(imgPath)
                .placeholder(errorDrawable)
                .error(errorDrawable)
                .into(holder.profilePic);
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtStudentName,txtStudentRollNo;
        ShapeableImageView profilePic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.imgStudentProfilePic);
            txtStudentName = itemView.findViewById(R.id.txtStudentName);
            txtStudentRollNo = itemView.findViewById(R.id.txtStudentRollNo);
        }
    }

}
