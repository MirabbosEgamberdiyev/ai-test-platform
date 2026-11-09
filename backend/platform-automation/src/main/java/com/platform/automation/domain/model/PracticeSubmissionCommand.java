package com.platform.automation.domain.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public record PracticeSubmissionCommand(
        UUID sessionId,
        String selectedOptionLabel,
        String textInputAnswer
) implements Serializable {
    public PracticeSubmissionCommand {
        Objects.requireNonNull(sessionId, "Session ID cannot be null");
    }
}
