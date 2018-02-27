package com.zzw.myapplication.rxjava2learn.day;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by zqy on 2017/10/24.
 * 基本的RxJava2的使用
 */

public class One {

	public One() {
		//one_1();
		one_2();
		//one_3();
	}

	/**
	 * 不带任何参数的subscribe() 表示下游不关心任何事件,你上游尽管发你的数据去吧, 老子可不管你发什么.
	 * <p>
	 * 带有一个Consumer参数的方法表示下游只关心onNext事件, 其他的事件我假装没看见, 因此我们如果只需要onNext事件可以这么写:
	 */
	private void one_3() {
		Observable.create(new ObservableOnSubscribe<Integer>() {
			@Override
			public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
				e.onNext(1);
				e.onNext(2);
				e.onNext(3);
				e.onComplete();
			}
		}).subscribe(new Consumer<Integer>() {
			@Override
			public void accept(Integer integer) throws Exception {
				System.out.println("rxjava2 -- accept " + integer);
			}
		});
	}

	private void one_2() {
		Observable.create(new ObservableOnSubscribe<Integer>() {
			@Override
			public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
				System.out.println("rxjava2 -- e 1");
				e.onNext(1);
				System.out.println("rxjava2 -- e 2");
				e.onNext(2);
				System.out.println("rxjava2 -- e 3");
				e.onNext(3);
				System.out.println("rxjava2 -- e--onComplete()");
				e.onComplete();
				System.out.println("rxjava2 -- e 4");
				e.onNext(4);
			}
		}).subscribe(new Observer<Integer>() {
			Disposable disposable;
			int i;

			@Override
			public void onSubscribe(@NonNull Disposable d) {
				System.out.println("rxjava2 -- onSubscribe");
				disposable = d;
			}

			@Override
			public void onNext(@NonNull Integer integer) {
				System.out.println("rxjava2 -- onNext " + integer);
				i++;
				if (i == 2) {
					disposable.dispose();
					System.out.println("rxjava2 -- isDisposed " + disposable.isDisposed());
				}
			}

			@Override
			public void onError(@NonNull Throwable e) {

			}

			@Override
			public void onComplete() {

			}
		});
	}

	/**
	 * <pre>
	 * 注: 关于onComplete和onError唯一并且互斥这一点, 是需要自行在代码中进行控制,
	 * 如果你的代码逻辑中违背了这个规则, 并不一定会导致程序崩溃.
	 * 比如发送多个onComplete是可以正常运行的, 依然是收到第一个onComplete就不再接收了,
	 * 但若是发送多个onError, 则收到第二个 onError 事件会导致程序会崩溃.
	 * @see Observer
	 * {@link ObservableOnSubscribe}
	 * @throws One
	 * </pre>
	 */
	private void one_1() {
		Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
			@Override
			public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
				e.onNext(0);
				e.onNext(1);
				e.onNext(2);
				e.onNext(3);
				//当上游发送了一个onComplete后, 上游onComplete之后的事件将会继续发送, 而下游收到onComplete事件之后将不再继续接收事件.
				//上游可以不发送onComplete或onError.
				e.onComplete();
			}
		});
		//最为关键的是onComplete和onError必须唯一并且互斥, 即不能发多个onComplete,
		// 也不能发多个onError, 也不能先发一个onComplete, 然后再发一个onError, 反之亦然
		Observer<Integer> observer = new Observer<Integer>() {
			@Override
			public void onSubscribe(@NonNull Disposable d) {
				System.out.println("rxjava2 -- onSubscribe");
			}

			@Override
			public void onNext(@NonNull Integer integer) {
				//上游可以发送无限个onNext, 下游也可以接收无限个onNext.
				System.out.println("rxjava2 -- " + integer);

			}

			@Override
			public void onError(@NonNull Throwable e) {
				//当上游发送了一个onError后, 上游onError之后的事件将继续发送, 而下游收到onError事件之后将不再继续接收事件.
				System.out.println("rxjava2 -- onError");
			}

			@Override
			public void onComplete() {
				System.out.println("rxjava2 -- onComplete");
			}
		};
		//发送者      订阅      消费者
		observable.subscribe(observer);
	}
}
