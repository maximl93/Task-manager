package hexlet.code.service;

import hexlet.code.dto.taskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskStatus.TaskStatusDTO;
import hexlet.code.dto.taskStatus.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatusService {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    public TaskStatusDTO create(TaskStatusCreateDTO createData) {
        TaskStatus newTaskStatus = taskStatusMapper.map(createData);
        taskStatusRepository.save(newTaskStatus);
        return taskStatusMapper.map(newTaskStatus);
    }

    public TaskStatusDTO findById(Long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status doesn't exist"));
        return taskStatusMapper.map(taskStatus);
    }

    public List<TaskStatusDTO> findAll() {
        return taskStatusMapper.map(taskStatusRepository.findAll());
    }

    public TaskStatusDTO update(Long id, TaskStatusUpdateDTO updateData) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status doesn't exist"));
        taskStatusMapper.update(updateData, taskStatus);
        taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    public void deleteById(Long id) {
        taskStatusRepository.deleteById(id);
    }
}
