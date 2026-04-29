package yoyo.inventory.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface Pagination {
    int DEFAULT_PAGE_LIMIT = 10;
    int DEFAULT_PAGE_NUMBER = 0;
    String PAGE_LIMIT  =   "limit";
    String PAGE_NUMBER    =   "page";
    //    Pageable
    static Pageable getPageable(int pageNumber , int pageSize ){
        if (pageNumber <DEFAULT_PAGE_NUMBER){
            pageNumber = DEFAULT_PAGE_NUMBER;
        }
        if (pageSize<1){
            pageSize = DEFAULT_PAGE_LIMIT;
        }
        Pageable pageable = PageRequest.of(pageNumber-1 , pageSize);
        return pageable ;
    }
}
