package com.example.ocr_mlkit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class RegisterToDB extends AppCompatActivity {

    //Button
    private Button button_reg;

    //EditText
    private EditText editTextEMail;
    private EditText editTextPassword;

    //String login & password
    private String username;
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
        username = "";
        password = "";
        encryptedPassword = "";

        //Button
        button_reg = findViewById(R.id.buttonLoginLogin);
        button_reg.setText(R.string.zarejestruj_si);

        //EditText
        editTextEMail = findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        username = editTextEMail.getText().toString();
        password = editTextPassword.getText().toString();

        //Button listener
        button_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = editTextEMail.getText().toString();
                password = editTextPassword.getText().toString();
                checkFields(username, password);
            }
        });
    }

    private void registerToDB(String ulogin, String upassword) throws NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException {
        if(!checkConnection()) {
            Toast.makeText(this, "Brak dostępu do internetu", Toast.LENGTH_SHORT).show();
        }


        final String userlogin = ulogin;
        final String userpassword = upassword;
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild(userlogin)) {
                    try {
                        Toast.makeText(RegisterToDB.this, "Pomyślnie zarejestrowano", Toast.LENGTH_SHORT).show();
                        encryptedPassword = encryptPassword(userpassword);
                        ref.child(userlogin).child("PasCrypt").setValue(encryptedPassword);
                        finish();
                        ref.removeEventListener(this);
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
                else {
                    Toast.makeText(RegisterToDB.this, "Nazwa użytkownika jest już zajęta", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(RegisterToDB.this, "Nazwa użytkownika jest już zajęta", Toast.LENGTH_SHORT).show();
            }
        });

        //Toast.makeText(RegisterToDB.this, email + "\n" + password + "\n" + encryptedPassword, Toast.LENGTH_SHORT).show();

    }

    private static String encryptPassword(String password) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
        final String ALGORITHM = "AES";
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
            Toast.makeText(RegisterToDB.this, "Podaj email oraz hasło", Toast.LENGTH_SHORT).show();
        }
        else if (email.isEmpty()) {
            Toast.makeText(RegisterToDB.this, "Podaj email", Toast.LENGTH_SHORT).show();
        }
        else if (password.isEmpty()) {
            Toast.makeText(RegisterToDB.this, "Podaj hasło", Toast.LENGTH_SHORT).show();
        }
        else if (password.length() < 8) {
            Toast.makeText(RegisterToDB.this, "Hasło musi mieć min. 8 znaków", Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                registerToDB(email, password);
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