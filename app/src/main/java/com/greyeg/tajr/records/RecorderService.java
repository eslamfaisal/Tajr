package com.greyeg.tajr.records;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.greyeg.tajr.order.CurrentOrderData;

import java.io.IOException;

/**
 * Created by VS00481543 on 30-10-2017.
 */

public class RecorderService extends Service {

    MediaRecorder recorder;
    public static String rec;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        recorder = new MediaRecorder();
        recorder.reset();

        String phoneNumber = CurrentOrderData.getInstance().getCurrentOrderResponse().getOrder().getPhone1();

        String time = new CommonMethods().getTIme();
        CurrentOrderData.getInstance().setCallTime(time);
        String path = new CommonMethods().getPath();

        rec = path + "/" + phoneNumber + "_" + time + ".amr";

        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        recorder.setOutputFile(rec);

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();

        return START_NOT_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();

        recorder.stop();
        recorder.reset();
        recorder.release();
        recorder = null;
    }
}
