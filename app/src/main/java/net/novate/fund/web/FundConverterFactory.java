package net.novate.fund.web;

import net.novate.fund.web.response.GetFundResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.annotations.Nullable;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by gavin on 18-1-26.
 */

public class FundConverterFactory extends Converter.Factory {
    public static final FundConverterFactory INSTANCE = new FundConverterFactory();

    public static FundConverterFactory create() {
        return INSTANCE;
    }

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (type == GetFundResponse.class) {
            return FundConverter.INSTANCE;
        } else {
            return null;
        }
    }
}
