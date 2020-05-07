package shop.chobitok.modnyi.util;

import shop.chobitok.modnyi.entity.response.EarningsResponse;
import shop.chobitok.modnyi.entity.response.StringResponse;

public class StringHelper {

    public static StringResponse fromEarningResponse(EarningsResponse earningsResponse) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("From " + earningsResponse.getFrom() + "  To " + earningsResponse.getTo() + "\n");
        stringBuilder.append("Sum: " + earningsResponse.getSum() + "\nPredicted sum: " + earningsResponse.getPredictedSum());
        stringBuilder.append("\nOrders\nAll: " + earningsResponse.getAll() + "\nReceived: " + earningsResponse.getReceived() + "\nDenied: " + earningsResponse.getDenied());
        stringBuilder.append("\nReceived %: " + earningsResponse.getReceivedPercentage());
        return new StringResponse(stringBuilder.toString());
    }

}
