package com.fellowship.gandalfd.model.convertor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.security.InvalidKeyException;
import java.security.Key;
import java.util.Base64;

@Component
@Converter
@Slf4j
public class AttributeEncryptor implements AttributeConverter<String, String> {

    Logger log = LoggerFactory.getLogger(AttributeEncryptor.class);
    private static final String AES = "AES";
    private static final String SECRET = System.getenv("VAULT_DATABASE_SECRET");

    private final Key key;
    private final Cipher cipher;

    public AttributeEncryptor() throws Exception {
        key = new SecretKeySpec(SECRET.getBytes(), AES);
        cipher = Cipher.getInstance(AES);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64
                    .getEncoder()
                    .encodeToString(
                        cipher
                            .doFinal(
                                    attribute.getBytes()
                            )
                    );
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            log.info("Encryption error: ", e);
            throw new IllegalStateException(e);
        } catch (Exception e) {
            log.error("Encryption error: ", e);
            throw new Thread.UncaughtExceptionHandler(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new IllegalStateException(e);
        }
    }
}