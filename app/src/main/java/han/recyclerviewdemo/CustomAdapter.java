package han.recyclerviewdemo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Han on 11/20/15.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ListItemViewHolder> implements View.OnClickListener{
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private List<InfoCollection> list;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    DisplayImageOptions options;

    CustomAdapter(List<InfoCollection> modellist) {
        if (modellist == null) {
            throw new IllegalArgumentException("modelData must not be null");
        }
        this.list = modellist;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ListItemViewHolder vh = new ListItemViewHolder(itemView);
        itemView.setOnClickListener(this);
        return vh;
    }


    @Override
    public void onBindViewHolder(ListItemViewHolder holder, int position) {
        options = new DisplayImageOptions.Builder().cacheInMemory(true).showImageForEmptyUri(android.R.drawable.ic_menu_report_image).cacheOnDisk(true).build();
        InfoCollection info = list.get(position);
        holder.list_name.setText(info.getName());
        holder.list_comment.setText(info.getNumber_comment() + " reviews");
        holder.list_desc.setText(info.getCategory());
        holder.list_distance.setText(info.getDistance() + " miles");
        holder.list_rating.setRating(info.getRating());
        imageLoader.displayImage(info.getImage_url(), holder.list_image, options);
        holder.itemView.setTag(list.get(position));

    }

    @Override
    public void onClick(View v) {
        if(mOnItemClickListener!=null){
            mOnItemClickListener.OnItemClick(v,(InfoCollection)v.getTag());
        }
    }
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener){
        this.mOnItemClickListener=listener;
    }
    @Override
    public int getItemCount() {
        return list.size();
    }


    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        TextView list_name;
        TextView list_distance;
        TextView list_comment;
        RatingBar list_rating;
        TextView list_desc;
        ImageView list_image;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            list_name = (TextView) itemView.findViewById(R.id.detail_name_list);
            list_distance = (TextView) itemView.findViewById(R.id.detail_distance_list);
            list_comment = (TextView) itemView.findViewById(R.id.detail_comment_list);
            list_rating = (RatingBar) itemView.findViewById(R.id.ratingBar_list);
            list_desc = (TextView) itemView.findViewById(R.id.detail_description_list);
            list_image = (ImageView) itemView.findViewById(R.id.imageView_list);
        }
    }

    public static interface  OnRecyclerViewItemClickListener{
        void OnItemClick(View view, InfoCollection data);
    }


}
