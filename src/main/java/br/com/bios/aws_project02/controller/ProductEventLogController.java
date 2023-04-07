package br.com.bios.aws_project02.controller;

import br.com.bios.aws_project02.models.dto.ProductEventLogDTO;
import br.com.bios.aws_project02.repository.ProductEventLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api")
public class ProductEventLogController {

    @Autowired
    private ProductEventLogRepository productEventLogRepository;

    @GetMapping("/events")
    public List<ProductEventLogDTO> getAllEvents(){
        return StreamSupport.stream(productEventLogRepository.findAll().spliterator(), false)
                .map(ProductEventLogDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/events/{code}")
    public List<ProductEventLogDTO> getAllEvents(@PathVariable String code){
        return productEventLogRepository.findAllByPk(code).stream()
                .map(ProductEventLogDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/events/{code}/{event}")
    public List<ProductEventLogDTO> getAllEvents(@PathVariable String code, @PathVariable String event){
        return productEventLogRepository.findAllByPkAndSkStartsWith(code, event).stream()
                .map(ProductEventLogDTO::new)
                .collect(Collectors.toList());
    }
}
