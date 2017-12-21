package com.globalpaysolutions.yocomprorecarga.interactors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.globalpaysolutions.yocomprorecarga.api.ApiClient;
import com.globalpaysolutions.yocomprorecarga.api.ApiInterface;
import com.globalpaysolutions.yocomprorecarga.interactors.interfaces.IHomeInteractor;
import com.globalpaysolutions.yocomprorecarga.models.SimpleMessageResponse;
import com.globalpaysolutions.yocomprorecarga.models.api.StoreAirtimeReportReqBody;
import com.globalpaysolutions.yocomprorecarga.models.geofire_data.SalePointData;
import com.globalpaysolutions.yocomprorecarga.models.geofire_data.VendorPointData;
import com.globalpaysolutions.yocomprorecarga.utils.Constants;
import com.globalpaysolutions.yocomprorecarga.utils.UserData;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import io.fabric.sdk.android.services.concurrency.AsyncTask;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Josué Chávez on 24/02/2017.
 */

public class HomeInteractor implements IHomeInteractor
{
    private static final String TAG = HomeInteractor.class.getSimpleName();
    private Context mContext;
    private HomeListener mHomeListener;

    //Firebase
    private DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mSalesPoints = mRootReference.child("staticPoints");
    private DatabaseReference mDataSalesPoints = mRootReference.child("dataStaticPoints");
    private DatabaseReference mVendorPoints = mRootReference.child("YVR");
    private DatabaseReference mDataVendorPoints = mRootReference.child("dataYVR");


    //GeoFire
    private GeoFire mSalesPntsRef;
    private GeoFire mVendorPntsRef;

    //GeoFire Queries
    private GeoQuery mSalesPntsQuery;
    private GeoQuery mVendorPntsQuery;

    public HomeInteractor(Context pContext, HomeListener pListener)
    {
        mContext = pContext;
        mHomeListener = pListener;
    }

    @Override
    public void initializeGeolocation()
    {
        //GeoFire
        mSalesPntsRef = new GeoFire(mSalesPoints);
        mVendorPntsRef = new GeoFire(mVendorPoints);
    }

