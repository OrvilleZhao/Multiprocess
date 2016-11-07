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
    int departure;//出发站编号
    int arrival;//到达站编号
}
public interface TicketingSystem {
    Ticket buyTicket(String passenger,int route,int departure,int arrival);
    int inquiry(int route,int departure,int arrival);
    boolean refundTicket(Ticket ticket);
}
