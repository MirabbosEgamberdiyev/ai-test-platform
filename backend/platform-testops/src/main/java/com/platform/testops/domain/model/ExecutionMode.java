package com.platform.testops.domain.model;

public enum ExecutionMode {
    /**
     * Solves questions, verifies arithmetic, and provides detailed legal/domain explanations
     * without modifying or submitting to the external target website.
     */
    ANSWER_MODE,

    /**
     * Assists user with step-by-step guidance, hints, and manual action checkpoints.
     */
    GUIDED_MODE,

    /**
     * Permitted only when technically supported, permitted by rules, and authorized by user.
     * Enters approved answers into the browser form, verifies page state, and captures screenshot evidence.
     */
    AUTHORIZED_AUTOMATION_MODE,

    /**
     * Disallowed when target platform policy prohibits automated interaction, proctoring is detected,
     * or user credentials/authorization are unavailable.
     */
    BLOCKED_MODE
}
