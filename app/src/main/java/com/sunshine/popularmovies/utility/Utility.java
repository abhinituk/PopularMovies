package com.sunshine.popularmovies.utility;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class Utility {
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
//    public static String downloadImagesToIntenalStorage(final Context context, String posterPath, final int movieId)
//    {
//        Target target = new Target() {
//            @Override
//            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                try {
//                    String root = context.getFilesDir().toString();
//                    File myDir = new File(root + "/images");
//                    if (!myDir.exists())
//                        myDir.mkdirs();
//
//                    Log.v("Created", String.valueOf(myDir.exists()));
//
//                    String name = movieId + ".jpg";
//                    myDir = new File(myDir, name);
//                    FileOutputStream outputStream = new FileOutputStream(myDir);
//
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//                    outputStream.flush();
//                    outputStream.close();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onBitmapFailed(Drawable errorDrawable) {
//
//            }
//
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//            }
//        };
//        Picasso.with(context)
//                .load(posterPath)
//                .into(target);
//        Log.v("Target", String.valueOf(target));
//
//        return context.getFilesDir().toString() +"/images/"+movieId+".jpg";
//    }
}