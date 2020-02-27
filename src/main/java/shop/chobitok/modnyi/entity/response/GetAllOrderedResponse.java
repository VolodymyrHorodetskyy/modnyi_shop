package shop.chobitok.modnyi.entity.response;

import shop.chobitok.modnyi.entity.Ordered;

import java.util.List;

public class GetAllOrderedResponse {

    private PaginationInfo paginationInfo;
    private List<Ordered> orderedList;

    public PaginationInfo getPaginationInfo() {
        return paginationInfo;
    }

    public void setPaginationInfo(PaginationInfo paginationInfo) {
        this.paginationInfo = paginationInfo;
    }

    public List<Ordered> getOrderedList() {
        return orderedList;
    }

    public void setOrderedList(List<Ordered> orderedList) {
        this.orderedList = orderedList;
    }
}
