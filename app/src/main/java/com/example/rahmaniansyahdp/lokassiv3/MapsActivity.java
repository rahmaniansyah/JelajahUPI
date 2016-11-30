package com.example.rahmaniansyahdp.lokassiv3;

import android.*;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, SensorEventListener {

    //deklarasi variabel
    private static final int MY_PERMISSION_REQUEST = 99;
    private GoogleMap mMap;
    private Marker mPosSekarang;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private LocationManager locationManager;

    //deklarasi variabel database
    Dbpostjelajahupi db;

    //deklarasi layout Quest
    Dialog firstAction;
    Dialog gikQuest;

    //deklarasi status quest
    int Qgik = 0;
    int Qgymnas = 0;

    //deklarasi poin bermain
    int score = 0;
    TextView txt_score;

    //deklarasi untuk sensor accelerometer
    private SensorManager sm;
    private Sensor senAccel;


    //prosedur perubahan waktu lokasi
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        //10 detik sekali minta lokasi
        mLocationRequest.setInterval(10000);
        //interval tidak lebih dari 5 detik
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    //prosedur integrasi MAP
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildGoogleApiClient();
        createLocationRequest();

        //database
        //insert lokasi
        db = new Dbpostjelajahupi(getApplicationContext());

        db.open();
        db.insertLokasiJelajahUpi("FPMIPA-C", "107.589643", "107.590130", "-6.860195", "-6.860367");
        db.insertLokasiJelajahUpi("GymnasiumUPI", "107.589623", "107.590433", "-6.859617", "-6.860134");


        db.close();

        txt_score = (TextView) findViewById(R.id.txtSkor);

        //sensor akselerometer
        sm = (SensorManager) getSystemService(getApplicationContext().SENSOR_SERVICE);
        senAccel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    //prosedur untuk mengetahui lokasi
    public void ambilLokasi() {
        //cek apakah sudah diijinkan oleh user, jika belum tampilkan dialog
        //Pastikan permission yang diminta cocok dengan manifest
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //belum ada ijin, tampilkan dialog
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_REQUEST);
            return;
        }
        //mendapatkan perubahan lkasi
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSION_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ambilLokasi();
            } else {
                //premission  tidak diberikan
                AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setMessage("Tidak mendapat ijin, tidak dapat mengambil lokasi");
                ad.show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng posSekarang = new LatLng(-6.860418, 107.589889);
        mPosSekarang = mMap.addMarker(new
                MarkerOptions().position(posSekarang).title("Posisi Sekarang"));

        //batas lokasi UPI
        //urutan harus kiri bawah, kanna atas kotak
        LatLngBounds UPI = new LatLngBounds(
                new LatLng(-6.863273, 107.587212), new LatLng(-6.858025, 107.597839));

        //marker gedung ilkom
        //LatLng gedungIlkom = new LatLng(-6.860418, 107.589889) ;
        //mMap.addMarker(new MarkerOptions().position(gedungIlkom).title("Marker di GIK")) ;

        //set Kamera sesuai batas UPI

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.12); //offset dari edges

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(UPI, width, height, padding));
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
        mMap.setMyLocationEnabled(true);
        mMap.setBuildingsEnabled(true);


        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gedungIlkom, 17));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        ambilLokasi();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop(){
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(MapsActivity.this,"Update data",Toast.LENGTH_SHORT).show();
        mPosSekarang.setPosition(new
                LatLng(location.getLatitude(),location.getLongitude()));

        db.open();
        Dbpostjelajahupi.Lokasi lokasi[] = db.getAllLokasi();

        if(lokasi[0].down_s <= location.getLatitude() && location.getLatitude() <= lokasi[0].up_s && lokasi[0].left_e <= location.getLongitude() && location.getLongitude() <= lokasi[0].right_e && Qgik == 0 ){
            showFirstAction(lokasi[0].nama_lokasi);
            Qgik = 1 ;
            Toast.makeText(MapsActivity.this,"Anda didalam gik",Toast.LENGTH_SHORT).show();
        }


        if(lokasi[1].down_s <= location.getLatitude() && location.getLatitude() <= lokasi[1].up_s && lokasi[1].left_e <= location.getLongitude() && location.getLongitude() <= lokasi[1].right_e && Qgymnas == 0 ){
            showFirstAction(lokasi[1].nama_lokasi);
            Qgymnas = 1 ;
            Toast.makeText(MapsActivity.this,"Anda didalam Gymnasium UPI",Toast.LENGTH_SHORT).show();
        }

        db.close();

    }

    public void showFirstAction(String tempat){
        firstAction = new Dialog(MapsActivity.this) ;
        firstAction.setContentView(R.layout.first_action);
        firstAction.setCancelable(true);
        firstAction.show();

        TextView textLokasi = (TextView) firstAction.findViewById(R.id.textView8) ;
        textLokasi.setText("Hai, Sekarang kamu sedang berada di "+tempat);
    }

    public void showFpmipacQuest(String keterangan){
        gikQuest = new Dialog(MapsActivity.this);
        gikQuest.setContentView(R.layout.fpmipac_layout);
        gikQuest.setCancelable(true);
        gikQuest.show();

        final RadioGroup radioGroup ;
        radioGroup = (RadioGroup)gikQuest.findViewById(R.id.radiofpmipaC) ;         //radio group
        TextView textSoal = (TextView) gikQuest.findViewById(R.id.textView) ;       //text soal
        Button buttonfpmipac = (Button) gikQuest.findViewById(R.id.btn_qfpmipac) ;  //tombol
        //pilihan jawaban
        RadioButton a, b, c, d ;
        a = (RadioButton) gikQuest.findViewById(R.id.rd7) ;
        b = (RadioButton) gikQuest.findViewById(R.id.rd8) ;
        c = (RadioButton) gikQuest.findViewById(R.id.rd9) ;
        d = (RadioButton) gikQuest.findViewById(R.id.rd10) ;

        if(keterangan.equals("FPMIPA-C")){

            buttonfpmipac.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int selectedId = radioGroup.getCheckedRadioButtonId();

                    if(selectedId == R.id.rd7){
                        Toast.makeText(MapsActivity.this,"Benar",Toast.LENGTH_SHORT).show();
                        score = score + 100 ;
                        txt_score.setText("Skor : "+score);
                    }else{
                        Toast.makeText(MapsActivity.this,"Maaf jawaban kamu salah",Toast.LENGTH_SHORT).show();
                        Qgik = 0 ;
                    }
                    gikQuest.dismiss();
                }
            });

        }else if(keterangan.equals("GYMNASIUM")){
            textSoal.setText("Menurut tradisi yang ada di UPI biasa digunakan untuk apakah gedung Gymnasium UPI?");
            a.setText("Wisuda Mahasiswa UPI");
            b.setText("Studio Musik");
            c.setText("Kolam Renang");
            d.setText("Konser band");
            buttonfpmipac.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int selectedId = radioGroup.getCheckedRadioButtonId();

                    if(selectedId == R.id.rd7){
                        Toast.makeText(MapsActivity.this,"Benar",Toast.LENGTH_SHORT).show();
                        score = score + 100 ;
                        txt_score.setText("Skor : "+score);
                    }else{
                        Toast.makeText(MapsActivity.this,"Maaf jawaban kamu salah",Toast.LENGTH_SHORT).show();
                        Qgymnas = 0 ;
                    }
                    gikQuest.dismiss();
                }
            });
        }


    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double ax=0, ay=0, az=0 ;
        //menangkap nilai sensor
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            ax = event.values[0];
            ay = event.values[1];
            az = event.values[2];
        }

        if(az <= -1 && Qgik==1){
            showFpmipacQuest("FPMIPA-C") ;
            Qgik = 2 ;
            firstAction.dismiss();
        }

        if(az <= -1 && Qgymnas==1){
            showFpmipacQuest("GYMNASIUM") ;
            Qgymnas = 2 ;
            firstAction.dismiss();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(this, senAccel, SensorManager. SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause(){
        super.onPause();
        sm.unregisterListener(this);
    }
}
