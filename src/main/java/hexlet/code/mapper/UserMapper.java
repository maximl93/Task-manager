package hexlet.code.mapper;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Mapper(
        uses = {JsonNullableMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Mapping(target = "encodedPassword", source = "password")
    public abstract User map(UserCreateDTO createData);
    public abstract UserDTO map(User user);
    public abstract List<UserDTO> map(List<User> users);
    public abstract void update(UserUpdateDTO updateData, @MappingTarget User user);

    @BeforeMapping
    public void encryptPassword(UserCreateDTO createData) {
        String password = createData.getPassword();
        createData.setPassword(passwordEncoder.encode(password));
    }

    @Mapping(target = "password", source = "encodedPassword")
    public abstract UserCreateDTO forTest(User user);

    public abstract User map(UserDTO dto);
}
