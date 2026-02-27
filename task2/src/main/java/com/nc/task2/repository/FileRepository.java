package com.nc.task2.repository;

import com.nc.task2.entity.FileEntity;
import com.nc.task2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

    // Get all files uploaded by a specific user
    List<FileEntity> findByUser(User user);

    // Find a file by id and user (to prevent accessing others' files)
    Optional<FileEntity> findByIdAndUser(Long id, User user);
}