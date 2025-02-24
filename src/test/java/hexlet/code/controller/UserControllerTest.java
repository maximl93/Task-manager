package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.UserCreateDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public User generateRandomUser() {
        return Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getEncodedPassword), () ->faker.internet().password())
                .create();
    }

    @Test
    public void testCreate() throws Exception {
        UserCreateDTO dto = userMapper.forTest(generateRandomUser());


        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        User savedUser = userRepository.findByEmail(dto.getEmail()).get();

        assertThat(savedUser.getFirstName()).isEqualTo(dto.getFirstName());
    }

    @Test
    public void testFindById() throws Exception {
        User data = generateRandomUser();
        userRepository.save(data);

        var request = get("/api/users/" + data.getId());

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("firstName").isEqualTo(data.getFirstName()),
                v -> v.node("email").isEqualTo(data.getEmail())
        );
    }

    @Test
    public void testFindAll() throws Exception{
        User data1 = generateRandomUser();
        User data2 = generateRandomUser();
        User data3 = generateRandomUser();
        userRepository.save(data1);
        userRepository.save(data2);
        userRepository.save(data3);

        var request = get("/api/users");

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    public void testUpdate() throws Exception{
        User data = generateRandomUser();
        userRepository.save(data);

        Map<String, String> updateData = new HashMap<>();
        updateData.put("firstName", "Maksim");

        var request = put("/api/users/" + data.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(updateData));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(data.getId()).get();

        assertThat(updatedUser.getFirstName()).isEqualTo(updateData.get("firstName"));
    }

    @Test
    public void testDeleteById() throws Exception{
        User data = generateRandomUser();
        userRepository.save(data);

        var request = delete("/api/users/" + data.getId());

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        User deletedUser = userRepository.findById(data.getId()).orElse(null);
        assertThat(deletedUser).isNull();
    }
}
