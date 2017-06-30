package com.tonyodev.fetch2;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArraySet;

import com.tonyodev.fetch2.core.ExecutorRunnableProcessor;
import com.tonyodev.fetch2.core.FetchCore;
import com.tonyodev.fetch2.core.Fetchable;
import com.tonyodev.fetch2.download.DownloadListener;
import com.tonyodev.fetch2.listener.FetchListener;
import com.tonyodev.fetch2.util.Assert;
import com.tonyodev.fetch2.util.NetworkUtils;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import okhttp3.OkHttpClient;


public final class Fetch implements Fetchable {

    private static Fetch fetch;

    private final FetchCore fetchCore;
    private final Set<WeakReference<FetchListener>> listeners;
    private final ExecutorRunnableProcessor runnableProcessor;
    private final Handler handler;

    public static void init(@NonNull Context context) {
        init(context, NetworkUtils.okHttpClient());
    }

    public static void init(@NonNull Context context, @NonNull OkHttpClient client) {
        Assert.contextNotNull(context);
        Assert.clientIsNotNull(client);
        if (fetch != null) {
            throw new RuntimeException("init was already called.");
        }
        fetch = new Fetch(context.getApplicationContext(), client);
    }

    @NonNull
    public static Fetch getInstance() {
        if (fetch == null) {
            throw new RuntimeException("Fetch was not initialized.");
        }
        return fetch;
    }

    private Fetch(Context context, OkHttpClient okHttpClient) {
        this.listeners = new ArraySet<>();
        this.runnableProcessor = new ExecutorRunnableProcessor();
        this.handler = new Handler(Looper.getMainLooper());
        this.fetchCore = new FetchCore(context, okHttpClient, getDownloadListener());
    }

