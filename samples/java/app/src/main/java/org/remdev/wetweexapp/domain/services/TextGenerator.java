package org.remdev.wetweexapp.domain.services;

import org.remdev.wetweexapp.domain.model.GeneratedText;

import java.io.ByteArrayOutputStream;
import java.util.Random;

public class TextGenerator {

    public GeneratedText generateText(Long millis) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final long end = System.currentTimeMillis() + millis;
        while (System.currentTimeMillis() < end) {
            baos.write((byte) new Random().nextInt(127));
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return new GeneratedText(new String(baos.toByteArray()));
    }
}