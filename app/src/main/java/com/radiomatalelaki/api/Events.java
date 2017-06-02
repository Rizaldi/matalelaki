package com.radiomatalelaki.api;

/**
 * Created by rianpradana on 5/26/17.
 */

import org.json.*;


public class Events implements Comparable {

    private String id;
    private String description;
    private String phone;
    private String brief_description;
    private String url;
    private String title;
    private String start_date_time;
    private String address;
    private String end_date_time;
    private double lat;
    private double lng;
    private String email;
    private double distance;


    /**
     * Instantiates a new Event.
     */
    public Events () {
        setId("0");
        setAddress("");
        setBrief_description("");
        setDescription("");
        setEmail("");
        setEnd_date_time("");
        setLat(0.0);
        setLng(0.0);
        setPhone("");
        setStart_date_time("0000-00-00 00:00");
        setEnd_date_time("0000-00-00 00:00");
        setTitle("");
        setUrl("");
    }

    /**
     * Instantiates a new Event from JSON object.
     *
     * @param json The JSON event representation.
     */
    public Events (JSONObject json) {

        this.id = json.optString("id");
        this.description = json.optString("description");
        this.phone = json.optString("phone");
        this.brief_description = json.optString("brief_description");
        this.url = json.optString("url");
        this.title = json.optString("title");
        this.start_date_time = json.optString("start_date_time");
        this.address = json.optString("address");
        this.end_date_time = json.optString("end_date_time");
        this.lat = json.optDouble("lat");
        this.lng = json.optDouble("lng");
        this.email = json.optString("email");

    }

    /**
     * Gets distance.
     *
     * @return the distance
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Sets distance.
     *
     * @param distance the distance
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title.
     *
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets address.
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets address.
     *
     * @param address the address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets brief description.
     *
     * @return the brief description
     */
    public String getBrief_description() {
        return brief_description;
    }

    /**
     * Sets brief description.
     *
     * @param brief_description the brief description
     */
    public void setBrief_description(String brief_description) {
        this.brief_description = brief_description;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets lat.
     *
     * @return the lat
     */
    public double getLat() {
        return lat;
    }

    /**
     * Sets lat.
     *
     * @param lat the lat
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * Gets lng.
     *
     * @return the lng
     */
    public double getLng() {
        return lng;
    }

    /**
     * Sets lng.
     *
     * @param lng the lng
     */
    public void setLng(double lng) {
        this.lng = lng;
    }

    /**
     * Gets phone.
     *
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets phone.
     *
     * @param phone the phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets url.
     *
     * @param url the url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets start date.
     *
     * @return the start date
     */
    public String getStart_date_time() {
        return start_date_time;
    }

    /**
     * Sets start date.
     *
     * @param start_date_time the start date
     */
    public void setStart_date_time(String start_date_time) {
        this.start_date_time = start_date_time;
    }

    /**
     * Gets end date.
     *
     * @return the end date
     */
    public String getEnd_date_time() {
        return end_date_time;
    }

    /**
     * Sets end date.
     *
     * @param end_date_time the end date
     */
    public void setEnd_date_time(String end_date_time) {
        this.end_date_time = end_date_time;
    }

    /* Compare by distance */
    @Override
    public int compareTo(Object o) {
        Events ev = (Events)o;
        if (ev.distance < distance)
            return 1;
        else if (ev.distance > distance)
            return -1;
        else
            return 0;
    }



}
