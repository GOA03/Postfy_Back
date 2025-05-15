package com.auer.postfy.repository;

import com.auer.postfy.entity.Email;
import com.auer.postfy.enums.EmailStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface EmailRepository extends CrudRepository<Email, Long> {

    Optional<Email> findById(Long id);

    Optional<Email> findBySenderId(Long senderId);

    List<Email> findByRecipientId(Long recipientId);

    List<Email> findBySenderIdAndStatus(Long id, EmailStatus emailStatus);
}
