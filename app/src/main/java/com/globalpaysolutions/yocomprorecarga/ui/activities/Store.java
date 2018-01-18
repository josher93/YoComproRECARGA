package com.globalpaysolutions.yocomprorecarga.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.globalpaysolutions.yocomprorecarga.R;
import com.globalpaysolutions.yocomprorecarga.models.api.ListGameStoreResponse;
import com.globalpaysolutions.yocomprorecarga.presenters.StorePresenterImpl;
import com.globalpaysolutions.yocomprorecarga.ui.adapters.StoreAdapter;
import com.globalpaysolutions.yocomprorecarga.utils.ButtonAnimator;
import com.globalpaysolutions.yocomprorecarga.utils.ImmersiveActivity;
import com.globalpaysolutions.yocomprorecarga.views.StoreView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Store extends ImmersiveActivity implements StoreView
{
    //MVP
    private StorePresenterImpl mPresenter;

    //Layouyts and Views

    ViewPager pagerStoreItems;
    ImageButton btnLeft;
    ImageButton btnRight;
    TextView lblRecarCoinsLeft;
    ImageView btnBuy;
    ImageButton btnBack;
    ProgressDialog mProgressDialog;
    ImageView bgTimemachine;

    //Global Variables
    List<ListGameStoreResponse> mStoreItems;
    int mCurrentItem;

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        pagerStoreItems = (ViewPager) findViewById(R.id.pagerStoreItems);
        btnLeft = (ImageButton) findViewById(R.id.btnLeft);
        btnRight = (ImageButton) findViewById(R.id.btnRight);
        lblRecarCoinsLeft = (TextView) findViewById(R.id.lblRecarCoinsLeft);
        btnBuy = (ImageView) findViewById(R.id.btnBuy);
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        bgTimemachine = (ImageView) findViewById(R.id.bgTimemachine);
        btnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ButtonAnimator.getInstance(Store.this).animateButton(v);
                Intent main = new Intent(Store.this, Main.class);
                main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(main);
                finish();
            }
        });

        mPresenter = new StorePresenterImpl(this, this, this);
        mPresenter.initialValues();
        mPresenter.retrieveStoreItems();
    }

    @Override
    public void showLoadingDialog(String label)
    {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(label);
        mProgressDialog.show();
        mProgressDialog.setCanceledOnTouchOutside(false);
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
    public void setInitialValues(String currentCoins)
    {
        try
        {
            Picasso.with(this).load(R.drawable.bg_time_machine).into(bgTimemachine);

            lblRecarCoinsLeft.setText(currentCoins);
            btnBuy.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ButtonAnimator.getInstance(Store.this).animateButton(v);
                    if(mStoreItems != null)
                    {
                        if(mStoreItems.size() > 0)
                        {
                            ListGameStoreResponse item = mStoreItems.get(mCurrentItem);

                            mPresenter.purchaseitem(item.getStoreID(), item.getValue());
                        }
                    }
                }
            });

            //Button hidden on first page
            btnLeft.setVisibility(View.GONE);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void renderStoreItems(List<ListGameStoreResponse> items)
    {
        int currentPage = 0;
        mStoreItems = items;

        ArrayList<ListGameStoreResponse> storeItemsArray = new ArrayList<>();

        try
        {
            storeItemsArray.addAll(mStoreItems);

            pagerStoreItems.setAdapter(new StoreAdapter(Store.this, storeItemsArray));
            pagerStoreItems.addOnPageChangeListener(viewPagerPageChangeListener);

            if (currentPage == mStoreItems.size())
            {
                currentPage = 0;
            }
            pagerStoreItems.setCurrentItem(currentPage++, true);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void showSouvenirWonDialog(String souvenirName, String souvenirDescription, String url)
    {
        showSouvenirDialog(souvenirName, souvenirDescription, url, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ButtonAnimator.getInstance(Store.this).animateButton(v);
                navigateSouvenirs();
            }
        });
    }

    @Override
    public void showNewAchievementDialog(String name, String level, String prize, String score, int resource)
    {
        try
        {
            final AlertDialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.custom_achievement_dialog, null);

            TextView lblReward = (TextView) dialogView.findViewById(R.id.lblReward);
            TextView lblAchievementName = (TextView) dialogView.findViewById(R.id.lblAchievementName);
            ImageView imgAchievement = (ImageView) dialogView.findViewById(R.id.imgAchievement);
            ImageButton btnClose = (ImageButton) dialogView.findViewById(R.id.btnClose);
            ImageButton btnAchievemtsNav = (ImageButton) dialogView.findViewById(R.id.btnAchievemtsNav);

            lblReward.setText(String.format("Tu recompensa es de %1$s RecarCoins",prize));
            lblAchievementName.setText(String.format("Haz logrado el nivel %1$s  de %2$s",level, name ));
            Picasso.with(this).load(resource).into(imgAchievement);

            dialog = builder.setView(dialogView).create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            btnClose.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ButtonAnimator.getInstance(Store.this).animateButton(v);
                    dialog.dismiss();
                }
            });
            btnAchievemtsNav.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ButtonAnimator.getInstance(Store.this).animateButton(v);
                    Intent store = new Intent(Store.this, Achievements.class);
                    store.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(store);
                    finish();
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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
            Picasso.with(this).load(resource).into(imgSouvenir);

            dialog = builder.setView(dialogView).create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            btnClose.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ButtonAnimator.getInstance(Store.this).animateButton(v);
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
    public void updateViews(String coinsLeft)
    {
        lblRecarCoinsLeft.setText(coinsLeft);
    }

    @Override
    public void createGenericDialog(String title, String content)
    {
        try
        {
            final AlertDialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(Store.this);
            LayoutInflater inflater = Store.this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.custom_dialog_generic, null);

            TextView tvTitle = (TextView) dialogView.findViewById(R.id.tvDialogTitle);
            TextView tvDescription = (TextView) dialogView.findViewById(R.id.tvDialogMessage);
            ImageView button = (ImageView) dialogView.findViewById(R.id.btnClose);

            tvTitle.setText(title);
            tvDescription.setText(content);

            dialog = builder.setView(dialogView).create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            button.setOnClickListener(new View.OnClickListener()
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

    private void createStoreItemInfoDialog()
    {
        try
        {
            final AlertDialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.custom_dialog_souvenir_info, null);

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
                    ButtonAnimator.getInstance(Store.this).animateButton(v);
                    dialog.dismiss();
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageSelected(int position)
        {
            mCurrentItem = position;

            // Last page, make right button invisible
            if (position == mStoreItems.size() - 1)
            {

                btnRight.setVisibility(View.GONE);
            }
            else if(position == 0)
            {
                // First page, make left button invisible
                btnLeft.setVisibility(View.GONE);
            }
            else
            {
                // Makes both button visible as long as are pages left
                btnRight.setVisibility(View.VISIBLE);
                btnLeft.setVisibility(View.VISIBLE);
            }
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2)
        {

        }
        @Override
        public void onPageScrollStateChanged(int arg0)
        {

        }
    };

    public void nextClick(View view)
    {
        try
        {
            ButtonAnimator.getInstance(Store.this).animateButton(view);
            int current = pagerStoreItems.getCurrentItem() + 1;
            if (current < mStoreItems.size())
                pagerStoreItems.setCurrentItem(current);
            else
                mPresenter.navigateNext();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void backClick(View view)
    {
        try
        {
            ButtonAnimator.getInstance(Store.this).animateButton(view);
            int current = pagerStoreItems.getCurrentItem() - 1;
            if (current < mStoreItems.size())
                pagerStoreItems.setCurrentItem(current);
            else
                mPresenter.navigateNext();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void navigateSouvenirs()
    {
        Intent souvenirs = new Intent(this, Souvenirs.class);
        startActivity(souvenirs);
    }

    private void showSouvenirDialog(String souvenirName, String souvenirDescription, String url, View.OnClickListener listener)
    {
        try
        {
            //Creates the builder and inflater of dialog
            final AlertDialog souvenirDialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.custom_dialog_won_sourvenir, null);

            TextView tvSouvenirName = (TextView) dialogView.findViewById(R.id.lblSouvenirName);
            TextView tvSouvenirDesc = (TextView) dialogView.findViewById(R.id.lblSouvenirDescription);
            ImageView imgSouvenir = (ImageView) dialogView.findViewById(R.id.imgSouvenirDialog);
            ImageButton button = (ImageButton) dialogView.findViewById(R.id.btnGenericDialogButton);
            ImageView btnClose = (ImageView) dialogView.findViewById(R.id.btnClose);
            button.setOnClickListener(listener);

            tvSouvenirName.setText(souvenirName);
            tvSouvenirDesc.setText(souvenirDescription);

            //TODO: Architecture violation - Requests made on Views
            Picasso.with(this).load(url).into(imgSouvenir);

            souvenirDialog = builder.setView(dialogView).create();
            souvenirDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            souvenirDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            btnClose.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ButtonAnimator.getInstance(Store.this).animateButton(v);
                    souvenirDialog.dismiss();
                }
            });

            souvenirDialog.show();
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
            Intent main = new Intent(this, Main.class);
            main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(main);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
