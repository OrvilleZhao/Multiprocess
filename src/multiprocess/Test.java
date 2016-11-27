/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiprocess;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
/**
 *
 * @author Administrator
 */
public class Test{
    /**
     * @param args the command line arguments
     */
    private static int route=1;
    private static int coach=1;
    private static int seat=1;
    private static int station=2;
    private static ConcurrentLinkedQueue History=new ConcurrentLinkedQueue();
    public static void main(String[] args) {
      final TicketingDS tds=new TicketingDS(route,coach,seat,station);
      int k=4;
      for(int i=0;i<k;i++)
      new Thread(new Runnable(){
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
                    if(k!=null){
                        History.offer(k);
                        System.out.println("成功订票:票的信息为:乘客姓名:"+k.passenger+" 车票编号:"+k.tid+" 列车车次:"+r+" 车厢编号:"+(k.coach+1)+" 座位号:"+(k.seat+1)+" 出发站编号:"+(k.departure+1)+" 终点站编号:"+(k.arrival+1)+
                                "该事务执行时间为"+(EndTime-StartTime)+"纳秒");
                    }
                    else{
                        System.out.println("购票失败,购票请求为:车次"+r+",始发站:"+s1+",终点站:"+s2+
                                "该事务执行时间为"+(EndTime-StartTime)+"纳秒");
                    }
               }
               if(chance>0.1&&chance<0.7){
                 int r=random(1,route);
                 int s1=random(1,station-1);
                 int s2=random(s1+1,station);
                 long StartTime=System.nanoTime();
                 int count= tds.inquiry(r-1,s1-1,s2-1);
                 long EndTime=System.nanoTime();
                 if(count==-1){
                     System.out.println("无效的查询行为"+"该事务执行时间为"+(EndTime-StartTime)+"纳秒");
                 }else{
                 System.out.println("您查询的"+r+"次列车由"+s1+"站开往"+s2+"站还剩下"+count+"个席次"+
                         "该事务执行时间为"+(EndTime-StartTime)+"纳秒");
                 }
               }
               if(chance>0&&chance<0.1){
                    if(!History.isEmpty()){
                        Ticket t=(Ticket)History.poll();
                        long StartTime=System.nanoTime();
                        if(t!=null)
                        if(tds.refundTicket(t)){
                            long EndTime=System.nanoTime();
                          System.out.println("您订购的编号为:"+t.tid+"的车票已经退订！"+
                                  "该事务执行时间为"+(EndTime-StartTime)+"纳秒");
                        }else{
                            long EndTime=System.currentTimeMillis();
                            System.out.println("错误的车票编号，请查询后再退订！"+
                                    "该事务执行时间为"+(EndTime-StartTime)+"纳秒");
                        }
                    }
               }
            }
            DecimalFormat   df=new   java.text.DecimalFormat("#.##");   
            long EndSumTime=System.currentTimeMillis();
            double t =10000000.0/(EndSumTime-StartSumTime==0?1:(EndSumTime-StartSumTime));
            System.out.println("系统吞吐率为"+df.format(t)+"方法每秒");
          }
      }).start();
    } 
    private static int random(int a,int b){
        if(a==b) return a;
        else{
           int x=b-a;
           return (int)Math.round(Math.random()*x)+a;                
        }
    }
}

  
