package com.zzw.myapplication.rxjava2learn.day;

import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zqy on 2017/10/24.
 * <p>
 * 上下游在同一个线程中的时候，在下游调用request(n)就会直接改变上游中的requested的值，
 * 多次调用便会叠加这个值，而上游每发送一个事件之后便会去减少这个值，当这个值减少至0的时候，继续发送事件便会抛异常了
 * </p>
 * <p>
 * 异步情况下 BackpressureStrategy.ERROR
 * 又是128，看了我前几篇文章的朋友肯定很熟悉这个数字啊！这个数字为什么和我们之前所说的默认的水缸大小一样啊
 * <p>
 * 当上下游工作在不同的线程里时，每一个线程里都有一个requested，而我们调用request（1000）时，
 * 实际上改变的是下游主线程中的requested，而上游中的requested的值是由RxJava内部调用request(n)去设置的，
 * 这个调用会在合适的时候自动触发。
 * </p>
 */

public class Ten {
	private static final String TAG = "TEN";

	public Ten() {
	}

	Subscription mSubscription = null;

	//同步
	public void demo1() {
		Flowable
				.create(new FlowableOnSubscribe<Integer>() {
					@Override
					public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
						Log.d(TAG, "current requested: " + emitter.requested());
						//这里输出的是 10
					}
				}, BackpressureStrategy.ERROR)
				.subscribe(new Subscriber<Integer>() {

					@Override
					public void onSubscribe(Subscription s) {
						Log.d(TAG, "onSubscribe");
						mSubscription = s;
						s.request(10); //我要打十个！
					}

					@Override
					public void onNext(Integer integer) {
						Log.d(TAG, "onNext: " + integer);
					}

					@Override
					public void onError(Throwable t) {
						Log.w(TAG, "onError: ", t);
					}

					@Override
					public void onComplete() {
						Log.d(TAG, "onComplete");
					}
				});
	}

	//同步
	public void demo2() {
		Flowable
				.create(new FlowableOnSubscribe<Integer>() {
					@Override
					public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
						Log.d(TAG, "current requested: " + emitter.requested());
						//这里输出的是110
					}
				}, BackpressureStrategy.ERROR)
				.subscribe(new Subscriber<Integer>() {

					@Override
					public void onSubscribe(Subscription s) {
						Log.d(TAG, "onSubscribe");
						mSubscription = s;
						s.request(10);  //我要打十个!
						s.request(100); //再给我一百个！
					}

					@Override
					public void onNext(Integer integer) {
						Log.d(TAG, "onNext: " + integer);
					}

					@Override
					public void onError(Throwable t) {
						Log.w(TAG, "onError: ", t);
					}

					@Override
					public void onComplete() {
						Log.d(TAG, "onComplete");
					}
				});
	}

	//异步的时候,上游下游什么操作都不做 request 128个事件
	public void demo3() {
		Flowable
				.create(new FlowableOnSubscribe<Integer>() {
					@Override
					public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
						Log.d(TAG, "current requested: " + emitter.requested());
						//这里输出的是128
					}
				}, BackpressureStrategy.ERROR)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<Integer>() {

					@Override
					public void onSubscribe(Subscription s) {
						Log.d(TAG, "onSubscribe");
						mSubscription = s;
					}

					@Override
					public void onNext(Integer integer) {
						Log.d(TAG, "onNext: " + integer);
					}

					@Override
					public void onError(Throwable t) {
						Log.w(TAG, "onError: ", t);
					}

					@Override
					public void onComplete() {
						Log.d(TAG, "onComplete");
					}
				});
	}

	//异步,下游设置的是1000个事件,但是上游输出的还是128
	public void demo4() {
		Flowable
				.create(new FlowableOnSubscribe<Integer>() {
					@Override
					public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
						Log.d(TAG, "current requested: " + emitter.requested());
						//输出128
					}
				}, BackpressureStrategy.ERROR)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<Integer>() {

					@Override
					public void onSubscribe(Subscription s) {
						Log.d(TAG, "onSubscribe");
						mSubscription = s;
						s.request(1000); //我要打1000个！！
					}

					@Override
					public void onNext(Integer integer) {
						Log.d(TAG, "onNext: " + integer);
					}

					@Override
					public void onError(Throwable t) {
						Log.w(TAG, "onError: ", t);
					}

					@Override
					public void onComplete() {
						Log.d(TAG, "onComplete");
					}
				});
	}

	public void request() {
		mSubscription.request(96); //请求96个事件
	}

	/**
	 * <p>
	 * 这里判断的是当emitter.requested() 不等于 0 才发送事件
	 * 上游会发送128个事件到内存里 , 下游调用request()请求消费了96个事件后 ,上游的值会变成96
	 * 然后上游又会发送96事件个事件 , 下游请求是的96是有原因的,如果是95上游是不会发送事件的
	 * </p>
	 */
	public void demo5() {
		Flowable
				.create(new FlowableOnSubscribe<Integer>() {
					@Override
					public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
						Log.d(TAG, "First requested = " + emitter.requested());
						boolean flag;
						for (int i = 0; ; i++) {
							flag = false;
							while (emitter.requested() == 0) {
								if (!flag) {
									Log.d(TAG, "Oh no! I can't emit value!");
									flag = true;
								}
							}
							emitter.onNext(i);
							Log.d(TAG, "emit " + i + " , requested = " + emitter.requested());
						}
					}
				}, BackpressureStrategy.ERROR)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<Integer>() {

					@Override
					public void onSubscribe(Subscription s) {
						Log.d(TAG, "onSubscribe");
						mSubscription = s;
					}

					@Override
					public void onNext(Integer integer) {
						Log.d(TAG, "onNext: " + integer);
					}

					@Override
					public void onError(Throwable t) {
						Log.w(TAG, "onError: ", t);
					}

					@Override
					public void onComplete() {
						Log.d(TAG, "onComplete");
					}
				});
	}
}
