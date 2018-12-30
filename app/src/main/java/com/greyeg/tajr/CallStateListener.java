//package com.greyeg.tajr;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.provider.CallLog;
//import android.telephony.PhoneStateListener;
//import android.telephony.TelephonyManager;
//import android.util.Log;
//import android.widget.Toast;
//
//import static com.yalantis.ucrop.UCropFragment.TAG;
//
//public class CallStateListener extends PhoneStateListener {
//
//
//    private Context ctx;
//
//    Cursor cur;
//    @Override
//    public void onCallStateChanged(int state, String incomingNumber) {
//        //super.onCallStateChanged(state, incomingNumber);
//
//        switch (state) {
//            case TelephonyManager.CALL_STATE_IDLE:
//                Log.i(TAG, "Idle " + state);
//                //when Idle i.e no call
//                if (flag.equals("outgoingcall") ) {
//
//                    // Put in delay because call log is not updated immediately
//                    // when state changed
//                    // The dialler takes a little bit of time to write to it
//                    // 500ms seems to be enough
//                    handler.postDelayed(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            // get start of cursor
//                            Log.i("CallLogDetailsActivity","Getting Log activity...");
//
//                            cur = ctx.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null,null, CallLog.Calls.DATE + " desc");
//
//                            int number = cur.getColumnIndex(CallLog.Calls.NUMBER);
//                            int type = cur.getColumnIndex(CallLog.Calls.TYPE);
//                            int date = cur.getColumnIndex(CallLog.Calls.DATE);
//                            int duration = cur.getColumnIndex(CallLog.Calls.DURATION);
//                            //Check if call was made from sim 1 or sim 2 , if it returns 0 its from sim 1 else if 1 its from sim 2.
//                            int idSimId = getSimIdColumn(cur);
//                            String callid = "0";
//
//                            if (cur.moveToFirst() == true) {
//                                phNumber = cur.getString(number);
//                                callType = cur.getString(type);
//                                callDate = cur.getString(date);
//                                callDayTime = new Date(Long.valueOf(callDate));
//                                callDuration = Integer.valueOf(cur.getString(duration));
//                                dir = null;
//                                int dircode = Integer.parseInt(callType);
//
//                                switch (dircode) {
//                                    case CallLog.Calls.OUTGOING_TYPE:
//                                        dir = "OUTGOING";
//                                        break;
//
//                                    case CallLog.Calls.INCOMING_TYPE:
//                                        dir = "INCOMING";
//                                        break;
//
//                                    case CallLog.Calls.MISSED_TYPE:
//                                        dir = "MISSED";
//                                        break;
//
//                                }
//
//
//                                if(idSimId >= 0){
//                                    callid = cur.getString(idSimId);
//                                }
//
//
//                                cur.close();
//                                TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(ctx);
//
//                                boolean isDualSIM = telephonyInfo.isDualSIM();
//
//
//                                if (isDualSIM) {
//                                    if(callid.equals("1")){
//                                        simserailno = telephonyInfo.getImeiSIM2();
//                                    }else {
//                                        simserailno = telephonyInfo.getImeiSIM1();
//                                    }
//                                } else {
//
//                                    simserailno = tmgr.getSimSerialNumber();
//                                }
//
//
//
//
//                                if (tmgr.isNetworkRoaming()) {
//                                    roaming = 1;
//                                } else {
//                                    roaming = 0;
//                                }
//
//
//                                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//
//                                StringBuffer sb = new StringBuffer();
//                                sb.append("Outgoing Call Log"
//                                        + "\nPhone Number:--- " + phNumber
//                                        + " \nCall Type:--- " + dir
//                                        + " \nCall Date:--- " + sdfDate.format(Long.valueOf(callDate))
//                                        + " \nDual isDualSIM:--- " + isDualSIM
//                                        + " \nSIM 1 imei:--- "  + telephonyInfo.getImeiSIM1()
//                                        + " \nSIM 2 imei:--- "  + telephonyInfo.getImeiSIM2()
//                                        + " \nCalling Sim:--- " + callid
//                                        + " \nDevice Number :--- " + Imeinumber
//                                        + " \nSim Number :--- " + simserailno
//                                        + " \nSubcscriber Number :--- " + subidno
//                                        + " \nRoaming :--- " + tmgr.isNetworkRoaming()
//                                        + " \nCall duration in sec :--- " + callDuration);
//                                sb.append("\n----------------------------------");
//                                Log.i("sb", sb.toString());
//
//                                Toast.makeText(ctx, sb.toString(),Toast.LENGTH_LONG).show();
//
//                            }
//
//                            flag = "";
//
//
//                        }
//                    }, 1500);
//
//
//
//                }
//
//                break;
//            case TelephonyManager.CALL_STATE_OFFHOOK:
//                Log.i(TAG, "offhook " + state);
//
//
//                flag= "outgoingcall";
//
//
//                break;
//            case TelephonyManager.CALL_STATE_RINGING:
//                Log.i(TAG, "Ringing " + state);
//                //when Ringing
//                // Log.i(TAG, "Incomng Number to sim1: " + incomingNumber);
//                String msg = "Detected Incoming Call number: " + incomingNumber;
//                Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
//                flag = "oncall";
//                break;
//            default:
//                break;
//        }
//    }
//
//    public static int getSimIdColumn(final Cursor c) {
//
//        for (String s : new String[] { "sim_id", "simid", "sub_id" }) {
//            int id = c.getColumnIndex(s);
//            if (id >= 0) {
//                Log.d(TAG, "sim_id column found: " + s);
//                return id;
//            }
//        }
//        Log.d(TAG, "no sim_id column found");
//        return -1;
//    }
//
//}