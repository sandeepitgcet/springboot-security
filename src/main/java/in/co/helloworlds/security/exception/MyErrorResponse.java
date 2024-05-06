package in.co.helloworlds.security.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatusCode;

import java.util.Locale;

@Data
@AllArgsConstructor
public class MyErrorResponse  {

    private HttpStatusCode status;
    private String message;

}
