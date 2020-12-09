package shop.chobitok.modnyi.util;

import shop.chobitok.modnyi.entity.Ordered;
import shop.chobitok.modnyi.entity.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderHelper {

    public static Map<Status, List<Ordered>> breakdownByStatuses(List<Ordered> orderedList) {
        Map<Status, List<Ordered>> statusListMap = new HashMap<>();
        for (Status status : Status.values()) {
            statusListMap.put(status, new ArrayList<>());
        }

        for (Ordered ordered : orderedList) {
            statusListMap.get(ordered.getStatus()).add(ordered);
        }
        return statusListMap;
    }

}
