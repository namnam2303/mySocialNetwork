package com.example.MySocialNetwork.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

// Service is used to map field error to error message
@Service
public class MapValidationErrorService {
    public ResponseEntity<?> mapValidationError(BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            Map<String, String> errorMap= new HashMap<>();
            for(FieldError fieldError : bindingResult.getFieldErrors()) {
                errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
        }
        return null;
    }
}