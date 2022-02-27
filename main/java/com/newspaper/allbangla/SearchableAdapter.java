package com.newspaper.allbangla;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SearchableAdapter extends BaseAdapter implements Filterable {



    String TAG="hhhhhhhhhhhhh";
    private List<String> originalData1 = null;
    private List<String> filteredData1 = null;
    private List<String> originalData2 = null;
    private List<String> filteredData2 = null;
    private List<String> originalData3 = null;
    private List<String> filteredData3 = null;
    private List<String> viewsList = null;
    private LayoutInflater mInflater;
    SQLiteHandler sqLiteHandler;
    MainActivity mainActivity;
    private ItemFilter mFilter = new ItemFilter();
    private MediaPlayer playr;

    public SearchableAdapter(Context context, List<String> dataName, List<String> dataLink, List<String> dataImage, List<String> viewsList) {
        this.filteredData1 = dataName;
        this.originalData1 = dataName;
        this.filteredData2 = dataLink;
        this.originalData2 = dataLink;
        this.filteredData3 = dataImage;
        this.originalData3 = dataImage;
        this.viewsList = viewsList;
        mainActivity = (MainActivity) context;
        mInflater = LayoutInflater.from(context);
        sqLiteHandler= new SQLiteHandler(mainActivity);
    }

    public int getCount() {
        return filteredData1.size();
    }

    public Object getItem(int position) {
        return filteredData1.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unnecessary calls
        // to findViewById() on each row.
        final ViewHolder holder;

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.text = convertView.findViewById(R.id.list_text);
            holder.simpleProgressBar = convertView.findViewById(R.id.simpleProgressBar);
            holder.videoThumb = convertView.findViewById(R.id.videoThumb);
            holder.itemCarry = convertView.findViewById(R.id.itemCaryy);

            // Bind the data efficiently with the holder.

            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        // If weren't re-ordering this you could rely on what you set last time

            holder.text.setText(filteredData1.get(position));
            holder.simpleProgressBar.setVisibility(View.VISIBLE);
            RequestOptions options = new RequestOptions()
                    .centerInside()
                    .placeholder(R.drawable.actionlg)
                    .error(R.drawable.actionlg);
            Glide.with(mainActivity).load(filteredData3.get(position)).apply(options).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    holder.simpleProgressBar.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.simpleProgressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(holder.videoThumb);


            holder.itemCarry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoNextDestinstion(position, filteredData1.get(position));
                }
            });


        return convertView;
    }

    static class ViewHolder {
        TextView text;
        ImageView videoThumb;
        LinearLayout itemCarry;
        TextView viewCount;
        ProgressBar simpleProgressBar;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<String> list1 = originalData1;
            final List<String> list2 = originalData2;
            final List<String> list3 = originalData3;

            int count = list1.size();
            final ArrayList<String> nlist = new ArrayList<String>(count);

            String filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list1.get(i);
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                    nlist.add(list2.get(i));
                    nlist.add(list3.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            LinkedList<String> filteredDataHere = (LinkedList<String>) results.values;
            filteredData1.clear();
            filteredData2.clear();
            filteredData3.clear();
            for (int i = 0; i < filteredDataHere.size(); i += 3) {
                filteredData1.add(filteredDataHere.get(i));
                filteredData2.add(filteredDataHere.get(i + 1));
                filteredData3.add(filteredDataHere.get(i + 2));
            }
            notifyDataSetChanged();
        }

    }


    public void ShowShareChooser(String filename) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=" + filename);
        shareIntent.setType("text/plain");
        //shareIntent.setDataAndType(fileUri,type); // another failed attempt
        mainActivity.startActivity(Intent.createChooser(shareIntent, "Funny Video"));
    }

    private void gotoNextDestinstion(int posi, String name) {
        Bundle bundle = new Bundle();
        Intent intent= new Intent(mainActivity, ViewData.class);
        bundle.putString("linknews", filteredData2.get(posi));
        bundle.putString("namenews", filteredData1.get(posi));
        intent.putExtras(bundle);
        mainActivity.startActivity(intent);
//        getActi
    }



    private void updateArchieve(String name, String link, final String img_url, final String views) {
        // Tag used to cancel the request
        sqLiteHandler.addUser(name, link, img_url, views);

    }



}