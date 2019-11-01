package com.greyeg.tajr.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.greyeg.tajr.models.AllProducts;
import com.greyeg.tajr.models.Cities;
import com.greyeg.tajr.models.NewOrderResponse;
import com.greyeg.tajr.models.OrderPayload;
import com.greyeg.tajr.repository.CitiesRepo;
import com.greyeg.tajr.repository.OrdersRepo;
import com.greyeg.tajr.repository.ProductsRepo;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class NewOrderFragVM extends ViewModel {

    // liveData fields of products
    private MutableLiveData<Response<AllProducts>> products=new MutableLiveData<>();
    private MutableLiveData<Boolean> isProductsLoading=new MutableLiveData<>();
    private MutableLiveData<String> productsLoadingError=new MutableLiveData<>();

    //liveData of cities
    private MutableLiveData<Response<Cities>> cities=new MutableLiveData<>();
    private MutableLiveData<Boolean> isCitiesLoading=new MutableLiveData<>();
    private MutableLiveData<String> citiesLoadingError=new MutableLiveData<>();

    //liveData of placingOrder
    private MutableLiveData<NewOrderResponse> makeNewOrder=new MutableLiveData<>();
    private MutableLiveData<Boolean> isOrderPlacing=new MutableLiveData<>();
    private MutableLiveData<String> orderPlacingError=new MutableLiveData<>();



    // products part
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

    // cities part
    public MutableLiveData<Response<Cities>> getCities(String token, String user_id) {
        isCitiesLoading.setValue(true);
        CitiesRepo
                .getInstance()
                .getCities(token,user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<Cities>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Response<Cities> citiesResponse) {
                        cities.setValue(citiesResponse);
                        isCitiesLoading.setValue(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        citiesLoadingError.setValue(e.getMessage());
                        isCitiesLoading.setValue(false);
                    }
                });

        return cities;
    }

    public MutableLiveData<Boolean> getIsCitiesLoading() {
        return isCitiesLoading;
    }

    public MutableLiveData<String> getCitiesLoadingError() {
        return citiesLoadingError;
    }

    //placing order part

    public MutableLiveData<NewOrderResponse> makeNewOrder(OrderPayload orderPayload){
        isOrderPlacing.setValue(true);
        OrdersRepo
                .getInstance()
                .makeNewOrder(orderPayload)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<NewOrderResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Response<NewOrderResponse> response) {
                        isOrderPlacing.setValue(false);
                        makeNewOrder.setValue(response.body());
                        Log.d("PLACCEEORDERR", "onSuccess: "+(response.body().getData()));

                    }

                    @Override
                    public void onError(Throwable e) {
                        isOrderPlacing.setValue(false);
                        orderPlacingError.setValue(e.getMessage());
                        Log.d("PLACCEEORDERR", "onError: "+e.getMessage());
                    }
                });

        return makeNewOrder;
    }

    public MutableLiveData<Boolean> getIsOrderPlacing() {
        return isOrderPlacing;
    }

    public MutableLiveData<String> getOrderPlacingError() {
        return orderPlacingError;
    }
}