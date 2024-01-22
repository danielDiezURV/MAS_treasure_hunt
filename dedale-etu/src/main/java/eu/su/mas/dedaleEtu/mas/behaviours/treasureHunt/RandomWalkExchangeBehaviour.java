package eu.su.mas.dedaleEtu.mas.behaviours.treasureHunt;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.behaviours.TickerBehaviour;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**************************************
 * 
 * 
 * 				BEHAVIOUR RandomWalk : Illustrates how an agent can interact with, and move in, the environment
 * 
 * 
 **************************************/

public class RandomWalkExchangeBehaviour extends TickerBehaviour{
	/**
	 * When an agent choose to move
	 *
	 */
	private static final long serialVersionUID = 9088209402507795289L;

	public RandomWalkExchangeBehaviour (final AbstractDedaleAgent myagent) {
		super(myagent, 500);
		//super(myagent);
	}

	@Override
	public void onTick() {
		//Example to retrieve the current position
		Location myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

		if (myPosition!=null && myPosition.getLocationId()!=""){
			List<Couple<Location,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
//			System.out.println(this.myAgent.getLocalName()+" -- list of observables: "+lobs);


			List<Couple<Observation,Integer>> lObservations= lobs.get(0).getRight();

			//example related to the use of the backpack for the treasure hunt
			Boolean b=false;
			for(Couple<Observation,Integer> o:lObservations){
				switch (o.getLeft()) {
					case DIAMOND:case GOLD:
						b=true;
						break;
					default:
						break;
				}
			}

			//If the agent picked (part of) the treasure
			if (b){
				List<Couple<Location,List<Couple<Observation,Integer>>>> lobs2=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
				System.out.println("State of the observations after picking "+lobs2);
			}

			//Trying to store everything in the tanker
//			System.out.println(this.myAgent.getLocalName()+" - My current backpack capacity is:"+ ((AbstractDedaleAgent)this.myAgent).getBackPackFreeSpace());
//			System.out.println(this.myAgent.getLocalName()+" - The agent tries to transfer is load into the Silo (if reachable); succes ? : "+((AbstractDedaleAgent)this.myAgent).emptyMyBackPack("Tank"));
//			System.out.println(this.myAgent.getLocalName()+" - My current backpack capacity is:"+ ((AbstractDedaleAgent)this.myAgent).getBackPackFreeSpace());
//
			//Random move from the current position
			Random r= new Random();
			int moveId=1+r.nextInt(lobs.size()-1);//removing the current position from the list of target to accelerate the tests, but not necessary as to stay is an action

			//The move action (if any) should be the last action of your behaviour
			((AbstractDedaleAgent)this.myAgent).moveTo(lobs.get(moveId).getLeft());
		}

	}

}