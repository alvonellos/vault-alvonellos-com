package com.fellowship.gandalfd.model;

import com.fellowship.gandalfd.model.convertor.AttributeEncryptor;
import com.fellowship.gandalfd.model.request.VaultRequest;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "ve")
@JsonPropertyOrder({"key", "secret"})
public class VaultEntity {
  @Id @JsonProperty public String key;
  @JsonProperty @Convert(converter = AttributeEncryptor.class)
  public String secret;

  public VaultEntity() {}

  public VaultEntity(String key, String secret) {
    this.key = key;
    this.secret = secret;
  }

  public VaultEntity(VaultRequest request) {
    this.key = request.getKey();
    this.secret = request.getSecret();
  }
}
