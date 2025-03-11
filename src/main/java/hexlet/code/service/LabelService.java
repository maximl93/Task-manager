package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper labelMapper;

    public LabelDTO findById(Long id) {
         Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));
         return labelMapper.map(label);
    }

    public List<LabelDTO> findAll() {
        return labelMapper.map(labelRepository.findAll());
    }

    public LabelDTO create(LabelCreateDTO createData) {
        Label newLabel = labelMapper.map(createData);
        labelRepository.save(newLabel);
        return labelMapper.map(newLabel);
    }

    public LabelDTO update(Long id, LabelUpdateDTO updateData) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));
        labelMapper.update(updateData, label);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

    public void deleteById(Long id) {
        labelRepository.deleteById(id);
    }
}
