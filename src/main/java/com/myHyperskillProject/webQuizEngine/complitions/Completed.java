package com.myHyperskillProject.webQuizEngine.complitions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.myHyperskillProject.webQuizEngine.quiz.Quiz;
import com.myHyperskillProject.webQuizEngine.security.User;


import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Completed {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long solutionId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    private Long id;
    private LocalDateTime completedAt;

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public Completed(Long id, LocalDateTime completedAt, User user, Quiz quiz) {
        this.id = id;
        this.completedAt = completedAt;
        this.user = user;
        this.quiz = quiz;
    }

    public Completed() {
    }

    @JsonIgnore
    public Long getSolutionId() {
        return solutionId;
    }

    public void setSolutionId(Long solutionId) {
        this.solutionId = solutionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("[id=").append(solutionId).append(", completedAt=").append(completedAt).append("]").toString();
    }
}
