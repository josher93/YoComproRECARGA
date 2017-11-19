package com.globalpaysolutions.yocomprorecarga.interactors;

import android.content.Context;
import android.util.Log;

import com.globalpaysolutions.yocomprorecarga.api.ApiClient;
import com.globalpaysolutions.yocomprorecarga.api.ApiInterface;
import com.globalpaysolutions.yocomprorecarga.interactors.interfaces.ICapturePrizeInteractor;
import com.globalpaysolutions.yocomprorecarga.models.api.ExchangeReqBody;
import com.globalpaysolutions.yocomprorecarga.models.api.ExchangeResponse;
import com.globalpaysolutions.yocomprorecarga.models.api.ExchangeSouvenirReq;
import com.globalpaysolutions.yocomprorecarga.models.api.ExchangeWildcardReq;
import com.globalpaysolutions.yocomprorecarga.models.api.ExchangeWildcardResponse;
import com.globalpaysolutions.yocomprorecarga.models.api.Tracking;
import com.globalpaysolutions.yocomprorecarga.models.api.WinPrizeResponse;
import com.globalpaysolutions.yocomprorecarga.utils.UserData;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Josué Chávez on 11/05/2017.
 */

public class CapturePrizeInteractor implements ICapturePrizeInteractor
{
    private static final String TAG = CapturePrizeInteractor.class.getSimpleName();
    private Context mContext;
    private CapturePrizeListener mListener;
    private UserData mUserData;

    public CapturePrizeInteractor(Context pContext, CapturePrizeListener pListener)
    {
        this.mContext = pContext;
        this.mListener = pListener;
        this.mUserData = UserData.getInstance(mContext);
    }

    @Override
    public void retrieveConsumerTracking()
    {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        final Call<Tracking> call = apiService.getConsumerTracking(mUserData.getUserAuthenticationKey());

        call.enqueue(new Callback<Tracking>()
        {
            @Override
            public void onResponse(Call<Tracking> call, Response<Tracking> response)
            {
                if(response.isSuccessful())
                {
                    Tracking trackingResponse = response.body();
                    mListener.onRetrieveTracking(trackingResponse);
                    Log.i(TAG, "TotalWinCoins:" + String.valueOf(trackingResponse.getTotalWinCoins())
                            + ", TotalWinPrizes:" + String.valueOf(trackingResponse.getTotalWinCoins())
                            + ", CurrentCoinsProgress:" + String.valueOf(trackingResponse.getCurrentCoinsProgress()));
                }
                else
                {
                    int codeResponse = response.code();
                    mListener.onTrackingError(codeResponse, null);
                }
            }

            @Override
            public void onFailure(Call<Tracking> call, Throwable t)
            {
                mListener.onTrackingError(0, t);
            }
        });

    }

    @Override
    public void openCoinsChest(LatLng pLocation, String pFirebaseID, final int pChestType, int pEraID)
    {
        ExchangeReqBody requestBody = new ExchangeReqBody();
        requestBody.setLocationID(pFirebaseID);
        requestBody.setLatitude(pLocation.latitude);
        requestBody.setLongitude(pLocation.longitude);
        requestBody.setChestType(pChestType);
        requestBody.setAgeID(pEraID);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        final Call<ExchangeResponse> call = apiService.exchangeChest(mUserData.getUserAuthenticationKey(), requestBody);

        call.enqueue(new Callback<ExchangeResponse>()
        {
            @Override
            public void onResponse(Call<ExchangeResponse> call, Response<ExchangeResponse> response)
            {
                if(response.isSuccessful())
                {
                    ExchangeResponse exchangeResponse = response.body();
                    mListener.onOpenChestSuccess(exchangeResponse, pChestType);
                }
                else
                {
                    int codeResponse = response.code();
                    mListener.onOpenChestError(codeResponse, null);
                }
            }
            @Override
            public void onFailure(Call<ExchangeResponse> call, Throwable t)
            {
                mListener.onOpenChestError(0, t);
            }
        });

    }

    @Override
    public void saveUserTracking(Tracking pTracking)
    {
        mUserData.SaveUserTrackingProgess(pTracking.getTotalWinCoins(),
                pTracking.getTotalWinPrizes(),
                pTracking.getCurrentCoinsProgress(),
                pTracking.getTotalSouvenirs(),
                pTracking.getAgeID());
    }

    @Override
    public void atemptRedeemPrize()
    {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        final Call<WinPrizeResponse> call = apiService.redeemPrize(mUserData.getUserAuthenticationKey());

        call.enqueue(new Callback<WinPrizeResponse>()
        {
            @Override
            public void onResponse(Call<WinPrizeResponse> call, Response<WinPrizeResponse> response)
            {
                if(response.isSuccessful())
                {
                    WinPrizeResponse redeemPrize = response.body();
                    mListener.onRedeemPrizeSuccess(redeemPrize);
                }
                else
                {
                    int codeResponse = response.code();
                    mListener.onRedeemPrizeError(codeResponse, null);
                    Log.e(TAG, response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<WinPrizeResponse> call, Throwable t)
            {
                mListener.onRedeemPrizeError(0, t);
            }
        });

    }

    @Override
    public void exchangeWildcard(String pFirebaseID, int eraID)
    {
        ExchangeWildcardReq requestBody = new ExchangeWildcardReq();
        requestBody.setAgeID(eraID);
        requestBody.setLocationID(pFirebaseID);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        final Call<ExchangeWildcardResponse> call = apiService.exchangeWildcard(mUserData.getUserAuthenticationKey(), requestBody);

        call.enqueue(new Callback<ExchangeWildcardResponse>()
        {
            @Override
            public void onResponse(Call<ExchangeWildcardResponse> call, Response<ExchangeWildcardResponse> response)
            {
                if(response.isSuccessful())
                {
                   mListener.onExchangeWildcardSuccess(response.body());
                }
                else
                {
                    int codeResponse = response.code();
                    mListener.onExchangeWildcardError(codeResponse, null);
                    Log.e(TAG, response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<ExchangeWildcardResponse> call, Throwable t)
            {
                mListener.onExchangeWildcardError(0, t);
            }
        });
    }
}
