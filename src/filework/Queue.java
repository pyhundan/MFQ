package filework;

import java.util.LinkedList;

public class Queue {
    private int priority;
    private LinkedList<PCB> list=new LinkedList<PCB>();

    public Queue(int priority){
        this.priority=priority;
    }

    public int get_priority()
    {
        return priority;
    }

    public void set_priority(int priority)
    {
        this.priority=priority;
    }

    public LinkedList<PCB> get_list()
    {
        return list;
    }

    public void set_list(LinkedList<PCB> list)
    {
        this.list=list;
    }
}
