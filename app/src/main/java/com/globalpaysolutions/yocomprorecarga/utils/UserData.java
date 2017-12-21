package com.globalpaysolutions.yocomprorecarga.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.globalpaysolutions.yocomprorecarga.models.Country;
import com.globalpaysolutions.yocomprorecarga.models.api.Achievement;

import java.nio.charset.MalformedInputException;

/**
 * Created by Josué Chávez on 16/01/2017.
 */

public class UserData
{
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private static Context mContext;
    private int PRIVATE_MODE = 0;
    private static UserData singleton;

    private static final String PREFERENCES_NAME = "ycrGeneralPreferences";

    //User Info
    private static final String KEY_CONSUMER_COUNTRY_ID = "usr_country_id";
    private static final String KEY_CONSUMER_COUNTRY_PHONE_CODE = "usr_country_phone_code";
    private static final String KEY_CONSUMER_COUNTRY_IS3CODE = "usr_country_iso3code";
    private static final String KEY_CONSUMER_COUNTRY_NAME = "usr_country_name";
    private static final String KEY_CONSUMER_PHONE = "usr_phone_number";
    private static final String KEY_CONSUMER_ID = "usr_consumer_id";
    private static final String KEY_CONSUMER_FIRSTNAME = "usr_firstname";
    private static final String KEY_CONSUMER_LASTNAME = "usr_lastname";
    private static final String KEY_CONSUMER_EMAIL = "usr_email";
    private static final String KEY_CONSUMER_NICKNAME = "usr_nickname";
    private static final String KEY_CONSUMER_SIMPLE_PHONE = "usr_simple_phone";

    //App Preferences and Settings
    private static final String KEY_HAS_ACCEPTED_TERMS = "usr_has_accepted_terms";
    private static final String KEY_HAS_SELECTED_COUNTRY = "usr_has_selected_country";
    private static final String KEY_HAS_CONFIRMED_PHONE = "usr_has_confirmed_phone";
    private static final String KEY_HAS_GRANTED_DEVICE_PERMISSIONS = "usr_has_granted_device_permissions";
    private static final String KEY_HAS_AUTHENTICATED = "usr_has_authenticated";
    private static final String KEY_AUTHENTICATION_KEY = "usr_authentication_key";
    private static final String KEY_HAS_READ_INTRO = "usr_has_read_intro";
    private static final String KEY_HAS_CONFIRMED_LIMITED_FUNCTIONALIITY = "usr_has_confirmed_limited_functionalty";
    private static final String KEY_HAS_SEEN_INTRO = "usr_has_seen_intro";
    private static final String KEY_HAS_SELECTED_ERA = "usr_has_selected_era";
    private static final String KEY_HAS_SET_NICKNAME = "usr_has_set_nickname";

    //Coins and Chests
    private static final String KEY_TOTAL_WON_COINS = "usr_total_won_coins";
    private static final String KEY_TOTAL_WON_PRIZES = "usr_total_won_prizes";
    private static final String KEY_CURRENT_COINS_PROGRESS = "usr_current_coins_progress";
    private static final String KEY_LAST_CHEST_EXCHANGED_VALUE = "usr_last_chest_exchanged_value";
    private static final String KEY_AWAIT_TIME_PENDING = "usr_await_time_pending";
    private static final String KEY_TOTAL_SOUVENIR = "usr_winned_souvenir";
    private static final String KEY_LAST_CHEST_ID = "usr_last_chest_exchanged";

    //Achievements
    private static final String KEY_ACHIEVEMENT_TITLE = "usr_achievement_title";
    private static final String KEY_ACHIEVEMENT_DESCRIPTION = "usr_achievement_description";
    private static final String KEY_ACHIEVEMENT_SCORE = "usr_achievement_score";
    private static final String KEY_ACHIEVEMENT_LEVEL = "usr_achievement_level";
    private static final String KEY_ACHIEVEMENT_VALUE_NEXT_LEVEL = "usr_achievement_value_next_level";
    private static final String KEY_ACHIEVEMENT_PRIZE = "usr_achievement_prize";
    private static final String KEY_ACHIEVEMENT_FROM_SOUVENIR_EXCHANGE = "usr_achievement_from_souvenir_exchange";

