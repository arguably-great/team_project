package com.example.fbuteamproject.utils;

//public class QueryListener implements Api.QueryListener {
//
//
//        private static final String TAG = "PhotoListener";
//        Context context;
//        public static int loadPhotoCount;
//        private final Set<PhotoViewer> photoViewers = new HashSet<>();
//        private List<com.example.fbuteamproject.utils.FlickrApi.Photo> currentPhotos = new ArrayList<>();
//        private final QueryListener queryListener = new QueryListener(context);
//
//
//        QueryListener(Context context) {
//            this.context = context;
//
//        }
//
//        @Override
//        public void onSearchCompleted(Query query, List<Photo> photos) {
//
//            Log.d(TAG, "GOT QUERY");
//
//            if (!isCurrentQuery(query)) {
//                return;
//            }
//
//            if (Log.isLoggable(TAG, Log.DEBUG)) {
//                Log.d(TAG, "Search completed, got " + photos.size() + " results");
//            }
//
//            for (PhotoViewer viewer : photoViewers) {
//                viewer.onPhotosUpdated(photos);
//            }
//            currentPhotos = photos;
//
//            if (currentPhotos.size() > 6) {
//
//                for (int i = 0; i < 6; i++) {
//
//                    Log.d(TAG, "on SearchCompleted: "+i);
//
//                    CompletableFuture<ViewRenderable> photoStage;
//                    ImageView iv = new ImageView(context);
//
//                    Glide.with(context).load(currentPhotos.get(i)).apply(new RequestOptions()
//                            .placeholder(R.mipmap.ic_launcher)
//                            .fitCenter()
//                            .override(1000, 1000)).into(iv);
//
//                    photoStage = ViewRenderable.builder().setView(context, iv).build();
//
//                    loadPhotoCount++;
//
//                    Log.d(TAG, "Current photo count is " + loadPhotoCount);
//
//                    photoStage.thenApply(viewRenderable -> {
//
//                        Log.d(TAG, "OUR ENTITY IS " + ARActivity.currEntitySelected.getEntity());
//
//                        PhotoComponent.buildViewRenderable(photoStage, context, ARActivity.currEntitySelected.getEntity());
//
//                        loadPhotoCount--;
//                        Log.d(TAG, "Current photo count is " + loadPhotoCount);
//
//                        if(loadPhotoCount == 0) {
//
//                            ArrayList<ViewRenderable> myViews = ARActivity.currEntitySelected.getEntity().getEntityPhotos();
//
//                            Log.d(TAG, "ENTITY PHOTOS ARE " + myViews);
//
//                            PhotoComponent.listener.startPhotoNodeCreation(myViews);
//                        }
//
//                        return null;
//                    });
//
//                }
//            }
//
//        }
//
//        private boolean isCurrentQuery(Query query) {
//            return PhotoComponent.currentQuery != null && PhotoComponent.currentQuery.equals(query);
//        }
//
//        @SuppressLint("StringFormatInvalid")
//        @Override
//        public void onSearchFailed(Query query, Exception e) {
//            if (!isCurrentQuery(query)) {
//                return;
//            }
//
//            if (Log.isLoggable(TAG, Log.ERROR)) {
//                Log.e(TAG, "Search failed", e);
//            }
//
//        }
//}
