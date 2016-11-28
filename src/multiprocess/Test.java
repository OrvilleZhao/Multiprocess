/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiprocess;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
/**
 *
 * @author Administrator
 */
public class Test{
    /**
     * @param args the command line arguments
     */
    private static int route=5;
    private static int coach=8;
    private static int seat=100;
    private static int station=10;
    private static ConcurrentLinkedQueue History=new ConcurrentLinkedQueue();
    private static AtomicLong buyTime=new AtomicLong(0);
    private static AtomicInteger buySUM=new AtomicInteger(0);  
    private static AtomicLong inquiryTime=new AtomicLong(0);
    private static AtomicInteger inquirySUM=new AtomicInteger(0);  
    private static AtomicLong refundTime=new AtomicLong(0);
    private static AtomicInteger refundSUM=new AtomicInteger(0);  
    public static void main(String[] args) {
      final TicketingDS tds=new TicketingDS(route,coach,seat,station);
      for(int p=2;p<=8;p++){
            int k=(int)Math.pow(2, p);
            Thread[] thread=new Thread[k];
            for(int i=0;i<k;i++){
            thread[i]=new Thread(new Runnable(){
                @Override
                public void run() {
                  long StartSumTime=System.currentTimeMillis();
                  int times=10000;
                  while(times-->0){
                      double chance=Math.random();
                      if(chance>0.7&&chance<1){
                          int r=random(1,route);
                          int s1=random(1,station-1);
                          int s2=random(s1+1,station);
                          long StartTime=System.nanoTime();
                          Ticket k=tds.buyTicket("乘客"+Thread.currentThread().getId(),r-1,s1-1,s2-1);
                          long EndTime=System.nanoTime();
                          if(k!=null)
                          History.offer(k);                        
                          buyTime.addAndGet(EndTime-StartTime);
                          buySUM.getAndIncrement();
                     }
                     if(chance>0.1&&chance<0.7){
                       int r=random(1,route);
                       int s1=random(1,station-1);
                       int s2=random(s1+1,station);
                       long StartTime=System.nanoTime();
                       int count= tds.inquiry(r-1,s1-1,s2-1);
                       long EndTime=System.nanoTime();
                       inquiryTime.addAndGet(EndTime-StartTime);
                       inquirySUM.getAndIncrement();
                     }
                     if(chance>0&&chance<0.1){
                          if(!History.isEmpty()){
                              Ticket t=(Ticket)History.poll();
                              long StartTime=System.nanoTime();
                              if(t!=null)
                              tds.refundTicket(t);
                              long EndTime=System.nanoTime();
                              refundSUM.getAndIncrement();
                              refundTime.addAndGet(EndTime-StartTime);
                          }
                     }
                  }
                }
            });
            thread[i].start();
            }
            
            for(int s=0;s<k;s++){
                while(thread[s].isAlive()){}
            }
            long avabuy=buyTime.get()/(buySUM.get()==0?1:buySUM.get());
            long avainquiry=inquiryTime.get()/(inquirySUM.get()==0?1:inquirySUM.get());
            long avarefund=refundTime.get()/(refundSUM.get()==0?1:refundSUM.get());
            double tt=(buySUM.get()+inquirySUM.get()+refundSUM.get())/(double)(buyTime.get()+inquiryTime.get()+refundTime.get());
            System.out.println(k+"线程"+buySUM.get()+"次购票所花平均时间为"+avabuy+"纳秒         ");
            System.out.println(k+"线程"+inquirySUM.get()+"次查询所花平均时间为"+avainquiry+"纳秒          ");
            System.out.println(k+"线程"+refundSUM.get()+"次退票所花平均时间为"+avarefund+"纳秒        ");
            System.out.println(k+"线程吞吐量为"+(tt*1000*1000)+"方法每秒     ");
            buyTime.set(0);
            buySUM.set(0);
            inquiryTime.set(0);
            inquirySUM.set(0);
            refundTime.set(0);
            refundSUM.set(0);
      }
    } 
    private static int random(int a,int b){
        if(a==b) return a;
        else{
           int x=b-a;
           return (int)Math.round(Math.random()*x)+a;                
        }
    }
}

  
