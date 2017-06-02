package com.radiomatalelaki.util;

/**
 * Created by rianpradana on 5/26/17.
 */

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import android.graphics.PointF;

/**
 * The type Utils. This class contains useful methods to do something...
 */
public class Utils {

    /**
     * Copy stream.
     *
     * @param is the input stream
     * @param os the output stream
     */
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

    /**
     * Calculate the derived position.
     *
     * @param point   the point
     * @param range   the range
     * @param bearing the bearing
     * @return the point
     */
    public static PointF calculateDerivedPosition(PointF point,
                                                  double range, double bearing) {
        double EarthRadius = 6371000; // m

        double latA = Math.toRadians(point.x);
        double lonA = Math.toRadians(point.y);
        double angularDistance = range / EarthRadius;
        double trueCourse = Math.toRadians(bearing);

        double lat = Math.asin(
                Math.sin(latA) * Math.cos(angularDistance) +
                        Math.cos(latA) * Math.sin(angularDistance)
                                * Math.cos(trueCourse));

        double dlon = Math.atan2(
                Math.sin(trueCourse) * Math.sin(angularDistance)
                        * Math.cos(latA),
                Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat));

        double lon = ((lonA + dlon + Math.PI) % (Math.PI * 2)) - Math.PI;

        lat = Math.toDegrees(lat);
        lon = Math.toDegrees(lon);

        PointF newPoint = new PointF((float) lat, (float) lon);

        return newPoint;
    }

    /**
     * Point is in circle boolean.
     *
     * @param pointForCheck the point for check
     * @param center        the center
     * @param radius        the radius
     * @return the boolean
     */
    public static boolean pointIsInCircle(PointF pointForCheck, PointF center,
                                          double radius) {
        if (getDistanceBetweenTwoPoints(pointForCheck, center) <= radius)
            return true;
        else
            return false;
    }

    /**
     * Gets distance between two points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the distance between two points
     */
    public static double getDistanceBetweenTwoPoints(PointF p1, PointF p2) {
        double R = 6371000; // m
        double dLat = Math.toRadians(p2.x - p1.x);
        double dLon = Math.toRadians(p2.y - p1.y);
        double lat1 = Math.toRadians(p1.x);
        double lat2 = Math.toRadians(p2.x);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2)
                * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;

        return d;
    }

    /**
     * Html to plain string.
     *
     * @param html the html
     * @return the string
     */
    public static String htmlToPlain (String html) {
        String s = html;
        Hashtable<String,String> html_specialchars_table = new Hashtable<String,String>();
        html_specialchars_table.put("&#039;","'");
        html_specialchars_table.put("&Atilde;&uml;","�");
        html_specialchars_table.put("&Atilde;&nbsp;","�");
        html_specialchars_table.put("&Atilde;&not;","�");
        html_specialchars_table.put("&amp;","&");
        html_specialchars_table.put("&Acirc;&deg;","�");

        Enumeration<String> en = html_specialchars_table.keys();
        while(en.hasMoreElements()){
            String key = (String)en.nextElement();
            String val = (String)html_specialchars_table.get(key);
            s = s.replaceAll(key, val);
        }

        return s;
    }
}