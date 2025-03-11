package hexlet.code.mapper;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "slugToModel")
    @Mapping(target = "taskLabels", source = "taskLabelIds", qualifiedByName = "labelToModel")
    public abstract Task map(TaskCreateDTO createData);

    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "status", source = "taskStatus.slug")
    @Mapping(target = "taskLabelIds", source = "taskLabels", qualifiedByName = "modelToLabel")
    public abstract TaskDTO map(Task task);

    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "slugToModel")
    @Mapping(target = "taskLabels", source = "taskLabelIds", qualifiedByName = "labelToModel")
    public abstract void update(TaskUpdateDTO updateData, @MappingTarget Task task);

    public abstract List<TaskDTO> map(List<Task> tasks);

    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "slugToModel")
    public abstract Task map(TaskDTO dto);

    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "status", source = "taskStatus.slug")
    public abstract TaskCreateDTO forTest(Task testTask);

    @Named("slugToModel")
    public TaskStatus toModel(String slug) {
        return taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Status not exists"));
    }

    @Named("labelToModel")
    public List<Label> getLabels(List<Long> taskLabelIds) {
        return taskLabelIds.stream()
                .map(labelId -> labelRepository.findById(labelId)
                        .orElseThrow(() -> new ResourceNotFoundException("Label " + labelId + " not exists")))
                .toList();
    }

    @Named("modelToLabel")
    public List<Long> getLabelsName(List<Label> taskLabels) {
        return taskLabels.stream()
                .map(Label::getId)
                .toList();
    }
}
