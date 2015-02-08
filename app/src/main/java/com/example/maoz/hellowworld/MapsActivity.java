package com.example.maoz.hellowworld;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;


import android.os.AsyncTask;

import android.os.StrictMode;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Document;


public class MapsActivity extends navigation_drawer implements GoogleMap.OnMapLongClickListener {

    GoogleMap mMap; // Might be null if Google Play services APK is not available.

    DecimalFormat df2 = new DecimalFormat("###.#");
    Double curLat = 0.0, curLng =0.0; //เซทดีฟ้อล ไว้
    Double desLat = 0.0, desLng =0.0;
    Marker Pin;
    LatLng mPosition = new LatLng(0,0);

    GMapV2GetRouteDirection v2GetRouteDirection;
    AppLocationManager  appLocationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_maps);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_maps, null, false);
        drawerLayout.addView(contentView, 0);
        setUpMapIfNeeded();
        setting();

        //Listener
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTitle().equals("ตำแหน่งของฉัน"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),10));//ซุมแม่ง
                return false;
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {///ถ้าคลิกแล้วตำแหน่งเท่ากับ ม หัวเฉียว ให้ทำอะไร

                if (marker.getTitle().equals("มหาวิทยาลัยหัวเฉียวเฉลิมพระเกียรติ")) {//ต้องเป็นตัวแปร จากการเสิช หรือผู้ใช้ต้องการไปไหน
                    double lat = marker.getPosition().latitude;
                    double lng = marker.getPosition().longitude;
                    desLat = lat;
                    desLng = lng;
                    String distant;
                    distant = String.valueOf("ระยะห่างปลายทาง "+df2.format(FindDistance.getDistance(curLat,curLng,  lat, lng)))+" กิโลเมตร";
                    setMyToast(distant);
                    //drawLine(lat,lng); //เรียกวาดเส้น จากตำแหน่งปัจจุบันไปยังตำแหน่งที่คลิก
                    exeProcess();
                 }
                return false;
            }

        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if(appLocationManager.location()==null){
                    appLocationManager.setMostRecentLocation(mMap.getMyLocation()); //เอาค่าจากปุ่ม MyLocation ไปใส่ใน Location ต้องรีเซตก่อนแทนค่า
                    updateGPSCoordinates();
                    Log.d("----MyLocationButton = ----","take from mMap"+ curLat +","+ curLng +"");
                    setMyMarker();
                }else{
                    //ล้างค่า เพื่อทดสอบการเอาค่ามาจากปุ่ม MyLocation
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLat, curLng), 10));
                    updateGPSCoordinates();
                    setMyMarker();
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        Log.d("----DEBUG----", "On Resume worked");
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("You Click here")
                .snippet("lat "+latLng.latitude+" long "+latLng.longitude));
    }


    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
     private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }
     /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
     private void setUpMap() {
         mMap.addMarker(new MarkerOptions()
                 .position(new LatLng(13.6113456,100.759669))
                 .title("มหาวิทยาลัยหัวเฉียวเฉลิมพระเกียรติ")//ไตเติ้ลหมุด
                 .snippet("Lat"+13.6113456+"Lng"+100.759669));
         mMap.setMyLocationEnabled(true);//สร้างปุ่ม
         mMap.getUiSettings().setCompassEnabled(false);
         mMap.getUiSettings().setRotateGesturesEnabled(false);

     }
     /*
    * Function Group Setting  wait for edit
    *
    *
    * */

    private void setting(){
        ////test route
        v2GetRouteDirection = new GMapV2GetRouteDirection();
        /*SupportMapFragment supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mMap = supportMapFragment.getMap();*/
        ////
        appLocationManager = new AppLocationManager(MapsActivity.this);
        if (!appLocationManager.isEnabled()){
            turnONGPS();
        }


        mMap.setOnMapLongClickListener(this);
        Pin = mMap.addMarker(new MarkerOptions().position(mPosition));
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }
    private void setMyMarker() {//เปลี่ยนตำแหน่ง Marker
        Pin.setPosition(mPosition);
        Pin.setTitle("ตำแหน่งของฉัน");
        Pin.setSnippet("Lat:"+ curLat +"Long:"+ curLng);
        Pin.showInfoWindow();
    }
    private void setMyToast(String string) {
        LayoutInflater inflater = getLayoutInflater();
        View Layout = inflater.inflate(R.layout.custom_toast,(ViewGroup) findViewById(R.id.toast_layout_root));
        TextView textView = (TextView) Layout.findViewById(R.id.text);
        textView.setText(string);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(Layout);
        toast.show();

    }

    /*
    *
    * Calculate Function*/

    private void drawLine(Double e1,Double e2){
        PolylineOptions line = new PolylineOptions();
        line.add(new LatLng(curLat, curLng)).add(new LatLng(e1,e2)).color(Color.RED).describeContents();//วาดเส้น
        mMap.addPolyline(line);//เอาเส้นที่วาดไปลงในแผนที่
    }
    private void updateGPSCoordinates(){
        if (appLocationManager.location() != null){
            Log.d("----UPDATECOORD----","WORKING");

            curLat = appLocationManager.getLatitude();
            curLng = appLocationManager.getLongitude();
            mPosition = new LatLng(curLat, curLng);
            setMyMarker();
        }else{
            Log.d("----UPDATECOORD----","NOTHING");
        }
    }
    private void exeProcess(){
        String d;
        JSONParsing j = new JSONParsing();
        d = j.getDocument(new LatLng(curLat, curLng), new LatLng(desLat, desLng));
        List<List<HashMap<String, String>>> line = j.GetLine(d);
        Drawline(line);
    }
    private void Drawline(List<List<HashMap<String, String>>> result){
        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;
        String distance = "";
        String duration = "";

        // Traversing through all the routes
        for(int i=0;i<result.size();i++){
            points = new ArrayList<>();
            lineOptions = new PolylineOptions().color(Color.BLUE).width(5);

            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);

            // Fetching all the points in i-th route
            for(int j=0;j<path.size();j++){
                HashMap<String,String> point = path.get(j);
                if(j==0){    // Get distance from the list
                    distance = point.get("distance");
                    continue;
                }else if(j==1){ // Get duration from the list
                    duration = point.get("duration");
                    continue;
                }

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
        }
        setMyToast("Distance:"+distance + ", Duration:"+duration);

        // Drawing polyline in the Google Map for the i-th route
        mMap.addPolyline(lineOptions);
    }

    private class xmlRouteTask extends AsyncTask<String, Void, String> {
        private ProgressDialog Dialog;
        String response = "";
        Document document;
        String d;

        @Override
        protected void onPreExecute() {
            Dialog = new ProgressDialog(MapsActivity.this);
            Dialog.setMessage("Loading route...");
            Dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            //Get All Route values ส่งค่าพิกัดไปเอาข้อมูลจาก api
            document = v2GetRouteDirection.getDocument(new LatLng(curLat, curLng), new LatLng(desLat, desLng));
            response = "Success";
            return d;
        }

        @Override
        protected void onPostExecute(String result) {
// รับค่าเส้นทางมาวาด

            if (response.equalsIgnoreCase("Success")) {
                ArrayList<LatLng> directionPoint = v2GetRouteDirection.getDirection(document);
                int distance = v2GetRouteDirection.getDistanceValue(document);
                PolylineOptions rectLine = new PolylineOptions().width(7).color(Color.BLUE);
                for (int i = 0; i < directionPoint.size(); i++) {
                    rectLine.add(directionPoint.get(i));
                }
                // Adding route on the map
                mMap.addPolyline(rectLine);
                setMyToast(String.valueOf("ระยะเส้นทาง "+df2.format(distance/1000))+" กิโลเมตร");
                Dialog.dismiss();

            }

        }//end getRouteClass

    }
    private void turnONGPS(){
        // Build the alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location Services Not Active");
        builder.setMessage("Please turn on Location service")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Show location settings when the user acknowledges the alert dialog
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                                dialogInterface.dismiss();
                            }
                        }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }
}