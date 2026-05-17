package yoyo.inventory.execption;

import org.springframework.http.HttpStatus;


public class ResourceNotFoundExecption extends  ApiExecption {
    public ResourceNotFoundExecption( String resourceName , Long id ) {
        super(HttpStatus.NOT_FOUND,  String.format("%s With Id = %d not found" ,resourceName , id ));
    }
    public ResourceNotFoundExecption( String resourceName , Long id , long parentId ) {
        super(HttpStatus.NOT_FOUND,  String.format("%s With Id = %d , id =%d not found" ,resourceName , id  , parentId ));
    }
    public ResourceNotFoundExecption( Long id , long parentId ) {
        super(HttpStatus.NOT_FOUND,  String.format("Id = %d , id =%d not found"  , id  , parentId ));
    }
    public ResourceNotFoundExecption( String resourceName ) {
        super(HttpStatus.NOT_FOUND,  String.format("%s  not found" ,resourceName  ));
    }
}
