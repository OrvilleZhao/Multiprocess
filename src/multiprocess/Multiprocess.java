/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiprocess;

import java.util.Random;

/**
 *
 * @author Administrator
 */
public class Multiprocess{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      final TicketingDS tds=new TicketingDS(1,1,10,4);
      int i=100;
      while(i-->0){
      new Thread(new Runnable(){
          @Override
          public void run() {
            Random seed=new Random();
            Ticket k=tds.buyTicket("hhhh",0,seed.nextInt(3),seed.nextInt(3));
            if(k!=null){
                System.out.println("成功订票:票的信息为:乘客姓名:"+k.passenger+" 车票编号:"+k.tid+" 列车车次:"+k.route+" 车厢编号:"+k.coach+" 座位号:"+k.seat+" 出发站编号:"+k.departure+" 终点站编号:"+k.arrival);
            }
            else{
                //System.out.println("购票失败");
            }
          }
      }).start();
      }
     /* i=100;
      while(i-->0){
          new Thread(
            new Runnable(){
              @Override
              public void run() {
                 Random seed=new Random();
                 int a=seed.nextInt(3);
                 int b=seed.nextInt(3);
                 int count= tds.inquiry(0,a,b);
                 if(count==-1){
                     System.out.println("无效的查询行为");
                 }else{
                 System.out.println("您查询的0次由"+a+"开往"+b+"还剩下"+count+"个席次");
                 }
              }
            } 
          ).start();
      }*/
    } 
}

  
