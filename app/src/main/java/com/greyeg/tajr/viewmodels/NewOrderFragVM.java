package com.greyeg.tajr.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.greyeg.tajr.models.AllProducts;
import com.greyeg.tajr.repository.ProductsRepo;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class NewOrderFragVM extends ViewModel {

    private MutableLiveData<Response<AllProducts>> products=new MutableLiveData<>();
    private MutableLiveData<Boolean> isProductsLoading=new MutableLiveData<>();
    private MutableLiveData<String> productsLoadingError=new MutableLiveData<>();

    public MutableLiveData<Response<AllProducts>> getProducts(String token, String user_id){

        isProductsLoading.setValue(true);
        ProductsRepo.getInstance().getProducts(token,user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<AllProducts>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Response<AllProducts> allProductsResponse) {
                        products.setValue(allProductsResponse);
                        isProductsLoading.setValue(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        isProductsLoading.setValue(false);
                        productsLoadingError.setValue(e.getMessage());

                    }
                });

        return products;

    }

    public MutableLiveData<Response<AllProducts>> getProducts() {
        return products;
    }

    public MutableLiveData<Boolean> getIsProductsLoading() {
        return isProductsLoading;
    }

    public MutableLiveData<String> getProductsLoadingError() {
        return productsLoadingError;
    }
}