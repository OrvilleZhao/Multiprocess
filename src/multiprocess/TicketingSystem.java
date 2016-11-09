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
class Ticket{
    long tid;//车票编号
    String passenger;//乘客姓名
    int route;//列车车次
    int coach;//车厢
    int seat;//座位
    int departure;//出发站编号
    int arrival;//到达站编号
}
public interface TicketingSystem {
    //购票方法
    Ticket buyTicket(String passenger,int route,int departure,int arrival);
    //查询余票方法
    int inquiry(int route,int departure,int arrival);
    //退票方法
    boolean refundTicket(Ticket ticket);
}
