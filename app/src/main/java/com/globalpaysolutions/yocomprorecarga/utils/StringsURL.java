package com.globalpaysolutions.yocomprorecarga.utils;

/**
 * Created by Josué Chávez on 13/01/2017.
 */

public class StringsURL
{
    //PRE-PRODUCCIÓN
    public final static String URL_BASE = "http://csncusgats.cloudapp.net:8074/v1/";

    public final static String COUNTRIES = "countries";

    public final static String OPERATORS = "reqTopupOperators";

    public final static String TOKEN = "token";

    public final static String VALIDATE_TOKEN = "validateToken";

    public final static String REQUESTTOPUP = "requestTopup";

    public final static String REGISTER_CONSUMER = "registerConsumer";

    //FIREBASE
    public static final String URL_FIREBASE = "https://yocomprorecarga-development.firebaseio.com/";

    public static final String STATIC_POINTS = URL_FIREBASE + "staticPoints";
    public static final String DATA_STATIC_PONTS = URL_FIREBASE + "dataStaticPoints";

    //OTRAS URL'S
    public final static String YVR_STORE = "https://play.google.com/store/apps/details?id=com.globalpaysolutions.yovendorecarga";

}
