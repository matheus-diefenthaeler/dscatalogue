package com.devsuperior.dscatalog.resources.exceptions;

import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

@ControllerAdvice
public class ResourceExceptionHandler {

  HttpStatus status = HttpStatus.I_AM_A_TEAPOT;

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<StandardError> entityNotFound(
          ResourceNotFoundException e, HttpServletRequest request) {
    status = HttpStatus.NOT_FOUND;
    var error = new StandardError();
    error.setTimestamp(Instant.now());
    error.setStatus(status.value());
    error.setError("Resource not found!");
    error.setMessage(e.getMessage());
    error.setPath(request.getRequestURI());
    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(DataBaseException.class)
  public ResponseEntity<StandardError> dataBase(
          DataBaseException e, HttpServletRequest request) {
    status = HttpStatus.BAD_REQUEST;
    var error = new StandardError();
    error.setTimestamp(Instant.now());
    error.setStatus(status.value());
    error.setError("Database Exception");
    error.setMessage(e.getMessage());
    error.setPath(request.getRequestURI());
    return ResponseEntity.status(status).body(error);
  }



}
