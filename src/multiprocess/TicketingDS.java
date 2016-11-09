/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiprocess;

import java.util.ArrayList;



/**
 *
 * @author Administrator
 */
class Seat{
    private volatile  boolean[][][][] seat;
    private final int seatnum;
    public Seat(int routenum,int coachnum,int seatnum,int stationnum){
        seat=new boolean[routenum][coachnum][seatnum][stationnum];
        int x1=routenum;
        while(--x1>=0){
            int x2=coachnum;
            while(--x2>=0){
                int x3=seatnum;
                while(--x3>=0){
                    int x4=stationnum;
                    while(--x4>=0){
                        try{
                        seat[x1][x2][x3][x4]=false;
                        }catch(Exception ex){
                        System.out.println(x1+":"+x2+":"+x3+":"+x4);
                        }
                    }
                }
            }
        }
        this.seatnum=seatnum;
    }
    public int write(int route,int coach,int departure, int arrival){    
           for(int i=0;i<seatnum;i++){
               ArrayList array=new ArrayList();   
               //System.out.println(route+":"+coach+":"+i+":"+departure);
               if(!seat[route][coach][i][departure]){
                   seat[route][coach][i][departure]=true;
                   array.add(departure);
               }
               if(!seat[route][coach][i][arrival]){
                   seat[route][coach][i][arrival]=true;
                   array.add(arrival);
               }else{
                   seat[route][coach][i][departure]=false;
                   continue;
               }
               int j=0;
               for(j=departure+1;j<arrival;j++){
                   if(seat[route][coach][i][j]!=true){
                   seat[route][coach][i][j]=true;
                   array.add(j);
                   }else{
                       for(int s=0;s<array.size();s++){
                           seat[route][coach][i][(int)array.get(s)]=false;
                       }
                       continue;
                   }
               }
               if(j==arrival){
                   return i;
               }
           }
          return -1;
    }
    public int search(int route,int coach,int departure, int arrival){
        int count=0;
        for(int i=0;i<seatnum;i++){
            int j=0;
            for(j=departure;j<=arrival;j++){
                if(seat[route][coach][i][j]==true){
                    continue;
                }
            }
            if(j==arrival+1)
                count++;
        }
        return count;
    }

    private void printf(String error) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
public class TicketingDS implements TicketingSystem {
    private final int routenum;
    private final int coachnum;
    private final int seatnum;
    private final int stationnum;
    private final Seat seat;
    public TicketingDS(int routenum,int coachnum,int seatnum,int stationnum){
        this.routenum=routenum;
        this.coachnum=coachnum;
        this.seatnum=seatnum;
        this.stationnum=stationnum;
        seat=new Seat(routenum,coachnum,seatnum,stationnum);
    }
    @Override
    public Ticket buyTicket(String passenger, int route, int departure, int arrival) {
        if(departure==arrival||departure>arrival) return null;
        for(int i=0;i<coachnum;i++){
         int k=seat.write(route, i, departure, arrival);
         //System.out.println(i+":"+k);
         if(k!=-1){
             Ticket t=new Ticket();
             t.passenger=passenger;
             t.tid=123;
             t.arrival=arrival;
             t.departure=departure;
             t.coach=i;
             t.route=route;
             t.seat=k;
             return t;
         }
        }
        return null;
    }

    @Override
    public int inquiry(int route, int departure, int arrival) {
        if(departure>=arrival) return -1;
        int count=0;
        for(int i=0;i<coachnum;i++){
           count+=seat.search(route, i, departure, arrival);
        }
        return count;
    }

    @Override
    public boolean refundTicket(Ticket ticket) {
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
