package yemian;
import java.util.*;

public class Yemian
{
    public static int instr_num;
    //页面大小
    public static int[] instr_Seq = null;
    //存放该程序依次执行的指令的有序地址序列
    public static int[] pages = null;
    //存放将有序指令地址序列转换成(经过合并相邻页号)的有序页号序列
    public static int mem_Num;//内存块数


    //指令数量320
    public static int[] push_instr(int instr_num)
    {
        int[] instrSeq=new int [instr_num];
        int count=0;

        while(count<instr_num)
        {
            //在[0,319]的指令地址中随机选取一起点m
            int random1=(int)(Math.random()*(instr_num-1));
            //顺序执行一条指令，即执行地址为m+1的指令
            random1++;
            instrSeq[count]=random1;
            //在前地址[0,m+1]中随机选取一条指令并执行，该指令的地址为 m'
            int random2=(int)(Math.random()*(random1));
            instrSeq[++count]=random2;
            //顺序执行一条指令，其地址为 m'+1
            random2++;
            instrSeq[++count]=random2;
            //在后地址[m'+2，319]中随机选取一条指令并执行
            int random3=(random2+1)+(int)(Math.random()*(319-(random2+1)));
            instrSeq[++count]=random3;
            //重复步骤 A-E，直到 320 次指令


            count++;
        }
        return instrSeq;
    }


    //输出指令地址序列
    public static void show_instrSeq(int[] instr_Seq)
    {
        for (int i = 0; i < instr_Seq.length; i++)
        {
            System.out.printf("%5s", instr_Seq[i]);
            if ((i + 1) % 20 == 0)
            {
                System.out.println();
            }
        }

        System.out.println();
    }

    //将指令序列变换为页地址流
    public static int[] change(int[] instr_Seq, int instr_num)
    {
        ArrayList<Integer> pagesList = new ArrayList<Integer>();
        int temp = -1;
        int pageIndex;//页号
        for (int i = 0; i < instr_Seq.length; i++)
        {
            pageIndex = instr_Seq[i] / instr_num;

            //判断是否与相临的页号相同
            if (pageIndex != temp)
            {
                pagesList.add(pageIndex);
                //下一次循环相邻的页号
                temp = pageIndex;
            }
            //相同则合并为一个
        }


        int[] pages_Seq = new int[pagesList.size()];
        //有序页号序列经合并之后长度最长不超过指令的地址序列长度
        for (int i = 0; i < pagesList.size(); i++)
        {
            pages_Seq[i] = pagesList.get(i);
        }
        return pages_Seq;
    }

    //显示页号
    public static void show_pages(int[] pages_Seq)
    {
        for (int i = 0; i < pages_Seq.length; i++)
        {
            System.out.printf("%5s", pages_Seq[i]);

            if ((i + 1) % 20 == 0)
            {
                System.out.println();
            }
        }

        System.out.println();
    }

    //返回key在array中第一次出现的位置,start和end为数组下标, 找不到则返回-1表示缺页
    public static int findKey(int[] arr, int start, int end, int key)
    {
        for (int i = start; i <= end; i++)
        {
            if (arr[i] == key)
            {
                return i;
            }
        }

        return -1;
    }

    public static void OPT(int[] pages, int mem_num)
    {
        //状态
        int[][] memory_state = new int[pages.length][mem_num];
        //该指针指向将要被置换的内存块的位置（下标位置）
        int curPosition = 0;
        //执行的状态
        int[] tempState = new int[mem_num];
        //记录缺页情况， 1缺页，0不缺页
        int[] isLackOfPage = new int[pages.length];
        Arrays.fill(isLackOfPage, 0, pages.length, 0);
        //缺页次数
        int count_lack = 0;

        //开始时，内存块状态都为空闲（-1表示）
        Arrays.fill(tempState, 0, mem_num, -1);

        for (int i = 0; i < pages.length; i++)
        {
            //缺页
            if(findKey(tempState, 0, mem_num - 1, pages[i]) == -1)
            {
                isLackOfPage[i] = 1;
                count_lack++;

                //判断内存块是否还有剩余
                //有
                if(tempState[mem_num - 1] == -1)
                {
                    tempState[curPosition] = pages[i];
                    curPosition++;
                }
                //无
                else
                {
                    //将来会被访问到的最远位置，设为maxLoc
                    int maxLoc = 0;

                    for(int j = 0; j < mem_num; j++)
                    {
                        //找出当前内存块序列中的内存块tempState[j]在将来会被访问到的（第一个）位置
                        int loc = findKey(pages, i + 1, pages.length - 1, tempState[j]);
                        //如果将来都找不到这个页号
                        if (loc == -1)
                        {
                            curPosition = j;
                            break;
                        }
                        //能找到页号
                        else
                        {
                            //比较哪个页号较远
                            if(maxLoc < loc)
                            {
                                maxLoc = loc;
                                curPosition = j;
                            }
                        }
                    }

                    tempState[curPosition] = pages[i];//替换这个页面
                }
            }

            //保存当前内存块序列的状态
            System.arraycopy(tempState, 0, memory_state[i], 0, mem_num);
        }

        show_memorystate(memory_state, pages, mem_num, isLackOfPage, count_lack);
        System.out.println("OPT算法已经执行完成");

    }

