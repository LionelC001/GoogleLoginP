package com.lionel.googleloginp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_GOOGLE_LOGIN_IN = 999;

    private TextView txtResult;
    private SignInButton btnLogin;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initGoogleLogin();
        initView();
    }

    private void initGoogleLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.google_web_client_id))
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void initView() {
        txtResult = findViewById(R.id.txtResult);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> {
            checkIsAlreadyLogin();
            onGoogleLogin();
        });
    }

    private void checkIsAlreadyLogin() {
        GoogleSignInAccount alreadyloggedAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (alreadyloggedAccount != null) {
            Toast.makeText(this, "Already Logged In, will auto logout for you", Toast.LENGTH_LONG).show();
            Log.d("<>", alreadyloggedAccount.getEmail()
                    + "\n\n" + alreadyloggedAccount.getId()
                    + "\n\n" + alreadyloggedAccount.getIdToken());

            autoLogout();
        } else {
            Toast.makeText(this, "Not Logged In, please choose an account to login", Toast.LENGTH_LONG).show();
        }
    }

    private void autoLogout() {
        googleSignInClient.signOut().addOnSuccessListener(aVoid -> updateTxtResult(""));
    }

    private void onGoogleLogin() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE_LOGIN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("<>", "onActivityResult code: " + resultCode);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_GOOGLE_LOGIN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("account: ").append(account.getAccount());
            stringBuilder.append("\n\nemail: ").append(account.getEmail());
            stringBuilder.append("\n\nid: ").append(account.getId());
            stringBuilder.append("\n\ntoken: ").append(account.getIdToken());
            updateTxtResult(stringBuilder.toString());
        } catch (ApiException e) {
            updateTxtResult(e.getMessage());
        }
    }

    private void updateTxtResult(String result) {
        txtResult.setText(result);
    }
}
