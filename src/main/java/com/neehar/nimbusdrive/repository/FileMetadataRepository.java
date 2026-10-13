package com.neehar.nimbusdrive.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import com.neehar.nimbusdrive.entity.FileMetadata;
import com.neehar.nimbusdrive.entity.User;

@Repository
public interface FileMetadataRepository
        extends JpaRepository<FileMetadata, Long> {
	
	List<FileMetadata> findByOwner(User owner);
	
	Optional<FileMetadata> findByIdAndOwner(Long id, User owner);
	
	long countByOwner(User owner);
	
	List<FileMetadata> findByOwnerAndFileNameContainingIgnoreCase(
	        User owner,
	        String fileName);
	
	Optional<FileMetadata> findByIdAndOwnerEmail(
            Long id,
            String email);
	Optional<FileMetadata> findByShareToken(
	        String shareToken);

}