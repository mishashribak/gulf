package Model;

public class LocalContactModel {
    private String name;
    private String phoneNumber;
    private String id;

    public LocalContactModel(String id, String name, String phoneNumber){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
