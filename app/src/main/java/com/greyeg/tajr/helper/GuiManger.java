package com.greyeg.tajr.helper;

import android.app.Activity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.greyeg.tajr.order.NewOrderActivity;

public class GuiManger {

    private Activity activity;

    private static GuiManger gui_manger = new GuiManger();

    private GuiManger() {
    }

    public static GuiManger getInstance() {
        return gui_manger;
    }

    private Fragment currFragment;
    private FragmentManager fragmentManager;

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public Fragment getcurrFragment() {
        return currFragment;
    }

    public void setcurrFragment(Fragment curr_fragment) {
        currFragment = curr_fragment;
        NewOrderActivity.update();
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }
}
