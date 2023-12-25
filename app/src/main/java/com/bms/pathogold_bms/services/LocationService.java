package com.bms.pathogold_bms.services;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.bms.pathogold_bms.R;
import com.bms.pathogold_bms.utility.AllKeys;
import com.bms.pathogold_bms.utility.CommonMethods;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class  LocationService extends Service {

    private static final String TAG = "LocationService";
    private DigiPath digiPath;
    private boolean isFirst =false;

    @Override
    public void onCreate() {
        super.onCreate();
        digiPath = (DigiPath) getApplication();
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NotNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            locationResult.getLastLocation();
            double currentLatitude = locationResult.getLastLocation().getLatitude();
            double currentLongitude = locationResult.getLastLocation().getLongitude();

            if(!isFirst){
                Log.e(TAG, "onLocationResult: Saving coordinates." );
                CommonMethods.Companion.setPreference(getApplicationContext(), AllKeys.Companion.getLAST_LATITUDE(), String.valueOf(currentLatitude));
                CommonMethods.Companion.setPreference(getApplicationContext(),AllKeys.Companion.getLAST_LONGITUDE(), String.valueOf(currentLongitude));
            }

            Log.e(TAG, "onLocationResult: Current My Location" + currentLatitude + "\n" + currentLongitude + "");

            Toast.makeText(getApplicationContext(), "Current My Location"+ currentLatitude + "\n" + currentLongitude + "", Toast.LENGTH_SHORT).show();
            try{
                Location locationA = new Location("point A");
                locationA.setLatitude(currentLatitude);
                locationA.setLongitude(currentLongitude);
                Location locationB = new Location("point B");
                locationB.setLatitude(Double.parseDouble(Objects.requireNonNull(CommonMethods.Companion.getPrefrence(getApplicationContext(), AllKeys.Companion.getLAST_LATITUDE()))));
                locationB.setLongitude(Double.parseDouble(Objects.requireNonNull(CommonMethods.Companion.getPrefrence(getApplicationContext(), AllKeys.Companion.getLAST_LONGITUDE()))));

                //double distance = locationA.distanceTo(locationB);
                double distanceInMile= distance(locationA.getLatitude(),locationA.getLongitude(),locationB.getLatitude(),locationB.getLongitude());
                //double distanceInMile= distance(locationA.getLatitude(),locationA.getLongitude(),18.54402,73.87834);
                double distanceInKm= distanceInMile * 1.60934;

                Log.e(TAG, "onLocationResult: distanceInMile"+distanceInMile );
                Log.e(TAG, "onLocationResult: distance "+distanceInKm );

                double totalDistance=Double.parseDouble(Objects.requireNonNull(CommonMethods.Companion.getPrefrence(getApplicationContext(), AllKeys.Companion.getTOTAL_DISTANCE())))+distanceInKm;

                CommonMethods.Companion.setPreference(getApplicationContext(),AllKeys.Companion.getTOTAL_DISTANCE(),String.valueOf(totalDistance));
                CommonMethods.Companion.setPreference(getApplicationContext(),AllKeys.Companion.getLAST_LATITUDE(), String.valueOf(currentLatitude));
                CommonMethods.Companion.setPreference(getApplicationContext(),AllKeys.Companion.getLAST_LONGITUDE(), String.valueOf(currentLongitude));
                Toast.makeText(getApplicationContext(), ""+totalDistance, Toast.LENGTH_SHORT).show();
                isFirst=true;

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not Yet implemented");
    }

    private void startLocationService() {
        String channelId = "location_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(),
                channelId
        );
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Getting Distance");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Distance service working in background");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null
                    && notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId,
                        "Location Service",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        //1000*60*30
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(60000*10);
        locationRequest.setFastestInterval(60000*10);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        startForeground(AllKeys.Companion.getLOCATION_SERVICE_ID(), builder.build());
    }

    private void stopLocationServices() {
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(AllKeys.Companion.getACTION_START_LOCATION_SERVICE())) {
                    startLocationService();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
