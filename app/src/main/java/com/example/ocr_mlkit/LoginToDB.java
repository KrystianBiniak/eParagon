package com.example.ocr_mlkit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.security.AlgorithmConstraints;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class LoginToDB extends AppCompatActivity {

    //Button
    private Button button_login;

    //EditText
    private EditText editTextEMail;
    private EditText editTextPassword;

    //String login & password
    private String email;
    private String password;
    private String encryptedPassword;

    //Database
    private FirebaseDatabase db;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_to_d_b);

        if(!checkConnection()) {
            Toast.makeText(this, "Brak dostępu do internetu", Toast.LENGTH_SHORT).show();
        }

        //Reference
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("Użytkownicy");

        //String
        email = "";
        password = "";
        encryptedPassword = "";

        //Button
        button_login = findViewById(R.id.buttonLoginLogin);

        //EditText
        editTextEMail = findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        email = editTextEMail.getText().toString();
        password = editTextPassword.getText().toString();

        //Button listener
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = editTextEMail.getText().toString();
                password = editTextPassword.getText().toString();
                checkFields(email, password);
            }
        });

    }

    private void loginToDB(String ulogin, String upassword) throws NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException {
        if(!checkConnection()) {
            Toast.makeText(this, "Brak dostępu do internetu", Toast.LENGTH_SHORT).show();
        }

        //encryptedPassword = encryptPassword(password);
        //Toast.makeText(LoginToDB.this, email + "\n" + password + "\n" + encryptedPassword, Toast.LENGTH_SHORT).show();

        final String userlogin = ulogin;
        final String userpassword = upassword;

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild(userlogin)) {
                    Toast.makeText(LoginToDB.this, "Nie ma takiego użytkownika", Toast.LENGTH_SHORT).show();
                    ref.removeEventListener(this);
                }
                else {
                    try {
                        encryptedPassword = encryptPassword(password);
                        if(encryptedPassword.equals(snapshot.child(userlogin).child("PasCrypt").getValue().toString())) {

                            Intent intent = new Intent(LoginToDB.this, MainActivity.class);
                            intent.putExtra("Permission", true);
                            intent.putExtra("Username", userlogin);
                            startActivity(intent);

                            Toast.makeText(LoginToDB.this, "Pomyślnie zalogowano", Toast.LENGTH_SHORT).show();
                            ref.removeEventListener(this);
                        }
                        else {
                            Toast.makeText(LoginToDB.this, "Złe hasło", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //
            }
        });

    }

    private static String encryptPassword(String password) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
        final String ALGORITHM = "AES";
        //final String KEY = "1Hbfh667adfDEJ78";
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedByteValue = cipher.doFinal(password.getBytes("utf-8"));
        String encryptedPassword = Base64.encodeToString(encryptedByteValue, Base64.DEFAULT);
        return encryptedPassword;
    }

    private static Key generateKey() {
        final String ALGORITHM = "AES";
        final String KEY = "1Hbfh667adfDEJ78";
        Key key = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        return key;
    }

    private void checkFields(String email, String password) {
        if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(LoginToDB.this, "Podaj email oraz hasło", Toast.LENGTH_SHORT).show();
        }
        else if (email.isEmpty()) {
            Toast.makeText(LoginToDB.this, "Podaj email", Toast.LENGTH_SHORT).show();
        }
        else if (password.isEmpty()) {
            Toast.makeText(LoginToDB.this, "Podaj hasło", Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                loginToDB(email, password);
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}