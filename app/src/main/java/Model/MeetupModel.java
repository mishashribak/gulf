package Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 15/9/17.
 */

public class MeetupModel implements Parcelable {

    public static final Creator<MeetupModel> CREATOR = new Creator<MeetupModel>() {
        @Override
        public MeetupModel createFromParcel(Parcel in) {
            return new MeetupModel(in);
        }

        @Override
        public MeetupModel[] newArray(int size) {
            return new MeetupModel[size];
        }
    };
    private String username;
    private String discription;
    private String image_url;
    private String distance;
    private int background_color;
    private double latitude;
    private double longitude;

    public MeetupModel() {
        super();
    }

    public MeetupModel(Parcel in) {
        username = in.readString();
        discription = in.readString();
        image_url = in.readString();
        distance = in.readString();
        background_color = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getBackground_color() {
        return background_color;
    }

    public void setBackground_color(int background_color) {
        this.background_color = background_color;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeString(discription);
        parcel.writeString(image_url);
        parcel.writeString(distance);
        parcel.writeInt(background_color);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }
}
