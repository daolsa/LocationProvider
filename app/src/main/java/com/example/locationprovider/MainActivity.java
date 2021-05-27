package com.example.locationprovider;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private boolean hayServicio;
    private static final int TIEMPO_REQUEST = 100; // tiempo en ms
    private static final int ESPACIO_REQUEST = 5; // espacio en m
    private static final int REQUEST_CODE = 100;
    private LocationManager locationManager = null;
    private LocationListener locationListener = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hayServicio = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationListener = obtenerLocationListener();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        solicitarActualizaciones(locationManager, locationListener);

    }

    public LocationListener obtenerLocationListener() {
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // En este ejemplo, queremos obtener información sobre el cambio de posición
                double latitud = location.getLatitude();
                double longitud = location.getLongitude();
                Toast.makeText(MainActivity.this, "Lat: " + latitud + ", Long: " + longitud, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                hayServicio = false;
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                hayServicio = true;
            }
        };

        return locationListener;

    }

    public void solicitarActualizaciones(LocationManager locationManager, LocationListener locationListener) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_CODE);
        } else{
           locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIEMPO_REQUEST, ESPACIO_REQUEST, locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean finalResult = true;

        if(requestCode == REQUEST_CODE){
            for(int i=0; (i<grantResults.length) && finalResult; i++){
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    finalResult = false;
                }
            }

            if(finalResult){
                solicitarActualizaciones(locationManager, locationListener);
            } else{
                Toast.makeText(this,"No hay permisos necesarios",Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }
}