package com.greyeg.tajr.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.greyeg.tajr.R;
import com.thefinestartist.movingbutton.MovingButton;
import com.thefinestartist.movingbutton.enums.ButtonPosition;
import com.thefinestartist.movingbutton.enums.MoveDirection;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmptyCallActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    MovingButton movingButton;
    @BindView(R.id.current_position_info)
    TextView currentPositionInfo;
    @BindView(R.id.move_direction_bt)
    View moveDirectionBt;
    @BindView(R.id.move_direction_tv)
    TextView moveDirectionTv;
    @BindView(R.id.movement_left_bt)
    View movementLeftBt;
    @BindView(R.id.movement_left_tv)
    TextView movementLeftTv;
    @BindView(R.id.movement_right_bt)
    View movementRightBt;
    @BindView(R.id.movement_right_tv)
    TextView movementRightTv;
    @BindView(R.id.movement_top_bt)
    View movementTopBt;
    @BindView(R.id.movement_top_tv)
    TextView movementTopTv;
    @BindView(R.id.movement_bottom_bt)
    View movementBottomBt;
    @BindView(R.id.movement_bottom_tv)
    TextView movementBottomTv;
    @BindView(R.id.movement_inner_offset_bt)
    View innerOffsetBt;
    @BindView(R.id.movement_inner_offset_tv)
    TextView innerOffsetTv;
    @BindView(R.id.movement_outer_offset_bt)
    View outerOffsetBt;
    @BindView(R.id.movement_outer_offset_tv)
    TextView outerOffsetTv;
    @BindView(R.id.volume_bt)
    View volumeBt;
    @BindView(R.id.volume_tv)
    TextView volumeTv;
    @BindView(R.id.vibration_duration_bt)
    View vibrationDurationBt;
    @BindView(R.id.vibration_duration_tv)
    TextView vibrationDurationTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_call);

        ButterKnife.bind(this);
        finish();
        setSupportActionBar(toolbar);

