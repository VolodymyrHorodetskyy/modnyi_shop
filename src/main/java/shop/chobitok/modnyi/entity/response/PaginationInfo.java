package shop.chobitok.modnyi.entity.response;

public class PaginationInfo {

    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalElement;

    public PaginationInfo() {
    }

    public PaginationInfo(int pageNumber, int pageSize, int totalPages, long totalElement) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalElement = totalElement;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElement() {
        return totalElement;
    }

    public void setTotalElement(long totalElement) {
        this.totalElement = totalElement;
    }
}
