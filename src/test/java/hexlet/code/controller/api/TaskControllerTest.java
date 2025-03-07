package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelProvider;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelProvider modelProvider;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    private JwtRequestPostProcessor token;

    private Task testTask;

    private TaskStatus testTaskStatus;

    private User testUser;

    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testUser = Instancio.of(modelProvider.getUserModel()).create();
        userRepository.save(testUser);

        testTaskStatus = Instancio.of(modelProvider.getTaskStatusModel()).create();
        taskStatusRepository.save(testTaskStatus);

        testTask = Instancio.of(modelProvider.getTaskModel()).create();
        testTask.setTaskStatus(testTaskStatus);
        testTask.setAssignee(testUser);
        taskRepository.save(testTask);

        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @Test
    public void testCreateValidData() throws Exception {
        TaskCreateDTO newTask = taskMapper
                .forTest(Instancio.of(modelProvider.getTaskModel()).create());
        newTask.setAssigneeId(testUser.getId());
        newTask.setStatus(testTaskStatus.getSlug());

        var request = post("/api/tasks")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(newTask));
        mockMvc.perform(request)
                .andExpect(status().isCreated());
        Task savedTask = taskRepository.findByName(newTask.getTitle()).get();

        assertNotNull(savedTask);
        assertThat(savedTask.getName()).isEqualTo(newTask.getTitle());
    }

    @Test
    public void testCreateInvalidData() throws Exception {
        TaskCreateDTO newTask = taskMapper
                .forTest(Instancio.of(modelProvider.getTaskModel()).create());

        var request = post("/api/tasks")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(newTask));
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testFindById() throws Exception {
        var request = get("/api/tasks/" + testTask.getId()).with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("title").isEqualTo(testTask.getName()),
                v -> v.node("status").isEqualTo(testTask.getTaskStatus().getSlug())
        );
    }

    @Test
    public void testFindByIdUnauthorized() throws Exception {
        var request = get("/api/tasks/" + testTask.getId());
        var result = mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testFindAll() throws Exception {
        var request = get("/api/tasks").with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        List<TaskDTO> taskDTOS = om.readValue(body, new TypeReference<>() {});
        var actual = taskDTOS.stream().map(taskMapper::map).toList();
        var expected = taskRepository.findAll();

        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testDeleteById() throws Exception {
        var request = delete("/api/tasks/" + testTask.getId()).with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        Task deletedTask = taskRepository.findById(testTask.getId()).orElse(null);

        assertThat(deletedTask).isNull();
    }

    @Test
    public void testUpdateValidData() throws Exception {
        Map<String, String> updateData = new HashMap<>();
        updateData.put("title", "Update");

        var request = put("/api/tasks/" + testTask.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(updateData));
        mockMvc.perform(request)
                .andExpect(status().isOk());
        Task updatedTask = taskRepository.findById(testTask.getId()).get();

        assertThat(updatedTask.getName()).isEqualTo(updateData.get("title"));
    }

    @Test
    public void testUpdateInvalidData() throws Exception {
        Map<String, String> updateData = new HashMap<>();
        updateData.put("title", "");

        var request = put("/api/tasks/" + testTask.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(updateData));
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }
}
