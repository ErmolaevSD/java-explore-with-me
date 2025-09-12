package ru.practicum.ewm.repository.comment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.entity.comment.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByAuthorId(Long authorId, Pageable pageable);

    List<Comment> findAllByEventId(Long eventId, Pageable pageable);

}
