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
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
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
                                .api("lsjz", "160213", 1, 1000)
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Consumer<GetFundResponse>() {
                                    @Override
                                    public void accept(GetFundResponse getFundResponse) throws Exception {
                                        getFundResponse.setCode("160213");

                                        test2(getFundResponse);
                                    }
                                });
                    }
                });
    }


    private void test4(final List<Fund> funds) {
        Observable.interval(10, TimeUnit.MILLISECONDS)
                .map(new Function<Long, Fund>() {

                    Iterator<Fund> iterator = funds.iterator();

                    @Override
                    public Fund apply(Long aLong) throws Exception {
                        if (iterator.hasNext()) {
                            return iterator.next();
                        } else {
                            return new Fund();
                        }
                    }
                })
                .subscribe(new Observer<Fund>() {
                    Disposable disposable;

                    double total = 0;
                    double share = 0;

                    double input = 0;   // 投入
                    double output = 0;  // 产出

                    int digitalDate;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Fund fund) {
                        if (fund.getCode() == null) {
                            disposable.dispose();
                            return;
                        }

                        if (getWeek(fund.getDate()).equals("星期五")) {
                            share += 1000 / fund.getValue();
                            total += 1000;
                        }

                        Log.d(TAG, "onNext - 5: " + total + "  " + share * fund.getValue() + "  " + share * fund.getValue() / total);


                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void test3(final List<Fund> funds) {

        Observable.interval(10, TimeUnit.MILLISECONDS)
                .map(new Function<Long, Fund>() {

                    Iterator<Fund> iterator = funds.iterator();

                    @Override
                    public Fund apply(Long aLong) throws Exception {
                        if (iterator.hasNext()) {
                            return iterator.next();
                        } else {
                            return new Fund();
                        }
                    }
                })
                .subscribe(new Observer<Fund>() {
                    Disposable disposable;

                    double total = 0;
                    double share = 0;

                    double input = 0;   // 投入
                    double output = 0;  // 产出

                    int digitalDate;

                    List<Double> collect = new ArrayList<>();

                    int cd = 25;

                    double out = 0.0;
                    double in = 0.0;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Fund fund) {
                        if (fund.getCode() == null) {
                            disposable.dispose();
                            return;
                        }
                        cd--;
                        // 初始化下次日期
                        if (digitalDate == 0) {
                            if (fund.getDay() > 25) {
                                if (fund.getMonth() == 12) {
                                    digitalDate = (fund.getYear() + 1) * 10000 + 125;
                                } else {
                                    digitalDate = fund.getYear() * 10000 + (fund.getMonth() + 1) * 100 + 25;
                                }
                            } else {
                                digitalDate = fund.getYear() * 10000 + fund.getMonth() * 100 + 25;
                            }
                        }

                        if (fund.isPurchasable() && fund.getDigitalDate() >= digitalDate) {
                            // 买入
                            share += 5000 / fund.getValue();
                            total = total + 5000;

                            // 下一次
                            if (fund.getDay() >= 25) {
                                if (fund.getMonth() == 12) {
                                    digitalDate = (fund.getYear() + 1) * 10000 + 125;
                                } else {
                                    digitalDate = fund.getYear() * 10000 + (fund.getMonth() + 1) * 100 + 25;
                                }
                            } else {
                                digitalDate = fund.getYear() * 10000 + fund.getMonth() * 100 + 25;
                            }
                        }

//                        if (out > 0) {
//                            Log.d(TAG, "onNext: out " + share * out * fund.getValue());
//                            output = output + share * out * fund.getValue();
//                            share = share - share * out;
//                            out = 0.0;
//                        }
//
//                        if (in > 0) {
//                            Log.d(TAG, "onNext: in " + output * in);
//
//                            share = share + output * in / fund.getValue();
//                            output = output - output * in;
//
//                            in = 0.0;
//                        }

                        collect.add(fund.getValue());
                        if (collect.size() > 260) {
                            collect.remove(0);
                        }
                        if (collect.size() == 260) {
                            if (cd <= 0) {
                                if (text(collect) > 1.5) {
                                    out = 0.1;
                                    cd = 25;
                                }
//                                else if (text(collect) > 1.6) {
//                                    out = 0.1;
//                                    cd = 10;
//                                }

                                if (text(collect) < 0.8) {
                                    in = 0.5;
                                    cd = 25;
                                }
//                                else if (text(collect) < 0.8) {
//                                    in = 0.4;
//                                    cd = 10;
//                                }
                            }
                        }
                        Log.d(TAG, "onNext: " + total + "  " + share * fund.getValue() + "  " + output + "  " + (share * fund.getValue() + output) / total);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private double text(List<Double> doubles) {
        if (doubles.size() != 260) {
            return 1.0;
        }

        double a1 = 0;
        for (int i = 0; i < 10; i++) {
            a1 += doubles.get(i);
        }
        a1 /= 10;

        double a2 = 0;
        for (int i = 225; i < 235; i++) {
            a2 += doubles.get(i);
        }
        a2 /= 10;

        double a3 = 0;
        for (int i = 250; i < 260; i++) {
            a3 += doubles.get(i);
        }
        a3 /= 10;

        double d250 = a3 / a1;
        double d25 = a3 / a2;

        d25 = Math.pow(d25, 10);
//        Log.d(TAG, "text: " + d250 + "  " + d25 + " -- " + (d25 / d250));

        return d25 / d250;
    }


    private void test2(GetFundResponse response) {
        List<Fund> funds = response.getFunds();
        Collections.sort(funds);

        test3(funds);
//        test4(funds);
    }

    private void test1(GetFundResponse getFundResponse) {
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