    //Prizes
    private static final String KEY_LAST_PRIZE_EXCHANGED_TITLE = "usr_last_prize_exchanged_title";
    private static final String KEY_LAST_PRIZE_EXCHANGED_DESCRIPTION = "usr_last_prize_exchanged_description";
    private static final String KEY_LAST_PRIZE_EXCHANGED_CODE = "usr_last_prize_exchanged_code";
    private static final String KEY_LAST_PRIZE_EXCHANGED_DIAL = "usr_last_prize_exchanged_dial";
    private static final String KEY_LAST_PRIZE_EXCHANGED_LEVEL = "usr_last_prize_exchanged_level";
    private static final String KEY_LAST_PRIZE_EXCHANGED_IMGURL = "usr_last_prize_exchanged_img_url";
    private static final String KEY_LAST_PRIZE_EXCHANGED_HEXCOLOR = "usr_last_prize_exchanged_hex_color";


    //Device
    private static final String KEY_UNIQUE_DEVICE_ID = "app_unique_device_id";
    private static final String KEY_3D_COMPATIBLE_DEVICE = "app_3d_compatible_device";

    //Facebook Data
    private static final String KEY_FACEBOOK_FIRST_NAME = "usr_facebook_first_name";
    private static final String KEY_FACEBOOK_LAST_NAME = "usr_facebook_last_name";
    private static final String KEY_FACEBOOK_EMAIL = "usr_facebook_email";
    private static final String KEY_FACEBOOK_FULLNAME = "usr_facebook_fullname";
    private static final String KEY_FACEBOOK_PROFILE_ID = "usr_facebook_profile_id";
    private static final String KEY_FACEBOOK_URL = "usr_facebook_url";

    //Souvenir
    private static final String KEY_SOUVENIR_TITLE = "usr_souvenir_title";
    private static final String KEY_SOUVENIR_DESCRIPTION = "usr_souvenir_description";
    private static final String KEY_SOUVENIR_IMG_URL = "usr_souvenir_img_url";
    private static final String KEY_SOUVENIR_QUANTITY_OWNED = "usr_souvenir_quantity_owned";
    private static final String KEY_SOUVENIR_PRICE = "usr_souvenir_prce";

    //Era
    private static final String KEY_ERA_ID = "usr_age_id";
    private static final String KEY_ERA_NAME = "usr_age_name";
    private static final String KEY_ERA_URL_ICON = "usr_age_url_image";
    private static final String KEY_ERA_MARKER_GOLD = "usr_era_marker_gold";
    private static final String KEY_ERA_MARKER_SILVER = "usr_era_marker_silver";
    private static final String KEY_ERA_MARKER_BRONZE = "usr_era_marker_bronze";
    private static final String KEY_ERA_MARKER_WILDCARD = "usr_era_marker_wildcard";

    //First time settings
    private static final String KEY_FIRTTIME_SIMPLE_INSTRUCTIONS_SHOWED = "usr_firsttime_simple_instructions";

    //Last Wildcard touched
    private static final String KEY_LAST_WILDCARD_TOUCHED_FIREBASE_ID = "usr_last_wildcard_touched_firebase_id";
    private static final String KEY_LAST_WILDCARD_TOUCHED_CHEST_TYPE = "usr_last_wildcard_touched_chest_type";

    private UserData(Context pContext)
    {
        UserData.mContext = pContext;
        mPreferences = mContext.getSharedPreferences(PREFERENCES_NAME, PRIVATE_MODE);
        mEditor = mPreferences.edit();
    }

    public static synchronized UserData getInstance(Context context)
    {
        if (singleton == null)
        {
            singleton = new UserData(context);
        }
        return singleton;
    }

    /*
    * ********************
    *
    *       SAVE
    *
    * ******************
    */

