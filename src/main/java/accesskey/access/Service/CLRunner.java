package accesskey.access.Service;

import accesskey.access.Entity.KeyDetails;
import accesskey.access.Entity.User;
import accesskey.access.Repository.AccessKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CLRunner implements CommandLineRunner {

    private final AccessKeyService accessKeyService;
    private final UserService userService;
    private final AccessKeyRepository accessKeyRepository;

    @Override
    public void run(String... args) throws Exception {

       List<KeyDetails> keys = this.accessKeyService.getAllAccessKeysByEmail("mawulegabriel@gmail.com");
       keys.forEach(System.out::println);

        //User user = userService.findUserByEmail("mawulegabriel@gmail.com");
        //var user2 = accessKeyRepository.findById(11);


        //System.out.println("This is user from access repo" +user2);

    }
}
