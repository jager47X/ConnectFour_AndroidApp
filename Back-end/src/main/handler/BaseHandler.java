package main.handler;

import main.request.ParsedRequest;
import main.response.HttpResponseBuilder;

public interface BaseHandler {

  HttpResponseBuilder handleRequest(ParsedRequest request);
}
