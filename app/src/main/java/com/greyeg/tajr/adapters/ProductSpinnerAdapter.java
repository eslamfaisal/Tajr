package com.greyeg.tajr.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.greyeg.tajr.R;
import com.greyeg.tajr.models.ProductForSpinner;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

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
            mViewHolder.price = (TextView) convertView.findViewById(R.id.price);

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.mFlag.setImageURI(products.get(position).getImage());
        mViewHolder.mName.setText(products.get(position).getName());
        mViewHolder.price.setVisibility(View.VISIBLE);
        mViewHolder.price.setText(  NumberFormat.getNumberInstance(Locale.getDefault()).format(Integer.valueOf(products.get(position).getItemCost()))+mContext.getString(R.string.le));


        return convertView;
    }

    private static class ViewHolder {
        SimpleDraweeView mFlag;
        TextView mName;
        TextView price;

    }
}
