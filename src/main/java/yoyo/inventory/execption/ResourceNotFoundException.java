package yoyo.inventory.execption;

import org.springframework.http.HttpStatus;


public class ResourceNotFoundException extends ApiException {
    public ResourceNotFoundException(String resourceName , Long id ) {
        super(HttpStatus.NOT_FOUND,  String.format("%s With Id = %d not found" ,resourceName , id ));
    }
    public ResourceNotFoundException(String resourceName , Long id , long parentId ) {
        super(HttpStatus.NOT_FOUND,  String.format("%s With Id = %d , id =%d not found" ,resourceName , id  , parentId ));
    }
    public ResourceNotFoundException(Long id , long parentId ) {
        super(HttpStatus.NOT_FOUND,  String.format("Id = %d , id =%d not found"  , id  , parentId ));
    }
    public ResourceNotFoundException(String resourceName ) {
        super(HttpStatus.NOT_FOUND,  String.format("%s  not found" ,resourceName  ));
    }
}
