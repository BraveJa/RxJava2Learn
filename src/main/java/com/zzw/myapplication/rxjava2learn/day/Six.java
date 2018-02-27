package com.zzw.myapplication.rxjava2learn.day;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zqy on 2017/10/24.
 * 因此我们总结一下, 本节中的治理的办法就两种:
 * 一是从数量上进行治理, 减少发送进水缸里的事件
 * 二是从速度上进行治理, 减缓事件发送进水缸的速度
 */

public class Six {
	public Six() {
		count();//数量限制
		speed();//速度限制
	}

	private void speed() {

		Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
			@Override
			public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
				for (int i = 0; ; i++) {
					emitter.onNext(i);
					Thread.sleep(2000);  //发送事件之后延时2秒,发送速度慢一点
				}
			}
		}).subscribeOn(Schedulers.io());

		Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
			@Override
			public void subscribe(ObservableEmitter<String> emitter) throws Exception {
				emitter.onNext("A");
			}
		}).subscribeOn(Schedulers.io());

		Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
			@Override
			public String apply(Integer integer, String s) throws Exception {
				return integer + s;
			}
		}).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
			@Override
			public void accept(String s) throws Exception {
			}
		}, new Consumer<Throwable>() {
			@Override
			public void accept(Throwable throwable) throws Exception {
			}
		});
	}

	private void count() {
		Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
			@Override
			public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
				for (int i = 0; ; i++) {
					emitter.onNext(i);
				}
			}
		}).subscribeOn(Schedulers.io())
				.sample(2, TimeUnit.SECONDS); //进行sample采样,每隔两秒去拿一个数据

		Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
			@Override
			public void subscribe(ObservableEmitter<String> emitter) throws Exception {
				emitter.onNext("A");
			}
		}).subscribeOn(Schedulers.io());

		Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
			@Override
			public String apply(Integer integer, String s) throws Exception {
				return integer + s;
			}
		}).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
			@Override
			public void accept(String s) throws Exception {
			}
		}, new Consumer<Throwable>() {
			@Override
			public void accept(Throwable throwable) throws Exception {
			}
		});

	}


}
