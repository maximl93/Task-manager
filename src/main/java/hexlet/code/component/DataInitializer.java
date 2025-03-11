package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private final CustomUserDetailsService userService;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        setDefaultUser();
        setDefaultTaskStatuses();
        setDefaultLabels();
    }

    public void setDefaultUser() {
        if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
            User initUser = new User();
            initUser.setEncodedPassword("qwerty");
            initUser.setEmail("hexlet@example.com");
            userService.createUser(initUser);
        }
    }

    public void setDefaultTaskStatuses() {
        Map<String, String> defaultTaskStatuses = Map.of(
                "Draft", "draft",
                "ToReview", "to_review",
                "ToBeFixed", "to_be_fixed",
                "ToPublish", "to_publish",
                "Published", "published"
        );

        defaultTaskStatuses.entrySet().stream()
                .filter(entry -> taskStatusRepository.findBySlug(entry.getValue()).isEmpty())
                .map(entry -> {
                    TaskStatus taskStatus = new TaskStatus();
                    taskStatus.setName(entry.getKey());
                    taskStatus.setSlug(entry.getValue());
                    return taskStatus;
                })
                .forEach(taskStatusRepository::save);
    }

    public void setDefaultLabels() {
        List<String> labelsNames = List.of("feature", "bug");
        for (String name : labelsNames) {
            if (labelRepository.findByName(name).isEmpty()) {
                Label label = new Label();
                label.setName(name);
                labelRepository.save(label);
            }
        }
    }
}
