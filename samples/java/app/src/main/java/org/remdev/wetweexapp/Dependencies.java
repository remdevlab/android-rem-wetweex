package org.remdev.wetweexapp;

import org.remdev.wetweexapp.domain.services.TextGenerator;

public final class Dependencies {

    public static TextGenerator textGenerator() {
        return new TextGenerator();
    }
}