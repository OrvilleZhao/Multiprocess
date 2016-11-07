/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiprocess;

/**
 *
 * @author Administrator
 */
public class TicketingDS implements TicketingSystem {
    private final int routenum;
    private final int coachnum;
    private final int seatnum;
    private final int stationnum;
    public TicketingDS(int routenum,int coachnum,int seatnum,int stationnum){
        this.routenum=routenum;
        this.coachnum=coachnum;
        this.seatnum=seatnum;
        this.stationnum=stationnum;
    }
    @Override
    public Ticket buyTicket(String passenger, int route, int departure, int arrival) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int inquiry(int route, int departure, int arrival) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean refundTicket(Ticket ticket) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
