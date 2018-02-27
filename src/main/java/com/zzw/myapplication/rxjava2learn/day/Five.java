package com.zzw.myapplication.rxjava2learn.day;

/**
 * Created by zqy on 2017/10/24.
 */

public class Five {
	/**
	 *异步线程,发射端会发射事件到内存里(可想象为水缸),处理事件时速度慢,水缸就满了,发生了OOM
	 *因为上下游工作在同一个线程呀骚年们! 这个时候上游每次调用emitter.onNext(i)其实就相当于直接调用了Consumer中的:不会OOM
	 */
	String msg = "当上下游工作在同一个线程中时, 这时候是一个同步的订阅关系, 也就是说上游每发送一个事件必须等到下游接收处理完了以后才能接着发送下一个事件.\n" +
			"当上下游工作在不同的线程中时, 这时候是一个异步的订阅关系, 这个时候上游发送数据不需要等待下游接收, 为什么呢, 因为两个线程并不能直接进行通信,\n" +
			" 因此上游发送的事件并不能直接到下游里去, 这个时候就需要一个缸 ! 上游把事件发送到水缸里去,\n " +
			"下游从水缸里取出事件来处理, 因此, 当上游发事件的速度太快, 下游取事件的速度太慢, 水缸就会迅速装满, 然后溢出来, 最后就OOM了.\n";
	public Five() {
		System.out.println("rxjava ---- "+msg);
	}
}
