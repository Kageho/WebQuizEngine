package com.myHyperskillProject.webQuizEngine.quiz;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.myHyperskillProject.webQuizEngine.complitions.Completed;
import com.myHyperskillProject.webQuizEngine.security.User;
import com.sun.istack.NotNull;


import javax.persistence.*;

import java.util.Arrays;
import java.util.List;


@Entity
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int[] answer = new int[]{};

    @NotNull(message = "Options are required")
    @Size(min = 2, message = "At least 2 options are required")
    private String[] options;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Text is required")
    private String text;

    @JsonIgnore
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<Completed> completed;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Quiz() {
    }


    public void setId(long id) {
        this.id = id;
    }


    public long getId() {
        return id;
    }

    public void setAnswer(int[] answer) {
        if (answer != null)
            this.answer = answer;
        Arrays.sort(this.answer);
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }


    public int[] getAnswer() {
        return answer;
    }

    public String[] getOptions() {
        return options;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "[" + Arrays.toString(options) + " " + text + " " + title + " " + id + "]";
    }

}

