package com.fellowship.gandalfd.controller;

import com.fellowship.gandalfd.model.VaultEntity;
import com.fellowship.gandalfd.model.request.VaultRequest;
import com.fellowship.gandalfd.model.response.ErrorResponse;
import com.fellowship.gandalfd.repository.VaultRepository;
import com.fellowship.gandalfd.service.VaultService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("${com.fellowship.gandalf.vaultpath}")
@Log
public class VaultController implements InitializingBean {

  @Value("${com.fellowship.gandalf.vaultpath}")
  String vaultPath;

  private final VaultRepository vaultRepository;
  private final VaultService vaultService;

  public VaultController(
      @Autowired VaultRepository vaultRepository, @Autowired VaultService vaultService) {
    this.vaultRepository = vaultRepository;
    this.vaultService = vaultService;
  }

  /**
   * Lists all the entities in the vault
   *
   * @param headers the http headers
   * @param token the authentication token
   * @param list the mode (ignored)
   * @return ResponseEntity of type list with all entries in the vault
   */
  @GetMapping(value = "", produces = "application/json")
  public ResponseEntity<List<VaultEntity>> getAllEntities(
      @RequestHeader HttpHeaders headers,
      @RequestHeader(name = "X-Vault-Token") String token,
      @RequestHeader(name = "list", required = false) boolean list) {
    log.info(
        String.format(
            "%s provided token: %s for key %s list: %s",
            headers.getHost(), token, "/vault/", list));
    return vaultService.findAllSecrets(token);
  }

  /**
   * Gets an entity or list of entities from the vault
   *
   * @param headers the http headers
   * @param token authentication token
   * @param namespace the namespace to use, can be included in the path also
   * @param request the servlet request
   * @return a vault entry or list of vault entry
   */
  @GetMapping(value = "**", produces = "application/json")
  public ResponseEntity<Object> getEntity(
      @RequestHeader HttpHeaders headers,
      @RequestHeader(name = "X-Vault-Token") String token,
      @RequestHeader(name = "X-Vault-Namespace", required = false) String namespace,
      HttpServletRequest request) {

    log.info(
        String.format(
            "%s provided token: %s for key %s and namespace %s",
            headers.getHost(), token, request.getRequestURI(), namespace == null));

    // We can provide the namespace or not provide the namespace, if there's a namespace
    // parameter we need to calculate it.
    if (namespace == null || namespace.equals("")) {
      return vaultService.findByKey(request.getRequestURI(), token);
    } else {
      //String calculatedPath = vaultPath + namespace + "/" + request.getRequestURI().substring(vaultPath.length());
      String calculatedPath = vaultPath + request.getRequestURI().substring(vaultPath.length());

      // Sometimes there's a problem where people do this, so let's just fix it.
      if (calculatedPath.contains("//")) {
        log.info("calculated path contains double //, correcting");
        calculatedPath = calculatedPath.replace("//", "/");
      }

      log.info(
          String.format(
              "Provided namespace %s so calculated path is: %s", namespace, calculatedPath));

      return vaultService.findByKey(calculatedPath, token);
    }
  }

  /**
   * Adds / updates an entity
   *
   * @param headers the http headers
   * @param token the authentication token
   * @param vaultRequest whether or not this is a vault request, ignored
   * @param entity the vault entity to add
   * @param request the servlet request
   * @return
   */
  @PostMapping(value = "**", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
  public ResponseEntity<VaultRequest> postEntity(
      @RequestHeader HttpHeaders headers,
      @RequestHeader(name = "X-Vault-Token") String token,
      @RequestHeader(name = "X-Vault-Request", required = false) String vaultRequest,
      @RequestBody VaultRequest entity,
      HttpServletRequest request) {

    log.info(
        String.format(
            "%s provided token %s for vault request %s with entity key: %s, value: %s",
            headers.getHost(), token, vaultRequest, request.getRequestURI(), entity));
    try {
      return vaultService.postEntity(entity, token, request.getRequestURI());
    } catch (IllegalArgumentException e) {
      log.info("Bad request creating vault entity");
      return new ResponseEntity(new ErrorResponse("Bad request creating vault entity"), HttpStatus.BAD_REQUEST);
    }
  }

    /**
     * Clears the vault of all entries
     * @param headers the HttpHeaders
     * @param token the authorization token
     * @return true if it cleared, false otherwise
     */
  @PutMapping(value = "clear")
  public ResponseEntity<Boolean> clearEntities(@RequestHeader HttpHeaders headers, @RequestHeader(name = "X-Vault-Token") String token) {
      log.info(
              String.format(
                      "%s provided token %s for vault request clear",
                      headers.getHost(), token));
      return vaultService.clearEntities(token);
  }

  /**
   * This is called after the properties are loaded from the application.properties file
   *
   * @throws Exception
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    log.info(
        String.format(
            "Loaded from properties com.fellowship.gandalfd.vaultpath: %s", vaultPath));
  }
}
