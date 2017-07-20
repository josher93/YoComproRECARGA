package com.globalpaysolutions.yocomprorecarga.ui.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.globalpaysolutions.yocomprorecarga.R;
import com.globalpaysolutions.yocomprorecarga.models.DialogViewModel;
import com.globalpaysolutions.yocomprorecarga.presenters.AuthenticatePresenterImpl;
import com.globalpaysolutions.yocomprorecarga.views.AuthenticateView;

public class Authenticate extends AppCompatActivity implements AuthenticateView
{
    private static final String TAG = Authenticate.class.getSimpleName();

    //Views and Layouts
    ProgressDialog progressDialog;
    LoginButton btnLogin;

    //MVP
    private AuthenticatePresenterImpl mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);

        //Views
        btnLogin = (LoginButton) findViewById(R.id.btnLogin);

        //Initialization
        mPresenter = new AuthenticatePresenterImpl(this, this, this);

        mPresenter.setupFacebookAuth(btnLogin);
    }

    @Override
    public void navigateValidatePhone()
    {
        Intent validatePhone = new Intent(this, ValidatePhone.class);
        startActivity(validatePhone);
    }

    @Override
    public void navigateSetNickname()
    {
        Intent setNickname = new Intent(this, Nickname.class);
        startActivity(setNickname);
    }

    @Override
    public void navigateHome()
    {
        Intent home = new Intent(this, Home.class);
        startActivity(home);
    }

    @Override
    public void showLoadingDialog(String pLabel)
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(pLabel);
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void hideLoadingDialog()
    {
        try
        {
            if (progressDialog != null && progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
        }
        catch (Exception ex) {  ex.printStackTrace();   }
    }

    @Override
    public void showGenericDialog(DialogViewModel pMessageModel)
    {
        createDialog(pMessageModel.getTitle(), pMessageModel.getLine1(), pMessageModel.getAcceptButton());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showGenericToast(String text)
    {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /*
    *
    *
    *   OTHER METHODS
    *
    *
    */

    private void createDialog(String pTitle, String pMessage, String pButton)
    {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Authenticate.this);
        alertDialog.setTitle(pTitle);
        alertDialog.setMessage(pMessage);
        alertDialog.setPositiveButton(pButton, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }


}