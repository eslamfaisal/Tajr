package com.greyeg.tajr.helper;

import com.greyeg.tajr.models.OrderProduct;
import com.greyeg.tajr.models.ProductData;

public class ProductUtil {

    public static OrderProduct toOrderProduct(ProductData productData,OrderProduct product){
        if (productData==null)return null;
        product.setId("1000");
        product.setName(productData.getProduct_name());
        product.setPrice(Integer.parseInt(productData.getProduct_real_price()));
        product.setImage(productData.getProduct_image());
        product.setExtras(productData.getExtra_data());
        return product;

    }
}
