package com.model.cjx.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by cjx on 2017/2/20.
 */
public abstract class BaseFragment extends Fragment {
    protected View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null){
            view = createView(inflater);
        }
        return view;
    }

    protected abstract View createView(LayoutInflater inflater);

    protected void showToast(String str){
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }
}
