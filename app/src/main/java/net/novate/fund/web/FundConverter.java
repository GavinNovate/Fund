package net.novate.fund.web;

import android.support.annotation.NonNull;

import net.novate.fund.data.entity.Fund;
import net.novate.fund.web.response.GetFundResponse;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by gavin on 18-1-26.
 */

public class FundConverter implements Converter<ResponseBody, GetFundResponse> {

    private static final String TAG = "FundConverter";

    public static final FundConverter INSTANCE = new FundConverter();

    @Override
    public GetFundResponse convert(@NonNull ResponseBody value) throws IOException {

        GetFundResponse response = new GetFundResponse();
        StringBuffer buffer = new StringBuffer(replace(value.string()));
        subTbody(buffer);
        String data = subData(buffer);
        while (data != null) {
            response.addFund(converToFund(data));
            data = subData(buffer);
        }

        return response;
    }

    private String replace(String string) {

        return string
                .replace("<td class='tor bold'>", "<td>")
                .replace("<td class='tor bold grn'>", "<td>")
                .replace("<td class='tor bold red'>", "<td>")
                .replace("<td class='tor bold bck'>", "<td>");
    }

    private void subTbody(StringBuffer buffer) {

        String tbodyHead = "<tbody>";
        String tbodyFoot = "</tbody>";

        int tbodyStart = buffer.indexOf(tbodyHead);
        if (tbodyStart >= 0) {
            buffer.delete(0, tbodyStart + tbodyHead.length());
        }

        int tbodyEnd = buffer.lastIndexOf(tbodyFoot);
        if (tbodyEnd >= 0) {
            buffer.delete(tbodyEnd, buffer.length());
        }
    }

    private String subData(StringBuffer buffer) {
        String trHead = "<tr>";
        String trFoot = "</tr>";

        int trStart = buffer.indexOf(trHead);
        int trEnd = buffer.indexOf(trFoot);

        if (trStart < trEnd && trStart >= 0) {
            String data = buffer.substring(trStart + trHead.length(), trEnd);
            buffer.delete(0, trEnd + trFoot.length());
            return data;
        } else {
            return null;
        }
    }

    private Fund converToFund(String data) {
        String tdHead = "<td>";
        String tdFoot = "</td>";

        int tdStart, tdEnd;

        Fund fund = new Fund();

        for (int i = 0; i < 6; i++) {
            tdStart = data.indexOf(tdHead);
            tdEnd = data.indexOf(tdFoot);

            if (tdStart < tdEnd && tdStart >= 0) {
                String string = data.substring(tdStart + tdHead.length(), tdEnd);
                switch (i) {
                    case 0:
                        fund.setDate(string);
                        break;
                    case 1:
                        fund.setPrice(Double.parseDouble(string));
                        break;
                    case 2:
                        fund.setValue(Double.parseDouble(string));
                        break;
                    case 3:
                        if (string.length() > 0) {
                            fund.setRate(Double.parseDouble(string.substring(0, string.length() - 1)));
                        }
                        break;
                    case 4:
                        if (string.equals("开放申购")) {
                            fund.setPurchasable(true);
                        }
                        break;
                    case 5:
                        if (string.equals("开放赎回")) {
                            fund.setRedeemable(true);
                        }
                        break;
                }
                data = data.substring(tdEnd + tdFoot.length());
            }
        }
        return fund;
    }
}
