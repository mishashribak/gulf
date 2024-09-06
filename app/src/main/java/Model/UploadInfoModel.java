package Model;

import android.os.PersistableBundle;

public class UploadInfoModel {
   public String uploadId;
   public UploadType uploadType;
   public int currentProgress;
   public boolean isUploadFail;
   public PersistableBundle bundle;
   public boolean isVideo;

   public enum UploadType {
      DAILY,
      MEMORY,
      BOTH
   }

   public UploadInfoModel(){

   }

   public UploadInfoModel(String id, UploadType type){
      uploadId = id;
      uploadType = type;
   }

}
