package com.worksum.android.tester;


import android.test.AndroidTestCase;

import com.jobs.lib_v1.app.AppUtil;

import junit.framework.TestCase;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;

/**
 * @author chao.qin
 *         <p/>
 *         16/5/17
 */
public class TestRx extends TestCase{

    public void testRx() {
        Observer observer = new Observer<String>() {
            @Override
            public void onCompleted() {
                System.out.println("-->onComplete");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("-->onError");
            }

            @Override
            public void onNext(String s) {
                System.out.println("-->onNext :" + s);
            }
        };

//        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
//            @Override
//            public void call(Subscriber<? super String> subscriber) {
//                subscriber.onNext("Hello!");
//                subscriber.onNext("Hi !");
//                subscriber.onNext("emial");
//                subscriber.onCompleted();
//            }
//
//        });

        Observable observable = Observable.just("Hello !","Hi !","大家好！","嘿嘿！");

        String[] words = new String[]{"Hello World!","Hi girl!","嘿嘿嘿！"};
        observable = Observable.from(words);

        observable.subscribe(observer);
    }
}
