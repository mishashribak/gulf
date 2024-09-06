package CustomView.Video;

import android.content.Context;
import android.util.Log;

import com.google.android.exoplayer2.database.DatabaseProvider;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;

public class VideoCache {
    private SimpleCache simpleCache;
    private static VideoCache videoCache;

    public static VideoCache getInstance(Context context) {
        if(videoCache == null){
            Log.e("VideoCache", "simpleCache");
            videoCache = new VideoCache(context);
            return videoCache;
        }
        return videoCache;
    }

    public VideoCache(Context context){
        LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024);
        DatabaseProvider databaseProvider = new ExoDatabaseProvider(context);
        File file = new File(context.getCacheDir(), "videocache");
        simpleCache = new SimpleCache(file, evictor, databaseProvider);
    }

    public SimpleCache getSimpleCache(){
        return simpleCache;
    }
}
