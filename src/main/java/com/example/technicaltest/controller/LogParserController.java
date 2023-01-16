package com.example.technicaltest.controller;

import com.example.technicaltest.service.LogParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/parser")
public class LogParserController {
    @Autowired
    private LogParserService logParserService;

    @GetMapping("/execute/{fileName}")
    public String getResult(@PathVariable final String fileName) {
        return logParserService.parseLog(fileName);
    }
}
