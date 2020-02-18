package net.iessochoa.manuelmartinez.practica7.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import net.iessochoa.manuelmartinez.practica7.R;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        // Primero, verificamos la existencia de una sesión.
        if (auth.getCurrentUser() != null) {
            finish();// Cerramos la actividad.
        // Abrimos la actividad que contiene el inicio de la funcionalidad de la app.
            startActivity(new Intent(this, InicioAppActivity.class));
        } else {
            //Si no está autenticado, llamamos al proceso de autenticación de FireBase
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
// Si quisieramos varios proveedores de autenticación.
// Mirar la documentación oficial, ya que cambia de una versión a otra
// .setAvalaibleProviders(
// AuthUI.EMAIL_PROVIDER,
// AuthUI.GOOGLE_PROVIDER)
                            //icono que mostrará, a mi no me funciona
//.setLogo(R.drawable.ic_developer_board_red_a700_48dp)
                            .setIsSmartLockEnabled(false)
                    //para guardar contraseñas y usuario: true
                    .build(),
                    RC_SIGN_IN);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Successfully signed in
            if (resultCode == RESULT_OK) {
                //si estamos autenticados abrimos la actividad principal
                startActivity(new Intent(this, InicioAppActivity.class));
            } else {//en otro caso nos salimos
                // Sign in failed
                String msg_error = "";
                if (response == null) {
                    // User pressed back button
                    msg_error = "Es necesario autenticarse";
                } else if (response.getError().getErrorCode() ==
                        ErrorCodes.NO_NETWORK) {
                    msg_error = "No hay red disponible para autenticarse";
                } else {
                    //if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        msg_error = "Error desconocido al autenticarse";
                    }
                    Toast.makeText(
                            this,
                            msg_error,
                            Toast.LENGTH_LONG)
                            .show();
                }
                finish();
        }
    }
}