//        movingButton.setOnPositionChangedListener(new MovingButton.OnPositionChangedListener() {
//            @Override
//            public void onPositionChanged(int action, ButtonPosition position) {
//                currentPositionInfo.setText(position.name());
//            }
//        });
//
//        moveDirectionBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new MaterialDialog.Builder(EmptyCallActivity.this)
//                        .title(getString(R.string.move_direction))
//                        .items(getMoveDirectionNames())
//                        .itemsCallbackSingleChoice(movingButton.getMoveDirection().ordinal(), new MaterialDialog.ListCallbackSingleChoice() {
//                            @Override
//                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//                                MoveDirection moveDirection = MoveDirection.values()[which];
//                                movingButton.setMoveDirection(moveDirection);
//                                moveDirectionTv.setText(moveDirection.name());
//                                return true;
//                            }
//                        })
//                        .positiveText(getString(R.string.choose))
//                        .show();
//            }
//        });
//
//        movementLeftBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new MaterialDialog.Builder(EmptyCallActivity.this)
//                        .title(getString(R.string.movement_left))
//                        .items(getDPLists())
//                        .itemsCallbackSingleChoice(dpFromPx(movingButton.getMovementLeft()) + 10, new MaterialDialog.ListCallbackSingleChoice() {
//                            @Override
//                            public boolean onSelection(MaterialDialog materialDialog, View view, int which, CharSequence text) {
//                                movingButton.setMovementLeft(pxFromDp(which - 10));
//                                movementLeftTv.setText("" + (which - 10) + "dp");
//                                return true;
//                            }
//                        })
//                        .positiveText(getString(R.string.choose))
//                        .show();
//            }
//        });
//
//        movementRightBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new MaterialDialog.Builder(EmptyCallActivity.this)
//                        .title(getString(R.string.movement_right))
//                        .items(getDPLists())
//                        .itemsCallbackSingleChoice(dpFromPx(movingButton.getMovementRight()) + 10, new MaterialDialog.ListCallbackSingleChoice() {
//                            @Override
//                            public boolean onSelection(MaterialDialog materialDialog, View view, int which, CharSequence text) {
//                                movingButton.setMovementRight(pxFromDp(which - 10));
//                                movementRightTv.setText("" + (which - 10) + "dp");
//                                return true;
//                            }
//                        })
//                        .positiveText(getString(R.string.choose))
//                        .show();
//            }
//        });
//
//        movementTopBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new MaterialDialog.Builder(EmptyCallActivity.this)
//                        .title(getString(R.string.movement_top))
//                        .items(getDPLists())
//                        .itemsCallbackSingleChoice(dpFromPx(movingButton.getMovementTop()) + 10, new MaterialDialog.ListCallbackSingleChoice() {
//                            @Override
//                            public boolean onSelection(MaterialDialog materialDialog, View view, int which, CharSequence text) {
//                                movingButton.setMovementTop(pxFromDp(which - 10));
//                                movementTopTv.setText("" + (which - 10) + "dp");
//                                return true;
//                            }
//                        })
//                        .positiveText(getString(R.string.choose))
//                        .show();
//            }
//        });
//
//        movementBottomBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new MaterialDialog.Builder(EmptyCallActivity.this)
//                        .title(getString(R.string.movement_bottom))
//                        .items(getDPLists())
//                        .itemsCallbackSingleChoice(dpFromPx(movingButton.getMovementBottom()) + 10, new MaterialDialog.ListCallbackSingleChoice() {
//                            @Override
//                            public boolean onSelection(MaterialDialog materialDialog, View view, int which, CharSequence text) {
//                                movingButton.setMovementBottom(pxFromDp(which - 10));
//                                movementBottomTv.setText("" + (which - 10) + "dp");
//                                return true;
//                            }
//                        })
//                        .positiveText(getString(R.string.choose))
//                        .show();
//            }
//        });
//
//        innerOffsetBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new MaterialDialog.Builder(EmptyCallActivity.this)
//                        .title(getString(R.string.inner_offset))
//                        .items(getDPLists2())
//                        .itemsCallbackSingleChoice(dpFromPx(movingButton.getOffSetInner()), new MaterialDialog.ListCallbackSingleChoice() {
//                            @Override
//                            public boolean onSelection(MaterialDialog materialDialog, View view, int which, CharSequence text) {
//                                movingButton.setOffSetInner(pxFromDp(which));
//                                innerOffsetTv.setText("" + which + "dp");
//                                return true;
//                            }
//                        })
//                        .positiveText(getString(R.string.choose))
//                        .show();
//            }
//        });
//
//        outerOffsetBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new MaterialDialog.Builder(EmptyCallActivity.this)
//                        .title(getString(R.string.outer_offset))
//                        .items(getDPLists2())
//                        .itemsCallbackSingleChoice(dpFromPx(movingButton.getOffSetOuter()), new MaterialDialog.ListCallbackSingleChoice() {
//                            @Override
//                            public boolean onSelection(MaterialDialog materialDialog, View view, int which, CharSequence text) {
//                                movingButton.setOffSetOuter(pxFromDp(which));
//                                outerOffsetTv.setText("" + which + "dp");
//                                return true;
//                            }
//                        })
//                        .positiveText(getString(R.string.choose))
//                        .show();
//            }
//        });
//
//        volumeBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new MaterialDialog.Builder(EmptyCallActivity.this)
//                        .title(getString(R.string.volume))
//                        .items(getVolumeLists())
//                        .itemsCallbackSingleChoice(movingButton.getEventVolume(), new MaterialDialog.ListCallbackSingleChoice() {
//                            @Override
//                            public boolean onSelection(MaterialDialog materialDialog, View view, int which, CharSequence text) {
//                                movingButton.setEventVolume(which);
//                                volumeTv.setText("" + which);
//                                return true;
//                            }
//                        })
//                        .positiveText(getString(R.string.choose))
//                        .show();
//            }
//        });
//
//        vibrationDurationBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new MaterialDialog.Builder(EmptyCallActivity.this)
//                        .title(getString(R.string.vibration_duration))
//                        .items(getVibrationLists())
//                        .itemsCallbackSingleChoice(movingButton.getVibrationDuration(), new MaterialDialog.ListCallbackSingleChoice() {
//                            @Override
//                            public boolean onSelection(MaterialDialog materialDialog, View view, int which, CharSequence text) {
//                                movingButton.setVibrationDuration(which);
//                                vibrationDurationTv.setText(which + " Milliseconds");
//                                return true;
//                            }
//                        })
//                        .positiveText(getString(R.string.choose))
//                        .show();
//            }
//        });

        movingButton = findViewById(R.id.moving_button2);
        movingButton.setMovementLeft(100);

        movingButton.setMovementRight(100);

        movingButton.setMovementTop(100);

        movingButton.setMovementBottom(100);

// Offset (in Pixel dimension)

//        movingButton.setOffSetInner(10);
//
//        movingButton.setOffSetOuter(10);

// Vibration

        //  movingButton.setVibrationDuration(20);

// Volume

        //  movingButton.setEventVolume(50);

// Current Position
        movingButton.setOnPositionChangedListener(new MovingButton.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int action, ButtonPosition position) {
                //your code here

                currentPositionInfo.setText(position.name());
            }

            @Override
            public void moveUp(String position) {
                Log.d("bbbbbbbbbbb", "moveUp: " + position);
            }
        });

    }

    private String[] getMoveDirectionNames() {
        MoveDirection[] states = MoveDirection.values();
        String[] names = new String[states.length];
        for (int i = 0; i < states.length; i++)
            names[i] = states[i].name();
        return names;
    }

    private String[] getVibrationLists() {
        String[] names = new String[101];
        for (int i = 0; i < names.length; i++)
            names[i] = "" + i + " Milliseconds";
        return names;
    }

    private String[] getDPLists() {
        String[] names = new String[41];
        for (int i = 0; i < names.length; i++)
            names[i] = "" + (i - 10) + "dp";
        return names;
    }

    private String[] getDPLists2() {
        String[] names = new String[51];
        for (int i = 0; i < names.length; i++)
            names[i] = "" + i + "dp";
        return names;
    }

    private String[] getVolumeLists() {
        String[] names = new String[101];
        for (int i = 0; i < names.length; i++)
            names[i] = "" + i;
        return names;
    }

    private int dpFromPx(int px) {
        return (int) (px / getResources().getDisplayMetrics().density);
    }

    private int pxFromDp(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
