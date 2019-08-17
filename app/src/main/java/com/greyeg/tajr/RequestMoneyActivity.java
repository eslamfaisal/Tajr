package com.greyeg.tajr;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.greyeg.tajr.activities.LoginActivity;
import com.greyeg.tajr.helper.SharedHelper;
import com.greyeg.tajr.helper.font.RobotoTextView;
import com.greyeg.tajr.models.MoneyRequestResponse;
import com.greyeg.tajr.server.Api;
import com.greyeg.tajr.server.BaseClient;
import com.greyeg.tajr.view.FloatLabeledEditText;
import com.greyeg.tajr.view.ProgressWheel;
import com.greyeg.tajr.view.kbv.KenBurnsView;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RequestMoneyActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    public static final String SPLASH_SCREEN_OPTION = "com.csform.android.uiapptemplate.SplashScreensActivity";
    public static final String SPLASH_SCREEN_OPTION_1 = "Fade in + Ken Burns";
    public static final String SPLASH_SCREEN_OPTION_2 = "Down + Ken Burns";
    public static final String SPLASH_SCREEN_OPTION_3 = "Down + fade in + Ken Burns";

    public static final String USER_NAME = "username";
    public static final String USER_TYPE = "user_type";
    public static final String USER_ID = "user_id";
    public static final String IS_TAJR = "is_tajr";
    public static final String PARENT_TAJR_ID = "parent_tajr_id";
    public static final String TOKEN = "token";
    public static final String IS_LOGIN = "is_login";

    @BindView(R.id.loginbtn)
    RobotoTextView loginBtn;

    @BindView(R.id.money)
    FloatLabeledEditText money;

    @BindView(R.id.notes)
    FloatLabeledEditText notes;

    @BindView(R.id.phone)
    FloatLabeledEditText phone;

    @BindView(R.id.ken_burns_images)
    KenBurnsView mKenBurns;

    @BindView(R.id.logo)
    ImageView mLogo;

    @BindView(R.id.progress_log_in)
    ProgressWheel progressLogin;

    ProgressDialog progressDialog;
    Api api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_money);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.request_money));
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.wati_to_log_in));
        api = BaseClient.getBaseClient().create(Api.class);
        setAnimation(SPLASH_SCREEN_OPTION_3);
//        loginBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), MainActivity.class));
//            }
//        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setVisibility(View.GONE);
                progressLogin.setVisibility(View.VISIBLE);
                progressLogin.spin();
                if (money.getText().equals("") || notes.getText().equals("")||phone.getText().equals("")) {
                    progressDialog.dismiss();
                    Toast.makeText(RequestMoneyActivity.this, "برجاء ادخال البريد وكلمة المرور", Toast.LENGTH_SHORT).show();
                } else {
                    api.requestCash(
                            SharedHelper.getKey(getApplicationContext(),LoginActivity.TOKEN),
                                    SharedHelper.getKey(getApplicationContext(),LoginActivity.USER_ID),
                                    phone.getText().toString(),
                                    money.getText().toString(),
                                    notes.getText().toString()
                    ).enqueue(new Callback<MoneyRequestResponse>() {
                        @Override
                        public void onResponse(Call<MoneyRequestResponse> call, Response<MoneyRequestResponse> response) {
                            Log.d("eeeeeeeeee", "response: "+response.body().getData());
                            if (response.body().getCode().equals("1200")||response.body().getCode().equals("1202")){
                                Toast.makeText(RequestMoneyActivity.this, "تم طلب سحب المال بنجاح", Toast.LENGTH_SHORT).show();

                                money.setText("");
                                notes.setText("");
                                phone.setText("");

                            }
                            progressLogin.setVisibility(View.GONE);
                            loginBtn.setVisibility(View.VISIBLE);
                            Toast.makeText(RequestMoneyActivity.this, response.body().getData(), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Call<MoneyRequestResponse> call, Throwable t) {
                            Log.d("eeeeeeeeee", "onFailure: "+t.getMessage());
                            progressLogin.setVisibility(View.GONE);
                            loginBtn.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
//        getCallLogs();
    }

    private void setAnimation(String category) {
        if (category.equals(SPLASH_SCREEN_OPTION_1)) {
            mKenBurns.setImageResource(R.drawable.background_media);
            animation1();
        } else if (category.equals(SPLASH_SCREEN_OPTION_3)) {
            mKenBurns.setImageResource(R.drawable.ic_traffic);
            animation2();
            animation3();
        }
    }

    private void animation1() {
        ObjectAnimator scaleXAnimation = ObjectAnimator.ofFloat(mLogo, "scaleX", 5.0F, 1.0F);
        scaleXAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleXAnimation.setDuration(1200);
        ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(mLogo, "scaleY", 5.0F, 1.0F);
        scaleYAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleYAnimation.setDuration(1200);
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(mLogo, "alpha", 0.0F, 1.0F);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setDuration(1200);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleXAnimation).with(scaleYAnimation).with(alphaAnimation);
        animatorSet.setStartDelay(500);
        animatorSet.start();
    }

    private void animation2() {
        mLogo.setAlpha(1.0F);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate_top_to_center);
        mLogo.startAnimation(anim);
    }

    private void animation3() {

//        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(email, "alpha", 0.0F, 1.0F);
//        alphaAnimation.setStartDelay(1700);
//        alphaAnimation.setDuration(500);
//        alphaAnimation.start();

    }
}

