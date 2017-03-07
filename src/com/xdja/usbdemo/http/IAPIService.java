package com.xdja.usbdemo.http;

import com.xdja.usbdemo.bean.PersonBean;
import com.xdja.usbdemo.bean.SaveReturn;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Query;

public interface IAPIService {

    @POST("savePerson.do")
    Call<SaveReturn> savePerson(@Body PersonBean bean);

    @POST("findAllPersons.do")
    Call<SaveReturn> getPersonList();

    @POST("findPerson.do")
    Call<SaveReturn> findPerson(@Query(value = "id") String id);

    @POST("deletePerson.do")
    Call<SaveReturn> deletePerson(@Query(value = "id") String id);

    @POST("findAllFingerprint.do")
    Call<SaveReturn> findAllFingerprint();

    @POST("findFingerprint.do")
    Call<SaveReturn> findFingerprint(@Query(value = "id") String id);
}
