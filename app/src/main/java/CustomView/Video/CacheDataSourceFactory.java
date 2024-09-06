package CustomView.Video;

import android.content.Context;

import com.app.khaleeji.R;
import com.google.android.exoplayer2.database.DatabaseProvider;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;

import java.io.File;

public class CacheDataSourceFactory implements DataSource.Factory {
    private final Context context;
    private final DefaultDataSourceFactory defaultDatasourceFactory;
    private final long maxFileSize, maxCacheSize;
    private SimpleCache simpleCache;
    private int mediaId;

    public CacheDataSourceFactory(Context context, long maxCacheSize, long maxFileSize, int mediaId) {
        super();
        this.context = context;
        this.mediaId = mediaId;
        this.maxCacheSize = maxCacheSize;
        this.maxFileSize = maxFileSize;
        String userAgent = Util.getUserAgent(context, context.getString(R.string.app_name));
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        defaultDatasourceFactory = new DefaultDataSourceFactory(this.context,
                bandwidthMeter,
                new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter));
    }


    @Override
    public DataSource createDataSource() {
//        LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(maxCacheSize);
//        DatabaseProvider databaseProvider = new ExoDatabaseProvider(context);
//        File file = new File(context.getCacheDir(), String.valueOf(mediaId));
//        simpleCache = new SimpleCache(file, evictor, databaseProvider);
//        return new CacheDataSource(simpleCache, defaultDatasourceFactory.createDataSource(),
//                new FileDataSource(), new CacheDataSink(simpleCache, maxFileSize),
//                CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, null);

        return new CacheDataSource(VideoCache.getInstance(context).getSimpleCache(), defaultDatasourceFactory.createDataSource(),
                new FileDataSource(), new CacheDataSink(VideoCache.getInstance(context).getSimpleCache(), maxFileSize),
                CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, null);
    }
}
