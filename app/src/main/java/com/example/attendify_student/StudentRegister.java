package com.example.attendify_student;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;


public class StudentRegister extends AppCompatActivity {

    private ActivityResultLauncher<Intent> launcher;
    AutoCompleteTextView ddClass;
    ShapeableImageView imgProfilePic;
    MaterialButton btnSelectProfilePic,btnLogin,btnSignUp;
    ImageView btnBack;
    ArrayList<ClassModel> classList = new ArrayList<ClassModel>();
    String baseAPi;
    private Uri selectedImageUri;
    TextInputEditText txtName,txtRollNo,txtEmail,txtContact,txtPassword,txtConfirmPassword;
    String imei;
    String selectedClassId;

    CustomProgressBar pBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);

        pBar = new CustomProgressBar(this);


        txtName = findViewById(R.id.edtName);
        txtRollNo = findViewById(R.id.edtRollNo);
        txtEmail = findViewById(R.id.edtEmail);
        txtContact = findViewById(R.id.edtContact);
        txtPassword = findViewById(R.id.edtPassword);
        txtConfirmPassword = findViewById(R.id.edtCnfPassword);
        ddClass = findViewById(R.id.ddClass);
        imgProfilePic = findViewById(R.id.imgProfilePic);
        btnSelectProfilePic = findViewById(R.id.btnSelectProfilePic);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnBack = findViewById(R.id.btnBack);

        imei = Settings.System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);


        baseAPi = getString(R.string.baseApi);

        getAllClass();

        ArrayDropDownAdapter adapter = new ArrayDropDownAdapter(this,classList);
        ddClass.setAdapter(adapter);


        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {
            if (result.getResultCode() == RESULT_OK){
                selectedImageUri = result.getData().getData();

                if(selectedImageUri != null){
                    imgProfilePic.setImageURI(selectedImageUri);
                    imgProfilePic.setTag(selectedImageUri);
                }

            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 19-05-2024 add sign up code to register student


                if(!isEmptyField() && isNameCorrect() && isRollNumberCorrect()  && isEmailCorrect() && isContactCorrect() && isPasswordCorrect()){
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),selectedImageUri);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                        byte[] imageData = byteArrayOutputStream.toByteArray();

                        HashMap<String,String> params = new HashMap<>();
                        params.put("name",txtName.getText().toString());
                        params.put("rollNo",txtRollNo.getText().toString());
                        params.put("email",txtEmail.getText().toString());
                        params.put("contact",txtContact.getText().toString());
                        params.put("password",txtPassword.getText().toString());
                        params.put("imei",imei);
                        params.put("classId",selectedClassId);


                        pBar.show();
                        MultipartRequest multipartRequest = new MultipartRequest(
                                Request.Method.POST,
                                baseAPi + "/student/register.php",
                                params,
                                imageData,
                                "profile_pic.jpeg",
                                new Response.Listener<NetworkResponse>() {
                                    @Override
                                    public void onResponse(NetworkResponse response) {
                                        pBar.hide();
                                        try {
                                            String responseBody = new String(response.data);
                                            JSONObject jsonObject = new JSONObject(responseBody);

                                            String mess = jsonObject.get("message").toString();

                                            Toast.makeText(StudentRegister.this, jsonObject.get("message").toString(), Toast.LENGTH_SHORT).show();
                                            if(mess.equals("successful")){
                                                finish();
                                            }

                                        } catch (JSONException e) {
                                            Log.d("API", "onResponse: Catch error");
                                            throw new RuntimeException(e);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        pBar.hide();
                                        Toast.makeText(StudentRegister.this, "No internet connection.", Toast.LENGTH_SHORT).show();
                                        Log.d("API", "onErrorResponse: "+error);
                                    }
                                }
                        );

                        RequestQueue requestQueue = Volley.newRequestQueue(StudentRegister.this);
                        requestQueue.add(multipartRequest);



                    } catch (IOException e) {
                        Log.d("API", "onClick: "+e);
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        ddClass.setOnItemClickListener((parent,view,position,id)->{
            ClassModel classItem = classList.get(position);
            selectedClassId = classItem.id;
        });


        btnSelectProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }


    boolean isEmptyField(){
        if(selectedImageUri == null){
            Toast.makeText(this, "Select Profile Picture.", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(TextUtils.isEmpty(txtName.getText())){
            Toast.makeText(this, "Enter Name.", Toast.LENGTH_SHORT).show();
            return  true;
        }

        if(selectedClassId == null){
            Toast.makeText(this,"Select Class.",Toast.LENGTH_SHORT).show();
            return true;
        }
        if(TextUtils.isEmpty(txtRollNo.getText())){
            Toast.makeText(this, "Enter Roll Number.", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(TextUtils.isEmpty(txtEmail.getText())){
            Toast.makeText(this, "Enter Email.", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(TextUtils.isEmpty(txtContact.getText())){
            Toast.makeText(this, "Enter Contact", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(TextUtils.isEmpty(txtPassword.getText())){
            Toast.makeText(this, "Enter Password.", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(TextUtils.isEmpty(txtConfirmPassword.getText())){
            Toast.makeText(this, "Enter Confirm Password.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return  false;
    }

    boolean isPasswordCorrect(){
        int len = txtPassword.getText().length();
        if(len <8 || len > 16){
            Toast.makeText(this, "Password Length should between 8 to 16.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(txtPassword.getText().toString().equals(txtConfirmPassword.getText().toString())){
            return true;
        }
        Toast.makeText(this, "Password don't Match.", Toast.LENGTH_SHORT).show();
        return false;
    }

    boolean isEmailCorrect() {
        if(Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText().toString()).matches()){
            return true;
        }
        Toast.makeText(this, "Invalid Email.", Toast.LENGTH_SHORT).show();
        return  false;

    }

    boolean isContactCorrect(){
        if(txtContact.getText().length()==10){
            return true;
        }
        Toast.makeText(this, "Invalid Contact Number.", Toast.LENGTH_SHORT).show();
        return false;
    }

    boolean isNameCorrect(){

        String name = txtName.getText().toString();

        if(!name.matches("[a-zA-Z ]*")){
            Toast.makeText(this, "Invalid Name.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return  true;
    }

    boolean isRollNumberCorrect(){
        if(TextUtils.isDigitsOnly(txtRollNo.getText().toString())){
            return true;
        }
        Toast.makeText(this, "Invalid Roll Number.", Toast.LENGTH_SHORT).show();
        return  false;
    }

    void selectImage(){
        Intent img = new Intent();
        img.setType("image/*");
        img.setAction(Intent.ACTION_GET_CONTENT);
        launcher.launch(img);
    }

    void getAllClass(){
        pBar.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                baseAPi + "/class/read_all.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pBar.hide();
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.has("message")){
                                Toast.makeText(StudentRegister.this, jsonObject.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                            else{
                                JSONArray jsonArray = new JSONArray(jsonObject.get("data").toString());
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    classList.add(new ClassModel(obj.get("id").toString(),obj.get("name").toString()));
                                }
                            }
                        } catch (JSONException e) {
                            Toast.makeText(StudentRegister.this, "Catch Error", Toast.LENGTH_SHORT).show();
                            Log.d("API", "onResponse: "+e.toString());
                            throw new RuntimeException(e);
                        }

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pBar.hide();
                        Toast.makeText(StudentRegister.this, "No internet connection", Toast.LENGTH_SHORT).show();
                        Log.d("API", "onErrorResponse: "+error);
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
}