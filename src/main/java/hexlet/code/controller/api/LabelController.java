package hexlet.code.controller.api;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.service.LabelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
public class LabelController {

    @Autowired
    private LabelService labelService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public LabelDTO create(@Valid @RequestBody LabelCreateDTO createData) {
        return labelService.create(createData);
    }

    @GetMapping("/{id}")
    public LabelDTO findById(@PathVariable Long id) {
        return labelService.findById(id);
    }

    @GetMapping("")
    public ResponseEntity<List<LabelDTO>> findAll() {
        List<LabelDTO> labelDTOS = labelService.findAll();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(labelDTOS.size()))
                .body(labelDTOS);
    }

    @PutMapping("/{id}")
    public LabelDTO update(
            @PathVariable Long id,
            @Valid @RequestBody LabelUpdateDTO updateData
            ) {
        return labelService.update(id, updateData);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        labelService.deleteById(id);
    }
}
