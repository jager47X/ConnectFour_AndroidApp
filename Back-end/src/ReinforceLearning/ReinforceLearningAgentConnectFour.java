package ReinforceLearning;



import dto.Connect4Dto;
import dto.QTableDto;


 public class  ReinforceLearningAgentConnectFour extends AbstractReinforceLearningAgent2D {
    int TotalReward;

    public ReinforceLearningAgentConnectFour (Connect4Dto connect4dto,QTableDto importedQTable) {
        super(connect4dto,importedQTable);
        TotalReward=0;
    }

    public ReinforceLearningAgentConnectFour (Connect4Dto connect4dto) {
        super(connect4dto);
        TotalReward=0;
    }

    public int makeAIMove(Connect4Dto CurrentState) {
        return selectAction(CurrentState);
    }

}
