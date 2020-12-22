package shop.chobitok.modnyi.util;

import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.response.EarningsResponse;
import shop.chobitok.modnyi.entity.response.StringResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StringHelper {

    public static StringResponse fromEarningResponse(EarningsResponse earningsResponse) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("From ").append(earningsResponse.getFrom()).append("  To ")
                .append(earningsResponse.getTo()).append("\n").append("Sum: ")
                .append(earningsResponse.getSum()).append("\n").append("Predicted sum: ").append(earningsResponse.getPredictedSum())
                .append("\n").append("Realistic sum: ").append(earningsResponse.getRealisticSum())
                .append("\n").append("Received %: ").append(earningsResponse.getReceivedPercentage())
                .append("\n");
        for (Map.Entry<Status, Integer> st : earningsResponse.getAmountByStatus().entrySet()) {
            stringBuilder.append(st.getKey()).append(" : ").append(st.getValue()).append("\n");
        }
        stringBuilder.append("All : ").append(earningsResponse.getAll());
        return new StringResponse(stringBuilder.toString());
    }

    public static StringResponse formCardStatsInfo(EarningsResponse earningsResponse) {
        StringBuilder result = new StringBuilder();
        result.append("Сума = ").append(earningsResponse.getSum()).append("\n")
                .append("Сума передбачуваних = ").append(earningsResponse.getPredictedSum()).append("\n")
                .append("80% = ").append(earningsResponse.getRealisticSum()).append("\n");
        return new StringResponse(result.toString());
    }

    public static StringResponse fromSoldShoeResponse(Map<Shoe, Integer> sortedByAmount) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Shoe, Integer> entry : sortedByAmount.entrySet()) {
            builder.append(entry.getKey().getModel() + " " + entry.getKey().getColor() + " = " + entry.getValue() + "\n");
        }
        return new StringResponse(builder.toString());
    }

    public static String removeSpaces(String s) {
        if (!StringUtils.isEmpty(s)) {
            s.replaceAll("\\s+", "");
        }
        return s;
    }


    public static List<String> splitTTNString(String ttns) {
        List<String> ttnsList = new ArrayList<>();
        if (ttns != null) {
            String[] ttnsArray = ttns.split("\\s+");
            for (String ttn : ttnsArray) {
                if (!StringUtils.isEmpty(ttn) && isNumeric(ttn) && ttn.length() == 14) {
                    ttnsList.add(ttn);
                }
            }
        }
        return ttnsList;
    }

    private static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }


}
