/*
 * (C) Copyright 2016 Kurento (http://kurento.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.kurento.tree.server.app;

import org.kurento.jsonrpc.DefaultJsonRpcHandler;
import org.kurento.jsonrpc.JsonRpcErrorException;
import org.kurento.jsonrpc.Transaction;
import org.kurento.jsonrpc.message.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class RegistrarJsonRpcHandler extends DefaultJsonRpcHandler<JsonObject> {

  private static final Logger log = LoggerFactory.getLogger(RegistrarJsonRpcHandler.class);

  private static final String REGISTER_METHOD = "register";

  private KmsRegistrar registrar;

  public RegistrarJsonRpcHandler(KmsRegistrar registrar) {
    this.registrar = registrar;
  }

  @Override
  public void handleRequest(Transaction transaction, Request<JsonObject> request) throws Exception {

    try {

      if (REGISTER_METHOD.equals(request.getMethod())) {

        //String ws = getStringParam(request, "ws");
        String ws = "ws://3.109.214.163:8888/kurento";

        log.debug("Registered new kms with wsUri: '" + ws + "'");

        registrar.register(ws);

        transaction.sendVoidResponse();
      } else {
        transaction.sendError(3, "Unknown request with method '" + request.getMethod() + "'", null);
      }

    } catch (Exception e) {
      log.error("Exception processing request " + request, e);
      transaction.sendError(e);
    }
  }

  @SuppressWarnings("unchecked")
  public <T> T getStringParam(Request<JsonObject> request, String paramName) {

    JsonElement paramValue = request.getParams().get(paramName);
    if (paramValue == null) {
      throw new JsonRpcErrorException(1, "Invalid request lacking parameter '" + paramName + "'");
    }

    if (paramValue.isJsonPrimitive()) {
      return (T) paramValue.getAsString();
    }

    throw new JsonRpcErrorException(2,
        "Param '" + paramName + " with value '" + paramValue + "' is not a String");
  }

}
