package com.greyeg.tajr.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.greyeg.tajr.MainActivity;
import com.greyeg.tajr.R;
import com.greyeg.tajr.helper.SharedHelper;
import com.greyeg.tajr.models.UserResponse;
import com.greyeg.tajr.server.Api;
import com.greyeg.tajr.server.BaseClient;
import com.rafakob.drawme.DrawMeButton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    public static final String USER_NAME = "username";
    public static final String USER_TYPE = "user_type";
    public static final String USER_ID = "user_id";
    public static final String IS_TAJR = "is_tajr";
    public static final String PARENT_TAJR_ID = "parent_tajr_id";
    public static final String TOKEN = "token";
    public static final String IS_LOGIN = "is_login";

    @BindView(R.id.loginbtn)
    DrawMeButton loginBtn;

    @BindView(R.id.email)
    EditText email;

    @BindView(R.id.password)
    EditText pass;


    ProgressDialog progressDialog;
    Api api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.wati_to_log_in));
        api = BaseClient.getBaseClient().create(Api.class);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                if (email.getText().equals("") || pass.getText().equals("")) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "برجاء ادخال البريد وكلمة المرور", Toast.LENGTH_SHORT).show();
                } else {
                    api.login(email.getText().toString(), pass.getText().toString()).enqueue(new Callback<UserResponse>() {
                        @Override
                        public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                            if (response.body() != null) {
                                if (response.body().getResponse() == null) {
                                    SharedHelper.putKey(getApplicationContext(), IS_LOGIN, "yes");
                                    SharedHelper.putKey(getApplicationContext(), USER_NAME, response.body().getData().getLogin_data().getUsername());
                                    SharedHelper.putKey(getApplicationContext(), USER_ID, response.body().getData().getLogin_data().getUser_id());
                                    SharedHelper.putKey(getApplicationContext(), USER_TYPE, response.body().getData().getLogin_data().getUser_type());
                                    SharedHelper.putKey(getApplicationContext(), PARENT_TAJR_ID, response.body().getData().getLogin_data().getParent_tajr_id());
                                    SharedHelper.putKey(getApplicationContext(), IS_TAJR, response.body().getData().getLogin_data().getIs_tajr());
                                    SharedHelper.putKey(getApplicationContext(), TOKEN, response.body().getData().getLogin_data().getToken());
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    progressDialog.dismiss();
                                    finish();

                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, response.body().getResponse(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<UserResponse> call, Throwable t) {
                            progressDialog.dismiss();
                            Log.d("eeeeeeeeeeeeeeee", "onFailure: " + t.getMessage());
                        }
                    });
                }
            }
        });
//        getCallLogs();
    }
//
//    public void editToFile() {
//        File file = new File(Environment.getExternalStorageDirectory(), "fileOut.txt");
//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
//            writer.write(mEdit.getText().toString());
//            writer.newLine();
//            writer.flush();
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void getCallLogs() {
//        final String[] projection = null;
//        final String selection = null;
//        final String[] selectionArgs = null;
//        final String sortOrder = "DATE DESC";
//        Cursor cursor = this.getContentResolver().query(Uri.parse("content://call_log/calls"), projection, selection, selectionArgs, sortOrder);
//        int j = 0;
//        //mEdit is EditText
//        mEdit.setText("");
//        if (cursor != null) {
//            int L = cursor.getColumnCount();
//            for (int i = 0; i < L; i++)
//                mEdit.append(cursor.getColumnName(i) + "\t");
//            mEdit.append("\n");
//            while (cursor.moveToNext()) {
//                j++;
//                if (j >= 5)
//                    break;
//                for (int i = 0; i < L; i++) {
//                    String callField = cursor.getString(i);
//                    mEdit.append(callField + "\t");
//                }
//                mEdit.append("\n");
//            }
//        }
//        editToFile();
//    }

}

