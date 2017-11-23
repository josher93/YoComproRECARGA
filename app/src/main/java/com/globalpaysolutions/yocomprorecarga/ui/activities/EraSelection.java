package com.globalpaysolutions.yocomprorecarga.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.globalpaysolutions.yocomprorecarga.R;
import com.globalpaysolutions.yocomprorecarga.models.api.AgesListModel;
import com.globalpaysolutions.yocomprorecarga.presenters.EraSelectionPresenterImpl;
import com.globalpaysolutions.yocomprorecarga.ui.adapters.ErasAdapter;
import com.globalpaysolutions.yocomprorecarga.utils.Constants;
import com.globalpaysolutions.yocomprorecarga.views.EraSelectionView;

import java.util.List;

public class EraSelection extends AppCompatActivity implements EraSelectionView
{
    private static final String TAG = EraSelection.class.getSimpleName();

    //MVP
    EraSelectionPresenterImpl mPresenter;

    //Layouts and Views
    ListView lvEras;
    TextView lblEraName;
    ImageButton btnBack;
    ProgressDialog mProgressDialog;

    //Global Variables
    ErasAdapter mErasAdapter;

    String mDestiny;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_era_selection);

        //Get extras
        mDestiny = getIntent().getStringExtra(Constants.BUNDLE_ERA_SELECTION_INTENT_DESTINY);

        lvEras = (ListView) findViewById(R.id.lvEras);
        lblEraName = (TextView) findViewById(R.id.lblEraName);
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent main = new Intent(EraSelection.this, Main.class);
                main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(main);
                finish();
            }
        });

        mErasAdapter = new ErasAdapter(this, R.layout.custom_era_selection_item);
        mPresenter = new EraSelectionPresenterImpl(this, this, this);

        lvEras.setAdapter(mErasAdapter);

        mPresenter.initialize();
        mPresenter.retrieveEras();
    }

    @Override
    public void showLoadingDialog(String label)
    {
        try
        {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(label);
            mProgressDialog.show();
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void hideLoadingDialog()
    {
        try
        {
            if (mProgressDialog != null && mProgressDialog.isShowing())
            {
                mProgressDialog.dismiss();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void initialViews()
    {

    }

    @Override
    public void renderEras(List<AgesListModel> eras)
    {
        mErasAdapter.notifyDataSetChanged();
        lvEras.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                AgesListModel currentItem = ((AgesListModel) parent.getItemAtPosition(position));
                mPresenter.switchEra(currentItem, mDestiny);
            }
        });

        //Llenado de items en el ListView
        try
        {
            mErasAdapter.clear();
            for (AgesListModel item : eras)
            {
                mErasAdapter.add(item);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void navigateMap()
    {
        Intent pointsMap = new Intent(this, PointsMap.class);
        startActivity(pointsMap);
    }

    @Override
    public void forwardToStore()
    {
        Intent store = new Intent(EraSelection.this, Store.class);
        store.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(store);
        finish();
    }

    @Override
    public void createImageDialog(String title, String description, int resource)
    {
        try
        {
            final AlertDialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.custom_dialog_generic_image, null);

            TextView tvTitle = (TextView) dialogView.findViewById(R.id.tvDialogTitle);
            TextView tvDescription = (TextView) dialogView.findViewById(R.id.tvDialogMessage);
            ImageView imgSouvenir = (ImageView) dialogView.findViewById(R.id.imgDialogImage);
            ImageButton btnClose = (ImageButton) dialogView.findViewById(R.id.btnClose);

            tvTitle.setText(title);
            tvDescription.setText(description);
            imgSouvenir.setImageResource(resource);

            dialog = builder.setView(dialogView).create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            btnClose.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dialog.dismiss();
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void createLockedEraDialog()
    {
        try
        {
            final AlertDialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.custom_dialog_locked_era, null);

            ImageButton btnClose = (ImageButton) dialogView.findViewById(R.id.btnClose);

            dialog = builder.setView(dialogView).create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            btnClose.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dialog.dismiss();
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void setSelectedEraName(String eraName)
    {
        try
        {
            lblEraName.setText(eraName);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            Intent main = new Intent(EraSelection.this, Main.class);
            main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(main);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}