package yoyo.inventory.execption;

import org.springframework.http.HttpStatus;


public class ResourceNotFoundExecption extends  ApiExecption {
    public ResourceNotFoundExecption( String resourceName , Long id ) {
        super(HttpStatus.NOT_FOUND,  String.format("%s With Id = %d not found" ,resourceName , id ));
    }
}
