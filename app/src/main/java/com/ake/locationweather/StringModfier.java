package com.ake.locationweather;

/**
 * Created by 유현석 on 2017-04-03.
 */

public class StringModfier {
    private String string;

    public StringModfier(String string) {
        this.string = string;
    }

    public StringModfier newLine() {
        string += "\n";
        return this;
    }

    public StringModfier addText(CharSequence addedText) {
        string += addedText;
        return this;
    }

    public StringModfier trim() {
        string = string.trim();
        return this;
    }

    public String end() {
        return string;
    }

}
