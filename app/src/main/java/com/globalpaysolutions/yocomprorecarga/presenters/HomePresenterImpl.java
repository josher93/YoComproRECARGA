package com.globalpaysolutions.yocomprorecarga.presenters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.firebase.geofire.GeoLocation;
import com.globalpaysolutions.yocomprorecarga.R;
import com.globalpaysolutions.yocomprorecarga.interactors.FirebasePOIInteractor;
import com.globalpaysolutions.yocomprorecarga.interactors.FirebasePOIListener;
import com.globalpaysolutions.yocomprorecarga.interactors.HomeInteractor;
import com.globalpaysolutions.yocomprorecarga.interactors.HomeListener;
import com.globalpaysolutions.yocomprorecarga.location.GoogleLocationApiManager;
import com.globalpaysolutions.yocomprorecarga.location.LocationCallback;
import com.globalpaysolutions.yocomprorecarga.models.DialogViewModel;
import com.globalpaysolutions.yocomprorecarga.models.MarkerData;
import com.globalpaysolutions.yocomprorecarga.models.SimpleMessageResponse;
import com.globalpaysolutions.yocomprorecarga.models.api.PendingsResponse;
import com.globalpaysolutions.yocomprorecarga.models.api.Sponsor;
import com.globalpaysolutions.yocomprorecarga.models.geofire_data.LocationPrizeYCRData;
import com.globalpaysolutions.yocomprorecarga.models.geofire_data.PlayerPointData;
import com.globalpaysolutions.yocomprorecarga.models.geofire_data.SalePointData;
import com.globalpaysolutions.yocomprorecarga.models.geofire_data.SponsorPrizeData;
import com.globalpaysolutions.yocomprorecarga.models.geofire_data.VendorPointData;
import com.globalpaysolutions.yocomprorecarga.models.geofire_data.WildcardYCRData;
import com.globalpaysolutions.yocomprorecarga.presenters.interfaces.IHomePresenter;
import com.globalpaysolutions.yocomprorecarga.utils.BitmapUtils;
import com.globalpaysolutions.yocomprorecarga.utils.Constants;
import com.globalpaysolutions.yocomprorecarga.utils.MockLocationUtility;
import com.globalpaysolutions.yocomprorecarga.utils.SponsoredChest;
import com.globalpaysolutions.yocomprorecarga.utils.UserData;
import com.globalpaysolutions.yocomprorecarga.views.HomeView;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Josué Chávez on 19/01/2017.
 */

public class HomePresenterImpl implements IHomePresenter, HomeListener, FirebasePOIListener, LocationCallback
{
    private static final String TAG = HomePresenterImpl.class.getSimpleName();

    private HomeView mView;
    private Context mContext;
    private UserData mUserData;
    private Activity mActivity;
    private HomeInteractor mInteractor;
    private FirebasePOIInteractor mFirebaseInteractor;

    private GoogleLocationApiManager mGoogleLocationApiManager;

    private Map<String, Bitmap> mMarkerMap;
    private int mLocationUpdatesCount;
    private SponsoredChest mSponsoredChest;

    public HomePresenterImpl(HomeView pView, AppCompatActivity pActivity, Context pContext)
    {
        mView = pView;
        mContext = pContext;
        mUserData = UserData.getInstance(mContext);
        mActivity = pActivity;
        mInteractor = new HomeInteractor(mContext, this);
        mFirebaseInteractor = new FirebasePOIInteractor(mContext, this);
        mSponsoredChest = new SponsoredChest(mContext);

        this.mGoogleLocationApiManager = new GoogleLocationApiManager(pActivity, mContext, Constants.FOUR_METTERS_DISPLACEMENT);
        this.mGoogleLocationApiManager.setLocationCallback(this);

        this.mMarkerMap = new HashMap<>();
        mLocationUpdatesCount = 0;
    }

    @Override
    public void setInitialViewsState()
    {
        this.mView.renderMap();
        this.mView.setClickListeners();

        try
        {
            String lastChallenges = UserData.getInstance(mContext).getPendingChallenges();
            int value = Integer.valueOf(lastChallenges);

            if(value > 0)
                this.mView.setPendingChallenges(lastChallenges, true);
            else
                this.mView.setPendingChallenges(lastChallenges, false);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error parsing string to int: " + ex.getMessage());
        }


    }



