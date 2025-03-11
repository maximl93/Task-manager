package hexlet.code.dto.task;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TaskDTO {
    private Long id;
    private Long index;
    private LocalDate createdAt;
    private Long assigneeId;
    private String title;
    private String content;
    private String status;
    private List<Long> taskLabelIds;
}
