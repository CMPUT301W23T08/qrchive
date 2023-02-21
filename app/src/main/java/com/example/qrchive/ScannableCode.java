package com.example.qrchive;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;

public class ScannableCode {
    private String code;
    private String hash;

    //TODO: Subject to change
    public ScannableCode(String code) {
        this.code = code;
        this.hash = Hashing.sha256().hashString(code, StandardCharsets.UTF_8).toString();
    }

    public String getCode() {
        return this.code;
    }

    public String getHash() {
        return this.hash;
    }
}
