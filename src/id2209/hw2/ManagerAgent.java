/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id2209.hw2;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author srinivaik
 */
public class ManagerAgent extends Agent {
    private final int PRICE = 10000;
    private int bid;
    private AID[] buyers;
    
    @Override
    protected void setup(){
    System.out.println("Agent: "+ getAID().getLocalName() + " is ready "); 
    bid = PRICE*2;
    
    SequentialBehaviour sb = new SequentialBehaviour();
    sb.addSubBehaviour(new registerParticipants(this, 5000));
    sb.addSubBehaviour(new startAuction());
    sb.addSubBehaviour(new performAuction());
}
 protected void takedown() {
     try{
        DFService.deregister(this);
     }
     catch(FIPAException fe){}
     
 } 
 private class registerParticipants extends WakerBehaviour{
     
        public registerParticipants(Agent agent, long timeout) {
            super(agent, timeout);
        }
        @Override
     protected void onWake(){
     DFAgentDescription template = new DFAgentDescription();
     ServiceDescription tsd = new ServiceDescription();
     tsd.setType("buyer");
     template.addServices(tsd);
     try{
         DFAgentDescription[] result = DFService.search(myAgent, template);
         if(result.length > 0){
             buyers = new AID[result.length];
             for(int i = 0; i< result.length; i++)
             {
                 buyers[i] = result[i].getName();
             }
         }
     }      catch (FIPAException ex) {}
     
 }
 }
 private class startAuction extends OneShotBehaviour{
     @Override
     public void action()
     {
         ACLMessage msg = new ACLMessage(ACLMessage.CFP);
         for(AID buyer : buyers){
             msg.addReceiver(buyer);
         }
         msg.setContent(Integer.toString(bid));
         send(msg);
         System.out.println("Agent"+ getAID().getLocalName()+"started the auction");
     }
     
 }
 private class performAuction extends ParallelBehaviour{
     performAuction(){
         super(WHEN_ANY);
         addSubBehaviour(new TickerBehaviour(myAgent ,3000){
         @Override
         protected void onTick(){
             bid *= 0.9;
             ACLMessage msg = new ACLMessage(ACLMessage.CFP);
             for(AID buyer : buyers){msg.addReceiver(buyer);}
             msg.setContent(Integer.toString(bid));
             send(msg);
             System.out.println("Current bid: "+bid);
         }
     });
         addSubBehaviour(new SimpleBehaviour(){
             boolean done = false;
             @Override
             public void action(){
             ACLMessage msg = receive();
             if (msg != null) {
                        if (msg.getPerformative() == ACLMessage.PROPOSE) {
                            if (Integer.parseInt(msg.getContent()) == bid) {
                                AID winner = msg.getSender();
                                System.out.println("Item sold to " + winner.getLocalName());

                                ACLMessage winMsg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                                winMsg.addReceiver(winner);
                                winMsg.setContent("You won the auction");
                                send(winMsg);

                                ACLMessage endMsg = new ACLMessage(ACLMessage.INFORM);
                                for (AID buyer : buyers) {endMsg.addReceiver(buyer);}
                                endMsg.setContent("Auction ended");
                                send(endMsg);
                                done = true;
                            }
                        }
                    }
             
         }

             @Override
             public boolean done() {
                 return done;
             }
         });
         
         
     }
 }
}
