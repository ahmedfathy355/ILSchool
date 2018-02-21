package org.kamsoft.ilschool.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.kamsoft.ilschool.R;
import org.kamsoft.ilschool.TabViewPagerAdapter;


/**
 * Created by admin on 10/12/2017.
 */

public class NavigationDashboardActivity  extends Fragment {


    private TabLayout tabLayout;
    private ViewPager firstViewPager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public NavigationDashboardActivity(){}

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_navigation_dashboard, container, false);

        firstViewPager = (ViewPager) rootView.findViewById(R.id.viewpager_content);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(firstViewPager);

        setupViewPager(firstViewPager);
        return rootView;


    }


    private void setupViewPager(ViewPager viewPager) {
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new Tab1Fragment(), "أحداث اليوم");
        adapter.addFragment(new Tab2(), "المناسبات القادمة");
        adapter.addFragment(new Tab3(), "مواعيد الرحلات");
        viewPager.setAdapter(adapter);
    }
}
