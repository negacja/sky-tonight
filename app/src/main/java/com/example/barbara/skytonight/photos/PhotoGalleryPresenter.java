package com.example.barbara.skytonight.photos;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PhotoGalleryPresenter implements PhotoGalleryContract.Presenter {

    private final PhotoGalleryContract.View mPhotoGalleryView;

    public PhotoGalleryPresenter(PhotoGalleryContract.View mPhotoGalleryView) {
        this.mPhotoGalleryView = mPhotoGalleryView;
    }

    @Override
    public void start() {
        mPhotoGalleryView.clearListInView();
        readPhotosAsync();
    }

    @Override
    public void dispatchTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mPhotoGalleryView.getViewActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(mPhotoGalleryView.getViewActivity());
            } catch (IOException ex) {
                Log.e("PhotoGallery", "IOException");
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(mPhotoGalleryView.getContext(), "com.example.barbara.skytonight.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                mPhotoGalleryView.startPhotoActivity(takePictureIntent);
            }
        }
    }

    private void readPhotosAsync() {
        File storageDir = mPhotoGalleryView.getViewActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Calendar selectedDate = mPhotoGalleryView.getSelectedDate();
        final String timeStamp = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(selectedDate.getTime());
        if (storageDir != null) {
            File[] filteredFiles = storageDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) { return name.contains(timeStamp); }
            });
            for (File file : filteredFiles)
                new DisplaySingleImageTask(file, mPhotoGalleryView.getPhotoList(), mPhotoGalleryView).execute(file);
        }
    }

    private File createImageFile(Activity activity) throws IOException {
        Calendar selectedDate = mPhotoGalleryView.getSelectedDate();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(selectedDate.getTime());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private static class DisplaySingleImageTask extends AsyncTask<File, Void, Boolean> {
        PhotoGalleryContract.View view;
        ArrayList<ImageFile> list;
        File file;

        DisplaySingleImageTask(File file, ArrayList<ImageFile> list, PhotoGalleryContract.View view){
            this.file = file;
            this.list = list;
            this.view = view;
        }

        @Override
        protected Boolean doInBackground(File... params) {
            return readFiles(params[0], list);
        }

        private Boolean readFiles(File file, ArrayList<ImageFile> list){
            boolean shouldRefresh = false;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (bitmap != null) {
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 4, bitmap.getHeight() / 4, false);
                list.add(new ImageFile(scaled, file));
                shouldRefresh = true;
            }
            return shouldRefresh;
        }

        @Override
        protected void onPostExecute(Boolean shouldRefresh) {
            if (shouldRefresh)
                view.refreshListInView();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

}
