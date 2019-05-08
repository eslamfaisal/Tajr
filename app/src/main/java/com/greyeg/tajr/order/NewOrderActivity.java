package com.greyeg.tajr.order;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.greyeg.tajr.R;
import com.greyeg.tajr.helper.GuiManger;
import com.greyeg.tajr.order.fragments.CurrentOrderFragment;

public class NewOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        GuiManger.getInstance().setActivity(this);
        GuiManger.getInstance().setFragmentManager(getSupportFragmentManager());
        GuiManger.getInstance().setcurrFragment(new CurrentOrderFragment());
    }

    //GUIManger Methods
    public static void update() {
        GuiManger.getInstance().getFragmentManager().beginTransaction().addToBackStack("")
                .replace(R.id.Handle_Frame, GuiManger.getInstance().getcurrFragment(), null).commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        if (GuiManger.getInstance().getcurrFragment() instanceof CurrentOrderFragment){
            finish();
        }else
        super.onBackPressed();
    }
}
