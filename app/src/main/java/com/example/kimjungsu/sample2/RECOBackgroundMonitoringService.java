package com.example.kimjungsu.sample2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.perples.recosdk.RECOBeacon;
import com.perples.recosdk.RECOBeaconManager;
import com.perples.recosdk.RECOBeaconRegion;
import com.perples.recosdk.RECOBeaconRegionState;
import com.perples.recosdk.RECOErrorCode;
import com.perples.recosdk.RECOMonitoringListener;
import com.perples.recosdk.RECOServiceConnectListener;

import java.util.ArrayList;
import java.util.Collection;

public class RECOBackgroundMonitoringService extends Service implements RECOMonitoringListener, RECOServiceConnectListener {


    private long mScanDuration = 1*1000L;
    private long mSleepDuration = 10*1000L;
    private long mRegionExpirationTime = 60*1000L;
    private int mNotificationID = 9999;

    private RECOBeaconManager mRecoManager;
    private ArrayList<RECOBeaconRegion> mRegions;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mRecoManager = RECOBeaconManager.getInstance(getApplicationContext(), MainActivity.SCAN_RECO_ONLY, MainActivity.ENABLE_BACKGROUND_RANGING_TIMEOUT);
        this.bindRECOService();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        this.tearDown();
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    private void bindRECOService() {

        mRegions = new ArrayList<RECOBeaconRegion>();
        this.generateBeaconRegion();

        mRecoManager.setMonitoringListener(this);
        mRecoManager.bind(this);
    }

    private void generateBeaconRegion() {

        RECOBeaconRegion recoRegion;

        recoRegion = new RECOBeaconRegion(MainActivity.RECO_UUID, "RECO Sample Region");
        recoRegion.setRegionExpirationTimeMillis(mRegionExpirationTime);
        mRegions.add(recoRegion);
    }

    private void startMonitoring() {

        mRecoManager.setScanPeriod(mScanDuration);
        mRecoManager.setSleepPeriod(mSleepDuration);

        for(RECOBeaconRegion region : mRegions) {
            try {
                mRecoManager.startMonitoringForRegion(region);
            } catch (RemoteException e) {

                e.printStackTrace();
            } catch (NullPointerException e) {

                e.printStackTrace();
            }
        }
    }

    private void stopMonitoring() {

        for(RECOBeaconRegion region : mRegions) {
            try {
                mRecoManager.stopMonitoringForRegion(region);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private void tearDown() {

        this.stopMonitoring();

        try {
            mRecoManager.unbind();
        } catch (RemoteException e) {

            e.printStackTrace();
        }
    }

    @Override
    public void onServiceConnect() {

        this.startMonitoring();
        //Write the code when RECOBeaconManager is bound to RECOBeaconService
    }

    @Override
    public void didDetermineStateForRegion(RECOBeaconRegionState state, RECOBeaconRegion region) {

        if(state == RECOBeaconRegionState.RECOBeaconRegionInside){
            //  Insert Alert
            onShowNotification(true);
        }
        else if(state == RECOBeaconRegionState.RECOBeaconRegionOutside){
            onShowNotification(false);
        }
    }

    @Override
    public void didEnterRegion(RECOBeaconRegion region, Collection<RECOBeacon> beacons) {
    }

    @Override
    public void didExitRegion(RECOBeaconRegion region) {
    }

    @Override
    public void didStartMonitoringForRegion(RECOBeaconRegion region) {

        //Write the code when starting monitoring the region is started successfully
    }

    @Override
    public IBinder onBind(Intent intent) {
        // This method is not used
        return null;
    }

    @Override
    public void onServiceFail(RECOErrorCode errorCode) {
        //Write the code when the RECOBeaconService is failed.
        //See the RECOErrorCode in the documents.
        return;
    }

    @Override
    public void monitoringDidFailForRegion(RECOBeaconRegion region, RECOErrorCode errorCode) {
        //Write the code when the RECOBeaconService is failed to monitor the region.
        //See the RECOErrorCode in the documents.
        return;
    }

    public void onShowNotification(Boolean isInside){


        String content = "";

        if(isInside == true){
            content = "Amazon 선릉점에 오신걸 환영합니다. 즐거운 시간 되십시오";
        }else{
            content = "Amazon 선릉점에서는 다양한 행사를 진행중입니다. 다음에 또 방문해주세요";
        }
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        Intent pIntent = new Intent(getApplicationContext() , WebViewAictivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, pIntent, 0);


        Notification noti = new Notification.Builder(getApplicationContext())
                .setTicker("Amazon Korea")
                .setContentTitle("Amazon Tite")
                .setContentText(content)
                .setSmallIcon(R.drawable.abc_ic_go_search_api_mtrl_alpha)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pendingIntent)
                .build();

        noti.flags = noti.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);
    }
}
