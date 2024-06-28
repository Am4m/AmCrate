package com.amam.amcrate.crate;

import org.jetbrains.annotations.NotNull;

public record CratePreset(int @NotNull ... slots) {

    public enum Type {
        HORIZONTAL(new CratePreset(9, 10, 11, 12, 13, 14, 15, 16, 17)),
        CIRCLE(new CratePreset(3, 4, 5, 15, 24, 33, 41, 40, 39, 29, 20, 11)),
        SNAKE(new CratePreset(0, 9, 18, 27, 36, 45, 46, 47, 38, 29, 20, 11, 2, 3, 4, 13, 22, 31, 40, 49, 50, 51, 42, 33, 24, 15, 6, 7, 8, 17, 26, 35, 44, 53));

        private final CratePreset preset;

        Type(CratePreset preset) {
            this.preset = preset;
        }

        public CratePreset getPreset() {
            return this.preset;
        }
    }
}