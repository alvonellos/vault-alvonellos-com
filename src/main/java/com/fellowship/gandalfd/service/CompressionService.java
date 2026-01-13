package com.fellowship.gandalfd.service;

import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Service
public class CompressionService {

    public byte[] compressString(String inputString) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream)) {
            gzipOutputStream.write(inputString.getBytes());
        }
        return outputStream.toByteArray();
    }

    public String decompressString(byte[] compressedBytes) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(compressedBytes);
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipInputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            return outputStream.toString();
        }
    }
}
