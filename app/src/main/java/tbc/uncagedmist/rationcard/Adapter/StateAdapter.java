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
import tbc.uncagedmist.rationcard.ItemActivity;
import tbc.uncagedmist.rationcard.Model.State;
import tbc.uncagedmist.rationcard.R;

public class StateAdapter extends RecyclerView.Adapter<StateAdapter.StateViewHolder> {

    Context context;
    List<State> stateList;

    public StateAdapter(Context context, List<State> stateList) {
        this.context = context;
        this.stateList = stateList;
    }

    @NonNull
    @Override
    public StateAdapter.StateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_states,parent,false);

        if (MyApplicationClass.getInstance().isShowAds())   {
            GoogleAds.loadGoogleFullscreen(context);
        }

        return new StateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StateAdapter.StateViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.stateName.setText(stateList.get(position).name);
        holder.stateDesc.setText(stateList.get(position).description);

        Picasso.get()
                .load(stateList.get(position).image)
                .error(R.mipmap.ic_logo)
                .into(holder.stateImage, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get()
                                .load(R.mipmap.ic_logo)
                                .into(holder.stateImage);
                    }
                });

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view) {
                if (GoogleAds.mInterstitialAd != null)  {
                    GoogleAds.mInterstitialAd.show((Activity) context);
                }
                else {

                    Intent intent = new Intent(context, ItemActivity.class);

                    Common.CURRENT_STATE = stateList.get(position);

                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return stateList.size();
    }

    public static class StateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView stateImage;
        TextView stateName, stateDesc;

        ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public StateViewHolder(@NonNull View itemView) {
            super(itemView);

            stateImage = itemView.findViewById(R.id.stateImage);
            stateName = itemView.findViewById(R.id.stateName);
            stateDesc = itemView.findViewById(R.id.stateDesc);

            stateName.setSelected(true);
            stateDesc.setSelected(true);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClick(view);
        }
    }
}
