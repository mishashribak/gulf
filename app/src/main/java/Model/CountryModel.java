package Model;

/**
 * Created by user on 13/10/17.
 */

public class CountryModel {
    String code;
    String country_name;
    int country_flag;
    boolean is_checked;
    public CountryModel(){

    }
    public CountryModel(String code, String country_name, int country_flag) {
        this.code = code;
        this.country_name = country_name;
        this.country_flag = country_flag;
    }

    public boolean is_checked() {
        return is_checked;
    }

    public void setIs_checked(boolean is_checked) {
        this.is_checked = is_checked;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public int getCountry_flag() {
        return country_flag;
    }

    public void setCountry_flag(int country_flag) {
        this.country_flag = country_flag;
    }
}
