package com.example.stockscan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.transition.ChangeBounds;
import android.widget.FrameLayout;

import com.example.stockscan.Fragments.InventoryFrag;
import com.example.stockscan.Fragments.LoginFrag;
import com.example.stockscan.Fragments.ProduceDetails;
import com.example.stockscan.Fragments.ScanFrag;
import com.example.stockscan.Models.Produce;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MasterActivity extends AppCompatActivity {

    private FrameLayout mainContainer;

    private Boolean badFrag;
    private String currentFrag = "";
    private final List<String> badFrags = new ArrayList<>();
    private HashMap<String, Fragment> frags = new HashMap<>();
    private final List<String> previousFrags = new ArrayList<>();

    private Produce selectedProduce;

    private Boolean popped = false;

    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);

        mainContainer = findViewById(R.id.mainContainer);

        setupFrags();

        currentFrag = /*currentUser == null? "login" :*/ "inventory";
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(mainContainer.getId(), frags.get(currentFrag)).commit();
        previousFrags.add(currentFrag);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int size = previousFrags.size() - 1;

        if ((size > 0)) {
            previousFrags.remove(previousFrags.size() - 1);
            currentFrag = previousFrags.get(previousFrags.size() - 1);
        }

//        Loop that pops backstack as long as the current fragment is a "bad fragment"
        while(badFrags.contains(currentFrag)){
            popped = true;
            getSupportFragmentManager().popBackStackImmediate();
        }
    }

    private void setupFrags(){
        LoginFrag loginFrag = LoginFrag.newInstance();
        InventoryFrag inventoryFrag = InventoryFrag.newInstance();
        ScanFrag scanFrag = ScanFrag.newInstance();
        ProduceDetails produceDetails = ProduceDetails.newInstance();

        frags.put("login",              loginFrag);
        frags.put("inventory",          inventoryFrag);
        frags.put("scan",               scanFrag);
        frags.put("prod_details",       produceDetails);

        //Fragments that should not be revisited from back navigation
        badFrags.add("login");

    }

    public void changeFrag(String fragName){

        if(!(currentFrag.equals(fragName))) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(mainContainer.getId(), frags.get(fragName)).addToBackStack(null).commit();
            currentFrag = fragName;
            badFrag = badFrags.contains(currentFrag);
            previousFrags.add(currentFrag);
        }
    }

    public Boolean isPopped() {
        return popped;
    }

    public Produce getSelectedProduce() {
        return selectedProduce;
    }

    public void setSelectedProduce(Produce selectedProduce) {
        this.selectedProduce = selectedProduce;
    }
}