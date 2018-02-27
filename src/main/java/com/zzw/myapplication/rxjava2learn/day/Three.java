package com.zzw.myapplication.rxjava2learn.day;

import com.zzw.myapplication.rxjava2learn.bean.LoginBean;
import com.zzw.myapplication.rxjava2learn.bean.RegistBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by zqy on 2017/10/24.
 */

public class Three {
	public Three() {
		//three_1();
		//通过Map, 可以将上游发来的事件转换为任意的类型, 可以是一个Object, 也可以是一个集合,
		//three_map();
		//three_flatAndConcatmap();
		RegistAndlogin();
	}

	private void RegistAndlogin() {
		Observable.create(new ObservableOnSubscribe<RegistBean>() {
			@Override
			public void subscribe(@NonNull ObservableEmitter<RegistBean> e) throws Exception {
				RegistBean registBean = new RegistBean();
				e.onNext(registBean);
			}
		}).flatMap(new Function<RegistBean, ObservableSource<LoginBean>>() {
			@Override
			public ObservableSource<LoginBean> apply(@NonNull RegistBean registBean) throws Exception {
				return Observable.create(new ObservableOnSubscribe<LoginBean>() {
					@Override
					public void subscribe(@NonNull ObservableEmitter<LoginBean> e) throws Exception {
						LoginBean loginBean = new LoginBean();
						e.onNext(loginBean);
					}
				});
			}
		}).subscribe(new Consumer<LoginBean>() {
			@Override
			public void accept(LoginBean loginBean) throws Exception {
				System.out.println("rxjava  flatmap之后 --注册成功并登录成功 " );
			}
		});
	}

	//无序
	private void three_flatAndConcatmap() {
	Observable.create(new ObservableOnSubscribe<Integer>() {
		@Override
		public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
			e.onNext(1);
			e.onNext(2);
			e.onNext(3);

		}
	//}).flatMap(new Function<Integer, ObservableSource<String>>() {//无序
	}).concatMap(new Function<Integer, ObservableSource<String>>() {	//有序
		@Override
		public ObservableSource<String> apply(@NonNull Integer integer) throws Exception {
			List list = new ArrayList();
			for (int i = 0; i < 3; i++) {
				list.add("I am value "+integer);//flatMap这里收到的integer是无序的
			}
			return Observable.fromIterable(list).delay(100, TimeUnit.MILLISECONDS);//发射一个集合出去
		}
	}).subscribe(new Consumer<String>() {
		@Override
		public void accept(String s) throws Exception {
			System.out.println("rxjava  flatmap之后 -- " + s);
		}
	});

	}

	private void three_map() {
		Observable.create(new ObservableOnSubscribe<Integer>() {
			@Override
			public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
				e.onNext(1);
				e.onNext(2);
				e.onNext(3);
			}
		}).map(new Function<Integer, String>() {
			@Override
			public String apply(@NonNull Integer integer) throws Exception {
				return "(变成了String " + integer + ")";
			}
		}).subscribe(new Consumer<String>() {
			@Override
			public void accept(String s) throws Exception {
				System.out.println("rxjava  map之后 -- " + s);
			}
		});

	}

	private void three_1() {
		regist();
	}


	private void regist() {
		Observable.create(new ObservableOnSubscribe<RegistBean>() {
			@Override
			public void subscribe(@NonNull ObservableEmitter<RegistBean> e) throws Exception {
				RegistBean registBean = new RegistBean();
				e.onNext(registBean);
			}
		}).subscribe(new Consumer<RegistBean>() {
			@Override
			public void accept(RegistBean registBean) throws Exception {
				if (registBean.getCode() == 200) {
					System.out.println("rxjava2 注册成功");
					login();
				}
			}
		}, new Consumer<Throwable>() {
			@Override
			public void accept(Throwable throwable) throws Exception {
				System.out.println("rxjava2 注册失败");
			}
		});

	}

	private void login() {
		Observable.create(new ObservableOnSubscribe<LoginBean>() {
			@Override
			public void subscribe(@NonNull ObservableEmitter<LoginBean> e) throws Exception {
				LoginBean loginBean = new LoginBean();
				e.onNext(loginBean);

			}
		}).subscribe(new Consumer<LoginBean>() {
			@Override
			public void accept(LoginBean loginBean) throws Exception {
				if (loginBean.getCode() == 200) {
					System.out.println("rxjava2 登录成功");
				}
			}
		}, new Consumer<Throwable>() {
			@Override
			public void accept(Throwable throwable) throws Exception {
				System.out.println("rxjava2 登录失败");
			}
		});
	}
}
