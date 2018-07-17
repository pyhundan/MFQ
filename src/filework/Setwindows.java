package filework;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.LinkedList;


public class Setwindows {

    private JFrame jf;
    private JPanel jp;
    private JMenuItem m_creatPro,m_start,m_stop,m_quit;
    private JMenuItem m_about;


    public static double timeSlice = 0.5; //设置优先级最高
    public static double PCV_time[] = new double[50];//设置每个队列时间片
    public static Queue[] PCB_Queues = new Queue[50];  //多级反馈队列
    public static int[] pidsUsed = new int[101]; //记录已使用的pid
    public static int current = 0;//当前内存中的进程数
    public static final int PCBS_MAX = 100; //内存中能够容纳的最大进程数
    public static boolean flag=true;//flag判断是否开始或者停止


    public void setwindow(){
        jf=new JFrame("多级反馈调度算法");
        jp=new JPanel();
        JScrollPane jScrollPane=new JScrollPane(jp,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        Font ft=new Font("宋体",Font.PLAIN,20);
        JMenuBar jmb=new JMenuBar();
        JMenu jm_option,jm_help;
        jm_option=new JMenu("设置");
        jm_help=new JMenu("帮助");
        //第一个菜单
        m_creatPro=new JMenuItem("创建进程");
        m_start=new JMenuItem("开始");
        m_stop=new JMenuItem("暂停");
        m_quit=new JMenuItem("退出");
        jm_option.add(m_creatPro);
        jm_option.addSeparator();
        jm_option.add(m_start);
        jm_option.addSeparator();
        jm_option.add(m_stop);
        jm_option.addSeparator();
        jm_option.add(m_quit);
        //第二个菜单
        m_about=new JMenuItem("关于");
        jm_help.add(m_about);
        //添加入菜单栏
        jmb.add(jm_option);
        jmb.add(jm_help);
        jf.setJMenuBar(jmb);

        init_Memory();

        jp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        jf.setContentPane(jScrollPane);//滚条
        jf.setBounds(250,50,810,630);
        jf.setVisible(true);
        jf.setResizable(false);//窗口不可拉伸
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setListener();
    }
    public static void init_Memory(){
        current = 0;
        Arrays.fill(pidsUsed, 1, 101, 0);//设置数组元素初值

        for(int i = 0; i < PCB_Queues.length; i++)
        {
            PCB_Queues[i] = new Queue(i);
        }//创建队列

        for(int i = PCV_time.length - 1; i >= 0; i--)
        {
            //队列优先级每降一级，时间片增加0.1秒
            PCV_time[i] = timeSlice;
            timeSlice += 0.1;
        }

    }
    //创建一个PCB
    public void creat_pcb(){

        if(current == PCBS_MAX)
        {
            JOptionPane.showMessageDialog(null,"超过最大的进程数");
        }
        else{
            current++;//

            int random_Pid = 1 + (int)(Math.random() * (100));
            while(pidsUsed[random_Pid] == 1)
            {
                random_Pid = 1 + (int)(Math.random() * (100));
            }

            pidsUsed[random_Pid] = 1;

            int random_Priority = 0 + (int)(Math.random() * (50));
            int random_Life = 1 + (int)(Math.random() * (5));

            PCB pcb = new PCB(random_Pid, "Ready", random_Priority, random_Life);

            LinkedList<PCB> queue = PCB_Queues[random_Priority].get_list();
            queue.offer(pcb);
            PCB_Queues[random_Priority].set_list(queue);

            show_PCB(PCB_Queues);
        }

    }
    //开始
    public void set_start(){
        flag = false;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //当前内存中还留有进程未执行
                while(current !=0 && !flag)
                {
                    for(int i = PCB_Queues.length - 1; i >= 0; i--)
                    {
                        LinkedList<PCB> queue = PCB_Queues[i].get_list();

                        if (queue.size() > 0)
                        {
                            //读取该队列首个PCB
                            PCB pcb = queue.element();//方法检索，但是不移除此列表的头(第一个元素)
                            pcb.set_status("Running");
                            show_PCB(PCB_Queues);//显示

                            int pid = pcb.get_pid();
                            int priority = pcb.get_priority();
                            int life = pcb.get_life();
                            priority = priority / 2;
                            life = life - 1;

                            //通过延时来模拟该进程的执行
                            try
                            {
                                Thread.sleep((int)(PCV_time[priority] * 1000));
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }

                            //若该进程执行完成
                            if(life == 0)
                            {
                                queue.poll();//移除队列中首个PCB
                                pidsUsed[pid] = 0;
                                current--;
                            }
                            //若该进程还未执行完成,则改变其PCB的相关参数,并插入其优先级所对应的队列尾部
                            else
                            {
                                //移除该队列的首个PCB
                                queue.poll();

                                pcb.set_priority(priority);
                                pcb.set_life(life);
                                pcb.set_status("Ready");
                                LinkedList<PCB> nextQueue = PCB_Queues[priority].get_list();
                                nextQueue.offer(pcb);
                                PCB_Queues[priority].set_list(nextQueue);
                            }

                            break;
                        }
                    }
                }

                init_Memory();
                show_PCB(PCB_Queues);
                //完成后显示弹窗
                JOptionPane.showMessageDialog(null, "进程调度完成!");
            }
        }).start();

    }
    //停止
    public void stop(){
        flag = true;
        init_Memory();
    }
    //显示PCB
    public void show_PCB(Queue[] PCBsQueues){

        int queueLocationY = 0;
        JPanel queuesPanel = new JPanel();

        for(int i = PCBsQueues.length - 1; i >= 0; i--)
        {
            LinkedList<PCB> queue = PCBsQueues[i].get_list();

            if (queue.size() > 0)
            {
                //创建PCB队列
                JPanel PCBsQueue = new JPanel();
                PCBsQueue.setLayout(new FlowLayout(FlowLayout.LEFT));
                PCBsQueue.setBounds(0, queueLocationY, 800, 700);

                queueLocationY += 50;

                //创建队列前面的优先级提示块
                JLabel PCBsQueuePriorityLabel = new JLabel("优先级: " + String.valueOf(i));
                PCBsQueuePriorityLabel.setOpaque(true);
                PCBsQueuePriorityLabel.setBackground(Color.RED);
                PCBsQueuePriorityLabel.setForeground(Color.YELLOW);

                JPanel PCBsQueuePriorityBlock = new JPanel();
                PCBsQueuePriorityBlock.add(PCBsQueuePriorityLabel);

                PCBsQueue.add(PCBsQueuePriorityBlock);

                for (PCB pcb : queue)
                {
                    //pid标签
                    JLabel pidLabel = new JLabel("标识符: " + String.valueOf(pcb.get_pid()));
                    pidLabel.setOpaque(true);
                    //status标签
                    JLabel statusLabel = new JLabel("状态: " + pcb.get_status());
                    statusLabel.setOpaque(true);
                    //priority标签
                    JLabel priorityLabel = new JLabel("优先级: " + String.valueOf(pcb.get_priority()));
                    priorityLabel.setOpaque(true);
                    //life标签
                    JLabel lifeLabel = new JLabel("生命周期: " + String.valueOf(pcb.get_life()));
                    lifeLabel.setOpaque(true);
                    //绘制
                    JPanel PCBPanel = new JPanel();
                    PCBPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    PCBPanel.add(pidLabel);
                    PCBPanel.add(statusLabel);
                    PCBPanel.add(priorityLabel);
                    PCBPanel.add(lifeLabel);
                    //将PCB加入队列
                    PCBsQueue.add(new DrawLinePanel());
                    PCBsQueue.add(PCBPanel);
                }

                queuesPanel.add(PCBsQueue);
            }
        }


        //按垂直方向排列
        BoxLayout boxLayout = new BoxLayout(queuesPanel, BoxLayout.Y_AXIS);
        queuesPanel.setLayout(boxLayout);

        queuesPanel.setSize(800, 700);

        jp.setLayout(new FlowLayout(FlowLayout.LEFT));
        jp.removeAll();
        jp.add(queuesPanel);
        jp.updateUI();
        jp.repaint();
    }


    public  void setListener(){
        m_creatPro.setAccelerator(KeyStroke.getKeyStroke('F', InputEvent.CTRL_MASK));
        m_creatPro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                creat_pcb();
            }
        });

        m_start.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_MASK));
        m_start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                set_start();
            }
        });

        m_stop.setAccelerator(KeyStroke.getKeyStroke('A', InputEvent.CTRL_MASK));
        m_stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                stop();
            }
        });


        m_quit.setAccelerator(KeyStroke.getKeyStroke('Q', InputEvent.CTRL_MASK));
        m_quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                System.exit(0);
            }
        });

        m_about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JOptionPane.showMessageDialog(null,"20162180099罗杰斌");
            }
        });

    }

    class DrawLinePanel extends JPanel
    {
        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            g.drawLine(0, this.getSize().height / 2, this.getSize().width, this.getSize().height/2);

        }

    }
    //main
    public static void main(String[] args)
    {
        new Setwindows().setwindow();
    }

}
