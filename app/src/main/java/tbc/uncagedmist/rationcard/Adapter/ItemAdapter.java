package tbc.uncagedmist.rationcard.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import tbc.uncagedmist.rationcard.Ads.GoogleAds;
import tbc.uncagedmist.rationcard.Common.Common;
import tbc.uncagedmist.rationcard.Common.MyApplicationClass;
import tbc.uncagedmist.rationcard.Interface.ItemClickListener;
import tbc.uncagedmist.rationcard.Model.Item;
import tbc.uncagedmist.rationcard.R;
import tbc.uncagedmist.rationcard.ResultActivity;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.DetailViewHolder> {

    Context context;
    List<Item> itemList;

    public ItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemAdapter.DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_items,parent,false);

        if (MyApplicationClass.getInstance().isShowAds())   {
            GoogleAds.loadGoogleFullscreen(context);
        }

        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.DetailViewHolder holder, @SuppressLint("RecyclerView") final int position) {


        Picasso.get()
                .load(Common.CURRENT_STATE.image)
                .error(R.mipmap.ic_logo)
                .into(holder.detailImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.detailName.setText(itemList.get(position).name);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });


        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view) {
                if (GoogleAds.mInterstitialAd != null)  {
                    GoogleAds.mInterstitialAd.show((Activity) context);
                }
                else {

                    Intent intent = new Intent(context, ResultActivity.class);
                    Common.CURRENT_ITEM = itemList.get(position);
                    context.startActivity(intent);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public static class DetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView detailImage;
        TextView detailName;

        ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);

            detailImage = itemView.findViewById(R.id.productImage);
            detailName = itemView.findViewById(R.id.productName);

            detailName.setSelected(true);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClick(view);
        }
    }
}
