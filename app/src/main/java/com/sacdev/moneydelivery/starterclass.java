package com.sacdev.moneydelivery;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class starterclass extends Application {

    public static FirebaseAuth firebaseAuth ;
    public static String selfuserid ;

    public static int  deliverycharge = 40 ;
    public static double chargepercent =  0.03;
    public static Boolean locationpermission = false , positionpermission = false ;
   public static Intent changeActivity(Context a , Class b)
   {
       Intent intent = new Intent(a,b);
       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
       return intent;
   }

}
