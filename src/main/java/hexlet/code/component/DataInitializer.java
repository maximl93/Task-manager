package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
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

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User initUser = new User();
        initUser.setEncodedPassword("qwerty");
        initUser.setEmail("hexlet@example.com");
        userService.createUser(initUser);

        Map<String, String> taskStatuses = Map.of(
                "Draft", "draft",
                "ToReview", "to_review",
                "ToBeFixed", "to_be_fixed",
                "ToPublish", "to_publish",
                "Published", "published"
        );

        taskStatuses.forEach((key, value) -> {
            TaskStatus taskStatus = new TaskStatus();
            taskStatus.setName(key);
            taskStatus.setSlug(value);
            taskStatusRepository.save(taskStatus);
        });

        List<String> labelsNames = List.of("feature", "bug");
        for (String name : labelsNames) {
            Label label = new Label();
            label.setName(name);
            labelRepository.save(label);
        }
    }
}
