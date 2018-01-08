package com.mike.word.container.wordcontainer.utilities;

import com.mike.word.container.wordcontainer.models.Word;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mike on 18/01/08.
 */

public final class WordJsonUtilities {
    private static final String ID = "id";
    private static final String MATCH_TYPE = "matchType";
    private static final String REGION = "region";
    private static final String WORD = "word";

    private static final String RESULTS = "results";

    public static List<Word> getWordStringsFromJson(String jsonStr) throws JSONException {
        List<Word> parsedData = new ArrayList<>();
        JSONObject wordJson = new JSONObject(jsonStr);
        JSONArray wordArray = wordJson.getJSONArray(RESULTS);

        for (int i = 0; i < wordArray.length(); i++) {
            JSONObject data = wordArray.getJSONObject(i);

            String wordId = data.getString(ID);
            String wordResult = data.getString(WORD);
            String wordMatchType = data.getString(MATCH_TYPE);
            String wordRegion = data.getString(REGION);

            Word word = new Word();
            word.setId(wordId);
            word.setWord(wordResult);
            word.setMatchType(wordMatchType);
            word.setRegion(wordRegion);

            parsedData.add(word);
        }

        return parsedData;
    }
}
