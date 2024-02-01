package handler;

import ReinforceLearning.ReinforceLearningAgentConnectFour;
import request.ParsedRequest;

public class HandlerFactory {
  // routes based on the path. Add your custom handlers here

public static BaseHandler getHandler(ParsedRequest request, ReinforceLearningAgentConnectFour Agent) {
      switch (request.getPath()) {
          case "/AgentResponse" -> {

              return new AgentResponse(Agent);
          }

          default -> {
              return new FallbackHandler();
          }
      }
  }

}
