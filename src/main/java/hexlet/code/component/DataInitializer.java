package hexlet.code.component;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Faker faker;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User initUser = new User();
        initUser.setFirstName(faker.name().firstName());
        initUser.setLastName(faker.name().lastName());
        initUser.setEncodedPassword("qwerty");
        initUser.setEmail("hexlet@example.com");
        userRepository.save(initUser);
    }
}
