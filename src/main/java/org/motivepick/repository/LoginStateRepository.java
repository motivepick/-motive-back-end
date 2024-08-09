package org.motivepick.repository;

import org.motivepick.domain.entity.LoginStateEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

interface LoginStateRepository extends PagingAndSortingRepository<LoginStateEntity, Long> {

    Optional<LoginStateEntity> findByStateUuid(String uuid);

    Optional<LoginStateEntity> deleteByStateUuid(String uuid);
}
