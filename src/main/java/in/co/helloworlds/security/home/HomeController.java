package in.co.helloworlds.security.home;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HomeController {

    @GetMapping("/home")
    public ResponseEntity<String> home() {
        log.info("HomeController ==========> /home");
        return ResponseEntity.ok("Home Page");
    }
}
