package eu.su.mas.dedaleEtu.mas.agents.treasureHunt;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.treasureHunt.RandomWalkExchangeBehaviour;
import jade.core.behaviours.Behaviour;

import java.util.ArrayList;
import java.util.List;

public class CollectorAgent extends AbstractDedaleAgent {
	private static final long serialVersionUID = -1784844593772918359L;
	protected void setup(){

		super.setup();

		List<Behaviour> lb=new ArrayList<Behaviour>();
		lb.add(new RandomWalkExchangeBehaviour(this));

		addBehaviour(new startMyBehaviours(this,lb));

		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}

	protected void takeDown(){

	}
}