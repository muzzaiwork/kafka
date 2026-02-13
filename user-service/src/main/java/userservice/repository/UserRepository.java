package userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import userservice.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
