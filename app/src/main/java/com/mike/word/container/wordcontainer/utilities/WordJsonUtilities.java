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
    private static final String LEXICAL_ENTRIES = "lexicalEntries";
    private static final String ENTRIES = "entries";
    private static final String SENSES = "senses";
    private static final String DEFINITIONS = "definitions";
    private static final String EXAMPLES = "examples";
    private static final String TEXT = "text";

    private static final int FIRST_POSITION = 0;

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

    public static Word getWordDetails(String jsonStr) throws JSONException{
        Word word = new Word();
        JSONObject wordJson = new JSONObject(jsonStr);
        JSONArray wordArray = wordJson.getJSONArray(RESULTS);

        for (int i = 0; i < 1; i++) {
            JSONObject lexicalEntries = wordArray.getJSONObject(i);
            JSONArray lexicalArray = lexicalEntries.getJSONArray(LEXICAL_ENTRIES);

            word.setId(lexicalEntries.getString(ID));

            for (int j = 0; j < 1; j++) {
                JSONObject entries = lexicalArray.getJSONObject(j);
                JSONArray entriesArray = entries.getJSONArray(ENTRIES);

                for (int k = 0; k < 1; k++) {
                    JSONObject senses = entriesArray.getJSONObject(k);
                    JSONArray sensesArray = senses.optJSONArray(SENSES);

                    if (sensesArray != null) {
                        for (int l = 0; l < 1; l++) {
                            JSONObject definitions = sensesArray.getJSONObject(l);
                            JSONArray definitionsArray = definitions.optJSONArray(DEFINITIONS);

                            // Use optJSONArray because there might not be any examples
                            JSONArray examplesArray = definitions.optJSONArray(EXAMPLES);
                            if (examplesArray != null) {
                                for (int m = 0; m < examplesArray.length(); m++) {
                                    JSONObject examples = examplesArray.getJSONObject(m);
                                    String example = examples.getString(TEXT);

                                    word.setExample(example);
                                }
                            }

                            if (definitionsArray != null) {
                                word.setDefinition(definitionsArray.getString(FIRST_POSITION));
                            }
                        }
                    }
                }

            }
        }

        return word;
    }
}
