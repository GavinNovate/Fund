package net.novate.fund;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import net.novate.fund.data.entity.Fund;
import net.novate.fund.web.FundConverterFactory;
import net.novate.fund.web.FundService;
import net.novate.fund.web.response.GetFundResponse;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FundService.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(FundConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        findViewById(R.id.hello)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        retrofit.create(FundService.class)
                                .api("lsjz", "110022", 1, 1000)
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Consumer<GetFundResponse>() {
                                    @Override
                                    public void accept(GetFundResponse getFundResponse) throws Exception {
                                        Log.d(TAG, "accept: " + getFundResponse.getFunds().size());
                                        getFundResponse.setCode("160222");

                                        List<Double> monday = new ArrayList<>();
                                        List<Double> tuesday = new ArrayList<>();
                                        List<Double> wednesday = new ArrayList<>();
                                        List<Double> thursday = new ArrayList<>();
                                        List<Double> friday = new ArrayList<>();

                                        List<Double>[] rateMap = new ArrayList[32];
                                        for (int i = 0; i < 32; i++) {
                                            rateMap[i] = new ArrayList<>();
                                        }

                                        for (Fund fund : getFundResponse.getFunds()) {
                                            Log.d(TAG, "accept: " + getWeek(fund.getDate()) + " - " + fund);
                                            switch (getWeek(fund.getDate())) {
                                                case "星期一":
                                                    monday.add(fund.getRate());
                                                    break;
                                                case "星期二":
                                                    tuesday.add(fund.getRate());
                                                    break;
                                                case "星期三":
                                                    wednesday.add(fund.getRate());
                                                    break;
                                                case "星期四":
                                                    thursday.add(fund.getRate());
                                                    break;
                                                case "星期五":
                                                    friday.add(fund.getRate());
                                                    break;
                                            }

                                            rateMap[dayOfM(fund.getDate())].add(fund.getRate());
                                        }

                                        Log.d(TAG, "accept: 星期一 " + monday.size() + " : " + average(monday) / monday.size());
                                        Log.d(TAG, "accept: 星期二 " + tuesday.size() + " : " + average(tuesday) / tuesday.size());
                                        Log.d(TAG, "accept: 星期三 " + wednesday.size() + " : " + average(wednesday) / wednesday.size());
                                        Log.d(TAG, "accept: 星期四 " + thursday.size() + " : " + average(thursday) / thursday.size());
                                        Log.d(TAG, "accept: 星期五 " + friday.size() + " : " + average(friday) / friday.size());

                                        for (int i = 1; i < rateMap.length; i++) {
                                            Log.d(TAG, "accept: " + i + "  " + rateMap[i].size() + " : " + average(rateMap[i]) / rateMap[i].size());
                                        }
                                    }
                                });
                    }
                });
    }

    private int dayOfM(String s) {
        String s1 = s.substring(s.length() - 2, s.length());
        return Integer.parseInt(s1);
    }

    private double average(List<Double> doubles) {
        double d = 0;
        for (double dou : doubles) {
            d += dou;
        }
        return d;
    }

    public String getWeek(String sdate) {
        // 再转换为时间
        Date date = strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // int hour=c.get(Calendar.DAY_OF_WEEK);
        // hour中存的就是星期几了，其范围 1~7
        // 1=星期日 7=星期六，其他类推
        return new SimpleDateFormat("EEEE").format(c.getTime());
    }

    public Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }
}
