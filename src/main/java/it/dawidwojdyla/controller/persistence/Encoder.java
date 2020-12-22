package it.dawidwojdyla.controller.persistence;

import java.util.Base64;

/**
 * Created by Dawid on 2020-12-10.
 */
public class Encoder {

    private static Base64.Encoder encoder = Base64.getEncoder();
    private static Base64.Decoder decoder = Base64.getDecoder();

    public String encode(String text) {
        try {
            return encoder.encodeToString(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decode(String text) {
        try {
            return new String(decoder.decode(text.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
