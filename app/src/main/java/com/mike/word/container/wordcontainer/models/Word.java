package com.mike.word.container.wordcontainer.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mike on 18/01/08.
 */

public class Word implements Parcelable {
    private String id;
    private String matchType;
    private String region;
    private String word;

    public Word() {}

    private Word(Parcel in) {
        id = in.readString();
        matchType= in.readString();
        region = in.readString();
        word = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public String getRegion() {
        return region;
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
