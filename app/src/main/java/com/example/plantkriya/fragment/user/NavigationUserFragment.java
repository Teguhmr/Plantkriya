package com.example.plantkriya.fragment.user;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.plantkriya.R;
import com.example.plantkriya.adapter.SectionPagerAdapter;
import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.BubbleToggleView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;

public class NavigationUserFragment extends Fragment {
    private ViewPager viewPager;
    private BubbleNavigationLinearView bubbleNav;
    private BubbleToggleView add;

    public NavigationUserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navigation_user, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        bubbleNav = view.findViewById(R.id.BubbleNav);
        add = view.findViewById(R.id.add);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SectionPagerAdapter adapter = new SectionPagerAdapter(getChildFragmentManager());
        PlantUserFragment plantFragment = new PlantUserFragment();
        NoteUserFragment noteFragment = new NoteUserFragment();
        KriyaUserFragment kriyaFragment = new KriyaUserFragment();

        adapter.addFragment(plantFragment);
        adapter.addFragment(noteFragment);
        adapter.addFragment(kriyaFragment);

        add.activate();

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bubbleNav.setCurrentActiveItem(position);

                if (position == 1) {
                    toggle();
                } else {
                    toggleUp();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bubbleNav.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                viewPager.setCurrentItem(position, true);

                if (position == 1) {
                    toggle();
                } else {
                    toggleUp();
                }
            }
        });

        int newInitialPosition = 1;
        bubbleNav.setCurrentActiveItem(newInitialPosition);
        viewPager.setCurrentItem(newInitialPosition, false);
    }

    private void toggle() {
        bubbleNav.animate()
                .translationY(bubbleNav.getWidth())
                .alpha(0.0f)
                .setDuration(1000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        bubbleNav.setVisibility(View.GONE);
                    }
                });

    }

    private void toggleUp() {
        bubbleNav.animate()
                .translationY(0)
                .setDuration(400)
                .alpha(1.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        bubbleNav.setVisibility(View.VISIBLE);
                    }
                });
    }


}