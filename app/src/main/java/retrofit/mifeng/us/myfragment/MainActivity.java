package com.wgm.scaneqinfo.view;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.wgm.scaneqinfo.MyFragment.Main_Fragment;
import com.wgm.scaneqinfo.MyFragment.Person_Fragment;
import com.wgm.scaneqinfo.R;

public class MainActivity extends FragmentActivity implements OnClickListener{

    private ImageButton Ibtn_haveorder;
    private ImageButton Ibtn_person;

    private FragmentManager fm = getSupportFragmentManager();
    private Main_Fragment ft1 = (Main_Fragment)fm.findFragmentByTag("Main_Fragment");
    private Person_Fragment ft2 = (Person_Fragment)fm.findFragmentByTag("Person_Fragment");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       Ibtn_haveorder =this.findViewById(R.id.haveorder);
       Ibtn_haveorder.setOnClickListener(this);
       Ibtn_person = this.findViewById(R.id.person);
       Ibtn_person.setOnClickListener(this);

       init();

    }

    @Override
    public void onClick(View v) {
        if(v == Ibtn_haveorder){
            oncreatehaveorder();
        }else if ( v == Ibtn_person){
            oncreateperson();
        }
    }

    private void oncreatehaveorder(){
        fm.beginTransaction()
                .hide(ft2)
                .show(ft1)
                .commit();
    }

    private void oncreateperson(){
        fm.beginTransaction()
                .hide(ft1)
                .show(ft2)
                .commit();
    }

    private void init(){
        Bundle args =new Bundle();
        if (ft1 == null){
            ft1 = Main_Fragment.newInstance(args,getApplicationContext());
            fm.beginTransaction()
                    .add(R.id.main_frame,ft1,"Main_Fragment")
                    .commit();
        }
        if(ft2 == null){
            ft2 = Person_Fragment.newInstance(args,getApplicationContext());
            fm.beginTransaction()
                    .add(R.id.main_frame,ft2,"Person_Fragment")
                    .hide(ft2)
                    .commit();
        }
    }



}
