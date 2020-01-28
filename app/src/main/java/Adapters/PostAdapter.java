package Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalproject.Models.Post;
import com.example.finalproject.Post_detail;
import com.example.finalproject.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context mContext;
    List<Post> mData ;

    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_iteam,parent,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tvtitle.setText(mData.get(position).getTitle());
        Glide.with(mContext).load(mData.get(position).getPicture()).into(holder.imgPost);
        Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(holder.imgPostProfile);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvtitle;
        ImageView imgPost;
        ImageView imgPostProfile;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvtitle = itemView.findViewById(R.id.row_post_title);
            imgPost = itemView.findViewById(R.id.row_post_img);
            imgPostProfile = itemView.findViewById(R.id.row_post_profile_img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent Post_detail=new Intent(mContext, Post_detail.class);
                    int position=getAdapterPosition();
                    Post_detail.putExtra("title",mData.get(position).getTitle());
                    Post_detail.putExtra("postImage",mData.get(position).getPicture());
                    Post_detail.putExtra("description",mData.get(position).getDescription());
                    Post_detail.putExtra("postKey",mData.get(position).getPostKey());
                    Post_detail.putExtra("userPhoto",mData.get(position).getUserPhoto());


                    long timestamp=(long)mData.get(position).getTimeStamp();
                    Post_detail.putExtra("postDate",timestamp);
                    mContext.startActivity(Post_detail);



                }
            });
        }
    }
}
