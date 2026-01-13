package com.fellowship.gandalfd.service;

import com.fellowship.gandalfd.model.VaultEntity;
import com.fellowship.gandalfd.model.request.VaultRequest;
import com.fellowship.gandalfd.repository.VaultRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/** VaultService service layer implementation */
@Service
@Log
public class VaultService implements InitializingBean {

  private final VaultRepository vaultRepository;
  // Load the vault path from the properties file
  @Value("${com.alvonellos.vault.vaultpath}")
  String vaultPath;

  /**
   * Autowired VaultService Constructor
   *
   * @param vaultRepository the vault session to use
   */
  public VaultService(@Autowired VaultRepository vaultRepository) {
    this.vaultRepository = vaultRepository;
  }

  /**
   * Add an entity to the vault
   *
   * @param entity the entity to add
   * @param token the token to check for authentication
   * @param requestURI the key path (obtained from requestURI)
   * @return response
   */
  public ResponseEntity<VaultRequest> postEntity(
          VaultRequest entity, String token, String requestURI) {
    // TODO Implement authentication
    entity.setKey(requestURI + "/" + entity.getKey());
    vaultRepository.save(new VaultEntity(entity));
    return new ResponseEntity<>(entity, HttpStatus.OK);
  }

  /**
   * Finds a response entity by key
   *
   * @param requestURI this is the key
   * @param token
   * @return
   */
  public ResponseEntity<Object> findByKey(String requestURI, String token) {
    List<VaultEntity> entity = vaultRepository.findByKeyStartsWith(requestURI);
    if (entity.isEmpty()) {
      return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
    }
    // TODO: implement authentication
    if (entity != null) {
      return new ResponseEntity<Object>(entity, HttpStatus.OK);
    } else {
      return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    log.info(
        String.format(
            "Loaded from properties com.alvonellos.vaultemulator.vaultpath: %s", vaultPath));
  }

  /**
   * Clear all entries from the vault
   *
   * @param token the authentication token
   * @return true if we cleared something from the database
   */
  public ResponseEntity<Boolean> clearEntities(String token) {
    // TODO: implement authentication
    long count = vaultRepository.count();
    vaultRepository.deleteAll();
    long countAfter = vaultRepository.count();
    return new ResponseEntity<>(countAfter < count, HttpStatus.OK);
  }

  public ResponseEntity<List<VaultEntity>> findAllSecrets(String token) {
    //TODO: implement authentication
    List<VaultEntity> entities = vaultRepository.findAllByOrderByKeyAsc();
    if (entities.isEmpty()) {
      return new ResponseEntity<>(
          Collections.singletonList(new VaultEntity()), HttpStatus.NOT_FOUND);
    } else {
      return new ResponseEntity<>(entities, HttpStatus.OK);
    }
  }
}
