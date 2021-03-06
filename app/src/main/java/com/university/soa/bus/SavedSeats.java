package com.university.soa.bus;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.university.soa.bus.SeatClass.OnSeatSelected;
import com.university.soa.bus.SeatClass.SelectableAdapter;
import com.university.soa.bus.SeatClass.TicketActivity;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import Models.AppStatus;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class SavedSeats extends AppCompatActivity {


    DatabaseReference ref;


    Button Saveinfo, button;
    SharedPreferences seats;
    Set<String> selected;
    List<Integer> selectSeats = new ArrayList<>();
    String str_name, str_empcode, str_psnum, str_phnmber, emp_code,number;
    EditText Pname, Pnumber, Empcode, passnumber,editText2;
    BookingInfo info;
    TextView T1, T2;
    AppStatus appStatus;

    ;    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private boolean mVerificationInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booked_info);

        if (getIntent() != null && getIntent().getExtras() != null
                && getIntent().hasExtra("employee")) {
            emp_code = getIntent().getStringExtra("employee");
            info = new BookingInfo();
            info = Parcels.unwrap(getIntent().getParcelableExtra("info"));


            selectSeats = info.seats;
        }

        selected = new HashSet<>();
        appStatus = new AppStatus(getApplicationContext());
        Saveinfo = findViewById(R.id.saveinfo);
        button = findViewById(R.id.button);
        Pname = findViewById(R.id.PName);
        Pnumber = findViewById(R.id.PhnNumber);
        Empcode = findViewById(R.id.EmpCode);
        passnumber = findViewById(R.id.PsNum);
        editText2 = (EditText) findViewById(R.id.editText2);
        T1 = (TextView) findViewById(R.id.Opt);
        T2 = (TextView) findViewById(R.id.Details);
        seats = getSharedPreferences("seats", MODE_PRIVATE);
        selected = seats.getStringSet(emp_code, new HashSet<String>());
        ref = FirebaseDatabase.getInstance().getReference().child("booked details");


        mAuth = FirebaseAuth.getInstance();


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // Log.d(TAG, "onVerificationCompleted:" + credential);
                mVerificationInProgress = true;
                Toast.makeText(SavedSeats.this, "Verification Complete", Toast.LENGTH_SHORT).show();
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // Log.w(TAG, "onVerificationFailed", e);
                Toast.makeText(SavedSeats.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(SavedSeats.this,
                            "InValid Phone Number", Toast.LENGTH_SHORT).show();
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                }

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                //Log.d(TAG, "onCodeSent:" + verificationId);
                Toast.makeText(SavedSeats.this, "Verification code has been send on your number", Toast.LENGTH_LONG).show();
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                // ...
            }
        };

        Saveinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                store();
                if (appStatus.isOnline()) {
                    str_name = Pname.getText().toString().trim();
                    str_phnmber = Pnumber.getText().toString().trim();
                    str_empcode = Empcode.getText().toString().trim();
                    str_psnum = passnumber.getText().toString().trim();
                    try {
                        if (str_name.length() == 0 && str_empcode.length() == 0 &&
                                str_phnmber.length() == 0 && str_psnum.length() == 0) {
                            Toast.makeText(getApplicationContext(), "Please fill the Details..", Toast.LENGTH_LONG).show();
                        } else if (str_name.length() == 0 || str_empcode.length() == 0 ||
                                str_empcode.length() == 0 || str_empcode.length() == 0) {
                            Toast.makeText(getApplicationContext(), "All fields are Mandatory", Toast.LENGTH_LONG).show();
                        } else if (str_empcode.equals(0)) {
                            Toast.makeText(getApplicationContext(), "EMPLOYEE IS MANDATORY", Toast.LENGTH_LONG).show();
                        } else if (!str_empcode.equals(0) && !str_name.equals(0) &&
                                !str_phnmber.equals(0)) {
                            if (str_empcode.equals("0000")) {


                                number = "9131341690";
                                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                        "+91 " + number,
                                        60,
                                        java.util.concurrent.TimeUnit.SECONDS,
                                        SavedSeats.this,
                                        mCallbacks);

                                T2.setText("Please Enter the OTP Send to Your Registered Mobile Number " + number);
                                T1.setVisibility(INVISIBLE);
                                Saveinfo.setVisibility(INVISIBLE);
                                Pname.setVisibility(INVISIBLE);
                                Pnumber.setVisibility(INVISIBLE);
                                Empcode.setVisibility(INVISIBLE);
                                passnumber.setVisibility(INVISIBLE);
                                button.setVisibility(VISIBLE);
                                editText2.setVisibility(VISIBLE);
                            } else if (str_empcode.equals("1234")) {


                                number = "8462935367";
                                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                        "+91 " + number,
                                        60,
                                        java.util.concurrent.TimeUnit.SECONDS,
                                        SavedSeats.this,
                                        mCallbacks);

                                T2.setText("Please Enter the OTP Send to Your Registered Mobile Number " + number);
                                T1.setVisibility(INVISIBLE);
                                Saveinfo.setVisibility(INVISIBLE);
                                Pname.setVisibility(INVISIBLE);
                                Pnumber.setVisibility(INVISIBLE);
                                Empcode.setVisibility(INVISIBLE);
                                passnumber.setVisibility(INVISIBLE);
                                button.setVisibility(VISIBLE);
                                editText2.setVisibility(VISIBLE);
                            } else {
                                Toast.makeText(SavedSeats.this, "Invalid Employee Code", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Soory,Error Occured..", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please see that you have Active internet connection..", Toast.LENGTH_LONG).show();

                }

            }
            });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, editText2.getText().toString());
                signInWithPhoneAuthCredential(credential);
                store();

            }
        });


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(SavedSeats.this, "Verification Done", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), "Booked Succesfully..", Toast.LENGTH_SHORT).show();

                        } else {
                            // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(SavedSeats.this, "Invalid Verification", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }
    public void store() {
        str_name = Pname.getText().toString().trim();
        str_empcode = Empcode.getText().toString().trim();
        str_phnmber = Pnumber.getText().toString().trim();
        str_psnum = passnumber.getText().toString().trim();
        Log.i("Seats", "Selected: " + selected);
        //Addata ad = new Addata(str_name, str_empcode, str_phnmber, str_psnum);
        //positions1.remove(positions);
        // String b = String.valueOf(seats.getStringSet("s", positions));
        ref.child(str_empcode).push().setValue(String.valueOf(selected));
        // Toast.makeText(this, "multiple", Toast.LENGTH_SHORT).show();


        Toast.makeText(SavedSeats.this, printSelected(selectSeats), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, TicketActivity.class);
        info.emp_name = str_name;
        info.emp_code = str_empcode;
        info.phoneNo = str_phnmber;
        info.passNo = str_psnum;
        intent.putExtra("info", Parcels.wrap(info));
        startActivity(intent);
    }

    private String printSelected(List<Integer> selectedSeats) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < selectedSeats.size(); i++) {
            if (i == selectedSeats.size() - 1) {
                result.append(selectedSeats.get(i));
                result.append(".");
            } else {
                result.append(selectedSeats.get(i));
                result.append(", ");
            }
        }

        return result.toString();
    }


}






