package shop.chobitok.modnyi.util;

import shop.chobitok.modnyi.entity.CompanyFinanceControl;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.response.EarningsResponse;
import shop.chobitok.modnyi.entity.response.StringResponse;
import shop.chobitok.modnyi.exception.ConflictException;

import java.util.*;

import static org.springframework.util.StringUtils.isEmpty;

public class StringHelper {

    public static StringResponse fromEarningResponse(EarningsResponse earningsResponse) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("From ").append(earningsResponse.getFrom()).append("  To ")
                .append(earningsResponse.getTo()).append("\n").append("Sum: ")
                .append(earningsResponse.getSum()).append("\n").append("Predicted sum: ").append(earningsResponse.getPredictedSum())
                .append("\n").append("Realistic sum: ").append(earningsResponse.getRealisticSum())
                .append("\n").append("Received %: ").append(earningsResponse.getReceivedPercentage())
                .append("\n").append("Monthly receiving %: ").append(earningsResponse.getMonthlyReceivingPercentage())
                .append("\n");
        for (Map.Entry<Status, Integer> st : earningsResponse.getAmountByStatus().entrySet()) {
            stringBuilder.append(st.getKey()).append(" : ").append(st.getValue()).append("\n");
        }
        stringBuilder.append("All : ").append(earningsResponse.getAll());
        return new StringResponse(stringBuilder.toString());
    }

    public static StringResponse formCardStatsInfo(EarningsResponse earningsResponse) {
        String result = "Сума = " + earningsResponse.getSum() + "\n" +
                "Сума передбачуваних = " + earningsResponse.getPredictedSum() + "\n" +
                "80% = " + earningsResponse.getRealisticSum() + "\n";
        return new StringResponse(result);
    }

    public static StringResponse fromSoldShoeResponse(Map<Shoe, Integer> sortedByAmount) {
        StringBuilder builder = new StringBuilder();
        Integer wholeAmountSoldShoes = 0;
        for (Map.Entry<Shoe, Integer> entry : sortedByAmount.entrySet()) {
            wholeAmountSoldShoes += entry.getValue();
        }
        Double percentageOfOneShoe = 100d / wholeAmountSoldShoes;
        for (Map.Entry<Shoe, Integer> entry : sortedByAmount.entrySet()) {
            builder.append(entry.getKey().getModel()).append(" ")
                    .append(entry.getKey().getColor()).append(" = ")
                    .append(entry.getValue()).append(", % = ").append(entry.getValue() * percentageOfOneShoe).append("\n");
        }
        return new StringResponse(builder.toString());
    }

    public static String removeSpaces(String s) {
        if (!isEmpty(s)) {
            s = s.replaceAll("\\s+", "");
        }
        return s;
    }

    public static List<String> splitTTNString(String ttns) {
        List<String> ttnsList = new ArrayList<>();
        if (ttns != null) {
            String[] ttnsArray = ttns.split("\\s+");
            for (String ttn : ttnsArray) {
                if (!isEmpty(ttn) && isNumeric(ttn) && ttn.length() == 14) {
                    ttnsList.add(ttn);
                }
            }
        }
        return ttnsList;
    }

    public static ArrayList<String> splitPhonesStringBySemiColonAndValidate(String phones) {
        if (!isEmpty(phones)) {
            phones = org.apache.commons.lang3.StringUtils.remove(phones, "+");
            Set<String> phonesSet = new HashSet<>();
            if (phones.contains(";")) {
                String[] phonesArray = phones.split(";");
                for (String p : phonesArray) {
                    validatePhone(p);
                    if (!phonesSet.add(p)) {
                        throw new ConflictException("Однакові телефони");
                    }
                }
            } else {
                validatePhone(phones);
                if (!phonesSet.add(phones)) {
                    throw new ConflictException("Однакові телефони");
                }
            }
            return new ArrayList<>(phonesSet);
        }
        return null;
    }

    private static void validatePhone(String phone) {
        if (isEmpty(phone)) {
            throw new ConflictException("Телефон пустий");
        } else if (!isNumeric(phone)) {
            throw new ConflictException("Телефон може містити тільки цифри");
        } else if (phone.length() != 12) {
            throw new ConflictException("Не вірна кількість цифр в телефоні");
        } else {
            char[] chars = phone.toCharArray();
            if (chars[0] != '3' || chars[1] != '8'
                    || chars[2] != '0') {
                throw new ConflictException("Телефон має починатись 380");
            }
        }
    }

    private static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static StringResponse fromCompanyFinanceControl(CompanyFinanceControl companyFinanceControl) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(companyFinanceControl.getCompany().getName()).append("\n\n")
                .append("Ситуація зараз : ").append(companyFinanceControl.getCurrentFinanceState()).append("\n")
                .append("Операція : ").append(companyFinanceControl.getOperation()).append("\n")
                .append("Опис : ").append("\n").append(companyFinanceControl.getDescription()).append("\n");
        return new StringResponse(stringBuilder.toString());
    }
}
