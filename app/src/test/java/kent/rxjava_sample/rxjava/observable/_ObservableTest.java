package kent.rxjava_sample.rxjava.observable;

import io.reactivex.MaybeObserver;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import kent.rxjava_sample.rxjava.TestHelper;
import kent.rxjava_sample.rxjava.TestSchedulerProvider;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by Kent on 2018/5/4.
 */
public class _ObservableTest {

    Observer<Number> w;
    SingleObserver<Number> wo;
    MaybeObserver<Number> wm;

    @Before
    public void setUp() throws Exception {
        w = TestHelper.mockObserver();
        wm = TestHelper.mockMaybeObserver();
        wo = TestHelper.mockSingleObserver();
    }

    @Test
    public void fromArray() {
        String[] items = new String[] { "A", "B", "C" };
        Observable<String> o = Observable.fromArray(items);

        //ASSERT
        assertEquals((Long) 3L, o.count().blockingGet());
        assertEquals("B", o.skip(1).take(1).blockingSingle());
        assertEquals("B", o.take(2).blockingLast());
        assertEquals("A", o.take(4).blockingFirst());
    }

    @Test
    public void fromInterable() {
        ArrayList<String> items = new ArrayList<String>();
        items.add("A");
        items.add("B");
        items.add("C");

        Observable<String> o = Observable.fromIterable(items);

        //ASSERT
        assertEquals((Long) 3L, o.count().blockingGet());
        assertEquals("B", o.skip(1).take(1).blockingSingle());
        assertEquals("B", o.take(2).blockingLast());
        assertEquals("A", o.take(4).blockingFirst());
    }

    @Test
    public void fromArgs() {
        Observable<String> items = Observable.just("one", "two", "three");

        assertEquals((Long) 3L, items.count().blockingGet());
        assertEquals("two", items.skip(1).take(1).blockingSingle());
        assertEquals("three", items.takeLast(1).blockingSingle());
    }

    @Test
    public void testObservable_Subscribe() {

        Observable<String> o = Observable.just("A", "B", "C", "B");
        Observer<String> observer = TestHelper.mockObserver();
        o.subscribe(observer);

        //ASSERT
        verify(observer, times(1)).onNext("A");
        verify(observer, times(2)).onNext("B");
        verify(observer, times(1)).onNext("C");
        verify(observer, never()).onError(any(Throwable.class));
        verify(observer, times(1)).onComplete();
    }

    @Test
    public void testCountAFewItemsObservable() {
        Observable<String> o = Observable.just("a", "b", "c", "d");

        o.count().toObservable().subscribe(w);

        // we should be called only once
        verify(w, times(1)).onNext(anyLong());
        verify(w).onNext(4L);
        verify(w, never()).onError(any(Throwable.class));
        verify(w, times(1)).onComplete();
    }

    @Test
    public void testCountZeroItems() {
        Observable<String> o = Observable.empty();
        o.count().subscribe(wo);
        // we should be called only once
        verify(wo, times(1)).onSuccess(anyLong());
        verify(wo).onSuccess(0L);
        verify(wo, never()).onError(any(Throwable.class));
    }

    @Test
    public void testCountError() {
        Observable<String> o = Observable.error(new Callable<Throwable>() {
            @Override
            public Throwable call() {
                return new RuntimeException();
            }
        });

        o.count().subscribe(wo);
        verify(wo, never()).onSuccess(anyInt());
        verify(wo, times(1)).onError(any(RuntimeException.class));
    }

    @Test
    public void testReduce() {
        Observable<Integer> o = Observable.just(1, 2, 3, 4);
        o.reduce(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        }).subscribe(wm);

