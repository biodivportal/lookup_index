package org.gfbio.LookupIndex.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.jena.query.QuerySolution;


import java.util.List;

public class FormatterUtils {

    public static JsonArray processArrayFields(JsonArray data, String value) {
        JsonPrimitive element = new JsonPrimitive(value);
        data.add(element);
        return data;
    }

    /**
     * Extracts a field from a ResultSet and formats its value as String
     *
     * @param currentResult
     * @param field
     * @return
     */
    public static String formatField(QuerySolution currentResult, String field) {
        String value = null;
        if (currentResult.get(field) != null) {
            value = currentResult.get(field).toString();
        }
        return value;
    }

    public static JsonObject formatTermInfo(QuerySolution current_result) {
        JsonObject builder = new JsonObject();
        builder.addProperty("uri", formatField(current_result, "uri"));
        builder.addProperty("label", formatField(current_result, "label"));
        return builder;
    }

    public static JsonObject formatHierarchyResult(Pair<String, String> entry, String uri,
                                                   JsonArray hierarchyArray) {
        JsonObject builder = new JsonObject();
        builder.addProperty("label", entry.getLeft());
        builder.addProperty("uri", uri);
        builder.add("hierarchy", hierarchyArray);
        return builder;
    }

    public static JsonArray convertListOfStringsToJsonArray(List<String> list) {
        JsonArray arr = new JsonArray();
        for (String jsonString : list) {
            arr.add(jsonString);
        }
        return arr;
    }

    public static String replaceSpaceWithAndString(String string) {
        return string.replace(" ", " and ");
    }

    public static String delQuotesFromString(String string) {
        return string.replaceAll("\"", "");
    }

    public static String delRedundantSpace(String string) {
        return string.replace("  ", " ");
    }

    public static String delSpaceAtTheEnd(String string) {
        return string.replaceAll("\\s+$", "");
    }

    public static String delLogicalOperatorsFromString(String string) {
        string = string.replace(" and ", " ");
        string = string.replace(" and", "");
        string = string.replace(" or ", " ");
        string = string.replace(" or", "");
        return string;
    }

    public static String formatSearchString(String searchString) {
        searchString = delQuotesFromString(searchString);
        searchString = delSpaceAtTheEnd(searchString);
        searchString = delLogicalOperatorsFromString(searchString);
        searchString = delRedundantSpace(searchString);
        searchString = replaceSpaceWithAndString(searchString);
        return searchString;
    }

}
