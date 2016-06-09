package com.softjourn.ubm.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by Inet on 08.02.2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Need implements Parcelable{

    private int id;
    private int internat_id;
    private String name;
    private long date;
    private String text;
    private String image;
    private int total;

    public Need() {
    }

    protected Need(Parcel in) {
        id = in.readInt();
        internat_id = in.readInt();
        name = in.readString();
        date = in.readLong();
        text = in.readString();
        image = in.readString();
        total = in.readInt();
    }

    public static final Creator<Need> CREATOR = new Creator<Need>() {
        @Override
        public Need createFromParcel(Parcel in) {
            return new Need(in);
        }

        @Override
        public Need[] newArray(int size) {
            return new Need[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInternat_id() {
        return internat_id;
    }

    public void setInternat_id(int internat_id) {
        this.internat_id = internat_id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(internat_id);
        dest.writeString(name);
        dest.writeLong(date);
        dest.writeString(text);
        dest.writeString(image);
        dest.writeInt(total);
    }
}
