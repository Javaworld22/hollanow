package com.doxa360.android.betacaller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by Javaworld on 5/21/2018.
 */

public class RootFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        View view = inflater.inflate(R.layout.fragment_layout,  container, false);
       // FragmentTransaction transaction = getFragmentManager().beginTransaction();
       // transaction.replace(R.id.fragment_container, new CallDiaryFragment());
       // transaction.commit();
        return view;
    }
}
