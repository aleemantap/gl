package com.project.gl.controller.api;



import com.project.gl.model.request.JournalRequest;
import com.project.gl.model.response.JournalResponse;
import com.project.gl.service.JournalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {
    @GetMapping("/api/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("GL API OK");
    }
}
