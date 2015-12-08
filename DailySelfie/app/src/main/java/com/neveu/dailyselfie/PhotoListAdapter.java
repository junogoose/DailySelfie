package com.neveu.dailyselfie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 *   Created by neveu on 7/14/2015.
 *   Maintains the list of PhotoItems. Generates the individual list item views.
 */
public class PhotoListAdapter extends BaseAdapter {

    private ArrayList<PhotoItem> photoItems;
    private Context mContext;
    LayoutInflater inflater;

    public PhotoListAdapter(Context context)
    {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        photoItems = new ArrayList<>();
    }

    public void add(PhotoItem item)
    {
        photoItems.add(item);
        notifyDataSetChanged();
    }

    public void removeAtIndex(int i)
    {
        photoItems.remove(i);
        notifyDataSetChanged();
    }

    public void removeAll()
    {
        photoItems.removeAll(photoItems);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return photoItems.size();
    }

    @Override
    public PhotoItem getItem(int position) {
        return photoItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View newView = convertView;
        ViewHolder holder;

        PhotoItem curr = photoItems.get(position);

        if (null == convertView) {
            holder = new ViewHolder();
            newView = inflater
                    .inflate(R.layout.photo_item, parent, false);
            holder.thumb = (ImageView) newView.findViewById(R.id.photo_thumbnail);
            holder.name = (TextView) newView.findViewById(R.id.photo_name);
            newView.setTag(holder);

        } else {
            holder = (ViewHolder) newView.getTag();
        }

        holder.thumb.setImageBitmap(curr.getThumbnail());
        holder.name.setText(curr.getFileName());

        return newView;
    }

    static class ViewHolder {

        ImageView thumb;
        TextView name;

    }
}
