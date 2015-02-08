package com.example.maoz.hellowworld;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;


/**
 * Created by jitsuem on 2/1/2015.
 */
public class AppLocationManager implements LocationListener {

    private LocationManager locationManager;
    private Location location;
    private double latitude;
    private double longitude;
    private Criteria criteria;
    private String provider;
    boolean isGPSEnabled = false; // ตัวแปรเช็คว่าเปิดหรือปิด
    boolean isNetworkEnabled = false;
    private boolean Enable = false;


    public AppLocationManager(Context context) {
        locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        provider = locationManager.getBestProvider(criteria, true);
        
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); //เปิดหรือปิด
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled){
            Log.d("----Please----", "Turn on Location");
            Enable = false;
        }else{
            Enable = true;
            if (isGPSEnabled){
                if (location == null){ // ถ้าโลเคชั่นว่าง ให้ไปร้องขอ
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 10, this);//get value from GPS
                    Log.d("----GPS----","reqLocation");
                    if (locationManager != null){ //โอเคเอาค่าล่าสุดที่รู้ไปให้ Location จาก GPS
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        setMostRecentLocation(location); //ปรับปรุงค่า Lat Long
                        Log.d("----Location gps not null = ----","GPS"+ getLatitude() +","+ getLongitude() +"");
                    }
                }
            }
            if (isNetworkEnabled){ // ถ้าเปิดให้ไปร้องขอตำแหน่ง
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 10, this);
                Log.d("----Network----","reqLocation");
                if (locationManager != null){//โอเคเอาค่าล่าสุดที่รู้ไปให้ Location จาก NETWORK
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    setMostRecentLocation(location); //ปรับปรุงค่า Lat Long
                    Log.d("----Location net not null = ----","Network"+ getLatitude() +","+ getLongitude() +"");
                }
            }

        }

    }
    public void setMostRecentLocation(Location lastKnownLocation) {
        latitude = lastKnownLocation.getLatitude();
        longitude = lastKnownLocation.getLongitude();
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Location location(){
        return location;
    }
    public Boolean isEnabled(){
        return Enable;
    }


    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        Log.d("----ApplocationManager----","UPDATED");

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }




}
