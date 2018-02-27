package com.zzw.myapplication.rxjava2learn.day;

import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zqy on 2017/10/24.
 * 如何使用Flowable
 * BackpressureStrategy.ERROR // 128
 * BackpressureStrategy.BUFFER //无穷大 OOM 看上去和Obserable功能一样但性能差点
 * <p>
 * Drop就是直接把存不下的事件丢弃,Latest就是只保留最新的事件
 * </p>
 * BackpressureStrategy.DROP
 * BackpressureStrategy.LATEST
 * <p>
 *     RxJava中的interval操作符, 这个操作符并不是我们自己创建的
 * .onBackpressureBuffer()
 * .onBackpressureDrop()
 * .onBackpressureLatest()
 * </p>
 */

public class Seven {
	public Seven() {
		seven_one();
		seven_two();
	}

	Subscription mSubscription;

	private void seven_two() {
		Flowable.create(new FlowableOnSubscribe<Integer>() {
			@Override
			public void subscribe(@NonNull FlowableEmitter<Integer> e) throws Exception {
				/**
				 * for (int i = 0; i < 129; i++) {
				 * 在上游发送了  第129个事件  的时候, 就抛出了MissingBackpressureException异常, 提醒我们发洪水啦.
				 * 当然了, 这个128也不是我凭空捏造出来的, Flowable的源码中就有这个buffersize的大小定义, 可以自行查看.
				 */
				for (int i = 0; i < 128; i++) {
					Log.d("rxjava", "emit " + i);
					e.onNext(i);
				}
			}
		}, BackpressureStrategy.ERROR)
				/**
				 * <p>
				 *     带着这个疑问, 我们再来看看异步的情况:
				 *     异步情况下不写这个上游会全部发送,但是下游一个都收不到
				 *     因为在Flowable里默认有一个大小为  128 的水缸, 当上下游工作在不同的线程中时,上游就会先把事件发送到这个水缸中,
				 *     因此, 下游虽然没有调用request, 但是上游在水缸中保存着这些事件, 只有当下游调用request时, 才从水缸里取出事件发给下游.
				 * </p>
				 */
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<Integer>() {
					@Override
					public void onSubscribe(Subscription s) {
						mSubscription = s;
						/**
						 * 这里我们把Subscription保存起来, 在界面上增加了一个按钮, 点击一次就调用Subscription.request(1), 来看看运行结果:
						 */
					}

					@Override
					public void onNext(Integer integer) {
						System.out.println("rxjava  " + integer);
					}

					@Override
					public void onError(Throwable t) {

					}

					@Override
					public void onComplete() {

					}
				});

	}

	private void seven_one() {
		Flowable<Integer> upstream = Flowable.create(new FlowableOnSubscribe<Integer>() {
			@Override
			public void subscribe(@NonNull FlowableEmitter<Integer> e) throws Exception {

				e.onNext(1);
				e.onNext(2);
				e.onNext(3);
				e.onNext(4);
				e.onComplete();
				System.out.println("rxjava 发射");
			}
		}, BackpressureStrategy.ERROR);
		Subscriber<Integer> downstream = new Subscriber<Integer>() {
			@Override
			public void onSubscribe(Subscription s) {
				//BackpressureStrategy.ERROR 的时候:onErrorio.reactivex.exceptions.
				// MissingBackpressureException:
				// create: could not emit value due to lack of requests

				System.out.println("rxjava flowable onSubscribe");
				s.request(Long.MAX_VALUE);   //TODO 记得加这句

				/**
				 * <p>
				 *     带着这个疑问, 我们再来看看异步的情况:
				 *     异步情况下不写这个上游会全部发送,但是下游一个都收不到
				 *     因为在Flowable里默认有一个大小为  128 的水缸, 当上下游工作在不同的线程中时,上游就会先把事件发送到这个水缸中,
				 *     因此, 下游虽然没有调用request, 但是上游在水缸中保存着这些事件, 只有当下游调用request时, 才从水缸里取出事件发给下游.
				 * </p>
				 */
			}

			@Override
			public void onNext(Integer integer) {
				System.out.println("rxjava flowable " + integer);
			}

			@Override
			public void onError(Throwable t) {
				System.out.println("rxjava flowable onError" + t.toString());
			}

			@Override
			public void onComplete() {
				System.out.println("rxjava flowable onComplete");
			}
		};
		upstream.subscribe(downstream);
	}
}