    @Override
    public void enqueue(final @NonNull Request request) {
        Assert.requestIsNotNull(request);
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.enqueue(request);
            }
        });
    }

    @Override
    public void enqueue(final @NonNull Request request, final @NonNull Callback callback) {
        Assert.requestIsNotNull(request);
        Assert.callbackIsNotNull(callback);
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.enqueue(request, new Callback() {
                    @Override
                    public void onQueued(final @NonNull Request request) {
                        postOnMain(new Runnable() {
                            @Override
                            public void run() {
                                callback.onQueued(request);
                            }
                        });
                    }
                    @Override
                    public void onFailure(final @NonNull Request request, final @NonNull Error error) {
                        postOnMain(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFailure(request, error);
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void enqueue(final @NonNull List<Request> requests) {
        Assert.requestListIsNotNull(requests);
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.enqueue(requests);
            }
        });
    }

    @Override
    public void enqueue(final @NonNull List<Request> requests, final @NonNull Callback callback) {
        Assert.requestListIsNotNull(requests);
        Assert.callbackIsNotNull(callback);
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.enqueue(requests, new Callback() {
                    @Override
                    public void onQueued(final @NonNull Request request) {
                        postOnMain(new Runnable() {
                            @Override
                            public void run() {
                                callback.onQueued(request);
                            }
                        });
                    }
                    @Override
                    public void onFailure(final @NonNull Request request, final @NonNull Error error) {
                        postOnMain(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFailure(request, error);
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void pause(final long id) {
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.pause(id);
            }
        });
    }

    @Override
    public void pauseGroup(final @NonNull String id) {
        Assert.groupIDIsNotNull(id);
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.pauseGroup(id);
            }
        });
    }

    @Override
    public void pauseAll() {
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.pauseAll();
            }
        });
    }

    @Override
    public void resume(final long id) {
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.resume(id);
            }
        });
    }

    @Override
    public void resumeGroup(final @NonNull String id) {
        Assert.groupIDIsNotNull(id);
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.resumeGroup(id);
            }
        });
    }

    @Override
    public void resumeAll() {
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.resumeAll();
            }
        });
    }

    @Override
    public void retry(final long id) {
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.retry(id);
            }
        });
    }

    @Override
    public void retryGroup(final @NonNull String id) {
        Assert.groupIDIsNotNull(id);
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.retryGroup(id);
            }
        });
    }

    @Override
    public void retryAll() {
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.retryAll();
            }
        });
    }

    @Override
    public void cancel(final long id) {
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.cancel(id);
            }
        });
    }

    @Override
    public void cancelGroup(final @NonNull String id) {
        Assert.groupIDIsNotNull(id);
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.cancelGroup(id);
            }
        });
    }

    @Override
    public void cancelAll() {
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.cancelAll();
            }
        });
    }

    @Override
    public void remove(final long id) {
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.remove(id);
            }
        });
    }

    @Override
    public void removeGroup(final @NonNull String id) {
        Assert.groupIDIsNotNull(id);
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.removeGroup(id);
            }
        });
    }

    @Override
    public void removeAll() {
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.removeAll();
            }
        });
    }

    @Override
    public void delete(final long id) {
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.delete(id);
            }
        });
    }

    @Override
    public void deleteGroup(final @NonNull String id) {
        Assert.groupIDIsNotNull(id);
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.deleteGroup(id);
            }
        });
    }

    @Override
    public void deleteAll() {
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.deleteAll();
            }
        });
    }

    @Override
    public void query(final long id, final @NonNull Query<RequestData> query) {
        Assert.queryIsNotNull(query);
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.query(id, new Query<RequestData>() {
                    @Override
                    public void onResult(final @Nullable RequestData result) {
                        postOnMain(new Runnable() {
                            @Override
                            public void run() {
                                query.onResult(result);
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void query(final @NonNull List<Long> ids, final @NonNull Query<List<RequestData>> query) {
        Assert.queryIsNotNull(query);
        Assert.idListIsNotNull(ids);
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.query(ids, new Query<List<RequestData>>() {
                    @Override
                    public void onResult(final @Nullable List<RequestData> result) {
                        postOnMain(new Runnable() {
                            @Override
                            public void run() {
                                query.onResult(result);
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void queryAll(final @NonNull Query<List<RequestData>> query) {
        Assert.queryIsNotNull(query);
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.queryAll(new Query<List<RequestData>>() {
                    @Override
                    public void onResult(final @Nullable List<RequestData> result) {
                        postOnMain(new Runnable() {
                            @Override
                            public void run() {
                                query.onResult(result);
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void queryByStatus(final @NonNull Status status, final @NonNull Query<List<RequestData>> query) {
        Assert.queryIsNotNull(query);
        Assert.statusIsNotNull(status);
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.queryByStatus(status, new Query<List<RequestData>>() {
                    @Override
                    public void onResult(final @Nullable List<RequestData> result) {
                        postOnMain(new Runnable() {
                            @Override
                            public void run() {
                                query.onResult(result);
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void queryByGroupId(final @NonNull String groupId, final @NonNull Query<List<RequestData>> query) {
        Assert.queryIsNotNull(query);
        Assert.groupIDIsNotNull(groupId);
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.queryByGroupId(groupId, new Query<List<RequestData>>() {
                    @Override
                    public void onResult(final @Nullable List<RequestData> result) {
                        postOnMain(new Runnable() {
                            @Override
                            public void run() {
                                query.onResult(result);
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void queryGroupByStatusId(final @NonNull String groupId, final @NonNull Status status, final @NonNull Query<List<RequestData>> query) {
        Assert.groupIDIsNotNull(groupId);
        Assert.statusIsNotNull(status);
        Assert.queryIsNotNull(query);
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.queryGroupByStatusId(groupId, status, new Query<List<RequestData>>() {
                    @Override
                    public void onResult(final @Nullable List<RequestData> result) {
                        postOnMain(new Runnable() {
                            @Override
                            public void run() {
                                query.onResult(result);
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void contains(final long id, final @NonNull Query<Boolean> query) {
        Assert.queryIsNotNull(query);
        runnableProcessor.queue(new Runnable() {
            @Override
            public void run() {
                fetchCore.contains(id, new Query<Boolean>() {
                    @Override
                    public void onResult(final @Nullable Boolean result) {
                        postOnMain(new Runnable() {
                            @Override
                            public void run() {
                                query.onResult(result);
                            }
                        });
                    }
                });
            }
        });
    }

    public synchronized void addListener(@NonNull FetchListener fetchListener) {
        if(fetchListener != null && !containsListener(fetchListener)) {
            fetchListener.onAttach(this);
            listeners.add(new WeakReference<>(fetchListener));
        }
    }

    private boolean containsListener(FetchListener fetchListener) {
        Iterator<WeakReference<FetchListener>> iterator = listeners.iterator();
        WeakReference<FetchListener> ref;

        while (iterator.hasNext()) {
            ref = iterator.next();

            if (ref.get() != null && ref.get() == fetchListener){
                return true;
            }
        }
        return false;
    }

    public synchronized void removeListener(@NonNull FetchListener fetchListener) {
        if (fetchListener != null) {
            Iterator<WeakReference<FetchListener>> iterator = listeners.iterator();
            WeakReference<FetchListener> ref;

            while (iterator.hasNext()) {
                ref = iterator.next();

                if (ref.get() != null && ref.get() == fetchListener){
                    iterator.remove();
                    fetchListener.onDetach(this);
                    break;
                }
            }
        }
    }

    public synchronized void removeListeners() {
        Iterator<WeakReference<FetchListener>> iterator = listeners.iterator();
        WeakReference<FetchListener> ref;

        while(iterator.hasNext()) {
            ref = iterator.next();
            iterator.remove();

            if (ref.get() != null) {
                ref.get().onDetach(this);
            }
        }
    }

    private DownloadListener getDownloadListener() {
        return new DownloadListener() {
            @Override
            public void onComplete(final long id,final int progress,final long downloadedBytes,final long totalBytes) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (WeakReference<FetchListener> ref : listeners) {
                            if (ref.get() != null) {
                                ref.get().onComplete(id, progress, downloadedBytes, totalBytes);
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(final long id,@NonNull final  Error error,final int progress,final long downloadedBytes,final long totalBytes) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (WeakReference<FetchListener> ref : listeners) {
                            if(ref.get() != null) {
                                ref.get().onError(id, error, progress, downloadedBytes, totalBytes);
                            }
                        }
                    }
                });
            }

            @Override
            public void onProgress(final long id,final int progress,final long downloadedBytes,final long totalBytes) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (WeakReference<FetchListener> ref : listeners) {
                            if(ref.get() != null) {
                                ref.get().onProgress(id, progress, downloadedBytes, totalBytes);
                            }
                        }
                    }
                });
            }

            @Override
            public void onPaused(final long id, final int progress, final long downloadedBytes, final long totalBytes) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (WeakReference<FetchListener> ref : listeners) {
                            if(ref.get() != null) {
                                ref.get().onPaused(id, progress, downloadedBytes, totalBytes);
                            }
                        }
                    }
                });
            }

            @Override
            public void onCancelled(final long id, final int progress, final long downloadedBytes, final long totalBytes) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (WeakReference<FetchListener> ref : listeners) {
                            if(ref.get() != null) {
                                ref.get().onCancelled(id, progress, downloadedBytes, totalBytes);
                            }
                        }
                    }
                });
            }

            @Override
            public void onRemoved(final long id, final int progress, final long downloadedBytes, final long totalBytes) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (WeakReference<FetchListener> ref : listeners) {
                            if(ref.get() != null) {
                                ref.get().onRemoved(id, progress, downloadedBytes, totalBytes);
                            }
                        }
                    }
                });
            }
        };
    }

    private void postOnMain(Runnable runnable) {
        handler.post(runnable);
    }
}