        //ASSERT
        // we should be called only once
        verify(wm, times(1)).onSuccess(anyInt());
        verify(wm).onSuccess(10);
        verify(wm, never()).onError(any(Throwable.class));
        verify(wm, never()).onComplete();
    }

    @Test
    public void testCreate() {

        Observable<Integer> o = Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                try {
                    for (int i = 1; i <= 5; i++) {
                        emitter.onNext(i);
                    }
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });

        Observer<Integer> observer = TestHelper.mockObserver();

        o.subscribe(observer);

        //ASSERT
        verify(observer, times(1)).onNext(1);
        verify(observer, times(1)).onNext(5);
        verify(observer, times(1)).onComplete();
        verify(observer, never()).onError(any(Throwable.class));
    }

    @Test
    public void testReplay() {
        //Observable<Integer> o = Observable.create(new ObservableOnSubscribe<Integer>() {
        //
        //    @Override
        //    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
        //        try {
        //            for (int i = 1; i <= 5; i++) {
        //                emitter.onNext(i);
        //                System.out.println(i);
        //            }
        //            emitter.onComplete();
        //        } catch (Exception e) {
        //            emitter.onError(e);
        //        }
        //    }
        //});
        //
        //Observer<Integer> observer = TestHelper.mockObserver();
        //o.subscribe(observer);
        //
        ////ASSERT
        ////verify(observer, times(1)).onNext(1);
        //
        //o.subscribe(observer);
        ////verify(observer, times(2)).onNext(1);
    }

    @Test
    public void testMap() {

        Observable<String> o = Observable.just(1, 2, 3)
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer i) throws Exception {
                        return i.toString();
                    }
                });

        Observer<String> observer = TestHelper.mockObserver();

        o.subscribe(observer);

        //ASSERT
        verify(observer, times(1)).onNext("1");
    }

    @Test
    public void testFloatMap() throws Exception {

        /** floatMap 是無序的，有可能因為多線程 delay 造成順序不同
         *  floatMap 將來源資料可以轉成平坦化的物件
         * **/

        List<String> list = new ArrayList<>();
        list.add("kent");
        list.add("tomaz");
        list.add("wilson");

        List<String> list2 = new ArrayList<>();
        list2.add("A");
        list2.add("B");

        Observable<String> o = Observable.just(list,list2)
                .subscribeOn(new TestSchedulerProvider().io())
                .flatMap(new Function<List<String>, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(final List<String> list) throws Exception {
                        int delay = 100;
                        if(list.size() >= 3){
                             delay = 500;
                        }
                        return Observable.fromIterable(list).delay(delay, TimeUnit.MILLISECONDS);

                    }
                });

        Observer<String> observer = TestHelper.mockObserver();

        o.subscribe(observer);

        //ASSERT
        assertEquals("A", o.take(1).blockingSingle());
        assertEquals("B", o.take(2).blockingLast());
        assertEquals("kent", o.take(3).blockingLast());
        assertEquals("tomaz", o.take(4).blockingLast());

        //TODO 因有 thread delay ，導致無法測試 verify times
        //verify(observer, atLeastOnce()).onNext("kent");
        //verify(observer, times(1)).onNext("tomaz");
        //verify(observer, times(1)).onNext("A");


    }

    @Test
    public void testFloatMapIterable() throws Exception {
        Observable<String> o = Observable.just(1,2,3)
                .flatMapIterable(new Function<Integer, Iterable<? extends String>>() {
                    @Override
                    public Iterable<? extends String> apply(Integer i) throws Exception {
                        return Arrays.asList("a", ""+i);
                    }
                });

        Observer<String> observer = TestHelper.mockObserver();

        o.subscribe(observer);

        //ASSERT
        assertEquals((Long)6L, o.count().blockingGet());
        assertEquals("a", o.take(1).blockingSingle());
        assertEquals("1", o.take(2).blockingLast());
        assertEquals("a", o.take(3).blockingLast());
        assertEquals("2", o.take(4).blockingLast());
        assertEquals("a", o.take(5).blockingLast());
        assertEquals("3", o.take(6).blockingLast());

    }

    @Test
    public void testConcatMap() throws Exception {

        /** ConcatMap 是有序的，在多線程 delay 時，可控制先後順序**/

        List<String> list = new ArrayList<>();
        list.add("kent");
        list.add("tomaz");

        List<String> list2 = new ArrayList<>();
        list2.add("A");
        list2.add("B");

        Observable<String> o = Observable.just(list,list2)
                .subscribeOn(new TestSchedulerProvider().io())
                .concatMap(new Function<List<String>, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(final List<String> list) throws Exception {
                        int delay = 100;
                        if(list.size() >= 3){
                            delay = 500;
                        }
                        return Observable.fromIterable(list).delay(delay, TimeUnit.MILLISECONDS);

                    }
                });

        Observer<String> observer = TestHelper.mockObserver();

        o.subscribe(observer);

        //ASSERT
        assertEquals("kent", o.take(1).blockingSingle());
        assertEquals("tomaz", o.take(2).blockingLast());
        assertEquals("A", o.take(3).blockingLast());
        assertEquals("B", o.take(4).blockingLast());

    }



}