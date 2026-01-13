package com.fellowship.gandalfd.controller;

import com.fellowship.gandalfd.model.request.VaultRequest;
import com.fellowship.gandalfd.repository.VaultRepository;
import com.fellowship.gandalfd.service.VaultService;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static com.fellowship.gandalfd.mock.MockRequest.*;

@SpringBootTest
public class VaultControllerTest {
  @Mock private VaultRepository vaultRepository;
  @Mock private VaultService vaultService;
  private VaultController vaultController;

  @BeforeClass
  void setUp() {
    this.vaultService = mock(VaultService.class);
    this.vaultRepository = mock(VaultRepository.class);
    this.vaultController = new VaultController(vaultRepository, vaultService);
    ReflectionTestUtils.setField(vaultController, "vaultPath", "/v1/vault/");
  }

  @Test
  void getAllEntitiesTest() {
    when(vaultRepository.findAllByOrderByKeyAsc())
        .thenReturn(Collections.singletonList(mockVaultEntity()));
    vaultController.getAllEntities(mockHeaders(), mockToken(),true);
    verify(vaultService).findAllSecrets(mockToken());
  }

  @Test
  void getEntityBlankNamespaceTest() {
    when(vaultRepository.findByKeyStartsWith(any()))
            .thenReturn(Collections.singletonList(mockVaultEntity()));
    ResponseEntity entity = vaultController.getEntity(mockHeaders(), mockToken(), mockBlankNamespace(), mockRequest());
    verify(vaultService).findByKey(mockRequest().getRequestURI(), mockToken());
  }
  @Test
  void getEntityNonBlankNamespaceTest() {
    when(vaultRepository.findByKeyStartsWith(any()))
            .thenReturn(Collections.singletonList(mockVaultEntity()));
    ResponseEntity entity = vaultController.getEntity(mockHeaders(), mockToken(), mockNamespace(), mockRequestNoNameSpace());
    verify(vaultService).findByKey(mockRequest().getRequestURI() + "/" + mockVaultEntity().key, mockToken());
  }


  @Test
  void postEntity() {
    VaultRequest request = new VaultRequest(mockVaultEntity());
    when(vaultService.postEntity(
            request,
            mockToken(),
            mockRequestNoNameSpace().getRequestURI()
    )).thenReturn(new ResponseEntity<>(request, HttpStatus.OK));
    ResponseEntity entity = vaultController.postEntity(mockHeaders(), mockToken(), "true", request, mockRequestNoNameSpace());
    verify(vaultService).postEntity(request, mockToken(), mockRequestNoNameSpace().getRequestURI());
  }

  @Test
  void clearEntities() {
    when(vaultService.clearEntities(mockToken())).thenReturn(new ResponseEntity<>(true, HttpStatus.OK));
    ResponseEntity response = vaultController.clearEntities(mockHeaders(), mockToken());
    verify(vaultService).clearEntities(mockToken());
  }
}
