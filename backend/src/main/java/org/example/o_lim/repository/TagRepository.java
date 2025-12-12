package org.example.o_lim.repository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.o_lim.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByProjectId(Long projectId);

    boolean existsByNameAndProjectId(@NotBlank(message = "태그명은 필수 입니다.") @Size(max = 50, message = "태그명은 50자까지 가능합니다.") String name, Long projectId);
    boolean existsByColorAndProjectId(@NotBlank(message = "색상은 필수 입니다.") @Size(max = 50, message = "컬러명은 50자 까지 가능합니다.") String color, Long projectId);

    boolean existsByIdAndProjectId(Long tagId, Long id);
}
