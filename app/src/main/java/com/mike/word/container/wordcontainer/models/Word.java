package com.mike.word.container.wordcontainer.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by mike on 18/01/08.
 */

public class Word implements Parcelable {
    private String id;
    private String matchType;
    private String region;
    private String word;

    // Details
    private String definition;
    private String example;

    public Word() {}

    public Word(String id, String word, String definition, String example) {
        this.id = id;
        this.word = word;
        this.definition = definition;
        this.example = example;
    }

    private Word(Parcel in) {
        id = in.readString();
        matchType= in.readString();
        region = in.readString();
        word = in.readString();
        definition = in.readString();
        example = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(matchType);
        parcel.writeString(region);
        parcel.writeString(word);
        parcel.writeString(definition);
        parcel.writeString(example);
    }

    public static final Parcelable.Creator<Word> CREATOR = new Parcelable.Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel parcel) {
            return new Word(parcel);
        }

        @Override
        public Word[] newArray(int i) {
            return new Word[i];
        }
    };
}
