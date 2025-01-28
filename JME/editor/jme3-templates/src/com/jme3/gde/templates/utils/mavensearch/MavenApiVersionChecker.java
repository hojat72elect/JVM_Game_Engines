/*
 * Copyright (c) 2009-2023 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.gde.templates.utils.mavensearch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.jme3.gde.templates.utils.mavensearch.models.SearchResult;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.joining;

/**
 * Checks versions from the Maven Search API
 */
public class MavenApiVersionChecker implements MavenVersionChecker {

    private static final String API_URL = "https://search.maven.org/solrsearch/select";

    private static final Gson GSON;

    static {
        JsonDeserializer<Instant> instantDeserializer = (jSon, typeOfT, context) -> jSon == null ? null : Instant.ofEpochMilli(jSon.getAsLong());

        GSON = new GsonBuilder()
                .registerTypeAdapter(Instant.class, instantDeserializer).create();
    }

    @Override
    public CompletableFuture<List<String>> getAllVersions(String groupId, String artifactId) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("q", String.format("g:%s AND a:%s", groupId, artifactId));
        queryParams.put("core", "gav");
        queryParams.put("rows", "50");
        queryParams.put("wt", "json");

        try {
            return callApi(queryParams, SearchResult.class).thenApply((result) -> {
                if (result == null || result.response.docs.isEmpty()) {
                    return null;
                }

                return result.response.docs.stream().map((doc) -> doc.v).collect(Collectors.toList());
            });
        } catch (InterruptedException | ExecutionException ex) {
            throw new MavenVersionCheckException("Failed to get version info!", ex);
        }
    }

    @Override
    public CompletableFuture<String> getLatestVersion(String groupId, String artifactId) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("q", String.format("g:%s AND a:%s", groupId, artifactId));
        queryParams.put("wt", "json");

        try {
            return callApi(queryParams, SearchResult.class).thenApply((result) -> {
                if (result == null || result.response.docs.isEmpty()) {
                    return null;
                }

                return result.response.docs.get(0).latestVersion;
            });
        } catch (InterruptedException | ExecutionException ex) {
            throw new MavenVersionCheckException("Failed to get latest version info!", ex);
        }
    }

    private static <T> CompletableFuture<T> callApi(Map<String, String> queryParams, Class<T> clazz) throws InterruptedException, ExecutionException {
        String encodedURL = queryParams.keySet().stream()
                .map(key -> key + "=" + encodeValue(queryParams.get(key)))
                .collect(joining("&", API_URL + "?", ""));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(encodedURL))
                .build();

        CompletableFuture<T> result = client.sendAsync(request, BodyHandlers.ofString())
                .thenApply((t) -> {
                    if (t.statusCode() != 200) {
                        throw new MavenVersionCheckException("Calling " + encodedURL + " not OK. API response " + t.statusCode() + "!");
                    }

            return GSON.fromJson(t.body(), clazz);
                });

        return result;
    }

    private static String encodeValue(final String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ex) {
            throw new MavenVersionCheckException("Failed to encode value!", ex);
        }
    }

}
