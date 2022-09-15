package com.kafkamgt.uiapi.repository;

import com.kafkamgt.uiapi.dao.Env;
import com.kafkamgt.uiapi.dao.EnvID;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface EnvRepo extends CrudRepository<Env, EnvID> {
  Optional<Env> findById(EnvID id);

  List<Env> findAllByTypeAndTenantId(String type, int tenantId);

  List<Env> findAllByType(String type);

  List<Env> findAllByClusterIdAndTenantId(Integer clusterId, int tenantId);

  List<Env> findAllByTenantId(int tenantId);
}
