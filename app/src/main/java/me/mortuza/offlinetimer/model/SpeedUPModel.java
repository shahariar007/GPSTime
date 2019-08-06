package me.mortuza.offlinetimer.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "speed")
public class SpeedUPModel implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @Expose(serialize = false, deserialize = true)
    private int uid;

    @SerializedName("TopSpeed")
    @Expose
    @ColumnInfo(name = "TopSpeed")
    private int topSpeed;

    @SerializedName("Lat")
    @Expose
    @ColumnInfo(name = "Lat")
    private float lat;
    @SerializedName("Lon")
    @Expose
    @ColumnInfo(name = "Lon")
    private float lon;

    @SerializedName("address")
    @Expose
    @ColumnInfo(name = "address")
    private String address;

    @SerializedName("DateTimes")
    @Expose
    @ColumnInfo(name = "DateTimes")
    private String dateTimes;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getTopSpeed() {
        return topSpeed;
    }

    public void setTopSpeed(int topSpeed) {
        this.topSpeed = topSpeed;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateTimes() {
        return dateTimes;
    }

    public void setDateTimes(String dateTimes) {
        this.dateTimes = dateTimes;
    }

    @Override
    public String toString() {
        return "SpeedUPModel{" +
                "uid=" + uid +
                ", topSpeed=" + topSpeed +
                ", lat=" + lat +
                ", lon=" + lon +
                ", address='" + address + '\'' +
                ", dateTimes='" + dateTimes + '\'' +
                '}';
    }
}
