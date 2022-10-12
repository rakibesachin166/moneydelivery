package com.sacdev.moneydelivery;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SettingFragment extends Fragment {
       String number = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhoneNumber();
       EditText nameedt , addressedt;
          DatabaseReference ref  = FirebaseDatabase.getInstance().getReference().child("Profile").child(number);
    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        nameedt = v.findViewById(R.id.namecardtt_id);
        addressedt = v.findViewById(R.id.addresscard_id);
        nameedt.setImeOptions(EditorInfo.IME_ACTION_DONE);
        nameedt.setRawInputType(InputType.TYPE_CLASS_TEXT);
        addressedt.setImeOptions(EditorInfo.IME_ACTION_DONE);
        addressedt.setRawInputType(InputType.TYPE_CLASS_TEXT);
        TextView mobilenumbertxt =  v.findViewById(R.id.mobilenumbercardtxt_id);

        nameedt.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                     ref.child("NAME").setValue(nameedt.getText().toString().trim())
                             .addOnSuccessListener(new OnSuccessListener<Void>() {
                         @Override
                         public void onSuccess(Void unused) {
                             hidekeyboard();
                         }
                     });
                    return true;
                }
                return false;
            }
        });


        addressedt.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ref.child("ADDRESS").setValue(addressedt.getText().toString().trim())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            hidekeyboard();
                        }
                    });

                    return true;
                }
                return false;
            }
        });



       v.findViewById(R.id.contactsupportbtn_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:8975255963"));
                startActivity(callIntent);
            }
        });

        mobilenumbertxt.setText(number);

        return v;
    }


    private void hidekeyboard()
    {
        try{
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    } catch (Exception e) {
        // TODO: handle exception
    }
    }

    @Override
    public void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            public void run(){
                getUserInfo();
            }
        }).start();
    }

    private void getUserInfo()
    {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nameedt.setText(snapshot.child("NAME").getValue().toString());
                addressedt.setText(snapshot.child("ADDRESS").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}