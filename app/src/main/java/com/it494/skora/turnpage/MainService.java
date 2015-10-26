package com.it494.skora.turnpage;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;

public class MainService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static final String LIVE_CARD_TAG = "winkPageSample";

    private LiveCard mLiveCard;
    private  RemoteViews remoteView;

    private final UpdateLiveCardRunnable mUpdateLiveCardRunnable = new UpdateLiveCardRunnable();
    private static final long DELAY_MILLIS = 10 * 1000;
    //private int count = 0;
    private final EyeGesture eyeReceiver = new EyeGesture();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.google.android.glass.action.EYE_GESTURE");
        registerReceiver(eyeReceiver, filter);

        if (mLiveCard == null) {
            mLiveCard = new LiveCard(this, LIVE_CARD_TAG);

            remoteView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.activity_main);
            remoteView.setTextViewText(R.id.textView1, eyeReceiver.getCurrentCount());
            mLiveCard.setViews(remoteView);
            Intent menuIntent = new Intent(this, MainActivity.class);
            menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));
            mLiveCard.publish(PublishMode.REVEAL);


        } else {
            mLiveCard.navigate();
        }

        return START_STICKY;
    }

    private class UpdateLiveCardRunnable implements Runnable{

        private boolean mIsStopped = false;

        public void run(){
            if(!isStopped()){

                remoteView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.activity_main);
                remoteView.setTextViewText(R.id.textView1, eyeReceiver.getCurrentCount());
                mLiveCard.setViews(remoteView);
                //mHandler.postDelayed(mUpdateLiveCardRunnable, DELAY_MILLIS);
            }
        }

        public boolean isStopped() {
            return mIsStopped;
        }

        public void setStop(boolean isStopped) {
            this.mIsStopped = isStopped;
        }
    }

    @Override
    public void onDestroy() {
        if (mLiveCard != null && mLiveCard.isPublished()) {
            mUpdateLiveCardRunnable.setStop(true);
            mLiveCard.unpublish();
            mLiveCard = null;
        }
        super.onDestroy();
    }

    private class EyeGesture extends BroadcastReceiver {
        private int count = 0;
        private String currentCount;
        private final Handler mHandler = new Handler();

        public EyeGesture(){
            count = 0;
            currentCount = "Winked " + count + " times";
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("gesture").equals("WINK")) {
                count++;
                //Disable Camera Snapshot
                abortBroadcast();
                setCurrentCount("Winked " + count + " times");
                mHandler.post(mUpdateLiveCardRunnable);

            } else {
                Log.e("SOMETHING", "is detected " + intent.getStringExtra("gesture"));
            }
        }

        public void setCurrentCount(String strCount){
            currentCount = strCount;
        }
        public String getCurrentCount(){
            return currentCount;
        }
    }
}
