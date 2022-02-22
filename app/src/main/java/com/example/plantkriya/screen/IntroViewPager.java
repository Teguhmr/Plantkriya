package com.example.plantkriya.screen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.plantkriya.R;

import java.util.List;

class IntroViewPager extends PagerAdapter {

    private Context mContext;
    private List<Screen_Item> screenItems;

    public IntroViewPager(Context mContext, List<Screen_Item> screenItems) {
        this.mContext = mContext;
        this.screenItems = screenItems;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen = layoutInflater.inflate(R.layout.screen_layout, null);

        ImageView imageSlide = layoutScreen.findViewById(R.id.intro_screen);

        imageSlide.setImageResource(screenItems.get(position).getScrrenImg());

        container.addView(layoutScreen);

        return layoutScreen;

    }

    @Override
    public int getCount() {
        return screenItems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