    @Override
    public void chekcLocationServiceEnabled()
    {
       LocationManager locationManager;
       boolean gpsEnabled = false;
       boolean networkEnabled = false;

       try
       {
           locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
           gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
           networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
       }
       catch (Exception ex)
       {
           ex.printStackTrace();
       }

       if(!gpsEnabled && !networkEnabled)
       {
            mView.displayActivateLocationDialog();
       }
    }

    @Override
    public void checkPermissions()
    {
        mView.checkPermissions();
    }

    @Override
    public void connnectToLocationService()
    {
        Log.d(TAG, "connectToLocationService: hit");
        mGoogleLocationApiManager.connect();
    }

    @Override
    public void disconnectFromLocationService()
    {
        Log.d(TAG, "disconnectFromLocationService: hit");
        mGoogleLocationApiManager.disconnect();
    }

    @Override
    public void sendStoreAirtimeReport(String pStoreName, String pAddress, LatLng pLocation, String pFirebaseID)
    {
        mView.showLoadingDialog(mContext.getString(R.string.label_loading_please_wait));
        double longitide = pLocation.longitude;
        double latitude = pLocation.latitude;
        mInteractor.sendStoreAirtimeReport(pStoreName, pAddress, longitide, latitude, pFirebaseID);
    }

    @Override
    public void onSalePointClick(String pStoreName, String pAddress, LatLng pLocation, String pFirebaseID)
    {
        DialogViewModel dialog = new DialogViewModel();
        dialog.setTitle(mContext.getString(R.string.dialog_title_airtime_report));
        dialog.setLine1(mContext.getString(R.string.dialog_content_airtime_report));
        dialog.setAcceptButton(mContext.getString(R.string.button_yes));
        dialog.setCanelButton(mContext.getString(R.string.button_no));
        mView.showCustomStoreReportDialog(dialog, pStoreName, pAddress, pLocation, pFirebaseID);
    }

    @Override
    public void setMapStyle()
    {
        Calendar calendar = Calendar.getInstance();
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        //if(timeOfDay >= 18 && timeOfDay < 5)
        if(timeOfDay > 0 && timeOfDay <= 5)
        {
            mView.swtichMapStyle(true);
        }
        else if (timeOfDay >= 18 && timeOfDay <= 24)
        {
            mView.swtichMapStyle(true);
        }
        else
        {
            mView.swtichMapStyle(false);
        }
    }


