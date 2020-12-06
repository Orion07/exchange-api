package com.orion.exchangeapi.service;

import java.net.URI;
import java.util.Map;

public interface HttpService {

    <T> T get(URI uri, Map<String, String> headers, Class<T> responseClass);
}
