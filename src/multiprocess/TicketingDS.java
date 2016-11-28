/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
/**
 *
 * @author Administrator
 */
class SeatLock{
    private  AtomicBoolean flag=new AtomicBoolean(true);
    public boolean Lock(){
        if(flag.get()){
            return false;
        }else{
            return flag.getAndSet(false);
        }
    } 
    public void unLock(){
        flag.set(true);
    }
}
class coachNum{
   volatile int Count;
   volatile int coachNum;
}
class Train{
    private AtomicBoolean key=new AtomicBoolean(false);
    public boolean value(){
        return  key.get();
    }
    public boolean Set(){
        if(!key.get()){
            return key.compareAndSet(false, true);
        }else{
            return false;
        }
    }
    public boolean unSet(){
        if(key.get()){
          return key.compareAndSet(true, false);
        }else{
          return false;
        }
    }
}
class Seat{
    private Train[][][][] seat;
    private final int seatnum;
    private SeatLock[][][] CL;
    private coachNum coachn[][];
    //初始化座位
    public Seat(int routenum,int coachnum,int seatnum,int stationnum){
        seat=new Train[routenum][coachnum][seatnum][stationnum];
        CL=new SeatLock[routenum][coachnum][seatnum];
        coachn=new coachNum[routenum][coachnum];
        for(int j=0;j<routenum;j++){
            for(int s=0;s<coachnum;s++){
                coachn[j][s]=new coachNum();
                coachn[j][s].Count=seatnum*stationnum;
                coachn[j][s].coachNum=s;
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
    //售出座位
    public int write(int route,int coach,int departure, int arrival){  
           for(int i=0;i<seatnum;i++){
               long startTime=System.nanoTime();
               long patient=400;
               while(CL[route][coach][i].Lock()){
                   if(System.nanoTime()-startTime>patient){
                       i=(i++)%seatnum;
                   }
               }
               ArrayList array=new ArrayList();   
               if(departure+1!=arrival){
                    if(seat[route][coach][i][departure].Set()&&seat[route][coach][i][arrival-1].Set()){
                        coachn[route][coach].Count-=2;
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
                       coachn[route][coach].Count-=1;
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
                   coachn[route][coach].Count-=1;
                   array.add(j);
                   }else{
                       for(int s=0;s<array.size();s++){
                           seat[route][coach][i][(int)array.get(s)].unSet();
                           coachn[route][coach].Count+=1;
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
    //查询座位
    public int search(int route,int coach,int departure, int arrival){
        int count=0;
        for(int i=0;i<seatnum;i++){
            boolean k=true;
            for(int j=departure;j<arrival;j++){
               if(seat[route][coach][i][j].value()){
                  k=false;
                  break;
               }
            }
            count++;
        }
        return count;
    }
    //取消座位
    boolean cancel(Ticket ticket) {
        while(CL[ticket.route][ticket.coach][ticket.seat].Lock()){}
        try{
             long id=ticket.tid;
             for(int i=ticket.departure;i<ticket.arrival;i++){
                 seat[ticket.route][ticket.coach][ticket.seat][i].unSet();
                 coachn[ticket.route][ticket.coach].Count+=1;
             }
        }catch(Exception ex){
            return false;
        }finally{
            CL[ticket.route][ticket.coach][ticket.seat].unLock();
        }
        return true;
    }
    //初始化空余座位队列,使用快排来对车厢进行排序
    public coachNum[] init(int route){
        coachNum[] array=coachn[route];//制作此时的记录副本
        int start=0;
        int end=array.length-1;   
        QuickSort(start,end,array);//利用快排筛选出最好的车厢数
        return array;
    }
    //快排
    private void QuickSort(int start, int end, coachNum[] array) {
        if(start>=end) return;
        coachNum key=array[start];
        int i=start;
        int j=end;
        while(i<j){
            while(i<j){
                if(array[j].Count>key.Count){
                    array[i]=array[j];
                    i++;
                    break;
                }else
                    j--;
            }
            while(i<j){
                if(array[i].Count<key.Count){
                    array[j]=array[i];
                    j--;
                    break;
                }else
                    i++;
            }
        }
        array[i]=key;
        QuickSort(start,i-1,array);
        QuickSort(i+1,end,array);
    }
}

public class TicketingDS implements TicketingSystem {
    private final int routenum;
    private final int coachnum;
    private final int seatnum;
    private final int stationnum;
    private volatile long maxId=0;
    private final Seat seat;
    private AtomicLong Id=new AtomicLong(0);
    private volatile coachNum[] array;
    private ConcurrentLinkedQueue history=new ConcurrentLinkedQueue();
    public TicketingDS(int routenum,int coachnum,int seatnum,int stationnum){
        this.routenum=routenum;
        this.coachnum=coachnum;
        this.seatnum=seatnum;
        this.stationnum=stationnum;
        seat=new Seat(routenum,coachnum,seatnum,stationnum);
        for(int i=0;i<routenum;i++)
        array=seat.init(i);
    }
    @Override
    public Ticket buyTicket(String passenger, int route, int departure, int arrival) {
        if(departure==arrival||departure>arrival) return null;
        if(Id.getAndIncrement()%(seatnum/3==0?1:seatnum/3)==0)//线程数达到座位数的一半即更新一次剩余座位排序表
          array=seat.init(route);
         for(int i=0;i<coachnum;i++){
            int k=seat.write(route, array[i].coachNum, departure, arrival);
            if(k!=-1){
                Ticket t=new Ticket();
                t.passenger=passenger;
                Thread current = Thread.currentThread();  
                t.tid=Id.getAndIncrement();
                t.arrival=arrival;
                t.departure=departure;
                t.coach=array[i].coachNum;
                t.route=route;
                t.seat=k;
                history.offer(t.tid);
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
            if(history.contains(ticket.tid))
                if(seat.cancel(ticket)){
                    history.remove(ticket.tid);
                    return true;
                }
                else
                  return false;
            else
                return false;
    } 
}
