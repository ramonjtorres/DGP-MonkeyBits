package com.monkeybit.routability;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class UserProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        if (currentUser == null) {
            Log.e("Debug:", "Error, no debería haber aparecido esta pantalla porque el usuario no ha iniciado sesión");
            Toast toast = Toast.makeText(getApplicationContext(), "Error, no debería haber aparecido esta pantalla porque el usuario no ha iniciado sesión", Toast.LENGTH_SHORT);
            toast.show();
            LoadActivityWithoutArguments(MainActivity.class);
        }
        UpdateUI();
    }

    private void UpdateUI() {
        String name = "";
        String email = "";
        if (currentUser != null) {
            for (UserInfo profile : currentUser.getProviderData()) {
                name = profile.getDisplayName();
                email = profile.getEmail();
            }
        }

        TextView emailTextView = findViewById(R.id.UserEmail);
        TextView userNameTextView = findViewById(R.id.UserName);

        if (name.isEmpty() || email.isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(), "No se han podido recuperar los datos del usuario.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (emailTextView != null && userNameTextView != null) {
            emailTextView.setText(email);
            userNameTextView.setText(name);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "No se ha podido acceder al TextView.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void OnLogOut(android.view.View view) {
        if (mAuth.getCurrentUser() != null) {
            mAuth.signOut();
            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.logged_out), Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.logged_out_fail), Toast.LENGTH_SHORT);
            toast.show();
        }
        LoadActivityWithoutArguments(MainActivity.class);
    }

    private void LoadActivityWithoutArguments(Class<?> newActivityName) {
        Intent intent = new Intent(this, newActivityName);
        startActivity(intent);
    }
}
