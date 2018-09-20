package cn.gov.eximbank.psl;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.math.BigDecimal.ROUND_HALF_DOWN;

public class PslInterestCalculator {

    public static void main(String[] args) {
        String fileName = "PSL.xlsx";
        PslInterestCalculator calculator = new PslInterestCalculator();
        if (calculator.readPslInfo(fileName)) {
            System.out.println("利息是 : " + calculator.calculateInterest());
        }
    }

    private List<PslContract> contracts;

    private Date currentDate;

    public PslInterestCalculator() {
        contracts = new ArrayList<PslContract>();
        currentDate = new Date();
    }

    public boolean readPslInfo(String fileName) {
        try {
            InputStream in = new FileInputStream(fileName);
            Workbook wb = WorkbookFactory.create(in);
            Sheet sheet = wb.getSheetAt(0);
            currentDate = getCurrent(sheet);
            for (int i = 1; i != sheet.getLastRowNum() + 1; ++i) {
                PslContract contract = getContract(sheet.getRow(i));
                if (contract != null) {
                    contracts.add(contract);
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public BigDecimal calculateInterest() {
        BigDecimal interest = BigDecimal.ZERO;
        for (PslContract contract : contracts) {
            if (contract.isValid() && currentDate.after(contract.getStartDate())) {
                int days = betweenDays(contract.getStartDate(), currentDate);
                interest = interest.add(contract.getAmount().multiply(BigDecimal.valueOf(days)).multiply(contract.getInterestRate())
                        .divide(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(365), 5, ROUND_HALF_DOWN));
            }
        }
        return interest;
    }

    private Date getCurrent(Sheet sheet) {
        Row titleRow = sheet.getRow(0);
        Cell currentDateCell = titleRow.getCell(8);
        Date currentDate = currentDateCell.getDateCellValue();
        return currentDate;
    }

    private PslContract getContract(Row row) {
        String name = row.getCell(0).getStringCellValue();
        Cell amountCell = row.getCell(1);
        BigDecimal amount = BigDecimal.ZERO;
        if (amountCell.getCellTypeEnum().equals(CellType.STRING)) {
            amount = new BigDecimal(amountCell.getStringCellValue());
        }
        Date startDate = row.getCell(2).getDateCellValue();
        BigDecimal interestRate = BigDecimal.ZERO;
        Cell interestRateCell = row.getCell(3);
        if (interestRateCell.getCellTypeEnum().equals(CellType.STRING)) {
            interestRate = new BigDecimal(interestRateCell.getStringCellValue());
        }
        boolean isValid = false;
        if (row.getCell(4).getStringCellValue().equals("是")) {
            isValid = true;
        }
        return new PslContract(name, amount, startDate, interestRate, isValid);
    }


    private int betweenDays(Date start, Date end) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        long time1 = cal.getTimeInMillis();
        cal.setTime(end);
        long time2 = cal.getTimeInMillis();
        long between_days=(time2 - time1) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days));
    }
}
