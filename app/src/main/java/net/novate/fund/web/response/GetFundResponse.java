package net.novate.fund.web.response;

import net.novate.fund.data.entity.Fund;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gavin on 18-1-26.
 */

public class GetFundResponse {

    private String code;

    private List<Fund> funds;

    public List<Fund> getFunds() {
        return funds;
    }

    public void addFund(Fund fund) {
        if (funds == null) {
            funds = new ArrayList<>();
        }
        if (code != null) {
            fund.setCode(code);
        }
        funds.add(fund);
    }

    public void setCode(String code) {
        this.code = code;
        if (funds != null) {
            for (Fund fund : funds) {
                fund.setCode(code);
            }
        }
    }
}
