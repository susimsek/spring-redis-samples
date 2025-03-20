package io.github.susimsek.springredissamples.exception;

import io.github.susimsek.springredissamples.ratelimiter.RateLimitExceededException;
import io.github.susimsek.springredissamples.ratelimiter.RateLimitHeaders;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ProblemDetail> handleRateLimitExceededException(RateLimitExceededException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.TOO_MANY_REQUESTS);
        problemDetail.setTitle("Too Many Requests");
        problemDetail.setDetail("The request rate limit has been exceeded. Please try again later.");

        HttpHeaders headers = new HttpHeaders();
        String waitTimeStr = String.valueOf(ex.getWaitTime());
        headers.add(RateLimitHeaders.RETRY_AFTER, waitTimeStr);
        headers.add(RateLimitHeaders.RATE_LIMIT_RESET, String.valueOf(ex.getResetTimestamp()));
        headers.add(RateLimitHeaders.RATE_LIMIT_LIMIT, String.valueOf(ex.getLimitForPeriod()));
        headers.add(RateLimitHeaders.RATE_LIMIT_REMAINING, String.valueOf(ex.getAvailablePermissions()));

        return new ResponseEntity<>(problemDetail, headers, HttpStatus.TOO_MANY_REQUESTS);
    }
}
