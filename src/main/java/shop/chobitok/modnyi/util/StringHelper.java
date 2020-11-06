package shop.chobitok.modnyi.util;

import org.springframework.util.StringUtils;
import shop.chobitok.modnyi.entity.Shoe;
import shop.chobitok.modnyi.entity.Status;
import shop.chobitok.modnyi.entity.response.EarningsResponse;
import shop.chobitok.modnyi.entity.response.StringResponse;

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

}
