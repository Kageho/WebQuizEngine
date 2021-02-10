package com.myHyperskillProject.webQuizEngine;



import com.myHyperskillProject.webQuizEngine.complitions.Completed;
import com.myHyperskillProject.webQuizEngine.complitions.CompletedRepository;
import com.myHyperskillProject.webQuizEngine.quiz.Quiz;
import com.myHyperskillProject.webQuizEngine.quiz.QuizRepository;
import com.myHyperskillProject.webQuizEngine.security.User;
import com.myHyperskillProject.webQuizEngine.security.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class QuizController {
    private static final String SERVICE_WARNING_MESSAGE = "There is no a such quiz";

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompletedRepository completedRepository;

    @PostMapping(path = "register")
    public void register(@Valid @RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }


    @PostMapping(path = "quizzes/{id}/solve")
    public String solve(@PathVariable long id, @RequestBody Map<String, int[]> answer, @AuthenticationPrincipal User user) {
        if (!quizRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, SERVICE_WARNING_MESSAGE);
        }
        Quiz quiz = quizRepository.findById(id).get();
        boolean result = answer.get("answer") == null && quiz.getAnswer() == null;
        if (answer.get("answer") != null && !result) {
            Arrays.sort(answer.get("answer"));
            result = Arrays.equals(answer.get("answer"), quiz.getAnswer());
        }
        if (result) {
            completedRepository.save(new Completed(quiz.getId(), LocalDateTime.now(), user, quiz));
        }
        return result ? "{\"success\":true,\"feedback\":\"Congratulations, you're right!\"}"
                : "{\"success\":false,\"feedback\":\"Wrong answer! Please, try again.\"}";
    }

    @PostMapping(path = "quizzes")
    public Quiz createQuiz(@Valid @RequestBody Quiz quiz, @AuthenticationPrincipal User user) {
        quiz.setUser(user);
        return quizRepository.save(quiz);
    }

    @GetMapping(path = "quizzes/{id}")
    public Quiz getQuiz(@PathVariable long id) {
        if (!quizRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, SERVICE_WARNING_MESSAGE);
        }
        return quizRepository.findById(id).get();
    }

    // returns page of quizzes, size and number of page might be customized
    @GetMapping(path = "quizzes")
    public ResponseEntity<Page<Quiz>> getQuizzes(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue =
            "10") Integer size) {
        Page<Quiz> page1 = quizRepository.findAll(PageRequest.of(page, size, Sort.by("id")));
        return new ResponseEntity<>(page1, new HttpHeaders(), HttpStatus.OK);
    }

    // returns page of completed quizzes for certain user, size and number of page might be customized
    @GetMapping(path = "quizzes/completed")
    public ResponseEntity<Page<Completed>> getCompleted(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue =
            "10") Integer size, @AuthenticationPrincipal User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("completedAt").descending());
        Page<Completed> page1 = completedRepository.findAllByUser(user, pageable);
        return new ResponseEntity<>(page1, new HttpHeaders(), HttpStatus.OK);
    }

    @DeleteMapping(path = "quizzes/{id}")
    public void delete(@PathVariable long id, @AuthenticationPrincipal User user) {
        if (!quizRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, SERVICE_WARNING_MESSAGE);
        }
        Quiz quiz = quizRepository.findById(id).get();
        if (quiz.getUser().getId() == user.getId()) {
            quizRepository.deleteById(id);
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    // creator can update his/her quiz
    @PutMapping(path = "quizzes/{id}")
    public Quiz updateQuiz(@PathVariable long id, @AuthenticationPrincipal User user, @RequestBody Quiz quiz) {
        if (!quizRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, SERVICE_WARNING_MESSAGE);
        }
        Quiz quiz1 = quizRepository.findById(id).get();
        if (user.getId() == quiz1.getUser().getId()) {
            if (quiz.getAnswer() != null) {
                quiz1.setAnswer(quiz.getAnswer());

            }
            if (quiz.getOptions() != null) {
                quiz1.setOptions(quiz.getOptions());

            }
            if (quiz.getText() != null) {
                quiz1.setText(quiz.getText());

            }
            if (quiz.getTitle() != null) {
                quiz1.setTitle(quiz.getTitle());
            }
            return quizRepository.save(quiz1);
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
}
