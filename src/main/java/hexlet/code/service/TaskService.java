package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.specification.TaskSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskSpecification taskSpecification;

    @Autowired
    private TaskMapper taskMapper;

    public TaskDTO create(TaskCreateDTO createdData) {
        Task newTask = taskMapper.map(createdData);
        taskRepository.save(newTask);
        return taskMapper.map(newTask);
    }

    public TaskDTO findById(Long id) {
        return taskMapper.map(taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found")));
    }

    public List<TaskDTO> findAll(TaskParamsDTO params) {
        Specification<Task> spec = taskSpecification.build(params);
        List<Task> tasks = taskRepository.findAll(spec);
        return taskMapper.map(tasks);
    }

    public TaskDTO update(Long id, TaskUpdateDTO updateData) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        taskMapper.update(updateData, task);
        taskRepository.save(task);
        return taskMapper.map(task);
    }

    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }
}
