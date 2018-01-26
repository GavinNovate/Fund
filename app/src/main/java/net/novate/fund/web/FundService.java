package net.novate.fund.web;

import net.novate.fund.web.response.GetFundResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by gavin on 18-1-26.
 */

public interface FundService {

    String BASE_URL = "http://fund.eastmoney.com/f10/";

    @GET("F10DataApi.aspx")
    Observable<GetFundResponse> api(@Query("type") String type, @Query("code") String code, @Query("page") int page, @Query("per") int per);
}
