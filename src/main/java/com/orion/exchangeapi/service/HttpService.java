package com.orion.exchangeapi.service;

import java.net.URI;
import java.util.Map;

public interface HttpService {

    <T> T get(URI uri, Map<String, String> headers, Class<T> responseClass);

    <T> T post(URI uri, Map<String, String> headers, Map<String, Object> body, Class<T> responseClass);
}
