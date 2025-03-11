package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelProvider;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
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
public class LabelControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelProvider modelProvider;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper labelMapper;

    private JwtRequestPostProcessor token;

    private User testUser;

    private Label testLabel;

    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        labelRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testUser = Instancio.of(modelProvider.getUserModel()).create();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));

        testLabel = Instancio.of(modelProvider.getLabelModel()).create();
        labelRepository.save(testLabel);
    }

    @Test
    public void testFindAllAuthorized() throws Exception {
        var request = get("/api/labels").with(token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        List<LabelDTO> labelDTOS = om.readValue(body, new TypeReference<>() { });

        var actual = labelDTOS.stream().map(labelMapper::map).toList();
        var expected = labelRepository.findAll();

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testFindAllUnauthorized() throws Exception {
        var request = get("/api/labels");

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testFindById() throws Exception {

        var request = get("/api/labels/" + testLabel.getId()).with(token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(testLabel.getName())
        );
    }

    @Test
    public void testCreateValid() throws Exception {
        LabelCreateDTO newLabel = labelMapper.forTest(Instancio.of(modelProvider.getLabelModel()).create());

        var request = post("/api/labels")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(newLabel));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        Label savedLabel = labelRepository.findByName(newLabel.getName()).get();

        assertEquals(savedLabel.getName(), newLabel.getName());
    }

    @Test
    public void testCreateInvalid() throws Exception {
        LabelCreateDTO newLabel = new LabelCreateDTO();
        newLabel.setName("Er");

        var request = post("/api/labels")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(newLabel));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDelete() throws Exception {
        var request = delete("/api/labels/" + testLabel.getId()).with(token);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        Label deletedLabel = labelRepository.findById(testLabel.getId()).orElse(null);
        assertThat(deletedLabel).isNull();
    }

    @Test
    public void testUpdate() throws Exception {
        Map<String, String> updateData = new HashMap<>();
        updateData.put("name", "UpdatedLabel");

        var request = put("/api/labels/" + testLabel.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(updateData));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        Label updatedLabel = labelRepository.findById(testLabel.getId()).get();

        assertEquals(updatedLabel.getName(), updateData.get("name"));
    }
}
