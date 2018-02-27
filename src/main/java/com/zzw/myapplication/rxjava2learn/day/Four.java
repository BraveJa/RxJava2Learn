package com.zzw.myapplication.rxjava2learn.day;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zqy on 2017/10/24.
 * <p>
 * Zip通过一个函数将多个Observable发送的事件结合到一起，然后发送这些组合到一起的事件. 它按照严格的顺序应用这个函数。
 * 它只发射与发射数据项 最少 的那个Observable一样多的数据。
 * </p>
 * <p>
 *     因为我们两根水管都是运行在同一个线程里, 同一个线程里执行代码肯定有先后顺序呀.
 * </p>
 *
 * <P>
 *     比如一个界面需要展示用户的一些信息, 而这些信息分别要从两个服务器接口中获取,
 *     而只有当两个都获取到了之后才能进行展示, 这个时候就可以用Zip了:
 * </P>
 */

public class Four {
	public Four() {

		zip();
	}

	private void zip() {
		final Observable<String> observable1 = Observable.create(new ObservableOnSubscribe<String>() {
			@Override
			public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
				e.onNext("A");
				e.onNext("B");
				e.onNext("C");
			}
		}).subscribeOn(Schedulers.newThread());

		final Observable<Integer> observable2 =Observable.create(new ObservableOnSubscribe<Integer>() {
			@Override
			public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
				e.onNext(1);
				e.onNext(2);
				e.onNext(3);
				e.onNext(4);
			}
		}).subscribeOn(Schedulers.io());

		Observable.zip(observable1, observable2, new BiFunction<String, Integer, String>() {
			@Override
			public String apply(@NonNull String s, @NonNull Integer integer) throws Exception {
				return s+"---"+integer;
			}
		}).subscribe(new Consumer<String>() {
			@Override
			public void accept(String s) throws Exception {
				System.out.println("rxjava zip整合之后的"+s);
			}
		});
	}
}
