package com.example.plantkriya.fragment.reviewer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.plantkriya.R;
import com.example.plantkriya.adapter.SectionPagerAdapter;
import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.BubbleToggleView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

/**
 * A simple {@link Fragment} subclass.
 */

public class NavigationFragment extends Fragment {

    private ViewPager viewPager;
    private BubbleNavigationLinearView bubbleNav;
    private BubbleToggleView add;

    public NavigationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        bubbleNav = view.findViewById(R.id.BubbleNav);
        add = view.findViewById(R.id.add);

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SectionPagerAdapter adapter = new SectionPagerAdapter(getChildFragmentManager());
        PlantFragment plantFragment = new PlantFragment();
        NoteFragment noteFragment = new NoteFragment();
        KriyaFragment kriyaFragment = new KriyaFragment();

        adapter.addFragment(plantFragment);
        adapter.addFragment(noteFragment);
        adapter.addFragment(kriyaFragment);

        add.activate();

        add.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                StyleableToast.makeText(v.getContext(), "add", Toast.LENGTH_SHORT).show();

                return false;
            }
        });


//        addOn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                BlankFragment mDetailCategoryFragment = new BlankFragment();
//                FragmentManager mFragmentManager = getFragmentManager();
//                if (mFragmentManager != null) {
//                    mFragmentManager
//                            .beginTransaction()
//                            .replace(R.id.contentFrame, mDetailCategoryFragment, BlankFragment.class.getSimpleName())
//                            .addToBackStack(null)
//                            .commit();
//                }
//            }
//        });
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bubbleNav.setCurrentActiveItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bubbleNav.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                viewPager.setCurrentItem(position, true);
            }
        });

        int newInitialPosition = 1;
        bubbleNav.setCurrentActiveItem(newInitialPosition);
        viewPager.setCurrentItem(newInitialPosition, false);
    }
}