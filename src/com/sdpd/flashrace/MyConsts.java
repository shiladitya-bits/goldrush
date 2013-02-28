package com.sdpd.flashrace;

public class MyConsts {
    public static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 5; // in Meters
    public static final long MINIMUM_TIME_BETWEEN_UPDATE = 1000; // in Milliseconds
    
    public static final long POINT_RADIUS = 10; // in Meters
    public static final long PROX_ALERT_EXPIRATION = -1; 

    public static final String POINT_LATITUDE_KEY = "POINT_LATITUDE_KEY";
    public static final String POINT_LONGITUDE_KEY = "POINT_LONGITUDE_KEY";
    
    public static final String PROX_ALERT_INTENT = 
         "com.sdpd.flashrace.ProximityAlerter";

	public static final int NOTIFICATION_ID = 1000;

    public static final String NETWORK_FOLDER = "http://goldrush.comze.com/";
    //public static final String NETWORK_FOLDER = "http://169.254.39.242/flashrace/";
	//public static final String NETWORK_FOLDER = "http://192.168.42.127/flashrace/";
    
    public static final String APP_NAME = "Goldrush";
    public static final String FACEBOOK_APP_ID = "103177931644";
    public static final String FACEBOOK_APP_SECRET = "c682ce45b4c52dcc94cddeb4c0dd6e41"; 
}