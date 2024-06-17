package com.example.attendify_student;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.stream.QMediaStoreUriLoader;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;


public class StudentProfile extends Fragment {




    private String studentId;

    MaterialButton btnSelectProfilePic,btnCancel,btnUpdate;

    private BottomNavigationView bnStudent;
    private ActivityResultLauncher<Intent> launcher;

    ImageView imgProfilePic;

    TextInputEditText edtName,edtClass,edtRollNo,edtEmail,edtContact,edtOldPassword,edtNewPassword,edtCnfPassword;

    CustomProgressBar pBar;

    private Uri selectedImageUri;
    String baseAPI;

    private String baseImgPath;


    public StudentProfile(){

    }
    public StudentProfile(BottomNavigationView bnStudent) {
        this.bnStudent = bnStudent;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        studentId = StudentInstance.getId();

        if(studentId.equals("-1")){
            getActivity().finish();
        }

        pBar = new CustomProgressBar(getActivity());
        baseAPI = getResources().getString(R.string.baseApi);
        baseImgPath = "https://testing306.000webhostapp.com/";

        btnSelectProfilePic = view.findViewById(R.id.btnSelectProfilePic);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        imgProfilePic = view.findViewById(R.id.imgProfilePic);
        edtName = view.findViewById(R.id.edtName);
        edtClass = view.findViewById(R.id.edtClass);
        edtRollNo = view.findViewById(R.id.edtRollNo);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtContact = view.findViewById(R.id.edtContact);
        edtOldPassword = view.findViewById(R.id.edtOldPassword);
        edtNewPassword = view.findViewById(R.id.edtPassword);
        edtCnfPassword = view.findViewById(R.id.edtCnfPassword);

        getStudentData();

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK){
                selectedImageUri = result.getData().getData();

                if(selectedImageUri != null){
                    imgProfilePic.setImageURI(selectedImageUri);
                }

            }
        });




        btnSelectProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bnStudent.setSelectedItemId(R.id.bnClass);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isEmptyField() && isNameCorrect() && isRollNumberCorrect() && isEmailCorrect() && isContactCorrect() && isPasswordCorrect()){
                    if(selectedImageUri == null){
                        updateProfileWithoutImage();
                    }else{
                        updateProfileWithImage();
                    }
                }
            }
        });

    }

    void selectImage(){
        Intent img = new Intent();
        img.setType("image/*");
        img.setAction(Intent.ACTION_GET_CONTENT);
        launcher.launch(img);
    }

    void getStudentData(){
        pBar.show();

        JSONObject params = new JSONObject();
        try {
            params.put("id",studentId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                baseAPI + "/student/read_single_student.php",
                params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pBar.hide();
                        try {
                            if(response.has("message")){
                                    Toast.makeText(getContext(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                }
                            else{
                                String imgPath = response.get("profilePic").toString();
                                imgPath = imgPath.substring(6);
                                imgPath = baseImgPath+imgPath;

                                int errorDrawable;
                                if((getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)== Configuration.UI_MODE_NIGHT_YES){
                                    errorDrawable = R.drawable.student_profile_dark;
                                }else{
                                    errorDrawable = R.drawable.student_profile_light;
                                }

                                Glide.with(getContext())
                                        .load(imgPath)
                                        .placeholder(errorDrawable)
                                        .error(errorDrawable)
                                        .into(imgProfilePic);

                                edtName.setText(response.get("name").toString());
                                edtClass.setText(response.get("class").toString());
                                edtRollNo.setText(response.get("rollNo").toString());
                                edtEmail.setText(response.get("email").toString());
                                edtContact.setText(response.get("contact").toString());

                            }
                        }
                        catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pBar.hide();

                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }


    boolean isEmptyField(){

        if(TextUtils.isEmpty(edtName.getText())){
            Toast.makeText(getContext(), "Enter Name.", Toast.LENGTH_SHORT).show();
            return  true;
        }

        if(TextUtils.isEmpty(edtRollNo.getText())){
            Toast.makeText(getContext(), "Enter Roll Number.", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(TextUtils.isEmpty(edtEmail.getText())){
            Toast.makeText(getContext(), "Enter Email.", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(TextUtils.isEmpty(edtContact.getText())){
            Toast.makeText(getContext(), "Enter Contact", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(TextUtils.isEmpty(edtOldPassword.getText())){
            Toast.makeText(getContext(), "Enter Current Password.", Toast.LENGTH_SHORT).show();
            return true;
        }

        boolean newPasswordState = TextUtils.isEmpty(edtNewPassword.getText());
        boolean newCnfPasswordState = TextUtils.isEmpty(edtCnfPassword.getText());

        if(!newPasswordState && newCnfPasswordState){
            Toast.makeText(getContext(), "Enter Confirm New Password.", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(!newCnfPasswordState && newPasswordState){
            Toast.makeText(getContext(), "Enter New Password.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return  false;
    }

    boolean isPasswordCorrect(){

        int len = edtNewPassword.getText().length();
        if(len <8 || len > 16){
            Toast.makeText(getContext(), "Password Length should between 8 to 16.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(edtNewPassword.getText().toString().equals(edtCnfPassword.getText().toString())){
            return true;
        }
        Toast.makeText(getContext(), "Password don't Match.", Toast.LENGTH_SHORT).show();
        return false;
    }

    boolean isEmailCorrect() {
        if(Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()){
            return true;
        }
        Toast.makeText(getContext(), "Invalid Email.", Toast.LENGTH_SHORT).show();
        return  false;

    }

    boolean isContactCorrect(){
        if(edtContact.getText().length()==10){
            return true;
        }
        Toast.makeText(getContext(), "Invalid Contact Number.", Toast.LENGTH_SHORT).show();
        return false;
    }

    boolean isNameCorrect(){
        String name = edtName.getText().toString();

        if(!name.matches("[a-zA-Z ]*")){
            Toast.makeText(getContext(), "Invalid Name.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    boolean isRollNumberCorrect(){
        if(TextUtils.isDigitsOnly(edtRollNo.getText().toString())){
            return true;
        }
        Toast.makeText(getContext(), "Invalid Roll Number.", Toast.LENGTH_SHORT).show();
        return  false;
    }


    public void updateProfileWithoutImage(){
        pBar.show();

        JSONObject params = new JSONObject();

        try {
            params.put("id",studentId);
            params.put("name",edtName.getText().toString());
            params.put("rollNo",edtRollNo.getText().toString());
            params.put("email",edtEmail.getText().toString());
            params.put("contact",edtContact.getText().toString());
            params.put("oldPassword",edtOldPassword.getText().toString());
            if(TextUtils.isEmpty(edtNewPassword.getText())){
                params.put("password",edtOldPassword.getText().toString());
            }
            else {
                params.put("password", edtNewPassword.getText().toString());
            }
        } catch (JSONException e) {
            pBar.hide();
            throw new RuntimeException(e);
        }


        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                baseAPI + "/student/update_student_without_image.php",
                params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pBar.hide();

                        if(response.has("message")){

                            try {
                                Toast.makeText(getContext(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                if(response.get("message").toString().equals("success")){
                                    bnStudent.setSelectedItemId(R.id.bnClass);
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pBar.hide();
                        Toast.makeText(getContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);

    }

    public void updateProfileWithImage(){
        pBar.show();

        Bitmap bitmap = null;
        byte[] imageData;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),selectedImageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            imageData = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            pBar.hide();
            throw new RuntimeException(e);
        }

        HashMap<String,String> params = new HashMap<>();
        params.put("id",studentId);
        params.put("name",edtName.getText().toString());
        params.put("rollNo",edtRollNo.getText().toString());
        params.put("email",edtEmail.getText().toString());
        params.put("contact",edtContact.getText().toString());
        params.put("oldPassword",edtOldPassword.getText().toString());
        if(TextUtils.isEmpty(edtNewPassword.getText())){
            params.put("password",edtOldPassword.getText().toString());
        }
        else {
            params.put("password", edtNewPassword.getText().toString());
        }

        MultipartRequest request = new MultipartRequest(
                Request.Method.POST,
                baseAPI + "/student/update_student_with_image.php",
                params,
                imageData,
                "profile_pic.jpeg",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        pBar.hide();
                        String responseBody = new String(response.data);
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody);


                            if(jsonObject.has("message")){

                                try {
                                    Toast.makeText(getContext(), jsonObject.get("message").toString(), Toast.LENGTH_SHORT).show();
                                    if(jsonObject.get("message").toString().equals("success")){
                                        selectedImageUri = null;
                                        bnStudent.setSelectedItemId(R.id.bnClass);
                                    }
                                } catch (JSONException e) {
                                    pBar.hide();
                                    Log.d("API", "onResponse: "+e);
                                    throw new RuntimeException(e);
                                }
                            }
                        } catch (JSONException e) {
                            pBar.hide();
                            Log.d("API", "onResponse: "+e);
                            throw new RuntimeException(e);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pBar.hide();
                        Toast.makeText(getContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);



    }
}