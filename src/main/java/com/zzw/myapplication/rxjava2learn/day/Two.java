package com.zzw.myapplication.rxjava2learn.day;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zqy on 2017/10/24.
 * 学习RxJava强大的线程控制.
 * <p>
 * Schedulers.io() 代表io操作的线程, 通常用于网络,读写文件等io密集型的操作
 * Schedulers.computation() 代表CPU计算密集型的操作, 例如需要大量计算的操作
 * Schedulers.newThread() 代表一个常规的新线程
 * AndroidSchedulers.mainThread() 代表Android的主线程
 *
 * 上游虽然指定了多次线程, 但只有第一次指定的有效
 * 下游每调用一次observeOn() 线程便会切换一次
 */

public class Two {
	public Two() {
		//主线程去创建一个下游Observer来接收事件, 则这个下游默认就在主线程中接收事件,上下游默认是在同一个线程工作.
		two_1();
	}

	private void two_1() {
		Observable observable = Observable.create(new ObservableOnSubscribe() {
			@Override
			public void subscribe(@NonNull ObservableEmitter e) throws Exception {
				e.onNext(1);
				System.out.println("rxjava2 subscribe 线程 " + Thread.currentThread().getName());
			}
		});

		Consumer<Integer> consumer = new Consumer<Integer>() {
			@Override
			public void accept(Integer integer) throws Exception {
				System.out.println("rxjava2 accept 线程 " + Thread.currentThread().getName());
			}
		};

		//observable.subscribe(consumer);
		observable
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(consumer);
	}
}