    @Override
    public void salesPointsQuery(GeoLocation pLocation)
    {
        try
        {
            mSalesPntsQuery = mSalesPntsRef.queryAtLocation(pLocation, Constants.SALES_POINTS_RADIUS_KM);
            mSalesPntsQuery.addGeoQueryEventListener(salesPointsListener);
        }
        catch (IllegalArgumentException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void salesPointsUpdateCriteria(GeoLocation pLocation)
    {
        try
        {
            mSalesPntsQuery.setLocation(pLocation, Constants.SALES_POINTS_RADIUS_KM);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void vendorPointsQuery(GeoLocation pLocation)
    {
        try
        {
            mVendorPntsQuery = mVendorPntsRef.queryAtLocation(pLocation, Constants.VENDOR_RADIUS_KM);
            mVendorPntsQuery.addGeoQueryEventListener(vendorPointsListener);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void vendorPointsUpdateCriteria(GeoLocation pLocation)
    {
        try
        {
            mVendorPntsQuery.setLocation(pLocation, Constants.VENDOR_RADIUS_KM);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void sendStoreAirtimeReport(String pStoreName, String pAddressStore, double pLongitude, double pLatitude, String pFirebaseID)
    {
        UserData userData = UserData.getInstance(mContext);
        StoreAirtimeReportReqBody airtimeReportReqBody = new StoreAirtimeReportReqBody();
        airtimeReportReqBody.setStoreName(pStoreName);
        airtimeReportReqBody.setAddressStore(pAddressStore);
        airtimeReportReqBody.setLongitude(pLongitude);
        airtimeReportReqBody.setLatitude(pLatitude);
        airtimeReportReqBody.setFirebaseID(pFirebaseID);
        airtimeReportReqBody.setConsumerID(userData.GetConsumerID());

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        final Call<SimpleMessageResponse> call = apiService.sendStoreAirtimeReport(UserData.getInstance(mContext).getUserAuthenticationKey(), airtimeReportReqBody);

        call.enqueue(new Callback<SimpleMessageResponse>()
        {
            @Override
            public void onResponse(Call<SimpleMessageResponse> call, Response<SimpleMessageResponse> response)
            {
                if(response.isSuccessful())
                {
                    SimpleMessageResponse Message = response.body();
                    mHomeListener.onStoreAirtimeReportSuccess(Message);
                }
                else
                {
                    int codeResponse = response.code();
                    mHomeListener.onError(codeResponse, null);
                }
            }

            @Override
            public void onFailure(Call<SimpleMessageResponse> call, Throwable t)
            {
                mHomeListener.onError(0, t);
            }
        });
    }

    @Override
    public Bitmap fetchBitmap(String url)
    {
        Bitmap bitmap = null;
        try
        {
            bitmap = new FetchMarker().execute(url).get();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        return  bitmap;
    }

    @Override
    public void fetchGoldMarker(String url)
    {
        Picasso.with(mContext).load(url).into(new Target()
        {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
            {
                mHomeListener.onGoldMarkerLoaded(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable)
            {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable)
            {

            }
        });
    }

    @Override
    public void fetchSilverMarker(String url)
    {
        Picasso.with(mContext).load(url).into(new Target()
        {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
            {
                mHomeListener.onSilverMarkerLoaded(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable)
            {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable)
            {

            }
        });
    }

    @Override
    public void fetchBronzeMarker(String url)
    {
        Picasso.with(mContext).load(url).into(new Target()
        {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
            {
                mHomeListener.onBronzeMarkerLoaded(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable)
            {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable)
            {

            }
        });
    }

    @Override
    public void fetchWildcardMarker(String url)
    {
        Picasso.with(mContext).load(url).into(new Target()
        {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
            {
                mHomeListener.onWildcardMarkerLoaded(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable)
            {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable)
            {

            }
        });
    }

    public static class FetchMarker extends AsyncTask<String, Void, Bitmap>
    {
        Bitmap mBitmap;

        @Override
        protected Bitmap doInBackground(String... strings)
        {
            try
            {
                URL bitmapUrl = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) bitmapUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                //mBitmap = BitmapFactory.decodeStream(input);
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                mBitmap = Bitmap.createScaledBitmap(bitmap , bitmap.getWidth()/2, bitmap.getHeight()/2, false);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                mBitmap = null;
            }
            return mBitmap;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

    }




    /*
    *
    * LISTENERS
    *
    */

    private GeoQueryEventListener salesPointsListener = new GeoQueryEventListener()
    {
        @Override
        public void onKeyEntered(final String key, GeoLocation location)
        {
            mDataSalesPoints.child(key).addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    SalePointData salePoint = dataSnapshot.getValue(SalePointData.class);
                    if(salePoint != null)
                        mHomeListener.fb_salePoint_onDataChange(key, salePoint);
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                    mHomeListener.fb_salePoint_onCancelled(databaseError);
                }
            });

            LatLng geoLocation = new LatLng(location.latitude, location.longitude);
            mHomeListener.gf_salePoint_onKeyEntered(key, geoLocation);
        }

        @Override
        public void onKeyExited(String key)
        {
            mHomeListener.gf_salePoint_onKeyExited(key);
        }

        @Override
        public void onKeyMoved(String key, GeoLocation location)
        {
            Log.i(TAG, "StaticPoint: Key moved fired.");
        }

        @Override
        public void onGeoQueryReady()
        {
            Log.i(TAG, "StaticPoint: GeoQuery ready fired.");
        }

        @Override
        public void onGeoQueryError(DatabaseError error)
        {
            Log.e(TAG, "StaticPoint: GeoFire Database error fired.");
        }
    };

    private GeoQueryEventListener vendorPointsListener = new GeoQueryEventListener()
    {
        @Override
        public void onKeyEntered(final String key, GeoLocation location)
        {
            mDataVendorPoints.child(key).addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    VendorPointData vendorPoint = dataSnapshot.getValue(VendorPointData.class);
                    if(vendorPoint != null)
                        mHomeListener.fb_vendorPoint_onDataChange(key, vendorPoint);
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                    mHomeListener.fb_vendorPoint_onCancelled(databaseError);
                }
            });

            LatLng geoLocation = new LatLng(location.latitude, location.longitude);
            mHomeListener.gf_vendorPoint_onKeyEntered(key, geoLocation);
        }

        @Override
        public void onKeyExited(String key)
        {
            mHomeListener.gf_vendorPoint_onKeyExited(key);
        }

        @Override
        public void onKeyMoved(String key, GeoLocation location)
        {
            LatLng geoLocation = new LatLng(location.latitude, location.longitude);
            mHomeListener.gf_vendorPoint_onKeyMoved(key, geoLocation);
        }

        @Override
        public void onGeoQueryReady()
        {
            mHomeListener.gf_vendorPoint_onGeoQueryReady();
            Log.i(TAG, "VendorPoint: GeoQuery ready fired.");
        }

        @Override
        public void onGeoQueryError(DatabaseError error)
        {
            mHomeListener.gf_vendorPoint_onGeoQueryError(error);
            Log.e(TAG, "VendorPoint: GeoFire Database error fired.");
        }
    };



}
