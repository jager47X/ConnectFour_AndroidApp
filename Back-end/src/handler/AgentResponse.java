package handler;

import ReinforceLearning.ReinforceLearningAgentConnectFour;
import com.google.gson.JsonSyntaxException;
import dao.QTableDao;
import dto.Connect4Dto;
import dto.QTableDto;
import request.ParsedRequest;
import response.HttpResponseBuilder;
import response.RestApiAppResponse;

public class AgentResponse implements BaseHandler {
    private final ReinforceLearningAgentConnectFour agent;

    protected AgentResponse(ReinforceLearningAgentConnectFour agent) {
        this.agent=agent;
    }

    public HttpResponseBuilder handleRequest(ParsedRequest request) {
        try {
            Connect4Dto connect4Dto = (Connect4Dto) GsonTool.gson.fromJson(request.getBody(), Connect4Dto.class);

            connect4Dto.getGame().playerDrop(connect4Dto.getLastAction());
                int AIResponse = agent.makeAIMove(connect4Dto);
            connect4Dto.addAction(AIResponse);
            if (AIResponse >= 1 && AIResponse <= 7) {
                RestApiAppResponse<QTableDto> res = new RestApiAppResponse<>(true, connect4Dto, "success");
                return (new HttpResponseBuilder()).setStatus("200 OK").setBody(res);
            }
            RestApiAppResponse<QTableDto> res = new RestApiAppResponse<>(true, connect4Dto, "fail");
            return (new HttpResponseBuilder()).setStatus("400 bad request").setBody(res);
        } catch (JsonSyntaxException e) {
            System.out.println(e.fillInStackTrace());
        }
        RestApiAppResponse<QTableDto> res = new RestApiAppResponse<>(true, null, "fail");
        return (new HttpResponseBuilder()).setStatus("200 OK").setBody(res);
    }


}
//keep adding action to list of action int the connect4Dto