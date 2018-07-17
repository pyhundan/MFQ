package filework;

public class PCB {
    public PCB(){
    }

    public PCB(int pid,String status,int priority,int life){
        this.pid=pid;
        this.status=status;
        this.priority=priority;
        this.life=life;
    }
    public int get_pid()
    {
        return pid;
    }
    public void set_pid(int pid)
    {
        this.pid=pid;
    }
    public String get_status()
    {
        return status;
    }
    public void set_status(String status)
    {
        this.status=status;
    }
    public int get_priority()
    {
        return priority;
    }
    public void set_priority(int priority)
    {
        this.priority=priority;
    }
    public int get_life()
    {
        return life;
    }
    public void set_life(int life)
    {
        this.life=life;
    }


    private int pid; //进程标识符

    private String status; //进程状态

    private int priority;//进程优先级

    private int life;//进程的生命周期


}
