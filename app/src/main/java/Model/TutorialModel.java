package Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 13/9/17.
 */

public class TutorialModel implements Parcelable {

    public static final Creator<TutorialModel> CREATOR = new Creator<TutorialModel>() {
        @Override
        public TutorialModel createFromParcel(Parcel in) {
            return new TutorialModel(in);
        }

        @Override
        public TutorialModel[] newArray(int size) {
            return new TutorialModel[size];
        }
    };

    private int background;
    private int title_icon;
    private String title = null;
    private String description = null;
    private String button_title = null;
    private int button_color;
    private int is_shownext = 0;  //show 1 hide 0

    public TutorialModel(int background, int title_icon, String title, String description, String button_title, int button_color, int is_shownext) {
        this.background = background;
        this.title_icon = title_icon;
        this.title = title;
        this.description = description;
        this.button_title = button_title;
        this.button_color = button_color;
        this.is_shownext = is_shownext;
    }

    protected TutorialModel(Parcel in) {
        background = in.readInt();
        title_icon = in.readInt();
        title = in.readString();
        description = in.readString();
        button_title = in.readString();
        button_color = in.readInt();
        is_shownext = in.readInt();
    }

    public int is_shownext() {
        return is_shownext;
    }

    public void setIs_shownext(int is_shownext) {
        this.is_shownext = is_shownext;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public int getTitle_icon() {
        return title_icon;
    }

    public void setTitle_icon(int title_icon) {
        this.title_icon = title_icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getButton_title() {
        return button_title;
    }

    public void setButton_title(String button_title) {
        this.button_title = button_title;
    }

    public int getButton_color() {
        return button_color;
    }

    public void setButton_color(int button_color) {
        this.button_color = button_color;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(background);
        parcel.writeInt(title_icon);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(button_title);
        parcel.writeInt(button_color);
        parcel.writeInt(is_shownext);
    }
}
