package com.example.learning_service.service;

import com.example.learning_service.client.UserClient;
import com.example.learning_service.dto.ReviewDTO;
import com.example.learning_service.dto.UserInternalDTO;
import com.example.learning_service.dto.UserResponseDTO;
import com.example.learning_service.entity.Review;
import com.example.learning_service.exception.InvalidRequestException;
import com.example.learning_service.exception.ResourceNotFoundException;
import com.example.learning_service.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.modelmapper.ModelMapper;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserClient userClient;
    private final ModelMapper modelMapper;

    // ADD REVIEW
    public void addReview(ReviewDTO dto) {

        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new InvalidRequestException("Rating must be between 1 and 5");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserInternalDTO user = userClient.getUserByEmail(email);

        UserInternalDTO mentor = userClient.getUserById(dto.getMentorId());

        if (!"ROLE_MENTOR".equals(mentor.getRole())) {
            throw new InvalidRequestException("Invalid mentor");
        }

        Review review = modelMapper.map(dto, Review.class);
        review.setUserId(user.getUserId());

        reviewRepository.save(review);
    }

    // UPDATE
    public UserResponseDTO updateReview(Long reviewId, ReviewDTO dto) {

        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserInternalDTO user = userClient.getUserByEmail(email);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // 🔐 OWNER OR ADMIN CHECK
        if (!review.getUserId().equals(user.getUserId()) &&
                !"ROLE_ADMIN".equals(user.getRole())) {
            throw new RuntimeException("Not authorized to update this review");
        }

        // ✅ UPDATE ONLY PROVIDED FIELDS (safe update)
        if (dto.getRating() != null) {
            review.setRating(dto.getRating());
        }

        if (dto.getComment() != null && !dto.getComment().isBlank()) {
            review.setComment(dto.getComment());
        }

        reviewRepository.save(review);

        return new UserResponseDTO("Review updated successfully");
    }

    // DELETE
    public void deleteReview(Long id) {

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        reviewRepository.delete(review);
    }

    // GET
    public List<Review> getReviewsByMentor(Long mentorId) {
        return reviewRepository.findByMentorId(mentorId);
    }
}