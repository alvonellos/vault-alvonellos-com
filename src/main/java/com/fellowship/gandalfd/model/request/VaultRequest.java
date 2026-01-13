package com.fellowship.gandalfd.model.request;

import com.fellowship.gandalfd.model.VaultEntity;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;


@Data
public class VaultRequest  {
    private final String key;
    private final String secret;

    @JsonGetter("key")
    public String getKey() {
        return this.key;
    }

    @JsonGetter("secret")
    public String getValue() {
        return this.secret;
    }

    public VaultRequest(VaultEntity vaultEntity) {
        this.key = vaultEntity.key;
        this.secret = vaultEntity.secret;
    }
}