    @Override
    public void intializeGeolocation()
    {
        try
        {
            //Checks if mock locations are active
            if(!MockLocationUtility.isMockSettingsON(mContext))
            {
                mView.getMarkerBitmaps(mMarkerMap);
                mInteractor.initializeGeolocation();
                mFirebaseInteractor.initializePOIGeolocation();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void salesPointsQuery(LatLng pLocation)
    {
        GeoLocation location = new GeoLocation(pLocation.latitude, pLocation.longitude);
        mInteractor.salesPointsQuery(location);
    }

    @Override
    public void updateSalePntCriteria(LatLng pLocation)
    {
        GeoLocation location = new GeoLocation(pLocation.latitude, pLocation.longitude);
        mInteractor.salesPointsUpdateCriteria(location);
    }

    @Override
    public void vendorsPointsQuery(LatLng pLocation)
    {
        GeoLocation location = new GeoLocation(pLocation.latitude, pLocation.longitude);
        mInteractor.vendorPointsQuery(location);
    }

    @Override
    public void updateVendorsPntCriteria(LatLng pLocation)
    {
        GeoLocation location = new GeoLocation(pLocation.latitude, pLocation.longitude);
        mInteractor.vendorPointsUpdateCriteria(location);
    }

    @Override
    public void prizePointsQuery(LatLng pLocation)
    {
        GeoLocation location = new GeoLocation(pLocation.latitude, pLocation.longitude);
        //Makes sponsors query first, in order to get sponsors chests ASAP
        mFirebaseInteractor.sponsorPrizeQuery(location, Constants.BRONZE_CHESTS_QUERY_RADIUS_KM);

        //Makes remaining queries
        mFirebaseInteractor.goldPointsQuery(location, Constants.GOLD_CHESTS_QUERY_RADIUS_KM);
        mFirebaseInteractor.silverPointsQuery(location, Constants.SILVER_CHESTS_QUERY_RADIUS_KM);
        mFirebaseInteractor.bronzePointsQuery(location, Constants.BRONZE_CHESTS_QUERY_RADIUS_KM);
        mFirebaseInteractor.wildcardPointsQuery(location, Constants.BRONZE_CHESTS_QUERY_RADIUS_KM);

    }

    @Override
    public void updatePrizePntCriteria(LatLng pLocation)
    {
        GeoLocation location = new GeoLocation(pLocation.latitude, pLocation.longitude);
        mFirebaseInteractor.goldPointsUpdateCriteria(location, Constants.GOLD_CHESTS_QUERY_RADIUS_KM);
        mFirebaseInteractor.silverPointsUpdateCriteria(location, Constants.SILVER_CHESTS_QUERY_RADIUS_KM);
        mFirebaseInteractor.bronzePointsUpdateCriteria(location, Constants.BRONZE_CHESTS_QUERY_RADIUS_KM);
        mFirebaseInteractor.wildcardPointsUpdateCriteria(location, Constants.BRONZE_CHESTS_QUERY_RADIUS_KM);
        mFirebaseInteractor.sponsorPrizeQueryUpdateCriteria(location, Constants.BRONZE_CHESTS_QUERY_RADIUS_KM);
    }

    @Override
    public void playersPointsQuery(LatLng location)
    {
        if(UserData.getInstance(mContext).checkCurrentLocationVisible())
        {
            GeoLocation geoLocation = new GeoLocation(location.latitude, location.longitude);
            mInteractor.playersPointsQuery(geoLocation);
        }
    }

    @Override
    public void updatePlayersPntCriteria(LatLng location)
    {
        GeoLocation geoLocation = new GeoLocation(location.latitude, location.longitude);
        mInteractor.playersPointsUpdateCriteria(geoLocation);
    }

    @Override
    public void writeCurrentPlayerLocation(LatLng location)
    {
        if(UserData.getInstance(mContext).checkCurrentLocationVisible())
        {
            //Inserts data first, then location
            GeoLocation geoLocation = new GeoLocation(location.latitude, location.longitude);
            mInteractor.insertCurrentPlayerData(geoLocation, UserData.getInstance(mContext).getAuthProviderId());
        }
        else
        {
            mInteractor.deletePlayerLocation(UserData.getInstance(mContext).getAuthProviderId());
        }

    }

    @Override
    public void startShowcase()
    {
        if(!mUserData.showcaseMapSeen())
        {
            mView.startShowcase();
        }
    }

    @Override
    public void showcaseMapSeen()
    {
        mUserData.setShowcaseMapSeen();
    }

    @Override
    public void setPendingChallenges()
    {
        try
        {
            mInteractor.getPendingChallenges(this);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error setting pending challenges: " + ex.getMessage());
        }
    }

    @Override
    public void navigateToAR()
    {
        String challenges = UserData.getInstance(mContext).getPendingChallenges();

        mView.navigateToAR();


        //If has no pending challenges continues to AR
        /*if(TextUtils.equals(challenges, "0"))
        {
            mView.navigateToAR();
        }
        else
        {
            DialogViewModel dialog = new DialogViewModel();
            dialog.setTitle(mContext.getString(R.string.title_pending_challenges));
            dialog.setLine1(mContext.getString(R.string.label_pending_challenges_to_solve));
            dialog.setAcceptButton(mContext.getString(R.string.button_accept));
            mView.showGenericImageDialog(dialog, new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    mView.navigateChallenges();
                }
            });
        }*/
    }

    @Override
    public void checkWelcomeChest(Location location)
    {
        try
        {
            //Checks if welcome user is available for welcome chest
            if(UserData.getInstance(mContext).checkWelcomeChestAvailable())
            {
                Bitmap goldMarker = BitmapUtils.retrieve(mContext, Constants.NAME_CHEST_TYPE_GOLD);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                float latt = Float.valueOf(String.valueOf(location.getLatitude()));
                float longt = Float.valueOf(String.valueOf(location.getLongitude()));

                UserData.getInstance(mContext).saveWelcomeChestLat(latt);
                UserData.getInstance(mContext).saveWelcomeChestLong(longt);

                mView.addGoldPointData(Constants.WELCOME_CHEST_FIREBASE_KEY, null, null);
                mView.addGoldPoint(Constants.WELCOME_CHEST_FIREBASE_KEY, latLng, goldMarker);

                //Sets user as welcome chest-unavailable
                //UserData.getInstance(mContext).setWelcomeChestAvailable(false);
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error checking for welcome chest: " + ex.getMessage());
        }
    }

    @Override
    public void onMapReady()
    {
        connnectToLocationService();
    }

    /*
    *
    *   LocationCallback
    *
    */
    @Override
    public void onLocationChanged(Location location)
    {
        try
        {
            if(location != null)
            {
                //Checks if location received is fake
                if(!MockLocationUtility.isMockLocation(location, mContext))
                {
                    //Checks apps blacklist
                    if(MockLocationUtility.isMockAppInstalled(mContext) <= 0 )
                    {
                        String firebaseKey = UserData.getInstance(mContext).getAuthProviderId();
                        GeoLocation geoLocation = new GeoLocation(location.getLatitude(), location.getLongitude());

                        //Inserts location if setting is set to 'Visible'
                        if(UserData.getInstance(mContext).checkCurrentLocationVisible())
                            mInteractor.setPlayerLocation(firebaseKey, geoLocation);
                        else
                            mInteractor.deletePlayerLocation(firebaseKey);

                        mView.updateUserLocationOnMap(location);
                    }
                    else
                    {
                        mView.showToast(mContext.getString(R.string.toast_mock_apps_may_be_installed));
                    }
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onLocationApiManagerConnected(Location location)
    {
        try
        {
            if(location != null)
            {
                //Checks if location received is fake
                if(!MockLocationUtility.isMockLocation(location, mContext))
                {
                    //Checks apps in blaclist
                    if(MockLocationUtility.isMockAppInstalled(mContext) <= 0)
                        mView.setInitialUserLocation(location);
                    else
                        mView.showToast(mContext.getString(R.string.toast_mock_apps_may_be_installed));
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onLocationApiManagerDisconnected()
    {

    }


   /*
   *
   *   HomeListener
   *
   */

    @Override
    public void onStoreAirtimeReportSuccess(SimpleMessageResponse pResponse)
    {
        mView.hideLoadingDialog();
        DialogViewModel dialog = new DialogViewModel();
        dialog.setTitle(mContext.getString(R.string.title_success_dialog_store_report));
        dialog.setLine1(mContext.getString(R.string.dialog_content_report_sent));
        dialog.setAcceptButton(mContext.getString(R.string.button_accept));
        mView.showSuccessMessage(dialog);
    }

    @Override
    public void onError(int pCodeStatus, Throwable pThrowable)
    {
        mView.hideLoadingDialog();
        ProcessErrorMessage(pCodeStatus, pThrowable);
    }

    // GEOFIRE STATIC POINTS
    @Override
    public void gf_salePoint_onKeyEntered(String pKey, LatLng pLocation)
    {
        mView.addSalePoint(pKey, pLocation );
    }

    @Override
    public void gf_salePoint_onKeyExited(String pKey)
    {
        mView.removeSalePoint(pKey);
    }

    // GEOFIRE VENDOR POINTS
    @Override
    public void gf_vendorPoint_onKeyEntered(String pKey, LatLng pLocation)
    {
        mView.addVendorPoint(pKey, pLocation);
    }

    @Override
    public void gf_vendorPoint_onKeyExited(String pKey)
    {
        mView.removeVendorPoint(pKey);
    }

    @Override
    public void gf_vendorPoint_onKeyMoved(String pKey, LatLng pLocation)
    {
        mView.moveVendorPoint(pKey, pLocation);
    }

    @Override
    public void gf_vendorPoint_onGeoQueryReady()
    {

    }

    @Override
    public void gf_vendorPoint_onGeoQueryError(DatabaseError pError)
    {

    }


    /*
    *
    *
    *   GEOFIRE PLAYERS POINTS
    *
    * */
    @Override
    public void gf_playerPoint_onKeyEntered(final String key, final LatLng location, PlayerPointData playerData)
    {
        //Checks if current Era selected is Worldcup
        if(TextUtils.equals(mUserData.getEraName(), Constants.ERA_WORLDCUP_NAME))
        {
            //If key entered is not user's key, then draws a marker
            if(!TextUtils.equals(key, UserData.getInstance(mContext).getAuthProviderId()))
            {
                try
                {
                    //Gets last part of Uri to create the file name
                    Uri markerUrl = Uri.parse(playerData.getMarkerUrl());
                    final String name = markerUrl.getLastPathSegment();

                    //Checks if bitmap already exists
                    Bitmap marker = BitmapUtils.retrieve(mContext, name);

                    if(marker != null)
                    {
                        mView.addWorldcupPlayerMarker(key, location, marker);
                    }
                    else
                    {
                        //If marker file does not exists, downloads it
                        Picasso.with(mContext).load(playerData.getMarkerUrl()).into(new Target()
                        {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                            {
                                bitmap = BitmapUtils.scaleMarker(bitmap, mContext);

                                saveBitmap(bitmap, name);
                                mView.addWorldcupPlayerMarker(key, location, bitmap);
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable)
                            { }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable)
                            { }
                        });
                    }
                }
                catch (Exception ex)
                {
                    Log.e(TAG, "Error on key entered: " + ex.getMessage());
                    mView.addPlayerPoint(key, location);
                }
            }
        }
        else
        {
            if(!TextUtils.equals(key, UserData.getInstance(mContext).getAuthProviderId()))
                mView.addPlayerPoint(key, location);
        }
    }

    @Override
    public void gf_playerPoint_onKeyExited(String key)
    {
        mView.removePlayerPoint(key);
    }

    @Override
    public void gf_playerPoint_onKeyMoved(String key, LatLng location)
    {
        mView.movePlayerPoint(key, location);
    }

    @Override
    public void gf_playerPoint_onGeoQueryReady()
    {

    }

    @Override
    public void gf_playerPoint_onGeoQueryError(DatabaseError pError)
    {

    }




    @Override
    public void gf_goldPoint_onKeyEntered(String pKey, LatLng pLocation, boolean p3DCompatible)
    {
        try
        {
            Bitmap goldMarker = BitmapUtils.retrieve(mContext, Constants.NAME_CHEST_TYPE_GOLD);
            Bitmap sponsor = mSponsoredChest.getRandomSponsorBitmap();

            Bitmap marker = BitmapUtils.getSponsoredMarker(mContext, goldMarker, sponsor);

            mView.addGoldPoint(pKey, pLocation, marker);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error presenting gold marker bitmap: " + ex.getMessage());
        }
    }

    @Override
    public void gf_goldPoint_onKeyExited(String pKey, boolean p3DCompatible)
    {
        mView.removeGoldPoint(pKey);
    }

    @Override
    public void gf_goldPoint_onGeoQueryReady()
    {
        Log.i(TAG, "Gold geoquery finished");
    }

    @Override
    public void gf_silverPoint_onKeyEntered(String pKey, LatLng pLocation,  boolean p3DCompatible)
    {
        try
        {
            Bitmap silverMarker = BitmapUtils.retrieve(mContext, Constants.NAME_CHEST_TYPE_SILVER);
            Bitmap sponsor = mSponsoredChest.getRandomSponsorBitmap();
            Bitmap marker = BitmapUtils.getSponsoredMarker(mContext, silverMarker, sponsor);

            mView.addSilverPoint(pKey, pLocation, marker);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error presenting silver sponsored marker: " + ex.getMessage());
        }
    }

    @Override
    public void gf_silverPoint_onKeyExited(String pKey, boolean p3DCompatible)
    {
        mView.removeSilverPoint(pKey);
    }

    @Override
    public void gf_silverPoint_onGeoQueryReady()
    {
        Log.i(TAG, "Silver geoquery finished");
    }

    @Override
    public void gf_bronzePoint_onKeyEntered(String pKey, LatLng pLocation,  boolean p3DCompatible)
    {
        try
        {
            Bitmap bronzeBitmap = BitmapUtils.retrieve(mContext, Constants.NAME_CHEST_TYPE_BRONZE);
            Bitmap sponsor = mSponsoredChest.getRandomSponsorBitmap();
            Bitmap marker = BitmapUtils.getSponsoredMarker(mContext, bronzeBitmap, sponsor);

            mView.addBronzePoint(pKey, pLocation, marker);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error presenting bronze sponsored marker: " + ex.getMessage());
        }
    }

    @Override
    public void gf_bronzePoint_onKeyExited(String pKey, boolean p3DCompatible)
    {
        mView.removeBronzePoint(pKey);
    }

    @Override
    public void gf_bronzePoint_onGeoQueryReady()
    {
        Log.i(TAG, "Bronze geoquery finished");
    }

    /*
    *
    *
    *   WILDCARD LISTENERS
    *
    *
    * */
    @Override
    public void gf_wildcardPoint_onKeyEntered(String pKey, LatLng pLocation, boolean p3DCompatible)
    {
        Bitmap wildcardBitmap = BitmapUtils.retrieve(mContext, Constants.NAME_CHEST_TYPE_WILDCARD);
        mView.addWildcardPoint(pKey, pLocation, wildcardBitmap);
    }

    @Override
    public void gf_wildcardPoint_onKeyExited(String pKey, boolean p3DCompatible)
    {
        mView.removeWildcardPoint(pKey);
    }

    @Override
    public void gf_wildcardPoint_onGeoQueryReady()
    {

    }


    /*
     *
     *
     *   SPONSOR LISTENERS
     *
     *
     * */

    @Override
    public void gf_sponsorPrize_onKeyEntered(String key, LatLng location)
    {
        //Prize point will not be drawed here. Data is requested
        mFirebaseInteractor.retrieveSponsorPrizeData(key, location);

    }

    @Override
    public void gf_sponsorPrize_onKeyExited(String key, boolean compatible3D)
    {
        mView.removeSponsorPrizePoint(key);
    }

    @Override
    public void gf_sponsorPrize_onGeoQueryReady()
    {

    }

    // FIREBASE STATIC POINTS
    @Override
    public void fb_salePoint_onDataChange(String pKey, SalePointData pSalePointData)
    {
        MarkerData markerData = new MarkerData(pKey, Constants.TAG_MARKER_SALEPOINT, null);
        mView.addSalePointData(pKey, pSalePointData.getTitle(), pSalePointData.getSnippet(), markerData);
    }

    @Override
    public void fb_salePoint_onCancelled(DatabaseError databaseError)
    {
        Log.e(TAG, "fb_salePoint_onCancelled -" + databaseError.getMessage());
    }

    // FIREBASE VENDOR POINTS
    @Override
    public void fb_vendorPoint_onDataChange(String pKey, VendorPointData pSalePointData)
    {
        if(pSalePointData != null)
        {
            MarkerData markerData = new MarkerData(pKey, Constants.TAG_MARKER_VENDOR, pSalePointData.getVendorCode());
            mView.addVendorPointData(pKey, mContext.getString(R.string.yvr_vendor_marker_title), markerData);
        }
    }

    @Override
    public void fb_vendorPoint_onCancelled(DatabaseError databaseError)
    {

    }

    @Override
    public void fb_playerPoint_onDataChange(String key, PlayerPointData playerPointData)
    {
        if(playerPointData != null)
        {
            if(!TextUtils.equals(key, UserData.getInstance(mContext).getAuthProviderId()))
            {
                MarkerData markerData = new MarkerData(key, Constants.TAG_MARKER_PLAYER, playerPointData.getNickname());

                String title = String.format(mContext.getString(R.string.title_marker_player), playerPointData.getNickname());
                String content = mContext.getString(R.string.label_player_code_snippet);

                mView.addPlayerPointData(key, title, content, markerData);
            }
        }
    }

    @Override
    public void fb_playerPoint_onCancelled(DatabaseError databaseError)
    {
        Log.e(TAG, "Error on dataChange for PlayerData: " + databaseError.getDetails());
    }

    @Override
    public void fb_currentPlayerDataInserted(String key, GeoLocation location)
    {
        try
        {
            //If data is inserted, then inserts respetive GeoFire location
            mInteractor.setPlayerLocation(key, location);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error trying to insert current player data: " + ex.getMessage());
        }
    }

    @Override
    public void onPendingChallengesSuccess(PendingsResponse body)
    {
        try
        {
            UserData.getInstance(mContext).savePendingChallenges(body.getMessage());

            String pending = (TextUtils.isEmpty(UserData.getInstance(mContext).getPendingChallenges())) ? "0" : UserData.getInstance(mContext).getPendingChallenges();

            int quantity = Integer.valueOf(pending);

            if(quantity > 0)
                mView.setPendingChallenges(pending, true);
            else
                mView.setPendingChallenges(pending, false);

        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error on success Pending Challenges: " + ex.getMessage());
        }
    }

    @Override
    public void onPendingChallengesError(int code, Throwable o)
    {
        try
        {
            Log.e(TAG, "Error retrieving pending challenges: CodeStatus = " +
                    String.valueOf(code) + ", Throwable: " + o.getLocalizedMessage());
        }catch (Exception ex) { ex.printStackTrace(); }
    }

    @Override
    public void onRetrieveBitmapSuccess(Bitmap bitmap, String name)
    {
        saveBitmap(bitmap, name);
    }

    //  FIREBASE GOLD POINTS
    @Override
    public void fb_goldPoint_onDataChange(String pKey, LocationPrizeYCRData pGoldPointData)
    {
        if(pGoldPointData != null)
            mView.addGoldPointData(pKey, pGoldPointData.getCoins(), pGoldPointData.getDetail());


    }

    @Override
    public void fb_goldPoint_onCancelled(DatabaseError databaseError)
    {
        Log.e(TAG, "GoldPoint DatabaseError: OnCancelled Fired!");
    }

    //  FIREBASE SILVER POINTS
    @Override
    public void fb_silverPoint_onDataChange(String pKey, LocationPrizeYCRData pSilverPointData)
    {
        if(pSilverPointData != null)
            mView.addSilverPointData(pKey, pSilverPointData.getCoins(), pSilverPointData.getDetail());
    }

    @Override
    public void fb_silverPoint_onCancelled(DatabaseError databaseError)
    {
        Log.e(TAG, "SilverPoint DatabaseError: OnCancelled Fired!");
    }

    @Override
    public void fb_bronzePoint_onDataChange(String pKey, LocationPrizeYCRData pBronzePointData)
    {
        if(pBronzePointData != null)
            mView.addBronzePointData(pKey, pBronzePointData.getCoins(), pBronzePointData.getDetail());
    }

    @Override
    public void fb_bronzePoint_onCancelled(DatabaseError databaseError)
    {
        Log.e(TAG, "BronzePoint DatabaseError: OnCancelled Fired!");
    }

    @Override
    public void fb_wildcardPoint_onDataChange(String pKey, WildcardYCRData wildcardYCRData)
    {
        String title = mContext.getString(R.string.title_wildcard_pointer);
        String message = mContext.getString(R.string.label_wildcard_pointer);
        if(wildcardYCRData != null)
            mView.addWildcardPointData(pKey, wildcardYCRData.getBrand(), title, message);
    }

    @Override
    public void fb_wildcardPoint_onCancelled(DatabaseError databaseError)
    {

    }

    @Override
    public void fb_sponsorPrize_onDataChange(final String key, final LatLng location, final SponsorPrizeData sponsorPrizeData)
    {
        if(sponsorPrizeData != null)
        {
            // If 'visible' must draw marker on map
            if(sponsorPrizeData.getVisible() > 0)
            {
                Bitmap sponsorMarkerBmp = BitmapUtils.retrieve(mContext, sponsorPrizeData.getName());

                //If sponsor bitmap does not exists
                if(sponsorMarkerBmp == null)
                {
                    //Downloads bitmap with Picasso
                    Picasso.with(mContext).load(sponsorPrizeData.getMarkerUrl()).into(new Target()
                    {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                        {
                            //Saves bitmap
                            Bitmap resizedBitmap = BitmapUtils.scaleMarker(bitmap, mContext);
                            BitmapUtils.save(mContext, resizedBitmap, sponsorPrizeData.getName());

                            //Draws marker on map
                            mView.addSponsorPrizePoint(key, location, resizedBitmap);

                            //Adds data (title, snippet, tags, etc) to recently added marker
                            MarkerData markerData = new MarkerData(key, Constants.TAG_MARKER_SPONSORPRIZE, sponsorPrizeData.getSponsorid());
                            String title = sponsorPrizeData.getName();
                            String content = sponsorPrizeData.getDescription();
                            mView.addSponsorPrizeData(key, title, content, markerData);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable)
                        {
                            Log.e(TAG, "Error downloading sponsor bitmap: " + errorDrawable.toString());
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable)
                        {

                        }
                    });
                }
                else
                {
                    //Draws marker on map
                    mView.addSponsorPrizePoint(key, location, sponsorMarkerBmp);

                    //Adds data (title, snippet, tags, etc) to recently added marker
                    MarkerData markerData = new MarkerData(key, Constants.TAG_MARKER_SPONSORPRIZE, sponsorPrizeData.getSponsorid());
                    String title = sponsorPrizeData.getName();
                    String content = sponsorPrizeData.getDescription();
                    mView.addSponsorPrizeData(key, title, content, markerData);
                }
            }
        }
    }

    @Override
    public void fb_sponsorPrize_onCancelled(DatabaseError databaseError)
    {

    }

    @Override
    public void detachFirebaseListeners()
    {
        mFirebaseInteractor.detachFirebaseListeners();
    }

    /*
    *
    *
    *   OTROS METODOS
    *
    *
    */

    private void addFlags(Intent pIntent)
    {
        pIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }



    private void saveBitmap(Bitmap bitmap, String name)
    {
        try
        {

            FileOutputStream outputStream = mContext.openFileOutput(name, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();

            Log.i(TAG, "Bitmap saved!");

        }
        catch (Exception e)
        {
            Log.e(TAG, "Error while saving bitmap: " + e.getMessage());
        }
    }

    private void ProcessErrorMessage(int pCodeStatus, Throwable pThrowable)
    {
        DialogViewModel errorResponse = new DialogViewModel();

        try
        {
            String Titulo;
            String Linea1;
            String Button;

            if (pThrowable != null)
            {
                if (pThrowable instanceof SocketTimeoutException)
                {
                    Titulo = mContext.getString(R.string.error_title_something_went_wrong);
                    Linea1 = mContext.getString(R.string.error_content_something_went_wrong_try_again);
                    Button = mContext.getString(R.string.button_accept);
                }
                else if (pThrowable instanceof IOException)
                {
                    Titulo = mContext.getString(R.string.error_title_internet_connecttion);
                    Linea1 = mContext.getString(R.string.error_content_internet_connecttion);
                    Button = mContext.getString(R.string.button_accept);
                }
                else
                {
                    Titulo = mContext.getString(R.string.error_title_something_went_wrong);
                    Linea1 = mContext.getString(R.string.error_content_something_went_wrong_try_again);
                    Button = mContext.getString(R.string.button_accept);
                }
            }
            else
            {

                Titulo = mContext.getString(R.string.error_title_something_went_wrong);
                Linea1 = mContext.getString(R.string.error_content_something_went_wrong_try_again);
                Button = mContext.getString(R.string.button_accept);

            }

            errorResponse.setTitle(Titulo);
            errorResponse.setLine1(Linea1);
            errorResponse.setAcceptButton(Button);
            this.mView.showErrorMessage(errorResponse);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
