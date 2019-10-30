package com.greyeg.tajr.repository;

import com.greyeg.tajr.models.AllProducts;
import com.greyeg.tajr.server.BaseClient;

import java.util.ArrayList;

import io.reactivex.Single;
import retrofit2.Response;

public class ProductsRepo {

    private static ProductsRepo productsRepo;


    public static ProductsRepo getInstance() {
        return productsRepo==null?productsRepo=new ProductsRepo():productsRepo;
    }


    private ProductsRepo() {

    }

    public Single<Response<AllProducts>> getProducts(String token, String user_id){
        Single<Response<AllProducts>> products
                = BaseClient
                .getService()
                .getProducts2(token,user_id);

        return products;
    }




}
