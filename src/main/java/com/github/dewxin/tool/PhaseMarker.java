package com.github.dewxin.tool;

import java.io.Closeable;
import java.io.IOException;
import java.text.MessageFormat;

public class PhaseMarker implements Closeable {

    private String currentPhase;

    public PhaseMarker(String phase) {
        currentPhase = phase;
        String content = MessageFormat.format(">>> >>> enter phase {0} >>> >>>", currentPhase);
        Logger.info("");
		Logger.info(content);
    }

    @Override
    public void close() throws IOException {
        String content = MessageFormat.format("<<< <<< finish phase {0} <<< <<<", currentPhase);
		Logger.info(content);
        Logger.info("");
    }
    
}