    public static void FIFO(int[] pages, int mem_num)
    {
        //状态
        int[][] memory_state = new int[pages.length][mem_num];
        //该指针指向将要被置换的内存块的位置（下标位置）
        int curPosition = 0;
        //执行的状态
        int[] tempState = new int[mem_num];
        //记录缺页情况， 1缺页，0不缺页
        int[] isLackOfPage = new int[pages.length];
        Arrays.fill(isLackOfPage, 0, pages.length, 0);
        //缺页次数
        int count_lack = 0;
        //开始时，内存块状态都为空闲（-1表示）
        Arrays.fill(tempState, 0, mem_num, -1);

        for (int i = 0; i < pages.length; i++)
        {
            //找不到则缺页
            if (findKey(tempState, 0, mem_num - 1, pages[i]) == -1)
            {
                isLackOfPage[i] = 1;
                count_lack++;
                tempState[curPosition] = pages[i];

                //指针向右移动超过memoryNum时，重置其指向开始的内存块位置0
                if (curPosition + 1 > mem_num - 1)
                {
                    curPosition = 0;
                }
                else
                {
                    curPosition++;
                }
            }

            //保存当前内存块序列的状态 复制到memory_state中
            System.arraycopy(tempState, 0, memory_state[i], 0, mem_num);

        }

        show_memorystate(memory_state, pages, mem_num, isLackOfPage, count_lack);
        System.out.println("FIFO算法已经执行完成");
    }

    //使用了LinkedHashMap
    public static void LRU(int[] pages, int mem_num)
    {
        LRULinkedHashMap<String, Integer> recentVisitedBlocks = new LRULinkedHashMap<String, Integer>(mem_num);
        //状态
        int[][] memory_state = new int[pages.length][mem_num];
        //该指针指向将要被置换的内存块的位置（下标位置）
        int curPosition = 0;

        //执行的状态
        int[] tempState = new int[mem_num];
        //记录缺页情况， 1缺页，0不缺页
        int[] isLackOfPage = new int[pages.length];
        Arrays.fill(isLackOfPage, 0, pages.length, 0);
        //缺页次数
        int lackTimes = 0;

        //开始时，内存块状态都为空闲（-1表示）
        Arrays.fill(tempState, 0, mem_num, -1);

        for (int i = 0; i < pages.length; i++)
        {
            //如果缺页
            if(findKey(tempState, 0, mem_num - 1, pages[i]) == -1)
            {
                isLackOfPage[i] = 1;
                lackTimes++;

                //如果内存块还有剩余
                if(tempState[mem_num - 1] == -1)
                {
                    tempState[curPosition] = pages[i];
                    recentVisitedBlocks.put(String.valueOf(pages[i]), pages[i]);
                    curPosition++;
                }
                //如果内存块都已被使用
                else
                {
                    //找到当前内存块序列中最近最少使用的内存块，并将其置换
                    curPosition = findKey(tempState, 0, mem_num - 1, recentVisitedBlocks.getHead());
                    tempState[curPosition] = pages[i];
                    recentVisitedBlocks.put(String.valueOf(pages[i]), pages[i]);
                }
            }
            //如果不缺页
            else
            {
                //将这里被使用的pageSequence[i]在最近使用的内存块集合中的原先位置调整到最近被访问的位置
                recentVisitedBlocks.get(String.valueOf(pages[i]));
            }

            //保存当前内存块序列的状态
            System.arraycopy(tempState, 0, memory_state[i], 0, mem_num);
        }

        show_memorystate(memory_state, pages, mem_num, isLackOfPage, lackTimes);
        System.out.println("LRU算法已执行完成");

    }



