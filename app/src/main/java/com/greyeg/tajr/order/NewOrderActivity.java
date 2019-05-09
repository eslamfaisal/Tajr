package com.greyeg.tajr.order;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.greyeg.tajr.R;
import com.greyeg.tajr.helper.GuiManger;
import com.greyeg.tajr.order.fragments.CurrentOrderFragment;
import com.greyeg.tajr.order.models.CurrentOrderResponse;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewOrderActivity extends AppCompatActivity {


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    //GUIManger Methods
    public static void update() {
        GuiManger.getInstance().getFragmentManager().beginTransaction().addToBackStack("")
                .replace(R.id.Handle_Frame, GuiManger.getInstance().getcurrFragment(), null).commitAllowingStateLoss();
    }

    public static void
    finishWork() {
        GuiManger.getInstance().getActivity().finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        GuiManger.getInstance().setActivity(this);
        GuiManger.getInstance().setFragmentManager(getSupportFragmentManager());
        GuiManger.getInstance().setcurrFragment(new CurrentOrderFragment());
    }

    @Override
    public void onBackPressed() {
        if (GuiManger.getInstance().getcurrFragment() instanceof CurrentOrderFragment) {
            finish();
        } else
            super.onBackPressed();
    }
}
