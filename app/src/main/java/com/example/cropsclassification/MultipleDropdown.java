package com.example.cropsclassification;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cropsclassification.databinding.ActivityMultipleDropdownBinding;

public class MultipleDropdown extends AppCompatActivity {

    ActivityMultipleDropdownBinding activityMultipleDropdownBinding;
    private String selectedSemester, selectedCourse;
    private TextView tvSemesterSpinner, tvCourseSpinner;
    private Spinner semesterSpinner, courseSpinner;
    private ArrayAdapter<CharSequence> semesterAdapter, courseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMultipleDropdownBinding = ActivityMultipleDropdownBinding.inflate(getLayoutInflater());
        setContentView(activityMultipleDropdownBinding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        activityMultipleDropdownBinding.actionbarDropdown.actionTitle.setText("Dropdown");



        semesterSpinner = findViewById(R.id.spinner_semester);

        //Populate ArrayAdapter using string array and a spinner layout that we will define
        semesterAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_semester, R.layout.spinner_layout);

        // Specify the layout to use when the list of choices appear
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        semesterSpinner.setAdapter(semesterAdapter);            //Set the adapter to the spinner to populate the State Spinner

        //When any item of the semesterSpinner uis selected
        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Define City Spinner but we will populate the options through the selected state
                courseSpinner = findViewById(R.id.spinner_courses);

                selectedSemester = semesterSpinner.getSelectedItem().toString();      //Obtain the selected State

                int parentID = parent.getId();
                if (parentID == R.id.spinner_semester){
                    switch (selectedSemester){
                        case "Select Semester": courseAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_course, R.layout.spinner_layout);
                            break;
                        case "1st Semester": courseAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_first_semester, R.layout.spinner_layout);
                            break;
                        case "2nd Semester": courseAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_second_semester, R.layout.spinner_layout);
                            break;
                        case "3rd Semester": courseAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_third_semester, R.layout.spinner_layout);
                            break;
                        case "4th Semester": courseAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_fourth_semester, R.layout.spinner_layout);
                            break;
                        case "5th Semester": courseAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_fifth_semester, R.layout.spinner_layout);
                            break;
                        case "6th Semester": courseAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_sixth_semester, R.layout.spinner_layout);
                            break;
                        case "7th Semester": courseAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_seventh_semester, R.layout.spinner_layout);
                            break;
                        case "8th Semester": courseAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_eighth_semester, R.layout.spinner_layout);
                            break;

                        default:  break;
                    }

                    courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     // Specify the layout to use when the list of choices appears
                    courseSpinner.setAdapter(courseAdapter);        //Populate the list of Districts in respect of the State selected

                    //To obtain the selected District from the spinner
                    courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedCourse = courseSpinner.getSelectedItem().toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button submitButton;                                //To display the selected State and District
        submitButton = findViewById(R.id.button_submit);
        tvSemesterSpinner = findViewById(R.id.textView_semester);
        tvCourseSpinner = findViewById(R.id.textView_courses);

        submitButton.setOnClickListener(v -> {
            if (selectedSemester.equals("Select Semester")) {
                Toast.makeText(MultipleDropdown.this, "Please select Semester from the list", Toast.LENGTH_LONG).show();
                tvSemesterSpinner.setError("Semester is required!");      //To set error on TextView
                tvSemesterSpinner.requestFocus();
                tvCourseSpinner.setError(null);
            } else if (selectedCourse.equals("Select Course")) {
                Toast.makeText(MultipleDropdown.this, "Please select course from the list", Toast.LENGTH_LONG).show();
                tvCourseSpinner.setError("Course is required!");
                tvCourseSpinner.requestFocus();
                tvSemesterSpinner.setError(null);                      //To remove error from semesterSpinner
            } else {
                tvSemesterSpinner.setError(null);
                tvCourseSpinner.setError(null);
                Toast.makeText(MultipleDropdown.this, selectedSemester+"\n"+selectedCourse, Toast.LENGTH_LONG).show();
            }
        });
    }
}