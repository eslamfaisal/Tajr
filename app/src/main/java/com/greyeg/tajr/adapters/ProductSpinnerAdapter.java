package com.greyeg.tajr.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.greyeg.tajr.R;
import com.greyeg.tajr.models.ProductForSpinner;
import com.greyeg.tajr.models.Products;

import java.util.List;

public class ProductSpinnerAdapter extends ArrayAdapter<String > {

    List<ProductForSpinner>products;

    Context mContext;

    public ProductSpinnerAdapter(@NonNull Context context, List<ProductForSpinner>products) {
        super(context, R.layout.layout_product_spinner_item);
        this.products = products;
        this.mContext = context;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder mViewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.layout_product_spinner_item, parent, false);
            mViewHolder.mFlag =  convertView.findViewById(R.id.product_image);
            mViewHolder.mName = (TextView) convertView.findViewById(R.id.product_name);

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.mFlag.setImageURI(products.get(position).getImage());
        mViewHolder.mName.setText(products.get(position).getName());


        return convertView;
    }

    private static class ViewHolder {
        SimpleDraweeView mFlag;
        TextView mName;

    }
}
