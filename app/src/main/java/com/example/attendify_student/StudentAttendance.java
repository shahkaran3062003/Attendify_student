package com.example.attendify_student;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class StudentAttendance extends Fragment implements OnNavigationButtonClickedListener {



    CustomCalendar customCalendar;

    private String studentId;

    HashMap<Integer,Object> attendance = new HashMap<Integer,Object>();
    String baseApi;
    CustomProgressBar pBar;

    int presetCount = 0;
    int absentCount = 0;

    TextView txtPresentCount,txtAbsentCount;



    public StudentAttendance() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_attendance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        baseApi = getResources().getString(R.string.baseApi);

        studentId = StudentInstance.getId();
//        studentId = "14";

        if(studentId.equals("-1")){
            getActivity().finish();
        }

        pBar = new CustomProgressBar(getActivity());

        txtPresentCount = view.findViewById(R.id.txtPresentCount);
        txtAbsentCount = view.findViewById(R.id.txtAbsentCount);



        customCalendar = view.findViewById(R.id.cCalendar);

        HashMap<Object,Property> descHasMap = new HashMap<Object,Property>();

        Property defaultProperty = new Property();
        defaultProperty.layoutResource = R.layout.default_date_view;
        defaultProperty.dateTextViewResource = R.id.defaultCalendarTextView;
        descHasMap.put("default",defaultProperty);

//        Property currentDateProperty = new Property();
//        currentDateProperty.layoutResource = R.layout.current_date_view;
//        currentDateProperty.dateTextViewResource = R.id.currentDateTextView;
//        descHasMap.put("current",currentDateProperty);

        Property presentDateProperty = new Property();
        presentDateProperty.layoutResource = R.layout.present_date_view;
        presentDateProperty.dateTextViewResource = R.id.presentDateTextView;
        descHasMap.put("present",presentDateProperty);

        Property abDateProperty = new Property();
        abDateProperty.layoutResource = R.layout.ab_date_layout;
        abDateProperty.dateTextViewResource = R.id.abDateTextView;
        descHasMap.put("absent",abDateProperty);





        HashMap<Integer, Object> dateHashMap = new HashMap<>();
        Calendar calendar = Calendar.getInstance();



//        dateHashMap.put(calendar.get(Calendar.DAY_OF_MONTH),"current");
//        dateHashMap.put(3,"present");
//        dateHashMap.put(5,"absent");
//
//        dateHashMap.put(8,"absent");
//        dateHashMap.put(10,"present");

        getAttendance(calendar);






        customCalendar.setMapDescToProp(descHasMap);
        customCalendar.setDate(calendar,dateHashMap);

        customCalendar.setOnNavigationButtonClickedListener(customCalendar.PREVIOUS,this);
        customCalendar.setOnNavigationButtonClickedListener(customCalendar.NEXT,this);


    }

    @Override
    public void onNavigationButtonClicked(int whichButton, Calendar newMonth) {

        getAttendance(newMonth);

    }


    public void getAttendance(Calendar newMonth){
        pBar.show();

        int year = newMonth.get(Calendar.YEAR);
        int month = newMonth.get(Calendar.MONTH) + 1;

        presetCount = 0;
        absentCount = 0;


        String start = year+"-"+month+"-"+"01";
        String end = year+"-"+month+"-"+"31";

        Log.d("API", "getAttendance: "+start);
        Log.d("API", "getAttendance: "+end);




        JSONObject params = new JSONObject();
        try {
            params.put("studentId",studentId);
            params.put("startDate",start);
            params.put("endDate",end);
        } catch (JSONException e) {
            pBar.hide();
            Toast.makeText(getContext(), "Error!!", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }


        customCalendar.setDate(newMonth,null);


        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                baseApi + "/attendance/read_student_by_start_end_date.php",
                params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pBar.hide();

                        try {
                            if(response.has("message")){
                                Toast.makeText(getContext(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }else{
                                JSONArray arr = response.getJSONArray("data");
                                JSONObject obj;
                                for(int i=0;i<arr.length();i++){
                                    obj = arr.getJSONObject(i);
                                    Log.d("API", "onResponse: "+obj.toString());
                                    Integer date =  Integer.parseInt(obj.get("date").toString().substring(9));
                                    String status = obj.get("status").toString();

                                    if(status.equals("0")){
                                        attendance.put(date,"absent");
                                        absentCount++;
                                    }else if(status.equals("1")){
                                        attendance.put(date,"present");
                                        presetCount++;
                                    }
                                }
                                customCalendar.setDate(newMonth,attendance);
                            }
                            txtPresentCount.setText(Integer.toString(presetCount));
                            txtAbsentCount.setText(Integer.toString(absentCount));
                        }
                        catch (JSONException e) {
                            Toast.makeText(getContext(), "Catch error ", Toast.LENGTH_SHORT).show();
                            Log.d("API", "onResponse: "+e);
                            throw new RuntimeException(e);
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "No internet connection.", Toast.LENGTH_SHORT).show();
                        Log.d("API", "onErrorResponse: "+error);
                        pBar.hide();
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }


}