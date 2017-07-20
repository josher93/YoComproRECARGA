package com.globalpaysolutions.yocomprorecarga.interactors;

import com.globalpaysolutions.yocomprorecarga.models.geofire_data.LocationPrizeYCRData;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;

/**
 * Created by Josué Chávez on 31/03/2017.
 */

public interface FirebasePOIListener
{
    //GeoFire GoldPoints
    void gf_goldPoint_onKeyEntered(String pKey, LatLng pLocation,  boolean p3DCompatible);
    void gf_goldPoint_onKeyExited(String pKey, boolean p3DCompatible);

    //GeoFire SilverPoints
    void gf_silverPoint_onKeyEntered(String pKey, LatLng pLocation,  boolean p3DCompatible);
    void gf_silverPoint_onKeyExited(String pKey, boolean p3DCompatible);

    //GeoFire BronzePoints
    void gf_bronzePoint_onKeyEntered(String pKey, LatLng pLocation,  boolean p3DCompatible);
    void gf_bronzePoint_onKeyExited(String pKey, boolean p3DCompatible);

    /*
    *
    *
    *   DATA
    *
    */

    // GeoFire GoldPointsData
    void fb_goldPoint_onDataChange(String pKey, LocationPrizeYCRData pGoldPointData);
    void fb_goldPoint_onCancelled(DatabaseError databaseError);

    // GeoFire SilverPointsData
    void fb_silverPoint_onDataChange(String pKey, LocationPrizeYCRData pSilverPointData);
    void fb_silverPoint_onCancelled(DatabaseError databaseError);

    // GeoFire BronzePointsData
    void fb_bronzePoint_onDataChange(String pKey, LocationPrizeYCRData pBronzePointData);
    void fb_bronzePoint_onCancelled(DatabaseError databaseError);
}