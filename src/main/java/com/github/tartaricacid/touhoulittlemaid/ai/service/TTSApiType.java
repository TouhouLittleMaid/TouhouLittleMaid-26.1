package com.github.tartaricacid.touhoulittlemaid.ai.service;

public enum TTSApiType {
    FISH_AUDIO("fish-audio"),
    SYSTEM("system");

    private final String name;

    TTSApiType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
