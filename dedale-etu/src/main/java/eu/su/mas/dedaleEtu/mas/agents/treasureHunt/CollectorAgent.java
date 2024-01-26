package eu.su.mas.dedaleEtu.mas.agents.treasureHunt;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.treasureHunt.mainBehaviours.CollectorMainBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.treasureHunt.mainBehaviours.ExplorerMainBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.AgentStatus;
import eu.su.mas.dedaleEtu.mas.knowledge.Chest;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.Behaviour;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CollectorAgent extends AbstractDedaleAgent {

	private static final long serialVersionUID = -7969469610241668140L;
	private List<String> agentNames;
	private List<Chest> chests;
	private List<AgentStatus> agentsInRange;
	private AgentStatus currentStatus;

	protected void setup(){
		super.setup();
		final Object[] args = getArguments();
		this.agentNames=new ArrayList<>();
		Integer period = 100;

		if(args.length==0){
			System.err.println("Error while creating the agent, names of agent to contact expected");
			System.exit(-1);
		}
		else{
			period = (Integer) args[2];
			int i = 3;
			while (i < args.length) {
				this.agentNames.add((String) args[i]);
				i++;
			}
			Integer hierarchy = this.agentNames.indexOf(this.getLocalName());
			this.agentNames.remove(this.getLocalName());

			//Init Explorer objects
			this.chests = new ArrayList<>();
			this.agentsInRange = new ArrayList<>();
			this.currentStatus = AgentStatus.builder()
								.agentName(this.getLocalName())
								.desesperation(0)
								.priority(0)
								.followingPath(new ArrayList<>())
								.hierarchy(hierarchy)
								.build();
		}
		List<Behaviour> lb=new ArrayList<Behaviour>();

		lb.add(new CollectorMainBehaviour(this, period, this.agentNames, this.chests, this.agentsInRange, this.currentStatus));

		addBehaviour(new startMyBehaviours(this,lb));
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}
}
