package cn.gov.eximbank.psl;

import java.math.BigDecimal;
import java.util.Date;

public class PslContract {

    private String name;

    private BigDecimal amount;

    private Date startDate;

    private BigDecimal interestRate;

    private boolean isValid;

    public PslContract(String name, BigDecimal amount, Date startDate, BigDecimal interestRate, boolean isValid) {
        this.name = name;
        this.amount = amount;
        this.startDate = startDate;
        this.interestRate = interestRate;

        this.isValid = isValid;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public boolean isValid() {
        return isValid;
    }
}
