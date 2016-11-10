/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiprocess;

import java.util.ArrayList;
import java.util.List;



/**
 *
 * @author Administrator
 */
class SeatLock{
    private volatile boolean flag=true;
    public synchronized boolean Lock(){
        if(this.flag){
            this.flag=false;
            return false;
        }else{
            return true;
        }
    } 
    public synchronized void unLock(){
        this.flag=true;
    }
}
class Train{
    private volatile boolean key;
    public Train(){
        this.key=false;
    }
    public synchronized boolean value(){
        return  this.key;
    }
    public synchronized boolean Set(){
        if(!this.key){
          this.key=true;
          return true;
        }else{
            return false;
        }
    }
    public synchronized boolean unSet(){
        if(this.key){
          this.key=false;
          return true;
        }else{
          return false;
        }
    }
}
class Seat{
    private Train[][][][] seat;
    private final int seatnum;
    private SeatLock[][][] CL;
    private List history;//Rid历史队列
    public Seat(int routenum,int coachnum,int seatnum,int stationnum){
        seat=new Train[routenum][coachnum][seatnum][stationnum];
        CL=new SeatLock[routenum][coachnum][seatnum];
        for(int j=0;j<routenum;j++){
            for(int s=0;s<coachnum;s++){
                for(int k=0;k<seatnum;k++)
                    CL[j][s][k]=new SeatLock();
            }
        }
        int X1=routenum;
        while(--X1>=0){
          int X2=coachnum;
          while(--X2>=0){
              int X3=seatnum;
              while(--X3>=0){
                  int X4=stationnum;
                  while(--X4>=0){
                      seat[X1][X2][X3][X4]=new Train();
                  }
              }
          }
        }
        this.seatnum=seatnum;
    }
    public int write(int route,int coach,int departure, int arrival){  
           for(int i=0;i<seatnum;i++){
               while(CL[route][coach][i].Lock()){}
               ArrayList array=new ArrayList();   
               //System.out.println(route+":"+coach+":"+i+":"+departure);
               if(departure+1!=arrival){
                    if(seat[route][coach][i][departure].Set()&&seat[route][coach][i][arrival-1].Set()){
                        if(departure+1==arrival){
                            CL[route][coach][i].unLock();
                            return i;
                        }
                        else{
                              array.add(departure);
                              array.add(arrival-1);
                        }
                    }else{
                        CL[route][coach][i].unLock();
                        continue;
                    }
               }else{
                   if(seat[route][coach][i][departure].Set()){
                       CL[route][coach][i].unLock();
                       return i;
                   }else{
                       CL[route][coach][i].unLock();
                       continue;
                   }
               }
               int j=0;
               for(j=departure+1;j<arrival-1;j++){
                   if(seat[route][coach][i][j].Set()){
                   array.add(j);
                   }else{
                       for(int s=0;s<array.size();s++){
                           seat[route][coach][i][(int)array.get(s)].unSet();
                       }
                       CL[route][coach][i].unLock();
                       continue;
                   }
               }
               if(j==arrival-1){
                   CL[route][coach][i].unLock();
                   return i;
               }
           }
          return -1;
    }
    public int search(int route,int coach,int departure, int arrival){
        int count=0;
        for(int i=0;i<seatnum;i++){
            if(!seat[route][coach][i][departure].value()&&!seat[route][coach][i][arrival-1].value())
               count++;
        }
        return count;
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
         //long Rid=seat.BuyList(route,departure, arrival);
         //System.out.println(i+":"+k);
         for(int i=0;i<coachnum;i++){
            int k=seat.write(route, i, departure, arrival);
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
