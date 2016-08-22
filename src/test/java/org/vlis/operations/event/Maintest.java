package org.vlis.operations.event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.vlis.operations.event.typeguess.config.OperationConfig;
import org.vlis.operations.event.typeguess.storageutils.ElasticSearchHuntUtil;


public class Maintest {
	private static final ExecutorService EXECUTORSERVICE = Executors.newFixedThreadPool(5);

	public static void main(String[] args) throws Exception {
		 OperationConfig.init();
//		 ElasticSearchHuntUtil.search();
//		Maintest maintest=new Maintest();		
//		maintest.test();
//		Thread currentThread = Thread.currentThread();
//		System.out.println("主线程ID：" + currentThread.getId());
		 ElasticSearchHuntUtil.getAllRecordsByRootUrl("/jeeshopserver_address/addressServiceFront");
	}

	public void test() {
		for(int i=0;i<10;i++){
		EXECUTORSERVICE.submit(new task());
		}
	}

	class task implements Runnable {
		public void run() {
			// TODO Auto-generated method stub
			Thread currentThread = Thread.currentThread();
			
		   new unsafeSequence().getNext( currentThread.getId());
		}
	}
    
	class unsafeSequence{
		private int i=0;
		public void  getNext(long tid){
			System.out.println("当前线程ID：" + tid+"---"+i);
		   System.out.println(i++);	
		}
	}
	
}
