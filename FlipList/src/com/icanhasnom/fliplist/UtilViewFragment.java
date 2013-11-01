package com.icanhasnom.fliplist;

import android.app.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class UtilViewFragment extends Fragment {
	int fragVal;
	Activity activity;
	View layoutView;
    static UtilViewFragment init(int val) {
        UtilViewFragment truitonFrag = new UtilViewFragment();
        Bundle args = new Bundle();
        args.putInt("val", val);
        truitonFrag.setArguments(args);
        return truitonFrag;
    }
	@Override
	public void onAttach(Activity a) {
		super.onAttach(a);
		activity = a;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        fragVal = getArguments() != null ? getArguments().getInt("val") : 1;
	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutView = inflater.inflate(R.layout.fragment_util_layout, container, false);
        return layoutView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
