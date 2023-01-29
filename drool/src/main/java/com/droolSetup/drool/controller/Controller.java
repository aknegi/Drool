package com.droolSetup.drool.controller;

import com.droolSetup.drool.dto.DTO;
import com.droolSetup.drool.dto.RequestDTO;
import com.droolSetup.drool.service.DroolServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class Controller {

    @Autowired
    DroolServiceImpl droolService;

    @PostMapping("/get-seats")
    public DTO get(@RequestBody DTO dto) throws IOException {
        return droolService.getSeats(dto);
    }


    @PostMapping("/reload-file/{fileType}/{reload}")
    public String reload(@PathVariable String fileType,@PathVariable int reload) throws IOException {
       return droolService.reload(fileType, reload);
    }

    @PostMapping("/upload")
    public RequestDTO upload(@RequestBody RequestDTO requestDTO) throws IOException {
       return  requestDTO;
    }
}
