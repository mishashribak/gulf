package Model;

import java.util.ArrayList;
import java.util.List;

public class GroupMembers {

   private List<UserData> user = new ArrayList<>();

   public List<UserData> getUser() {
      return user;
   }

   public void setUser(List<UserData> user) {
      this.user = user;
   }
}
