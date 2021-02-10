package com.myHyperskillProject.webQuizEngine.complitions;


import com.myHyperskillProject.webQuizEngine.security.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CompletedRepository extends PagingAndSortingRepository<Completed, Long> {
    Page<Completed> findAllByUser(User user, Pageable pageable);
}
