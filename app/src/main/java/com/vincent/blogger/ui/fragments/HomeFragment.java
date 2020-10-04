package com.vincent.blogger.ui.fragments;

import android.app.Activity;
import android.content.Context;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.vincent.blogger.R;
import com.vincent.blogger.adapters.ViewPagerAdapter;

public class HomeFragment extends Fragment{

    private Activity mActivity;
    private Context mContext;

    private LatestFragment latestFragment;
    private FashionFragment fashionFragment;
    private SportsFragment sportsFragment;
    private GymFragment gymFragment;
    private ScienceFragment scienceFragment;
    private FoodFragment foodFragment;
    private BeautyFragment beautyFragment;
    private AutomobileFragment automobileFragment;
    private NatureFragment natureFragment;

    private TabLayout tabLayout;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;

    private MaterialCardView categoryCard;


    public HomeFragment(Context context){
        this.mContext = context;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mActivity = getActivity();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initVeiws(view);
        initFragments();
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit (5);

        if(viewPager !=null){
            initViewPagerAndTabLayout();
        }
    }

    public void initViewPagerAndTabLayout(){
        tabLayout.setupWithViewPager(viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getContext(), getChildFragmentManager(), 0);
        viewPagerAdapter.addFragment(latestFragment, "All");
        viewPagerAdapter.addFragment(fashionFragment, "Fashion");
        viewPagerAdapter.addFragment(natureFragment, "Nature");
        viewPagerAdapter.addFragment(sportsFragment, "Sports");
        viewPagerAdapter.addFragment(gymFragment, "Gym");
        viewPagerAdapter.addFragment(scienceFragment, "Science");
        viewPagerAdapter.addFragment(foodFragment, "Food");
        viewPagerAdapter.addFragment(automobileFragment, "Automobile");
        viewPagerAdapter.addFragment(beautyFragment, "Beauty");

        viewPager.setAdapter(viewPagerAdapter);
        viewPagerAdapter.notifyDataSetChanged();

        for(int i=0; i<tabLayout.getTabCount(); i++){
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(viewPagerAdapter.getTabView(i));
        }

    }


    private void initVeiws(View mView){

        categoryCard = mView.findViewById(R.id.categories_card);

    }


    private void initFragments(){
        latestFragment = new LatestFragment(getActivity());
        fashionFragment = new FashionFragment(getActivity());
        sportsFragment = new SportsFragment(getActivity());
        automobileFragment = new AutomobileFragment(getActivity());
        beautyFragment = new BeautyFragment(getActivity());
        foodFragment = new FoodFragment(getActivity());
        scienceFragment = new ScienceFragment(getActivity());
        gymFragment = new GymFragment(getActivity());
        natureFragment = new NatureFragment(getActivity());
    }



}