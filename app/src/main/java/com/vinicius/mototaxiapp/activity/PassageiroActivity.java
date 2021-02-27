package com.vinicius.mototaxiapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.vinicius.mototaxiapp.R;
import com.vinicius.mototaxiapp.model.Destino;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import config.ConfigFireBase;

public class PassageiroActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseAuth firebaseAuth;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private EditText editDestino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passageiro);

        inicializarComponetes();

    }

    public void chamarCorrida(View view) {

        String enderecoDestino = editDestino.getText().toString();

        if (!enderecoDestino.equals("") || enderecoDestino != null) {

            Address addressDestino = recuperarEndereco(enderecoDestino);
            if (addressDestino != null) {

                Destino destino = new Destino();
                destino.setCidade(addressDestino.getAdminArea());
                destino.setCep(addressDestino.getPostalCode());
                destino.setBairro(addressDestino.getSubLocality());
                destino.setRua(addressDestino.getThoroughfare());
                destino.setNumero(addressDestino.getFeatureName());
                destino.setLatitude(String.valueOf(addressDestino.getLatitude()));
                destino.setLongitude(String.valueOf(addressDestino.getLongitude()));

                StringBuilder mensagem = new StringBuilder();
                mensagem.append("Cidade: " + destino.getCidade());
                mensagem.append("\nRua: " + destino.getRua());
                mensagem.append("\nBairro: " + destino.getBairro());
                mensagem.append("\nNúmero: " + destino.getNumero());
                mensagem.append("\nCEP: " + destino.getCep());

                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("Confirme seu endereço!")
                        .setMessage(mensagem)
                        .setPositiveButton("Confirmar", (dialog, which) -> {
                            //Salvar requisição


                        }).setNegativeButton("Cancelar", (dialog, which) -> {
                            //Cancelar requisição

                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        } else {
            Toast.makeText(PassageiroActivity.this, "Informe um endereço de destino!", Toast.LENGTH_SHORT).show();
        }

    }

    private Address recuperarEndereco(String endereco) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocationName(endereco, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);

                return address;

                //double lat = address.getLatitude();
                //double lon = address.getLongitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Recuperar localização do Usuário
        locationRecover();

    }

    private void locationRecover() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                LatLng meulocal = new LatLng(lat, lon);


                mMap.clear();
                mMap.addMarker(
                        new MarkerOptions()
                                .position(meulocal)
                                .title("Meu Local")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_usuario))
                );
                mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(meulocal, 15)
                );
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }
        };

        //Solicitar atualizações de localozação
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10000,
                    10,
                    locationListener
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuSair:
                firebaseAuth.signOut();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void inicializarComponetes() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Iniciar Uma Viagem");
        setSupportActionBar(toolbar);

        //Inicializar componentes
        editDestino = findViewById(R.id.editDestino);

        //Configurações iniciais
        firebaseAuth = ConfigFireBase.getFirebaseAuth();//Autenticação

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
}