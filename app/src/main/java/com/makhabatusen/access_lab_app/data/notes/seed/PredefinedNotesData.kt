package com.makhabatusen.access_lab_app.data.notes.seed

import com.makhabatusen.access_lab_app.data.notes.local.Note

object PredefinedNotesData {

    val predefinedNotes = listOf(
        // Audio & Video
        Note(
            content = "[Clause 11.1.2.1] - Audio-only and Video-only (Pre-recorded)\nProvide alternatives for audio-only and video-only content.",
            isProtected = true
        ),
        Note(
            content = "[Clause 11.1.2.2] - Captions (Pre-recorded)\nProvide captions for pre-recorded audio content in synchronized media.",
            isProtected = true
        ),
        Note(
            content = "[Clause 11.1.2.3] - Audio description or media alternative (Pre-recorded)\nAudio description provided for pre-recorded video content.",
            isProtected = true
        ),
        Note(
            content = "[Clause 11.1.2.4] - Captions (Live)\nProvide captions for live audio content.",
            isProtected = true
        ),
        Note(
            content = "[Clause 11.1.2.5] - Audio description (Pre-recorded)\nProvide audio descriptions for pre-recorded video content.",
            isProtected = true
        ),

        // Text & Structure
        Note(
            content = "[Clause 11.1.3.1] - Info and Relationships\nEnsure information and relationships conveyed through presentation can be programmatically determined.",
            isProtected = true
        ),
        Note(
            content = "[Clause 11.1.3.2] - Meaningful Sequence\nEnsure meaningful reading sequence of content.",
            isProtected = true
        ),
        Note(
            content = "[Clause 11.1.3.4] - Orientation\nContent does not restrict view and operation to a single display orientation.",
            isProtected = true
        ),
        Note(
            content = "[Clause 11.1.3.5] - Identify Input Purpose\nInput fields must have identifiable purposes programmatically determinable.",
            isProtected = true
        ),

        // Images & Media
        Note(
            content = "[Clause 11.1.1.1] - Non-text Content\nProvide text alternatives for non-text content like images.",
            isProtected = true
        ),

        // Input & Touch
        Note(
            content = "[Clause 11.2.1.1] - Keyboard\nAll functionality must be operable through a keyboard interface.",
            isProtected = true
        ),
        Note(
            content = "[Clause 11.2.5.1] - Pointer Gestures\nFunctionality requiring complex gestures must also be operable by simpler methods (e.g., single taps).",
            isProtected = true
        ),
        Note(
            content = "[Clause 11.2.5.2] - Pointer Cancellation\nActivation on pointer up, providing a mechanism to abort or undo pointer down events.",
            isProtected = true
        ),
        Note(
            content = "[Clause 11.2.5.4] - Motion Actuation\nProvide alternative means to operate functionality activated by device motion.",
            isProtected = true
        ),

        // Navigation & Focus
        Note(
            content = "[Clause 11.2.4.3] - Focus Order\nNavigable components must receive focus in a meaningful sequence.",
            isProtected = true
        ),
        Note(
            content = "[Clause 11.3.2.1] - On Focus\nNo unexpected changes when elements receive focus.",
            isProtected = true
        ),
        Note(
            content = "[Clause 11.3.2.2] - On Input\nChanges of context must not happen unexpectedly when inputting data.",
            isProtected = true
        ),

        // Screen Reader / Semantics
        Note(
            content = "[Clause 11.5.2.5] - Object Information\nRole, state, boundary, name, and description of UI elements must be programmatically determinable by assistive technologies.",
            isProtected = true
        ),
        Note(
            content = "[Clause 11.5.2.6] - Row, Column, and Headers\nTable cells and headers must be programmatically determinable.",
            isProtected = true
        ),
        Note(
            content = "[Clause 11.5.2.15] - Change Notification\nNotify assistive technologies about UI changes.",
            isProtected = true
        ),
        Note(
            content = "[Clause 11.5.2.16] - Modifications of States and Properties\nAllow assistive technologies to modify states and properties of UI elements.",
            isProtected = true
        ),
        Note(
            content = "[Clause 11.5.2.17] - Modifications of Values and Text\nAllow assistive technologies to modify values and text of UI elements.",
            isProtected = true
        ),

        // Color & Contrast
        Note(
            content = "[Clause 11.1.4.1] - Use of Colour\nColor must not be the sole means of conveying information.",
            isProtected = true
        ),
        Note(
            content = "[Clause 11.1.4.3] - Contrast (Minimum)\nText and images of text must have a minimum contrast ratio of 4.5:1.",
            isProtected = true
        ),
        Note(
            content = "[Clause 11.1.4.11] - Non-text Contrast\nUI components must have sufficient contrast ratio of at least 3:1 against adjacent colors.",
            isProtected = true
        ),

        // Forms & Labels
        Note(
            content = "[Clause 11.2.5.3] - Label in Name\nVisible labels of components must match their accessible names.",
            isProtected = true
        ),
        Note(
            content = "[Clause 11.3.3.1] - Error Identification\nInput errors must be identified clearly and described to the user.",
            isProtected = true
        ),
        Note(
            content = "[Clause 11.3.3.2] - Labels or Instructions\nLabels or instructions must be provided for user input.",
            isProtected = true
        ),

        // Feedback & Notifications
        Note(
            content = "[Clause 11.3.3.3] - Error Suggestion\nSuggestions for correcting input errors must be provided.",
            isProtected = true
        ),
        Note(
            content = "[Clause 11.3.3.4] - Error Prevention (Legal, Financial, Data)\nProvide means to reverse submissions, check data, and confirm entries to prevent errors.",
            isProtected = true
        ),

        // Compatibility (Assistive Tech, APIs, etc.)
        Note(
            content = "[Clause 11.5.2.2] - Platform Accessibility Service Support\nPlatform accessibility services must be available for interoperability with assistive technologies.",
            isProtected = true
        ),
        Note(
            content = "[Clause 11.5.2.3] - Use of Accessibility Services\nApps must use documented platform accessibility services.",
            isProtected = true
        ),
        Note(
            content = "[Clause 11.5.2.4] - Assistive Technology Integration\nAssistive technologies must use documented platform accessibility services.",
            isProtected = true
        ),
        Note(
            content = "[Clause 11.6.2] - No Disruption of Accessibility Features\nDo not disrupt documented platform accessibility features.",
            isProtected = true
        )
    )
}