    public static void show_memorystate(int[][] memory_State, int[] pages, int mem_num, int[] isLackofPage, int lackTimes)
    {
        String[] pagesDescription = {"不缺页", "缺页"};

        int pages_Length = pages.length;//有序页号序列的长度

        for (int i = 0; i < pages_Length; i++)
        {
            System.out.println("当前访问的页号：" + pages[i]);
            System.out.print("\t");
            System.out.print("\n\t");

            for (int k = 0; k < mem_num; k++)
            {
                if (k == 0)
                {
                    System.out.print("|");
                }
                //如果当前内存块还没被使用，置为空
                if (memory_State[i][k] == -1)
                {
                    System.out.printf("%5s|", " ");
                }
                else
                {
                    System.out.printf("%5s|", memory_State[i][k]);
                }
            }

            System.out.print("  缺页情况：" + pagesDescription[isLackofPage[i]]);

            System.out.print("\n\t");
            System.out.println();
        }

        //缺页率
        double lackOfPagesRate = lackTimes * 1.0 / pages.length;

        System.out.println("\n该程序的页号序列长度为：" + pages.length + ", 执行该算法后，缺页次数为：" + lackTimes + ", 缺页率为：" + lackOfPagesRate * 100 + "%");
    }

    //main函数
    public static void main(String[] args)
    {

        Scanner scan = new Scanner(System.in);
        while (true)
        {
            System.out.println("程序包含的指令数量为320条,如果需要退出系统请输入-1，否则将运行");

            int input = scan.nextInt();
            if(input == -1) break;//为-1退出系统
            int instructionsNum = 320;//320
            //产生指令队列
            instr_Seq = push_instr(instructionsNum);
            System.out.println("系统随机生成的指令地址序列如下：");
            show_instrSeq(instr_Seq);
            System.out.println();
            System.out.println("请输入页面大小（1,2,4,8,16 即 1k,2k,4k,8k,16k）：");

            instr_num = scan.nextInt() * 10;//每 K 存放 10 条指令排列虚存地址
            pages = change(instr_Seq, instr_num);
            System.out.println("该指令地址序列对应的页号序列如下(相邻相同的页号合并为一个)：");
            show_pages(pages);
            System.out.println();

            System.out.println("页号个数为：" + pages.length);
            System.out.println();

            //分配给程序的内存块数
            System.out.println("输入分配给该程序的内存块数:即1~" + pages.length + "个)");
            mem_Num = scan.nextInt();
            while(true)
            {
                System.out.println("输入 1：OPT， 2：FIFO， 3：LRU, -1：退出)");
                int n = scan.nextInt();
                if(n == -1) break;

                switch (n)
                {
                    case 1:
                        OPT(pages, mem_Num);
                        break;
                    case 2:
                        FIFO(pages, mem_Num);
                        break;
                    case 3:
                        LRU(pages, mem_Num);
                        break;
                    default:
                        System.out.println("请输入正确的数字！");
                }

                System.out.println();
            }
            System.out.println("\n\n");
        }

    }

}


//LRU算法的辅助存储类
class LRULinkedHashMap<K, V> extends LinkedHashMap<K, V>
{
    //最大内存块数（容量）
    private int maxMemoryBlocksNum;

    //设置默认负载因子
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    public LRULinkedHashMap(int maxCapacity)
    {
        //设置accessOrder为true，保证了LinkedHashMap底层实现的双向链表是按照访问的先后顺序排序
        super(maxCapacity, DEFAULT_LOAD_FACTOR, true);
        this.maxMemoryBlocksNum = maxCapacity;
    }

    //得到最近最少被访问的元素
    public V getHead()
    {
        return (V) this.values().toArray()[0];
    }

    //移除多余的最近最少被访问的元素
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest)
    {
        return size() > maxMemoryBlocksNum;
    }
}