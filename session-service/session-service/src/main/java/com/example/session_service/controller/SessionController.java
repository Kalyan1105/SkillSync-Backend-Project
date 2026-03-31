
package com.example.session_service.controller;
import com.example.session_service.dto.SessionRequestDTO;
import com.example.session_service.dto.SessionResponseDTO;
import com.example.session_service.entity.Session;
import com.example.session_service.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    // BOOK
    @PostMapping
    public SessionResponseDTO bookSession(@RequestBody SessionRequestDTO request) {
        return sessionService.bookSession(request);
    }

    @PutMapping("/{id}/accept")
    public SessionResponseDTO accept(@PathVariable Long id) {
        return sessionService.acceptSession(id);
    }

    @PutMapping("/{id}/reject")
    public SessionResponseDTO reject(@PathVariable Long id) {
        return sessionService.rejectSession(id);
    }

    // GET
    @GetMapping("/learner")
    public List<Session> getByLearner() {
        return sessionService.getByLearner();
    }

    @GetMapping("/mentor")
    public List<Session> getByMentor() {
        return sessionService.getByMentor();
    }

}
