package com.example.learning_service.LearningServiceTest;

import com.example.learning_service.client.UserClient;
import com.example.learning_service.dto.ReviewDTO;
import com.example.learning_service.dto.UserInternalDTO;
import com.example.learning_service.entity.Review;
import com.example.learning_service.repository.ReviewRepository;
import com.example.learning_service.service.ReviewService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserClient userClient;

    @Spy
    private org.modelmapper.ModelMapper modelMapper = new org.modelmapper.ModelMapper();

    @InjectMocks
    private ReviewService reviewService;

    private void mockSecurity(String email) {
        var auth = mock(org.springframework.security.core.Authentication.class);
        when(auth.getName()).thenReturn(email);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void addReview_success() {
        mockSecurity("a@gmail.com");

        when(userClient.getUserByEmail(any()))
                .thenReturn(new UserInternalDTO(1L,"",true));
        when(userClient.getUserById(2L))
                .thenReturn(new UserInternalDTO(2L,"ROLE_MENTOR",true));

        reviewService.addReview(new ReviewDTO(2L,5,"good"));

        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void addReview_invalidRating() {
        assertThrows(RuntimeException.class,
                () -> reviewService.addReview(new ReviewDTO(2L,6,"bad")));
    }

    @Test
    void addReview_invalidMentor() {
        mockSecurity("a@gmail.com");

        when(userClient.getUserByEmail(any()))
                .thenReturn(new UserInternalDTO(1L,"",true));
        when(userClient.getUserById(2L))
                .thenReturn(new UserInternalDTO(2L,"ROLE_USER",true));

        assertThrows(RuntimeException.class,
                () -> reviewService.addReview(new ReviewDTO(2L,5,"bad")));
    }

    @Test
    void updateReview_success() {
        mockSecurity("a@gmail.com");

        when(userClient.getUserByEmail(any()))
                .thenReturn(new UserInternalDTO(1L,"ROLE_USER",true));

        Review review = new Review(1L,2L,1L,5,"ok");

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.updateReview(1L,new ReviewDTO(2L,4,"better"));

        verify(reviewRepository).save(review);
    }

    @Test
    void deleteReview_success() {
        Review review = new Review();

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.deleteReview(1L);

        verify(reviewRepository).delete(review);
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }
}
