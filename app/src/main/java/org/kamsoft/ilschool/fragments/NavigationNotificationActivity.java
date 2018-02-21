package org.kamsoft.ilschool.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.kamsoft.ilschool.R;

/**
 * Created by admin on 10/12/2017.
 */

public class NavigationNotificationActivity  extends Fragment {
    public static NavigationNotificationActivity newInstance() {
        NavigationNotificationActivity fragment = new NavigationNotificationActivity();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation_notifications, container, false);
    }
}
