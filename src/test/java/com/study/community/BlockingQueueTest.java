package com.study.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @ClassName community BlockingQueueTest
 * @Author 陈必强
 * @Date 2021/1/3 19:37
 * @Description 阻塞队列--演示
 **/
public class BlockingQueueTest {

    //主线程
    public static void main(String[] args) {
        //实例化阻塞队列:底层为数据的阻塞队列，队列容量为10
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(10);
        //生产者线程不断生产数据
        new Thread(new Producer(queue)).start();
        //多个消费者线程不断消费数据
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
    }

}

//生产者（线程）
class Producer implements Runnable{

    //阻塞队列
    private BlockingQueue<Integer> queue;

    //实例化这个生产者线程时，需要把阻塞队列传入（因为该线程是由阻塞队列管理的）
    public Producer(BlockingQueue<Integer> queue){
        this.queue = queue;
    }

    @Override
    public void run() {
        try{
            //频繁地生成一些数据（生产者），存放在队列中
            for (int i = 0; i < 100; i++) {
                //停顿20ms,设置中间的时间间隔
                Thread.sleep(20);
                queue.put(i);
                System.out.println(Thread.currentThread().getName()+"生产："+queue.size());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

//消费者（线程）
class Consumer implements Runnable{

    private BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue){
        this.queue = queue;
    }

    @Override
    public void run() {
        //消费者一直消费（只要队列中有数据）
        try{
            while (true){
                //停顿0-1000之间随机ms[消费者的消费能力没有生产者快],设置中间的时间间隔
                Thread.sleep(new Random().nextInt(1000));
                //弹出队首元素
                queue.take();
                System.out.println(Thread.currentThread().getName()+"消费："+queue.size());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