    public void saveUserPhoneInfo(String pCountryID, String pCountryPhoneCode, String pIso3Code, String pCountryName, String pPhone, int pConsumerID)
    {
        try
        {
            mEditor.putString(KEY_CONSUMER_COUNTRY_ID, pCountryID);
            mEditor.putString(KEY_CONSUMER_COUNTRY_PHONE_CODE, pCountryPhoneCode);
            mEditor.putString(KEY_CONSUMER_COUNTRY_IS3CODE, pIso3Code);
            mEditor.putString(KEY_CONSUMER_COUNTRY_NAME, pCountryName);
            mEditor.putString(KEY_CONSUMER_PHONE, pPhone);
            mEditor.putInt(KEY_CONSUMER_ID, pConsumerID);
            mEditor.commit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void savePreselectedCountryInfo(String countryId, String countryPhoneCode, String iso3code, String countryName)
    {
        try
        {
            mEditor.putString(KEY_CONSUMER_COUNTRY_ID, countryId);
            mEditor.putString(KEY_CONSUMER_COUNTRY_PHONE_CODE, countryPhoneCode);
            mEditor.putString(KEY_CONSUMER_COUNTRY_IS3CODE, iso3code);
            mEditor.putString(KEY_CONSUMER_COUNTRY_NAME, countryName);
            mEditor.commit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void saveUserGeneralInfo(String pFirstname, String pLastname, String pEmail, String pUserPhone)
    {
       try
       {
           mEditor.putString(KEY_CONSUMER_FIRSTNAME, pFirstname);
           mEditor.putString(KEY_CONSUMER_LASTNAME, pLastname);
           mEditor.putString(KEY_CONSUMER_EMAIL, pEmail);
           mEditor.putString(KEY_CONSUMER_PHONE, pUserPhone); //Valor null. Se guardará en conf. de SMS
           mEditor.commit();
       }
       catch (Exception ex) { ex.printStackTrace(); }
    }

    public void HasAccpetedTerms(boolean pAccepted)
    {
        mEditor.putBoolean(KEY_HAS_ACCEPTED_TERMS, pAccepted);
        mEditor.commit();
    }

    public void HasSelectedCountry(boolean pSelectedCountry)
    {
        mEditor.putBoolean(KEY_HAS_SELECTED_COUNTRY, pSelectedCountry);
        mEditor.commit();
    }

    public void HasConfirmedPhone(boolean pConfirmedPhone)
    {
        mEditor.putBoolean(KEY_HAS_CONFIRMED_PHONE, pConfirmedPhone);
        mEditor.commit();
    }

    public void HasGrantedDevicePermissions(boolean pGrantedPermissions)
    {
        mEditor.putBoolean(KEY_HAS_GRANTED_DEVICE_PERMISSIONS, pGrantedPermissions);
        mEditor.commit();
    }

    public void hasAuthenticated(boolean pAuthenticated)
    {
        mEditor.putBoolean(KEY_HAS_AUTHENTICATED, pAuthenticated);
        mEditor.commit();
    }

    public void setHasConfirmedLimitedFunctionality(boolean confirmed)
    {
        mEditor.putBoolean(KEY_HAS_CONFIRMED_LIMITED_FUNCTIONALIITY, confirmed);
        mEditor.commit();
    }

    public void SaveDeviceID(String pDeviceID)
    {
        mEditor.putString(KEY_UNIQUE_DEVICE_ID, pDeviceID);
        mEditor.commit();
    }

    public void SaveUserTrackingProgess(int pCoins, int pPrizes, int pCoinsProgress, int pSouvenirs, int pEraID)
    {
        //int coins = (pCoins < 0) ? 0 : pCoins;
        //mEditor.putInt(KEY_TOTAL_WON_COINS, coins);
        mEditor.putInt(KEY_TOTAL_WON_COINS, pCoins);
        mEditor.putInt(KEY_TOTAL_WON_PRIZES, pPrizes);
        mEditor.putInt(KEY_CURRENT_COINS_PROGRESS, pCoinsProgress);
        mEditor.putInt(KEY_TOTAL_SOUVENIR, pSouvenirs);
        //mEditor.putInt(KEY_ERA_ID, 1); //TODO: Quitar era quemada
        mEditor.putInt(KEY_ERA_ID, pEraID);
        mEditor.commit();
    }

    public void Save3DCompatibleValue(boolean isCompatible)
    {
        mEditor.putBoolean(KEY_3D_COMPATIBLE_DEVICE, isCompatible);
        mEditor.commit();
    }

    public void saveFacebookData(String pFacebookID, String pURL)
    {
        mEditor.putString(KEY_FACEBOOK_PROFILE_ID, pFacebookID);
        mEditor.putString(KEY_FACEBOOK_URL, pURL);
        mEditor.commit();
    }

    public void saveAuthenticationKey(String pAuthKey)
    {
        mEditor.putString(KEY_AUTHENTICATION_KEY, pAuthKey);
        mEditor.commit();
    }

    public void saveNickname(String pNickname)
    {
        mEditor.putString(KEY_CONSUMER_NICKNAME, pNickname);
        mEditor.commit();
    }

    public void saveLastChestValue(int pCoins)
    {
        int coins = (pCoins < 0) ? 0 : pCoins;
        mEditor.putInt(KEY_LAST_CHEST_EXCHANGED_VALUE, coins);
        mEditor.commit();
    }

    public void saveAwaitTime(String pTime)
    {
        mEditor.putString(KEY_AWAIT_TIME_PENDING, pTime);
        mEditor.commit();
    }

    public void saveLastPrizeTitle(String pTitle)
    {
        mEditor.putString(KEY_LAST_PRIZE_EXCHANGED_TITLE, pTitle);
        mEditor.commit();
    }

    public void saveLastPrizeDescription(String pDescription)
    {
        mEditor.putString(KEY_LAST_PRIZE_EXCHANGED_DESCRIPTION, pDescription);
        mEditor.commit();
    }

    public void saveLastPrizeCode(String pCode)
    {
        mEditor.putString(KEY_LAST_PRIZE_EXCHANGED_CODE, pCode);
        mEditor.commit();
    }

    public void saveLastPrizeDial(String pDial)
    {
        mEditor.putString(KEY_LAST_PRIZE_EXCHANGED_DIAL, pDial);
        mEditor.commit();
    }

    public void saveLastPrizeLevel(int pLevel)
    {
        mEditor.putInt(KEY_LAST_PRIZE_EXCHANGED_LEVEL, pLevel);
        mEditor.commit();
    }

    public void saveLastPrizeLogoUrl(String url)
    {
        mEditor.putString(KEY_LAST_PRIZE_EXCHANGED_IMGURL, url);
        mEditor.commit();
    }

    public void saveLastPrizeExchangedColor(String hexColor)
    {
        mEditor.putString(KEY_LAST_PRIZE_EXCHANGED_HEXCOLOR, hexColor);
        mEditor.commit();
    }

    public void saveFacebookFullname(String pFullname)
    {
        mEditor.putString(KEY_FACEBOOK_FULLNAME, pFullname);
        mEditor.commit();
    }

    public void saveSimpleInstructionsSetting(boolean pShowed)
    {
        mEditor.putBoolean(KEY_FIRTTIME_SIMPLE_INSTRUCTIONS_SHOWED, pShowed);
        mEditor.commit();
    }

    public void saveSimpleUserPhone(String pUserPhone)
    {
        mEditor.putString(KEY_CONSUMER_SIMPLE_PHONE, pUserPhone);
        mEditor.commit();
    }

    public void saveHasReadIntro(boolean pRead)
    {
        mEditor.putBoolean(KEY_HAS_READ_INTRO, pRead);
        mEditor.commit();
    }

    public void saveEraID(int eraID)
    {
        mEditor.putInt(KEY_ERA_ID, eraID);
        mEditor.commit();
    }

    public void saveLastAchievement(Achievement achievement)
    {
        mEditor.putString(KEY_ACHIEVEMENT_TITLE, achievement.getName());
        mEditor.putString(KEY_ACHIEVEMENT_DESCRIPTION, "");
        mEditor.putInt(KEY_ACHIEVEMENT_SCORE, achievement.getScore());
        mEditor.putInt(KEY_ACHIEVEMENT_LEVEL, achievement.getLevel());
        mEditor.putInt(KEY_ACHIEVEMENT_VALUE_NEXT_LEVEL, achievement.getValueNextLevel());
        mEditor.putInt(KEY_ACHIEVEMENT_PRIZE, achievement.getPrize());
        mEditor.commit();
    }

    public void saveSouvenirObtained(String pTitle, String pDescription, String pImageUrl, int pQuantityOwned)
    {

        mEditor.putString(KEY_SOUVENIR_TITLE, pTitle);
        mEditor.putString(KEY_SOUVENIR_DESCRIPTION, pDescription);
        mEditor.putString(KEY_SOUVENIR_IMG_URL, pImageUrl);
        mEditor.putInt(KEY_SOUVENIR_QUANTITY_OWNED, pQuantityOwned);
        mEditor.commit();
    }

    public void saveEraSelected(int eraID, String eraName, String eraUrl, String markerGold, String markerSilver,
                                String markerBronze, String markerWildcard)
    {
        try
        {
            mEditor.putInt(KEY_ERA_ID, eraID);
            mEditor.putString(KEY_ERA_NAME, eraName);
            mEditor.putString(KEY_ERA_URL_ICON, eraUrl);
            mEditor.putString(KEY_ERA_MARKER_GOLD, markerGold);
            mEditor.putString(KEY_ERA_MARKER_SILVER, markerSilver);
            mEditor.putString(KEY_ERA_MARKER_BRONZE, markerBronze);
            mEditor.putString(KEY_ERA_MARKER_WILDCARD, markerWildcard);
            mEditor.commit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void saveLastWildcardTouched(String pFirebaseID, int chestType)
    {
        mEditor.putString(KEY_LAST_WILDCARD_TOUCHED_FIREBASE_ID, pFirebaseID);
        mEditor.putInt(KEY_LAST_WILDCARD_TOUCHED_CHEST_TYPE, chestType);
        mEditor.commit();
    }

    public void saveAchievementFromSouvenir(String fromSouvenirKey)
    {
        mEditor.putString(KEY_ACHIEVEMENT_FROM_SOUVENIR_EXCHANGE, fromSouvenirKey);
        mEditor.commit();
    }

    public void hasSelectedEra(boolean hasSelected)
    {
        mEditor.putBoolean(KEY_HAS_SELECTED_ERA, hasSelected);
        mEditor.commit();
    }

    public void saveLastExchangedChestID(String lastExchanged)
    {
        mEditor.putString(KEY_LAST_CHEST_ID, lastExchanged);
        mEditor.commit();
    }

    public void hasSetNickname(boolean hasSetNickname)
    {
        mEditor.putBoolean(KEY_HAS_SET_NICKNAME, hasSetNickname);
        mEditor.commit();
    }

    /*
    * ********************
    *
    *       SELECT
    *
    * ******************
    */

    public String GetMsisdn()
    {
        String phoneCode = mPreferences.getString(KEY_CONSUMER_COUNTRY_PHONE_CODE, "");
        String phoneNumber = mPreferences.getString(KEY_CONSUMER_PHONE, "");

        return phoneCode + phoneNumber;
    }

    public String GetCountryID()
    {
        return mPreferences.getString(KEY_CONSUMER_COUNTRY_ID, "0");
    }

    public boolean UserAcceptedTerms()
    {
        return mPreferences.getBoolean(KEY_HAS_ACCEPTED_TERMS, false);
    }

    public boolean UserSelectedCountry()
    {
        return mPreferences.getBoolean(KEY_HAS_SELECTED_COUNTRY, false);
    }

    public boolean UserVerifiedPhone()
    {
        return mPreferences.getBoolean(KEY_HAS_CONFIRMED_PHONE, false);
    }

    public boolean UserGrantedDevicePermissions()
    {
        return mPreferences.getBoolean(KEY_HAS_GRANTED_DEVICE_PERMISSIONS, false);
    }

    public boolean isUserAuthenticated()
    {
        return mPreferences.getBoolean(KEY_HAS_AUTHENTICATED, false);
    }

    public boolean isUserConfirmedLimitedFunctionality()
    {
        return mPreferences.getBoolean(KEY_HAS_CONFIRMED_LIMITED_FUNCTIONALIITY, false);
    }

    public String getUserPhone()
    {
        return mPreferences.getString(KEY_CONSUMER_PHONE, "");
    }

    public String GetUserFormattedPhone()
    {
        String phoneCode = mPreferences.getString(KEY_CONSUMER_PHONE, "");
        phoneCode = phoneCode.substring(0,4) + "-" + phoneCode.substring(4,phoneCode.length());
        return phoneCode;
    }

    public String GetIso3Code()
    {
        return mPreferences.getString(KEY_CONSUMER_COUNTRY_IS3CODE, "");
    }

    public String GetPhoneCode()
    {
        return mPreferences.getString(KEY_CONSUMER_COUNTRY_PHONE_CODE, "");
    }

    public String GetDeviceID()
    {
        return mPreferences.getString(KEY_UNIQUE_DEVICE_ID, "");
    }

    public int GetConsumerID()
    {
        return mPreferences.getInt(KEY_CONSUMER_ID, 0);
    }

    public int getTotalWonCoins()
    {
        return mPreferences.getInt(KEY_TOTAL_WON_COINS, 0);
    }

    public int GetConsumerPrizes()
    {
        return mPreferences.getInt(KEY_TOTAL_WON_PRIZES, 0);
    }

    public int GetUserCurrentCoinsProgress()
    {
        return mPreferences.getInt(KEY_CURRENT_COINS_PROGRESS, 0);
    }

    public int getLastChestExchangedValue()
    {
        return mPreferences.getInt(KEY_LAST_CHEST_EXCHANGED_VALUE, 0);
    }

    public boolean Is3DCompatibleDevice()
    {
        return mPreferences.getBoolean(KEY_3D_COMPATIBLE_DEVICE, false);
    }

    public String getUserAuthenticationKey()
    {
        return mPreferences.getString(KEY_AUTHENTICATION_KEY, "");
    }

    public String getNickname()
    {
        return mPreferences.getString(KEY_CONSUMER_NICKNAME, "");
    }

    public String getAwaitTimePending()
    {
        return mPreferences.getString(KEY_AWAIT_TIME_PENDING, "");
    }

    public int getCurrentCoinsProgress()
    {
        return mPreferences.getInt(KEY_CURRENT_COINS_PROGRESS, 0);
    }

    public String getLastPrizeTitle()
    {
        return mPreferences.getString(KEY_LAST_PRIZE_EXCHANGED_TITLE, "");
    }

    public String getLastPrizeDescription()
    {
        return mPreferences.getString(KEY_LAST_PRIZE_EXCHANGED_DESCRIPTION, "");
    }

    public String getLastPrizeCode()
    {
        return mPreferences.getString(KEY_LAST_PRIZE_EXCHANGED_CODE, "");
    }

    public String getLastPrizeDial()
    {
        return mPreferences.getString(KEY_LAST_PRIZE_EXCHANGED_DIAL, "");
    }

    public int getLastPrizeLevel()
    {
        return mPreferences.getInt(KEY_LAST_PRIZE_EXCHANGED_LEVEL, 0);
    }

    public String getLasPrizeLogoUrl()
    {
        return mPreferences.getString(KEY_LAST_PRIZE_EXCHANGED_IMGURL, "");
    }

    public String getLasPrizeHexColor()
    {
        return mPreferences.getString(KEY_LAST_PRIZE_EXCHANGED_HEXCOLOR, "");
    }

    public String getFacebookFullname()
    {
        return mPreferences.getString(KEY_FACEBOOK_FULLNAME, "");
    }

    public boolean getSimpleInstructionsShowed()
    {
        return mPreferences.getBoolean(KEY_FIRTTIME_SIMPLE_INSTRUCTIONS_SHOWED, false);
    }

    public Country getSelectedCountry()
    {
        Country country = new Country();
        try
        {
            country.setCode(mPreferences.getString(KEY_CONSUMER_COUNTRY_ID, ""));
            country.setPhoneCode(mPreferences.getString(KEY_CONSUMER_COUNTRY_PHONE_CODE, ""));
            country.setCountrycode(mPreferences.getString(KEY_CONSUMER_COUNTRY_IS3CODE, ""));
            country.setName(mPreferences.getString(KEY_CONSUMER_COUNTRY_NAME, ""));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return country;
    }

    public String getUserSimplePhone()
    {
        return mPreferences.getString(KEY_CONSUMER_SIMPLE_PHONE, "");
    }

    public boolean hasReadIntro()
    {
        return mPreferences.getBoolean(KEY_HAS_READ_INTRO, false);
    }

    public int getEraID()
    {
        return mPreferences.getInt(KEY_ERA_ID, 1);
    }

    public String getEraName()
    {
        return mPreferences.getString(KEY_ERA_NAME, "");
    }
    public String getLastAchievementTitle()
    {
        return mPreferences.getString(KEY_ACHIEVEMENT_TITLE, "");
    }

    public String getLastAchievementDescription()
    {
        return mPreferences.getString(KEY_ACHIEVEMENT_DESCRIPTION, "");
    }

    public int getSavedSouvenirsCount()
    {
        return mPreferences.getInt(KEY_TOTAL_SOUVENIR, 0);
    }

    public String getLastWildcardTouchedFirebaseId()
    {
        return mPreferences.getString(KEY_LAST_WILDCARD_TOUCHED_FIREBASE_ID, "");
    }

    public int getLastWildcardTouchedChestType()
    {
        return mPreferences.getInt(KEY_LAST_WILDCARD_TOUCHED_CHEST_TYPE, 0);
    }

    public boolean getHasSeenIntroValue()
    {
        return mPreferences.getBoolean(KEY_HAS_SEEN_INTRO, false);
    }

    public Achievement getLastAchievement()
    {
        Achievement achievement = new Achievement();
        try
        {
            achievement.setLevel(mPreferences.getInt(KEY_ACHIEVEMENT_LEVEL, 0));
            achievement.setName(mPreferences.getString(KEY_ERA_NAME, ""));
            achievement.setPrize(mPreferences.getInt(KEY_ACHIEVEMENT_LEVEL, 0));
            achievement.setScore(mPreferences.getInt(KEY_ACHIEVEMENT_SCORE, 0));
            achievement.setValueNextLevel(mPreferences.getInt(KEY_ACHIEVEMENT_VALUE_NEXT_LEVEL, 0));
        }
        catch (Exception ex) { ex.printStackTrace();}

        return  achievement;
    }

    public String getAchievementFromSouvenirExchange()
    {
        return mPreferences.getString(KEY_ACHIEVEMENT_FROM_SOUVENIR_EXCHANGE, "");
    }

    public boolean chechUserHasSelectedEra()
    {
        return mPreferences.getBoolean(KEY_HAS_SELECTED_ERA, false);
    }

    public String getLastExchangedChestID()
    {
        return mPreferences.getString(KEY_LAST_CHEST_ID, "");
    }

    public boolean checkSetNickname()
    {
        return mPreferences.getBoolean(KEY_HAS_SET_NICKNAME, false);
    }

    public String getGoldMarker()
    {
        return mPreferences.getString(KEY_ERA_MARKER_GOLD, "");
    }

    public String getSilverMarker()
    {
        return mPreferences.getString(KEY_ERA_MARKER_SILVER, "");
    }

    public String getBronzeMarker()
    {
        return mPreferences.getString(KEY_ERA_MARKER_BRONZE, "");
    }

    public String getWildcardMarker()
    {
        return mPreferences.getString(KEY_ERA_MARKER_WILDCARD, "");
    }

    /*
    * ********************
    *
    *       DELETE
    *
    * ******************
    */
    public void DeleteUserGeneralInfo()
    {
        try
        {
            mEditor.remove(KEY_CONSUMER_COUNTRY_ID);
            mEditor.remove(KEY_CONSUMER_COUNTRY_PHONE_CODE);
            mEditor.remove(KEY_CONSUMER_COUNTRY_IS3CODE);
            mEditor.remove(KEY_CONSUMER_COUNTRY_NAME);
            mEditor.remove(KEY_CONSUMER_NICKNAME);
            mEditor.remove(KEY_CONSUMER_ID);
            mEditor.remove(KEY_FACEBOOK_PROFILE_ID);
            mEditor.remove(KEY_FACEBOOK_URL);
            mEditor.commit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void deleteNickname()
    {
        mEditor.remove(KEY_CONSUMER_NICKNAME);
        mEditor.commit();
    }


    public void hasSeenIntro(boolean seen)
    {
        mEditor.putBoolean(KEY_HAS_SEEN_INTRO, seen);
        mEditor.commit();
    }
}
