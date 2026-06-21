package com.example.demo.Repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL AND u.isActive = true")
    List<User> findAllActive();

    Optional<User> findByUserCode(String userCode);

    Optional<User> findByDocNumber(String docNumber);

    Optional<User> findByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.docNumber = :docNumber")
    boolean existsByDocNumber(String docNumber);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role.id = :roleId AND u.deletedAt IS NULL AND u.isActive = true")
    List<User> findByRoleId(Long roleId);

    @Query("SELECT u FROM User u WHERE u.store.id = :storeId AND u.deletedAt IS NULL AND u.isActive = true")
    List<User> findByStoreId(Long storeId);
}
