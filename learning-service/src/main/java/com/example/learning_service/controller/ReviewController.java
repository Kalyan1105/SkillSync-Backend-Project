package com.example.learning_service.controller;

import com.example.learning_service.dto.ReviewDTO;
import com.example.learning_service.dto.UserResponseDTO;
import com.example.learning_service.entity.Review;
import com.example.learning_service.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // ADD REVIEW
    @PostMapping
    public UserResponseDTO add(@RequestBody ReviewDTO dto) {
        reviewService.addReview(dto);
        return new UserResponseDTO("Review submitted successfully");
    }



    // DELETE REVIEW
    @DeleteMapping("/{id}")
    public UserResponseDTO delete(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return new UserResponseDTO("Review deleted successfully");
    }
    @PutMapping("/{reviewId}")
    public UserResponseDTO updateReview(@PathVariable Long reviewId,
                                        @RequestBody ReviewDTO dto) {
        return reviewService.updateReview(reviewId, dto);
    }

    // GET
    @GetMapping("/mentor/{mentorId}")
    public List<Review> getByMentor(@PathVariable Long mentorId) {
        return reviewService.getReviewsByMentor(mentorId);
    }
}