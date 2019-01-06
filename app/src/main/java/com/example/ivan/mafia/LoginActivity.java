package com.example.ivan.mafia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    int minNameChars = 3;
    int minPasswordChars = 3;

    EditText editName, editPassword;
    Button btnSignIn;
    TextView textRegister, textNoAccount;

    Handler handler;

    SharedPreferences sPref;

    String name, password;

    Mafia mafia = Mafia.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.title_pass);

        editName = findViewById(R.id.editName);
        editPassword = findViewById(R.id.editPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        textRegister = findViewById(R.id.textRegister);
        textNoAccount = findViewById(R.id.textNoAccount);

        sPref = getSharedPreferences("prefs", MODE_PRIVATE);

        signInMode();
    }

    private void signInMode() {
        textNoAccount.setText(getString(R.string.noAccount));
        textRegister.setText(getString(R.string.registerAccount));
        textRegister.setOnClickListener(textReg -> registerMode());

        btnSignIn.setText(getString(R.string.joinAccount));
        btnSignIn.setOnClickListener(view -> {
            name = editName.getText().toString();
            password = editPassword.getText().toString();
            if (name.length() < minNameChars)
                Toast.makeText(this, getString(R.string.nameMustBe) + minNameChars + getString(R.string.symbol), Toast.LENGTH_SHORT).show();
            else if (password.length() < minPasswordChars)
                Toast.makeText(this, getString(R.string.passwordMustBe) + minPasswordChars + getString(R.string.symbol), Toast.LENGTH_SHORT).show();
            else mafia.signIn(handler, name, password);
        });

        handler = new Handler(msg -> {
            String message = (String) msg.obj;
            if (msg.arg1 == -1) {
                Toast.makeText(this, getString(R.string.error) + message, Toast.LENGTH_SHORT).show();
                return true;
            }
            if (msg.what == 11) {
                if (message.equals("OK")) {
                    saveNameAndPassword(getSharedPreferences("prefs", MODE_PRIVATE), name, password);
                    Toast.makeText(this, getString(R.string.logToAccountSuccess), Toast.LENGTH_SHORT).show();
                    setResult(1, new Intent().putExtra("name", name));
                    finish();
                }
                else Toast.makeText(this, getString(R.string.logError) + message, Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    private void registerMode() {
        textNoAccount.setText(getString(R.string.alreadyRegistered));
        textRegister.setText(getString(R.string.joinAccount));
        textRegister.setOnClickListener(textRegister2 -> signInMode());

        btnSignIn.setText(getString(R.string.registerAccount));
        btnSignIn.setOnClickListener(btnSignIn -> {
            name = editName.getText().toString();
            password = editPassword.getText().toString();
            if (name.length() < minNameChars)
                Toast.makeText(this, getString(R.string.nameMustBe) + minNameChars + getString(R.string.symbol), Toast.LENGTH_SHORT).show();
            else if (password.length() < minPasswordChars)
                Toast.makeText(this, getString(R.string.passwordMustBe) + minPasswordChars + getString(R.string.symbol), Toast.LENGTH_SHORT).show();
            else mafia.register(handler, name, password);
        });

        handler = new Handler(msg -> {
            String message = (String) msg.obj;
            if (msg.arg1 == -1) {
                Toast.makeText(this, getString(R.string.error) + message, Toast.LENGTH_SHORT).show();
                return true;
            }
            if (msg.what == 10) {
                if (message.equals(getString(R.string.accountCreatingSuccess))) {
                    saveNameAndPassword(getSharedPreferences("prefs", MODE_PRIVATE), name, password);
                    Toast.makeText(this, getString(R.string.registerSuccess), Toast.LENGTH_SHORT).show();
                    setResult(1, new Intent().putExtra("name", name));
                    finish();
                }
                else Toast.makeText(this, getString(R.string.regError) + message, Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    public static void saveNameAndPassword(SharedPreferences sPrefs, String name, String password) {
        SharedPreferences.Editor ePref = sPrefs.edit();
        ePref.putString("name", name);
        ePref.putString("password", password);
        ePref.apply();
    }
}
